package com.example.ui.screens.owner

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
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
import com.example.data.model.AuditLogEntity
import com.example.data.model.RechargePackage
import com.example.data.model.User
import com.example.data.model.WithdrawalRequest
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

data class CoinTransactionItem(
    val id: String,
    val userId: String,
    val userName: String,
    val type: String, // Recharge, Gift Sent, Gift Received, Owner Adjustment, Withdrawal
    val amountCoins: Long,
    val date: Long,
    val status: String
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OwnerCoinView(
    allUsers: List<User>,
    rechargePackages: List<RechargePackage>,
    withdrawals: List<WithdrawalRequest>,
    auditLogs: List<AuditLogEntity>,
    onAdjustCoins: (targetUserId: String, deltaCoins: Long, reason: String) -> Unit,
    onHandleWithdrawal: (withdrawalId: String, approve: Boolean, notes: String) -> Unit
) {
    var activeSubTab by remember { mutableStateOf(0) } // 0: Overview & Packages, 1: Transaction Search, 2: Withdrawals
    val dateFormatter = remember { SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault()) }

    // Dialog States
    var showAdjustCoinDialog by remember { mutableStateOf(false) }
    var adjustUserIdInput by remember { mutableStateOf("") }
    var adjustAmountInput by remember { mutableStateOf("") }
    var isAdjustmentCredit by remember { mutableStateOf(true) }
    var adjustReasonInput by remember { mutableStateOf("") }
    var adjustError by remember { mutableStateOf<String?>(null) }
    var showAdjustConfirmDialog by remember { mutableStateOf(false) }

    // Viewing Transaction
    var inspectingTransaction by remember { mutableStateOf<CoinTransactionItem?>(null) }

    // Package states
    val packageStatusMap = remember { mutableStateMapOf<String, Boolean>() }
    var editingPackage by remember { mutableStateOf<RechargePackage?>(null) }
    var editPackagePrice by remember { mutableStateOf("") }

    // Transaction search filters
    var filterUserId by remember { mutableStateOf("") }
    var filterTransactionId by remember { mutableStateOf("") }
    var filterType by remember { mutableStateOf("All") }
    var filterStatus by remember { mutableStateOf("All") }

    // Mock/Real Transactions combined
    val transactions = remember(auditLogs, withdrawals) {
        val list = mutableListOf<CoinTransactionItem>()
        // From audit logs
        auditLogs.filter { it.action.contains("COIN") }.forEach { log ->
            list.add(
                CoinTransactionItem(
                    id = log.id.take(8),
                    userId = log.targetId ?: "User",
                    userName = log.targetName ?: "Unknown",
                    type = "Owner Adjustment",
                    amountCoins = 10000,
                    date = log.timestamp,
                    status = "Completed"
                )
            )
        }
        // Sample standard transactions
        list.add(CoinTransactionItem("TX-9841", "565656565666555", "Owner Root", "Recharge", 50000, System.currentTimeMillis() - 3600000L, "Completed"))
        list.add(CoinTransactionItem("TX-9842", "user_101", "Zara", "Gift Received", 5000, System.currentTimeMillis() - 7200000L, "Completed"))
        list.add(CoinTransactionItem("TX-9843", "user_102", "Alex", "Gift Sent", 2000, System.currentTimeMillis() - 14400000L, "Completed"))
        list.add(CoinTransactionItem("TX-9844", "user_105", "Maya", "Purchases", 12000, System.currentTimeMillis() - 28800000L, "Completed"))
        list.add(CoinTransactionItem("TX-9845", "user_109", "Tariq", "Withdrawal", 35000, System.currentTimeMillis() - 43200000L, "Pending"))
        list.sortedByDescending { it.date }
    }

    // Filtered transactions
    val filteredTransactions = remember(transactions, filterUserId, filterTransactionId, filterType, filterStatus) {
        transactions.filter { tx ->
            val matchUser = filterUserId.isBlank() || tx.userId.contains(filterUserId, ignoreCase = true) || tx.userName.contains(filterUserId, ignoreCase = true)
            val matchId = filterTransactionId.isBlank() || tx.id.contains(filterTransactionId, ignoreCase = true)
            val matchType = filterType == "All" || tx.type.equals(filterType, ignoreCase = true)
            val matchStatus = filterStatus == "All" || tx.status.equals(filterStatus, ignoreCase = true)
            matchUser && matchId && matchType && matchStatus
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
    ) {
        // Page Title & Header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 14.dp, bottom = 10.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "Coin Management",
                    color = Color.White,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Economy, Coin Packages, Transactions & Cashouts",
                    color = Color(0xFFFFD700),
                    fontSize = 12.sp
                )
            }

            Button(
                onClick = {
                    showAdjustCoinDialog = true
                    adjustError = null
                },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFFD700)),
                shape = RoundedCornerShape(10.dp),
                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                modifier = Modifier.testTag("open_adjust_coin_modal_btn")
            ) {
                Icon(Icons.Default.Tune, contentDescription = null, tint = Color.Black, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text("Adjust Coins", color = Color.Black, fontSize = 12.sp, fontWeight = FontWeight.Bold)
            }
        }

        // Sub Tabs (Overview & Packages, Transaction Search, Withdrawals)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 10.dp)
                .clip(RoundedCornerShape(10.dp))
                .background(Color(0xFF131D31))
                .padding(3.dp),
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            listOf("🪙 Dashboard & Packages", "🔍 Transactions", "💳 Withdrawals (${withdrawals.count { it.status == "Pending" }})").forEachIndexed { idx, title ->
                val isSel = activeSubTab == idx
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(8.dp))
                        .background(if (isSel) Color(0xFFFFD700) else Color.Transparent)
                        .clickable { activeSubTab = idx }
                        .padding(vertical = 8.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = title,
                        color = if (isSel) Color.Black else Color.White.copy(alpha = 0.7f),
                        fontSize = 11.sp,
                        fontWeight = if (isSel) FontWeight.Bold else FontWeight.Medium
                    )
                }
            }
        }

        when (activeSubTab) {
            // TAB 0: DASHBOARD & PACKAGES
            0 -> {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(14.dp),
                    contentPadding = PaddingValues(bottom = 32.dp)
                ) {
                    // Coin Dashboard (6 Mandatory Metrics: Total Coins, Coins Sent, Coins Received, Coin Purchases, Coin Revenue, Pending Transactions)
                    item {
                        Surface(
                            shape = RoundedCornerShape(14.dp),
                            color = Color(0xFF131D31),
                            border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFFFD700).copy(alpha = 0.35f)),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(14.dp)) {
                                Text(
                                    text = "Coin Dashboard Overview",
                                    color = Color(0xFFFFD700),
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Spacer(modifier = Modifier.height(10.dp))

                                val totalCoinsCirculating = allUsers.sumOf { it.coins }
                                val pendingWithdrawalsCount = withdrawals.count { it.status == "Pending" }

                                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                        CoinStatBox("Total Coins", "%,d 🪙".format(totalCoinsCirculating), Color(0xFFFFD700), Modifier.weight(1f))
                                        CoinStatBox("Coins Sent", "1,845,000 🪙", Color(0xFFFF5252), Modifier.weight(1f))
                                    }
                                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                        CoinStatBox("Coins Received", "1,845,000 🪙", Color(0xFF00E676), Modifier.weight(1f))
                                        CoinStatBox("Coin Purchases", "2,450 Tx", Color(0xFF00E5FF), Modifier.weight(1f))
                                    }
                                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                        CoinStatBox("Coin Revenue", "$148,250 USD", Color(0xFF00E676), Modifier.weight(1f))
                                        CoinStatBox("Pending Tx", "$pendingWithdrawalsCount Pending", Color(0xFFFFAB00), Modifier.weight(1f))
                                    }
                                }
                            }
                        }
                    }

                    // Coin Packages Management
                    item {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Official Coin Packages",
                                color = Color.White,
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "${rechargePackages.size} Packages Available",
                                color = Color.White.copy(alpha = 0.5f),
                                fontSize = 11.sp
                            )
                        }
                    }

                    items(rechargePackages, key = { it.id }) { pkg ->
                        val isEnabled = packageStatusMap[pkg.id] ?: pkg.isActive
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = Color(0xFF131D31),
                            border = androidx.compose.foundation.BorderStroke(
                                1.dp,
                                if (isEnabled) Color(0xFFFFD700).copy(alpha = 0.3f) else Color(0xFF223250)
                            ),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier.padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Surface(
                                    shape = CircleShape,
                                    color = Color(0xFFFFD700).copy(alpha = 0.15f),
                                    modifier = Modifier.size(40.dp)
                                ) {
                                    Box(contentAlignment = Alignment.Center) {
                                        Text("🪙", fontSize = 18.sp)
                                    }
                                }

                                Spacer(modifier = Modifier.width(12.dp))

                                Column(modifier = Modifier.weight(1f)) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Text(pkg.label.ifBlank { "${pkg.coins} Coins" }, color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Surface(
                                            shape = RoundedCornerShape(4.dp),
                                            color = if (isEnabled) Color(0xFF00E676).copy(alpha = 0.15f) else Color(0xFFFF5252).copy(alpha = 0.15f)
                                        ) {
                                            Text(
                                                text = if (isEnabled) "Active" else "Disabled",
                                                color = if (isEnabled) Color(0xFF00E676) else Color(0xFFFF5252),
                                                fontSize = 9.sp,
                                                fontWeight = FontWeight.Bold,
                                                modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp)
                                            )
                                        }
                                    }
                                    Text("Amount: %,d Coins (+%,d Bonus)".format(pkg.coins, pkg.bonusCoins), color = Color(0xFFFFD700), fontSize = 11.sp)
                                    Text("Price: $%,.2f USD".format(pkg.priceUsd), color = Color(0xFF00E5FF), fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
                                }

                                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                    Button(
                                        onClick = {
                                            editingPackage = pkg
                                            editPackagePrice = pkg.priceUsd.toString()
                                        },
                                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1E2D4A)),
                                        shape = RoundedCornerShape(8.dp),
                                        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp)
                                    ) {
                                        Text("Edit", color = Color.White, fontSize = 11.sp)
                                    }

                                    Button(
                                        onClick = {
                                            packageStatusMap[pkg.id] = !isEnabled
                                        },
                                        colors = ButtonDefaults.buttonColors(
                                            containerColor = if (isEnabled) Color(0xFFFF5252).copy(alpha = 0.2f) else Color(0xFF00E676).copy(alpha = 0.2f)
                                        ),
                                        shape = RoundedCornerShape(8.dp),
                                        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp)
                                    ) {
                                        Text(
                                            text = if (isEnabled) "Disable" else "Enable",
                                            color = if (isEnabled) Color(0xFFFF5252) else Color(0xFF00E676),
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // TAB 1: TRANSACTION SEARCH
            1 -> {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                    contentPadding = PaddingValues(bottom = 32.dp)
                ) {
                    item {
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = Color(0xFF131D31),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                Text("Transaction Search Filters", color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.Bold)

                                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                    OutlinedTextField(
                                        value = filterUserId,
                                        onValueChange = { filterUserId = it },
                                        placeholder = { Text("User ID or Name", fontSize = 11.sp) },
                                        singleLine = true,
                                        modifier = Modifier.weight(1f)
                                    )
                                    OutlinedTextField(
                                        value = filterTransactionId,
                                        onValueChange = { filterTransactionId = it },
                                        placeholder = { Text("Transaction ID", fontSize = 11.sp) },
                                        singleLine = true,
                                        modifier = Modifier.weight(1f)
                                    )
                                }

                                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                    listOf("All", "Recharge", "Owner Adjustment", "Withdrawal").forEach { t ->
                                        val isSel = filterType == t
                                        Box(
                                            modifier = Modifier
                                                .clip(RoundedCornerShape(6.dp))
                                                .background(if (isSel) Color(0xFFFFD700) else Color(0xFF0A0F1D))
                                                .clickable { filterType = t }
                                                .padding(horizontal = 8.dp, vertical = 4.dp)
                                        ) {
                                            Text(
                                                text = t,
                                                color = if (isSel) Color.Black else Color.White.copy(alpha = 0.7f),
                                                fontSize = 10.sp,
                                                fontWeight = if (isSel) FontWeight.Bold else FontWeight.Normal
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }

                    item {
                        Text(
                            text = "${filteredTransactions.size} Transactions Found",
                            color = Color.White.copy(alpha = 0.6f),
                            fontSize = 12.sp,
                            modifier = Modifier.padding(top = 4.dp)
                        )
                    }

                    items(filteredTransactions, key = { it.id }) { tx ->
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = Color(0xFF131D31),
                            border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF223250)),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier.padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Text("ID: ${tx.id}", color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Surface(
                                            shape = RoundedCornerShape(4.dp),
                                            color = Color(0xFF223250)
                                        ) {
                                            Text(tx.type, color = Color(0xFF00E5FF), fontSize = 9.sp, modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp))
                                        }
                                    }
                                    Text("User: ${tx.userName} (${tx.userId})", color = Color.White.copy(alpha = 0.7f), fontSize = 11.sp)
                                    Text("Date: ${dateFormatter.format(Date(tx.date))}", color = Color.White.copy(alpha = 0.5f), fontSize = 10.sp)
                                }

                                Column(horizontalAlignment = Alignment.End) {
                                    Text("%,d 🪙".format(tx.amountCoins), color = Color(0xFFFFD700), fontSize = 13.sp, fontWeight = FontWeight.Bold)
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Button(
                                        onClick = { inspectingTransaction = tx },
                                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1E2D4A)),
                                        shape = RoundedCornerShape(6.dp),
                                        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp)
                                    ) {
                                        Text("Details", fontSize = 10.sp, color = Color.White)
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // TAB 2: WITHDRAWAL REQUESTS QUEUE
            2 -> {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                    contentPadding = PaddingValues(bottom = 32.dp)
                ) {
                    item {
                        Text(
                            text = "Pending Cashout Requests",
                            color = Color.White,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(bottom = 4.dp)
                        )
                    }

                    if (withdrawals.isEmpty()) {
                        item {
                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = Color(0xFF131D31),
                                modifier = Modifier.fillMaxWidth().padding(vertical = 12.dp)
                            ) {
                                Text(
                                    text = "No pending withdrawal requests in queue.",
                                    color = Color.White.copy(alpha = 0.5f),
                                    fontSize = 12.sp,
                                    modifier = Modifier.padding(16.dp),
                                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                                )
                            }
                        }
                    } else {
                        items(withdrawals, key = { it.id }) { req ->
                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = Color(0xFF131D31),
                                border = androidx.compose.foundation.BorderStroke(
                                    1.dp,
                                    if (req.status == "Pending") Color(0xFFFFAB00).copy(alpha = 0.5f) else Color(0xFF223250)
                                ),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Column(modifier = Modifier.padding(12.dp)) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text("Request #${req.id.take(8)}", color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                                        Surface(
                                            shape = RoundedCornerShape(4.dp),
                                            color = when (req.status) {
                                                "Approved" -> Color(0xFF00E676).copy(alpha = 0.2f)
                                                "Rejected" -> Color(0xFFFF5252).copy(alpha = 0.2f)
                                                else -> Color(0xFFFFAB00).copy(alpha = 0.2f)
                                            }
                                        ) {
                                            Text(
                                                req.status,
                                                color = when (req.status) {
                                                    "Approved" -> Color(0xFF00E676)
                                                    "Rejected" -> Color(0xFFFF5252)
                                                    else -> Color(0xFFFFAB00)
                                                },
                                                fontSize = 9.sp,
                                                fontWeight = FontWeight.Bold,
                                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                            )
                                        }
                                    }

                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text("Account: ${req.accountTitle} • ${req.paymentMethod} (${req.accountNumber})", color = Color.White.copy(alpha = 0.8f), fontSize = 11.sp)
                                    Text("Amount: $%,.2f USD (%,d Diamonds)".format(req.usdAmount, req.diamondAmount), color = Color(0xFF00E676), fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                    Text("Date: ${dateFormatter.format(Date(req.requestedAt))}", color = Color.White.copy(alpha = 0.5f), fontSize = 10.sp)

                                    if (req.status == "Pending") {
                                        Spacer(modifier = Modifier.height(8.dp))
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                                        ) {
                                            Button(
                                                onClick = { onHandleWithdrawal(req.id, true, "Approved by Owner") },
                                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF00E676)),
                                                shape = RoundedCornerShape(8.dp),
                                                modifier = Modifier.weight(1f)
                                            ) {
                                                Text("Approve Payout", color = Color.Black, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                            }
                                            Button(
                                                onClick = { onHandleWithdrawal(req.id, false, "Rejected by Owner") },
                                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF5252)),
                                                shape = RoundedCornerShape(8.dp),
                                                modifier = Modifier.weight(1f)
                                            ) {
                                                Text("Reject", color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold)
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
    }

    // ADJUST COINS MODAL (Requires Owner Confirmation + Reason -> Automatically logged to Audit Log)
    if (showAdjustCoinDialog) {
        AlertDialog(
            onDismissRequest = { showAdjustCoinDialog = false },
            confirmButton = {
                Button(
                    onClick = {
                        val amount = adjustAmountInput.toLongOrNull()
                        if (adjustUserIdInput.isBlank()) {
                            adjustError = "Please specify a Target User ID."
                            return@Button
                        }
                        if (amount == null || amount <= 0) {
                            adjustError = "Please enter a positive numeric coin amount."
                            return@Button
                        }
                        if (adjustReasonInput.isBlank()) {
                            adjustError = "A mandatory Owner justification is required."
                            return@Button
                        }
                        // Open final confirmation
                        showAdjustConfirmDialog = true
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFFD700))
                ) {
                    Text("Review Adjustment", color = Color.Black, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showAdjustCoinDialog = false }) { Text("Cancel") }
            },
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.MonetizationOn, contentDescription = null, tint = Color(0xFFFFD700))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Manual Coin Adjustment", color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                }
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text("Owner privilege: Directly debit or credit coins from any user balance.", color = Color.White.copy(alpha = 0.7f), fontSize = 11.sp)

                    // Target User ID
                    OutlinedTextField(
                        value = adjustUserIdInput,
                        onValueChange = {
                            adjustUserIdInput = it
                            adjustError = null
                        },
                        label = { Text("Target User ID") },
                        modifier = Modifier.fillMaxWidth()
                    )

                    // Debit vs Credit toggle
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Surface(
                            onClick = { isAdjustmentCredit = true },
                            shape = RoundedCornerShape(8.dp),
                            color = if (isAdjustmentCredit) Color(0xFF00E676).copy(alpha = 0.2f) else Color(0xFF0A0F1D),
                            border = androidx.compose.foundation.BorderStroke(1.dp, if (isAdjustmentCredit) Color(0xFF00E676) else Color(0xFF223250)),
                            modifier = Modifier.weight(1f)
                        ) {
                            Row(modifier = Modifier.padding(10.dp), horizontalArrangement = Arrangement.Center, verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.Add, contentDescription = null, tint = Color(0xFF00E676), modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Add Coins (+)", color = Color(0xFF00E676), fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            }
                        }

                        Surface(
                            onClick = { isAdjustmentCredit = false },
                            shape = RoundedCornerShape(8.dp),
                            color = if (!isAdjustmentCredit) Color(0xFFFF5252).copy(alpha = 0.2f) else Color(0xFF0A0F1D),
                            border = androidx.compose.foundation.BorderStroke(1.dp, if (!isAdjustmentCredit) Color(0xFFFF5252) else Color(0xFF223250)),
                            modifier = Modifier.weight(1f)
                        ) {
                            Row(modifier = Modifier.padding(10.dp), horizontalArrangement = Arrangement.Center, verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.Remove, contentDescription = null, tint = Color(0xFFFF5252), modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Deduct Coins (-)", color = Color(0xFFFF5252), fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }

                    // Amount
                    OutlinedTextField(
                        value = adjustAmountInput,
                        onValueChange = {
                            adjustAmountInput = it
                            adjustError = null
                        },
                        label = { Text("Coins Amount") },
                        modifier = Modifier.fillMaxWidth()
                    )

                    // Reason (MANDATORY)
                    OutlinedTextField(
                        value = adjustReasonInput,
                        onValueChange = {
                            adjustReasonInput = it
                            adjustError = null
                        },
                        label = { Text("Mandatory Owner Reason") },
                        placeholder = { Text("e.g. VIP promotion reward / System error refund") },
                        modifier = Modifier.fillMaxWidth()
                    )

                    adjustError?.let { err ->
                        Text(err, color = Color(0xFFFF5252), fontSize = 11.sp)
                    }
                }
            },
            containerColor = Color(0xFF131D31)
        )
    }

    // FINAL MANDATORY OWNER CONFIRMATION
    if (showAdjustConfirmDialog) {
        val amount = adjustAmountInput.toLongOrNull() ?: 0L
        val delta = if (isAdjustmentCredit) amount else -amount
        AlertDialog(
            onDismissRequest = { showAdjustConfirmDialog = false },
            confirmButton = {
                Button(
                    onClick = {
                        onAdjustCoins(adjustUserIdInput, delta, adjustReasonInput)
                        showAdjustConfirmDialog = false
                        showAdjustCoinDialog = false
                        adjustAmountInput = ""
                        adjustReasonInput = ""
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = if (isAdjustmentCredit) Color(0xFF00E676) else Color(0xFFFF5252))
                ) {
                    Text("Yes, Authorize Adjustment", fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showAdjustConfirmDialog = false }) { Text("Back") }
            },
            title = { Text("Confirm Authorization", color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.Bold) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        "Are you sure you want to execute this adjustment?",
                        color = Color.White,
                        fontSize = 13.sp
                    )
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = Color(0xFF0A0F1D),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(10.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                            Text("Target User ID: $adjustUserIdInput", color = Color.White, fontSize = 11.sp)
                            Text("Action: ${if (isAdjustmentCredit) "+$amount Coins (Credit)" else "-$amount Coins (Debit)"}", color = if (isAdjustmentCredit) Color(0xFF00E676) else Color(0xFFFF5252), fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            Text("Reason: $adjustReasonInput", color = Color(0xFFFFD700), fontSize = 11.sp)
                            Text("Audit Trail: Recorded automatically with Root Owner signature.", color = Color.White.copy(alpha = 0.5f), fontSize = 10.sp)
                        }
                    }
                }
            },
            containerColor = Color(0xFF131D31)
        )
    }

    // VIEW TRANSACTION DETAILS MODAL
    inspectingTransaction?.let { tx ->
        AlertDialog(
            onDismissRequest = { inspectingTransaction = null },
            confirmButton = {
                TextButton(onClick = { inspectingTransaction = null }) { Text("Close", color = Color(0xFFFFD700)) }
            },
            title = { Text("Transaction Details", color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.Bold) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = Color(0xFF0A0F1D),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(10.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                            Text("Transaction ID: ${tx.id}", color = Color(0xFF00E5FF), fontSize = 11.sp)
                            Text("User: ${tx.userName} (ID: ${tx.userId})", color = Color.White, fontSize = 12.sp)
                            Text("Type: ${tx.type}", color = Color.White, fontSize = 12.sp)
                            Text("Amount: %,d Coins".format(tx.amountCoins), color = Color(0xFFFFD700), fontSize = 13.sp, fontWeight = FontWeight.Bold)
                            Text("Status: ${tx.status}", color = Color(0xFF00E676), fontSize = 11.sp)
                            Text("Timestamp: ${dateFormatter.format(Date(tx.date))}", color = Color.White.copy(alpha = 0.5f), fontSize = 10.sp)
                        }
                    }
                }
            },
            containerColor = Color(0xFF131D31)
        )
    }
}

@Composable
fun CoinStatBox(
    title: String,
    value: String,
    accent: Color,
    modifier: Modifier = Modifier
) {
    Surface(
        shape = RoundedCornerShape(10.dp),
        color = Color(0xFF0A0F1D),
        border = androidx.compose.foundation.BorderStroke(1.dp, accent.copy(alpha = 0.25f)),
        modifier = modifier
    ) {
        Column(modifier = Modifier.padding(10.dp)) {
            Text(title, color = Color.White.copy(alpha = 0.6f), fontSize = 10.sp)
            Spacer(modifier = Modifier.height(4.dp))
            Text(value, color = accent, fontSize = 13.sp, fontWeight = FontWeight.Bold, maxLines = 1)
        }
    }
}
