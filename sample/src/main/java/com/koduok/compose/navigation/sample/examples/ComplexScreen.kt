package com.koduok.compose.navigation.sample.examples

import androidx.compose.Composable
import androidx.ui.core.Modifier
import androidx.ui.foundation.Box
import androidx.ui.foundation.Text
import androidx.ui.foundation.clickable
import androidx.ui.graphics.Color
import androidx.ui.layout.Column
import androidx.ui.layout.height
import androidx.ui.material.Divider
import androidx.ui.material.MaterialTheme
import androidx.ui.unit.dp
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