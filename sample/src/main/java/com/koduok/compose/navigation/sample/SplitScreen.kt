package com.koduok.compose.navigation.sample

import androidx.compose.Composable
import androidx.ui.foundation.Box
import androidx.ui.graphics.Color
import androidx.ui.layout.Column
import androidx.ui.material.Divider

@Composable
fun SplitScreen() {
    Column {
        Box(modifier = LayoutFlexible(1f)) {
            SampleScreenRouter("A")
        }
        Divider(color = Color.Black)
        Box(modifier = LayoutFlexible(1f)) {
            SampleScreenRouter("B")
        }
    }

}