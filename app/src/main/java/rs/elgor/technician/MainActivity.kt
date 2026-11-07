package rs.elgor.technician

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import rs.elgor.technician.ui.ElgorTechnicianApp
import rs.elgor.technician.ui.theme.ElgorTechnicianTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        
        val jobId = intent.getIntExtra("jobId", -1).takeIf { it != -1 }
        
        // a notification tapped while the app was closed carries the id as a String, our own notification as a Long
        val nalogId = (intent.extras?.get("nalogId") as? Number)?.toLong()
            ?: intent.getStringExtra("nalogId")?.toLongOrNull()

        setContent {
            ElgorTechnicianTheme {
                ElgorTechnicianApp(initialJobId = jobId, initialNalogId = nalogId)
            }
        }
    }
}
