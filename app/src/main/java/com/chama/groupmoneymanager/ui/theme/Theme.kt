package com.chama.groupmoneymanager.ui.theme

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

// ─────────────────────────────────────────────────────────────────────────
// Full Material 3 ColorScheme mapping — DESIGN_SYSTEM.md §3.5. Every role
// is mapped explicitly (not left to M3 defaults) so the brand palette is
// consistent everywhere, in both light and dark.
// ─────────────────────────────────────────────────────────────────────────
private val MfukoLightColorScheme = lightColorScheme(
    primary                  = Green700,
    onPrimary                = Color.White,
    primaryContainer         = Green100,
    onPrimaryContainer       = Green900,
    secondary                = Gold600,
    onSecondary              = Color.White,
    secondaryContainer       = Gold100,
    onSecondaryContainer     = Gold900,
    tertiary                 = Clay500,
    onTertiary               = Color.White,
    tertiaryContainer        = Clay100,
    onTertiaryContainer      = Clay900,
    background               = NeutralSurfaceLight,
    onBackground             = NeutralOnSurfaceLight,
    surface                  = NeutralSurfaceLight,
    onSurface                = NeutralOnSurfaceLight,
    surfaceVariant           = NeutralSurfaceVariantLight,
    onSurfaceVariant         = NeutralOnSurfaceVariantLight,
    surfaceContainerLowest   = NeutralSurfaceContainerLowestLight,
    surfaceContainerLow      = NeutralSurfaceContainerLowLight,
    surfaceContainer         = NeutralSurfaceContainerLight,
    surfaceContainerHigh     = NeutralSurfaceContainerHighLight,
    surfaceContainerHighest  = NeutralSurfaceContainerHighestLight,
    outline                  = NeutralOutlineLight,
    outlineVariant           = NeutralOutlineVariantLight,
    inverseSurface           = NeutralInverseSurfaceLight,
    inverseOnSurface         = NeutralInverseOnSurfaceLight,
    inversePrimary           = Green300,
    error                    = ErrorLight,
    onError                  = OnErrorLight,
    errorContainer           = ErrorContainerLight,
    onErrorContainer         = OnErrorContainerLight,
    scrim                    = Color.Black,
)

private val MfukoDarkColorScheme = darkColorScheme(
    primary                  = Green300,
    onPrimary                = Green900,
    primaryContainer         = Green800,
    onPrimaryContainer       = Green100,
    secondary                = Gold300,
    onSecondary              = Gold900,
    secondaryContainer       = Gold700,
    onSecondaryContainer     = Gold100,
    tertiary                 = Clay200,
    onTertiary               = Clay900,
    tertiaryContainer        = Clay700,
    onTertiaryContainer      = Clay100,
    background               = NeutralSurfaceDark,
    onBackground             = NeutralOnSurfaceDark,
    surface                  = NeutralSurfaceDark,
    onSurface                = NeutralOnSurfaceDark,
    surfaceVariant           = NeutralSurfaceVariantDark,
    onSurfaceVariant         = NeutralOnSurfaceVariantDark,
    surfaceContainerLowest   = NeutralSurfaceContainerLowestDark,
    surfaceContainerLow      = NeutralSurfaceContainerLowDark,
    surfaceContainer         = NeutralSurfaceContainerDark,
    surfaceContainerHigh     = NeutralSurfaceContainerHighDark,
    surfaceContainerHighest  = NeutralSurfaceContainerHighestDark,
    outline                  = NeutralOutlineDark,
    outlineVariant           = NeutralOutlineVariantDark,
    inverseSurface           = NeutralInverseSurfaceDark,
    inverseOnSurface         = NeutralInverseOnSurfaceDark,
    inversePrimary           = Green700,
    error                    = ErrorDark,
    onError                  = OnErrorDark,
    errorContainer           = ErrorContainerDark,
    onErrorContainer         = OnErrorContainerDark,
    scrim                    = Color.Black,
)

/**
 * MfukoTheme — the single Compose theme entry point for the whole app.
 *
 * Dynamic colour is intentionally OFF so the brand palette is always applied
 * consistently regardless of the user's wallpaper.
 */
@Composable
fun MfukoTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) MfukoDarkColorScheme else MfukoLightColorScheme

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = colorScheme.surface.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !darkTheme
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography  = Typography,
        shapes      = MfukoShapes,
        content     = content
    )
}
