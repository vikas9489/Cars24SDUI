package com.vikas.cars24sdui.sdui.model

import kotlinx.serialization.Serializable

/**
 * Root payload for one screen. `minSupportedVersion` is the versioning
 * hook: the client compares it against its own SCHEMA_VERSION constant
 * before rendering. If the payload requires a newer engine than the
 * installed app ships, the client shows an "update the app" state
 * instead of attempting to render -- it never guesses at a schema shape
 * it wasn't built for. Older-server-vs-newer-client and
 * newer-server-vs-older-client (within the same major schema) are both
 * handled for free by the unknown-component fallback: unrecognized
 * section types just degrade gracefully rather than requiring a version
 * bump at all.
 */
@Serializable
data class SduiScreen(
    val screenId: String,
    val version: Int,
    val minSupportedVersion: Int = 1,
    val sections: List<SduiNode>
)

/** Bump when SduiNode/SduiAction's *shape* changes in a breaking way. */
const val SDUI_SCHEMA_VERSION = 1
