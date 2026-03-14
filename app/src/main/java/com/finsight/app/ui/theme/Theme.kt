package com.finsight.app.ui.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val LightColorScheme = lightColorScheme(
    primary          = Teal500,
    onPrimary        = White,
    primaryContainer = Teal50,
    onPrimaryContainer = Teal900,
    secondary        = Teal300,
    onSecondary      = White,
    background       = Gray50,
    onBackground     = Gray900,
    surface          = White,
    onSurface        = Gray900,
    surfaceVariant   = Gray100,
    onSurfaceVariant = Gray700,
    outline          = Gray400,
    error            = Red500,
    onError          = White,
)

private val DarkColorScheme = darkColorScheme(
    primary          = Teal300,
    onPrimary        = Teal900,
    primaryContainer = Teal800,
    onPrimaryContainer = Teal50,
    secondary        = Teal500,
    onSecondary      = White,
    background       = Gray900,
    onBackground     = Gray50,
    surface          = Color(0xFF1E1E1E),
    onSurface        = Gray100,
    surfaceVariant   = Color(0xFF2A2A2A),
    onSurfaceVariant = Gray400,
    outline          = Gray700,
    error            = Red500,
    onError          = White,
)

@Composable
fun FinsightTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme
    val view = LocalView.current

    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = colorScheme.primary.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !darkTheme
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
