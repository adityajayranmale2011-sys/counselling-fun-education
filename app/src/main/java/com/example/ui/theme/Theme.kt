package com.example.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

enum class AppThemeSkin(val label: String, val subtitle: String) {
    MIDNIGHT_GOLD("Midnight Gold (Dark)", "Luxury navy & golden luminescence"),
    DAYLIGHT_ACADEMIC("Daylight Academic (Light)", "Crisp ivory & royal blue clarity"),
    CYBER_SCHOLAR("Cyber Scholar (Skin)", "Bio-emerald & neon cyan focus"),
    ROYAL_AMETHYST("Royal Amethyst (Skin)", "Deep violet & rose scholarly aura")
}

private val MidnightGoldScheme = darkColorScheme(
    primary = AcademicGold,
    onPrimary = Color(0xFF1A1200),
    primaryContainer = Color(0xFF3B2B0D),
    onPrimaryContainer = AcademicGoldLight,
    secondary = ElectricCyan,
    onSecondary = Color(0xFF001F2A),
    secondaryContainer = Color(0xFF0F3448),
    onSecondaryContainer = Color(0xFFB8E8FF),
    tertiary = EmeraldRecharge,
    onTertiary = Color(0xFF002114),
    background = MidnightNavy,
    onBackground = Color(0xFFF1F5F9),
    surface = MidnightSurface,
    onSurface = Color(0xFFF1F5F9),
    surfaceVariant = MidnightCard,
    onSurfaceVariant = Color(0xFFCBD5E1),
    error = CoralAlert
)

private val DaylightAcademicScheme = lightColorScheme(
    primary = DaylightPrimary,
    onPrimary = Color.White,
    primaryContainer = Color(0xFFDBEAFE),
    onPrimaryContainer = Color(0xFF1E3A8A),
    secondary = DaylightSecondary,
    onSecondary = Color.White,
    secondaryContainer = Color(0xFFFEF3C7),
    onSecondaryContainer = Color(0xFF78350F),
    tertiary = DaylightTertiary,
    onTertiary = Color.White,
    background = DaylightBg,
    onBackground = Color(0xFF0F172A),
    surface = DaylightSurface,
    onSurface = Color(0xFF0F172A),
    surfaceVariant = DaylightCard,
    onSurfaceVariant = Color(0xFF334155),
    error = Color(0xFFDC2626)
)

private val CyberScholarScheme = darkColorScheme(
    primary = CyberPrimary,
    onPrimary = Color(0xFF002114),
    primaryContainer = Color(0xFF064E3B),
    onPrimaryContainer = Color(0xFFA7F3D0),
    secondary = CyberSecondary,
    onSecondary = Color(0xFF001F24),
    secondaryContainer = Color(0xFF164E63),
    onSecondaryContainer = Color(0xFFCFFAFE),
    tertiary = CyberTertiary,
    background = CyberBg,
    onBackground = Color(0xFFECFDF5),
    surface = CyberSurface,
    onSurface = Color(0xFFECFDF5),
    surfaceVariant = Color(0xFF133830),
    onSurfaceVariant = Color(0xFFA7F3D0)
)

private val RoyalAmethystScheme = darkColorScheme(
    primary = AmethystPrimary,
    onPrimary = Color(0xFF280047),
    primaryContainer = Color(0xFF4C1D95),
    onPrimaryContainer = Color(0xFFF3E8FF),
    secondary = AmethystSecondary,
    onSecondary = Color(0xFF3B001E),
    secondaryContainer = Color(0xFF701A75),
    onSecondaryContainer = Color(0xFFFCE7F3),
    tertiary = AmethystTertiary,
    background = AmethystBg,
    onBackground = Color(0xFFFAF5FF),
    surface = AmethystSurface,
    onSurface = Color(0xFFFAF5FF),
    surfaceVariant = Color(0xFF311D54),
    onSurfaceVariant = Color(0xFFE9D5FF)
)

@Composable
fun MyApplicationTheme(
    themeSkin: AppThemeSkin = AppThemeSkin.MIDNIGHT_GOLD,
    content: @Composable () -> Unit
) {
    val colorScheme = when (themeSkin) {
        AppThemeSkin.MIDNIGHT_GOLD -> MidnightGoldScheme
        AppThemeSkin.DAYLIGHT_ACADEMIC -> DaylightAcademicScheme
        AppThemeSkin.CYBER_SCHOLAR -> CyberScholarScheme
        AppThemeSkin.ROYAL_AMETHYST -> RoyalAmethystScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
