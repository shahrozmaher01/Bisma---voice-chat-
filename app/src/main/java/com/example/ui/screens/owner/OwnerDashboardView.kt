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
import androidx.compose.material.icons.automirrored.filled.TrendingUp
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.AuditLogEntity
import com.example.data.model.OwnerDashboardStats
import com.example.data.model.User
import com.example.data.model.UserRoleAssignment
import com.example.data.model.WithdrawalRequest
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

data class KpiCardData(
    val title: String,
    val value: String,
    val subtitle: String,
    val icon: ImageVector,
    val accentColor: Color
)

data class StatMetric(
    val title: String,
    val value: String,
    val change: String,
    val isPositive: Boolean,
    val icon: ImageVector,
    val progress: Float
)

@Composable
fun OwnerDashboardView(
    stats: OwnerDashboardStats,
    allUsers: List<User> = emptyList(),
    allRoles: List<UserRoleAssignment> = emptyList(),
    auditLogs: List<AuditLogEntity> = emptyList(),
    withdrawals: List<WithdrawalRequest> = emptyList(),
    onNavigateTab: (Int) -> Unit
) {
    var selectedStatsFilter by remember { mutableStateOf("Daily") }
    var selectedActivityFilter by remember { mutableStateOf("All") }
    val dateFormatter = remember { SimpleDateFormat("HH:mm • MMM dd", Locale.getDefault()) }

    // 10 Mandatory Dashboard Cards
    val dashboardCards = remember(stats) {
        listOf(
            KpiCardData("Total Users", stats.totalUsers.toString(), "+24 today", Icons.Default.People, Color(0xFF00E5FF)),
            KpiCardData("Online Users", stats.onlineUsers.toString(), "Real-time active", Icons.Default.Wifi, Color(0xFF00E676)),
            KpiCardData("Total Rooms", stats.totalRooms.toString(), "All voice rooms", Icons.Default.Mic, Color(0xFFFF2A85)),
            KpiCardData("Active Rooms", stats.activeRooms.toString(), "Live audio seats", Icons.Default.GraphicEq, Color(0xFFFFD700)),
            KpiCardData("Total Agencies", stats.totalAgencies.toString(), "Official partners", Icons.Default.CorporateFare, Color(0xFF9C27B0)),
            KpiCardData("Total Hosts", stats.totalHosts.toString(), "Verified talent", Icons.Default.RecordVoiceOver, Color(0xFFFF9100)),
            KpiCardData("Total Admins", stats.totalAdmins.toString(), "Security & oversight", Icons.Default.Shield, Color(0xFF2979FF)),
            KpiCardData("Total Managers", stats.totalManagers.toString(), "Operations staff", Icons.Default.SupervisorAccount, Color(0xFF7C4DFF)),
            KpiCardData("Total Coins", "%,d".format(stats.totalCoinsInCirculation), "In user wallets", Icons.Default.MonetizationOn, Color(0xFFFFD700)),
            KpiCardData("Total Revenue", "$%,.2f".format(stats.totalRevenueUsd), "Gross earnings USD", Icons.AutoMirrored.Filled.TrendingUp, Color(0xFF00E676))
        )
    }

    // 6 Mandatory Statistics Metrics
    val statisticsList = remember(selectedStatsFilter, stats) {
        when (selectedStatsFilter) {
            "Weekly" -> listOf(
                StatMetric("Daily Users", "1,840", "+8.4%", true, Icons.Default.Person, 0.72f),
                StatMetric("Weekly Users", "11,450", "+14.2%", true, Icons.Default.Group, 0.85f),
                StatMetric("Monthly Users", "42,800", "+18.9%", true, Icons.Default.DateRange, 0.90f),
                StatMetric("Coin Transactions", "284,500 🪙", "+22.5%", true, Icons.Default.SwapHoriz, 0.78f),
                StatMetric("Revenue Statistics", "$31,240 USD", "+16.8%", true, Icons.Default.AttachMoney, 0.82f),
                StatMetric("New Users", "620", "+11.3%", true, Icons.Default.PersonAdd, 0.65f)
            )
            "Monthly" -> listOf(
                StatMetric("Daily Users", "2,150", "+12.1%", true, Icons.Default.Person, 0.80f),
                StatMetric("Weekly Users", "14,200", "+19.0%", true, Icons.Default.Group, 0.88f),
                StatMetric("Monthly Users", "58,900", "+25.4%", true, Icons.Default.DateRange, 0.94f),
                StatMetric("Coin Transactions", "1,140,000 🪙", "+31.2%", true, Icons.Default.SwapHoriz, 0.86f),
                StatMetric("Revenue Statistics", "$124,500 USD", "+28.7%", true, Icons.Default.AttachMoney, 0.89f),
                StatMetric("New Users", "2,840", "+19.5%", true, Icons.Default.PersonAdd, 0.74f)
            )
            else -> listOf(
                StatMetric("Daily Users", "1,240", "+5.2%", true, Icons.Default.Person, 0.65f),
                StatMetric("Weekly Users", "8,920", "+11.8%", true, Icons.Default.Group, 0.79f),
                StatMetric("Monthly Users", "34,500", "+15.6%", true, Icons.Default.DateRange, 0.84f),
                StatMetric("Coin Transactions", "48,200 🪙", "+18.4%", true, Icons.Default.SwapHoriz, 0.72f),
                StatMetric("Revenue Statistics", "$4,850 USD", "+14.0%", true, Icons.Default.AttachMoney, 0.76f),
                StatMetric("New Users", "142", "+8.7%", true, Icons.Default.PersonAdd, 0.58f)
            )
        }
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        contentPadding = PaddingValues(top = 14.dp, bottom = 32.dp)
    ) {
        // Welcome Banner & High Level Status
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(18.dp))
                    .background(
                        Brush.horizontalGradient(
                            listOf(Color(0xFF1E1538), Color(0xFF0D1B2A), Color(0xFF162544))
                        )
                    )
                    .border(1.dp, Color(0xFFFFD700).copy(alpha = 0.35f), RoundedCornerShape(18.dp))
                    .padding(18.dp)
            ) {
                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Surface(
                                shape = CircleShape,
                                color = Color(0xFFFFD700).copy(alpha = 0.2f),
                                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFFFD700)),
                                modifier = Modifier.size(42.dp)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Text("👑", fontSize = 20.sp)
                                }
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(
                                    text = "AURA Live Master Control",
                                    color = Color.White,
                                    fontSize = 17.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = "Root Owner Access • Full Authority",
                                    color = Color(0xFFFFD700),
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Medium
                                )
                            }
                        }

                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = Color(0xFF00E676).copy(alpha = 0.15f),
                            border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF00E676).copy(alpha = 0.6f))
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(8.dp)
                                        .clip(CircleShape)
                                        .background(Color(0xFF00E676))
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "LIVE SYSTEM",
                                    color = Color(0xFF00E676),
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = "Unified executive dashboard monitoring real-time users, active voice rooms, staff assignments, financial circulation, and system health.",
                        color = Color.White.copy(alpha = 0.75f),
                        fontSize = 12.sp,
                        lineHeight = 17.sp
                    )
                }
            }
        }

        // Quick Actions Section (6 Mandatory Actions)
        item {
            Column {
                Text(
                    text = "Quick Actions",
                    color = Color.White,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(bottom = 10.dp)
                )

                // 2 rows of 3 buttons
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        QuickActionButton(
                            title = "Add Manager",
                            icon = Icons.Default.SupervisorAccount,
                            color = Color(0xFF7C4DFF),
                            modifier = Modifier.weight(1f),
                            onClick = { onNavigateTab(2) } // Send Manager
                        )
                        QuickActionButton(
                            title = "Add Admin",
                            icon = Icons.Default.Shield,
                            color = Color(0xFF2979FF),
                            modifier = Modifier.weight(1f),
                            onClick = { onNavigateTab(3) } // Admin
                        )
                        QuickActionButton(
                            title = "Create Agency",
                            icon = Icons.Default.CorporateFare,
                            color = Color(0xFF9C27B0),
                            modifier = Modifier.weight(1f),
                            onClick = { onNavigateTab(4) } // Agency
                        )
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        QuickActionButton(
                            title = "Manage Users",
                            icon = Icons.Default.People,
                            color = Color(0xFF00E5FF),
                            modifier = Modifier.weight(1f),
                            onClick = { onNavigateTab(1) } // Users
                        )
                        QuickActionButton(
                            title = "Coin Management",
                            icon = Icons.Default.MonetizationOn,
                            color = Color(0xFFFFD700),
                            modifier = Modifier.weight(1f),
                            onClick = { onNavigateTab(5) } // Coin
                        )
                        QuickActionButton(
                            title = "Withdrawals",
                            icon = Icons.Default.AccountBalanceWallet,
                            color = Color(0xFFFF5252),
                            modifier = Modifier.weight(1f),
                            onClick = { onNavigateTab(5) } // Coin / Withdrawals
                        )
                    }
                }
            }
        }

        // Dashboard Cards Section (10 Mandatory KPI Cards)
        item {
            Text(
                text = "Core Metrics",
                color = Color.White,
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(top = 4.dp, bottom = 8.dp)
            )
        }

        item {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                val chunks = dashboardCards.chunked(2)
                chunks.forEach { rowItems ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        rowItems.forEach { kpi ->
                            Box(modifier = Modifier.weight(1f)) {
                                KpiCard(kpi = kpi)
                            }
                        }
                        if (rowItems.size == 1) {
                            Spacer(modifier = Modifier.weight(1f))
                        }
                    }
                }
            }
        }

        // Statistics Section (6 Mandatory Metrics: Daily Users, Weekly Users, Monthly Users, Coin Transactions, Revenue Statistics, New Users)
        item {
            Column(modifier = Modifier.padding(top = 8.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "System Statistics",
                        color = Color.White,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold
                    )

                    Row(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(Color(0xFF16233B))
                            .padding(2.dp),
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        listOf("Daily", "Weekly", "Monthly").forEach { filter ->
                            val isSel = selectedStatsFilter == filter
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(if (isSel) Color(0xFFFFD700) else Color.Transparent)
                                    .clickable { selectedStatsFilter = filter }
                                    .padding(horizontal = 8.dp, vertical = 4.dp)
                            ) {
                                Text(
                                    text = filter,
                                    color = if (isSel) Color.Black else Color.White.copy(alpha = 0.7f),
                                    fontSize = 10.sp,
                                    fontWeight = if (isSel) FontWeight.Bold else FontWeight.Normal
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    statisticsList.chunked(2).forEach { pair ->
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            pair.forEach { stat ->
                                Surface(
                                    shape = RoundedCornerShape(12.dp),
                                    color = Color(0xFF131D31),
                                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF223250)),
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Column(modifier = Modifier.padding(12.dp)) {
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Text(stat.title, color = Color.White.copy(alpha = 0.7f), fontSize = 11.sp)
                                            Surface(
                                                shape = RoundedCornerShape(4.dp),
                                                color = Color(0xFF00E676).copy(alpha = 0.15f)
                                            ) {
                                                Text(stat.change, color = Color(0xFF00E676), fontSize = 9.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp))
                                            }
                                        }
                                        Spacer(modifier = Modifier.height(6.dp))
                                        Text(stat.value, color = Color.White, fontSize = 15.sp, fontWeight = FontWeight.Bold)
                                        Spacer(modifier = Modifier.height(6.dp))
                                        LinearProgressIndicator(
                                            progress = { stat.progress },
                                            modifier = Modifier.fillMaxWidth().height(4.dp).clip(RoundedCornerShape(2.dp)),
                                            color = Color(0xFF00E5FF),
                                            trackColor = Color(0xFF1A263D)
                                        )
                                    }
                                }
                            }
                            if (pair.size == 1) {
                                Spacer(modifier = Modifier.weight(1f))
                            }
                        }
                    }
                }
            }
        }

        // Recent Activity Section (Latest user registrations, Latest Manager assignments, Latest Admin actions, Latest coin transactions, Latest withdrawals)
        item {
            Column(modifier = Modifier.padding(top = 8.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Recent Activity Feed",
                        color = Color.White,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold
                    )

                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        items(listOf("All", "Users", "Staff", "Coins", "Cashouts")) { filter ->
                            val isSel = selectedActivityFilter == filter
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(if (isSel) Color(0xFF00E5FF) else Color(0xFF16233B))
                                    .clickable { selectedActivityFilter = filter }
                                    .padding(horizontal = 8.dp, vertical = 4.dp)
                            ) {
                                Text(
                                    text = filter,
                                    color = if (isSel) Color.Black else Color.White.copy(alpha = 0.7f),
                                    fontSize = 10.sp,
                                    fontWeight = if (isSel) FontWeight.Bold else FontWeight.Normal
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Construct filtered activities
                val activities = remember(allUsers, allRoles, auditLogs, withdrawals, selectedActivityFilter) {
                    val list = mutableListOf<ActivityItem>()

                    // Latest user registrations
                    if (selectedActivityFilter == "All" || selectedActivityFilter == "Users") {
                        allUsers.takeLast(4).forEachIndexed { idx, u ->
                            list.add(
                                ActivityItem(
                                    icon = Icons.Default.PersonAdd,
                                    iconColor = Color(0xFF00E5FF),
                                    title = "New User Registered",
                                    subtitle = "${u.username} (ID: ${u.id}) • Level ${u.userLevel}",
                                    timestamp = System.currentTimeMillis() - (idx * 3600000L)
                                )
                            )
                        }
                    }

                    // Latest Manager assignments
                    if (selectedActivityFilter == "All" || selectedActivityFilter == "Staff") {
                        allRoles.filter { it.role.contains("Manager", ignoreCase = true) }.take(4).forEach { m ->
                            list.add(
                                ActivityItem(
                                    icon = Icons.Default.SupervisorAccount,
                                    iconColor = Color(0xFF7C4DFF),
                                    title = "Manager Assigned: ${m.role}",
                                    subtitle = "${m.username} (ID: ${m.userId}) by ${m.assignedByName}",
                                    timestamp = m.assignedAt
                                )
                            )
                        }
                    }

                    // Latest Admin actions
                    if (selectedActivityFilter == "All" || selectedActivityFilter == "Staff") {
                        auditLogs.filter { it.adminRole.contains("Admin", ignoreCase = true) || it.action.contains("ADMIN") }.take(4).forEach { a ->
                            list.add(
                                ActivityItem(
                                    icon = Icons.Default.Shield,
                                    iconColor = Color(0xFF2979FF),
                                    title = "Admin Action: ${a.action}",
                                    subtitle = "${a.adminName} targeted ${a.targetName} (${a.newValue ?: ""})",
                                    timestamp = a.timestamp
                                )
                            )
                        }
                    }

                    // Latest coin transactions
                    if (selectedActivityFilter == "All" || selectedActivityFilter == "Coins") {
                        auditLogs.filter { it.action.contains("COIN") }.take(4).forEach { c ->
                            list.add(
                                ActivityItem(
                                    icon = Icons.Default.Paid,
                                    iconColor = Color(0xFFFFD700),
                                    title = "Coin Adjustment",
                                    subtitle = "${c.targetName} • ${c.newValue ?: ""}",
                                    timestamp = c.timestamp
                                )
                            )
                        }
                    }

                    // Latest withdrawals
                    if (selectedActivityFilter == "All" || selectedActivityFilter == "Cashouts") {
                        withdrawals.take(4).forEach { w ->
                            list.add(
                                ActivityItem(
                                    icon = Icons.Default.AccountBalanceWallet,
                                    iconColor = if (w.status == "Approved") Color(0xFF00E676) else Color(0xFFFF5252),
                                    title = "Withdrawal: ${w.status}",
                                    subtitle = "${w.accountTitle} (${w.paymentMethod}) • $%,.2f USD".format(w.usdAmount),
                                    timestamp = w.requestedAt
                                )
                            )
                        }
                    }

                    list.sortedByDescending { it.timestamp }.take(8)
                }

                if (activities.isEmpty()) {
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = Color(0xFF131D31),
                        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF223250)),
                        modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp)
                    ) {
                        Text(
                            text = "No recent activity records found.",
                            color = Color.White.copy(alpha = 0.5f),
                            fontSize = 12.sp,
                            modifier = Modifier.padding(16.dp),
                            textAlign = androidx.compose.ui.text.style.TextAlign.Center
                        )
                    }
                } else {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        activities.forEach { act ->
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
                                    Surface(
                                        shape = CircleShape,
                                        color = act.iconColor.copy(alpha = 0.15f),
                                        modifier = Modifier.size(34.dp)
                                    ) {
                                        Box(contentAlignment = Alignment.Center) {
                                            Icon(act.icon, contentDescription = null, tint = act.iconColor, modifier = Modifier.size(18.dp))
                                        }
                                    }
                                    Spacer(modifier = Modifier.width(10.dp))
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(act.title, color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                                        Text(act.subtitle, color = Color.White.copy(alpha = 0.65f), fontSize = 11.sp)
                                    }
                                    Text(
                                        text = dateFormatter.format(Date(act.timestamp)),
                                        color = Color.White.copy(alpha = 0.45f),
                                        fontSize = 10.sp
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

data class ActivityItem(
    val icon: ImageVector,
    val iconColor: Color,
    val title: String,
    val subtitle: String,
    val timestamp: Long
)

@Composable
fun QuickActionButton(
    title: String,
    icon: ImageVector,
    color: Color,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Surface(
        onClick = onClick,
        shape = RoundedCornerShape(10.dp),
        color = Color(0xFF131D31),
        border = androidx.compose.foundation.BorderStroke(1.dp, color.copy(alpha = 0.4f)),
        modifier = modifier
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Icon(icon, contentDescription = null, tint = color, modifier = Modifier.size(16.dp))
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = title,
                color = Color.White,
                fontSize = 11.sp,
                fontWeight = FontWeight.SemiBold,
                maxLines = 1
            )
        }
    }
}

@Composable
fun KpiCard(kpi: KpiCardData, modifier: Modifier = Modifier) {
    Surface(
        shape = RoundedCornerShape(12.dp),
        color = Color(0xFF131D31),
        border = androidx.compose.foundation.BorderStroke(1.dp, kpi.accentColor.copy(alpha = 0.35f)),
        modifier = modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(kpi.title, color = Color.White.copy(alpha = 0.7f), fontSize = 11.sp)
                Surface(
                    shape = CircleShape,
                    color = kpi.accentColor.copy(alpha = 0.15f),
                    modifier = Modifier.size(28.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(kpi.icon, contentDescription = null, tint = kpi.accentColor, modifier = Modifier.size(16.dp))
                    }
                }
            }
            Spacer(modifier = Modifier.height(4.dp))
            Text(kpi.value, color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.Bold, maxLines = 1)
            Spacer(modifier = Modifier.height(2.dp))
            Text(kpi.subtitle, color = kpi.accentColor, fontSize = 10.sp, fontWeight = FontWeight.Medium)
        }
    }
}
