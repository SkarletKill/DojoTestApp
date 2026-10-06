package uk.skarlet.dojotestapp.navigation

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.navigation3.rememberViewModelStoreNavEntryDecorator
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.runtime.rememberSaveableStateHolderNavEntryDecorator
import androidx.navigation3.ui.NavDisplay
import kotlinx.serialization.Serializable
import uk.skarlet.dojotestapp.feature.main.MainRoute

/** One serializable key per destination. Add a key + an `entry<Key>` per new screen. */
@Serializable
data object MainKey : NavKey

@Composable
fun AppNavigation() {
    val backStack = rememberNavBackStack(MainKey)

    NavDisplay(
        backStack = backStack,
        onBack = { backStack.removeLastOrNull() },
        // Scopes each ViewModel to its back-stack entry; it's cleared when the entry is popped.
        entryDecorators = listOf(
            rememberSaveableStateHolderNavEntryDecorator(),
            rememberViewModelStoreNavEntryDecorator(),
        ),
        entryProvider = entryProvider {
            entry<MainKey> { MainRoute() }
        },
    )
}
