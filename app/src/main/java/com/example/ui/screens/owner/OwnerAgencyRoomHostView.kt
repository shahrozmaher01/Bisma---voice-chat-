package com.example.ui.screens.owner

import androidx.compose.animation.AnimatedVisibility
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
import coil.compose.AsyncImage
import com.example.data.model.Agency
import com.example.data.model.VoiceRoom

data class HostItem(
    val id: String,
    val name: String,
    val agencyName: String,
    val targetHours: Int,
    val completedHours: Int,
    val targetDiamonds: Long,
    val earnedDiamonds: Long,
    val status: String
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OwnerAgencyRoomHostView(
    agencies: List<Agency>,
    rooms: List<VoiceRoom>,
    onCreateAgency: (name: String, code: String, ownerId: String, announcement: String) -> Unit,
    onDeleteAgency: (agencyId: String, reason: String) -> Unit,
    onCreateRoom: (title: String, ownerId: String, category: String, seatCount: Int) -> Unit,
    onDeleteRoom: (roomId: String, reason: String) -> Unit
) {
    var activeSubTab by remember { mutableStateOf(0) } // 0: Rooms, 1: Agencies, 2: Hosts

    // Create Room State
    var showCreateRoomDialog by remember { mutableStateOf(false) }
    var newRoomTitle by remember { mutableStateOf("") }
    var newRoomOwnerId by remember { mutableStateOf("565656565666555") }
    var newRoomCategory by remember { mutableStateOf("Chat & Music") }
    var newRoomSeatCount by remember { mutableStateOf(8) }

    // Create Agency State
    var showCreateAgencyDialog by remember { mutableStateOf(false) }
    var newAgencyName by remember { mutableStateOf("") }
    var newAgencyCode by remember { mutableStateOf("") }
    var newAgencyLeaderId by remember { mutableStateOf("565656565666555") }
    var newAgencyAnnouncement by remember { mutableStateOf("Welcome to official talent family!") }

    // Sample hosts list
    val mockHosts = remember {
        listOf(
            HostItem("host_101", "Zara Official", "Empire Stars", 40, 36, 100000, 112000, "Active"),
            HostItem("host_102", "Alex Vocalist", "AURA Elite", 40, 28, 80000, 67500, "Active"),
            HostItem("host_103", "Elena Melody", "Royal Voice", 50, 48, 150000, 195000, "Active"),
            HostItem("host_104", "Farhan Beats", "Empire Stars", 30, 12, 50000, 24000, "Needs Attention"),
            HostItem("host_105", "Maya DJ", "AURA Elite", 40, 40, 120000, 142000, "Active")
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
            listOf("🎙️ Voice Rooms (${rooms.size})", "🏢 Agencies (${agencies.size})", "⭐ Streamer Hosts (${mockHosts.size})").forEachIndexed { index, label ->
                val isSelected = activeSubTab == index
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(10.dp))
                        .background(if (isSelected) Color(0xFFFF2A85) else Color.Transparent)
                        .clickable { activeSubTab = index }
                        .padding(vertical = 8.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = label,
                        color = if (isSelected) Color.White else Color.White.copy(alpha = 0.7f),
                        fontSize = 11.sp,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                    )
                }
            }
        }

        when (activeSubTab) {
            // TAB 0: VOICE ROOMS
            0 -> {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(bottom = 10.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Managed Voice Rooms", color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                    Button(
                        onClick = { showCreateRoomDialog = true },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF2A85)),
                        shape = RoundedCornerShape(10.dp),
                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                        modifier = Modifier.testTag("owner_create_room_btn")
                    ) {
                        Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("New Room", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                }

                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                    contentPadding = PaddingValues(bottom = 32.dp)
                ) {
                    items(rooms, key = { it.id }) { room ->
                        Surface(
                            shape = RoundedCornerShape(14.dp),
                            color = Color(0xFF131C2E),
                            border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF223250)),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier.padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                AsyncImage(
                                    model = room.coverUrl.ifBlank { "https://images.unsplash.com/photo-1518709268805-4e9042af9f23?w=300" },
                                    contentDescription = null,
                                    modifier = Modifier.size(50.dp).clip(RoundedCornerShape(8.dp))
                                )
                                Spacer(modifier = Modifier.width(12.dp))
                                Column(modifier = Modifier.weight(1f)) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Text(room.title, color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                                        if (room.isActive) {
                                            Spacer(modifier = Modifier.width(6.dp))
                                            Surface(shape = RoundedCornerShape(4.dp), color = Color(0xFF00E676)) {
                                                Text("LIVE", color = Color.Black, fontSize = 9.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp))
                                            }
                                        }
                                    }
                                    Text("Owner: ${room.ownerName} • ID: ${room.id}", color = Color.White.copy(alpha = 0.6f), fontSize = 11.sp)
                                    Text("${room.seatCount} Seats • ${room.onlineCount} Online • Cat: ${room.category}", color = Color(0xFF00E5FF), fontSize = 11.sp)
                                }

                                IconButton(
                                    onClick = { onDeleteRoom(room.id, "Closed by Owner Directive") },
                                    modifier = Modifier.testTag("delete_room_${room.id}")
                                ) {
                                    Icon(Icons.Default.Delete, contentDescription = "Close/Delete", tint = Color(0xFFFF5252))
                                }
                            }
                        }
                    }
                }
            }

            // TAB 1: AGENCIES
            1 -> {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(bottom = 10.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Registered Agencies", color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                    Button(
                        onClick = { showCreateAgencyDialog = true },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF9C27B0)),
                        shape = RoundedCornerShape(10.dp),
                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                        modifier = Modifier.testTag("owner_create_agency_btn")
                    ) {
                        Icon(Icons.Default.AddBusiness, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("New Agency", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                }

                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                    contentPadding = PaddingValues(bottom = 32.dp)
                ) {
                    items(agencies, key = { it.id }) { agency ->
                        Surface(
                            shape = RoundedCornerShape(14.dp),
                            color = Color(0xFF131C2E),
                            border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF9C27B0).copy(alpha = 0.3f)),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier.padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                AsyncImage(
                                    model = agency.logoUrl.ifBlank { "https://images.unsplash.com/photo-1557804506-669a67965ba0?w=200" },
                                    contentDescription = null,
                                    modifier = Modifier.size(50.dp).clip(CircleShape)
                                )
                                Spacer(modifier = Modifier.width(12.dp))
                                Column(modifier = Modifier.weight(1f)) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Text(agency.name, color = Color.White, fontSize = 15.sp, fontWeight = FontWeight.Bold)
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Surface(shape = RoundedCornerShape(4.dp), color = Color(0xFF9C27B0)) {
                                            Text(agency.agencyCode, color = Color.White, fontSize = 9.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp))
                                        }
                                    }
                                    Text("Leader: ${agency.ownerName} • Members: ${agency.memberCount}", color = Color.White.copy(alpha = 0.7f), fontSize = 11.sp)
                                    Text("Revenue: ${agency.totalIncome} 💎 • Level ${agency.level}", color = Color(0xFFFFD700), fontSize = 11.sp)
                                }

                                IconButton(
                                    onClick = { onDeleteAgency(agency.id, "Owner administrative dissolve") },
                                    modifier = Modifier.testTag("delete_agency_${agency.id}")
                                ) {
                                    Icon(Icons.Default.DeleteOutline, contentDescription = "Delete", tint = Color(0xFFFF5252))
                                }
                            }
                        }
                    }
                }
            }

            // TAB 2: HOSTS
            2 -> {
                Text(
                    text = "Official Host Performance & Targets",
                    color = Color.White,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(bottom = 10.dp)
                )

                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                    contentPadding = PaddingValues(bottom = 32.dp)
                ) {
                    items(mockHosts, key = { it.id }) { host ->
                        Surface(
                            shape = RoundedCornerShape(14.dp),
                            color = Color(0xFF131C2E),
                            border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF223250)),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Surface(shape = CircleShape, color = Color(0xFFFF9100).copy(alpha = 0.2f), modifier = Modifier.size(36.dp)) {
                                            Box(contentAlignment = Alignment.Center) {
                                                Icon(Icons.Default.Star, contentDescription = null, tint = Color(0xFFFF9100), modifier = Modifier.size(18.dp))
                                            }
                                        }
                                        Spacer(modifier = Modifier.width(10.dp))
                                        Column {
                                            Text(host.name, color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                                            Text("Agency: ${host.agencyName}", color = Color.White.copy(alpha = 0.6f), fontSize = 11.sp)
                                        }
                                    }

                                    Surface(
                                        shape = RoundedCornerShape(6.dp),
                                        color = if (host.status == "Active") Color(0xFF00E676).copy(alpha = 0.2f) else Color(0xFFFFAB00).copy(alpha = 0.2f)
                                    ) {
                                        Text(
                                            text = host.status,
                                            color = if (host.status == "Active") Color(0xFF00E676) else Color(0xFFFFAB00),
                                            fontSize = 10.sp,
                                            fontWeight = FontWeight.Bold,
                                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.height(10.dp))

                                // Progress bars for Target Hours and Diamonds
                                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text("Streaming Hours: ${host.completedHours}/${host.targetHours}h", color = Color.White.copy(alpha = 0.7f), fontSize = 10.sp)
                                        Spacer(modifier = Modifier.height(3.dp))
                                        LinearProgressIndicator(
                                            progress = { (host.completedHours.toFloat() / host.targetHours.toFloat()).coerceIn(0f, 1f) },
                                            modifier = Modifier.fillMaxWidth().height(4.dp).clip(RoundedCornerShape(2.dp)),
                                            color = Color(0xFF00E5FF),
                                            trackColor = Color(0xFF1F2937)
                                        )
                                    }
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text("Income: %,d/%,d 💎".format(host.earnedDiamonds, host.targetDiamonds), color = Color.White.copy(alpha = 0.7f), fontSize = 10.sp)
                                        Spacer(modifier = Modifier.height(3.dp))
                                        LinearProgressIndicator(
                                            progress = { (host.earnedDiamonds.toFloat() / host.targetDiamonds.toFloat()).coerceIn(0f, 1f) },
                                            modifier = Modifier.fillMaxWidth().height(4.dp).clip(RoundedCornerShape(2.dp)),
                                            color = Color(0xFFFFD700),
                                            trackColor = Color(0xFF1F2937)
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

    // Create Room Dialog
    if (showCreateRoomDialog) {
        AlertDialog(
            onDismissRequest = { showCreateRoomDialog = false },
            title = { Text("Create Voice Room", fontWeight = FontWeight.Bold, color = Color.White) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    OutlinedTextField(
                        value = newRoomTitle,
                        onValueChange = { newRoomTitle = it },
                        label = { Text("Room Title") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = newRoomOwnerId,
                        onValueChange = { newRoomOwnerId = it },
                        label = { Text("Owner User ID") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        listOf(4, 8, 12).forEach { count ->
                            FilterChip(
                                selected = newRoomSeatCount == count,
                                onClick = { newRoomSeatCount = count },
                                label = { Text("$count Seats") }
                            )
                        }
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        onCreateRoom(newRoomTitle.ifBlank { "VIP Lounge" }, newRoomOwnerId, newRoomCategory, newRoomSeatCount)
                        showCreateRoomDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF2A85))
                ) {
                    Text("Create Room 🎙️", fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showCreateRoomDialog = false }) { Text("Cancel", color = Color.Gray) }
            },
            containerColor = Color(0xFF162238)
        )
    }

    // Create Agency Dialog
    if (showCreateAgencyDialog) {
        AlertDialog(
            onDismissRequest = { showCreateAgencyDialog = false },
            title = { Text("Create Agency", fontWeight = FontWeight.Bold, color = Color.White) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    OutlinedTextField(
                        value = newAgencyName,
                        onValueChange = { newAgencyName = it },
                        label = { Text("Agency Name") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = newAgencyCode,
                        onValueChange = { newAgencyCode = it },
                        label = { Text("Agency Code (e.g. AG8899)") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = newAgencyLeaderId,
                        onValueChange = { newAgencyLeaderId = it },
                        label = { Text("Leader User ID") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        onCreateAgency(newAgencyName.ifBlank { "Golden Star Agency" }, newAgencyCode, newAgencyLeaderId, newAgencyAnnouncement)
                        showCreateAgencyDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF9C27B0))
                ) {
                    Text("Register Agency 🏢", fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showCreateAgencyDialog = false }) { Text("Cancel", color = Color.Gray) }
            },
            containerColor = Color(0xFF162238)
        )
    }
}
