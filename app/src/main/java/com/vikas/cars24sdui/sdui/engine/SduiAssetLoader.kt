package com.vikas.cars24sdui.sdui.engine

import android.content.Context
import android.util.Log
import com.vikas.cars24sdui.sdui.model.SduiScreen
import kotlinx.serialization.json.Json

private const val PERF_TAG = "SduiPerf"

object SduiAssetLoader {
    private val json = Json { ignoreUnknownKeys = true }

    /** Logs fetch (asset read) vs parse (decode) time separately -- this is the "SDUI breakdown" row in PERF.md. */
    fun loadScreen(context: Context, assetPath: String): SduiScreen {
        val fetchStart = System.nanoTime()
        val text = context.assets.open(assetPath).bufferedReader().use { it.readText() }
        val fetchEnd = System.nanoTime()

        val screen = json.decodeFromString(SduiScreen.serializer(), text)
        val parseEnd = System.nanoTime()

        Log.i(
            PERF_TAG,
            "json_fetch_ms=${(fetchEnd - fetchStart) / 1_000_000} json_parse_ms=${(parseEnd - fetchEnd) / 1_000_000}"
        )
        return screen
    }
}
