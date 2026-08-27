package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.User
import com.example.data.model.VoiceRoom
import com.example.data.repository.BismaRepository
import com.example.ui.components.*
import com.example.ui.theme.*

@Composable
fun RankingsScreen(
    repository: BismaRepository,
    initialTab: Int = 0, // 0: Wealth, 1: Charm, 2: Room
    onBack: () -> Unit,
    onOpenUserProfile: (String) -> Unit,
    onOpenRoom: (String) -> Unit
) {
    val topWealth by repository.topWealthUsers.collectAsState(initial = emptyList())
    val topCharm by repository.topCharmUsers.collectAsState(initial = emptyList())
    val activeRooms by repository.activeRooms.collectAsState(initial = emptyList())

    var selectedTab by remember { mutableIntStateOf(initialTab) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkBackgroundGradient)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
        ) {
            // Top Bar
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onBack) {
                    Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = Color.White)
                }
                Text(
                    text = "Bisma Leaderboards 👑",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }

            // Tabs (Top Wealth, Top Charm, Top Room)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 14.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                FilterChip(
                    selected = selectedTab == 0,
                    onClick = { selectedTab = 0 },
                    label = { Text("🏆 Top Wealth") }
                )
                FilterChip(
                    selected = selectedTab == 1,
                    onClick = { selectedTab = 1 },
                    label = { Text("💜 Top Charm") }
                )
                FilterChip(
                    selected = selectedTab == 2,
                    onClick = { selectedTab = 2 },
                    label = { Text("💚 Top Rooms") }
                )
            }

            when (selectedTab) {
                0 -> {
                    // Top Wealth Users
                    LazyColumn(
                        modifier = Modifier.fillMaxSize().padding(horizontal = 14.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                        contentPadding = PaddingValues(bottom = 30.dp)
                    ) {
                        itemsIndexed(topWealth) { index, user ->
                            RankUserCard(
                                rank = index + 1,
                                user = user,
                                scoreText = "🪙 ${user.coins} Coins",
                                scoreColor = GoldYellow,
                                onClick = { onOpenUserProfile(user.id) }
                            )
                        }
                    }
                }
                1 -> {
                    // Top Charm Users
                    LazyColumn(
                        modifier = Modifier.fillMaxSize().padding(horizontal = 14.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                        contentPadding = PaddingValues(bottom = 30.dp)
                    ) {
                        itemsIndexed(topCharm) { index, user ->
                            RankUserCard(
                                rank = index + 1,
                                user = user,
                                scoreText = "💜 Lv ${user.charmLevel} Charm",
                                scoreColor = CharmPink,
                                onClick = { onOpenUserProfile(user.id) }
                            )
                        }
                    }
                }
                2 -> {
                    // Top Rooms
                    LazyColumn(
                        modifier = Modifier.fillMaxSize().padding(horizontal = 14.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                        contentPadding = PaddingValues(bottom = 30.dp)
                    ) {
                        itemsIndexed(activeRooms) { index, room ->
                            GlassCard(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { onOpenRoom(room.id) }
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Text(
                                            text = when (index) {
                                                0 -> "🥇"
                                                1 -> "🥈"
                                                2 -> "🥉"
                                                else -> "#${index + 1}"
                                            },
                                            fontSize = 16.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = Color.White
                                        )
                                        Spacer(modifier = Modifier.width(10.dp))
                                        AvatarWithFrame(avatarUrl = room.ownerAvatar, size = 42.dp, vipLevel = room.ownerVip)
                                        Spacer(modifier = Modifier.width(10.dp))
                                        Column {
                                            Text(text = room.title, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 13.sp, maxLines = 1)
                                            Text(text = "Host: ${room.ownerName} • ${room.country}", color = TextSecondary, fontSize = 10.sp)
                                        }
                                    }

                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        SpeakingWaveAnimation(modifier = Modifier.height(10.dp))
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text(text = "${room.onlineCount}", color = EmeraldGreen, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun RankUserCard(
    rank: Int,
    user: User,
    scoreText: String,
    scoreColor: Color,
    onClick: () -> Unit
) {
    val medal = when (rank) {
        1 -> "🥇"
        2 -> "🥈"
        3 -> "🥉"
        else -> "#$rank"
    }

    GlassCard(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(text = medal, fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color.White)
                Spacer(modifier = Modifier.width(10.dp))
                AvatarWithFrame(avatarUrl = user.avatarUrl, size = 44.dp, vipLevel = user.vipLevel)
                Spacer(modifier = Modifier.width(10.dp))
                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(text = user.username, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                        if (user.vipLevel > 0) {
                            Spacer(modifier = Modifier.width(4.dp))
                            VipBadge(vipLevel = user.vipLevel)
                        }
                    }
                    Text(text = "ID: ${user.id} • ${user.country}", color = TextSecondary, fontSize = 10.sp)
                }
            }

            Text(text = scoreText, color = scoreColor, fontWeight = FontWeight.Bold, fontSize = 12.sp)
        }
    }
}
