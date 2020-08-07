package com.koduok.compose.navigation.sample.examples

import androidx.compose.foundation.Box
import androidx.compose.foundation.Text
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.koduok.compose.navigation.BackStackAmbient
import com.koduok.compose.navigation.Router

data class SampleRoute(val value: Int) {
    override fun toString(): String = value.toString()
}

@Composable
fun SampleScreen(sampleRoute: SampleRoute, onNext: () -> Unit) {
    Box(modifier = Modifier.fillMaxSize()) {
        Text(
            modifier = Modifier.clickable(onClick = onNext),
            text = "${BackStackAmbient.current.key.id}${sampleRoute.value}",
            style = MaterialTheme.typography.h3
        )
    }
}

@Composable
fun SampleScreenRouter(routerName: String, otherStart: List<SampleRoute> = emptyList()) {
    Router(routerName, start = SampleRoute(0), otherStart = otherStart) {
        SampleScreen(it.data) {
            push(SampleRoute(it.data.value + 1))
        }
    }
}
