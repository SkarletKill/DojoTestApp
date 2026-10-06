package uk.skarlet.dojotestapp.core.mvi

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/**
 * MVI contract:
 * - [State]  — one immutable snapshot the UI renders.
 * - [Intent] — the only way in: user actions and UI events.
 * - [Effect] — one-off events (navigation, snackbar) that must not replay on recomposition/rotation.
 */
abstract class BaseViewModel<State, Intent, Effect>(initialState: State) : ViewModel() {

    private val _state = MutableStateFlow(initialState)
    val state: StateFlow<State> = _state.asStateFlow()

    // Channel, not SharedFlow: effects sent while the UI is stopped are delivered once it resumes.
    private val _effects = Channel<Effect>(Channel.BUFFERED)
    val effects: Flow<Effect> = _effects.receiveAsFlow()

    protected val currentState: State get() = _state.value

    abstract fun onIntent(intent: Intent)

    protected fun setState(reduce: State.() -> State) = _state.update(reduce)

    protected fun sendEffect(effect: Effect) {
        viewModelScope.launch { _effects.send(effect) }
    }
}
