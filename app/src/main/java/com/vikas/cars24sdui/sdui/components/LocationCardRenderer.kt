package com.vikas.cars24sdui.sdui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
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
private data class LocationCardProps(
    val imageUrl: String,
    val name: String,
    val meta: String? = null,
    val distance: String? = null,
    val openStatus: String? = null,
    val isOpen: Boolean = false,
    val primaryCta: CtaSpec? = null,
    val secondaryCta: CtaSpec? = null
)

object LocationCardRenderer : ComponentRenderer {
    @Composable
    override fun Render(node: SduiNode, state: Map<String, JsonElement>, dispatcher: ActionDispatcher) {
        val props = remember(node.id) { PropsJson.decodeFromJsonElement(LocationCardProps.serializer(), node.props) }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp)
                .clip(RoundedCornerShape(16.dp))
                .border(1.dp, Color(0xFFE5E5EA), RoundedCornerShape(16.dp))
        ) {
            SduiImage(
                ref = props.imageUrl,
                contentDescription = props.name,
                modifier = Modifier.fillMaxWidth().height(140.dp),
                contentScale = ContentScale.Crop
            )
            Column(Modifier.padding(16.dp)) {
                Text(props.name, style = MaterialTheme.typography.titleMedium)
                Spacer(Modifier.height(4.dp))
                props.meta?.let {
                    Text(it, style = MaterialTheme.typography.bodySmall, color = Color(0xFF6B6B6B))
                }
                Row {
                    props.distance?.let {
                        Text(it, style = MaterialTheme.typography.bodySmall, color = Color(0xFF6B6B6B))
                        Spacer(Modifier.width(8.dp))
                    }
                    props.openStatus?.let {
                        Text(
                            it,
                            style = MaterialTheme.typography.bodySmall,
                            color = if (props.isOpen) Color(0xFF0E8F5B) else Color(0xFFCC4400)
                        )
                    }
                }
                Spacer(Modifier.height(12.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    props.secondaryCta?.let { cta ->
                        Text(
                            cta.label,
                            style = MaterialTheme.typography.labelLarge,
                            color = MaterialTheme.colorScheme.primary,
                            modifier = Modifier
                                .clip(RoundedCornerShape(20.dp))
                                .border(1.dp, MaterialTheme.colorScheme.primary, RoundedCornerShape(20.dp))
                                .clickable { dispatcher.dispatch(cta.onTap) }
                                .padding(horizontal = 16.dp, vertical = 8.dp)
                        )
                    }
                    props.primaryCta?.let { cta ->
                        Text(
                            cta.label,
                            style = MaterialTheme.typography.labelLarge,
                            color = Color.White,
                            modifier = Modifier
                                .clip(RoundedCornerShape(20.dp))
                                .background(MaterialTheme.colorScheme.primary)
                                .clickable { dispatcher.dispatch(cta.onTap) }
                                .padding(horizontal = 16.dp, vertical = 8.dp)
                        )
                    }
                }
            }
        }
    }
}
