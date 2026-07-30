package com.vikas.cars24sdui.sdui.engine

import com.vikas.cars24sdui.sdui.components.BannerCardRailRenderer
import com.vikas.cars24sdui.sdui.components.CarCardGridRenderer
import com.vikas.cars24sdui.sdui.components.CarCardRailRenderer
import com.vikas.cars24sdui.sdui.components.ChipTabRowRenderer
import com.vikas.cars24sdui.sdui.components.FooterRenderer
import com.vikas.cars24sdui.sdui.components.HeaderBarRenderer
import com.vikas.cars24sdui.sdui.components.IconGridRenderer
import com.vikas.cars24sdui.sdui.components.IconRailRenderer
import com.vikas.cars24sdui.sdui.components.LocationCardRenderer
import com.vikas.cars24sdui.sdui.components.PromoBannerRenderer
import com.vikas.cars24sdui.sdui.components.SearchHeaderBarRenderer
import com.vikas.cars24sdui.sdui.components.SectionRenderer
import com.vikas.cars24sdui.sdui.registry.ComponentRegistry

/** Central place new component types get wired in -- this is the entire diff needed to support a new type once its renderer exists. */
object SduiRegistryInstaller {
    private var installed = false

    fun installDefaults() {
        if (installed) return
        installed = true
        ComponentRegistry.register("conditional", ConditionalRenderer)
        ComponentRegistry.register("header_bar", HeaderBarRenderer)
        ComponentRegistry.register("chip_tab_row", ChipTabRowRenderer)
        ComponentRegistry.register("banner_card_rail", BannerCardRailRenderer)
        ComponentRegistry.register("icon_grid", IconGridRenderer)
        ComponentRegistry.register("section", SectionRenderer)
        ComponentRegistry.register("car_card_rail", CarCardRailRenderer)
        ComponentRegistry.register("car_card_grid", CarCardGridRenderer)
        ComponentRegistry.register("promo_banner", PromoBannerRenderer)
        ComponentRegistry.register("location_card", LocationCardRenderer)
        ComponentRegistry.register("footer", FooterRenderer)
        ComponentRegistry.register("search_header_bar", SearchHeaderBarRenderer)
        ComponentRegistry.register("icon_rail", IconRailRenderer)
    }
}
