package com.vikas.cars24sdui.coverage

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.Modifier
import com.vikas.cars24sdui.sdui.engine.SduiScreenHost
import com.vikas.cars24sdui.ui.theme.Cars24SDUITheme

/**
 * Renders search_screen.json -- a Cars24 screen this project was NOT built
 * for -- through the exact same engine as the home screen, with zero new
 * client code, to make COVERAGE.md's claim tested rather than guessed.
 * Kept as a permanent artifact, not a throwaway: it's real proof of the
 * "given a new screen, X% renders with JSON-only changes" claim, and it's
 * the same exercise the live round will ask for.
 *
 * adb shell am start -n com.vikas.cars24sdui/.coverage.SearchScreenPreviewActivity
 */
class SearchScreenPreviewActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            Cars24SDUITheme {
                SduiScreenHost(
                    assetPath = "sdui/search_screen.json",
                    modifier = Modifier.fillMaxSize()
                )
            }
        }
    }
}
