package com.vikas.cars24sdui.sdui.engine

import android.content.Context
import com.vikas.cars24sdui.sdui.model.SduiScreen
import kotlinx.serialization.json.Json

object SduiAssetLoader {
    private val json = Json { ignoreUnknownKeys = true }

    fun loadScreen(context: Context, assetPath: String): SduiScreen {
        val text = context.assets.open(assetPath).bufferedReader().use { it.readText() }
        return json.decodeFromString(SduiScreen.serializer(), text)
    }
}
