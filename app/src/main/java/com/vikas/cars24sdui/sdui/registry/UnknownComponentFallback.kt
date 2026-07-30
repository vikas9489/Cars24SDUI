package com.vikas.cars24sdui.sdui.registry

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.vikas.cars24sdui.sdui.engine.ActionDispatcher
import com.vikas.cars24sdui.sdui.model.SduiNode
import kotlinx.serialization.json.JsonElement

/**
 * What renders for a `type` the client doesn't recognize. Visible on
 * purpose (a dashed-ish bordered note, not literally nothing) so a
 * reviewer -- or a future engineer debugging a partially-blank page --
 * can see graceful degradation happening rather than mistaking it for a
 * silent rendering bug. Never throws: this is the one renderer every
 * unmapped `type` always resolves to, so it must not depend on anything
 * in `props`/`children` being shaped a particular way.
 */
object UnknownComponentFallback : ComponentRenderer {
    @Composable
    override fun Render(node: SduiNode, state: Map<String, JsonElement>, dispatcher: ActionDispatcher) {
        Text(
            text = "Unsupported component \"${node.type}\" (id=${node.id}) — degraded gracefully",
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp)
                .border(1.dp, Color(0xFFCBB6FF), RoundedCornerShape(8.dp))
                .padding(12.dp),
            color = Color(0xFF6B5B95),
            style = MaterialTheme.typography.bodySmall
        )
    }
}
