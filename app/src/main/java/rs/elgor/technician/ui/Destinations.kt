package rs.elgor.technician.ui

sealed class Destination(val route: String) {
    data object ServerSetup : Destination("server_setup")
    data object Login : Destination("login")
    data object JobList : Destination("job_list")
    data object JobDetail : Destination("job/{jobId}") {
        fun createRoute(jobId: Int) = "job/$jobId"
    }
}
