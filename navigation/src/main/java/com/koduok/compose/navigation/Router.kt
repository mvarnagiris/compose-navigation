package com.koduok.compose.navigation

import androidx.compose.Composable
import androidx.compose.Providers
import androidx.compose.ambientOf
import androidx.compose.onActive
import androidx.compose.remember
import androidx.compose.state
import com.koduok.compose.navigation.core.BackStack
import com.koduok.compose.navigation.core.BackStackId
import com.koduok.compose.navigation.core.Route
import com.koduok.compose.navigation.core.backStackController

internal val NullableBackStackAmbient = ambientOf<BackStack<Any>?> { null }
val BackStackAmbient = ambientOf<BackStack<Any>> { throw IllegalStateException("Missing Router(...) { ... } above") }

@Composable
inline fun <reified T : Any> Router(start: T, otherStart: List<T> = emptyList(), noinline children: @Composable() BackStack<T>.(Route<T>) -> Unit) =
    Router(T::class.java.name, start, otherStart, children)

@Composable
fun <T : Any> Router(id: BackStackId, start: T, otherStart: List<T> = emptyList(), children: @Composable() BackStack<T>.(Route<T>) -> Unit) {
    val parentKey = remember { NullableBackStackAmbient.current?.key }
    val backStack = remember { backStackController.register(id, parentKey, start, otherStart) }
    var currentRouteState by state { backStack.current }

    onActive {
        val listener = object : BackStack.Listener<T> {
            override fun onCurrentChanged(route: Route<T>) {
                currentRouteState = route
            }
        }
        backStack.addListener(listener)

        onDispose {
            backStack.removeListener(listener)
        }
    }

    @Suppress("UNCHECKED_CAST") val anyBackStack = backStack as BackStack<Any>
    Providers(BackStackAmbient.provides(anyBackStack), NullableBackStackAmbient.provides(anyBackStack)) {
        children(backStack, currentRouteState)
    }
}