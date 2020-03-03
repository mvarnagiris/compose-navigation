package com.koduok.compose.navigation.sample.examples

import androidx.compose.Composable

@Composable
fun MultipleStartScreen() {
    SampleScreenRouter(
        "A",
        listOf(
            SampleRoute(10),
            SampleRoute(20)
        )
    )
}
