package com.koduok.compose.navigation

import androidx.compose.Composable
import androidx.compose.Providers
import androidx.compose.ambientOf
import androidx.compose.onActive
import androidx.compose.remember
import androidx.compose.state
import androidx.ui.core.Modifier
import androidx.ui.layout.Stack
import com.koduok.compose.navigation.core.BackStack
import com.koduok.compose.navigation.core.BackStackId
import com.koduok.compose.navigation.core.Route
import com.koduok.compose.navigation.core.RouteState
import com.koduok.compose.navigation.core.RouteState.Entering
import com.koduok.compose.navigation.core.RouteState.Exiting
import com.koduok.compose.navigation.core.RouteState.Settled
import com.koduok.compose.navigation.core.backStackController

internal val NullableBackStackAmbient = ambientOf<BackStack<Any>?> { null }
val BackStackAmbient = ambientOf<BackStack<Any>> { throw IllegalStateException("Missing Router(...) { ... } above") }

@Composable
inline fun <reified T : Any> Router(start: T, otherStart: List<T> = emptyList(), noinline children: @Composable BackStack<T>.(Route<T>) -> Unit) =
    Router(T::class.java.name, start, otherStart, children)

@Composable
fun <T : Any> Router(id: BackStackId, start: T, otherStart: List<T> = emptyList(), children: @Composable BackStack<T>.(Route<T>) -> Unit) {
    val backStack = remember {
        val parentKey = NullableBackStackAmbient.current?.key
        backStackController.register(id, parentKey, start, otherStart)
    }
    val showRoutesState = state { backStack.currentWithPotentialShowStack }

    onActive {
        val listener = object : BackStack.Listener<T> {
            override fun onCurrentChanged(route: Route<T>) {
                showRoutesState.value = backStack.currentWithPotentialShowStack
            }
        }
        backStack.addListener(listener)

        onDispose {
            backStack.removeListener(listener)
        }
    }

    @Suppress("UNCHECKED_CAST") val anyBackStack = backStack as BackStack<Any>
    Providers(BackStackAmbient.provides(anyBackStack), NullableBackStackAmbient.provides(anyBackStack)) {
        Stack(modifier = Modifier) {
            showRoutesState.value.forEach { children(backStack, it) }
        }
    }
}

//@Composable
//fun <T: Any> RouteScreen(routeStates: RouteState<T>) {
//    Stack {
//        mutsta
//    }
//}

@Composable
fun <T : Any> RouteScreen(backStack: BackStack<T>, routeState: RouteState<T>, children: @Composable BackStack<T>.(Route<T>) -> Unit) {
    when (routeState) {
        is Settled -> children(backStack, routeState.route)
        is Entering -> TODO()
        is Exiting -> TODO()
    }
}