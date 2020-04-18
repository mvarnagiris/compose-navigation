package com.koduok.compose.navigation.sample.examples

import androidx.compose.Composable
import androidx.ui.core.Modifier
import androidx.ui.foundation.Box
import androidx.ui.graphics.Color
import androidx.ui.layout.Column
import androidx.ui.material.Divider

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