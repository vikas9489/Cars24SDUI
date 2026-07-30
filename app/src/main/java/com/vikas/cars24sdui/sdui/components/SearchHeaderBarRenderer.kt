package com.vikas.cars24sdui.sdui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
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
import kotlinx.serialization.json.decodeFromJsonElement

@Serializable
private data class SearchHeaderBarProps(val searchPlaceholder: String = "Search")

/**
 * Added to close a real coverage gap found testing search_screen.json:
 * header_bar always renders a location pill + avatar and requires a
 * non-null location, which doesn't fit a back-button + full-width search
 * screen. This is a new component, not an extension of header_bar,
 * because the two have essentially no shared layout once location/avatar
 * are removed.
 */
object SearchHeaderBarRenderer : ComponentRenderer {
    @Composable
    override fun Render(node: SduiNode, state: Map<String, JsonElement>, dispatcher: ActionDispatcher) {
        val props = remember(node.id) { PropsJson.decodeFromJsonElement(SearchHeaderBarProps.serializer(), node.props) }

        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 12.dp)
        ) {
            Text(
                "←",
                modifier = Modifier
                    .clickable { dispatcher.dispatch(node.actions["onBackTap"]) }
                    .padding(end = 12.dp)
            )
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color(0xFFF0F0F5))
                    .clickable { dispatcher.dispatch(node.actions["onSearchTap"]) }
                    .padding(horizontal = 16.dp, vertical = 14.dp)
            ) {
                Text("🔍  ${props.searchPlaceholder}", color = Color(0xFF8A8A99))
            }
        }
    }
}
