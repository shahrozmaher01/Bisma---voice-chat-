package com.example.ui.screens

import android.widget.Toast
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.repository.BismaRepository
import com.example.ui.components.*
import com.example.ui.theme.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun GamesScreen(
    repository: BismaRepository,
    onBack: () -> Unit
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val currentUser by repository.currentUser.collectAsState(initial = null)

    var reels by remember { mutableStateOf(listOf(7, 7, 7)) }
    var isSpinning by remember { mutableStateOf(false) }
    var selectedBet by remember { mutableLongStateOf(100L) }
    var lastResultText by remember { mutableStateOf("Spin the Lucky 77 reels to win up to 77x Coins!") }

    val betOptions = listOf(50L, 100L, 500L, 1000L, 5000L)

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
            // Header
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
                    text = "Lucky 77 Slots & Arcade 🎰",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 10.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // User Balance
                GlassCard(modifier = Modifier.fillMaxWidth()) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Player Coin Balance:", color = TextSecondary, fontSize = 13.sp)
                        Text("🪙 ${currentUser?.coins ?: 0} Coins", color = GoldYellow, fontWeight = FontWeight.Black, fontSize = 16.sp)
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Slot Machine Visual Cabinet
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(24.dp))
                        .background(
                            Brush.verticalGradient(
                                listOf(Color(0xFF6B0F3F), Color(0xFF2A0845), Color(0xFF140326))
                            )
                        )
                        .border(3.dp, GoldYellow, RoundedCornerShape(24.dp))
                        .padding(20.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "⭐ LUCKY 77 ⭐",
                            color = GoldYellow,
                            fontWeight = FontWeight.Black,
                            fontSize = 22.sp,
                            letterSpacing = 2.sp
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        // 3 Slot Reels
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceEvenly
                        ) {
                            reels.forEach { num ->
                                Box(
                                    modifier = Modifier
                                        .size(74.dp)
                                        .clip(RoundedCornerShape(16.dp))
                                        .background(Color(0xFF0F041A))
                                        .border(2.dp, NeonPink, RoundedCornerShape(16.dp)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    val emoji = when (num) {
                                        7 -> "7️⃣"
                                        6 -> "👑"
                                        5 -> "💎"
                                        4 -> "🍒"
                                        3 -> "🔔"
                                        2 -> "⭐"
                                        else -> "🍀"
                                    }
                                    Text(text = emoji, fontSize = 34.sp)
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        Text(
                            text = lastResultText,
                            color = if (lastResultText.contains("Won") || lastResultText.contains("JACKPOT")) GoldYellow else Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp,
                            textAlign = TextAlign.Center
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Bet selector
                Text("Select Bet Amount (Coins):", color = TextSecondary, fontSize = 12.sp)
                Spacer(modifier = Modifier.height(6.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    betOptions.forEach { bet ->
                        FilterChip(
                            selected = selectedBet == bet,
                            onClick = { selectedBet = bet },
                            label = { Text("🪙 $bet") }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Spin Button
                NeonButton(
                    text = if (isSpinning) "Spinning..." else "SPIN (🪙 $selectedBet)",
                    enabled = !isSpinning,
                    onClick = {
                        if ((currentUser?.coins ?: 0) < selectedBet) {
                            Toast.makeText(context, "Insufficient coins! Please recharge in Wallet.", Toast.LENGTH_SHORT).show()
                            return@NeonButton
                        }
                        isSpinning = true
                        coroutineScope.launch {
                            // Slot reel animation effect
                            for (i in 0..6) {
                                reels = listOf((1..7).random(), (1..7).random(), (1..7).random())
                                delay(90)
                            }
                            val (finalReels, win, msg) = repository.playLucky77(selectedBet)
                            reels = finalReels
                            lastResultText = msg
                            isSpinning = false
                        }
                    },
                    modifier = Modifier.fillMaxWidth(0.85f)
                )
            }
        }
    }
}
