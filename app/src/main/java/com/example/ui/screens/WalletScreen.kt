package com.example.ui.screens

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import com.example.data.model.WalletTransaction
import com.example.data.repository.BismaRepository
import com.example.ui.components.*
import com.example.ui.theme.*
import kotlinx.coroutines.launch

@Composable
fun WalletScreen(
    repository: BismaRepository,
    onBack: () -> Unit
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val currentUser by repository.currentUser.collectAsState(initial = null)
    val transactions by repository.getTransactions().collectAsState(initial = emptyList())

    var selectedTab by remember { mutableIntStateOf(0) } // 0: Recharge Coins, 1: Diamond Exchange, 2: History

    val rechargePackages = listOf(
        Pair(1000L, "$0.99"),
        Pair(5500L, "$4.99"),
        Pair(12000L, "$9.99"),
        Pair(30000L, "$24.99"),
        Pair(70000L, "$49.99"),
        Pair(150000L, "$99.99")
    )

    var exchangeAmountText by remember { mutableStateOf("10") }

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
                IconButton(onClick = onBack, modifier = Modifier.size(36.dp)) {
                    Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = Color.White, modifier = Modifier.size(20.dp))
                }
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = "My Wallet & Balance",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }

            // Balance Overview Card
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 14.dp, vertical = 4.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(
                        Brush.horizontalGradient(
                            listOf(Color(0xFF4A154B), Color(0xFF2A0845), Color(0xFF0F041A))
                        )
                    )
                    .border(1.2.dp, GoldAmber.copy(alpha = 0.6f), RoundedCornerShape(16.dp))
                    .padding(14.dp)
            ) {
                Column {
                    Text("Total Assets", color = TextSecondary, fontSize = 11.sp)
                    Spacer(modifier = Modifier.height(6.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text("🪙", fontSize = 20.sp)
                                Spacer(modifier = Modifier.width(5.dp))
                                Text(
                                    text = "${currentUser?.coins ?: 0}",
                                    fontSize = 20.sp,
                                    fontWeight = FontWeight.Black,
                                    color = GoldYellow
                                )
                            }
                            Text("Gold Coins Balance", color = TextSecondary, fontSize = 10.sp)
                        }

                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text("💎", fontSize = 20.sp)
                                Spacer(modifier = Modifier.width(5.dp))
                                Text(
                                    text = "${currentUser?.diamonds ?: 0}",
                                    fontSize = 20.sp,
                                    fontWeight = FontWeight.Black,
                                    color = BrightCyan
                                )
                            }
                            Text("Diamonds Earned", color = TextSecondary, fontSize = 10.sp)
                        }
                    }
                }
            }

            // Tabs
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 14.dp, vertical = 6.dp),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                FilterChip(
                    selected = selectedTab == 0,
                    onClick = { selectedTab = 0 },
                    label = { Text("🪙 Recharge", fontSize = 11.5.sp) }
                )
                FilterChip(
                    selected = selectedTab == 1,
                    onClick = { selectedTab = 1 },
                    label = { Text("💎 Exchange", fontSize = 11.5.sp) }
                )
                FilterChip(
                    selected = selectedTab == 2,
                    onClick = { selectedTab = 2 },
                    label = { Text("📜 History", fontSize = 11.5.sp) }
                )
            }

            // Tab Content
            when (selectedTab) {
                0 -> {
                    // Recharge Packages Grid
                    LazyColumn(
                        modifier = Modifier.fillMaxSize().padding(horizontal = 14.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                        contentPadding = PaddingValues(bottom = 30.dp)
                    ) {
                        item {
                            Text("Select Coin Top-up Package:", color = TextSecondary, fontSize = 12.sp, modifier = Modifier.padding(bottom = 6.dp))
                        }
                        items(rechargePackages) { (coins, price) ->
                            GlassCard(modifier = Modifier.fillMaxWidth()) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Text("🪙", fontSize = 22.sp)
                                        Spacer(modifier = Modifier.width(10.dp))
                                        Column {
                                            Text(text = "$coins Gold Coins", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                            Text(text = "Instant credit to Bisma Account", color = TextSecondary, fontSize = 9.5.sp)
                                        }
                                    }

                                    Button(
                                        onClick = {
                                            coroutineScope.launch {
                                                repository.rechargeCoins(coins, price)
                                                Toast.makeText(context, "Successfully recharged $coins Coins! 🎉", Toast.LENGTH_SHORT).show()
                                            }
                                        },
                                        colors = ButtonDefaults.buttonColors(containerColor = GoldAmber),
                                        shape = RoundedCornerShape(10.dp),
                                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp),
                                        modifier = Modifier.height(34.dp)
                                    ) {
                                        Text(text = price, color = Color.Black, fontWeight = FontWeight.Black, fontSize = 12.sp)
                                    }
                                }
                            }
                        }
                    }
                }
                1 -> {
                    // Diamond Exchange to Coins
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 14.dp, vertical = 12.dp)
                    ) {
                        GlassCard(modifier = Modifier.fillMaxWidth()) {
                            Column(modifier = Modifier.fillMaxWidth()) {
                                Text("Exchange Diamonds for Gold Coins", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                                Text("Rate: 1 Diamond = 10 Gold Coins", color = EmeraldGreen, fontSize = 12.sp)
                                Spacer(modifier = Modifier.height(14.dp))

                                OutlinedTextField(
                                    value = exchangeAmountText,
                                    onValueChange = { exchangeAmountText = it },
                                    label = { Text("Diamonds to exchange") },
                                    modifier = Modifier.fillMaxWidth(),
                                    colors = OutlinedTextFieldDefaults.colors(
                                        focusedBorderColor = BrightCyan,
                                        unfocusedBorderColor = SurfaceCardBorder,
                                        focusedTextColor = TextPrimary,
                                        unfocusedTextColor = TextPrimary
                                    )
                                )

                                val amount = exchangeAmountText.toLongOrNull() ?: 0L
                                val willReceive = amount * 10

                                Spacer(modifier = Modifier.height(8.dp))
                                Text("You will receive: 🪙 $willReceive Coins", color = GoldYellow, fontWeight = FontWeight.Bold, fontSize = 13.sp)

                                Spacer(modifier = Modifier.height(14.dp))

                                NeonButton(
                                    text = "Confirm Exchange",
                                    onClick = {
                                        coroutineScope.launch {
                                            val success = repository.exchangeDiamondsToCoins(amount)
                                            if (success) {
                                                Toast.makeText(context, "Exchanged $amount Diamonds for $willReceive Coins!", Toast.LENGTH_SHORT).show()
                                            } else {
                                                Toast.makeText(context, "Insufficient Diamonds balance!", Toast.LENGTH_SHORT).show()
                                            }
                                        }
                                    },
                                    brush = Brush.horizontalGradient(listOf(BrightCyan, ElectricBlue)),
                                    modifier = Modifier.fillMaxWidth()
                                )
                            }
                        }
                    }
                }
                2 -> {
                    // Transactions History
                    if (transactions.isEmpty()) {
                        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            Text("No wallet transactions recorded yet", color = TextSecondary)
                        }
                    } else {
                        LazyColumn(
                            modifier = Modifier.fillMaxSize().padding(horizontal = 14.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp),
                            contentPadding = PaddingValues(bottom = 30.dp)
                        ) {
                            items(transactions) { tx ->
                                GlassCard(modifier = Modifier.fillMaxWidth()) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Column {
                                            Text(text = tx.type, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                            Text(text = tx.description, color = TextSecondary, fontSize = 11.sp)
                                        }
                                        Text(
                                            text = if (tx.amountCoins >= 0) "+${tx.amountCoins} 🪙" else "${tx.amountCoins} 🪙",
                                            color = if (tx.amountCoins >= 0) EmeraldGreen else DarkRed,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 14.sp
                                        )
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
