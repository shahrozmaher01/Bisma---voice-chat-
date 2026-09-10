package com.example.ui.screens

import android.widget.Toast
import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.RechargePackage
import com.example.data.model.WalletTransaction
import com.example.data.model.WithdrawalRequest
import com.example.data.repository.BismaRepository
import com.example.ui.components.GlassCard
import com.example.ui.components.NeonButton
import com.example.ui.theme.*
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WalletScreen(
    repository: BismaRepository,
    onBack: () -> Unit
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val currentUser by repository.currentUser.collectAsState(initial = null)
    val currencyConfig by repository.currencyConfigFlow.collectAsState(initial = com.example.data.model.CurrencyConfig())
    val activePackages by repository.rechargePackagesFlow.collectAsState(initial = emptyList())
    val transactions by repository.getTransactions().collectAsState(initial = emptyList())

    // Tabs: 0: User Coins, 1: Host Diamonds (Earnings & Withdrawal), 2: Transaction History
    var selectedTab by remember { mutableIntStateOf(0) }

    // User Wallet Stats
    var totalPurchased by remember { mutableLongStateOf(0L) }
    var totalSpent by remember { mutableLongStateOf(0L) }
    var totalEarnedDiamonds by remember { mutableLongStateOf(0L) }
    var totalWithdrawnDiamonds by remember { mutableLongStateOf(0L) }

    LaunchedEffect(currentUser?.id) {
        currentUser?.id?.let { uid ->
            val stats = repository.getWalletStats(uid)
            totalPurchased = stats["totalPurchased"] ?: 0L
            totalSpent = stats["totalSpent"] ?: 0L
            totalEarnedDiamonds = stats["totalEarnedDiamonds"] ?: 0L
            totalWithdrawnDiamonds = stats["totalWithdrawnDiamonds"] ?: 0L
        }
    }

    // Modal Sheet State for Recharge Checkout
    var showRechargeSheet by remember { mutableStateOf(false) }
    var selectedPackageToBuy by remember { mutableStateOf<RechargePackage?>(null) }
    var selectedPaymentMethod by remember { mutableStateOf("Google Play") }

    // Modal Sheet State for Withdrawal Request
    var showWithdrawSheet by remember { mutableStateOf(false) }
    var withdrawDiamondsInput by remember { mutableStateOf("10000") }
    var withdrawPaymentMethod by remember { mutableStateOf("Bank Transfer") }
    var withdrawAccountTitle by remember { mutableStateOf("") }
    var withdrawAccountNumber by remember { mutableStateOf("") }
    var withdrawAccountNotes by remember { mutableStateOf("") }

    // Dialog for Diamond to Coin Exchange
    var showExchangeDialog by remember { mutableStateOf(false) }
    var exchangeDiamondsInput by remember { mutableStateOf("1000") }

    // User Withdrawals List
    val userWithdrawals by repository.getUserWithdrawalsFlow(currentUser?.id ?: "").collectAsState(initial = emptyList())

    val dateFormatter = remember { SimpleDateFormat("dd MMM, hh:mm a", Locale.getDefault()) }

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
            // Top App Bar
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 6.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(onClick = onBack, modifier = Modifier.size(36.dp)) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = Color.White,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(4.dp))
                    Column {
                        Text(
                            text = "AURA Wallet",
                            fontSize = 17.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                        Text(
                            text = "Official: 1 USD = 28,000 Coins",
                            fontSize = 10.sp,
                            color = GoldAmber
                        )
                    }
                }

                // Official Rate Badge
                Surface(
                    color = Color(0x33FFB300),
                    shape = RoundedCornerShape(12.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, GoldAmber.copy(alpha = 0.5f))
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Text("🛡️", fontSize = 11.sp)
                        Spacer(modifier = Modifier.width(3.dp))
                        Text("Secure Escrow", color = GoldYellow, fontSize = 10.5.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }

            // Compact Segmented Tabs (User Coin Wallet, Host Diamond Wallet, Transaction History)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 14.dp, vertical = 4.dp),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                TabButton(
                    title = "🪙 Coin Wallet",
                    isSelected = selectedTab == 0,
                    onClick = { selectedTab = 0 },
                    modifier = Modifier.weight(1f)
                )
                TabButton(
                    title = "💎 Host Diamonds",
                    isSelected = selectedTab == 1,
                    onClick = { selectedTab = 1 },
                    modifier = Modifier.weight(1f)
                )
                TabButton(
                    title = "📜 History",
                    isSelected = selectedTab == 2,
                    onClick = { selectedTab = 2 },
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(4.dp))

            // TAB 0: USER COIN WALLET
            when (selectedTab) {
                0 -> {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(horizontal = 14.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp),
                        contentPadding = PaddingValues(bottom = 32.dp)
                    ) {
                        // Section 1: User Coin Balance Card
                        item {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(16.dp))
                                    .background(
                                        Brush.horizontalGradient(
                                            listOf(Color(0xFF2C1654), Color(0xFF1B0F33), Color(0xFF0D061A))
                                        )
                                    )
                                    .border(1.dp, GoldAmber.copy(alpha = 0.5f), RoundedCornerShape(16.dp))
                                    .padding(14.dp)
                            ) {
                                Column {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text("My Coin Wallet", color = Color.White.copy(alpha = 0.8f), fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                                        Surface(
                                            color = Color(0x3300E5FF),
                                            shape = RoundedCornerShape(8.dp)
                                        ) {
                                            Text(
                                                text = "AURA Live",
                                                color = BrightCyan,
                                                fontSize = 9.sp,
                                                fontWeight = FontWeight.Bold,
                                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                            )
                                        }
                                    }

                                    Spacer(modifier = Modifier.height(8.dp))

                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Text("🪙", fontSize = 28.sp)
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Column {
                                            Text(
                                                text = "${currentUser?.coins ?: 0}",
                                                fontSize = 24.sp,
                                                fontWeight = FontWeight.Black,
                                                color = GoldYellow
                                            )
                                            Text("Available Coins", color = TextSecondary, fontSize = 10.5.sp)
                                        }
                                    }

                                    Spacer(modifier = Modifier.height(12.dp))
                                    HorizontalDivider(color = Color(0x22FFFFFF), thickness = 0.8.dp)
                                    Spacer(modifier = Modifier.height(10.dp))

                                    // Lifetime Stats Row
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Column {
                                            Text("Total Purchased", color = TextSecondary, fontSize = 10.sp)
                                            Text(
                                                text = "+$totalPurchased 🪙",
                                                color = EmeraldGreen,
                                                fontSize = 12.sp,
                                                fontWeight = FontWeight.Bold
                                            )
                                        }
                                        Column(horizontalAlignment = Alignment.End) {
                                            Text("Total Coins Spent", color = TextSecondary, fontSize = 10.sp)
                                            Text(
                                                text = "-$totalSpent 🪙",
                                                color = Color(0xFFFF80AB),
                                                fontSize = 12.sp,
                                                fontWeight = FontWeight.Bold
                                            )
                                        }
                                    }

                                    Spacer(modifier = Modifier.height(12.dp))

                                    // Action Buttons Row
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                                    ) {
                                        Button(
                                            onClick = {
                                                selectedPackageToBuy = activePackages.firstOrNull()
                                                showRechargeSheet = true
                                            },
                                            colors = ButtonDefaults.buttonColors(containerColor = GoldAmber),
                                            shape = RoundedCornerShape(10.dp),
                                            modifier = Modifier.weight(1f).height(38.dp),
                                            contentPadding = PaddingValues(0.dp)
                                        ) {
                                            Row(verticalAlignment = Alignment.CenterVertically) {
                                                Icon(Icons.Default.AddCircle, contentDescription = null, tint = Color.Black, modifier = Modifier.size(16.dp))
                                                Spacer(modifier = Modifier.width(4.dp))
                                                Text("Recharge Coins", color = Color.Black, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                                            }
                                        }

                                        OutlinedButton(
                                            onClick = { selectedTab = 2 },
                                            colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.White),
                                            border = androidx.compose.foundation.BorderStroke(1.dp, Color(0x55FFFFFF)),
                                            shape = RoundedCornerShape(10.dp),
                                            modifier = Modifier.weight(1f).height(38.dp),
                                            contentPadding = PaddingValues(0.dp)
                                        ) {
                                            Row(verticalAlignment = Alignment.CenterVertically) {
                                                Icon(Icons.Default.ReceiptLong, contentDescription = null, tint = Color.White, modifier = Modifier.size(16.dp))
                                                Spacer(modifier = Modifier.width(4.dp))
                                                Text("Transaction History", color = Color.White, fontWeight = FontWeight.SemiBold, fontSize = 12.sp)
                                            }
                                        }
                                    }
                                }
                            }
                        }

                        // Section 2: Recharge Packages Grid (Clean, compact mobile-friendly cards)
                        item {
                            Row(
                                modifier = Modifier.fillMaxWidth().padding(top = 6.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "Recharge Packages",
                                    color = Color.White,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = "Rate: 1 USD = 28k 🪙",
                                    color = GoldAmber,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.SemiBold
                                )
                            }
                        }

                        item {
                            LazyVerticalGrid(
                                columns = GridCells.Fixed(2),
                                modifier = Modifier.fillMaxWidth().height(360.dp),
                                verticalArrangement = Arrangement.spacedBy(8.dp),
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                userScrollEnabled = true
                            ) {
                                items(activePackages) { pkg ->
                                    val isSelected = selectedPackageToBuy?.id == pkg.id
                                    Box(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(14.dp))
                                            .background(if (isSelected) Color(0x33FFB300) else Color(0x55170B2C))
                                            .border(
                                                1.dp,
                                                if (isSelected) GoldAmber else Color(0x3359338F),
                                                RoundedCornerShape(14.dp)
                                            )
                                            .clickable {
                                                selectedPackageToBuy = pkg
                                                showRechargeSheet = true
                                            }
                                            .padding(10.dp)
                                    ) {
                                        Column(
                                            horizontalAlignment = Alignment.CenterHorizontally,
                                            modifier = Modifier.fillMaxWidth()
                                        ) {
                                            // Optional tag
                                            if (pkg.isBestValue) {
                                                Surface(
                                                    color = Color(0xFFFF2A85),
                                                    shape = RoundedCornerShape(6.dp),
                                                    modifier = Modifier.padding(bottom = 4.dp)
                                                ) {
                                                    Text(
                                                        "BEST VALUE",
                                                        color = Color.White,
                                                        fontSize = 8.sp,
                                                        fontWeight = FontWeight.Black,
                                                        modifier = Modifier.padding(horizontal = 5.dp, vertical = 1.dp)
                                                    )
                                                }
                                            } else if (pkg.isPopular) {
                                                Surface(
                                                    color = GoldAmber,
                                                    shape = RoundedCornerShape(6.dp),
                                                    modifier = Modifier.padding(bottom = 4.dp)
                                                ) {
                                                    Text(
                                                        "MOST POPULAR",
                                                        color = Color.Black,
                                                        fontSize = 8.sp,
                                                        fontWeight = FontWeight.Black,
                                                        modifier = Modifier.padding(horizontal = 5.dp, vertical = 1.dp)
                                                    )
                                                }
                                            }

                                            Text("🪙", fontSize = 24.sp)
                                            Spacer(modifier = Modifier.height(2.dp))
                                            Text(
                                                text = "${pkg.coins} Coins",
                                                color = Color.White,
                                                fontSize = 13.sp,
                                                fontWeight = FontWeight.Bold
                                            )

                                            if (pkg.bonusCoins > 0) {
                                                Text(
                                                    text = "+${pkg.bonusCoins} Bonus",
                                                    color = EmeraldGreen,
                                                    fontSize = 10.sp,
                                                    fontWeight = FontWeight.Bold
                                                )
                                            } else {
                                                Text(
                                                    text = pkg.label.ifBlank { "Standard" },
                                                    color = TextSecondary,
                                                    fontSize = 9.5.sp
                                                )
                                            }

                                            Spacer(modifier = Modifier.height(8.dp))

                                            Surface(
                                                color = Color(0x33FFD700),
                                                shape = RoundedCornerShape(8.dp),
                                                border = androidx.compose.foundation.BorderStroke(0.8.dp, GoldAmber)
                                            ) {
                                                Text(
                                                    text = "$${pkg.priceUsd}",
                                                    color = GoldYellow,
                                                    fontWeight = FontWeight.Black,
                                                    fontSize = 12.sp,
                                                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp)
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                // TAB 1: HOST DIAMOND WALLET & EARNINGS
                1 -> {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(horizontal = 14.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp),
                        contentPadding = PaddingValues(bottom = 32.dp)
                    ) {
                        // Diamond Balance Card
                        item {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(16.dp))
                                    .background(
                                        Brush.horizontalGradient(
                                            listOf(Color(0xFF0F2042), Color(0xFF091428), Color(0xFF050A14))
                                        )
                                    )
                                    .border(1.dp, BrightCyan.copy(alpha = 0.5f), RoundedCornerShape(16.dp))
                                    .padding(14.dp)
                            ) {
                                Column {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text("Host Diamond Wallet", color = Color.White.copy(alpha = 0.8f), fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                                        Surface(
                                            color = Color(0x3300E676),
                                            shape = RoundedCornerShape(8.dp)
                                        ) {
                                            Text(
                                                text = "Host Earnings",
                                                color = EmeraldGreen,
                                                fontSize = 9.sp,
                                                fontWeight = FontWeight.Bold,
                                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                            )
                                        }
                                    }

                                    Spacer(modifier = Modifier.height(8.dp))

                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Text("💎", fontSize = 28.sp)
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Column {
                                            Text(
                                                text = "${currentUser?.diamonds ?: 0}",
                                                fontSize = 24.sp,
                                                fontWeight = FontWeight.Black,
                                                color = BrightCyan
                                            )
                                            val estUsd = String.format(Locale.US, "%.2f", (currentUser?.diamonds ?: 0L).toDouble() / currencyConfig.diamondsPerUsd.toDouble())
                                            Text("Est. Value: $$estUsd USD", color = EmeraldGreen, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                        }
                                    }

                                    Spacer(modifier = Modifier.height(10.dp))
                                    HorizontalDivider(color = Color(0x22FFFFFF), thickness = 0.8.dp)
                                    Spacer(modifier = Modifier.height(8.dp))

                                    // Lifetime Diamond Metrics
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Column {
                                            Text("Lifetime Earned", color = TextSecondary, fontSize = 10.sp)
                                            Text("+$totalEarnedDiamonds 💎", color = BrightCyan, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                        }
                                        Column(horizontalAlignment = Alignment.End) {
                                            Text("Total Withdrawn", color = TextSecondary, fontSize = 10.sp)
                                            Text("-$totalWithdrawnDiamonds 💎", color = GoldYellow, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                        }
                                    }

                                    Spacer(modifier = Modifier.height(12.dp))

                                    // Action Buttons: Withdraw & Exchange to Coins
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                                    ) {
                                        Button(
                                            onClick = { showWithdrawSheet = true },
                                            colors = ButtonDefaults.buttonColors(containerColor = BrightCyan),
                                            shape = RoundedCornerShape(10.dp),
                                            modifier = Modifier.weight(1f).height(38.dp),
                                            contentPadding = PaddingValues(0.dp)
                                        ) {
                                            Row(verticalAlignment = Alignment.CenterVertically) {
                                                Icon(Icons.Default.AccountBalance, contentDescription = null, tint = Color.Black, modifier = Modifier.size(16.dp))
                                                Spacer(modifier = Modifier.width(4.dp))
                                                Text("Withdraw USD", color = Color.Black, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                                            }
                                        }

                                        OutlinedButton(
                                            onClick = { showExchangeDialog = true },
                                            colors = ButtonDefaults.outlinedButtonColors(contentColor = GoldYellow),
                                            border = androidx.compose.foundation.BorderStroke(1.dp, GoldAmber),
                                            shape = RoundedCornerShape(10.dp),
                                            modifier = Modifier.weight(1f).height(38.dp),
                                            contentPadding = PaddingValues(0.dp)
                                        ) {
                                            Row(verticalAlignment = Alignment.CenterVertically) {
                                                Icon(Icons.Default.SwapHoriz, contentDescription = null, tint = GoldYellow, modifier = Modifier.size(16.dp))
                                                Spacer(modifier = Modifier.width(4.dp))
                                                Text("Exchange to 🪙", color = GoldYellow, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                                            }
                                        }
                                    }
                                }
                            }
                        }

                        // Commission & Rate Details
                        item {
                            GlassCard(modifier = Modifier.fillMaxWidth()) {
                                Column(modifier = Modifier.fillMaxWidth()) {
                                    Text("Earning Rules & Commission Rates", color = Color.White, fontSize = 12.5.sp, fontWeight = FontWeight.Bold)
                                    Spacer(modifier = Modifier.height(6.dp))
                                    RuleRow("Host Gift Commission:", "${currencyConfig.hostGiftCommissionPercent.toInt()}% of gift value")
                                    RuleRow("Agency Commission:", "${currencyConfig.agencyGiftCommissionPercent.toInt()}% bonus to agency")
                                    RuleRow("Exchange Rate:", "1 Diamond = 10 Coins (Instant)")
                                    RuleRow("Min Withdrawal:", "${currencyConfig.minWithdrawalDiamonds} Diamonds")
                                    RuleRow("Withdrawal Processing:", "Admin audited & dispatched securely")
                                }
                            }
                        }

                        // Withdrawal Requests History
                        item {
                            Text(
                                text = "My Withdrawal Requests",
                                color = Color.White,
                                fontSize = 13.5.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(top = 4.dp)
                            )
                        }

                        if (userWithdrawals.isEmpty()) {
                            item {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 16.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text("No withdrawal requests yet", color = TextSecondary, fontSize = 12.sp)
                                }
                            }
                        } else {
                            items(userWithdrawals) { req ->
                                GlassCard(modifier = Modifier.fillMaxWidth()) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Column {
                                            Row(verticalAlignment = Alignment.CenterVertically) {
                                                Text("${req.diamondAmount} 💎", color = BrightCyan, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                                Spacer(modifier = Modifier.width(6.dp))
                                                Text("($${String.format(Locale.US, "%.2f", req.usdAmount)})", color = EmeraldGreen, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                                            }
                                            Text("${req.paymentMethod} • ${req.accountTitle}", color = TextSecondary, fontSize = 10.5.sp)
                                            Text(dateFormatter.format(Date(req.requestedAt)), color = TextSecondary.copy(alpha = 0.7f), fontSize = 9.5.sp)
                                        }

                                        val statusColor = when (req.status) {
                                            "Approved" -> EmeraldGreen
                                            "Rejected" -> DarkRed
                                            "Under Review" -> ElectricCyan
                                            else -> GoldAmber
                                        }

                                        Surface(
                                            color = statusColor.copy(alpha = 0.2f),
                                            shape = RoundedCornerShape(6.dp),
                                            border = androidx.compose.foundation.BorderStroke(0.8.dp, statusColor)
                                        ) {
                                            Text(
                                                text = req.status,
                                                color = statusColor,
                                                fontSize = 10.sp,
                                                fontWeight = FontWeight.Bold,
                                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                // TAB 2: TRANSACTION HISTORY
                2 -> {
                    if (transactions.isEmpty()) {
                        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            Text("No wallet transactions recorded yet", color = TextSecondary)
                        }
                    } else {
                        LazyColumn(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(horizontal = 14.dp),
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
                                        Column(modifier = Modifier.weight(1f)) {
                                            Row(verticalAlignment = Alignment.CenterVertically) {
                                                val badgeColor = when (tx.type) {
                                                    "Recharge" -> EmeraldGreen
                                                    "Gift Received" -> BrightCyan
                                                    "Diamond Exchange" -> GoldAmber
                                                    "Agency Commission" -> Color(0xFFC084FC)
                                                    else -> Color(0xFFFF80AB)
                                                }
                                                Surface(
                                                    color = badgeColor.copy(alpha = 0.2f),
                                                    shape = RoundedCornerShape(4.dp)
                                                ) {
                                                    Text(
                                                        text = tx.type,
                                                        color = badgeColor,
                                                        fontSize = 9.5.sp,
                                                        fontWeight = FontWeight.Bold,
                                                        modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp)
                                                    )
                                                }
                                                Spacer(modifier = Modifier.width(6.dp))
                                                Text(
                                                    text = dateFormatter.format(Date(tx.timestamp)),
                                                    color = TextSecondary,
                                                    fontSize = 9.5.sp
                                                )
                                            }
                                            Spacer(modifier = Modifier.height(3.dp))
                                            Text(
                                                text = tx.description,
                                                color = Color.White.copy(alpha = 0.9f),
                                                fontSize = 11.5.sp,
                                                maxLines = 2
                                            )
                                        }

                                        Spacer(modifier = Modifier.width(8.dp))

                                        Column(horizontalAlignment = Alignment.End) {
                                            if (tx.amountCoins != 0L) {
                                                Text(
                                                    text = if (tx.amountCoins > 0) "+${tx.amountCoins} 🪙" else "${tx.amountCoins} 🪙",
                                                    color = if (tx.amountCoins > 0) EmeraldGreen else DarkRed,
                                                    fontWeight = FontWeight.Bold,
                                                    fontSize = 12.5.sp
                                                )
                                            }
                                            if (tx.amountDiamonds != 0L) {
                                                Text(
                                                    text = if (tx.amountDiamonds > 0) "+${tx.amountDiamonds} 💎" else "${tx.amountDiamonds} 💎",
                                                    color = if (tx.amountDiamonds > 0) BrightCyan else GoldYellow,
                                                    fontWeight = FontWeight.Bold,
                                                    fontSize = 12.5.sp
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
    }

    // ==========================================
    // RECHARGE CHECKOUT MODAL BOTTOM SHEET
    // ==========================================
    if (showRechargeSheet) {
        ModalBottomSheet(
            onDismissRequest = { showRechargeSheet = false },
            containerColor = Color(0xFF140D26)
        ) {
            val pkg = selectedPackageToBuy
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Confirm Recharge", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                    Surface(
                        color = Color(0x3300E5FF),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text("Instant Delivery", color = BrightCyan, fontSize = 10.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp))
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                if (pkg != null) {
                    GlassCard(modifier = Modifier.fillMaxWidth()) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column {
                                Text("${pkg.coins} Coins", color = GoldYellow, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                                if (pkg.bonusCoins > 0) {
                                    Text("+${pkg.bonusCoins} Bonus Coins Included", color = EmeraldGreen, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                }
                                Text("Total: ${pkg.coins + pkg.bonusCoins} 🪙", color = Color.White, fontSize = 12.sp)
                            }
                            Text("$${pkg.priceUsd}", color = Color.White, fontWeight = FontWeight.Black, fontSize = 18.sp)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))
                Text("Select Payment Gateway:", color = TextSecondary, fontSize = 11.5.sp)
                Spacer(modifier = Modifier.height(6.dp))

                val paymentMethods = listOf("Google Play Billing", "Credit / Debit Card", "PayPal", "EasyPaisa / JazzCash")
                paymentMethods.forEach { method ->
                    val isChosen = selectedPaymentMethod == method
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 3.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(if (isChosen) Color(0x33FFB300) else Color(0x22FFFFFF))
                            .border(1.dp, if (isChosen) GoldAmber else Color(0x22FFFFFF), RoundedCornerShape(10.dp))
                            .clickable { selectedPaymentMethod = method }
                            .padding(horizontal = 12.dp, vertical = 8.dp)
                    ) {
                        RadioButton(
                            selected = isChosen,
                            onClick = { selectedPaymentMethod = method },
                            colors = RadioButtonDefaults.colors(selectedColor = GoldAmber)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(method, color = Color.White, fontSize = 12.5.sp, fontWeight = FontWeight.Medium)
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                NeonButton(
                    text = "Pay $${pkg?.priceUsd ?: 0.0} & Credit Coins",
                    onClick = {
                        if (pkg != null) {
                            coroutineScope.launch {
                                val result = repository.rechargeCoins(pkg.id, selectedPaymentMethod)
                                if (result.isSuccess) {
                                    Toast.makeText(context, "Successfully recharged ${result.getOrNull()} Coins! 🎉", Toast.LENGTH_SHORT).show()
                                    showRechargeSheet = false
                                } else {
                                    Toast.makeText(context, "Error: ${result.exceptionOrNull()?.message}", Toast.LENGTH_SHORT).show()
                                }
                            }
                        }
                    },
                    brush = Brush.horizontalGradient(listOf(Color(0xFFFF9100), Color(0xFFFF3D00))),
                    modifier = Modifier.fillMaxWidth().height(44.dp)
                )

                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }

    // ==========================================
    // WITHDRAWAL MODAL BOTTOM SHEET
    // ==========================================
    if (showWithdrawSheet) {
        ModalBottomSheet(
            onDismissRequest = { showWithdrawSheet = false },
            containerColor = Color(0xFF0F172A)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp)
            ) {
                Text("Withdraw Host Earnings", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                Text("Min ${currencyConfig.minWithdrawalDiamonds} Diamonds • Audited by AURA Admin", color = TextSecondary, fontSize = 11.sp)

                Spacer(modifier = Modifier.height(12.dp))

                OutlinedTextField(
                    value = withdrawDiamondsInput,
                    onValueChange = { withdrawDiamondsInput = it.filter { ch -> ch.isDigit() } },
                    label = { Text("Diamonds to withdraw") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = BrightCyan,
                        unfocusedBorderColor = Color(0x33FFFFFF),
                        focusedTextColor = TextPrimary,
                        unfocusedTextColor = TextPrimary
                    )
                )

                val amountToWithdraw = withdrawDiamondsInput.toLongOrNull() ?: 0L
                val calculatedUsd = amountToWithdraw.toDouble() / currencyConfig.diamondsPerUsd.toDouble()
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Equivalent USD: $${String.format(Locale.US, "%.2f", calculatedUsd)} USD",
                    color = EmeraldGreen,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = withdrawAccountTitle,
                    onValueChange = { withdrawAccountTitle = it },
                    label = { Text("Account Holder Name") },
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = BrightCyan,
                        unfocusedBorderColor = Color(0x33FFFFFF),
                        focusedTextColor = TextPrimary,
                        unfocusedTextColor = TextPrimary
                    )
                )

                Spacer(modifier = Modifier.height(6.dp))

                OutlinedTextField(
                    value = withdrawAccountNumber,
                    onValueChange = { withdrawAccountNumber = it },
                    label = { Text("Account Number / IBAN / PayPal / USDT Address") },
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = BrightCyan,
                        unfocusedBorderColor = Color(0x33FFFFFF),
                        focusedTextColor = TextPrimary,
                        unfocusedTextColor = TextPrimary
                    )
                )

                Spacer(modifier = Modifier.height(12.dp))

                Button(
                    onClick = {
                        if (withdrawAccountTitle.isBlank() || withdrawAccountNumber.isBlank()) {
                            Toast.makeText(context, "Please fill in all payment details", Toast.LENGTH_SHORT).show()
                            return@Button
                        }
                        coroutineScope.launch {
                            val res = repository.submitWithdrawalRequest(
                                diamondAmount = amountToWithdraw,
                                paymentMethod = withdrawPaymentMethod,
                                accountTitle = withdrawAccountTitle,
                                accountNumber = withdrawAccountNumber,
                                accountNotes = withdrawAccountNotes
                            )
                            if (res.isSuccess) {
                                Toast.makeText(context, "Withdrawal request submitted for Admin review! 🚀", Toast.LENGTH_LONG).show()
                                showWithdrawSheet = false
                            } else {
                                Toast.makeText(context, "Error: ${res.exceptionOrNull()?.message}", Toast.LENGTH_SHORT).show()
                            }
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = BrightCyan),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.fillMaxWidth().height(42.dp)
                ) {
                    Text("Submit Withdrawal Request", color = Color.Black, fontWeight = FontWeight.Bold)
                }

                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }

    // ==========================================
    // EXCHANGE DIAMONDS FOR COINS DIALOG
    // ==========================================
    if (showExchangeDialog) {
        AlertDialog(
            onDismissRequest = { showExchangeDialog = false },
            containerColor = Color(0xFF1B1130),
            title = {
                Text("Exchange Diamonds for Coins", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 15.sp)
            },
            text = {
                Column {
                    Text("Instant exchange at 1 Diamond = 10 Coins", color = EmeraldGreen, fontSize = 11.5.sp)
                    Spacer(modifier = Modifier.height(10.dp))
                    OutlinedTextField(
                        value = exchangeDiamondsInput,
                        onValueChange = { exchangeDiamondsInput = it.filter { ch -> ch.isDigit() } },
                        label = { Text("Diamonds amount") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.fillMaxWidth()
                    )
                    val diam = exchangeDiamondsInput.toLongOrNull() ?: 0L
                    val coinsGain = diam * 10
                    Spacer(modifier = Modifier.height(6.dp))
                    Text("You will receive: 🪙 $coinsGain Coins", color = GoldYellow, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val diam = exchangeDiamondsInput.toLongOrNull() ?: 0L
                        coroutineScope.launch {
                            val res = repository.exchangeDiamondsToCoins(diam)
                            if (res.isSuccess) {
                                Toast.makeText(context, "Exchanged $diam Diamonds for ${res.getOrNull()} Coins! 🎉", Toast.LENGTH_SHORT).show()
                                showExchangeDialog = false
                            } else {
                                Toast.makeText(context, "Error: ${res.exceptionOrNull()?.message}", Toast.LENGTH_SHORT).show()
                            }
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = GoldAmber)
                ) {
                    Text("Exchange", color = Color.Black, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showExchangeDialog = false }) {
                    Text("Cancel", color = TextSecondary)
                }
            }
        )
    }
}

@Composable
private fun TabButton(
    title: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        color = if (isSelected) Color(0x33FFB300) else Color(0x331E1B2E),
        shape = RoundedCornerShape(10.dp),
        border = androidx.compose.foundation.BorderStroke(
            1.dp,
            if (isSelected) GoldAmber else Color(0x22FFFFFF)
        ),
        modifier = modifier
            .height(36.dp)
            .clickable { onClick() }
    ) {
        Box(contentAlignment = Alignment.Center) {
            Text(
                text = title,
                color = if (isSelected) GoldYellow else TextSecondary,
                fontSize = 11.5.sp,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
            )
        }
    }
}

@Composable
private fun RuleRow(title: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 2.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(title, color = TextSecondary, fontSize = 11.sp)
        Text(value, color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
    }
}
