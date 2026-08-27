package com.example.ui.screens

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.data.model.Agency
import com.example.data.model.Family
import com.example.data.repository.BismaRepository
import com.example.ui.components.*
import com.example.ui.theme.*

@Composable
fun AgencyFamilyScreen(
    repository: BismaRepository,
    onBack: () -> Unit
) {
    val context = LocalContext.current
    val agencies by repository.agencies.collectAsState(initial = emptyList())
    val families by repository.families.collectAsState(initial = emptyList())
    val currentUser by repository.currentUser.collectAsState(initial = null)

    var selectedTab by remember { mutableIntStateOf(0) } // 0: Agency, 1: Family, 2: CP Space

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
                    text = "Agency & Family Guilds",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }

            // Tabs
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 14.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                FilterChip(
                    selected = selectedTab == 0,
                    onClick = { selectedTab = 0 },
                    label = { Text("🏢 Voice Agency") }
                )
                FilterChip(
                    selected = selectedTab == 1,
                    onClick = { selectedTab = 1 },
                    label = { Text("👨‍👩‍👧‍👦 Royal Family") }
                )
                FilterChip(
                    selected = selectedTab == 2,
                    onClick = { selectedTab = 2 },
                    label = { Text("💖 CP Love Space") }
                )
            }

            when (selectedTab) {
                0 -> {
                    // Agency Tab
                    LazyColumn(
                        modifier = Modifier.fillMaxSize().padding(horizontal = 14.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp),
                        contentPadding = PaddingValues(bottom = 30.dp)
                    ) {
                        item {
                            GlassCard(modifier = Modifier.fillMaxWidth()) {
                                Column {
                                    Text("Agency Host Program", color = GoldYellow, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text("Join a verified voice talent agency to unlock daily host salaries, official badge perks, and exclusive event invitations.", color = TextSecondary, fontSize = 11.sp)
                                }
                            }
                        }

                        if (agencies.isEmpty()) {
                            item {
                                Box(
                                    modifier = Modifier.fillMaxWidth().padding(32.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text("No voice agencies registered yet.", color = TextSecondary, fontSize = 13.sp)
                                }
                            }
                        } else {
                            items(agencies) { ag ->
                                GlassCard(modifier = Modifier.fillMaxWidth()) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            AsyncImage(
                                                model = ag.logoUrl,
                                                contentDescription = null,
                                                modifier = Modifier.size(46.dp).clip(CircleShape)
                                            )
                                            Spacer(modifier = Modifier.width(10.dp))
                                            Column {
                                                Text(text = ag.name, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                                Text(text = "Owner: ${ag.ownerName} • ${ag.memberCount} Hosts", color = TextSecondary, fontSize = 11.sp)
                                                Text(text = "Agency Monthly Income: 🪙 ${ag.totalIncome}", color = GoldAmber, fontSize = 10.sp)
                                            }
                                        }

                                        Button(
                                            onClick = { Toast.makeText(context, "Application sent to ${ag.name}!", Toast.LENGTH_SHORT).show() },
                                            colors = ButtonDefaults.buttonColors(containerColor = NeonPink),
                                            shape = RoundedCornerShape(10.dp),
                                            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 2.dp),
                                            modifier = Modifier.height(30.dp)
                                        ) {
                                            Text("Apply", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
                1 -> {
                    // Family Tab
                    LazyColumn(
                        modifier = Modifier.fillMaxSize().padding(horizontal = 14.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp),
                        contentPadding = PaddingValues(bottom = 30.dp)
                    ) {
                        item {
                            GlassCard(modifier = Modifier.fillMaxWidth()) {
                                Column {
                                    Text("Family Community Clans", color = ElectricBlue, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text("Create or join a family clan to participate in clan room wars, share family tree badges, and chat in family channels.", color = TextSecondary, fontSize = 11.sp)
                                }
                            }
                        }

                        if (families.isEmpty()) {
                            item {
                                Box(
                                    modifier = Modifier.fillMaxWidth().padding(32.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text("No families created yet.", color = TextSecondary, fontSize = 13.sp)
                                }
                            }
                        } else {
                            items(families) { fam ->
                                GlassCard(modifier = Modifier.fillMaxWidth()) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            AsyncImage(
                                                model = fam.logoUrl,
                                                contentDescription = null,
                                                modifier = Modifier.size(46.dp).clip(RoundedCornerShape(10.dp))
                                            )
                                            Spacer(modifier = Modifier.width(10.dp))
                                            Column {
                                                Text(text = fam.name, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                                Text(text = "Leader: ${fam.leaderName} • Lv ${fam.level}", color = TextSecondary, fontSize = 11.sp)
                                                Text(text = "${fam.memberCount} Members • ${fam.score} Score", color = EmeraldGreen, fontSize = 10.sp)
                                            }
                                        }

                                        Button(
                                            onClick = { Toast.makeText(context, "Join request sent to ${fam.name}!", Toast.LENGTH_SHORT).show() },
                                            colors = ButtonDefaults.buttonColors(containerColor = ElectricBlue),
                                            shape = RoundedCornerShape(10.dp),
                                            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 2.dp),
                                            modifier = Modifier.height(30.dp)
                                        ) {
                                            Text("Join", color = Color.Black, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
                2 -> {
                    // CP Love Space Tab
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 14.dp, vertical = 8.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        GlassCard(modifier = Modifier.fillMaxWidth()) {
                            Column(
                                modifier = Modifier.fillMaxWidth().padding(12.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text("💖 CP (Couple) Love Space", color = CharmPink, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                                Spacer(modifier = Modifier.height(6.dp))
                                Text(
                                    text = "Bind a romantic or best-friend CP relationship. Unlock shared glowing heart frames, ring badges, and sweet interactive room entrances.",
                                    color = TextSecondary,
                                    fontSize = 11.sp,
                                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                                )

                                Spacer(modifier = Modifier.height(20.dp))

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.Center,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    AvatarWithFrame(avatarUrl = currentUser?.avatarUrl, size = 64.dp, vipLevel = currentUser?.vipLevel ?: 0)
                                    Spacer(modifier = Modifier.width(16.dp))
                                    Text("💖", fontSize = 28.sp)
                                    Spacer(modifier = Modifier.width(16.dp))
                                    Box(
                                        modifier = Modifier
                                            .size(64.dp)
                                            .clip(CircleShape)
                                            .background(SurfaceCard)
                                            .clickable {
                                                Toast.makeText(context, "Select a friend to propose CP!", Toast.LENGTH_SHORT).show()
                                            },
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(Icons.Default.Add, contentDescription = "Add Partner", tint = CharmPink)
                                    }
                                }

                                Spacer(modifier = Modifier.height(18.dp))

                                Text("Status: Looking for CP Partner", color = TextMuted, fontSize = 12.sp)

                                Spacer(modifier = Modifier.height(16.dp))

                                NeonButton(
                                    text = "Send CP Ring Proposal 💍",
                                    onClick = {
                                        Toast.makeText(context, "Proposal sent to chosen partner! 💖", Toast.LENGTH_SHORT).show()
                                    },
                                    brush = Brush.horizontalGradient(listOf(CharmRose, NeonPinkLight)),
                                    modifier = Modifier.fillMaxWidth(0.85f)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
