package com.koduok.compose.navigation.sample

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.Composable
import androidx.compose.onActive
import androidx.compose.state
import androidx.ui.core.Text
import androidx.ui.core.setContent
import androidx.ui.foundation.AdapterList
import androidx.ui.foundation.Box
import androidx.ui.foundation.Clickable
import androidx.ui.foundation.HorizontalScroller
import androidx.ui.foundation.VerticalScroller
import androidx.ui.layout.Column
import androidx.ui.layout.LayoutHeight
import androidx.ui.layout.LayoutPadding
import androidx.ui.layout.LayoutSize.Fill
import androidx.ui.layout.LayoutWidth
import androidx.ui.material.MaterialTheme
import androidx.ui.material.surface.Card
import androidx.ui.material.surface.Surface
import androidx.ui.text.TextStyle
import androidx.ui.text.font.FontFamily
import androidx.ui.tooling.preview.Preview
import androidx.ui.unit.dp
import androidx.ui.unit.sp
import com.koduok.compose.navigation.Router
import com.koduok.compose.navigation.core.BackStackController
import com.koduok.compose.navigation.core.GlobalRoute
import com.koduok.compose.navigation.core.Route
import com.koduok.compose.navigation.core.backStackController
import com.koduok.compose.navigation.sample.AppRoute.BottomTabsRoute
import com.koduok.compose.navigation.sample.AppRoute.HomeRoute
import com.koduok.compose.navigation.sample.AppRoute.LinearRoute
import com.koduok.compose.navigation.sample.AppRoute.SplitScreenRoute

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            AppTheme {
                SampleApp()
            }
        }
    }

    override fun onBackPressed() {
        if (!backStackController.pop()) super.onBackPressed()
    }
}

@Composable
fun AppTheme(children: @Composable() () -> Unit) = MaterialTheme(colors = lightThemeColors, children = children)

@Composable
fun SampleApp() {
    var globalRoutes by state { backStackController.snapshot }
    onActive {
        val listener = object : BackStackController.Listener {
            override fun onBackStackChanged(snapshot: List<GlobalRoute>) {
                globalRoutes = snapshot
            }
        }
        backStackController.addListener(listener)
        onDispose {
            backStackController.removeListener(listener)
        }
    }

    Column {
        Box(modifier = LayoutHeight(144.dp)) {
            Surface(modifier = Fill, color = MaterialTheme.colors().primary) {
                VerticalScroller(modifier = Fill) {
                    HorizontalScroller(modifier = Fill) {
                        Text(
                            modifier = Fill,
                            text = buildNavigationLog(globalRoutes), style = TextStyle(fontFamily = FontFamily.Monospace, fontSize = 12.sp)
                        )
                    }

                }

            }
        }
        Router<AppRoute>("Root|", HomeRoute) {
            when (it.data) {
                HomeRoute -> HomeScreen { appRoute -> push(appRoute) }
                LinearRoute -> LinearScreen()
                BottomTabsRoute -> BottomTabsScreen()
                SplitScreenRoute -> SplitScreen()
            }
        }
    }
}

private fun buildNavigationLog(globalRoutes: List<GlobalRoute>): String {
    return globalRoutes
        .map { globalRoute ->
            globalRoute.snapshot.filter { it.key.id != "Root|" }.sortedBy { it.key.parentCount }.map { it.name }
        }
        .reversed()
        .filter { it.isNotEmpty() }
        .joinToString(separator = "\n")
}

private val Route<*>.name: String get() = "${this.key.id}${this.data}"

sealed class AppRoute {
    object HomeRoute : AppRoute() {
        override fun toString(): String = "Home"
    }

    object LinearRoute : AppRoute() {
        override fun toString(): String = "Linear"
    }

    object BottomTabsRoute : AppRoute() {
        override fun toString(): String = "Bottom tabs"
    }

    object SplitScreenRoute : AppRoute() {
        override fun toString(): String = "Split screen"
    }
}

@Composable
fun HomeScreen(onShowSample: (AppRoute) -> Unit) {
    val data = listOf(
        LinearRoute,
        BottomTabsRoute,
        SplitScreenRoute
    )

    AdapterList(data = data) {
        Clickable(onClick = { onShowSample(it) }) {
            Card(modifier = LayoutWidth.Fill + LayoutPadding(8.dp)) {
                Text(text = it.toString(), modifier = LayoutPadding(16.dp), style = MaterialTheme.typography().h5)
            }
        }
    }
}

@Preview
@Composable
fun DefaultPreview() {
    AppTheme {
        SampleApp()
    }
}