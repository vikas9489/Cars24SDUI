package com.vikas.cars24sdui.sdui.registry

object ComponentRegistry {
    private val renderers = mutableMapOf<String, ComponentRenderer>()

    fun register(type: String, renderer: ComponentRenderer) {
        renderers[type] = renderer
    }

    fun rendererFor(type: String): ComponentRenderer = renderers[type] ?: UnknownComponentFallback
}
