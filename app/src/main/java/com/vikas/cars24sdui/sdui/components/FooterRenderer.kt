package com.vikas.cars24sdui.sdui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.vikas.cars24sdui.sdui.engine.ActionDispatcher
import com.vikas.cars24sdui.sdui.model.SduiNode
import com.vikas.cars24sdui.sdui.registry.ComponentRenderer
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.decodeFromJsonElement

@Serializable
private data class FooterProps(
    val title: String,
    val subtitle: String? = null,
    val backgroundColor: String,
    val textColor: String
)

object FooterRenderer : ComponentRenderer {
    @Composable
    override fun Render(node: SduiNode, state: Map<String, JsonElement>, dispatcher: ActionDispatcher) {
        val props = remember(node.id) { PropsJson.decodeFromJsonElement(FooterProps.serializer(), node.props) }
        val bg = props.backgroundColor.toComposeColor(MaterialTheme.colorScheme.primary)
        val fg = props.textColor.toComposeColor(Color.White)

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(bg)
                .padding(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                props.title,
                color = fg,
                style = MaterialTheme.typography.headlineSmall,
                textAlign = TextAlign.Center
            )
            props.subtitle?.let { subtitle ->
                Spacer(Modifier.height(12.dp))
                Text(subtitle, color = fg.copy(alpha = 0.85f), style = MaterialTheme.typography.bodyMedium)
            }
        }
    }
}
