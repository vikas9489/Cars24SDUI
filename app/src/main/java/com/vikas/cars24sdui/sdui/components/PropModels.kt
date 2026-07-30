package com.vikas.cars24sdui.sdui.components

import com.vikas.cars24sdui.sdui.model.SduiAction
import kotlinx.serialization.Serializable

/** Reusable item shapes shared by more than one renderer -- decoded straight out of a node's `props` JSON. */
@Serializable
data class ChipOption(val id: String, val label: String, val icon: String? = null)

@Serializable
data class IconActionItem(val id: String, val label: String, val icon: String? = null, val onTap: SduiAction? = null)

@Serializable
data class CarRailItem(
    val id: String,
    val title: String,
    val subtitle: String? = null,
    val imageUrl: String,
    val onTap: SduiAction? = null
)

@Serializable
data class CarGridItem(
    val id: String,
    val title: String,
    val subtitle: String? = null,
    val price: String? = null,
    val priceSubtext: String? = null,
    val imageUrl: String,
    val badges: List<String> = emptyList(),
    val onTap: SduiAction? = null
)

@Serializable
data class CtaSpec(val label: String, val onTap: SduiAction? = null)
