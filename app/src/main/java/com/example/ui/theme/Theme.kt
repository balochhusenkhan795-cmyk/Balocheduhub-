package com.example.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkColorScheme = darkColorScheme(
    primary = RoyalIndigoLight,
    onPrimary = Color.White,
    secondary = GoldenAmberAccent,
    onSecondary = Color.Black,
    tertiary = CorrectEmerald,
    background = DeepObsidianBg,
    onBackground = SlateTextPrimary,
    surface = PremiumCardSurface,
    onSurface = SlateTextPrimary,
    surfaceVariant = PremiumBorderSecondary,
    onSurfaceVariant = SlateTextSecondary,
    error = IncorrectCrimson,
    onError = Color.White
)

private val LightColorScheme = lightColorScheme(
    primary = LightIndigoPrimary,
    onPrimary = Color.White,
    secondary = LightAmberAccent,
    onSecondary = Color.White,
    tertiary = CorrectEmerald,
    background = WarmSlateBg,
    onBackground = LightTextPrimary,
    surface = LightCardSurface,
    onSurface = LightTextPrimary,
    surfaceVariant = LightBorderSecondary,
    onSurfaceVariant = LightTextSecondary,
    error = IncorrectCrimson,
    onError = Color.White
)

@Composable
fun MyApplicationTheme(
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
