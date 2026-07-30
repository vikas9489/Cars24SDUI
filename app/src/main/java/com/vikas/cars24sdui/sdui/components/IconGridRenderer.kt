package com.vikas.cars24sdui.sdui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
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
private data class IconGridProps(
    val columns: Int = 4,
    val backgroundColor: String? = null,
    val cardBackgroundColor: String? = null,
    val textColor: String? = null,
    val items: List<IconActionItem>
)

object IconGridRenderer : ComponentRenderer {
    @Composable
    override fun Render(node: SduiNode, state: Map<String, JsonElement>, dispatcher: ActionDispatcher) {
        val props = remember(node.id) { PropsJson.decodeFromJsonElement(IconGridProps.serializer(), node.props) }
        val fg = props.textColor.toComposeColor(Color(0xFF1A1A1A))
        val cardColor = props.backgroundColor ?: props.cardBackgroundColor

        var containerModifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
        if (cardColor != null) {
            containerModifier = containerModifier
                .clip(RoundedCornerShape(16.dp))
                .background(cardColor.toComposeColor(Color.Transparent))
                .padding(16.dp)
        }

        Column(containerModifier) {
            props.items.chunked(props.columns).forEach { row ->
                Row(Modifier.fillMaxWidth()) {
                    row.forEach { item ->
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier
                                .weight(1f)
                                .clickable { dispatcher.dispatch(item.onTap) }
                                .padding(vertical = 8.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(44.dp)
                                    .clip(CircleShape)
                                    .background(Color.Black.copy(alpha = 0.05f)),
                                contentAlignment = Alignment.Center
                            ) { Text(item.icon ?: "•") }
                            Spacer(Modifier.height(4.dp))
                            Text(
                                item.label,
                                color = fg,
                                style = MaterialTheme.typography.labelSmall,
                                textAlign = TextAlign.Center,
                                maxLines = 2
                            )
                        }
                    }
                    repeat(props.columns - row.size) { Spacer(Modifier.weight(1f)) }
                }
            }
        }
    }
}
