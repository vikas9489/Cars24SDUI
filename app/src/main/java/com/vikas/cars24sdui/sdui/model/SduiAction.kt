package com.vikas.cars24sdui.sdui.model

import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonObject

/**
 * Generic action envelope, same forward-compat philosophy as SduiNode:
 * `type` is a free-form string ("SET_STATE" | "NAVIGATE" | "OPEN_SHEET" |
 * "TOAST" today), unrecognized types are no-ops in ActionDispatcher rather
 * than parse failures. `value`/`params` can hold "{{...}}" template
 * strings resolved against page state and/or the firing event's context
 * (e.g. which chip was tapped) at dispatch time -- see ActionDispatcher.
 *
 * Node-level `actions` (e.g. a chip row's `onSelect`) live in
 * SduiNode.actions. Per-item actions inside a rail/grid/list (e.g. a
 * single car card's tap target) are embedded inline as an "onTap" field
 * on that item's JSON object within `props`, decoded with this same
 * shape by the renderer that owns that item.
 */
@Serializable
data class SduiAction(
    val type: String,
    val key: String? = null,
    val value: JsonElement? = null,
    val route: String? = null,
    val params: JsonObject? = null,
    val sheetId: String? = null
)
