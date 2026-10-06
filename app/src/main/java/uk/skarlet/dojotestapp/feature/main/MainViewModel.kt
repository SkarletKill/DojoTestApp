package uk.skarlet.dojotestapp.feature.main

import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import uk.skarlet.dojotestapp.core.mvi.BaseViewModel

@HiltViewModel
class MainViewModel @Inject constructor() :
    BaseViewModel<MainState, MainIntent, MainEffect>(MainState()) {

    override fun onIntent(intent: MainIntent) {
        // Handle intents with an exhaustive `when` once MainIntent has subtypes.
    }
}
