package com.vikas.cars24sdui.sdui.engine

import android.app.Activity
import android.util.Log
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.vikas.cars24sdui.sdui.model.SDUI_SCHEMA_VERSION
import kotlinx.coroutines.launch
import kotlinx.serialization.json.JsonObject

/** Root entry point: loads a screen's JSON from assets and renders it end to end. */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SduiScreenHost(assetPath: String, modifier: Modifier = Modifier) {
    val context = LocalContext.current
    val screen = remember(assetPath) { SduiAssetLoader.loadScreen(context, assetPath) }

    if (screen.minSupportedVersion > SDUI_SCHEMA_VERSION) {
        UpdateRequiredScreen(modifier)
        return
    }

    remember { SduiRegistryInstaller.installDefaults() }

    val viewModel: SduiViewModel = viewModel()
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    var activeSheet by remember { mutableStateOf<Pair<String, JsonObject?>?>(null) }

    val dispatcher = remember(viewModel) {
        ActionDispatcher(
            state = viewModel.state,
            onNavigate = { route, params ->
                scope.launch {
                    snackbarHostState.showSnackbar("Navigate → $route ${params?.toString().orEmpty()}")
                }
            },
            onOpenSheet = { sheetId, params -> activeSheet = sheetId to params }
        )
    }

    // TTR/TTI marker for PERF.md: fires once the first composition (all
    // above-the-fold content) has committed. reportFullyDrawn() is what
    // `adb shell am start -W` and Logcat's "Fully drawn" line measure from
    // process start.
    LaunchedEffect(Unit) {
        (context as? Activity)?.reportFullyDrawn()
        Log.i("SduiPerf", "view_build_complete")
    }

    Scaffold(
        modifier = modifier,
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
        ) {
            items(screen.sections, key = { it.id }) { node ->
                RenderNode(node, viewModel.state, dispatcher)
            }
        }
    }

    activeSheet?.let { (sheetId, params) ->
        ModalBottomSheet(onDismissRequest = { activeSheet = null }) {
            Column(Modifier.fillMaxWidth().padding(24.dp)) {
                Text("Sheet: $sheetId", style = MaterialTheme.typography.titleMedium)
                Text(params?.toString() ?: "(no params)", style = MaterialTheme.typography.bodyMedium)
            }
        }
    }
}
