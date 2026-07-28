package com.example.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val CapDarkColorScheme = darkColorScheme(
    primary = PurpleAccent,
    onPrimary = DeepPurpleContainer,
    primaryContainer = DeepPurpleContainer,
    onPrimaryContainer = Color(0xFFE8DEF8),
    secondary = CyanAccent,
    onSecondary = Color(0xFF00363A),
    tertiary = RoseContainer,
    onTertiary = RoseText,
    background = SlateDarkBackground,
    onBackground = TextPrimaryDark,
    surface = SlateDarkCard,
    onSurface = TextPrimaryDark,
    surfaceVariant = SlateDarkSurface,
    onSurfaceVariant = TextSecondaryDark,
    outline = SlateDarkBorder
)

private val CapLightColorScheme = lightColorScheme(
    primary = CyanAccent,
    onPrimary = Color.White,
    primaryContainer = Color(0xFFE0F2FE),
    onPrimaryContainer = Color(0xFF0369A1),
    secondary = RwandaGold,
    onSecondary = Color.Black,
    tertiary = RwandaGreen,
    background = LightBackground,
    onBackground = TextPrimaryLight,
    surface = LightSurface,
    onSurface = TextPrimaryLight,
    surfaceVariant = Color(0xFFF1F5F9),
    onSurfaceVariant = TextSecondaryLight,
    outline = LightBorder
)

@Composable
fun CapRwandaTheme(
    darkTheme: Boolean = true, // Default to dark video editing studio theme
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) CapDarkColorScheme else CapLightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = true,
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    CapRwandaTheme(darkTheme = darkTheme, content = content)
}
