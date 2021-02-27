package com.koduok.compose.navigation.sample

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.koduok.compose.navigation.Router
import com.koduok.compose.navigation.core.BackStackController
import com.koduok.compose.navigation.core.Route
import com.koduok.compose.navigation.core.backStackController
import com.koduok.compose.navigation.sample.AppRoute.BottomTabsRoute
import com.koduok.compose.navigation.sample.AppRoute.ComplexRoute
import com.koduok.compose.navigation.sample.AppRoute.HomeRoute
import com.koduok.compose.navigation.sample.AppRoute.MultipleStartRoute
import com.koduok.compose.navigation.sample.AppRoute.SimpleRoute
import com.koduok.compose.navigation.sample.AppRoute.SplitScreenRoute
import com.koduok.compose.navigation.sample.examples.BottomTabsScreen
import com.koduok.compose.navigation.sample.examples.ComplexScreen
import com.koduok.compose.navigation.sample.examples.MultipleStartScreen
import com.koduok.compose.navigation.sample.examples.SimpleScreen
import com.koduok.compose.navigation.sample.examples.SplitScreen

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            AppTheme {
                SampleApp()
            }
        }
    }
}

@Composable
fun SampleApp() {
    Column {
        BackStackVisualizer()
        Router<AppRoute>("Root", HomeRoute) {
            when (it.value) {
                HomeRoute -> HomeScreen { appRoute -> push(appRoute) }
                SimpleRoute -> SimpleScreen()
                MultipleStartRoute -> MultipleStartScreen()
                BottomTabsRoute -> BottomTabsScreen()
                SplitScreenRoute -> SplitScreen()
                ComplexRoute -> ComplexScreen()
            }
        }
    }
}

@Composable
fun BackStackVisualizer() {
    val globalRoutes = remember { mutableStateOf(backStackController.snapshot) }
    DisposableEffect(Unit) {
        val listener = object : BackStackController.Listener {
            override fun onBackStackChanged(snapshot: List<Route<*>>) {
                globalRoutes.value = snapshot
            }
        }
        backStackController.addListener(listener)
        onDispose {
            backStackController.removeListener(listener)
        }
    }

    Box(modifier = Modifier.height(144.dp)) {
        Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colors.primary) {
            Column(modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState()), content = {
                Row(modifier = Modifier
                    .fillMaxSize()
                    .horizontalScroll(rememberScrollState()), content = {
                    Text(
                        modifier = Modifier.fillMaxSize(),
                        text = buildBackStackVisual(globalRoutes.value), style = TextStyle(fontFamily = FontFamily.Monospace, fontSize = 12.sp)
                    )
                })

            })

        }
    }
}

private fun buildBackStackVisual(globalRoutes: List<Route<*>>): String {
    return globalRoutes
        .filter { it.key.id != "Root" }
        .reversed()
        .joinToString(separator = "\n", transform = { it.name })
}

private val Route<*>.name: String get() = "${this.key.id}${this.value}"

@Preview
@Composable
fun DefaultPreview() {
    AppTheme {
        SampleApp()
    }
}