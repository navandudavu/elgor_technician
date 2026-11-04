package rs.elgor.technician.ui

sealed class Destination(val route: String) {
    data object ServerSetup : Destination("server_setup")
    data object Login : Destination("login")
    data object JobList : Destination("job_list")
    data object NalogList : Destination("nalog_list")
    data object NalogDetail : Destination("nalog/{nalogId}") {
        fun createRoute(nalogId: Long) = "nalog/$nalogId"
    }
    data object NalogEdit : Destination("nalog/{nalogId}/edit") {
        fun createRoute(nalogId: Long) = "nalog/$nalogId/edit"
    }
    data object JobDetail : Destination("job/{jobId}") {
        fun createRoute(jobId: Int) = "job/$jobId"
    }
}
