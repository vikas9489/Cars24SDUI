package com.vikas.cars24sdui.sdui.registry

import androidx.compose.runtime.Composable
import com.vikas.cars24sdui.sdui.engine.ActionDispatcher
import com.vikas.cars24sdui.sdui.model.SduiNode
import kotlinx.serialization.json.JsonElement

fun interface ComponentRenderer {
    @Composable
    fun Render(node: SduiNode, state: Map<String, JsonElement>, dispatcher: ActionDispatcher)
}
