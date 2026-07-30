package com.vikas.cars24sdui.staticscreen

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.Modifier
import com.vikas.cars24sdui.ui.theme.Cars24SDUITheme

/**
 * Separate launcher entry point (not shown in the app drawer, no LAUNCHER
 * category) so the static and SDUI variants can each be cold-started in
 * isolation for PERF.md, e.g.:
 * adb shell am start -W -n com.vikas.cars24sdui/.staticscreen.StaticMainActivity
 */
class StaticMainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            Cars24SDUITheme {
                StaticHomeScreen(modifier = Modifier.fillMaxSize())
            }
        }
    }
}
