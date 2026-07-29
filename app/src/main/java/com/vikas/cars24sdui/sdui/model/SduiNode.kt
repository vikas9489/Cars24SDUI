package com.vikas.cars24sdui.sdui.model

import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonObject

/**
 * Generic SDUI node. `type` is a free-form string looked up in the
 * ComponentRegistry at render time -- an unrecognized type simply fails
 * that lookup and falls back to UnknownComponentFallback instead of
 * failing deserialization. `props` stays a raw JsonObject so each
 * renderer owns parsing its own shape; nothing here needs to change
 * when a new component type is added.
 */
@Serializable
data class SduiNode(
    val id: String,
    val type: String,
    val props: JsonObject = JsonObject(emptyMap()),
    val children: List<SduiNode> = emptyList(),
    val actions: Map<String, SduiAction> = emptyMap()
)
