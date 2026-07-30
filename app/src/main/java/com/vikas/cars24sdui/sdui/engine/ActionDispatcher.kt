package com.vikas.cars24sdui.sdui.engine

import android.util.Log
import com.vikas.cars24sdui.sdui.model.SduiAction
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonObject

private const val TAG = "SduiActionDispatcher"

/**
 * Interprets a JSON-declared action against page state or a navigation/
 * sheet callback. Same forward-compat rule as everywhere else in the
 * engine: an action `type` the dispatcher doesn't recognize is a no-op,
 * not a crash -- a server rolling out a new action kind degrades to
 * "this control does nothing yet" rather than breaking the page.
 */
class ActionDispatcher(
    private val state: MutableMap<String, JsonElement>,
    private val onNavigate: (route: String, params: JsonObject?) -> Unit = { _, _ -> },
    private val onOpenSheet: (sheetId: String, params: JsonObject?) -> Unit = { _, _ -> }
) {
    fun dispatch(action: SduiAction?, eventContext: Map<String, String> = emptyMap()) {
        val resolved = action ?: return
        when (resolved.type) {
            "SET_STATE" -> {
                val key = resolved.key ?: return
                state[key] = resolveActionValue(resolved.value, state, eventContext)
            }
            "NAVIGATE" -> {
                val route = resolved.route ?: return
                onNavigate(route, resolved.params)
            }
            "OPEN_SHEET" -> {
                val sheetId = resolved.sheetId ?: return
                onOpenSheet(sheetId, resolved.params)
            }
            else -> Log.w(TAG, "unrecognized/unimplemented action type '${resolved.type}' -- ignoring")
        }
    }
}
