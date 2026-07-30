package com.vikas.cars24sdui.sdui.engine

import androidx.compose.runtime.Composable
import com.vikas.cars24sdui.sdui.model.SduiNode
import com.vikas.cars24sdui.sdui.registry.ComponentRenderer
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.jsonPrimitive

/**
 * A normal SduiNode, not a special JSON shape: `props.stateKey` names the
 * state entry to watch, `props.default` is used until that state key is
 * set, and each child is tagged with `props.caseKey`. This is what turns
 * a chip_tab_row's SET_STATE action into visibly different content --
 * the chip and the conditional are linked only by sharing a stateKey, so
 * a "surprise screen" gets the same conditional-content pattern for free
 * via new JSON, no client code.
 */
object ConditionalRenderer : ComponentRenderer {
    @Composable
    override fun Render(node: SduiNode, state: Map<String, JsonElement>, dispatcher: ActionDispatcher) {
        val stateKey = node.props["stateKey"]?.jsonPrimitive?.content
        val default = node.props["default"]?.jsonPrimitive?.content
        val activeCase = stateKey?.let { state[it]?.jsonPrimitive?.content } ?: default

        val match = node.children.firstOrNull { child ->
            child.props["caseKey"]?.jsonPrimitive?.content == activeCase
        }
        match?.let { RenderNode(it, state, dispatcher) }
    }
}
