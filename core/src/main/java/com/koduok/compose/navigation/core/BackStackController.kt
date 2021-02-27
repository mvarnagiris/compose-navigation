package com.koduok.compose.navigation.core

val backStackController by lazy { BackStackController() }

class BackStackController internal constructor() {
    private val listeners = mutableListOf<Listener>()
    private val backStacks = mutableMapOf<BackStackKey, BackStack<*>>()
    private val globalRoutes = mutableListOf<Route<*>>()
    val snapshot: List<Route<*>> get() = globalRoutes.toList()

    fun <T : Any> register(id: BackStackId, parentKey: BackStackKey?, start: T, otherStart: List<T>): BackStack<T> {
        val key = BackStackKey(id, parentKey)
        val existing = getExistingBackStack(key, start, otherStart)
        if (existing != null) return existing

        val backStack = BackStack(key, start, otherStart)
        backStacks[key] = backStack

        backStack.snapshot.forEach {
            globalRoutes.add(it)
        }

        notifyListeners()
        return backStack
    }

    private fun <T : Any> getExistingBackStack(key: BackStackKey, start: T, otherStart: List<T>): BackStack<T>? {
        val existingBackStack = backStacks[key]

        @Suppress("UNCHECKED_CAST")
        val existingTypedBackStack = existingBackStack as? BackStack<T>?
        if (existingBackStack != null && existingTypedBackStack == null) {
            throw IllegalStateException(
                """BackStack with same BackStackKey already exists, but it's for different type
                    |BackStackKey: $key
                    |Existing: $existingBackStack
                    |Trying to register back stack with start: $start and $otherStart
                """.trimMargin()
            )
        }
        return existingTypedBackStack
    }

    fun pop(): Boolean {
        if (globalRoutes.size <= 1) return false

        val globalRoute = globalRoutes.removeAt(globalRoutes.size - 1)
        val shouldRemoveBackStack = backStacks[globalRoute.key]?.popFromBackStackHandler()?.not() ?: false
        if (shouldRemoveBackStack) {
            backStacks.remove(globalRoute.key)
            return pop()
        }

        notifyListeners()
        return true
    }

    internal fun onPushed(routes: List<Route<*>>) {
        routes.forEach { globalRoutes.add(it) }
        notifyListeners()
    }

    internal fun onRemoved(route: Route<*>) {
        globalRoutes.remove(route)
        clearChildBackStacks(route)
        notifyListeners()
    }

    internal fun onReplaced(route: Route<*>, withRoutes: List<Route<*>>) {
        if (withRoutes.isEmpty()) {
            onRemoved(route)
            return
        }

        val index = globalRoutes.indexOf(route)
        globalRoutes.remove(route)
        if (route != withRoutes.last()) {
            clearChildBackStacks(route)
        } else {
            popChildBackStacks(route)
        }

        globalRoutes.addAll(index, withRoutes)
        notifyListeners()
    }

    private fun clearChildBackStacks(route: Route<*>) {
        val backStacksToClear = backStacks.values
            .filter { backStack -> backStack.key.hasParent(route.key) }
            .sortedByDescending { it.key.parentCount }

        backStacksToClear.forEach {
            globalRoutes.removeAll(it.snapshot)
            var popAgain = it.popFromBackStackHandler()
            while (popAgain) {
                popAgain = it.popFromBackStackHandler()
            }
            backStacks.remove(it.key)
        }
    }

    private fun popChildBackStacks(route: Route<*>) {
        val backStacksToPop = backStacks.values
            .filter { backStack -> backStack.key.hasParent(route.key) }
            .sortedByDescending { it.key.parentCount }

        backStacksToPop.forEach {
            var backStackRoute = it.current
            var popAgain = it.popFromBackStackHandler()
            if (popAgain) {
                globalRoutes.remove(backStackRoute)
            }

            while (popAgain) {
                backStackRoute = it.current
                popAgain = it.popFromBackStackHandler()
                if (popAgain) {
                    globalRoutes.remove(backStackRoute)
                }
            }
        }
    }

    fun addListener(listener: Listener) {
        listeners.add(listener)
        listener.onBackStackChanged(snapshot)
    }

    fun removeListener(listener: Listener) {
        listeners.remove(listener)
    }

    private fun notifyListeners() {
        if (listeners.isEmpty()) return
        val snapshot = snapshot
        listeners.forEach { it.onBackStackChanged(snapshot) }
    }

    interface Listener {
        fun onBackStackChanged(snapshot: List<Route<*>>)
    }
}
