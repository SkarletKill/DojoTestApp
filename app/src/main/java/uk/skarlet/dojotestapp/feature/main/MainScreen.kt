package uk.skarlet.dojotestapp.feature.main

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import uk.skarlet.dojotestapp.R
import uk.skarlet.dojotestapp.core.designsystem.AppTheme
import uk.skarlet.dojotestapp.core.mvi.CollectEffects

/** Stateful entry: wires the ViewModel. [MainScreen] stays stateless and previewable. */
@Composable
fun MainRoute(viewModel: MainViewModel = hiltViewModel()) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    CollectEffects(viewModel.effects) { _ ->
        // Handle effects with an exhaustive `when` once MainEffect has subtypes.
    }
    MainScreen(state = state, onIntent = viewModel::onIntent)
}

@Composable
fun MainScreen(
    state: MainState,
    onIntent: (MainIntent) -> Unit,
    modifier: Modifier = Modifier,
) {
    Scaffold(modifier = modifier) { padding ->
        Box(Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
            if (state.isLoading) {
                CircularProgressIndicator()
            } else {
                Text(stringResource(R.string.app_name), style = MaterialTheme.typography.headlineMedium)
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun MainScreenPreview() = AppTheme {
    MainScreen(state = MainState(), onIntent = {})
}
