package com.vikas.cars24sdui.sdui.engine

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

/** Shown instead of attempting to render a payload whose minSupportedVersion exceeds what this client build understands. */
@Composable
fun UpdateRequiredScreen(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier.fillMaxSize().padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text("Update required", style = MaterialTheme.typography.titleMedium)
        Text(
            "This version of the app can't render the latest home screen. Please update to continue.",
            style = MaterialTheme.typography.bodyMedium
        )
    }
}
