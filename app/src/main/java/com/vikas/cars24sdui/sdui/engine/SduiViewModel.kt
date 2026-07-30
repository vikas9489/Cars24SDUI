package com.vikas.cars24sdui.sdui.engine

import androidx.compose.runtime.mutableStateMapOf
import androidx.lifecycle.ViewModel
import kotlinx.serialization.json.JsonElement

/** One state map per screen -- everything a chip/tab/toggle can drive lives here. */
class SduiViewModel : ViewModel() {
    val state = mutableStateMapOf<String, JsonElement>()
}
