package com.koduok.compose.navigation.sample.examples

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.BottomNavigation
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.koduok.compose.navigation.Router
import com.koduok.compose.navigation.core.BackStack
import com.koduok.compose.navigation.sample.examples.TabRoute.TabA
import com.koduok.compose.navigation.sample.examples.TabRoute.TabB
import com.koduok.compose.navigation.sample.examples.TabRoute.TabC
import java.io.Serializable

@Composable
fun BottomTabsScreen() {
    Router<TabRoute>("Tab", start = TabA) {
        Column {
            Box(modifier = Modifier.weight(1f)) {
                when (it.value) {
                    TabA -> SampleScreenRouter("A")
                    TabB -> SampleScreenRouter("B")
                    TabC -> SampleScreenRouter("C")
                }
            }

            BottomNavigation {
                TabButton(backStack = this@Router, tabRoute = TabA, currentTabRoute = it.value)
                TabButton(backStack = this@Router, tabRoute = TabB, currentTabRoute = it.value)
                TabButton(backStack = this@Router, tabRoute = TabC, currentTabRoute = it.value)
            }
        }
    }
}

@Composable
fun RowScope.TabButton(backStack: BackStack<TabRoute>, tabRoute: TabRoute, currentTabRoute: TabRoute) {
    val isSelected = currentTabRoute == tabRoute
    Box(
        modifier = Modifier
            .weight(1f)
            .background(Color.Gray.copy(alpha = if (isSelected) 1f else 0.3f))
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .clickable(onClick = { backStack.replace(tabRoute) }),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = tabRoute.toString(),
                style = MaterialTheme.typography.h5
            )
        }
    }
}

sealed class TabRoute : Serializable {
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