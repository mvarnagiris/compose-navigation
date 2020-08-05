package com.koduok.compose.navigation.sample.examples

import androidx.compose.Composable
import androidx.ui.core.Modifier
import androidx.ui.foundation.Box
import androidx.ui.foundation.Text
import androidx.ui.foundation.clickable
import androidx.ui.layout.fillMaxSize
import androidx.ui.material.MaterialTheme
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
