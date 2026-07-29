package com.vikas.cars24sdui.sdui

import com.vikas.cars24sdui.sdui.model.SduiNode
import com.vikas.cars24sdui.sdui.model.SduiScreen
import kotlinx.serialization.json.Json
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import java.io.File

class SduiScreenParsingTest {

    private val json = Json { ignoreUnknownKeys = true }

    private fun loadHomeScreen(): SduiScreen {
        val file = File("src/main/assets/sdui/home_screen.json")
        assertTrue("expected asset at ${file.absolutePath}", file.exists())
        return json.decodeFromString(SduiScreen.serializer(), file.readText())
    }

    @Test
    fun `home screen json decodes and clears the assignment's complexity bar`() {
        val screen = loadHomeScreen()
        assertEquals("home", screen.screenId)

        val distinctTypes = mutableSetOf<String>()
        fun collectTypes(nodes: List<SduiNode>) {
            nodes.forEach {
                distinctTypes += it.type
                collectTypes(it.children)
            }
        }
        collectTypes(screen.sections)

        assertTrue("expected >= 5 distinct section types, got $distinctTypes", distinctTypes.size >= 5)
        assertTrue("expected a rail component", "car_card_rail" in distinctTypes)
        assertTrue("expected a grid/list component", "car_card_grid" in distinctTypes)
        assertTrue("expected the interactive chip selector", "chip_tab_row" in distinctTypes)
        assertTrue("expected the conditional that makes the chip selection actually change content", "conditional" in distinctTypes)
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
}
