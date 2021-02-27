package com.koduok.compose.navigation.sample.examples

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.koduok.compose.navigation.LocalBackStack
import com.koduok.compose.navigation.Router
import java.io.Serializable

data class SampleRoute(val value: Int) : Serializable {
    override fun toString(): String = value.toString()
}

@Composable
fun SampleScreen(sampleRoute: SampleRoute, onNext: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .clickable(onClick = onNext), contentAlignment = Alignment.Center
    ) {
        Text(
            text = "${LocalBackStack.current.key.id}${sampleRoute.value}",
            style = MaterialTheme.typography.h3
        )
    }
}

@Composable
fun SampleScreenRouter(routerName: String, otherStart: List<SampleRoute> = emptyList()) {
    Router(routerName, start = SampleRoute(0), otherStart = otherStart) {
        SampleScreen(it.value) {
            push(SampleRoute(it.value.value + 1))
        }
    }
}
