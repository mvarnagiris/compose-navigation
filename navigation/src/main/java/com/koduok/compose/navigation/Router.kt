package com.koduok.compose.navigation

import androidx.activity.OnBackPressedCallback
import androidx.activity.compose.LocalOnBackPressedDispatcherOwner
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveableStateHolder
import androidx.compose.runtime.setValue
import com.koduok.compose.navigation.core.BackStack
import com.koduok.compose.navigation.core.BackStackController.Listener
import com.koduok.compose.navigation.core.BackStackId
import com.koduok.compose.navigation.core.Route
import com.koduok.compose.navigation.core.backStackController

internal val LocalNullableBackStack = compositionLocalOf<BackStack<*>?> { null }
internal val LocalOnBackPressedDispatcherEnabled = compositionLocalOf { false }
val LocalBackStack = compositionLocalOf<BackStack<*>> { throw IllegalStateException("Missing Router(...) { ... } above") }

@Composable
inline fun <reified T : Any> Router(start: T, otherStart: List<T> = emptyList(), noinline content: @Composable BackStack<T>.(Route<T>) -> Unit) =
    Router(T::class.java.name, start, otherStart, content)

@Composable
fun <T : Any> Router(id: BackStackId, start: T, otherStart: List<T> = emptyList(), content: @Composable BackStack<T>.(Route<T>) -> Unit) {
    val parentKey = LocalNullableBackStack.current?.key
    val backStack = remember { backStackController.register(id, parentKey, start, otherStart) }
    val saveableStateHolder = rememberSaveableStateHolder()
    var currentRoute by remember { mutableStateOf(backStack.current) }

    HandleBackPress()

    DisposableEffect(Unit) {
        val listener = object : BackStack.Listener<T> {
            override fun onCurrentChanged(route: Route<T>) {
                currentRoute = backStack.current
            }
        }

        backStack.addListener(listener)

        onDispose {
            backStack.removeListener(listener)
        }
    }

    val anyBackStack = backStack as BackStack<*>
    CompositionLocalProvider(
        LocalBackStack.provides(anyBackStack),
        LocalNullableBackStack.provides(anyBackStack),
        LocalOnBackPressedDispatcherEnabled.provides(true),
    ) {
        saveableStateHolder.SaveableStateProvider(currentRoute.value.toString()) {
            content(backStack, currentRoute)
        }
    }

}

@Composable
private fun HandleBackPress() {
    val onBackPressedDispatcher = LocalOnBackPressedDispatcherOwner.current?.onBackPressedDispatcher
    val isBackPressAlreadyHandled = LocalOnBackPressedDispatcherEnabled.current

    if (!isBackPressAlreadyHandled) {
        DisposableEffect(Unit) {
            val onBackPressCallback = object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    backStackController.pop()
                }
            }
            onBackPressedDispatcher?.addCallback(onBackPressCallback)

            backStackController.addListener(object : Listener {
                override fun onBackStackChanged(snapshot: List<Route<*>>) {
                    onBackPressCallback.isEnabled = snapshot.size > 1
                }
            })

            onDispose {
                onBackPressCallback.remove()
            }
        }
    }
}