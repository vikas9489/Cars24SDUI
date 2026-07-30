package com.vikas.cars24sdui.sdui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.vikas.cars24sdui.sdui.engine.ActionDispatcher
import com.vikas.cars24sdui.sdui.engine.RenderNode
import com.vikas.cars24sdui.sdui.model.SduiAction
import com.vikas.cars24sdui.sdui.model.SduiNode
import com.vikas.cars24sdui.sdui.registry.ComponentRenderer
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.decodeFromJsonElement

@Serializable
private data class SectionProps(
    val title: String,
    val viewAllRoute: String? = null,
    val trailingActionLabel: String? = null,
    val backgroundColor: String? = null,
    val textColor: String? = null
)

/**
 * Generic "title + optional trailing action/link + children" wrapper --
 * reused for every "View all" header on the page instead of one bespoke
 * component per section name. `viewAllRoute` is sugar for a plain
 * no-params NAVIGATE; anything needing params/other action types uses
 * `trailingActionLabel` + the node-level `onTrailingActionTap` action.
 */
object SectionRenderer : ComponentRenderer {
    @Composable
    override fun Render(node: SduiNode, state: Map<String, JsonElement>, dispatcher: ActionDispatcher) {
        val props = remember(node.id) { PropsJson.decodeFromJsonElement(SectionProps.serializer(), node.props) }
        val fg = props.textColor.toComposeColor(Color(0xFF1A1A1A))

        var containerModifier = Modifier.fillMaxWidth()
        props.backgroundColor?.let { bg ->
            containerModifier = containerModifier.background(bg.toComposeColor(Color.Transparent))
        }

        Column(containerModifier.padding(vertical = 8.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(props.title, color = fg, style = MaterialTheme.typography.titleMedium)
                when {
                    props.trailingActionLabel != null -> Text(
                        props.trailingActionLabel,
                        color = fg,
                        style = MaterialTheme.typography.labelLarge,
                        modifier = Modifier.clickable {
                            dispatcher.dispatch(node.actions["onTrailingActionTap"])
                        }
                    )
                    props.viewAllRoute != null -> Text(
                        "View all",
                        color = MaterialTheme.colorScheme.primary,
                        style = MaterialTheme.typography.labelLarge,
                        modifier = Modifier.clickable {
                            dispatcher.dispatch(SduiAction(type = "NAVIGATE", route = props.viewAllRoute))
                        }
                    )
                }
            }
            Spacer(Modifier.height(8.dp))
            node.children.forEach { child -> RenderNode(child, state, dispatcher) }
        }
    }
}
