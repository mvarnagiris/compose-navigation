package com.koduok.compose.navigation.sample.examples

import androidx.compose.foundation.Text
import androidx.compose.foundation.layout.Column
import androidx.compose.material.Button
import androidx.compose.material.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.koduok.compose.navigation.AmbientBackStack
import com.koduok.compose.navigation.Router
import com.koduok.compose.navigation.core.getResultFlow
import com.koduok.compose.navigation.sample.examples.ResultRoute.ResultProviderRoute
import com.koduok.compose.navigation.sample.examples.ResultRoute.ResultScreenRoute

@Composable
fun ResultExampleScreen() {
    Router<ResultRoute>(start = ResultScreenRoute) {
        when (it.data) {
            ResultScreenRoute -> ResultScreen()
            ResultProviderRoute -> ResultProviderScreen()
        }
    }
}

@Composable
private fun ResultScreen() {
    val backStack = AmbientBackStack.current
    val result by backStack.current.getResultFlow<String>("result").collectAsState(initial = "")
    Column {
        Text("Result:")
        Text(result)
        Button(onClick = { backStack.push(ResultProviderRoute) }) { Text("Request result") }
    }
}

@Composable
private fun ResultProviderScreen() {
    val backStack = AmbientBackStack.current
    Column {
        var text by remember { mutableStateOf("") }
        TextField(value = text, onValueChange = { text = it })
        Button(onClick = {
            backStack.previous.setResult("result", text)
            backStack.pop()
        }) {
            Text(text = "Set result")
        }
    }
}

private sealed class ResultRoute {
    object ResultScreenRoute : ResultRoute()
    object ResultProviderRoute : ResultRoute()
}
