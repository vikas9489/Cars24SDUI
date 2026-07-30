package com.vikas.cars24sdui.sdui.components

import androidx.compose.foundation.background
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
import com.vikas.cars24sdui.sdui.model.SduiAction
import com.vikas.cars24sdui.sdui.model.SduiNode
import com.vikas.cars24sdui.sdui.registry.ComponentRenderer
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.decodeFromJsonElement

@Serializable
private data class PromoBannerProps(
    val title: String,
    val subtitle: String? = null,
    val badgeText: String? = null,
    val imageUrl: String,
    val backgroundColor: String,
    val textColor: String,
    val ctaLabel: String? = null,
    val onTap: SduiAction? = null
)

/** Covers the Orbit ad, match-finder, 30-day return, and Crashfree India banners -- one full-bleed image+CTA shape, different content. */
object PromoBannerRenderer : ComponentRenderer {
    @Composable
    override fun Render(node: SduiNode, state: Map<String, JsonElement>, dispatcher: ActionDispatcher) {
        val props = remember(node.id) { PropsJson.decodeFromJsonElement(PromoBannerProps.serializer(), node.props) }
        val bg = props.backgroundColor.toComposeColor(Color(0xFF1A1A1A))
        val fg = props.textColor.toComposeColor(Color.White)

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp)
                .clip(RoundedCornerShape(16.dp))
                .background(bg)
                .clickable { dispatcher.dispatch(props.onTap) }
        ) {
            SduiImage(
                ref = props.imageUrl,
                contentDescription = props.title,
                modifier = Modifier.fillMaxWidth().height(140.dp),
                contentScale = ContentScale.Crop
            )
            Column(Modifier.padding(16.dp)) {
                props.badgeText?.let { badge ->
                    Text(
                        badge,
                        color = bg,
                        style = MaterialTheme.typography.labelSmall,
                        modifier = Modifier
                            .clip(RoundedCornerShape(4.dp))
                            .background(Color.White)
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                    Spacer(Modifier.height(6.dp))
                }
                Text(props.title, color = fg, style = MaterialTheme.typography.titleMedium)
                props.subtitle?.let { subtitle ->
                    Spacer(Modifier.height(4.dp))
                    Text(subtitle, color = fg.copy(alpha = 0.85f), style = MaterialTheme.typography.bodySmall)
                }
                props.ctaLabel?.let { cta ->
                    Spacer(Modifier.height(12.dp))
                    Text(
                        cta,
                        color = bg,
                        style = MaterialTheme.typography.labelLarge,
                        modifier = Modifier
                            .clip(RoundedCornerShape(20.dp))
                            .background(Color.White)
                            .padding(horizontal = 16.dp, vertical = 8.dp)
                    )
                }
            }
        }
    }
}
