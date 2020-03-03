package com.koduok.compose.navigation.sample.examples

import androidx.compose.Composable

@Composable
fun MultiStartLinearScreen() {
    SampleScreenRouter(
        "A",
        listOf(
            SampleRoute(10),
            SampleRoute(20)
        )
    )
}
