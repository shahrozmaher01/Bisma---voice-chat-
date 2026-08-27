package com.example.ui.screens

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.repository.BismaRepository
import com.example.ui.components.*
import com.example.ui.theme.*
import kotlinx.coroutines.launch

@Composable
fun AdminPanelScreen(
    repository: BismaRepository,
    onBack: () -> Unit
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val currentUser by repository.currentUser.collectAsState(initial = null)

    var addCoinsText by remember { mutableStateOf("10000") }
    var selectedVipLevel by remember { mutableIntStateOf(currentUser?.vipLevel ?: 2) }

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
                    text = "Bisma Admin & Moderation Panel 🛠️",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = GoldYellow
                )
            }

            LazyColumn(
                modifier = Modifier.fillMaxSize().padding(horizontal = 14.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                contentPadding = PaddingValues(bottom = 30.dp)
            ) {
                item {
                    GlassCard(modifier = Modifier.fillMaxWidth()) {
                        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                            Text("Account Privileges & VIP Tier", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                            Text("Current User: ${currentUser?.username} (ID: ${currentUser?.id})", color = TextSecondary, fontSize = 11.sp)

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                (0..5).forEach { vip ->
                                    FilterChip(
                                        selected = selectedVipLevel == vip,
                                        onClick = { selectedVipLevel = vip },
                                        label = { Text("VIP $vip") }
                                    )
                                }
                            }

                            NeonButton(
                                text = "Apply VIP Tier $selectedVipLevel",
                                onClick = {
                                    coroutineScope.launch {
                                        repository.setVipLevel(selectedVipLevel)
                                        Toast.makeText(context, "VIP updated to $selectedVipLevel!", Toast.LENGTH_SHORT).show()
                                    }
                                }
                            )
                        }
                    }
                }

                item {
                    GlassCard(modifier = Modifier.fillMaxWidth()) {
                        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                            Text("Quick Coin Credit System", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                            OutlinedTextField(
                                value = addCoinsText,
                                onValueChange = { addCoinsText = it },
                                label = { Text("Coins Amount") },
                                modifier = Modifier.fillMaxWidth(),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = GoldYellow,
                                    unfocusedBorderColor = SurfaceCardBorder,
                                    focusedTextColor = TextPrimary,
                                    unfocusedTextColor = TextPrimary
                                )
                            )

                            NeonButton(
                                text = "Credit Coins to Wallet",
                                onClick = {
                                    val amount = addCoinsText.toLongOrNull() ?: 0L
                                    coroutineScope.launch {
                                        repository.rechargeCoins(amount, "Admin Grant")
                                        Toast.makeText(context, "Credited $amount Coins to wallet! 🪙", Toast.LENGTH_SHORT).show()
                                    }
                                }
                            )
                        }
                    }
                }

                item {
                    GlassCard(modifier = Modifier.fillMaxWidth()) {
                        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            Text("Safety & Content Moderation Status", color = EmeraldGreen, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                            Text("• Real-time Audio Mic Filter: Active", color = TextSecondary, fontSize = 11.sp)
                            Text("• Automated Spam & Chat Filter: Active", color = TextSecondary, fontSize = 11.sp)
                            Text("• User Report Queue: 0 Pending Flags", color = TextSecondary, fontSize = 11.sp)
                        }
                    }
                }
            }
        }
    }
}
