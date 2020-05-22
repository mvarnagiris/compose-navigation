package com.koduok.compose.navigation.core

typealias BackStackId = String

data class BackStackKey internal constructor(val id: BackStackId, val parentKey: BackStackKey?) {

    val parentCount by lazy {
        var count = 0
        var currentParentKey = parentKey

        while (currentParentKey != null) {
            count++
            currentParentKey = currentParentKey?.parentKey
        }

        count
    }

    internal fun hasAnyInParents(keys: List<BackStackKey>): Boolean {
        var currentParentKey = parentKey

        while (currentParentKey != null) {
            if (keys.contains(currentParentKey)) return true
            currentParentKey = currentParentKey.parentKey
        }

        return false
    }
}

class BackStack<T : Any> internal constructor(val key: BackStackKey, start: RouteDescription<T>, otherStart: List<RouteDescription<T>>) {
    private val listeners = mutableListOf<Listener<T>>()
    private val routes = mutableListOf(Route(key, 0, start))
        .apply { addAll(otherStart.mapIndexed { index: Int, routeDescription: RouteDescription<T> -> Route(key, 1 + index, routeDescription) }) }

    val snapshot get() = routes.toList()
    val current get() = routes.last()
    val currentWithShowStack: List<Route<T>>
        get() {
            val current = current
            var onTop = current
            return snapshot.takeLastWhile {
                val take = onTop.routeDescription.showRouteBelow || it == current
                onTop = it
                take
            }
        }

    fun push(vararg values: T) = push(*values.map { RouteDescription(it) }.toTypedArray())
    fun push(vararg descriptions: RouteDescription<T>) {
        val startIndex = current.index + 1
        val routesToPush = descriptions.mapIndexed { index: Int, description: RouteDescription<T> -> Route(key, startIndex + index, description) }

        routes.addAll(routesToPush)
        backStackController.onPushed(routesToPush)

        val listeners = listeners.toList()
        routesToPush.forEach {
            listeners.forEach { listener -> listener.onAdded(it) }
        }

        if (descriptions.isNotEmpty()) {
            listeners.forEach { listener -> listener.onCurrentChanged(current) }
        }
    }

    fun pop(): Boolean {
        if (current.index == 0) return false

        val removedIndex = current.index
        val removedRoute = routes.removeAt(removedIndex)
        backStackController.onRemoved(removedRoute)

        val listeners = listeners.toList()
        listeners.forEach { listener ->
            listener.onRemoved(removedRoute)
            listener.onCurrentChanged(current)
        }

        return true
    }

    fun popUntil(predicate: (Route<T>) -> Boolean): Boolean {
        if (current.index == 0) return false

        val currentRoute = current
        val listeners = listeners.toList()
        var indexToBeRemoved = currentRoute.index
        var routeToBeRemoved = routes[indexToBeRemoved]
        while (routeToBeRemoved.index > 0 && !predicate(routeToBeRemoved)) {
            routes.removeAt(indexToBeRemoved)
            backStackController.onRemoved(routeToBeRemoved)

            listeners.forEach { listener -> listener.onRemoved(routeToBeRemoved) }
            indexToBeRemoved = current.index
            routeToBeRemoved = routes[indexToBeRemoved]
        }

        val newCurrent = current
        if (currentRoute == newCurrent) return false

        listeners.forEach { listener -> listener.onCurrentChanged(newCurrent) }

        return true
    }

    fun popUntilAndPush(vararg descriptions: RouteDescription<T>, predicate: (Route<T>) -> Boolean): Boolean {
        if (current.index == 0) return false

        val currentRoute = current
        val listeners = listeners.toList()
        var indexToBeRemoved = currentRoute.index
        var routeToBeRemoved = routes[indexToBeRemoved]
        while (routeToBeRemoved.index > 0 && !predicate(routeToBeRemoved)) {
            routes.removeAt(indexToBeRemoved)
            backStackController.onRemoved(routeToBeRemoved)

            listeners.forEach { listener -> listener.onRemoved(routeToBeRemoved) }
            indexToBeRemoved = current.index
            routeToBeRemoved = routes[indexToBeRemoved]
        }

        val startIndex = current.index + 1
        val routesToPush = descriptions.mapIndexed { index: Int, description: RouteDescription<T> -> Route(key, startIndex + index, description) }
        routes.addAll(routesToPush)
        backStackController.onPushed(routesToPush)
        routesToPush.forEach {
            listeners.forEach { listener -> listener.onAdded(it) }
        }

        val newCurrent = current
        if (currentRoute == newCurrent) return false

        listeners.forEach { listener -> listener.onCurrentChanged(newCurrent) }

        return true
    }

    fun replace(vararg withValues: T) = replaceRoute(routes.last(), *withValues.map { RouteDescription(it) }.toTypedArray())
    fun replace(vararg withDescriptions: RouteDescription<T>) = replaceRoute(routes.last(), *withDescriptions)

    fun replaceRoute(route: Route<T>, vararg withValues: T) = replaceRoute(route, *withValues.map { RouteDescription(it) }.toTypedArray())
    fun replaceRoute(route: Route<T>, vararg withDescriptions: RouteDescription<T>): Boolean {
        val routeIndex = routes.indexOf(route)
        if (routeIndex < 0) return false

        val currentRoute = current

        val indexDelta = withDescriptions.size - 1
        val routesToAdd = withDescriptions.mapIndexed { index: Int, description: RouteDescription<T> -> Route(key, routeIndex + index, description) }
        val updatedIndexRoutes = if (indexDelta == 0) emptyList() else {
            val count = routes.size - 1 - routeIndex
            routes.takeLast(count).map { it to it.copy(index = it.index + indexDelta) }
        }

        routes.removeAt(routeIndex)
        routes.addAll(routeIndex, routesToAdd)
        backStackController.onReplaced(route, routesToAdd)
        backStackController.onIndexChanged(updatedIndexRoutes)

        val listeners = listeners.toList()
        listeners.forEach { listener -> listener.onRemoved(route) }
        routesToAdd.forEach {
            listeners.forEach { listener -> listener.onAdded(it) }
        }
        updatedIndexRoutes.forEach {
            listeners.forEach { listener -> listener.onIndexChanged(it.second, it.first.index) }
        }
        if (currentRoute != current) {
            listeners.forEach { listener -> listener.onCurrentChanged(current) }
        }

        return true
    }

    internal fun popFromBackStackHandler(): Boolean {
        val removedIndex = current.index
        val removedRoute = routes[removedIndex]
        val isNotRoot = removedIndex > 0
        if (isNotRoot) {
            routes.removeAt(removedIndex)
        }

        val listeners = listeners.toList()
        listeners.forEach { listener ->
            listener.onRemoved(removedRoute)
            if (isNotRoot) {
                listener.onCurrentChanged(current)
            }
        }

        return isNotRoot
    }

    fun addListener(listener: Listener<T>) {
        listeners.add(listener)
    }

    fun removeListener(listener: Listener<T>) {
        listeners.remove(listener)
    }

    interface Listener<T : Any> {
        fun onAdded(route: Route<T>) = Unit
        fun onRemoved(route: Route<T>) = Unit
        fun onCurrentChanged(route: Route<T>) = Unit
        fun onIndexChanged(route: Route<T>, previousIndex: Int) = Unit
    }
}