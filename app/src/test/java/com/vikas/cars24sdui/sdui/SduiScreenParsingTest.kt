package com.vikas.cars24sdui.sdui

import com.vikas.cars24sdui.sdui.model.SduiNode
import com.vikas.cars24sdui.sdui.model.SduiScreen
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import java.io.File

/**
 * These tests exist because "valid JSON that decodes" is not the same as
 * "a correct schema" -- a typo'd component type, a mismatched stateKey, or
 * a chip option with no matching conditional case all decode fine and
 * only fail silently at render time (or worse, render the wrong content).
 * This suite catches those classes of authoring bugs before a renderer
 * even exists.
 */
class SduiScreenParsingTest {

    private val json = Json { ignoreUnknownKeys = true }

    /** Every component type the registry is planned to support (task #5). */
    private val knownComponentTypes = setOf(
        "header_bar", "chip_tab_row", "banner_card_rail", "icon_grid",
        "section", "car_card_rail", "car_card_grid", "promo_banner",
        "location_card", "conditional", "footer"
    )

    private val knownActionTypes = setOf("SET_STATE", "NAVIGATE", "OPEN_SHEET", "TOAST")

    /** "loyalty_points_widget" is deliberately never registered -- it's the unknown-component fallback demo, not a typo. */
    private val intentionallyUnregisteredTypes = setOf("loyalty_points_widget")

    private fun loadHomeScreen(): SduiScreen {
        val file = File("src/main/assets/sdui/home_screen.json")
        assertTrue("expected asset at ${file.absolutePath}", file.exists())
        return json.decodeFromString(SduiScreen.serializer(), file.readText())
    }

    private fun allNodes(nodes: List<SduiNode>): List<SduiNode> =
        nodes + nodes.flatMap { allNodes(it.children) }

    /** Finds every nested action-shaped object (has a "type" key) inside a props tree. */
    private fun findActionTypes(element: JsonElement, out: MutableList<String>) {
        when (element) {
            is JsonObject -> {
                (element["type"] as? JsonPrimitive)?.let { if (it.isString) out += it.content }
                element.values.forEach { findActionTypes(it, out) }
            }
            is JsonArray -> element.forEach { findActionTypes(it, out) }
            else -> Unit
        }
    }

    @Test
    fun `home screen json decodes and clears the assignment's complexity bar`() {
        val screen = loadHomeScreen()
        assertEquals("home", screen.screenId)

        val distinctTypes = allNodes(screen.sections).map { it.type }.toSet()

        assertTrue("expected >= 5 distinct section types, got $distinctTypes", distinctTypes.size >= 5)
        assertTrue("expected a rail component", "car_card_rail" in distinctTypes)
        assertTrue("expected a grid/list component", "car_card_grid" in distinctTypes)
        assertTrue("expected the interactive chip selector", "chip_tab_row" in distinctTypes)
        assertTrue(
            "expected the conditional that makes the chip selection actually change content",
            "conditional" in distinctTypes
        )
    }

    @Test
    fun `every node id is unique among its siblings`() {
        fun checkUnique(nodes: List<SduiNode>) {
            val ids = nodes.map { it.id }
            assertEquals("duplicate sibling id in $ids", ids.size, ids.toSet().size)
            nodes.forEach { checkUnique(it.children) }
        }
        checkUnique(loadHomeScreen().sections)
    }

    @Test
    fun `every component type is one the registry is planned to support`() {
        val screen = loadHomeScreen()
        val used = allNodes(screen.sections).map { it.type }.toSet()
        val unrecognized = used - knownComponentTypes - intentionallyUnregisteredTypes
        assertTrue(
            "found component type(s) not in the planned registry and not flagged as an " +
                "intentional fallback demo (typo?): $unrecognized",
            unrecognized.isEmpty()
        )
        assertTrue(
            "the unknown-component fallback demo node is missing from the payload",
            intentionallyUnregisteredTypes.all { it in used }
        )
    }

    @Test
    fun `every action anywhere in the payload uses a known action type`() {
        val screen = loadHomeScreen()
        val nodes = allNodes(screen.sections)

        val nodeLevelActionTypes = nodes.flatMap { it.actions.values }.map { it.type }

        val inlineActionTypes = mutableListOf<String>()
        nodes.forEach { findActionTypes(it.props, inlineActionTypes) }

        val allActionTypes = (nodeLevelActionTypes + inlineActionTypes).toSet()
        val unrecognized = allActionTypes - knownActionTypes
        assertTrue(
            "found action type(s) not in the dispatcher's known set (typo?): $unrecognized",
            unrecognized.isEmpty()
        )
    }

    @Test
    fun `every chip_tab_row's options are covered by its paired conditional's cases`() {
        val screen = loadHomeScreen()
        val nodes = allNodes(screen.sections)

        val chipRows = nodes.filter { it.type == "chip_tab_row" }
        val conditionals = nodes.filter { it.type == "conditional" }

        chipRows.forEach { chip ->
            val stateKey = chip.props["stateKey"]?.jsonPrimitive?.content ?: return@forEach
            val optionIds = chip.props["options"]?.let { opts ->
                (opts as? JsonArray)?.map { it.jsonObject["id"]!!.jsonPrimitive.content }
            } ?: emptyList()

            val pairedConditional = conditionals.firstOrNull {
                it.props["stateKey"]?.jsonPrimitive?.content == stateKey
            } ?: return@forEach // not every chip drives a conditional (e.g. the top category tabs) -- fine

            val caseKeys = pairedConditional.children.map {
                it.props["caseKey"]!!.jsonPrimitive.content
            }.toSet()

            val uncovered = optionIds.toSet() - caseKeys
            assertTrue(
                "chip_tab_row '${chip.id}' has option(s) $uncovered with no matching case in " +
                    "conditional '${pairedConditional.id}' (cases: $caseKeys) -- selecting that chip " +
                    "would render nothing",
                uncovered.isEmpty()
            )
        }
    }

    @Test
    fun `content spot-checks match the reference screenshot`() {
        val raw = File("src/main/assets/sdui/home_screen.json").readText()
        val screen = loadHomeScreen()

        val header = screen.sections.first { it.type == "header_bar" }
        assertEquals("Chandigarh", header.props["location"]!!.jsonPrimitive.content)

        assertTrue("expected the reference used-car listing to be present", "Celerio" in raw)
        assertTrue("expected the trending Kia line-up from the screenshot", "Seltos" in raw)
        assertTrue("expected the showroom name from the screenshot", "Bestech Square Mall" in raw)
        assertTrue("expected the footer tagline from the screenshot", "Gurugram" in raw)
    }
}
