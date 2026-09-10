package com.example.ui.screens.owner

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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.RechargePackage
import com.example.data.model.User
import com.example.data.model.WithdrawalRequest

data class VirtualGiftItem(
    val id: String,
    val name: String,
    val icon: String,
    val coinPrice: Long,
    val charmPoints: Long,
    val type: String
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OwnerEconomyFinanceView(
    allUsers: List<User>,
    withdrawals: List<WithdrawalRequest>,
    packages: List<RechargePackage>,
    onAdjustCoins: (userId: String, deltaCoins: Long, reason: String) -> Unit,
    onHandleWithdrawal: (requestId: String, action: String, notes: String) -> Unit
) {
    var activeSubTab by remember { mutableStateOf(0) } // 0: Coins & Adjustment, 1: Withdrawals, 2: Gifts & Packages

    // Coin Adjustment Dialog / Form
    var adjustUserId by remember { mutableStateOf("") }
    var adjustAmount by remember { mutableStateOf("") }
    var isAddingCoins by remember { mutableStateOf(true) }
    var adjustReason by remember { mutableStateOf("") }
    var showAdjustConfirmDialog by remember { mutableStateOf(false) }

    // Withdrawal Status Filter
    var withdrawalFilter by remember { mutableStateOf("Pending") }

    val mockGifts = remember {
        listOf(
            VirtualGiftItem("gift_1", "Red Rose", "🌹", 10, 10, "2D"),
            VirtualGiftItem("gift_2", "Love Heart", "💖", 50, 50, "2D"),
            VirtualGiftItem("gift_3", "Sports Car", "🏎️", 2000, 2000, "SVGA"),
            VirtualGiftItem("gift_4", "Luxury Yacht", "🛥️", 5000, 5000, "SVGA"),
            VirtualGiftItem("gift_5", "Fantasy Castle", "🏰", 20000, 20000, "3D Full Screen"),
            VirtualGiftItem("gift_6", "AURA Rocket", "🚀", 50000, 50000, "3D Full Screen")
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
    ) {
        // Sub tabs
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 10.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(Color(0xFF131D2F))
                .padding(4.dp),
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            listOf("🪙 Coin Operations", "💸 Cashout Requests", "🎁 Gifts & Packages").forEachIndexed { index, label ->
                val isSelected = activeSubTab == index
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(10.dp))
                        .background(if (isSelected) Color(0xFFFFD700) else Color.Transparent)
                        .clickable { activeSubTab = index }
                        .padding(vertical = 8.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = label,
                        color = if (isSelected) Color.Black else Color.White.copy(alpha = 0.7f),
                        fontSize = 11.sp,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                    )
                }
            }
        }

        when (activeSubTab) {
            // TAB 0: COINS & ADJUSTMENT
            0 -> {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(14.dp),
                    contentPadding = PaddingValues(bottom = 32.dp)
                ) {
                    item {
                        Surface(
                            shape = RoundedCornerShape(16.dp),
                            color = Color(0xFF131D31),
                            border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFFFD700).copy(alpha = 0.4f)),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Default.MonetizationOn, contentDescription = null, tint = Color(0xFFFFD700), modifier = Modifier.size(26.dp))
                                    Spacer(modifier = Modifier.width(10.dp))
                                    Column {
                                        Text("Manual Coin Adjustment (Owner Only)", color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                                        Text("Direct balance credit/debit with mandatory audit log", color = Color.White.copy(alpha = 0.6f), fontSize = 11.sp)
                                    }
                                }

                                Spacer(modifier = Modifier.height(14.dp))

                                OutlinedTextField(
                                    value = adjustUserId,
                                    onValueChange = { adjustUserId = it },
                                    label = { Text("Target User ID") },
                                    placeholder = { Text("e.g. 565656565666555", color = Color.Gray) },
                                    modifier = Modifier.fillMaxWidth().testTag("adjust_coins_user_id")
                                )

                                Spacer(modifier = Modifier.height(8.dp))

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    // Add or Deduct Toggle
                                    Row(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(8.dp))
                                            .background(Color(0xFF1E293B))
                                            .padding(2.dp)
                                    ) {
                                        listOf(true to "+ Add", false to "- Deduct").forEach { (isAdd, lbl) ->
                                            Box(
                                                modifier = Modifier
                                                    .clip(RoundedCornerShape(6.dp))
                                                    .background(if (isAddingCoins == isAdd) (if (isAdd) Color(0xFF00E676) else Color(0xFFFF5252)) else Color.Transparent)
                                                    .clickable { isAddingCoins = isAdd }
                                                    .padding(horizontal = 12.dp, vertical = 8.dp)
                                            ) {
                                                Text(lbl, color = if (isAddingCoins == isAdd) Color.Black else Color.White, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                                            }
                                        }
                                    }

                                    OutlinedTextField(
                                        value = adjustAmount,
                                        onValueChange = { adjustAmount = it },
                                        label = { Text("Coin Amount") },
                                        modifier = Modifier.weight(1f).testTag("adjust_coins_amount")
                                    )
                                }

                                Spacer(modifier = Modifier.height(8.dp))

                                OutlinedTextField(
                                    value = adjustReason,
                                    onValueChange = { adjustReason = it },
                                    label = { Text("Mandatory Owner Authorization Reason") },
                                    placeholder = { Text("e.g. Event top-up reward, VIP compensation", color = Color.Gray) },
                                    modifier = Modifier.fillMaxWidth().testTag("adjust_coins_reason")
                                )

                                Spacer(modifier = Modifier.height(14.dp))

                                Button(
                                    onClick = { showAdjustConfirmDialog = true },
                                    enabled = adjustUserId.isNotBlank() && adjustAmount.toLongOrNull() != null && adjustReason.isNotBlank(),
                                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFFD700)),
                                    shape = RoundedCornerShape(10.dp),
                                    modifier = Modifier.fillMaxWidth().testTag("submit_coin_adjust_btn")
                                ) {
                                    Text("Authorize & Apply Adjustment 🪙", color = Color.Black, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }

                    // Economic Summary
                    item {
                        Surface(
                            shape = RoundedCornerShape(14.dp),
                            color = Color(0xFF131C2E),
                            border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF223250)),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(14.dp)) {
                                Text("AURA Platform Monetary Settings", color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                                Spacer(modifier = Modifier.height(8.dp))
                                Text("• Exchange Rate: $1.00 USD = 28,000 Coins / 2,800 Diamonds", color = Color.White.copy(alpha = 0.8f), fontSize = 12.sp)
                                Text("• Agency Commission Tier: 15% Platform, 85% Host & Agency Share", color = Color.White.copy(alpha = 0.8f), fontSize = 12.sp)
                                Text("• Minimum Withdrawal Threshold: 10,000 💎 ($10.00 USD)", color = Color.White.copy(alpha = 0.8f), fontSize = 12.sp)
                            }
                        }
                    }
                }
            }

            // TAB 1: WITHDRAWALS
            1 -> {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(bottom = 10.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Host Cashouts (${withdrawals.size})", color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.Bold)

                    Row(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(Color(0xFF1E293B))
                            .padding(2.dp),
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        listOf("Pending", "Approved", "All").forEach { filter ->
                            val isSel = withdrawalFilter == filter
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(if (isSel) Color(0xFF00E5FF) else Color.Transparent)
                                    .clickable { withdrawalFilter = filter }
                                    .padding(horizontal = 8.dp, vertical = 4.dp)
                            ) {
                                Text(filter, color = if (isSel) Color.Black else Color.White.copy(alpha = 0.7f), fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }

                val filteredWithdrawals = remember(withdrawals, withdrawalFilter) {
                    when (withdrawalFilter) {
                        "Pending" -> withdrawals.filter { it.status.equals("Pending", ignoreCase = true) }
                        "Approved" -> withdrawals.filter { it.status.equals("Approved", ignoreCase = true) }
                        else -> withdrawals
                    }
                }

                if (filteredWithdrawals.isEmpty()) {
                    Box(modifier = Modifier.fillMaxWidth().padding(top = 40.dp), contentAlignment = Alignment.Center) {
                        Text("No withdrawals in this filter.", color = Color.White.copy(alpha = 0.5f), fontSize = 13.sp)
                    }
                } else {
                    LazyColumn(
                        verticalArrangement = Arrangement.spacedBy(10.dp),
                        contentPadding = PaddingValues(bottom = 32.dp)
                    ) {
                        items(filteredWithdrawals, key = { it.id }) { req ->
                            Surface(
                                shape = RoundedCornerShape(14.dp),
                                color = Color(0xFF131C2E),
                                border = androidx.compose.foundation.BorderStroke(
                                    1.dp,
                                    when (req.status.lowercase()) {
                                        "approved" -> Color(0xFF00E676).copy(alpha = 0.4f)
                                        "rejected" -> Color(0xFFFF5252).copy(alpha = 0.4f)
                                        else -> Color(0xFFFFAB00).copy(alpha = 0.4f)
                                    }
                                ),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Column(modifier = Modifier.padding(14.dp)) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Column {
                                            Text(req.userName.ifBlank { "User ${req.userId}" }, color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                                            Text("ID: ${req.userId} • Method: ${req.paymentMethod}", color = Color.White.copy(alpha = 0.6f), fontSize = 11.sp)
                                        }
                                        Surface(
                                            shape = RoundedCornerShape(6.dp),
                                            color = when (req.status.lowercase()) {
                                                "approved" -> Color(0xFF00E676).copy(alpha = 0.15f)
                                                "rejected" -> Color(0xFFFF5252).copy(alpha = 0.15f)
                                                else -> Color(0xFFFFAB00).copy(alpha = 0.15f)
                                            }
                                        ) {
                                            Text(
                                                text = req.status,
                                                color = when (req.status.lowercase()) {
                                                    "approved" -> Color(0xFF00E676)
                                                    "rejected" -> Color(0xFFFF5252)
                                                    else -> Color(0xFFFFAB00)
                                                },
                                                fontSize = 11.sp,
                                                fontWeight = FontWeight.Bold,
                                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                                            )
                                        }
                                    }

                                    Spacer(modifier = Modifier.height(8.dp))
                                    Text("Account: ${req.accountNumber} (${req.accountTitle})", color = Color(0xFF00E5FF), fontSize = 12.sp)
                                    Text("Amount: %,d 💎 ($%,.2f USD)".format(req.diamondAmount, req.usdAmount), color = Color(0xFFFFD700), fontSize = 13.sp, fontWeight = FontWeight.Bold)

                                    if (req.status.equals("Pending", ignoreCase = true)) {
                                        Spacer(modifier = Modifier.height(10.dp))
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                                        ) {
                                            Button(
                                                onClick = { onHandleWithdrawal(req.id, "APPROVE", "Processed by Owner") },
                                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF00E676)),
                                                shape = RoundedCornerShape(8.dp),
                                                modifier = Modifier.weight(1f).testTag("approve_cashout_${req.id}")
                                            ) {
                                                Text("Approve Payment", color = Color.Black, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                            }

                                            Button(
                                                onClick = { onHandleWithdrawal(req.id, "REJECT", "Rejected & refunded by Owner") },
                                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF5252)),
                                                shape = RoundedCornerShape(8.dp),
                                                modifier = Modifier.weight(1f).testTag("reject_cashout_${req.id}")
                                            ) {
                                                Text("Reject & Refund", color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // TAB 2: GIFTS & PACKAGES
            2 -> {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(14.dp),
                    contentPadding = PaddingValues(bottom = 32.dp)
                ) {
                    item {
                        Text("Active Virtual Gifts Gallery", color = Color.White, fontSize = 15.sp, fontWeight = FontWeight.Bold)
                    }

                    items(mockGifts) { gift ->
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = Color(0xFF131C2E),
                            border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF223250)),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier.padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(gift.icon, fontSize = 28.sp)
                                Spacer(modifier = Modifier.width(12.dp))
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(gift.name, color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                                    Text("Animation: ${gift.type}", color = Color.White.copy(alpha = 0.6f), fontSize = 11.sp)
                                }
                                Column(horizontalAlignment = Alignment.End) {
                                    Text("%,d 🪙".format(gift.coinPrice), color = Color(0xFFFFD700), fontSize = 13.sp, fontWeight = FontWeight.Bold)
                                    Text("+${gift.charmPoints} Charm", color = Color(0xFFFF2A85), fontSize = 10.sp)
                                }
                            }
                        }
                    }

                    item {
                        Text("Store Recharge Packages (${packages.size})", color = Color.White, fontSize = 15.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(top = 10.dp))
                    }

                    items(packages) { pkg ->
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = Color(0xFF131C2E),
                            border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF223250)),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier.padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(Icons.Default.ShoppingBag, contentDescription = null, tint = Color(0xFF00E5FF))
                                Spacer(modifier = Modifier.width(12.dp))
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(pkg.label.ifBlank { "%,d Coins Pack".format(pkg.coins) }, color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                                    Text("%,d Coins + %,d Bonus".format(pkg.coins, pkg.bonusCoins), color = Color(0xFFFFD700), fontSize = 11.sp)
                                }
                                Surface(shape = RoundedCornerShape(6.dp), color = Color(0xFF00E5FF).copy(alpha = 0.2f)) {
                                    Text("$${pkg.priceUsd}", color = Color(0xFF00E5FF), fontSize = 12.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp))
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    // Confirm Coin Adjustment Dialog
    if (showAdjustConfirmDialog) {
        val delta = adjustAmount.toLongOrNull() ?: 0L
        val finalDelta = if (isAddingCoins) delta else -delta
        val targetUser = allUsers.find { it.id == adjustUserId.trim() }

        AlertDialog(
            onDismissRequest = { showAdjustConfirmDialog = false },
            title = { Text("Confirm Coin Adjustment", fontWeight = FontWeight.Bold, color = Color(0xFFFFD700)) },
            text = {
                Column {
                    Text("Target: ${targetUser?.username ?: adjustUserId} (${adjustUserId})", color = Color.White)
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "Amount: ${if (isAddingCoins) "+$delta" else "-$delta"} Coins",
                        color = if (isAddingCoins) Color(0xFF00E676) else Color(0xFFFF5252),
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text("Reason: $adjustReason", color = Color.White.copy(alpha = 0.7f), fontSize = 12.sp)
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        onAdjustCoins(adjustUserId.trim(), finalDelta, adjustReason)
                        showAdjustConfirmDialog = false
                        adjustAmount = ""
                        adjustReason = ""
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFFD700))
                ) {
                    Text("Confirm 🪙", color = Color.Black, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showAdjustConfirmDialog = false }) { Text("Cancel", color = Color.Gray) }
            },
            containerColor = Color(0xFF162238)
        )
    }
}
