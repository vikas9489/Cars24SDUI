package com.vikas.cars24sdui.sdui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
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
import androidx.compose.ui.unit.dp
import com.vikas.cars24sdui.sdui.engine.ActionDispatcher
import com.vikas.cars24sdui.sdui.model.SduiNode
import com.vikas.cars24sdui.sdui.registry.ComponentRenderer
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.contentOrNull
import kotlinx.serialization.json.decodeFromJsonElement

@Serializable
private data class ChipTabRowProps(
    val stateKey: String,
    val default: String,
    val style: String = "text_pill",
    val options: List<ChipOption>
)

/**
 * One renderer, two visual styles chosen by `props.style` -- this backs
 * both the top icon-tab row and the Wishlisted/Hot Deals text-pill
 * selector. `onSelect` fires with the tapped option's id in the event
 * context so a single JSON-declared "{{selectedId}}" template works for
 * every chip row without per-row client code.
 */
object ChipTabRowRenderer : ComponentRenderer {
    @Composable
    override fun Render(node: SduiNode, state: Map<String, JsonElement>, dispatcher: ActionDispatcher) {
        val props = remember(node.id) { PropsJson.decodeFromJsonElement(ChipTabRowProps.serializer(), node.props) }
        val selected = (state[props.stateKey] as? JsonPrimitive)?.contentOrNull ?: props.default

        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp)
        ) {
            items(props.options, key = { it.id }) { option ->
                val isSelected = option.id == selected
                val onClick = {
                    dispatcher.dispatch(node.actions["onSelect"], eventContext = mapOf("selectedId" to option.id))
                }

                if (props.style == "icon_pill") {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.clickable(onClick = onClick).padding(4.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(48.dp)
                                .clip(CircleShape)
                                .background(if (isSelected) MaterialTheme.colorScheme.primary else Color(0xFFF0F0F5)),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(option.icon ?: "•")
                        }
                        Spacer(Modifier.height(4.dp))
                        Text(option.label, style = MaterialTheme.typography.labelSmall)
                    }
                } else {
                    Text(
                        option.label,
                        color = if (isSelected) Color.White else Color(0xFF1A1A1A),
                        style = MaterialTheme.typography.labelLarge,
                        modifier = Modifier
                            .clip(RoundedCornerShape(20.dp))
                            .background(if (isSelected) MaterialTheme.colorScheme.primary else Color(0xFFF0F0F5))
                            .clickable(onClick = onClick)
                            .padding(horizontal = 16.dp, vertical = 8.dp)
                    )
                }
            }
        }
    }
}
