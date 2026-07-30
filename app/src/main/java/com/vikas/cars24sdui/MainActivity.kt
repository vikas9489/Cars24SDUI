package com.vikas.cars24sdui

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.Modifier
import com.vikas.cars24sdui.coverage.SearchScreenPreviewActivity
import com.vikas.cars24sdui.sdui.engine.SduiScreenHost
import com.vikas.cars24sdui.ui.theme.Cars24SDUITheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            Cars24SDUITheme {
                SduiScreenHost(
                    assetPath = "sdui/home_screen.json",
                    modifier = Modifier.fillMaxSize(),
                    // One route wired to a real destination to demonstrate the
                    // NAVIGATE mechanism end to end -- it happens to be the same
                    // screen used for the COVERAGE.md dry-run. Every other route
                    // still falls through to SduiScreenHost's Snackbar, which is
                    // enough to prove the action fires correctly without a full
                    // navigation graph (see README's Trade-offs section).
                    onNavigateRoute = { route, _ ->
                        if (route == "search") {
                            startActivity(Intent(this, SearchScreenPreviewActivity::class.java))
                            true
                        } else {
                            false
                        }
                    }
                )
            }
        }
    }
}