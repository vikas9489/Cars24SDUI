package com.vikas.cars24sdui.sdui.engine

import com.vikas.cars24sdui.sdui.model.SduiAction
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.buildJsonObject
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class ActionDispatcherTest {

    @Test
    fun `SET_STATE writes a resolved value into state`() {
        val state = mutableMapOf<String, JsonElement>()
        val dispatcher = ActionDispatcher(state)

        dispatcher.dispatch(
            SduiAction(type = "SET_STATE", key = "used_cars_filter", value = JsonPrimitive("{{selectedId}}")),
            eventContext = mapOf("selectedId" to "hot_deals")
        )

        assertEquals(JsonPrimitive("hot_deals"), state["used_cars_filter"])
    }

    @Test
    fun `NAVIGATE invokes the onNavigate callback with route and params`() {
        var capturedRoute: String? = null
        var capturedCarId: String? = null
        val dispatcher = ActionDispatcher(
            state = mutableMapOf(),
            onNavigate = { route, params ->
                capturedRoute = route
                capturedCarId = params?.get("carId")?.let { (it as JsonPrimitive).content }
            }
        )

        dispatcher.dispatch(
            SduiAction(
                type = "NAVIGATE",
                route = "car_details",
                params = buildJsonObject { put("carId", JsonPrimitive("celerio_2015")) }
            )
        )

        assertEquals("car_details", capturedRoute)
        assertEquals("celerio_2015", capturedCarId)
    }

    @Test
    fun `OPEN_SHEET invokes the onOpenSheet callback`() {
        var capturedSheetId: String? = null
        val dispatcher = ActionDispatcher(
            state = mutableMapOf(),
            onOpenSheet = { sheetId, _ -> capturedSheetId = sheetId }
        )

        dispatcher.dispatch(SduiAction(type = "OPEN_SHEET", sheetId = "call_showroom"))

        assertEquals("call_showroom", capturedSheetId)
    }

    @Test
    fun `unrecognized action type is a no-op, not a crash`() {
        val state = mutableMapOf<String, JsonElement>()
        val dispatcher = ActionDispatcher(state)

        dispatcher.dispatch(SduiAction(type = "SOME_FUTURE_ACTION_TYPE", key = "x", value = JsonPrimitive("y")))

        assertNull(state["x"])
    }

    @Test
    fun `null action is a no-op`() {
        val dispatcher = ActionDispatcher(mutableMapOf())
        dispatcher.dispatch(null) // should not throw
    }
}
