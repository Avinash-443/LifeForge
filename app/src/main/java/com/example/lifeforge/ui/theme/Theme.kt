package com.example.lifeforge.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val LifeForgeColorScheme = darkColorScheme(
    primary = PrimaryAccent,
    onPrimary = PrimaryText,
    secondary = SecondaryAccent,
    onSecondary = PrimaryText,
    background = BackgroundBlack,
    onBackground = PrimaryText,
    surface = CardBackground,
    onSurface = PrimaryText,
    surfaceVariant = CardBackground,
    onSurfaceVariant = SecondaryText,
    tertiary = SuccessGreen,
    error = DangerRed,
    outline = SecondaryText.copy(alpha = 0.4f)
)

object LifeForgeColors {
    val background = BackgroundBlack
    val card = CardBackground
    val primary = PrimaryAccent
    val secondary = SecondaryAccent
    val success = SuccessGreen
    val warning = WarningOrange
    val danger = DangerRed
    val textPrimary = PrimaryText
    val textSecondary = SecondaryText
}

@Composable
fun LifeForgeTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = LifeForgeColorScheme,
        typography = Typography,
        content = content
    )
}
