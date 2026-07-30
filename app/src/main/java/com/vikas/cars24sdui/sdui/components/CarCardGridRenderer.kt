package com.vikas.cars24sdui.sdui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import com.vikas.cars24sdui.sdui.engine.ActionDispatcher
import com.vikas.cars24sdui.sdui.model.SduiNode
import com.vikas.cars24sdui.sdui.registry.ComponentRenderer
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.decodeFromJsonElement

@Serializable
private data class CarCardGridProps(
    val caseKey: String? = null,
    val columns: Int = 1,
    val items: List<CarGridItem>
)

/** `columns == 1` (the "Used cars you'll love" case in our JSON) gets full-width detail cards; >1 gets a compact grid. */
object CarCardGridRenderer : ComponentRenderer {
    @Composable
    override fun Render(node: SduiNode, state: Map<String, JsonElement>, dispatcher: ActionDispatcher) {
        val props = remember(node.id) { PropsJson.decodeFromJsonElement(CarCardGridProps.serializer(), node.props) }

        Column(
            Modifier.fillMaxWidth().padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            if (props.columns <= 1) {
                props.items.forEach { item -> FullCarCard(item, dispatcher) }
            } else {
                props.items.chunked(props.columns).forEach { row ->
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        row.forEach { item -> CompactCarCard(item, dispatcher, Modifier.weight(1f)) }
                        repeat(props.columns - row.size) { Spacer(Modifier.weight(1f)) }
                    }
                }
            }
        }
    }
}

@Composable
private fun FullCarCard(item: CarGridItem, dispatcher: ActionDispatcher) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .border(1.dp, Color(0xFFE5E5EA), RoundedCornerShape(12.dp))
            .clickable { dispatcher.dispatch(item.onTap) }
            .padding(12.dp)
    ) {
        SduiImage(
            ref = item.imageUrl,
            contentDescription = item.title,
            modifier = Modifier.size(96.dp).clip(RoundedCornerShape(8.dp)),
            contentScale = ContentScale.Crop
        )
        Spacer(Modifier.width(12.dp))
        Column(Modifier.weight(1f)) {
            Text(item.title, style = MaterialTheme.typography.titleSmall)
            item.subtitle?.let {
                Text(it, style = MaterialTheme.typography.bodySmall, color = Color(0xFF6B6B6B))
            }
            Spacer(Modifier.height(4.dp))
            Row {
                item.price?.let { Text(it, style = MaterialTheme.typography.titleMedium) }
                item.priceSubtext?.let {
                    Spacer(Modifier.width(6.dp))
                    Text(it, style = MaterialTheme.typography.bodySmall, color = Color(0xFF6B6B6B))
                }
            }
            if (item.badges.isNotEmpty()) {
                Spacer(Modifier.height(4.dp))
                Row(
                    modifier = Modifier.horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    item.badges.take(3).forEach { badge ->
                        Text(
                            badge,
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.primary,
                            maxLines = 1,
                            softWrap = false,
                            modifier = Modifier
                                .clip(RoundedCornerShape(4.dp))
                                .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.1f))
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun CompactCarCard(item: CarGridItem, dispatcher: ActionDispatcher, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .border(1.dp, Color(0xFFE5E5EA), RoundedCornerShape(12.dp))
            .clickable { dispatcher.dispatch(item.onTap) }
            .padding(8.dp)
    ) {
        SduiImage(
            ref = item.imageUrl,
            contentDescription = item.title,
            modifier = Modifier.fillMaxWidth().height(80.dp).clip(RoundedCornerShape(8.dp)),
            contentScale = ContentScale.Crop
        )
        Spacer(Modifier.height(6.dp))
        Text(item.title, style = MaterialTheme.typography.labelLarge, maxLines = 1)
        item.price?.let { Text(it, style = MaterialTheme.typography.bodySmall) }
    }
}
