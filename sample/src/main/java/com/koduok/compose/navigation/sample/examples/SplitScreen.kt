package com.koduok.compose.navigation.sample.examples

import androidx.compose.foundation.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.material.Divider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color

@Composable
fun SplitScreen() {
    Column {
        Box(modifier = Modifier.weight(1f)) {
            SampleScreenRouter("A")
        }
        Divider(color = Color.Black)
        Box(modifier = Modifier.weight(1f)) {
            SampleScreenRouter("B")
        }
    }

}