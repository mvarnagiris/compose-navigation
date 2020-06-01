package com.koduok.compose.navigation.core

data class Route<T : Any> internal constructor(val key: BackStackKey, val index: Int, val routeDescription: RouteDescription<T>) {
    val value get() = routeDescription.value
    val data get() = routeDescription.value
}

data class RouteDescription<T : Any>(
    val value: T,
    val showRouteBelow: Boolean = false
) {
    fun showRouteBelow(drawPreviousRoute: Boolean = true) = copy(showRouteBelow = drawPreviousRoute)
}

sealed class RouteState<T : Any> {
    abstract val route: Route<T>

    data class Settled<T : Any>(override val route: Route<T>) : RouteState<T>()
    data class Entering<T : Any>(override val route: Route<T>) : RouteState<T>()
    data class Exiting<T : Any>(override val route: Route<T>) : RouteState<T>()
}