package com.koduok.compose.navigation.sample

import android.text.TextUtils.replace
import androidx.compose.Composable
import androidx.ui.core.Text
import androidx.ui.foundation.Box
import androidx.ui.foundation.Clickable
import androidx.ui.foundation.DrawBackground
import androidx.ui.graphics.Color
import androidx.ui.layout.Center
import androidx.ui.layout.Column
import androidx.ui.layout.LayoutHeight
import androidx.ui.layout.Row
import androidx.ui.layout.RowScope
import androidx.ui.material.MaterialTheme
import androidx.ui.text.font.FontWeight
import androidx.ui.text.font.FontWeight.Companion
import androidx.ui.unit.dp
import com.koduok.compose.navigation.Router
import com.koduok.compose.navigation.core.BackStack
import com.koduok.compose.navigation.sample.TabRoute.TabA
import com.koduok.compose.navigation.sample.TabRoute.TabB
import com.koduok.compose.navigation.sample.TabRoute.TabC

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

            Row(modifier = LayoutHeight(72.dp) + DrawBackground(color = Color.Gray)) {
                TabButton(backStack = this@Router, tabRoute = TabA, currentTabRoute = it.data)
                TabButton(backStack = this@Router, tabRoute = TabB, currentTabRoute = it.data)
                TabButton(backStack = this@Router, tabRoute = TabC, currentTabRoute = it.data)
            }
        }
    }
}

@Composable
fun RowScope.TabButton(backStack: BackStack<TabRoute>, tabRoute: TabRoute, currentTabRoute: TabRoute) {
    Box(modifier = LayoutFlexible(1f)) {
        Clickable(onClick = { backStack.replace(tabRoute) }) {
            Center {
                Text(
                    text = tabRoute.toString(),
                    style = MaterialTheme.typography().h5.copy(fontWeight = if (currentTabRoute == tabRoute) FontWeight.Bold else Companion.Normal)
                )
            }
        }
    }
}

sealed class TabRoute {
    object TabA : TabRoute() {
        override fun toString(): String = "TabA"
    }

    object TabB : TabRoute() {
        override fun toString(): String = "TabB"
    }

    object TabC : TabRoute() {
        override fun toString(): String = "TabC"
    }
}