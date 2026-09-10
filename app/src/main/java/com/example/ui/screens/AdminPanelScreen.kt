package com.example.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
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
import coil.compose.AsyncImage
import com.example.data.model.OwnerDashboardStats
import com.example.data.repository.BismaRepository
import com.example.ui.screens.owner.*
import kotlinx.coroutines.launch

enum class OwnerRoleMode(
    val title: String,
    val badgeColor: Color,
    val isOwner: Boolean,
    val canManageEconomy: Boolean,
    val canSendManager: Boolean
) {
    OWNER_ROOT("👑 Root Owner", Color(0xFFFFD700), true, true, true),
    ADMIN_LEADER("🛡️ Admin Leader", Color(0xFF2979FF), false, false, false),
    ROOM_MANAGER("📋 Room Manager", Color(0xFF7C4DFF), false, false, false)
}

data class NavItem(
    val title: String,
    val icon: ImageVector,
    val badge: String? = null
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminPanelScreen(
    repository: BismaRepository,
    onBack: () -> Unit
) {
    val coroutineScope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }

    // Repository Flows
    val allUsers by repository.users.collectAsState(initial = emptyList())
    val allRoles by repository.userRoles.collectAsState(initial = emptyList())
    val agencies by repository.agencies.collectAsState(initial = emptyList())
    val withdrawals by repository.withdrawalRequests.collectAsState(initial = emptyList())
    val packages by repository.rechargePackages.collectAsState(initial = emptyList())
    val auditLogs by repository.recentAuditLogs.collectAsState(initial = emptyList())
    val configs by repository.appConfigs.collectAsState(initial = emptyList())

    // Owner Dashboard Stats state
    var dashboardStats by remember {
        mutableStateOf(
            OwnerDashboardStats(
                totalUsers = 14L,
                onlineUsers = 8L,
                totalRooms = 6L,
                activeRooms = 3L,
                totalAgencies = 4L,
                totalHosts = 10L,
                totalAdmins = 3L,
                totalManagers = 4L,
                totalCoinsInCirculation = 2450000L,
                totalRevenueUsd = 18600.0,
                pendingWithdrawalsCount = 2,
                pendingReportsCount = 1
            )
        )
    }

    LaunchedEffect(Unit) {
        dashboardStats = repository.getOwnerStats()
    }

    // Role Simulator state (Default: Root Owner)
    var currentRoleMode by remember { mutableStateOf(OwnerRoleMode.OWNER_ROOT) }
    var showRoleModeMenu by remember { mutableStateOf(false) }

    // 7 Navigation Sections:
    // 0: Home (Dashboard)
    // 1: Users (User Management)
    // 2: Send Manager (Dedicated Manager Assignment)
    // 3: Admin (Admin Management)
    // 4: Agency (Agency Management)
    // 5: Coin (Coin Management)
    // 6: Settings (Owner Settings)
    var selectedCategoryIndex by remember { mutableStateOf(0) }

    // Header Dialogs: Notifications & Logout
    var showNotificationDialog by remember { mutableStateOf(false) }
    var showLogoutConfirmDialog by remember { mutableStateOf(false) }

    val navItems = remember {
        listOf(
            NavItem("Home", Icons.Default.Home),
            NavItem("Users", Icons.Default.People),
            NavItem("Send Manager", Icons.Default.SupervisorAccount),
            NavItem("Admin", Icons.Default.Shield),
            NavItem("Agency", Icons.Default.CorporateFare),
            NavItem("Coin", Icons.Default.MonetizationOn),
            NavItem("Settings", Icons.Default.Settings)
        )
    }

    BackHandler {
        onBack()
    }

    BoxWithConstraints(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF080C16))
    ) {
        val isDesktop = maxWidth >= 720.dp

        if (isDesktop) {
            // DESKTOP LAYOUT: Left Sidebar Navigation + Right Content Pane
            Row(modifier = Modifier.fillMaxSize()) {
                // LEFT SIDEBAR (Width: 230.dp)
                Surface(
                    color = Color(0xFF0D1424),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF1E2D4A)),
                    modifier = Modifier
                        .width(230.dp)
                        .fillMaxHeight()
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(14.dp)
                            .verticalScroll(rememberScrollState()),
                        verticalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            // Top Header in Sidebar
                            Row(
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Surface(
                                    shape = RoundedCornerShape(10.dp),
                                    color = Color(0xFFFF2A85),
                                    modifier = Modifier.size(36.dp)
                                ) {
                                    Box(contentAlignment = Alignment.Center) {
                                        Text("A", color = Color.White, fontWeight = FontWeight.Black, fontSize = 20.sp)
                                    }
                                }
                                Spacer(modifier = Modifier.width(10.dp))
                                Column {
                                    Text(
                                        text = "AURA Live",
                                        color = Color.White,
                                        fontSize = 16.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Text(
                                        text = "Owner Panel",
                                        color = Color(0xFFFFD700),
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Medium
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(18.dp))

                            // Owner Profile Card
                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = Color(0xFF131D31),
                                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFFFD700).copy(alpha = 0.3f)),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Row(
                                    modifier = Modifier.padding(10.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    AsyncImage(
                                        model = "https://images.unsplash.com/photo-1534528741775-53994a69daeb?w=150",
                                        contentDescription = null,
                                        modifier = Modifier
                                            .size(38.dp)
                                            .clip(CircleShape)
                                            .border(1.5.dp, Color(0xFFFFD700), CircleShape)
                                    )
                                    Spacer(modifier = Modifier.width(10.dp))
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text("Owner Root", color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                        Text("👑 Super Admin", color = Color(0xFFFFD700), fontSize = 10.sp)
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(16.dp))
                            Divider(color = Color(0xFF1E2D4A), thickness = 0.5.dp)
                            Spacer(modifier = Modifier.height(12.dp))

                            // Navigation Items Menu
                            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                navItems.forEachIndexed { index, item ->
                                    val isSelected = selectedCategoryIndex == index
                                    Surface(
                                        onClick = { selectedCategoryIndex = index },
                                        shape = RoundedCornerShape(10.dp),
                                        color = if (isSelected) Color(0xFFFFD700) else Color.Transparent,
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .testTag("sidebar_nav_item_$index")
                                    ) {
                                        Row(
                                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp),
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Icon(
                                                item.icon,
                                                contentDescription = item.title,
                                                tint = if (isSelected) Color.Black else Color.White.copy(alpha = 0.8f),
                                                modifier = Modifier.size(18.dp)
                                            )
                                            Spacer(modifier = Modifier.width(12.dp))
                                            Text(
                                                text = item.title,
                                                color = if (isSelected) Color.Black else Color.White,
                                                fontSize = 12.sp,
                                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                                            )
                                        }
                                    }
                                }
                            }
                        }

                        // Bottom Actions: Notifications, Role Simulator, Logout
                        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            Divider(color = Color(0xFF1E2D4A), thickness = 0.5.dp)

                            // Role Switcher Button
                            Surface(
                                onClick = { showRoleModeMenu = true },
                                shape = RoundedCornerShape(8.dp),
                                color = currentRoleMode.badgeColor.copy(alpha = 0.15f),
                                border = androidx.compose.foundation.BorderStroke(1.dp, currentRoleMode.badgeColor),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(currentRoleMode.title, color = currentRoleMode.badgeColor, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                    Icon(Icons.Default.ArrowDropDown, contentDescription = null, tint = currentRoleMode.badgeColor, modifier = Modifier.size(14.dp))
                                }
                            }

                            // Notification & Logout row
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Button(
                                    onClick = { showNotificationDialog = true },
                                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF131D31)),
                                    shape = RoundedCornerShape(8.dp),
                                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp),
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Icon(Icons.Default.Notifications, contentDescription = null, tint = Color(0xFF00E5FF), modifier = Modifier.size(16.dp))
                                }

                                Button(
                                    onClick = { showLogoutConfirmDialog = true },
                                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF5252).copy(alpha = 0.2f)),
                                    shape = RoundedCornerShape(8.dp),
                                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp),
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Icon(Icons.Default.Logout, contentDescription = null, tint = Color(0xFFFF5252), modifier = Modifier.size(16.dp))
                                }
                            }
                        }
                    }
                }

                // RIGHT CONTENT PANE
                Column(modifier = Modifier.weight(1f).fillMaxHeight()) {
                    // Right Top Status Bar
                    Surface(
                        color = Color(0xFF0D1424),
                        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF1E2D4A)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp, vertical = 10.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "AURA Live • ${navItems[selectedCategoryIndex].title}",
                                color = Color.White,
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold
                            )

                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Surface(
                                    shape = RoundedCornerShape(6.dp),
                                    color = Color(0xFF00E676).copy(alpha = 0.15f),
                                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF00E676).copy(alpha = 0.5f))
                                ) {
                                    Text(
                                        text = "LIVE OPERATIONS",
                                        color = Color(0xFF00E676),
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.width(10.dp))
                                IconButton(
                                    onClick = {
                                        coroutineScope.launch {
                                            dashboardStats = repository.getOwnerStats()
                                            snackbarHostState.showSnackbar("Metrics refreshed successfully")
                                        }
                                    },
                                    modifier = Modifier.size(32.dp)
                                ) {
                                    Icon(Icons.Default.Refresh, contentDescription = "Refresh", tint = Color(0xFF00E5FF), modifier = Modifier.size(16.dp))
                                }
                            }
                        }
                    }

                    // Content View
                    Box(modifier = Modifier.fillMaxSize().weight(1f)) {
                        RenderOwnerContent(
                            categoryIndex = selectedCategoryIndex,
                            roleMode = currentRoleMode,
                            dashboardStats = dashboardStats,
                            allUsers = allUsers,
                            allRoles = allRoles,
                            agencies = agencies,
                            withdrawals = withdrawals,
                            packages = packages,
                            auditLogs = auditLogs,
                            configs = configs,
                            repository = repository,
                            coroutineScope = coroutineScope,
                            snackbarHostState = snackbarHostState,
                            onNavigateTab = { selectedCategoryIndex = it },
                            onSwitchToOwner = { currentRoleMode = OwnerRoleMode.OWNER_ROOT }
                        )
                    }
                }
            }
        } else {
            // MOBILE LAYOUT: Top Header + Main Content + Bottom Navigation Bar
            Scaffold(
                snackbarHost = { SnackbarHost(snackbarHostState) },
                topBar = {
                    Surface(
                        color = Color(0xFF0D1424),
                        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF1E2D4A)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .statusBarsPadding()
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 12.dp, vertical = 8.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                // AURA Live Logo & Title
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Surface(
                                        shape = RoundedCornerShape(8.dp),
                                        color = Color(0xFFFF2A85),
                                        modifier = Modifier.size(32.dp)
                                    ) {
                                        Box(contentAlignment = Alignment.Center) {
                                            Text("A", color = Color.White, fontWeight = FontWeight.Black, fontSize = 17.sp)
                                        }
                                    }
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Column {
                                        Text(
                                            text = "AURA Live",
                                            color = Color.White,
                                            fontSize = 14.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                        Text(
                                            text = "Owner Panel",
                                            color = Color(0xFFFFD700),
                                            fontSize = 10.sp,
                                            fontWeight = FontWeight.Medium
                                        )
                                    }
                                }

                                // Owner Profile, Notification Icon, Logout Button
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    // Role Switcher / Profile Badge
                                    Box {
                                        Surface(
                                            onClick = { showRoleModeMenu = true },
                                            shape = CircleShape,
                                            color = Color(0xFF131D31),
                                            border = androidx.compose.foundation.BorderStroke(1.dp, currentRoleMode.badgeColor),
                                            modifier = Modifier.size(32.dp)
                                        ) {
                                            Box(contentAlignment = Alignment.Center) {
                                                Text("👑", fontSize = 14.sp)
                                            }
                                        }

                                        DropdownMenu(
                                            expanded = showRoleModeMenu,
                                            onDismissRequest = { showRoleModeMenu = false },
                                            modifier = Modifier.background(Color(0xFF162238))
                                        ) {
                                            OwnerRoleMode.values().forEach { mode ->
                                                DropdownMenuItem(
                                                    text = {
                                                        Column {
                                                            Text(mode.title, color = mode.badgeColor, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                                                            Text(
                                                                text = if (mode.isOwner) "Full Root Control" else "Restricted View",
                                                                color = Color.White.copy(alpha = 0.6f),
                                                                fontSize = 9.sp
                                                            )
                                                        }
                                                    },
                                                    onClick = {
                                                        currentRoleMode = mode
                                                        showRoleModeMenu = false
                                                        coroutineScope.launch {
                                                            snackbarHostState.showSnackbar("Switched to role: ${mode.title}")
                                                        }
                                                    }
                                                )
                                            }
                                        }
                                    }

                                    Spacer(modifier = Modifier.width(4.dp))

                                    // Notification Icon
                                    IconButton(
                                        onClick = { showNotificationDialog = true },
                                        modifier = Modifier.size(36.dp)
                                    ) {
                                        Icon(Icons.Default.Notifications, contentDescription = "Notifications", tint = Color(0xFF00E5FF), modifier = Modifier.size(20.dp))
                                    }

                                    // Logout Button
                                    IconButton(
                                        onClick = { showLogoutConfirmDialog = true },
                                        modifier = Modifier.size(36.dp)
                                    ) {
                                        Icon(Icons.Default.Logout, contentDescription = "Logout", tint = Color(0xFFFF5252), modifier = Modifier.size(20.dp))
                                    }
                                }
                            }
                        }
                    }
                },
                bottomBar = {
                    // BOTTOM NAVIGATION BAR (7 Mandatory Items)
                    Surface(
                        color = Color(0xFF0D1424),
                        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF1E2D4A)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .navigationBarsPadding()
                                .padding(vertical = 4.dp),
                            horizontalArrangement = Arrangement.SpaceAround,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            navItems.forEachIndexed { index, item ->
                                val isSelected = selectedCategoryIndex == index
                                val tintColor = if (isSelected) Color(0xFFFFD700) else Color.White.copy(alpha = 0.5f)

                                Column(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(8.dp))
                                        .clickable { selectedCategoryIndex = index }
                                        .padding(horizontal = 4.dp, vertical = 4.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    Icon(
                                        item.icon,
                                        contentDescription = item.title,
                                        tint = tintColor,
                                        modifier = Modifier.size(20.dp)
                                    )
                                    Spacer(modifier = Modifier.height(2.dp))
                                    Text(
                                        text = item.title,
                                        color = tintColor,
                                        fontSize = 9.sp,
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                        maxLines = 1
                                    )
                                }
                            }
                        }
                    }
                },
                containerColor = Color(0xFF080C16)
            ) { innerPadding ->
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding)
                ) {
                    RenderOwnerContent(
                        categoryIndex = selectedCategoryIndex,
                        roleMode = currentRoleMode,
                        dashboardStats = dashboardStats,
                        allUsers = allUsers,
                        allRoles = allRoles,
                        agencies = agencies,
                        withdrawals = withdrawals,
                        packages = packages,
                        auditLogs = auditLogs,
                        configs = configs,
                        repository = repository,
                        coroutineScope = coroutineScope,
                        snackbarHostState = snackbarHostState,
                        onNavigateTab = { selectedCategoryIndex = it },
                        onSwitchToOwner = { currentRoleMode = OwnerRoleMode.OWNER_ROOT }
                    )
                }
            }
        }
    }

    // TOP HEADER DIALOG: NOTIFICATIONS & ALERTS
    if (showNotificationDialog) {
        AlertDialog(
            onDismissRequest = { showNotificationDialog = false },
            confirmButton = {
                TextButton(onClick = { showNotificationDialog = false }) {
                    Text("Close", color = Color(0xFF00E5FF))
                }
            },
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.NotificationsActive, contentDescription = null, tint = Color(0xFF00E5FF))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("System Alerts & Notifications", color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                }
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    listOf(
                        "📢 System Health: All 6 voice servers operating normally at 99.9% uptime.",
                        "⚡ Security: Zero unauthorized API attempts in the past 24 hours.",
                        "💳 Payout Queue: ${withdrawals.count { it.status == "Pending" }} withdrawal requests awaiting Owner review."
                    ).forEach { note ->
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = Color(0xFF0A0F1D),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = note,
                                color = Color.White.copy(alpha = 0.85f),
                                fontSize = 11.sp,
                                modifier = Modifier.padding(10.dp)
                            )
                        }
                    }
                }
            },
            containerColor = Color(0xFF131D31)
        )
    }

    // TOP HEADER DIALOG: LOGOUT CONFIRMATION
    if (showLogoutConfirmDialog) {
        AlertDialog(
            onDismissRequest = { showLogoutConfirmDialog = false },
            confirmButton = {
                Button(
                    onClick = {
                        showLogoutConfirmDialog = false
                        onBack()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF5252))
                ) {
                    Text("Exit Owner Mode", color = Color.White, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showLogoutConfirmDialog = false }) {
                    Text("Cancel", color = Color.White.copy(alpha = 0.7f))
                }
            },
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Logout, contentDescription = null, tint = Color(0xFFFF5252))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Exit Owner Panel", color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                }
            },
            text = {
                Text(
                    "Are you sure you want to exit the Owner Panel? You will return to the standard user view.",
                    color = Color.White.copy(alpha = 0.8f),
                    fontSize = 12.sp
                )
            },
            containerColor = Color(0xFF131D31)
        )
    }
}

