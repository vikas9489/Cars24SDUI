package com.vikas.cars24sdui.sdui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
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
private data class CarCardRailProps(val items: List<CarRailItem>)

object CarCardRailRenderer : ComponentRenderer {
    @Composable
    override fun Render(node: SduiNode, state: Map<String, JsonElement>, dispatcher: ActionDispatcher) {
        val props = remember(node.id) { PropsJson.decodeFromJsonElement(CarCardRailProps.serializer(), node.props) }

        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            contentPadding = PaddingValues(horizontal = 16.dp)
        ) {
            items(props.items, key = { it.id }) { item ->
                Column(
                    modifier = Modifier
                        .width(140.dp)
                        .clickable { dispatcher.dispatch(item.onTap) }
                ) {
                    SduiImage(
                        ref = item.imageUrl,
                        contentDescription = item.title,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(100.dp)
                            .clip(RoundedCornerShape(12.dp)),
                        contentScale = ContentScale.Crop
                    )
                    Spacer(Modifier.height(6.dp))
                    Text(item.title, style = MaterialTheme.typography.titleSmall, maxLines = 1)
                    item.subtitle?.let {
                        Text(it, style = MaterialTheme.typography.bodySmall, color = Color(0xFF6B6B6B))
                    }
                }
            }
        }
    }
}
