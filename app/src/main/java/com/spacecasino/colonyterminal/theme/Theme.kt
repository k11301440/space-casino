package com.spacecasino.colonyterminal.theme

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import com.spacecasino.colonyterminal.model.ThemeMode

val LocalTerminalColors = staticCompositionLocalOf { CrtGreenPalette }

@Composable
fun ColonyAssetTerminalTheme(
    themeMode: ThemeMode = ThemeMode.CRT_GREEN,
    content: @Composable () -> Unit
) {
    val palette = getTerminalPalette(themeMode)

    val colorScheme = darkColorScheme(
        primary = palette.primary,
        secondary = palette.secondary,
        background = palette.background,
        surface = palette.surface,
        surfaceVariant = palette.surfaceVariant,
        onPrimary = palette.background,
        onSecondary = palette.background,
        onBackground = palette.textPrimary,
        onSurface = palette.textPrimary,
        outline = palette.border
    )

    CompositionLocalProvider(
        LocalTerminalColors provides palette
    ) {
        MaterialTheme(
            colorScheme = colorScheme,
            typography = TerminalTypography,
            content = content
        )
    }
}

// CRT スキャンラインオーバーレイ (ノイズの少ない控えめな走査線)
@Composable
fun CrtScanlineOverlay(
    modifier: Modifier = Modifier,
    alpha: Float = 0.05f
) {
    Canvas(modifier = modifier) {
        val step = 4f
        var y = 0f
        while (y < size.height) {
            drawLine(
                color = Color.Black.copy(alpha = alpha),
                start = Offset(0f, y),
                end = Offset(size.width, y),
                strokeWidth = 1.5f
            )
            y += step
        }
    }
}
