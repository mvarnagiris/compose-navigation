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

    internal fun hasParent(key: BackStackKey): Boolean {
        var currentParentKey = parentKey

        while (currentParentKey != null) {
            if (currentParentKey == key) return true
            currentParentKey = currentParentKey.parentKey
        }

        return false
    }
}

class BackStack<T> internal constructor(val key: BackStackKey, start: T, otherStart: List<T>) {
    private val listeners = mutableListOf<Listener<T>>()
    private val routes = mutableListOf(Route(key, start)).apply { addAll(otherStart.map { Route(key, it) }) }

    val snapshot get() = routes.toList()
    val current get() = routes.last()
    val previous get() = routes.dropLast(1).last()

    fun push(vararg values: T) {
        val routesToPush = values.map { Route(key, it) }

        routes.addAll(routesToPush)
        backStackController.onPushed(routesToPush)

        val listeners = listeners.toList()
        routesToPush.forEach {
            listeners.forEach { listener -> listener.onAdded(it) }
        }

        if (values.isNotEmpty()) {
            listeners.forEach { listener -> listener.onCurrentChanged(current) }
        }
    }

    fun pop(): Boolean {
        if (routes.size == 1) return false

        val routeToRemove = current
        routes.remove(routeToRemove)
        backStackController.onRemoved(routeToRemove)

        val listeners = listeners.toList()
        listeners.forEach { listener ->
            listener.onRemoved(routeToRemove)
            listener.onCurrentChanged(current)
        }

        return true
    }

    fun popAll() = popUntil { false }

    fun popUntil(predicate: (snapshot: List<Route<T>>) -> Boolean): Boolean {
        if (routes.size == 1) return false

        val currentRoute = current
        val listeners = listeners.toList()
        while (routes.size > 1 && !predicate(snapshot)) {
            val routeToRemove = current
            routes.remove(routeToRemove)
            backStackController.onRemoved(routeToRemove)

            listeners.forEach { listener -> listener.onRemoved(routeToRemove) }
        }

        val newCurrent = current
        if (currentRoute == newCurrent) return false

        listeners.forEach { listener -> listener.onCurrentChanged(newCurrent) }

        return true
    }

    fun popUntilAndPush(vararg values: T, predicate: (snapshot: List<Route<T>>) -> Boolean): Boolean {
        if (values.size == 1) return false

        val currentRoute = current
        val listeners = listeners.toList()
        while (values.size > 1 && !predicate(snapshot)) {
            val routeToRemove = current
            routes.remove(routeToRemove)
            backStackController.onRemoved(routeToRemove)

            listeners.forEach { listener -> listener.onRemoved(routeToRemove) }
        }

        val routesToPush = values.map { Route(key, it) }
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

    fun replace(vararg withValues: T) = replaceRoute(current, *withValues)
    fun replaceRoute(route: Route<T>, vararg withValues: T): Boolean {
        val routeIndex = routes.indexOf(route)
        if (routeIndex < 0) return false

        val currentRoute = current
        val routesToAdd = withValues.map { Route(key, it) }

        routes.removeAt(routeIndex)
        routes.addAll(routeIndex, routesToAdd)
        backStackController.onReplaced(route, routesToAdd)

        val listeners = listeners.toList()
        listeners.forEach { listener -> listener.onRemoved(route) }
        routesToAdd.forEach {
            listeners.forEach { listener -> listener.onAdded(it) }
        }
        if (currentRoute != current) {
            listeners.forEach { listener -> listener.onCurrentChanged(current) }
        }

        return true
    }

    internal fun popFromBackStackHandler(): Boolean {
        val removedRoute = current
        val isNotRoot = routes.size > 1
        if (isNotRoot) {
            routes.remove(removedRoute)
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

    interface Listener<T> {
        fun onAdded(route: Route<T>) = Unit
        fun onRemoved(route: Route<T>) = Unit
        fun onCurrentChanged(route: Route<T>) = Unit
    }
}