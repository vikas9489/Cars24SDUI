package com.vikas.cars24sdui.sdui.engine

import com.vikas.cars24sdui.sdui.registry.ComponentRegistry

/** Central place new component types get wired in -- visual renderers land here as they're built. */
object SduiRegistryInstaller {
    private var installed = false

    fun installDefaults() {
        if (installed) return
        installed = true
        ComponentRegistry.register("conditional", ConditionalRenderer)
    }
}
