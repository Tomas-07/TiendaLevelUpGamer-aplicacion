package com.levelup.gamer.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val Colors = darkColorScheme(
    primary = Color(0xFF39FF14),
    secondary = Color(0xFF1E90FF),
    background = Color(0xFF0B0D10),
    surface = Color(0xFF0F141A),
    onPrimary = Color(0xFF0B0D10),
    onBackground = Color(0xFFFFFFFF),
    onSurface = Color(0xFFFFFFFF)
)

@Composable
fun LevelUpTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = Colors,
        typography = androidx.compose.material3.Typography(),
        content = content
    )
}