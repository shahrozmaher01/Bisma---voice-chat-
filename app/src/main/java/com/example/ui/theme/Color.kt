package com.example.ui.theme

import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color

// Sophisticated Dark Primary & Jewel Accents
val NeonPink = Color(0xFFF43F85)
val NeonPinkLight = Color(0xFFFB71A5)
val ElectricBlue = Color(0xFF38BDF8)
val ElectricBlueDark = Color(0xFF0284C7)
val NeonPurple = Color(0xFFA855F7)
val NeonPurpleLight = Color(0xFFC084FC)
val NeonViolet = Color(0xFF7E22CE)

// Rich Level & Wealth Gold (Sophisticated Champagne & Amber Gold)
val GoldYellow = Color(0xFFFCD34D)
val GoldAmber = Color(0xFFF59E0B)
val GoldOrange = Color(0xFFD97706)

// Charm Level Rose & Pink (Sophisticated Rose Gold & Magenta)
val CharmRose = Color(0xFFF43F5E)
val CharmPink = Color(0xFFFB7185)

// Status & Emerald Accents
val EmeraldGreen = Color(0xFF10B981)
val BrightCyan = Color(0xFF06B6D4)
val ElectricCyan = Color(0xFF06B6D4)
val DarkRed = Color(0xFFEF4444)

// Sophisticated Dark Obsidian & Midnight Backgrounds
val BackgroundDark = Color(0xFF0A0716)
val BackgroundGradientTop = Color(0xFF130D28)
val BackgroundGradientBottom = Color(0xFF06040E)

val SurfaceDark = Color(0xFF130E29)
val SurfaceCard = Color(0xFF1A1438)
val SurfaceCardBackground = Color(0xFF1A1438)
val SurfaceCardBorder = Color(0xFF2E245E)
val SurfaceGlass = Color(0x661A1438)
val SurfaceGlassBorder = Color(0x40F43F85)

val TextPrimary = Color(0xFFF8FAFC)
val TextSecondary = Color(0xFFCBD5E1)
val TextMuted = Color(0xFF7E8EA6)

// Gradients - Sophisticated Dark Luxe
val PrimaryGradient = Brush.horizontalGradient(
    colors = listOf(Color(0xFFEC4899), Color(0xFFA855F7), Color(0xFF38BDF8))
)

val CardGlowGradient = Brush.verticalGradient(
    colors = listOf(Color(0x33F43F85), Color(0x1A38BDF8), Color(0x08130E29))
)

val GoldGradient = Brush.horizontalGradient(
    colors = listOf(GoldYellow, GoldAmber)
)

val CharmGradient = Brush.horizontalGradient(
    colors = listOf(CharmPink, CharmRose)
)

val RoomRankGradient = Brush.horizontalGradient(
    colors = listOf(EmeraldGreen, BrightCyan)
)

val DarkBackgroundGradient = Brush.verticalGradient(
    colors = listOf(BackgroundGradientTop, BackgroundDark, BackgroundGradientBottom)
)

