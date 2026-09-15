package rs.elgor.technician

import android.app.Application
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import rs.elgor.technician.data.JobRepository
import rs.elgor.technician.data.SessionManager
import rs.elgor.technician.data.local.AppDatabase
import rs.elgor.technician.data.remote.ElgorNetwork

class ElgorApplication : Application() {

    lateinit var sessionManager: SessionManager
        private set

    lateinit var database: AppDatabase
        private set

    var repository: JobRepository? = null
        private set

    override fun onCreate() {
        super.onCreate()
        sessionManager = SessionManager(this)
        database = AppDatabase.getDatabase(this)
        
        // Try to initialize repository if we have a server URL
        runBlocking {
            val url = sessionManager.serverUrlFlow.first()
            if (!url.isNullOrBlank()) {
                repository = JobRepository(
                    api = ElgorNetwork.create(url, sessionManager),
                    dao = database.jobDao()
                )
            }
        }
    }
    
    fun getOrCreateRepository(): JobRepository? {
        if (repository != null) return repository
        
        val url = runBlocking { sessionManager.serverUrlFlow.first() }
        if (!url.isNullOrBlank()) {
            repository = JobRepository(
                api = ElgorNetwork.create(url, sessionManager),
                dao = database.jobDao()
            )
        }
        return repository
    }
}
