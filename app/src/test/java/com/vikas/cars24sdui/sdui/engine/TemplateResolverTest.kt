package com.vikas.cars24sdui.sdui.engine

import kotlinx.serialization.json.JsonNull
import kotlinx.serialization.json.JsonPrimitive
import org.junit.Assert.assertEquals
import org.junit.Test

class TemplateResolverTest {

    @Test
    fun `state template resolves from state map`() {
        val state = mapOf("used_cars_filter" to JsonPrimitive("hot_deals"))
        val resolved = resolveActionValue(JsonPrimitive("{{state.used_cars_filter}}"), state)
        assertEquals(JsonPrimitive("hot_deals"), resolved)
    }

    @Test
    fun `state template with missing key resolves to JsonNull`() {
        val resolved = resolveActionValue(JsonPrimitive("{{state.missing}}"), emptyMap())
        assertEquals(JsonNull, resolved)
    }

    @Test
    fun `event template resolves from event context`() {
        val resolved = resolveActionValue(
            JsonPrimitive("{{selectedId}}"),
            emptyMap(),
            mapOf("selectedId" to "hot_deals")
        )
        assertEquals(JsonPrimitive("hot_deals"), resolved)
    }

    @Test
    fun `plain literal passes through unchanged`() {
        val resolved = resolveActionValue(JsonPrimitive("wishlisted"), emptyMap())
        assertEquals(JsonPrimitive("wishlisted"), resolved)
    }

    @Test
    fun `null raw value resolves to JsonNull`() {
        assertEquals(JsonNull, resolveActionValue(null, emptyMap()))
    }
}
