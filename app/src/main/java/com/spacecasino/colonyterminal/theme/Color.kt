package com.spacecasino.colonyterminal.theme

import androidx.compose.ui.graphics.Color
import com.spacecasino.colonyterminal.model.ThemeMode

data class TerminalColors(
    val background: Color,
    val surface: Color,
    val surfaceVariant: Color,
    val primary: Color,
    val secondary: Color,
    val textPrimary: Color,
    val textSecondary: Color,
    val border: Color,
    val positive: Color,
    val negative: Color,
    val hazard: Color
)

val CrtGreenPalette = TerminalColors(
    background = Color(0xFF0C130C),
    surface = Color(0xFF121E12),
    surfaceVariant = Color(0xFF1B2E1B),
    primary = Color(0xFF39FF5A),
    secondary = Color(0xFF8BFF9F),
    textPrimary = Color(0xFF39FF5A),
    textSecondary = Color(0xFF209E38),
    border = Color(0xFF2A502A),
    positive = Color(0xFF39FF5A),
    negative = Color(0xFFFF5252),
    hazard = Color(0xFFFFD600)
)

val CrtAmberPalette = TerminalColors(
    background = Color(0xFF140F08),
    surface = Color(0xFF22190E),
    surfaceVariant = Color(0xFF332514),
    primary = Color(0xFFFFB300),
    secondary = Color(0xFFFFCC80),
    textPrimary = Color(0xFFFFB300),
    textSecondary = Color(0xFFB27B00),
    border = Color(0xFF4D3818),
    positive = Color(0xFFFFB300),
    negative = Color(0xFFFF5252),
    hazard = Color(0xFFFF3D00)
)

val FlatGreyPalette = TerminalColors(
    background = Color(0xFF18191B),
    surface = Color(0xFF222428),
    surfaceVariant = Color(0xFF2D3036),
    primary = Color(0xFFE2E4E9),
    secondary = Color(0xFF64B5F6),
    textPrimary = Color(0xFFF0F2F5),
    textSecondary = Color(0xFF90949D),
    border = Color(0xFF3B3E46),
    positive = Color(0xFF66BB6A),
    negative = Color(0xFFEF5350),
    hazard = Color(0xFFFFCA28)
)

val CyberCyanPalette = TerminalColors(
    background = Color(0xFF081016),
    surface = Color(0xFF10202C),
    surfaceVariant = Color(0xFF183244),
    primary = Color(0xFF00E5FF),
    secondary = Color(0xFFFF4081),
    textPrimary = Color(0xFF00E5FF),
    textSecondary = Color(0xFF0097A7),
    border = Color(0xFF1C4961),
    positive = Color(0xFF00E5FF),
    negative = Color(0xFFFF1744),
    hazard = Color(0xFFFFAB00)
)

fun getTerminalPalette(mode: ThemeMode): TerminalColors {
    return when (mode) {
        ThemeMode.CRT_GREEN -> CrtGreenPalette
        ThemeMode.CRT_AMBER -> CrtAmberPalette
        ThemeMode.FLAT_GREY -> FlatGreyPalette
        ThemeMode.CYBER_CYAN -> CyberCyanPalette
    }
}
