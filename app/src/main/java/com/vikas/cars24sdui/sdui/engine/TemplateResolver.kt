package com.vikas.cars24sdui.sdui.engine

import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonNull
import kotlinx.serialization.json.JsonPrimitive

// Closing }} must be escaped: Android's on-device ICU regex engine rejects a
// bare "}" as a syntax error even though desktop JVM regex (and unit tests
// running on the host JVM) accept it unescaped -- caught only at real runtime.
private val STATE_TEMPLATE = Regex("""^\{\{\s*state\.([a-zA-Z0-9_]+)\s*\}\}$""")
private val EVENT_TEMPLATE = Regex("""^\{\{\s*([a-zA-Z0-9_]+)\s*\}\}$""")

/**
 * Resolves an SduiAction's `value`/param fields against page state
 * ("{{state.used_cars_filter}}") or the firing event's own context
 * ("{{selectedId}}", e.g. which chip was tapped). Anything that isn't a
 * "{{...}}" string is returned unchanged -- this is intentionally just
 * substitution, not an expression language.
 */
fun resolveActionValue(
    raw: JsonElement?,
    state: Map<String, JsonElement>,
    eventContext: Map<String, String> = emptyMap()
): JsonElement {
    if (raw !is JsonPrimitive || !raw.isString) return raw ?: JsonNull
    val content = raw.content

    STATE_TEMPLATE.matchEntire(content)?.let { match ->
        return state[match.groupValues[1]] ?: JsonNull
    }
    EVENT_TEMPLATE.matchEntire(content)?.let { match ->
        return eventContext[match.groupValues[1]]?.let { JsonPrimitive(it) } ?: JsonNull
    }
    return raw
}
