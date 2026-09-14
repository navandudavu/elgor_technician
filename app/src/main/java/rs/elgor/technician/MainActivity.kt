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
        setContent {
            ElgorTechnicianTheme {
                ElgorTechnicianApp()
            }
        }
    }
}
