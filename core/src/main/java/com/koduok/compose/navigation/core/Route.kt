@file:Suppress("EXPERIMENTAL_API_USAGE")

package com.koduok.compose.navigation.core

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.filterIsInstance
import kotlinx.coroutines.flow.map

data class Route<T : Any> internal constructor(val key: BackStackKey, val index: Int, val routeDescription: RouteDescription<T>) {
    private val results = MutableStateFlow(mapOf<String, Any?>())

    val value get() = routeDescription.value
    val data get() = routeDescription.value

    fun setResult(key: String, result: Any) {
        results.value = results.value + (key to result)
    }

    fun removeResult(key: String) {
        results.value = results.value.minus(key)
    }

    fun getResult(key: String): Any? = results.value[key]
    fun getResultFlow(key: String): Flow<Any?> = results.filter { it.containsKey(key) }.map { it[key] }
}

inline fun <reified T> Route<*>.getResult(key: String): T? = getResult(key) as? T
inline fun <reified T> Route<*>.getResultFlow(key: String): Flow<T> = getResultFlow(key).filterIsInstance()

data class RouteDescription<T : Any>(
    val value: T,
    val showRouteBelow: Boolean = false
) {
    fun showRouteBelow(drawPreviousRoute: Boolean = true) = copy(showRouteBelow = drawPreviousRoute)
}