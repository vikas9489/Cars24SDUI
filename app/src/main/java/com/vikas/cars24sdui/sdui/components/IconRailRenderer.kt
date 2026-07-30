package com.vikas.cars24sdui.sdui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
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
private data class IconRailProps(val items: List<IconActionItem>)

/**
 * Added to close a real coverage gap found testing search_screen.json:
 * a plain horizontally-scrollable icon+label rail with no colored wrapper
 * and no title/badge. banner_card_rail always needs a title + background
 * color; icon_grid wraps to new rows instead of scrolling. This is the
 * minimal thing that's actually missing between them.
 */
object IconRailRenderer : ComponentRenderer {
    @Composable
    override fun Render(node: SduiNode, state: Map<String, JsonElement>, dispatcher: ActionDispatcher) {
        val props = remember(node.id) { PropsJson.decodeFromJsonElement(IconRailProps.serializer(), node.props) }

        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp)
        ) {
            items(props.items, key = { it.id }) { item ->
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.width(76.dp).clickable { dispatcher.dispatch(item.onTap) }
                ) {
                    Box(
                        modifier = Modifier
                            .size(56.dp)
                            .clip(CircleShape)
                            .background(Color(0xFFF0F0F5)),
                        contentAlignment = Alignment.Center
                    ) { Text(item.icon ?: "🖼️") }
                    Spacer(Modifier.height(6.dp))
                    Text(
                        item.label,
                        style = MaterialTheme.typography.labelMedium,
                        textAlign = TextAlign.Center,
                        maxLines = 2
                    )
                }
            }
        }
    }
}
