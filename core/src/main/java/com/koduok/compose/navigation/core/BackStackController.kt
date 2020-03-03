package com.koduok.compose.navigation.core

val backStackController by lazy { BackStackController() }

class GlobalRoute internal constructor(vararg routes: Route<*>) {
    private val routes = mutableListOf(*routes)
    internal val keys get() = routes.map { it.key }
    val snapshot get() = routes.toList()

    internal fun contains(key: BackStackKey) = routes.any { it.key == key }
    internal fun contains(route: Route<*>) = routes.any { it == route }
    internal fun add(route: Route<*>) = routes.add(route)
    internal fun remove(route: Route<*>) = routes.remove(route)
    internal fun isEmpty() = routes.isEmpty()
    internal fun replace(route: Route<*>, withRoute: Route<*>) = routes.remove(route).also { routes.add(withRoute) }
}

class BackStackController internal constructor() {
    private val listeners = mutableListOf<Listener>()
    private val backStacks = mutableMapOf<BackStackKey, BackStack<*>>()
    private val globalRoutes = mutableListOf<GlobalRoute>()
    val snapshot: List<GlobalRoute> get() = globalRoutes.toList()

    fun <T : Any> register(id: BackStackId, parentKey: BackStackKey?, start: T, otherStart: List<T>): BackStack<T> {
        val key = BackStackKey(id, parentKey)
        val existing = getExistingBackStack(key, start, otherStart)
        if (existing != null) return existing

        val backStack = BackStack(key, start, otherStart)
        backStacks[key] = backStack

        val backStackIndex = getLastIndexOf(parentKey)
        val globalRoute = getOrCreateGlobalRoute(backStackIndex)
        globalRoute.add(Route(key, 0, start))

        backStack.snapshot.drop(1).forEachIndexed { index, route ->
            globalRoutes.add(backStackIndex + index + 1, GlobalRoute(route))
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

    private fun getLastIndexOf(key: BackStackKey?): Int {
        if (key == null) return 0
        return globalRoutes.indexOfLast { it.contains(key) }
    }

    private fun getOrCreateGlobalRoute(index: Int): GlobalRoute {
        val existing = globalRoutes.getOrNull(index)
        if (existing != null) return existing

        val globalRoute = GlobalRoute()
        globalRoutes.add(index, globalRoute)
        return globalRoute
    }

    fun pop(): Boolean {
        if (globalRoutes.size <= 1) return false

        val globalRoute = globalRoutes.removeAt(globalRoutes.size - 1)
        globalRoute.keys.forEach {
            val shouldRemoveBackStack = backStacks[it]?.popFromBackStackHandler()?.not() ?: false
            if (shouldRemoveBackStack) {
                backStacks.remove(it)
            }
        }

        notifyListeners()
        return true
    }

    internal fun onPushed(routes: List<Route<*>>) {
        routes.forEach { globalRoutes.add(GlobalRoute(it)) }
        notifyListeners()
    }

    internal fun onRemoved(route: Route<*>) {
        val index = globalRoutes.indexOfLast { it.contains(route) }
        val globalRoute = globalRoutes[index]
        globalRoute.remove(route)
        if (globalRoute.isEmpty()) {
            globalRoutes.removeAt(index)
            notifyListeners()
            return
        }

        clearChildBackStacks(globalRoute, route)
        notifyListeners()
    }

    internal fun onReplaced(route: Route<*>, withRoutes: List<Route<*>>) {
        if (withRoutes.isEmpty()) {
            onRemoved(route)
            return
        }

        val index = globalRoutes.indexOfLast { it.contains(route) }
        val globalRoute = globalRoutes[index]
        globalRoute.remove(route)

        if (route != withRoutes.first()) {
            clearChildBackStacks(globalRoute, route)
        }

        globalRoute.add(withRoutes.first())
        withRoutes.drop(1).forEachIndexed { i, r ->
            globalRoutes.add(index + i + 1, GlobalRoute(r))
        }

        notifyListeners()
    }

    private fun clearChildBackStacks(globalRoute: GlobalRoute, route: Route<*>) {
        val keysToClear = globalRoute.keys.filter { it.parentKey == route.key }
        val backStacksToClear = backStacks.values
            .filter { backStack -> keysToClear.contains(backStack.key) || backStack.key.hasAnyInParents(keysToClear) }
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

    internal fun onIndexChanged(routes: List<Pair<Route<*>, Route<*>>>) {
        routes.forEach { route ->
            globalRoutes.first { it.contains(route.first) }.replace(route.first, route.second)
        }
        notifyListeners()
    }

    private fun MutableList<GlobalRoute>.removeAll(routes: List<Route<*>>) {
        routes.reversed().forEach { route ->
            val index = indexOfLast { it.contains(route) }

            val globalRoute = this[index]
            globalRoute.remove(route)

            if (globalRoute.isEmpty()) {
                removeAt(index)
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
        fun onBackStackChanged(snapshot: List<GlobalRoute>)
    }
}
