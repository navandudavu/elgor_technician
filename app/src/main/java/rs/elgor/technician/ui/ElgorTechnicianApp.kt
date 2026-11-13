package rs.elgor.technician.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import rs.elgor.technician.data.AuthState
import rs.elgor.technician.data.AuthViewModel
import rs.elgor.technician.data.AuthViewModelFactory
import rs.elgor.technician.data.NalogCreateViewModel
import rs.elgor.technician.data.NalogCreateViewModelFactory
import rs.elgor.technician.data.NalogEditViewModel
import rs.elgor.technician.data.NalogEditViewModelFactory
import rs.elgor.technician.data.NalogDetailViewModel
import rs.elgor.technician.data.NalogDetailViewModelFactory
import rs.elgor.technician.data.NalogListViewModel
import rs.elgor.technician.data.NalogListViewModelFactory
import rs.elgor.technician.data.NalogRepository
import rs.elgor.technician.data.JobDetailViewModel
import rs.elgor.technician.data.JobDetailViewModelFactory
import rs.elgor.technician.data.JobRepository
import rs.elgor.technician.data.JobsViewModel
import rs.elgor.technician.data.JobsViewModelFactory
import rs.elgor.technician.data.SessionManager
import rs.elgor.technician.data.local.AppDatabase
import rs.elgor.technician.data.remote.ElgorNetwork
import rs.elgor.technician.ui.screens.JobDetailScreen
import rs.elgor.technician.ui.screens.JobListScreen
import rs.elgor.technician.ui.screens.LoginScreen
import rs.elgor.technician.ui.screens.NalogCreateScreen
import rs.elgor.technician.data.CarriedViewModel
import rs.elgor.technician.data.CarriedViewModelFactory
import rs.elgor.technician.model.isAdminLevel
import rs.elgor.technician.ui.screens.CarriedScreen
import rs.elgor.technician.ui.screens.NalogDetailScreen
import rs.elgor.technician.ui.screens.NalogEditScreen
import rs.elgor.technician.ui.screens.NalogListScreen
import rs.elgor.technician.ui.screens.ServerSetupScreen

// Top-level app composable. Decides between three states before showing
// any real screen: no server URL saved yet (first run), server URL saved
// but not logged in, or logged in - mirrors the web app's RequireAuth
// wrapper in App.jsx, just with an extra step since this app also needs
// to know WHERE the server is before it can check WHO's logged in.
@Composable
fun ElgorTechnicianApp(initialJobId: Int? = null, initialNalogId: Long? = null) {
    val context = LocalContext.current
    val sessionManager = remember { SessionManager(context) }
    val scope = rememberCoroutineScope()

    val serverUrl by sessionManager.serverUrlFlow.collectAsState(initial = null)
    var isCheckingServerUrl by remember { mutableStateOf(true) }

    LaunchedEffect(Unit) {
        // Wait for first emission to avoid jumping to Setup screen too fast
        sessionManager.serverUrlFlow.first()
        isCheckingServerUrl = false
    }

    if (isCheckingServerUrl) {
        return // brief splash-less pause while DataStore is read; avoids a flash of the setup screen for returning users
    }

    if (serverUrl.isNullOrBlank()) {
        ServerSetupScreen(
            sessionManager = sessionManager,
            onServerSaved = { }
        )
        return
    }

    val repository = remember(serverUrl) {
        val database = AppDatabase.getDatabase(context)
        JobRepository(
            api = ElgorNetwork.create(serverUrl!!, sessionManager),
            dao = database.jobDao()
        )
    }

    val nalogRepository = remember(serverUrl) {
        NalogRepository(ElgorNetwork.createNalogApi(serverUrl!!, sessionManager))
    }

    MainNavHost(
        repository = repository,
        nalogRepository = nalogRepository,
        sessionManager = sessionManager,
        serverUrl = serverUrl!!,
        initialJobId = initialJobId,
        initialNalogId = initialNalogId
    )
}

