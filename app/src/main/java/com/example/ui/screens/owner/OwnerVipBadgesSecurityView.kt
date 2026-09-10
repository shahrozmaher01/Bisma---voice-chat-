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
import com.example.data.admin.AdminService
import com.example.data.model.OfficialFrameAssignment
import com.example.data.model.ReportEntity
import com.example.data.model.User

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OwnerVipBadgesSecurityView(
    allUsers: List<User>,
    officialAssignments: List<OfficialFrameAssignment>,
    reports: List<ReportEntity>,
    onAssignBadge: (userId: String, frameId: String, days: Int) -> Unit,
    onRevokeBadge: (assignmentId: String) -> Unit,
    onToggleBanUser: (userId: String, isBanned: Boolean, reason: String) -> Unit,
    onResolveReport: (reportId: String, status: String, notes: String) -> Unit
) {
    var activeSubTab by remember { mutableStateOf(1) } // 0: VIP Tiers, 1: 12 Official Badges & Frames, 2: Bans & Security, 3: Reports

    // Badge Assignment State
    var targetUserIdForBadge by remember { mutableStateOf("") }
    var selectedBadgeId by remember { mutableStateOf(AdminService.OFFICIAL_FRAMES.first().id) }
    var selectedBadgeDurationDays by remember { mutableStateOf(30) }

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
            listOf("💎 VIP System", "🎖️ 12 Official Badges", "⛔ Bans & Security", "⚠️ Reports (${reports.count { it.status == "Pending" }})").forEachIndexed { index, label ->
                val isSelected = activeSubTab == index
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(10.dp))
                        .background(if (isSelected) Color(0xFF00E5FF) else Color.Transparent)
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
            // TAB 0: VIP SYSTEM
            0 -> {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    contentPadding = PaddingValues(bottom = 32.dp)
                ) {
                    item {
                        Text("AURA VIP Membership Hierarchy", color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                    }

                    items((1..7).toList()) { tier ->
                        val tierName = when (tier) {
                            1 -> "Baron"
                            2 -> "Viscount"
                            3 -> "Count"
                            4 -> "Marquess"
                            5 -> "Duke"
                            6 -> "Prince"
                            else -> "Sovereign Emperor"
                        }
                        Surface(
                            shape = RoundedCornerShape(14.dp),
                            color = Color(0xFF131C2E),
                            border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFFFD700).copy(alpha = 0.3f)),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier.padding(14.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Surface(
                                    shape = CircleShape,
                                    color = Color(0xFFFFD700).copy(alpha = 0.2f),
                                    modifier = Modifier.size(44.dp)
                                ) {
                                    Box(contentAlignment = Alignment.Center) {
                                        Text("VIP $tier", color = Color(0xFFFFD700), fontSize = 13.sp, fontWeight = FontWeight.Bold)
                                    }
                                }
                                Spacer(modifier = Modifier.width(12.dp))
                                Column(modifier = Modifier.weight(1f)) {
                                    Text("Tier $tier: $tierName", color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                                    Text("Cost: ${tier * 50000} Coins/mo • Exclusive entrance animation, badge glow & priority seat", color = Color.White.copy(alpha = 0.7f), fontSize = 11.sp)
                                }
                            }
                        }
                    }
                }
            }

            // TAB 1: 12 OFFICIAL BADGES & FRAMES (CORE REQUIREMENT)
            1 -> {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(14.dp),
                    contentPadding = PaddingValues(bottom = 32.dp)
                ) {
                    item {
                        Surface(
                            shape = RoundedCornerShape(16.dp),
                            color = Color(0xFF131D31),
                            border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF00E5FF).copy(alpha = 0.4f)),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Default.MilitaryTech, contentDescription = null, tint = Color(0xFF00E5FF), modifier = Modifier.size(26.dp))
                                    Spacer(modifier = Modifier.width(10.dp))
                                    Column {
                                        Text("Assign Official Badge & Avatar Frame", color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                                        Text("Encompasses all 12 Official Roles with custom frames", color = Color.White.copy(alpha = 0.6f), fontSize = 11.sp)
                                    }
                                }

                                Spacer(modifier = Modifier.height(14.dp))

                                OutlinedTextField(
                                    value = targetUserIdForBadge,
                                    onValueChange = { targetUserIdForBadge = it },
                                    label = { Text("Target User ID") },
                                    placeholder = { Text("Enter ID (e.g. 565656565666555)", color = Color.Gray) },
                                    modifier = Modifier.fillMaxWidth().testTag("badge_target_user_id")
                                )

                                Spacer(modifier = Modifier.height(12.dp))
                                Text("Select from the 12 Official Roles:", color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                                Spacer(modifier = Modifier.height(6.dp))

                                // Grid of 12 official roles
                                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                                    AdminService.OFFICIAL_FRAMES.chunked(2).forEach { pair ->
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                                        ) {
                                            pair.forEach { frame ->
                                                val isSelected = selectedBadgeId == frame.id
                                                Surface(
                                                    onClick = { selectedBadgeId = frame.id },
                                                    shape = RoundedCornerShape(8.dp),
                                                    color = if (isSelected) Color(0xFF00E5FF).copy(alpha = 0.2f) else Color(0xFF162238),
                                                    border = androidx.compose.foundation.BorderStroke(
                                                        1.dp,
                                                        if (isSelected) Color(0xFF00E5FF) else Color(0xFF223250)
                                                    ),
                                                    modifier = Modifier.weight(1f)
                                                ) {
                                                    Row(
                                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp),
                                                        verticalAlignment = Alignment.CenterVertically
                                                    ) {
                                                        Text(frame.iconEmoji, fontSize = 16.sp)
                                                        Spacer(modifier = Modifier.width(6.dp))
                                                        Text(
                                                            text = frame.name,
                                                            color = if (isSelected) Color(0xFF00E5FF) else Color.White,
                                                            fontSize = 11.sp,
                                                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
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

                                Spacer(modifier = Modifier.height(12.dp))
                                Text("Select Validity Duration:", color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    listOf(7 to "7 Days", 30 to "30 Days", 90 to "90 Days", 365 to "1 Year").forEach { (days, label) ->
                                        FilterChip(
                                            selected = selectedBadgeDurationDays == days,
                                            onClick = { selectedBadgeDurationDays = days },
                                            label = { Text(label, fontSize = 11.sp) },
                                            colors = FilterChipDefaults.filterChipColors(
                                                selectedContainerColor = Color(0xFF00E5FF),
                                                selectedLabelColor = Color.Black
                                            )
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.height(14.dp))

                                Button(
                                    onClick = {
                                        onAssignBadge(targetUserIdForBadge.trim(), selectedBadgeId, selectedBadgeDurationDays)
                                        targetUserIdForBadge = ""
                                    },
                                    enabled = targetUserIdForBadge.isNotBlank(),
                                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF00E5FF)),
                                    shape = RoundedCornerShape(10.dp),
                                    modifier = Modifier.fillMaxWidth().testTag("assign_official_badge_btn")
                                ) {
                                    Text("Grant Official Badge & Frame 🎖️", color = Color.Black, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }

                    // Active Assignments
                    item {
                        Text(
                            text = "Currently Active Official Badges (${officialAssignments.size})",
                            color = Color.White,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(top = 8.dp)
                        )
                    }

                    items(officialAssignments, key = { it.id }) { assign ->
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = Color(0xFF131C2E),
                            border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF00E5FF).copy(alpha = 0.3f)),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier.padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Surface(
                                    shape = CircleShape,
                                    color = Color(0xFF00E5FF).copy(alpha = 0.2f),
                                    modifier = Modifier.size(38.dp)
                                ) {
                                    Box(contentAlignment = Alignment.Center) {
                                        Text("🎖️", fontSize = 18.sp)
                                    }
                                }
                                Spacer(modifier = Modifier.width(10.dp))
                                Column(modifier = Modifier.weight(1f)) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Text(assign.userName, color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Surface(shape = RoundedCornerShape(4.dp), color = Color(0xFF00E5FF)) {
                                            Text(assign.frameName, color = Color.Black, fontSize = 9.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp))
                                        }
                                    }
                                    Text("User ID: ${assign.userId} • Granted by ${assign.adminName}", color = Color.White.copy(alpha = 0.6f), fontSize = 11.sp)
                                    Text("Valid until: ${assign.expiryDateFormatted}", color = Color(0xFFFFD700), fontSize = 10.sp)
                                }

                                Button(
                                    onClick = { onRevokeBadge(assign.id) },
                                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF5252).copy(alpha = 0.2f)),
                                    shape = RoundedCornerShape(8.dp),
                                    contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp)
                                ) {
                                    Text("Revoke", color = Color(0xFFFF5252), fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }
                }
            }

            // TAB 2: BANS & SECURITY
            2 -> {
                val bannedUsers = remember(allUsers) { allUsers.filter { it.isBanned } }
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                    contentPadding = PaddingValues(bottom = 32.dp)
                ) {
                    item {
                        Text("Banned Accounts & Security Enforcement (${bannedUsers.size})", color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                    }

                    if (bannedUsers.isEmpty()) {
                        item {
                            Text("No users are currently banned.", color = Color.White.copy(alpha = 0.5f), fontSize = 13.sp, modifier = Modifier.padding(vertical = 14.dp))
                        }
                    } else {
                        items(bannedUsers, key = { it.id }) { user ->
                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = Color(0xFF22161A),
                                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFFF5252).copy(alpha = 0.4f)),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Row(
                                    modifier = Modifier.padding(12.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(Icons.Default.Block, contentDescription = null, tint = Color(0xFFFF5252))
                                    Spacer(modifier = Modifier.width(10.dp))
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(user.username, color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                                        Text("ID: ${user.id} • Balance: ${user.coins} Coins", color = Color.White.copy(alpha = 0.7f), fontSize = 11.sp)
                                        Text("Status: Suspended by Owner", color = Color(0xFFFF5252), fontSize = 10.sp)
                                    }
                                    Button(
                                        onClick = { onToggleBanUser(user.id, false, "Unbanned by Owner") },
                                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF00E676)),
                                        shape = RoundedCornerShape(8.dp),
                                        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp)
                                    ) {
                                        Text("Unban", color = Color.Black, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // TAB 3: REPORTS
            3 -> {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                    contentPadding = PaddingValues(bottom = 32.dp)
                ) {
                    item {
                        Text("Moderation & User Complaints (${reports.size})", color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                    }

                    if (reports.isEmpty()) {
                        item {
                            Text("No active reports in queue. Moderation inbox is clean!", color = Color.White.copy(alpha = 0.5f), fontSize = 13.sp)
                        }
                    } else {
                        items(reports, key = { it.id }) { rep ->
                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = Color(0xFF131C2E),
                                border = androidx.compose.foundation.BorderStroke(1.dp, if (rep.status == "Pending") Color(0xFFFFAB00) else Color(0xFF223250)),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Column(modifier = Modifier.padding(12.dp)) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text("Report #${rep.id.take(8)}", color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                                        Surface(
                                            shape = RoundedCornerShape(4.dp),
                                            color = if (rep.status == "Pending") Color(0xFFFFAB00) else Color(0xFF00E676)
                                        ) {
                                            Text(rep.status, color = Color.Black, fontSize = 10.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp))
                                        }
                                    }

                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text("Target ${rep.targetType}: ${rep.targetId} • Reason: ${rep.reason}", color = Color(0xFFFF2A85), fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                                    Text(rep.details, color = Color.White.copy(alpha = 0.8f), fontSize = 12.sp)

                                    if (rep.status == "Pending") {
                                        Spacer(modifier = Modifier.height(8.dp))
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                                        ) {
                                            Button(
                                                onClick = { onResolveReport(rep.id, "Resolved - Action Taken", "Warning issued / Account handled") },
                                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF00E676)),
                                                shape = RoundedCornerShape(8.dp),
                                                modifier = Modifier.weight(1f)
                                            ) {
                                                Text("Resolve & Warn", color = Color.Black, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                            }
                                            Button(
                                                onClick = { onResolveReport(rep.id, "Dismissed", "No violation found") },
                                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2E3D5C)),
                                                shape = RoundedCornerShape(8.dp),
                                                modifier = Modifier.weight(1f)
                                            ) {
                                                Text("Dismiss", color = Color.White, fontSize = 11.sp)
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
}
