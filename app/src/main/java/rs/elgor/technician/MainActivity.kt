package rs.elgor.technician

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import rs.elgor.technician.ui.ElgorTechnicianApp
import rs.elgor.technician.ui.theme.ElgorTechnicianTheme

class MainActivity : ComponentActivity() {

    // Android 13+ shows no notification at all until the user allows it; without asking, pushes silently never appear
    private val notificationPermission =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU &&
            ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED
        ) {
            notificationPermission.launch(Manifest.permission.POST_NOTIFICATIONS)
        }
        
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
