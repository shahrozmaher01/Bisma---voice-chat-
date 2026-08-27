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
    // Pulse animation when speaking
    val infiniteTransition = rememberInfiniteTransition(label = "speaking")
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 1.0f,
        targetValue = 1.15f,
        animationSpec = infiniteRepeatable(
            animation = tween(600, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "speaking_pulse"
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
        // Outer Speaking ripple
        if (isSpeaking) {
            Box(
                modifier = Modifier
                    .size(size)
                    .scale(pulseScale)
                    .clip(CircleShape)
                    .background(Color(0x3300E5FF))
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

        // Transparent Frame Overlay (Center is transparent)
        Box(
            modifier = Modifier
                .size(size)
                .clip(CircleShape)
                .border(BorderStroke(2.5.dp, frameBorderBrush), CircleShape)
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
        if (isOnline && vipLevel == 0) {
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
