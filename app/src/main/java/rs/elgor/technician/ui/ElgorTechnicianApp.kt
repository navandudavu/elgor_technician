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
import rs.elgor.technician.ui.screens.ServerSetupScreen

// Top-level app composable. Decides between three states before showing
// any real screen: no server URL saved yet (first run), server URL saved
// but not logged in, or logged in - mirrors the web app's RequireAuth
// wrapper in App.jsx, just with an extra step since this app also needs
// to know WHERE the server is before it can check WHO's logged in.
@Composable
fun ElgorTechnicianApp(initialJobId: Int? = null) {
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

    MainNavHost(
        repository = repository,
        sessionManager = sessionManager,
        serverUrl = serverUrl!!,
        initialJobId = initialJobId
    )
}

@Composable
private fun MainNavHost(
    repository: JobRepository,
    sessionManager: SessionManager,
    serverUrl: String,
    initialJobId: Int?
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

    LaunchedEffect(Unit) { authViewModel.checkExistingSession() }

    when (authState) {
        is AuthState.CheckingSession -> Unit // nothing to render yet, avoids a flash of the login screen
        is AuthState.LoggedOut -> LoginScreen(authViewModel = authViewModel)
        is AuthState.LoggedIn -> {
            NavHost(navController = navController, startDestination = Destination.JobList.route) {
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
