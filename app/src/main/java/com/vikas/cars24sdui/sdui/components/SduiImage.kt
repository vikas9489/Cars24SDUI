package com.vikas.cars24sdui.sdui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.layout.ContentScale
import coil3.compose.AsyncImage

/**
 * `imageUrl` values in our demo JSON use a "placeholder:<type>:<hex>" scheme
 * instead of a real network URL -- the assignment brief says nothing needs
 * to be a live API, and remote images made scroll perf/jank non-reproducible
 * across runs (also relevant for Part 2's benchmark). A real server payload
 * would put an actual https:// CDN URL in the same field; this renderer
 * still supports that path via Coil so the schema itself hasn't changed.
 *
 * Drawn with Canvas primitives, not a Text/emoji glyph -- emoji rendering
 * can trigger slow color-font fallback lookups on some devices, which is
 * exactly the kind of per-item cost that shows up as scroll jank.
 */
@Composable
fun SduiImage(
    ref: String,
    contentDescription: String?,
    modifier: Modifier = Modifier,
    contentScale: ContentScale = ContentScale.Crop
) {
    if (ref.startsWith("placeholder:")) {
        val parts = ref.removePrefix("placeholder:").split(":")
        val tint = parts.getOrNull(1)?.let { "#$it".toComposeColor(Color(0xFFB0BEC5)) } ?: Color(0xFFB0BEC5)
        Canvas(modifier = modifier.background(tint)) {
            val w = size.width
            val h = size.height
            val overlay = Color.White.copy(alpha = 0.35f)

            drawCircle(color = overlay, radius = h * 0.12f, center = Offset(w * 0.22f, h * 0.28f))

            val mountains = Path().apply {
                moveTo(0f, h)
                lineTo(w * 0.32f, h * 0.45f)
                lineTo(w * 0.55f, h * 0.7f)
                lineTo(w * 0.72f, h * 0.35f)
                lineTo(w, h)
                close()
            }
            drawPath(mountains, color = overlay)
        }
    } else {
        AsyncImage(
            model = ref,
            contentDescription = contentDescription,
            modifier = modifier,
            contentScale = contentScale
        )
    }
}
