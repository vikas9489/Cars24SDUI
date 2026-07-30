package com.vikas.cars24sdui.sdui.components

import android.graphics.Color as AndroidColor
import androidx.compose.ui.graphics.Color
import kotlinx.serialization.json.Json

/** Shared Json instance every renderer uses to decode its own props shape. */
val PropsJson = Json { ignoreUnknownKeys = true }

fun String?.toComposeColor(fallback: Color): Color =
    this?.let { runCatching { Color(AndroidColor.parseColor(it)) }.getOrDefault(fallback) } ?: fallback