@Composable
private fun MainNavHost(
    repository: JobRepository,
    nalogRepository: NalogRepository,
    sessionManager: SessionManager,
    serverUrl: String,
    initialJobId: Int?,
    initialNalogId: Long?
) {
    val navController = rememberNavController()
    val authViewModel = viewModel<AuthViewModel>(
        factory = AuthViewModelFactory(repository, sessionManager),
        key = "auth_$serverUrl"
    )
    val authState by authViewModel.authState.collectAsState()

    LaunchedEffect(authState) {
        if (authState is AuthState.LoggedIn && initialJobId != null) {
            navController.navigate(Destination.JobDetail.createRoute(initialJobId))
        }
    }

    // Tapped a "nalog assigned" notification: open that nalog once logged in.
    LaunchedEffect(authState) {
        if (authState is AuthState.LoggedIn && initialNalogId != null) {
            navController.navigate(Destination.NalogDetail.createRoute(initialNalogId))
        }
    }

    LaunchedEffect(Unit) { authViewModel.checkExistingSession() }

    when (authState) {
        is AuthState.CheckingSession -> Unit // nothing to render yet, avoids a flash of the login screen
        is AuthState.LoggedOut -> LoginScreen(authViewModel = authViewModel)
        is AuthState.LoggedIn -> {
            NavHost(navController = navController, startDestination = Destination.NalogList.route) {
                composable(Destination.NalogList.route) {
                    val nalogListViewModel = viewModel<NalogListViewModel>(
                        factory = NalogListViewModelFactory(nalogRepository),
                        key = "nalozi_$serverUrl"
                    )
                    NalogListScreen(
                        viewModel = nalogListViewModel,
                        authViewModel = authViewModel,
                        onNalogClick = { id -> navController.navigate(Destination.NalogDetail.createRoute(id)) },
                        onCreateNalog = { navController.navigate(Destination.NalogCreate.route) },
                        onOpenCarried = { navController.navigate(Destination.Carried.route) }
                    )
                }
                composable(Destination.Carried.route) {
                    val me = (authState as? AuthState.LoggedIn)?.user
                    val carriedViewModel = viewModel<CarriedViewModel>(
                        factory = CarriedViewModelFactory(nalogRepository, isAdminLevel(me?.role), (me?.id ?: -1).toLong()),
                        key = "carried_${me?.id}"
                    )
                    CarriedScreen(
                        viewModel = carriedViewModel,
                        isAdmin = isAdminLevel(me?.role),
                        myName = me?.name ?: "",
                        onBack = { navController.popBackStack() }
                    )
                }
                composable(Destination.NalogCreate.route) {
                    val me = (authState as? AuthState.LoggedIn)?.user
                    val createViewModel = viewModel<NalogCreateViewModel>(
                        factory = NalogCreateViewModelFactory(nalogRepository, isAdminLevel(me?.role)),
                        key = "nalog_create"
                    )
                    NalogCreateScreen(
                        viewModel = createViewModel,
                        onBack = { navController.popBackStack() },
                        // open the new nalog; Back from there returns to the list, not to the finished form
                        onCreated = { id ->
                            navController.navigate(Destination.NalogDetail.createRoute(id)) {
                                popUpTo(Destination.NalogCreate.route) { inclusive = true }
                            }
                        }
                    )
                }
                composable(
                    route = Destination.NalogDetail.route,
                    arguments = listOf(navArgument("nalogId") { type = NavType.LongType })
                ) { backStackEntry ->
                    val nalogId = backStackEntry.arguments?.getLong("nalogId") ?: return@composable
                    val nalogDetailViewModel = viewModel<NalogDetailViewModel>(
                        factory = NalogDetailViewModelFactory(nalogRepository, nalogId),
                        key = "nalog_detail_$nalogId"
                    )
                    val me = (authState as? AuthState.LoggedIn)?.user
                    // The edit screen sets this flag on its way back so the detail reloads the saved values.
                    val needsRefresh by backStackEntry.savedStateHandle
                        .getStateFlow("nalog_changed", false).collectAsState()
                    LaunchedEffect(needsRefresh) {
                        if (needsRefresh) {
                            nalogDetailViewModel.load()
                            backStackEntry.savedStateHandle["nalog_changed"] = false
                        }
                    }
                    NalogDetailScreen(
                        viewModel = nalogDetailViewModel,
                        repository = nalogRepository,
                        currentUserId = (me?.id ?: -1).toLong(),
                        isAdmin = isAdminLevel(me?.role),
                        onBack = { navController.popBackStack() },
                        onEdit = { navController.navigate(Destination.NalogEdit.createRoute(nalogId)) }
                    )
                }
                composable(
                    route = Destination.NalogEdit.route,
                    arguments = listOf(navArgument("nalogId") { type = NavType.LongType })
                ) { backStackEntry ->
                    val nalogId = backStackEntry.arguments?.getLong("nalogId") ?: return@composable
                    val editViewModel = viewModel<NalogEditViewModel>(
                        factory = NalogEditViewModelFactory(nalogRepository, nalogId),
                        key = "nalog_edit_$nalogId"
                    )
                    NalogEditScreen(
                        viewModel = editViewModel,
                        onBack = { navController.popBackStack() },
                        onSaved = {
                            navController.previousBackStackEntry?.savedStateHandle?.set("nalog_changed", true)
                            navController.popBackStack()
                        }
                    )
                }
                composable(Destination.JobList.route) {
                    val jobsViewModel = viewModel<JobsViewModel>(
                        factory = JobsViewModelFactory(repository),
                        key = "jobs_$serverUrl"
                    )
                    JobListScreen(
                        jobsViewModel = jobsViewModel,
                        authViewModel = authViewModel,
                        onJobClick = { jobId -> navController.navigate(Destination.JobDetail.createRoute(jobId)) }
                    )
                }
                composable(
                    route = Destination.JobDetail.route,
                    arguments = listOf(navArgument("jobId") { type = NavType.IntType })
                ) { backStackEntry ->
                    val jobId = backStackEntry.arguments?.getInt("jobId") ?: return@composable
                    val detailViewModel = viewModel<JobDetailViewModel>(
                        factory = JobDetailViewModelFactory(repository, jobId),
                        // Keyed by jobId so navigating between two different jobs
                        // doesn't reuse a stale ViewModel instance from the last one.
                        key = "job_detail_$jobId"
                    )
                    JobDetailScreen(
                        viewModel = detailViewModel,
                        repository = repository,
                        onBack = { navController.popBackStack() }
                    )
                }
            }
        }
    }
}
