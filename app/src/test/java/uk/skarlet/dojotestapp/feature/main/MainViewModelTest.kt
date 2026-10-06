package uk.skarlet.dojotestapp.feature.main

import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import uk.skarlet.dojotestapp.testing.MainDispatcherRule

class MainViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    @Test
    fun `starts in the initial state`() {
        assertEquals(MainState(), MainViewModel().state.value)
    }
}
