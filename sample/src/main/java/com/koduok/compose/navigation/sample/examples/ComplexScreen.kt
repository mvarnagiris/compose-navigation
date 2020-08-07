package com.koduok.compose.navigation.sample.examples

import androidx.compose.foundation.Box
import androidx.compose.foundation.Text
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.height
import androidx.compose.material.Divider
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.koduok.compose.navigation.Router
import com.koduok.compose.navigation.sample.examples.ComplexRoute.HomeRoute
import com.koduok.compose.navigation.sample.examples.ComplexRoute.SimpleRoute
import com.koduok.compose.navigation.sample.examples.ComplexRoute.SplitRoute

@Composable
fun ComplexScreen() {
    Router<ComplexRoute>("D", HomeRoute) {
        when (val route = it.data) {
            HomeRoute -> SampleScreen(sampleRoute = SampleRoute(0), onNext = { push(SplitRoute) })
            SplitRoute -> {
                Column {
                    Box(modifier = Modifier.height(72.dp)) {
                        Text(modifier = Modifier.clickable(onClick = { push(SimpleRoute(2)) }), text = "D1", style = MaterialTheme.typography.h3)
                    }
                    Divider(color = Color.Black)
                    Box(modifier = Modifier.weight(1f)) {
                        BottomTabsScreen()
                    }
                    Divider(color = Color.Black)
                    Box(modifier = Modifier.weight(1f)) {
                        SampleScreenRouter("E")
                    }
                }
            }
            is SimpleRoute -> SampleScreen(sampleRoute = SampleRoute(route.value), onNext = { push(SimpleRoute(route.value + 1)) })
        }
    }
}

sealed class ComplexRoute {
    object HomeRoute : ComplexRoute() {
        override fun toString(): String = "0"
    }

    object SplitRoute : ComplexRoute() {
        override fun toString(): String = "1"
    }

    data class SimpleRoute(val value: Int) : ComplexRoute() {
        override fun toString(): String = value.toString()
    }
}