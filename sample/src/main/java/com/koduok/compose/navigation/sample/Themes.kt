package com.koduok.compose.navigation.sample

import androidx.compose.Composable
import androidx.ui.graphics.Color
import androidx.ui.material.MaterialTheme
import androidx.ui.material.lightColorPalette

val lightThemeColors
    get() = lightColorPalette(
        primary = Color.Black,
        onPrimary = Color.White
    )

@Composable
fun AppTheme(content: @Composable() () -> Unit) = MaterialTheme(colors = lightThemeColors, content = content)