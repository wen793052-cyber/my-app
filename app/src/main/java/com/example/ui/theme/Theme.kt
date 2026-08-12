package com.example.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

data class ThemePaletteItem(
    val key: String,
    val name: String,
    val primaryColor: Color,
    val containerColor: Color
)

val AvailablePalettes = listOf(
    ThemePaletteItem("WARM_ROSE", "温暖粉桃", Color(0xFFC06C84), Color(0xFFF8E5EE)),
    ThemePaletteItem("AMBER_SUN", "琥珀暖阳", Color(0xFFD97706), Color(0xFFFEF3C7)),
    ThemePaletteItem("MATCHA_GREEN", "抹茶清绿", Color(0xFF4E8A5E), Color(0xFFE2F0E6)),
    ThemePaletteItem("MORANDI_BLUE", "莫兰迪蓝", Color(0xFF3B82F6), Color(0xFFE8F0FE)),
    ThemePaletteItem("LAVENDER_PURPLE", "熏衣草紫", Color(0xFF8B5CF6), Color(0xFFF3E8FF)),
    ThemePaletteItem("CARAMEL_COFFEE", "焦糖咖啡", Color(0xFF8C5A3C), Color(0xFFF5ECE5)),
    ThemePaletteItem("SAKURA_PINK", "樱花软粉", Color(0xFFDB2777), Color(0xFFFCE7F3))
)

