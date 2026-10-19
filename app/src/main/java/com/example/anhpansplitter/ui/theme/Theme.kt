package com.example.anhpansplitter.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color



private val DarkColorScheme = darkColorScheme(
    primary = BrandBlue,
    onPrimary = Color.White,
    secondary = BrandBeige,
    onSecondary = BrandDark,
    background = DarkBg,
    onBackground = Color(0xFFE5E7EB),
    surface = DarkSurface,
    onSurface = Color(0xFFE5E7EB),
    error = BrandRed,
    outline = DarkBorder
)



private val LightColorScheme = lightColorScheme(
    primary = BrandBlue,
    onPrimary = Color.White,
    secondary = BrandDark,
    onSecondary = Color.White,
    background = BrandLightBg,
    onBackground = BrandDark,
    surface = Color.White,
    onSurface = BrandDark,
    error = BrandRed,
    outline = LightBorder
)



@Composable
fun AnhpanSplitterTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}