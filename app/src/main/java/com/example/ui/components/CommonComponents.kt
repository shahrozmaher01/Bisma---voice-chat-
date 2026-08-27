package com.example.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.ui.theme.*

@Composable
fun GlassCard(
    modifier: Modifier = Modifier,
    backgroundColor: Color = SurfaceCard.copy(alpha = 0.85f),
    borderColor: Color = SurfaceCardBorder,
    shape: RoundedCornerShape = RoundedCornerShape(16.dp),
    content: @Composable BoxScope.() -> Unit
) {
    Box(
        modifier = modifier
            .clip(shape)
            .background(backgroundColor)
            .border(1.dp, borderColor, shape)
            .padding(12.dp),
        content = content
    )
}

@Composable
fun AvatarWithFrame(
    avatarUrl: String?,
    size: Dp = 56.dp,
    frameId: String? = null,
    vipLevel: Int = 0,
    isSpeaking: Boolean = false,
    isOnline: Boolean = true,
    onClick: (() -> Unit)? = null
) {
    // Multi-stage pulse animation when speaking
    val infiniteTransition = rememberInfiniteTransition(label = "speaking")
    val pulseScale1 by infiniteTransition.animateFloat(
        initialValue = 1.0f,
        targetValue = 1.25f,
        animationSpec = infiniteRepeatable(
            animation = tween(800, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "speaking_pulse1"
    )
    val pulseScale2 by infiniteTransition.animateFloat(
        initialValue = 1.05f,
        targetValue = 1.4f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, delayMillis = 200, easing = LinearOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "speaking_pulse2"
    )
    val pulseAlpha by infiniteTransition.animateFloat(
        initialValue = 0.6f,
        targetValue = 0.1f,
        animationSpec = infiniteRepeatable(
            animation = tween(800, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "speaking_alpha"
    )

    val frameBorderBrush = when {
        frameId == "frame_galaxy_gold" || vipLevel >= 4 -> Brush.sweepGradient(listOf(GoldYellow, GoldAmber, GoldOrange, GoldYellow))
        frameId == "frame_cyber_blue" -> Brush.sweepGradient(listOf(ElectricBlue, BrightCyan, NeonPink, ElectricBlue))
        frameId == "frame_heart_romance" -> Brush.sweepGradient(listOf(CharmRose, NeonPink, CharmPink, CharmRose))
        else -> Brush.sweepGradient(listOf(NeonPink, NeonPurpleLight, ElectricBlue, NeonPink))
    }

    Box(
        modifier = Modifier
            .size(size)
            .then(if (onClick != null) Modifier.clickable { onClick() } else Modifier),
        contentAlignment = Alignment.Center
    ) {
        // Multi-ring Speaking concentric ripple waves
        if (isSpeaking) {
            // Outer diffuse ring
            Box(
                modifier = Modifier
                    .size(size)
                    .scale(pulseScale2)
                    .clip(CircleShape)
                    .background(BrightCyan.copy(alpha = pulseAlpha * 0.4f))
            )
            // Middle ring
            Box(
                modifier = Modifier
                    .size(size)
                    .scale(pulseScale1)
                    .clip(CircleShape)
                    .border(1.5.dp, BrightCyan.copy(alpha = pulseAlpha), CircleShape)
                    .background(ElectricBlue.copy(alpha = pulseAlpha * 0.5f))
            )
        }

        // Inner Avatar photo
        AsyncImage(
            model = avatarUrl ?: "https://images.unsplash.com/photo-1534528741775-53994a69daeb?w=200",
            contentDescription = "User Avatar",
            modifier = Modifier
                .size(size - 6.dp)
                .clip(CircleShape),
            contentScale = ContentScale.Crop
        )

        // Frame Overlay
        Box(
            modifier = Modifier
                .size(size)
                .clip(CircleShape)
                .border(
                    BorderStroke(
                        if (isSpeaking) 3.dp else 2.5.dp,
                        if (isSpeaking) Brush.sweepGradient(listOf(BrightCyan, EmeraldGreen, ElectricBlue, BrightCyan)) else frameBorderBrush
                    ),
                    CircleShape
                )
        )

        // VIP Mini Crown Badge
        if (vipLevel > 0) {
            Box(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .offset(x = 2.dp, y = (-2).dp)
                    .clip(CircleShape)
                    .background(GoldAmber)
                    .padding(2.dp)
            ) {
                Text(
                    text = "V$vipLevel",
                    color = Color.Black,
                    fontWeight = FontWeight.Black,
                    fontSize = 8.sp,
                    modifier = Modifier.padding(horizontal = 2.dp)
                )
            }
        }

        // Online dot
        if (isOnline && vipLevel == 0 && !isSpeaking) {
            Box(
                modifier = Modifier
                    .size(10.dp)
                    .align(Alignment.BottomEnd)
                    .clip(CircleShape)
                    .background(EmeraldGreen)
                    .border(1.dp, BackgroundDark, CircleShape)
            )
        }
    }
}

@Composable
fun VipBadge(vipLevel: Int, modifier: Modifier = Modifier) {
    if (vipLevel <= 0) {
        Surface(
            color = SurfaceCard,
            shape = RoundedCornerShape(8.dp),
            modifier = modifier
        ) {
            Text(
                text = "VIP: Inactive",
                color = TextMuted,
                fontSize = 11.sp,
                fontWeight = FontWeight.Medium,
                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
            )
        }
    } else {
        Box(
            modifier = modifier
                .clip(RoundedCornerShape(8.dp))
                .background(Brush.horizontalGradient(listOf(GoldYellow, GoldAmber)))
                .padding(horizontal = 6.dp, vertical = 2.dp)
        ) {
            Text(
                text = "👑 VIP $vipLevel",
                color = Color.Black,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
fun LevelBadge(level: Int, type: LevelType, modifier: Modifier = Modifier) {
    val (colorBrush, label, icon) = when (type) {
        LevelType.USER -> Triple(Brush.horizontalGradient(listOf(ElectricBlueDark, ElectricBlue)), "Lv", "⚡")
        LevelType.RICH -> Triple(Brush.horizontalGradient(listOf(GoldYellow, GoldOrange)), "Rich", "🏆")
        LevelType.CHARM -> Triple(Brush.horizontalGradient(listOf(CharmPink, CharmRose)), "Charm", "💜")
    }

    Box(
        modifier = modifier
            .clip(RoundedCornerShape(6.dp))
            .background(colorBrush)
            .padding(horizontal = 5.dp, vertical = 1.5.dp)
    ) {
        Text(
            text = "$icon $label $level",
            color = Color.White,
            fontSize = 10.sp,
            fontWeight = FontWeight.Bold
        )
    }
}

enum class LevelType { USER, RICH, CHARM }

@Composable
fun SpeakingWaveAnimation(modifier: Modifier = Modifier) {
    val infiniteTransition = rememberInfiniteTransition(label = "wave")
    val h1 by infiniteTransition.animateFloat(
        initialValue = 4f, targetValue = 18f,
        animationSpec = infiniteRepeatable(tween(400, easing = FastOutSlowInEasing), RepeatMode.Reverse), label = "h1"
    )
    val h2 by infiniteTransition.animateFloat(
        initialValue = 16f, targetValue = 6f,
        animationSpec = infiniteRepeatable(tween(350, easing = FastOutSlowInEasing), RepeatMode.Reverse), label = "h2"
    )
    val h3 by infiniteTransition.animateFloat(
        initialValue = 8f, targetValue = 20f,
        animationSpec = infiniteRepeatable(tween(450, easing = FastOutSlowInEasing), RepeatMode.Reverse), label = "h3"
    )

    Row(
        modifier = modifier.height(20.dp),
        horizontalArrangement = Arrangement.spacedBy(2.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(modifier = Modifier.width(3.dp).height(h1.dp).clip(RoundedCornerShape(2.dp)).background(EmeraldGreen))
        Box(modifier = Modifier.width(3.dp).height(h2.dp).clip(RoundedCornerShape(2.dp)).background(EmeraldGreen))
        Box(modifier = Modifier.width(3.dp).height(h3.dp).clip(RoundedCornerShape(2.dp)).background(EmeraldGreen))
    }
}

@Composable
fun AudioActivityEqualizer(
    isSpeaking: Boolean = true,
    barCount: Int = 5,
    modifier: Modifier = Modifier,
    barColor: Color = EmeraldGreen,
    maxBarHeight: Dp = 18.dp,
    minBarHeight: Dp = 3.dp,
    barWidth: Dp = 3.dp
) {
    val infiniteTransition = rememberInfiniteTransition(label = "audio_equalizer")
    
    // Animate 5 distinct bars with different speeds and phases
    val h1 by infiniteTransition.animateFloat(
        initialValue = 0.2f, targetValue = 1.0f,
        animationSpec = infiniteRepeatable(tween(350, easing = FastOutSlowInEasing), RepeatMode.Reverse), label = "h1"
    )
    val h2 by infiniteTransition.animateFloat(
        initialValue = 0.9f, targetValue = 0.15f,
        animationSpec = infiniteRepeatable(tween(280, delayMillis = 50, easing = FastOutSlowInEasing), RepeatMode.Reverse), label = "h2"
    )
    val h3 by infiniteTransition.animateFloat(
        initialValue = 0.3f, targetValue = 0.95f,
        animationSpec = infiniteRepeatable(tween(420, delayMillis = 100, easing = FastOutSlowInEasing), RepeatMode.Reverse), label = "h3"
    )
    val h4 by infiniteTransition.animateFloat(
        initialValue = 0.85f, targetValue = 0.25f,
        animationSpec = infiniteRepeatable(tween(310, delayMillis = 150, easing = FastOutSlowInEasing), RepeatMode.Reverse), label = "h4"
    )
    val h5 by infiniteTransition.animateFloat(
        initialValue = 0.15f, targetValue = 0.8f,
        animationSpec = infiniteRepeatable(tween(390, delayMillis = 80, easing = FastOutSlowInEasing), RepeatMode.Reverse), label = "h5"
    )

    val multipliers = listOf(h1, h2, h3, h4, h5)

    Row(
        modifier = modifier.height(maxBarHeight),
        horizontalArrangement = Arrangement.spacedBy(2.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        for (i in 0 until barCount) {
            val factor = if (isSpeaking) multipliers[i % multipliers.size] else 0.15f
            val calculatedHeight = minBarHeight + (maxBarHeight - minBarHeight) * factor
            val color = if (isSpeaking) {
                when {
                    factor > 0.8f -> GoldYellow
                    factor > 0.4f -> barColor
                    else -> barColor.copy(alpha = 0.7f)
                }
            } else {
                TextMuted.copy(alpha = 0.4f)
            }

            Box(
                modifier = Modifier
                    .width(barWidth)
                    .height(calculatedHeight)
                    .clip(RoundedCornerShape(2.dp))
                    .background(color)
            )
        }
    }
}

@Composable
fun LiveDecibelMeter(
    isSpeaking: Boolean,
    modifier: Modifier = Modifier
) {
    val infiniteTransition = rememberInfiniteTransition(label = "db_meter")
    val level by infiniteTransition.animateFloat(
        initialValue = 0.2f,
        targetValue = 0.95f,
        animationSpec = infiniteRepeatable(tween(300, easing = FastOutSlowInEasing), RepeatMode.Reverse),
        label = "db_level"
    )

    val currentLevel = if (isSpeaking) level else 0.05f
    val activeBars = (currentLevel * 8).toInt().coerceIn(0, 8)

    Row(
        modifier = modifier
            .clip(RoundedCornerShape(6.dp))
            .background(Color(0x33000000))
            .padding(horizontal = 4.dp, vertical = 2.dp),
        horizontalArrangement = Arrangement.spacedBy(2.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        for (i in 0 until 8) {
            val barColor = when {
                i < 4 -> EmeraldGreen
                i < 6 -> GoldYellow
                else -> DarkRed
            }
            val isActive = i <= activeBars && isSpeaking
            Box(
                modifier = Modifier
                    .width(2.5.dp)
                    .height((6 + i * 1.2f).dp)
                    .clip(RoundedCornerShape(1.dp))
                    .background(if (isActive) barColor else Color.White.copy(alpha = 0.15f))
            )
        }
    }
}

@Composable
fun InteractiveMuteToggleButton(
    isMuted: Boolean,
    onToggle: () -> Unit,
    modifier: Modifier = Modifier,
    hasSeat: Boolean = true
) {
    val infiniteTransition = rememberInfiniteTransition(label = "mic_glow")
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 1.0f,
        targetValue = 1.12f,
        animationSpec = infiniteRepeatable(tween(700, easing = FastOutSlowInEasing), RepeatMode.Reverse),
        label = "pulse"
    )
    val glowAlpha by infiniteTransition.animateFloat(
        initialValue = 0.5f,
        targetValue = 0.15f,
        animationSpec = infiniteRepeatable(tween(700, easing = FastOutSlowInEasing), RepeatMode.Reverse),
        label = "glow_alpha"
    )

    val isLive = !isMuted && hasSeat

    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center
    ) {
        // Glowing Aura when unmuted & active
        if (isLive) {
            Box(
                modifier = Modifier
                    .size(54.dp)
                    .scale(pulseScale)
                    .clip(CircleShape)
                    .background(EmeraldGreen.copy(alpha = glowAlpha))
            )
        }

        // Main Button Surface
        Surface(
            onClick = onToggle,
            shape = CircleShape,
            color = when {
                !hasSeat -> SurfaceCard
                !isMuted -> EmeraldGreen
                else -> Color(0x33FF1744)
            },
            border = BorderStroke(
                1.5.dp,
                when {
                    !hasSeat -> SurfaceCardBorder
                    !isMuted -> BrightCyan
                    else -> DarkRed
                }
            ),
            modifier = Modifier.size(46.dp),
            shadowElevation = if (isLive) 8.dp else 2.dp
        ) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = if (isLive) Icons.Default.Mic else Icons.Default.MicOff,
                    contentDescription = if (isLive) "Mute Microphone" else "Unmute Microphone",
                    tint = if (isLive) Color.Black else if (!hasSeat) TextMuted else DarkRed,
                    modifier = Modifier.size(24.dp)
                )
            }
        }
    }
}

@Composable
fun NeonButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    icon: ImageVector? = null,
    brush: Brush = PrimaryGradient,
    enabled: Boolean = true
) {
    Button(
        onClick = onClick,
        enabled = enabled,
        colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
        contentPadding = PaddingValues(),
        shape = RoundedCornerShape(12.dp),
        modifier = modifier.height(48.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(brush)
                .padding(horizontal = 16.dp),
            contentAlignment = Alignment.Center
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                if (icon != null) {
                    Icon(imageVector = icon, contentDescription = null, tint = Color.White, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                }
                Text(text = text, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 15.sp)
            }
        }
    }
}