fun getLightColorScheme(palette: String): ColorScheme {
    return when (palette) {
        "AMBER_SUN" -> lightColorScheme(
            primary = Color(0xFFD97706),
            onPrimary = Color(0xFFFFFFFF),
            primaryContainer = Color(0xFFFEF3C7),
            onPrimaryContainer = Color(0xFF78350F),
            secondary = Color(0xFFF59E0B),
            onSecondary = Color(0xFFFFFFFF),
            secondaryContainer = Color(0xFFFFFBEB),
            onSecondaryContainer = Color(0xFF92400E),
            tertiary = Color(0xFF10B981),
            background = Color(0xFFFFFDF5),
            onBackground = Color(0xFF292524),
            surface = Color(0xFFFFFFFF),
            onSurface = Color(0xFF292524),
            surfaceVariant = Color(0xFFF7F3E9),
            onSurfaceVariant = Color(0xFF57534E)
        )
        "MATCHA_GREEN" -> lightColorScheme(
            primary = Color(0xFF4E8A5E),
            onPrimary = Color(0xFFFFFFFF),
            primaryContainer = Color(0xFFE2F0E6),
            onPrimaryContainer = Color(0xFF143B1F),
            secondary = Color(0xFF6B9E78),
            onSecondary = Color(0xFFFFFFFF),
            secondaryContainer = Color(0xFFEDF7F0),
            onSecondaryContainer = Color(0xFF20482B),
            tertiary = Color(0xFF85DCB0),
            background = Color(0xFFF7FAF7),
            onBackground = Color(0xFF1F2922),
            surface = Color(0xFFFFFFFF),
            onSurface = Color(0xFF1F2922),
            surfaceVariant = Color(0xFFE9F0EC),
            onSurfaceVariant = Color(0xFF435248)
        )
        "MORANDI_BLUE" -> lightColorScheme(
            primary = Color(0xFF3B82F6),
            onPrimary = Color(0xFFFFFFFF),
            primaryContainer = Color(0xFFE8F0FE),
            onPrimaryContainer = Color(0xFF1E3A8A),
            secondary = Color(0xFF60A5FA),
            onSecondary = Color(0xFFFFFFFF),
            secondaryContainer = Color(0xFFEFF6FF),
            onSecondaryContainer = Color(0xFF1E40AF),
            tertiary = Color(0xFF38BDF8),
            background = Color(0xFFF8FAFC),
            onBackground = Color(0xFF1E293B),
            surface = Color(0xFFFFFFFF),
            onSurface = Color(0xFF1E293B),
            surfaceVariant = Color(0xFFEDF2F7),
            onSurfaceVariant = Color(0xFF475569)
        )
        "LAVENDER_PURPLE" -> lightColorScheme(
            primary = Color(0xFF8B5CF6),
            onPrimary = Color(0xFFFFFFFF),
            primaryContainer = Color(0xFFF3E8FF),
            onPrimaryContainer = Color(0xFF4C1D95),
            secondary = Color(0xFFA855F7),
            onSecondary = Color(0xFFFFFFFF),
            secondaryContainer = Color(0xFFFAF5FF),
            onSecondaryContainer = Color(0xFF6B21A8),
            tertiary = Color(0xFFEC4899),
            background = Color(0xFFFAF8FC),
            onBackground = Color(0xFF261D33),
            surface = Color(0xFFFFFFFF),
            onSurface = Color(0xFF261D33),
            surfaceVariant = Color(0xFFF1EAFA),
            onSurfaceVariant = Color(0xFF55446B)
        )
        "CARAMEL_COFFEE" -> lightColorScheme(
            primary = Color(0xFF8C5A3C),
            onPrimary = Color(0xFFFFFFFF),
            primaryContainer = Color(0xFFF5ECE5),
            onPrimaryContainer = Color(0xFF3D2110),
            secondary = Color(0xFFA06D4D),
            onSecondary = Color(0xFFFFFFFF),
            secondaryContainer = Color(0xFFFAF2EC),
            onSecondaryContainer = Color(0xFF57331D),
            tertiary = Color(0xFFD97706),
            background = Color(0xFFFAF7F5),
            onBackground = Color(0xFF2B231F),
            surface = Color(0xFFFFFFFF),
            onSurface = Color(0xFF2B231F),
            surfaceVariant = Color(0xFFF0E8E2),
            onSurfaceVariant = Color(0xFF594B43)
        )
        "SAKURA_PINK" -> lightColorScheme(
            primary = Color(0xFFDB2777),
            onPrimary = Color(0xFFFFFFFF),
            primaryContainer = Color(0xFFFCE7F3),
            onPrimaryContainer = Color(0xFF831843),
            secondary = Color(0xFFF472B6),
            onSecondary = Color(0xFFFFFFFF),
            secondaryContainer = Color(0xFFFDF2F8),
            onSecondaryContainer = Color(0xFF9D174D),
            tertiary = Color(0xFFFB7185),
            background = Color(0xFFFFF9FC),
            onBackground = Color(0xFF331D28),
            surface = Color(0xFFFFFFFF),
            onSurface = Color(0xFF331D28),
            surfaceVariant = Color(0xFFF7E9F1),
            onSurfaceVariant = Color(0xFF634555)
        )
        else -> lightColorScheme( // WARM_ROSE
            primary = WarmPrimaryLight,
            onPrimary = WarmOnPrimaryLight,
            primaryContainer = WarmPrimaryContainerLight,
            onPrimaryContainer = WarmOnPrimaryContainerLight,
            secondary = WarmSecondaryLight,
            onSecondary = WarmOnSecondaryLight,
            secondaryContainer = WarmSecondaryContainerLight,
            onSecondaryContainer = WarmOnSecondaryContainerLight,
            tertiary = WarmTertiaryLight,
            onTertiary = WarmOnTertiaryLight,
            tertiaryContainer = WarmTertiaryContainerLight,
            onTertiaryContainer = WarmOnTertiaryContainerLight,
            background = WarmBackgroundLight,
            onBackground = WarmOnBackgroundLight,
            surface = WarmSurfaceLight,
            onSurface = WarmOnSurfaceLight,
            surfaceVariant = WarmSurfaceVariantLight,
            onSurfaceVariant = WarmOnSurfaceVariantLight
        )
    }
}

