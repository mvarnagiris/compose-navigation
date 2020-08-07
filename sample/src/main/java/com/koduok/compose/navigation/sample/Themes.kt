package com.koduok.compose.navigation.sample

import androidx.compose.material.MaterialTheme
import androidx.compose.material.lightColors
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color


val lightThemeColors
    get() = lightColors(
        primary = Color.Black,
        onPrimary = Color.White
    )

@Composable
fun AppTheme(content: @Composable () -> Unit) = MaterialTheme(colors = lightThemeColors, content = content)