@Composable
fun RenderOwnerContent(
    categoryIndex: Int,
    roleMode: OwnerRoleMode,
    dashboardStats: OwnerDashboardStats,
    allUsers: List<com.example.data.model.User>,
    allRoles: List<com.example.data.model.UserRoleAssignment>,
    agencies: List<com.example.data.model.Agency>,
    withdrawals: List<com.example.data.model.WithdrawalRequest>,
    packages: List<com.example.data.model.RechargePackage>,
    auditLogs: List<com.example.data.model.AuditLogEntity>,
    configs: List<com.example.data.model.AppConfigEntity>,
    repository: BismaRepository,
    coroutineScope: kotlinx.coroutines.CoroutineScope,
    snackbarHostState: SnackbarHostState,
    onNavigateTab: (Int) -> Unit,
    onSwitchToOwner: () -> Unit
) {
    // Check Role Restrictions: Owner-only controls must never be accessible to normal users, Admins, or Managers unless granted!
    val isOwnerRestricted = when (categoryIndex) {
        2 -> !roleMode.canSendManager
        3 -> !roleMode.isOwner
        5 -> !roleMode.canManageEconomy
        6 -> !roleMode.isOwner
        else -> false
    }

    if (isOwnerRestricted) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            contentAlignment = Alignment.Center
        ) {
            Surface(
                shape = RoundedCornerShape(18.dp),
                color = Color(0xFF1A1528),
                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFFF5252).copy(alpha = 0.5f)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(
                        imageVector = Icons.Default.Lock,
                        contentDescription = "Locked",
                        tint = Color(0xFFFF5252),
                        modifier = Modifier.size(48.dp)
                    )
                    Spacer(modifier = Modifier.height(14.dp))
                    Text(
                        text = "Access Denied: Owner Authorization Required",
                        color = Color.White,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Your current role '${roleMode.title}' lacks permission for this Owner-only control. These systems can only be accessed with explicit Root Owner authorization.",
                        color = Color.White.copy(alpha = 0.7f),
                        fontSize = 12.sp,
                        lineHeight = 17.sp,
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Button(
                        onClick = onSwitchToOwner,
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFFD700)),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Text("Authorize as Root Owner 👑", color = Color.Black, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                    }
                }
            }
        }
    } else {
        when (categoryIndex) {
            // 0: HOME / DASHBOARD
            0 -> OwnerDashboardView(
                stats = dashboardStats,
                allUsers = allUsers,
                allRoles = allRoles,
                auditLogs = auditLogs,
                withdrawals = withdrawals,
                onNavigateTab = onNavigateTab
            )

            // 1: USERS (User Management)
            1 -> OwnerUsersView(
                allUsers = allUsers,
                onToggleBanUser = { userId, isBanned, reason ->
                    coroutineScope.launch {
                        val res = repository.toggleUserBanOwner(userId, isBanned, reason)
                        snackbarHostState.showSnackbar(res.getOrDefault("Account updated."))
                    }
                },
                onAdjustCoins = { userId, deltaCoins, reason ->
                    coroutineScope.launch {
                        val res = repository.adjustCoinsOwner(userId, deltaCoins, reason)
                        snackbarHostState.showSnackbar(res.getOrDefault("Coins balance updated."))
                    }
                },
                onAssignFrame = { userId, frameId ->
                    coroutineScope.launch {
                        val res = repository.assignOfficialFrameOwner(userId, frameId)
                        snackbarHostState.showSnackbar(res.getOrDefault("Frame equipped."))
                    }
                }
            )

            // 2: SEND MANAGER (Dedicated Manager assignment interface)
            2 -> OwnerSendManagerView(
                allUsers = allUsers,
                allRoles = allRoles,
                onSendOrAssignManager = { userId, role, perms, status, notes ->
                    coroutineScope.launch {
                        val res = repository.sendOrAssignManager(userId, role, perms, status, notes)
                        snackbarHostState.showSnackbar(res.getOrDefault("Manager role applied successfully!"))
                    }
                },
                onRemoveManager = { userId, reason ->
                    coroutineScope.launch {
                        val res = repository.removeManager(userId, reason)
                        snackbarHostState.showSnackbar(res.getOrDefault("Manager role revoked."))
                    }
                }
            )

            // 3: ADMIN (Admin Management)
            3 -> OwnerAdminView(
                allUsers = allUsers,
                allRoles = allRoles,
                onAddOrUpdateAdmin = { userId, role, perms, notes ->
                    coroutineScope.launch {
                        val res = repository.addOrUpdateAdmin(userId, role, perms, notes)
                        snackbarHostState.showSnackbar(res.getOrDefault("Admin appointment saved!"))
                    }
                },
                onRemoveAdmin = { userId, reason ->
                    coroutineScope.launch {
                        val res = repository.removeManager(userId, reason)
                        snackbarHostState.showSnackbar(res.getOrDefault("Admin revoked."))
                    }
                }
            )

            // 4: AGENCY (Agency Management)
            4 -> OwnerAgencyView(
                agencies = agencies,
                allUsers = allUsers,
                onCreateAgency = { name, code, ownerId, ann ->
                    coroutineScope.launch {
                        val res = repository.createAgencyOwner(name, code, ownerId, ann)
                        snackbarHostState.showSnackbar(if (res.isSuccess) "Agency '$name' registered!" else "Failed to create agency")
                    }
                },
                onDeleteAgency = { agencyId, reason ->
                    coroutineScope.launch {
                        val res = repository.deleteAgencyOwner(agencyId, reason)
                        snackbarHostState.showSnackbar(res.getOrDefault("Agency dissolved."))
                    }
                }
            )

            // 5: COIN (Coin Management)
            5 -> OwnerCoinView(
                allUsers = allUsers,
                rechargePackages = packages,
                withdrawals = withdrawals,
                auditLogs = auditLogs,
                onAdjustCoins = { userId, deltaCoins, reason ->
                    coroutineScope.launch {
                        val res = repository.adjustCoinsOwner(userId, deltaCoins, reason)
                        snackbarHostState.showSnackbar(res.getOrDefault("Coins updated."))
                    }
                },
                onHandleWithdrawal = { withdrawalId, approve, notes ->
                    coroutineScope.launch {
                        val res = repository.handleWithdrawalOwner(withdrawalId, if (approve) "APPROVE" else "REJECT", notes)
                        snackbarHostState.showSnackbar(res.getOrDefault("Withdrawal processed."))
                    }
                }
            )

            // 6: SETTINGS (Owner Settings)
            6 -> OwnerSettingsView(
                appConfigs = configs,
                auditLogs = auditLogs,
                onUpdateConfig = { key, value, desc ->
                    coroutineScope.launch {
                        val res = repository.updateAppConfigOwner(key, value, desc)
                        snackbarHostState.showSnackbar(res.getOrDefault("Configuration saved."))
                    }
                },
                onBroadcastAnnouncement = { title, msg, aud ->
                    coroutineScope.launch {
                        val res = repository.broadcastAnnouncementOwner(title, msg, aud)
                        snackbarHostState.showSnackbar(res.getOrDefault("Broadcast transmitted."))
                    }
                }
            )
        }
    }
}
