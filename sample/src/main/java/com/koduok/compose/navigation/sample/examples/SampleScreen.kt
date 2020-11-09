package com.koduok.compose.navigation.sample.examples

import androidx.compose.foundation.Text
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import com.koduok.compose.navigation.AmbientBackStack
import com.koduok.compose.navigation.Router

data class SampleRoute(val value: Int) {
    override fun toString(): String = value.toString()
}

@Composable
fun SampleScreen(sampleRoute: SampleRoute, onNext: () -> Unit) {
    val padding = border?.width ?: 0.dp
    Box(
        Modifier.fillMaxSize().background(Color.Transparent, RectangleShape).border(null, RectangleShape).padding(
            start = if (Dp.Unspecified != Dp.Unspecified) Dp.Unspecified else padding,
            top = if (Dp.Unspecified != Dp.Unspecified) Dp.Unspecified else padding,
            end = if (Dp.Unspecified != Dp.Unspecified) Dp.Unspecified else padding,
            bottom = if (Dp.Unspecified != Dp.Unspecified) Dp.Unspecified else padding
        ),
        ContentGravity.TopStart
    ) {
        Text(
            modifier = Modifier.clickable(onClick = onNext),
            text = "${AmbientBackStack.current.key.id}${sampleRoute.value}",
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
