package com.businessledger.presentation.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.Typography
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.view.WindowCompat

val FintechEmerald = Color(0xFF10B981) // Vibrant Neon Mint/Emerald
val FintechEmeraldGlow = Color(0xFF34D399)
val FintechEmeraldDark = Color(0xFF064E3B)
val FintechEmeraldContainer = Color(0xFF065F46)

val FintechDarkBg = Color(0xFF0A0F1D) // Deep Slate Charcoal Canvas
val FintechDarkSurface = Color(0xFF111927) // Primary Surface
val FintechDarkCard = Color(0xFF192233) // Elevated Card Surface
val FintechDarkCardElevated = Color(0xFF1F2B42) // Highlighted Card
val FintechDarkBorder = Color(0xFF26334D) // Subtle high-end border

val EmeraldGreen = FintechEmerald
val EmeraldDark = Color(0xFF0F172A)
val EmeraldLight = Color(0xFF064E3B)
val EmeraldContainer = Color(0xFF132D29)
val EmeraldAccent = FintechEmeraldGlow

val AmberGold = Color(0xFFF59E0B)
val AmberGoldLight = Color(0xFF78350F)

val CreditGreen = Color(0xFF10B981)
val CreditGreenBg = Color(0x2410B981)
val DebitRed = Color(0xFFF43F5E)
val DebitRedBg = Color(0x24F43F5E)

val DarkSurface = FintechDarkSurface
val DarkBackground = FintechDarkBg
val DarkCard = FintechDarkCard

private val LightColorScheme = lightColorScheme(
    primary = Color(0xFF0D9488),
    onPrimary = Color.White,
    primaryContainer = Color(0xFFCCFBF1),
    onPrimaryContainer = Color(0xFF115E59),
    secondary = AmberGold,
    onSecondary = Color.White,
    secondaryContainer = Color(0xFFFEF3C7),
    onSecondaryContainer = Color(0xFF78350F),
    tertiary = Color(0xFF0284C7),
    background = Color(0xFFF8FAFC),
    onBackground = Color(0xFF0F172A),
    surface = Color.White,
    onSurface = Color(0xFF0F172A),
    surfaceVariant = Color(0xFFF1F5F9),
    onSurfaceVariant = Color(0xFF475569),
    outline = Color(0xFFCBD5E1),
    error = DebitRed,
    onError = Color.White
)

private val DarkColorScheme = darkColorScheme(
    primary = FintechEmerald,
    onPrimary = Color(0xFF022C22),
    primaryContainer = Color(0xFF064E3B),
    onPrimaryContainer = Color(0xFFA7F3D0),
    secondary = Color(0xFFFBBF24),
    onSecondary = Color(0xFF451A03),
    secondaryContainer = Color(0xFF78350F),
    onSecondaryContainer = Color(0xFFFEF3C7),
    tertiary = Color(0xFF38BDF8),
    background = FintechDarkBg,
    onBackground = Color(0xFFF8FAFC),
    surface = FintechDarkSurface,
    onSurface = Color(0xFFF8FAFC),
    surfaceVariant = FintechDarkCard,
    onSurfaceVariant = Color(0xFF94A3B8),
    outline = FintechDarkBorder,
    error = Color(0xFFFB7185),
    onError = Color(0xFF4C0519)
)

val AppTypography = Typography(
    headlineLarge = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Bold,
        fontSize = 28.sp,
        lineHeight = 34.sp
    ),
    headlineMedium = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Bold,
        fontSize = 22.sp,
        lineHeight = 28.sp
    ),
    titleLarge = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.SemiBold,
        fontSize = 18.sp,
        lineHeight = 24.sp
    ),
    titleMedium = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.SemiBold,
        fontSize = 16.sp,
        lineHeight = 22.sp
    ),
    bodyLarge = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Normal,
        fontSize = 15.sp,
        lineHeight = 20.sp
    ),
    bodyMedium = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Normal,
        fontSize = 14.sp,
        lineHeight = 18.sp
    ),
    labelLarge = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Medium,
        fontSize = 13.sp,
        lineHeight = 16.sp
    )
)

val AppShapes = Shapes(
    extraSmall = RoundedCornerShape(6.dp),
    small = RoundedCornerShape(10.dp),
    medium = RoundedCornerShape(14.dp),
    large = RoundedCornerShape(20.dp),
    extraLarge = RoundedCornerShape(28.dp)
)

@Composable
fun BusinessLedgerTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme
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
        typography = AppTypography,
        shapes = AppShapes,
        content = content
    )
}
