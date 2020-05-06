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

data class Route<T : Any> internal constructor(val key: BackStackKey, val index: Int, val data: T)

class BackStack<T : Any> internal constructor(val key: BackStackKey, start: T, otherStart: List<T>) {
    private val listeners = mutableListOf<Listener<T>>()
    private val routes = mutableListOf(Route(key, 0, start))
        .apply { addAll(otherStart.mapIndexed { index: Int, route: T -> Route(key, 1 + index, route) }) }

    val snapshot get() = routes.toList()
    val current get() = routes.last()

    fun push(vararg data: T) {
        val startIndex = current.index + 1
        val routesToPush = data.mapIndexed { index: Int, route: T -> Route(key, startIndex + index, route) }

        routes.addAll(routesToPush)
        backStackController.onPushed(routesToPush)

        val listeners = listeners.toList()
        routesToPush.forEach {
            listeners.forEach { listener -> listener.onAdded(it) }
        }

        if (data.isNotEmpty()) {
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

        val currentIndex = current.index
        val listeners = listeners.toList()
        var indexToBeRemoved = currentIndex
        var routeToBeRemoved = routes[indexToBeRemoved]
        while (routeToBeRemoved.index > 0 && !predicate(routeToBeRemoved)) {
            routes.removeAt(indexToBeRemoved)
            backStackController.onRemoved(routeToBeRemoved)

            listeners.forEach { listener -> listener.onRemoved(routeToBeRemoved) }
            indexToBeRemoved = current.index
            routeToBeRemoved = routes[indexToBeRemoved]
        }

        if (currentIndex == current.index) return false

        val newCurrent = current
        listeners.forEach { listener -> listener.onCurrentChanged(newCurrent) }

        return true
    }

    fun replace(vararg withData: T) = replaceRoute(routes.last(), *withData)

    fun replaceRoute(route: Route<T>, vararg withData: T): Boolean {
        val routeIndex = routes.indexOf(route)
        if (routeIndex < 0) return false

        val currentRoute = current

        val indexDelta = withData.size - 1
        val routesToAdd = withData.mapIndexed { index: Int, data: T -> Route(key, routeIndex + index, data) }
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