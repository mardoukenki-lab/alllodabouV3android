package com.example.ui.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val LightColorScheme = lightColorScheme(
    primary = GreenPrimary,
    onPrimary = androidx.compose.ui.graphics.Color.White,
    primaryContainer = GreenContainer,
    onPrimaryContainer = GreenDark,
    secondary = GreenDark,
    onSecondary = androidx.compose.ui.graphics.Color.White,
    secondaryContainer = GreenBorder,
    onSecondaryContainer = GreenDark,
    background = BgCanvas,
    onBackground = InkDark,
    surface = BgCard,
    onSurface = InkDark,
    surfaceVariant = BgInput,
    onSurfaceVariant = InkMuted,
    outline = BorderInput,
    outlineVariant = BorderLight,
    error = AccentRed,
    onError = androidx.compose.ui.graphics.Color.White,
    errorContainer = RedContainer,
    onErrorContainer = AccentRed
)

private val DarkColorScheme = darkColorScheme(
    primary = GreenLight,
    onPrimary = androidx.compose.ui.graphics.Color.White,
    primaryContainer = DarkGreenContainer,
    onPrimaryContainer = GreenContainer,
    secondary = GreenLight,
    onSecondary = androidx.compose.ui.graphics.Color.White,
    secondaryContainer = DarkBorder,
    onSecondaryContainer = GreenContainer,
    background = DarkBgCanvas,
    onBackground = DarkInkText,
    surface = DarkBgCard,
    onSurface = DarkInkText,
    surfaceVariant = DarkBorder,
    onSurfaceVariant = DarkInkMuted,
    outline = DarkBorder,
    outlineVariant = DarkBorder,
    error = AccentRed,
    onError = androidx.compose.ui.graphics.Color.White,
    errorContainer = RedContainer,
    onErrorContainer = AccentRed
)

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme
    val view = LocalView.current

    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as? Activity)?.window
            if (window != null) {
                window.statusBarColor = GreenPrimary.toArgb()
                val windowInsetsController = WindowCompat.getInsetsController(window, view)
                // White icons on dark emerald status bar
                windowInsetsController.isAppearanceLightStatusBars = false
            }
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
