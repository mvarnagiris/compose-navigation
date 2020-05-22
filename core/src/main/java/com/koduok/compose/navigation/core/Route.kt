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