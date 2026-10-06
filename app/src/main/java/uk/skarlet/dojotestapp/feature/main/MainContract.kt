package uk.skarlet.dojotestapp.feature.main

data class MainState(
    val isLoading: Boolean = false,
)

sealed interface MainIntent

sealed interface MainEffect
