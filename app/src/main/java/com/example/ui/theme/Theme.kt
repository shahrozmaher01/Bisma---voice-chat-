package com.example.ui.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.unit.Density
import androidx.core.view.WindowCompat

private val DarkColorScheme = darkColorScheme(
    primary = NeonPink,
    onPrimary = TextPrimary,
    primaryContainer = NeonViolet,
    onPrimaryContainer = TextPrimary,
    secondary = ElectricBlue,
    onSecondary = BackgroundDark,
    secondaryContainer = SurfaceCard,
    onSecondaryContainer = ElectricBlue,
    tertiary = GoldYellow,
    onTertiary = BackgroundDark,
    background = BackgroundDark,
    onBackground = TextPrimary,
    surface = SurfaceDark,
    onSurface = TextPrimary,
    surfaceVariant = SurfaceCard,
    onSurfaceVariant = TextSecondary,
    outline = SurfaceCardBorder,
    error = DarkRed,
    onError = TextPrimary
)

private val LightColorScheme = DarkColorScheme // Preserving vibrant dark neon aesthetic for social voice chat

@Composable
fun BismaTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false, // Set false to maintain the custom neon branding
    content: @Composable () -> Unit
) {
    val colorScheme = DarkColorScheme
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = BackgroundDark.toArgb()
            window.navigationBarColor = BackgroundDark.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = false
            WindowCompat.getInsetsController(window, view).isAppearanceLightNavigationBars = false
        }
    }

    val currentDensity = LocalDensity.current
    // Lock fontScale to 1.0f so installed APKs and previews match 1:1 regardless of device font scaling settings
    val responsiveDensity = Density(
        density = currentDensity.density,
        fontScale = 1.0f
    )

    CompositionLocalProvider(
        LocalDensity provides responsiveDensity
    ) {
        MaterialTheme(
            colorScheme = colorScheme,
            typography = Typography,
            content = content
        )
    }
}

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    BismaTheme(darkTheme = darkTheme, dynamicColor = dynamicColor, content = content)
}
