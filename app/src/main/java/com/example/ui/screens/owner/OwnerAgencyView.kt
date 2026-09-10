package com.example.ui.screens.owner

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import coil.compose.AsyncImage
import com.example.data.model.Agency
import com.example.data.model.User

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OwnerAgencyView(
    agencies: List<Agency>,
    allUsers: List<User>,
    onCreateAgency: (name: String, code: String, ownerId: String, announcement: String) -> Unit,
    onDeleteAgency: (agencyId: String, reason: String) -> Unit
) {
    var searchQuery by remember { mutableStateOf("") }
    val agencyStatusMap = remember { mutableStateMapOf<String, String>() }
    val agencyTargetMap = remember { mutableStateMapOf<String, Long>() }

    // Dialog States
    var showCreateAgencyDialog by remember { mutableStateOf(false) }
    var inspectingAgency by remember { mutableStateOf<Agency?>(null) }
    var editingAgency by remember { mutableStateOf<Agency?>(null) }
    var settingTargetAgency by remember { mutableStateOf<Agency?>(null) }
    var managingMembersAgency by remember { mutableStateOf<Agency?>(null) }
    var addingLeaderAgency by remember { mutableStateOf<Agency?>(null) }
    var agencyToDelete by remember { mutableStateOf<Agency?>(null) }

    // Create Agency Form
    var newAgencyName by remember { mutableStateOf("") }
    var newAgencyCode by remember { mutableStateOf("") }
    var newAgencyLeaderId by remember { mutableStateOf("565656565666555") }
    var newAgencyAnnouncement by remember { mutableStateOf("Welcome to official AURA talent agency!") }

    // Edit Agency Form
    var editAgencyName by remember { mutableStateOf("") }
    var editAgencyAnnouncement by remember { mutableStateOf("") }

    // Target Form
    var targetDiamondsInput by remember { mutableStateOf("1000000") }

    // Delete reason
    var deleteReasonInput by remember { mutableStateOf("Owner administrative decision") }

    val filteredAgencies = remember(agencies, searchQuery) {
        agencies.filter {
            searchQuery.isBlank() ||
                    it.name.contains(searchQuery, ignoreCase = true) ||
                    it.agencyCode.contains(searchQuery, ignoreCase = true) ||
                    it.ownerName.contains(searchQuery, ignoreCase = true)
        }
    }

    // Agency Dashboard KPIs
    val totalAgenciesCount = agencies.size
    val activeAgenciesCount = agencies.count { agencyStatusMap[it.id] != "Inactive" }
    val totalMembersCount = agencies.sumOf { it.memberCount }
    val totalEarningsCount = agencies.sumOf { it.totalIncome }

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
                    text = "Agency Management",
                    color = Color.White,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Talent Networks & Official Guilds",
                    color = Color(0xFF9C27B0),
                    fontSize = 12.sp
                )
            }

            Button(
                onClick = { showCreateAgencyDialog = true },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF9C27B0)),
                shape = RoundedCornerShape(10.dp),
                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                modifier = Modifier.testTag("create_agency_btn")
            ) {
                Icon(Icons.Default.AddBusiness, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text("Create Agency", fontSize = 12.sp, fontWeight = FontWeight.Bold)
            }
        }

        // Agency Dashboard (5 Metrics: Total Agencies, Active Agencies, Total Members, Revenue, Targets)
        Surface(
            shape = RoundedCornerShape(14.dp),
            color = Color(0xFF131D31),
            border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF9C27B0).copy(alpha = 0.4f)),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(12.dp)) {
                Text(
                    text = "Agency Overview",
                    color = Color(0xFFE1BEE7),
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    AgencyKpiItem("Total", "$totalAgenciesCount", Color(0xFF9C27B0), Modifier.weight(1f))
                    AgencyKpiItem("Active", "$activeAgenciesCount", Color(0xFF00E676), Modifier.weight(1f))
                    AgencyKpiItem("Members", "$totalMembersCount", Color(0xFF00E5FF), Modifier.weight(1f))
                    AgencyKpiItem("Revenue", "%,d 💎".format(totalEarningsCount), Color(0xFFFFD700), Modifier.weight(1.3f))
                    AgencyKpiItem("Target Hit", "88%", Color(0xFFFF9100), Modifier.weight(1f))
                }
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Search Bar
        OutlinedTextField(
            value = searchQuery,
            onValueChange = { searchQuery = it },
            placeholder = { Text("Search agency by name, code, or leader...", color = Color.White.copy(alpha = 0.5f), fontSize = 12.sp) },
            leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = Color(0xFF9C27B0)) },
            singleLine = true,
            shape = RoundedCornerShape(10.dp),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(10.dp))

        // Agency List
        if (filteredAgencies.isEmpty()) {
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = Color(0xFF131D31),
                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF223250)),
                modifier = Modifier.fillMaxWidth().padding(vertical = 12.dp)
            ) {
                Text(
                    text = "No agencies found. Click 'Create Agency' to register a new talent family.",
                    color = Color.White.copy(alpha = 0.5f),
                    fontSize = 12.sp,
                    modifier = Modifier.padding(16.dp),
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                )
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                contentPadding = PaddingValues(bottom = 32.dp)
            ) {
                items(filteredAgencies, key = { it.id }) { agency ->
                    val status = agencyStatusMap[agency.id] ?: "Active"
                    val isActive = status == "Active"
                    val target = agencyTargetMap[agency.id] ?: 500000L

                    Surface(
                        shape = RoundedCornerShape(14.dp),
                        color = Color(0xFF131D31),
                        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF9C27B0).copy(alpha = 0.35f)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                AsyncImage(
                                    model = agency.logoUrl.ifBlank { "https://images.unsplash.com/photo-1557804506-669a67965ba0?w=200" },
                                    contentDescription = null,
                                    modifier = Modifier.size(46.dp).clip(CircleShape).border(2.dp, Color(0xFF9C27B0), CircleShape)
                                )

                                Spacer(modifier = Modifier.width(12.dp))

                                Column(modifier = Modifier.weight(1f)) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Text(agency.name, color = Color.White, fontSize = 15.sp, fontWeight = FontWeight.Bold)
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Surface(
                                            shape = RoundedCornerShape(4.dp),
                                            color = Color(0xFF9C27B0)
                                        ) {
                                            Text(
                                                agency.agencyCode,
                                                color = Color.White,
                                                fontSize = 9.sp,
                                                fontWeight = FontWeight.Bold,
                                                modifier = Modifier.padding(horizontal = 5.dp, vertical = 2.dp)
                                            )
                                        }
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Surface(
                                            shape = RoundedCornerShape(4.dp),
                                            color = if (isActive) Color(0xFF00E676).copy(alpha = 0.15f) else Color(0xFFFF5252).copy(alpha = 0.15f)
                                        ) {
                                            Text(
                                                status,
                                                color = if (isActive) Color(0xFF00E676) else Color(0xFFFF5252),
                                                fontSize = 9.sp,
                                                fontWeight = FontWeight.Bold,
                                                modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp)
                                            )
                                        }
                                    }

                                    Text("Leader: ${agency.ownerName} • Members: ${agency.memberCount}", color = Color.White.copy(alpha = 0.7f), fontSize = 11.sp)
                                    Text("Earnings: %,d 💎 • Target: %,d 💎".format(agency.totalIncome, target), color = Color(0xFFFFD700), fontSize = 11.sp)
                                }

                                // Status toggle: Activate / Deactivate
                                Button(
                                    onClick = {
                                        agencyStatusMap[agency.id] = if (isActive) "Inactive" else "Active"
                                    },
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = if (isActive) Color(0xFFFFAB00).copy(alpha = 0.2f) else Color(0xFF00E676).copy(alpha = 0.2f)
                                    ),
                                    shape = RoundedCornerShape(8.dp),
                                    contentPadding = PaddingValues(horizontal = 6.dp, vertical = 4.dp)
                                ) {
                                    Text(
                                        if (isActive) "Deactivate" else "Activate",
                                        color = if (isActive) Color(0xFFFFAB00) else Color(0xFF00E676),
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.SemiBold
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(10.dp))
                            Divider(color = Color(0xFF1E2D4A), thickness = 0.5.dp)
                            Spacer(modifier = Modifier.height(8.dp))

                            // Action buttons: View Agency, Edit, Set Target, Members, Delete
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Button(
                                    onClick = { inspectingAgency = agency },
                                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF9C27B0).copy(alpha = 0.2f)),
                                    shape = RoundedCornerShape(8.dp),
                                    contentPadding = PaddingValues(horizontal = 6.dp, vertical = 4.dp),
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Text("View", color = Color(0xFFE1BEE7), fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                }

                                Button(
                                    onClick = {
                                        editingAgency = agency
                                        editAgencyName = agency.name
                                        editAgencyAnnouncement = agency.announcement
                                    },
                                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1E2D4A)),
                                    shape = RoundedCornerShape(8.dp),
                                    contentPadding = PaddingValues(horizontal = 6.dp, vertical = 4.dp),
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Text("Edit", color = Color.White, fontSize = 11.sp)
                                }

                                Button(
                                    onClick = {
                                        settingTargetAgency = agency
                                        targetDiamondsInput = target.toString()
                                    },
                                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1E2D4A)),
                                    shape = RoundedCornerShape(8.dp),
                                    contentPadding = PaddingValues(horizontal = 6.dp, vertical = 4.dp),
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Text("Target", color = Color(0xFFFFD700), fontSize = 11.sp)
                                }

                                Button(
                                    onClick = { managingMembersAgency = agency },
                                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1E2D4A)),
                                    shape = RoundedCornerShape(8.dp),
                                    contentPadding = PaddingValues(horizontal = 6.dp, vertical = 4.dp),
                                    modifier = Modifier.weight(1.1f)
                                ) {
                                    Text("Members", color = Color(0xFF00E5FF), fontSize = 11.sp)
                                }

                                Button(
                                    onClick = {
                                        agencyToDelete = agency
                                        deleteReasonInput = "Dissolved by Owner"
                                    },
                                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF5252).copy(alpha = 0.2f)),
                                    shape = RoundedCornerShape(8.dp),
                                    contentPadding = PaddingValues(horizontal = 6.dp, vertical = 4.dp),
                                    modifier = Modifier.weight(0.9f)
                                ) {
                                    Text("Delete", color = Color(0xFFFF5252), fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    // CREATE AGENCY MODAL
    if (showCreateAgencyDialog) {
        AlertDialog(
            onDismissRequest = { showCreateAgencyDialog = false },
            confirmButton = {
                Button(
                    onClick = {
                        onCreateAgency(newAgencyName, newAgencyCode, newAgencyLeaderId, newAgencyAnnouncement)
                        showCreateAgencyDialog = false
                    },
                    enabled = newAgencyName.isNotBlank() && newAgencyLeaderId.isNotBlank(),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF9C27B0))
                ) {
                    Text("Create Agency", color = Color.White, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showCreateAgencyDialog = false }) { Text("Cancel") }
            },
            title = { Text("Create Official Agency", color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.Bold) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = newAgencyName,
                        onValueChange = { newAgencyName = it },
                        label = { Text("Agency Name") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = newAgencyCode,
                        onValueChange = { newAgencyCode = it },
                        label = { Text("Agency Code (e.g. AG777)") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = newAgencyLeaderId,
                        onValueChange = { newAgencyLeaderId = it },
                        label = { Text("Leader User ID") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = newAgencyAnnouncement,
                        onValueChange = { newAgencyAnnouncement = it },
                        label = { Text("Announcement") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            containerColor = Color(0xFF131D31)
        )
    }

    // VIEW AGENCY DETAILS MODAL/SHEET
    inspectingAgency?.let { agency ->
        val target = agencyTargetMap[agency.id] ?: 500000L
        AlertDialog(
            onDismissRequest = { inspectingAgency = null },
            confirmButton = {
                TextButton(onClick = { inspectingAgency = null }) { Text("Close", color = Color(0xFF9C27B0)) }
            },
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Business, contentDescription = null, tint = Color(0xFF9C27B0))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Agency Details: ${agency.name}", color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                }
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = Color(0xFF0A0F1D),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(10.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                            Text("Agency ID: ${agency.id} • Code: ${agency.agencyCode}", color = Color(0xFF00E5FF), fontSize = 11.sp)
                            Text("Leader: ${agency.ownerName} (ID: ${agency.ownerId})", color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            Text("Announcement: ${agency.announcement}", color = Color.White.copy(alpha = 0.8f), fontSize = 11.sp)
                            Text("Target Monthly: %,d 💎 • Earnings: %,d 💎".format(target, agency.totalIncome), color = Color(0xFFFFD700), fontSize = 11.sp)
                            Text("Members Count: ${agency.memberCount} Streamers", color = Color(0xFF00E676), fontSize = 11.sp)
                        }
                    }

                    Text("Active Hosts & Members", color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = Color(0xFF0A0F1D),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(10.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                            Text("1. Zara Official • 42h Streamed • 112k 💎", color = Color.White.copy(alpha = 0.8f), fontSize = 11.sp)
                            Text("2. Farhan Beats • 18h Streamed • 34k 💎", color = Color.White.copy(alpha = 0.8f), fontSize = 11.sp)
                            Text("3. DJ Malik • 36h Streamed • 88k 💎", color = Color.White.copy(alpha = 0.8f), fontSize = 11.sp)
                        }
                    }

                    Text("Activity History", color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                    Text("• Monthly Target achieved for previous cycle (120%)\n• 2 New hosts onboarded this week\n• Payout disbursed to Agency Wallet", color = Color.White.copy(alpha = 0.65f), fontSize = 11.sp)
                }
            },
            containerColor = Color(0xFF131D31)
        )
    }

    // SET TARGET MODAL
    settingTargetAgency?.let { agency ->
        AlertDialog(
            onDismissRequest = { settingTargetAgency = null },
            confirmButton = {
                Button(
                    onClick = {
                        val diamonds = targetDiamondsInput.toLongOrNull() ?: 500000L
                        agencyTargetMap[agency.id] = diamonds
                        settingTargetAgency = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFFD700))
                ) {
                    Text("Save Target", color = Color.Black, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = { TextButton(onClick = { settingTargetAgency = null }) { Text("Cancel") } },
            title = { Text("Set Agency Target: ${agency.name}", color = Color(0xFFFFD700), fontSize = 16.sp, fontWeight = FontWeight.Bold) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("Set diamond performance goal for this agency:", color = Color.White.copy(alpha = 0.8f), fontSize = 12.sp)
                    OutlinedTextField(
                        value = targetDiamondsInput,
                        onValueChange = { targetDiamondsInput = it },
                        label = { Text("Target Diamonds") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            containerColor = Color(0xFF131D31)
        )
    }

    // MANAGE MEMBERS MODAL
    managingMembersAgency?.let { agency ->
        AlertDialog(
            onDismissRequest = { managingMembersAgency = null },
            confirmButton = {
                TextButton(onClick = { managingMembersAgency = null }) { Text("Done", color = Color(0xFF00E5FF)) }
            },
            title = { Text("Members: ${agency.name}", color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.Bold) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("Roster of active hosts & creators assigned to this agency:", color = Color.White.copy(alpha = 0.7f), fontSize = 11.sp)
                    listOf(
                        Triple("Zara Official", "host_101", "42 Stream Hours"),
                        Triple("Farhan Beats", "host_104", "18 Stream Hours"),
                        Triple("DJ Malik", "host_108", "36 Stream Hours")
                    ).forEach { (name, id, perf) ->
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = Color(0xFF0A0F1D),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier.padding(10.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column {
                                    Text(name, color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                    Text("ID: $id • $perf", color = Color.White.copy(alpha = 0.5f), fontSize = 10.sp)
                                }
                                Text("Active", color = Color(0xFF00E676), fontSize = 10.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            },
            containerColor = Color(0xFF131D31)
        )
    }

    // DELETE AGENCY MODAL
    agencyToDelete?.let { agency ->
        AlertDialog(
            onDismissRequest = { agencyToDelete = null },
            confirmButton = {
                Button(
                    onClick = {
                        onDeleteAgency(agency.id, deleteReasonInput)
                        agencyToDelete = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF5252))
                ) {
                    Text("Confirm Delete", fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = { TextButton(onClick = { agencyToDelete = null }) { Text("Cancel") } },
            title = { Text("Delete Agency: ${agency.name}", color = Color(0xFFFF5252), fontSize = 16.sp, fontWeight = FontWeight.Bold) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("This action will dissolve the agency and detach its members.", color = Color.White.copy(alpha = 0.8f), fontSize = 12.sp)
                    OutlinedTextField(
                        value = deleteReasonInput,
                        onValueChange = { deleteReasonInput = it },
                        label = { Text("Reason for Deletion") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            containerColor = Color(0xFF131D31)
        )
    }
}

@Composable
fun AgencyKpiItem(
    title: String,
    value: String,
    accent: Color,
    modifier: Modifier = Modifier
) {
    Surface(
        shape = RoundedCornerShape(8.dp),
        color = Color(0xFF0A0F1D),
        border = androidx.compose.foundation.BorderStroke(1.dp, accent.copy(alpha = 0.3f)),
        modifier = modifier
    ) {
        Column(modifier = Modifier.padding(6.dp), horizontalAlignment = Alignment.CenterHorizontally) {
            Text(title, color = Color.White.copy(alpha = 0.6f), fontSize = 9.sp)
            Spacer(modifier = Modifier.height(2.dp))
            Text(value, color = accent, fontSize = 11.sp, fontWeight = FontWeight.Bold, maxLines = 1)
        }
    }
}
