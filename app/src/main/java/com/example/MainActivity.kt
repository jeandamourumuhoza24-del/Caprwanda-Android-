package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.navigation.compose.rememberNavController
import com.example.ui.navigation.CapRwandaNavGraph
import com.example.ui.theme.CapRwandaTheme

import androidx.activity.result.contract.ActivityResultContracts

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // Automatically request all required permissions on startup
        val permissionLauncher = registerForActivityResult(
            ActivityResultContracts.RequestMultiplePermissions()
        ) { permissions ->
            // Handled permissions
        }

        val permissionsToRequest = mutableListOf(
            android.Manifest.permission.CAMERA,
            android.Manifest.permission.RECORD_AUDIO
        ).apply {
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
                add(android.Manifest.permission.READ_MEDIA_VIDEO)
                add(android.Manifest.permission.READ_MEDIA_IMAGES)
                add(android.Manifest.permission.READ_MEDIA_AUDIO)
            } else {
                add(android.Manifest.permission.READ_EXTERNAL_STORAGE)
                add(android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
            }
        }.toTypedArray()

        permissionLauncher.launch(permissionsToRequest)

        setContent {
            CapRwandaTheme(darkTheme = true) {
                Surface(modifier = Modifier.fillMaxSize()) {
                    val navController = rememberNavController()
                    CapRwandaNavGraph(navController = navController)
                }
            }
        }
    }
}
