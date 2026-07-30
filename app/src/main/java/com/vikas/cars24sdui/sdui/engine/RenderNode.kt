package com.vikas.cars24sdui.sdui.engine

import androidx.compose.runtime.Composable
import com.vikas.cars24sdui.sdui.model.SduiNode
import com.vikas.cars24sdui.sdui.registry.ComponentRegistry
import kotlinx.serialization.json.JsonElement

/** Single recursive entry point every renderer (including structural ones like `conditional`) calls for its children. */
@Composable
fun RenderNode(node: SduiNode, state: Map<String, JsonElement>, dispatcher: ActionDispatcher) {
    ComponentRegistry.rendererFor(node.type).Render(node, state, dispatcher)
}
