package com.koduok.compose.navigation.sample.examples

import androidx.compose.Composable
import androidx.ui.core.Text
import androidx.ui.foundation.Box
import androidx.ui.foundation.Clickable
import androidx.ui.graphics.Color
import androidx.ui.layout.Center
import androidx.ui.layout.Column
import androidx.ui.layout.RowScope
import androidx.ui.material.BottomNavigation
import androidx.ui.material.MaterialTheme
import com.koduok.compose.navigation.Router
import com.koduok.compose.navigation.core.BackStack
import com.koduok.compose.navigation.sample.examples.TabRoute.TabA
import com.koduok.compose.navigation.sample.examples.TabRoute.TabB
import com.koduok.compose.navigation.sample.examples.TabRoute.TabC

@Composable
fun BottomTabsScreen() {
    Router<TabRoute>("Tab", start = TabA) {
        Column {
            Box(modifier = LayoutFlexible(1f)) {
                when (it.data) {
                    TabA -> SampleScreenRouter("A")
                    TabB -> SampleScreenRouter("B")
                    TabC -> SampleScreenRouter("C")
                }
            }

            BottomNavigation {
                TabButton(backStack = this@Router, tabRoute = TabA, currentTabRoute = it.data)
                TabButton(backStack = this@Router, tabRoute = TabB, currentTabRoute = it.data)
                TabButton(backStack = this@Router, tabRoute = TabC, currentTabRoute = it.data)
            }
        }
    }
}

@Composable
fun RowScope.TabButton(backStack: BackStack<TabRoute>, tabRoute: TabRoute, currentTabRoute: TabRoute) {
    val isSelected = currentTabRoute == tabRoute
    Box(modifier = LayoutFlexible(1f), backgroundColor = Color.Gray.copy(alpha = if (isSelected) 1f else 0.3f)) {
        Clickable(onClick = { backStack.replace(tabRoute) }) {
            Center {
                Text(
                    text = tabRoute.toString(),
                    style = MaterialTheme.typography().h5
                )
            }
        }
    }
}

sealed class TabRoute {
    object TabA : TabRoute() {
        override fun toString(): String = "A"
    }

    object TabB : TabRoute() {
        override fun toString(): String = "B"
    }

    object TabC : TabRoute() {
        override fun toString(): String = "C"
    }
}