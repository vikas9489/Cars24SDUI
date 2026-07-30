package com.vikas.cars24sdui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.Modifier
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
                    modifier = Modifier.fillMaxSize()
                )
            }
        }
    }
}