fun getDarkColorScheme(palette: String): ColorScheme {
    return when (palette) {
        "AMBER_SUN" -> darkColorScheme(
            primary = Color(0xFFFBBF24),
            onPrimary = Color(0xFF451A03),
            primaryContainer = Color(0xFF78350F),
            onPrimaryContainer = Color(0xFFFEF3C7),
            secondary = Color(0xFFF59E0B),
            background = Color(0xFF1C1917),
            onBackground = Color(0xFFF5F5F4),
            surface = Color(0xFF262322),
            onSurface = Color(0xFFF5F5F4)
        )
        "MATCHA_GREEN" -> darkColorScheme(
            primary = Color(0xFF81C784),
            onPrimary = Color(0xFF0C3B18),
            primaryContainer = Color(0xFF1B4323),
            onPrimaryContainer = Color(0xFFE2F0E6),
            secondary = Color(0xFF6B9E78),
            background = Color(0xFF151F17),
            onBackground = Color(0xFFE8F2EA),
            surface = Color(0xFF1E2B21),
            onSurface = Color(0xFFE8F2EA)
        )
        "MORANDI_BLUE" -> darkColorScheme(
            primary = Color(0xFF82B1FF),
            onPrimary = Color(0xFF0A2540),
            primaryContainer = Color(0xFF1A365D),
            onPrimaryContainer = Color(0xFFE8F0FE),
            secondary = Color(0xFF60A5FA),
            background = Color(0xFF121824),
            onBackground = Color(0xFFE2E8F0),
            surface = Color(0xFF1A2234),
            onSurface = Color(0xFFE2E8F0)
        )
        "LAVENDER_PURPLE" -> darkColorScheme(
            primary = Color(0xFFC084FC),
            onPrimary = Color(0xFF3B0764),
            primaryContainer = Color(0xFF581C87),
            onPrimaryContainer = Color(0xFFF3E8FF),
            secondary = Color(0xFFA855F7),
            background = Color(0xFF1A1325),
            onBackground = Color(0xFFF3E8FF),
            surface = Color(0xFF241B33),
            onSurface = Color(0xFFF3E8FF)
        )
        "CARAMEL_COFFEE" -> darkColorScheme(
            primary = Color(0xFFD7A17C),
            onPrimary = Color(0xFF3B1E0A),
            primaryContainer = Color(0xFF54321A),
            onPrimaryContainer = Color(0xFFF5ECE5),
            secondary = Color(0xFFA06D4D),
            background = Color(0xFF1C1714),
            onBackground = Color(0xFFEFE8E3),
            surface = Color(0xFF27211C),
            onSurface = Color(0xFFEFE8E3)
        )
        "SAKURA_PINK" -> darkColorScheme(
            primary = Color(0xFFF472B6),
            onPrimary = Color(0xFF500724),
            primaryContainer = Color(0xFF831843),
            onPrimaryContainer = Color(0xFFFCE7F3),
            secondary = Color(0xFFFB7185),
            background = Color(0xFF21131B),
            onBackground = Color(0xFFFCE7F3),
            surface = Color(0xFF2B1B24),
            onSurface = Color(0xFFFCE7F3)
        )
        else -> darkColorScheme( // WARM_ROSE
            primary = WarmPrimaryDark,
            onPrimary = WarmOnPrimaryDark,
            primaryContainer = WarmPrimaryContainerDark,
            onPrimaryContainer = WarmOnPrimaryContainerDark,
            secondary = WarmSecondaryDark,
            onSecondary = WarmOnSecondaryDark,
            secondaryContainer = WarmSecondaryContainerDark,
            onSecondaryContainer = WarmOnSecondaryContainerDark,
            tertiary = WarmTertiaryDark,
            onTertiary = WarmOnTertiaryDark,
            tertiaryContainer = WarmTertiaryContainerDark,
            onTertiaryContainer = WarmOnTertiaryContainerDark,
            background = WarmBackgroundDark,
            onBackground = WarmOnBackgroundDark,
            surface = WarmSurfaceDark,
            onSurface = WarmOnSurfaceDark,
            surfaceVariant = WarmSurfaceVariantDark,
            onSurfaceVariant = WarmOnSurfaceVariantDark
        )
    }
}

@Composable
fun WarmJournalTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    palette: String = "WARM_ROSE",
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) getDarkColorScheme(palette) else getLightColorScheme(palette)

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
