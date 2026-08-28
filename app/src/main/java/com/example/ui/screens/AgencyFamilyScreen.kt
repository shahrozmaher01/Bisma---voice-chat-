package com.example.ui.screens

import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.data.model.Agency
import com.example.data.model.AgencyJoinRequest
import com.example.data.model.User
import com.example.data.repository.BismaRepository
import com.example.ui.components.*
import com.example.ui.theme.*
import kotlinx.coroutines.launch

@Composable
fun AgencyFamilyScreen(
    repository: BismaRepository,
    onBack: () -> Unit
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val agencies by repository.agencies.collectAsState(initial = emptyList())
    val currentUser by repository.currentUser.collectAsState(initial = null)

    var searchQuery by remember { mutableStateOf("") }
    var showCreateAgencyDialog by remember { mutableStateOf(false) }
    var showInviteDialog by remember { mutableStateOf(false) }
    var showEditAgencyDialog by remember { mutableStateOf(false) }

    val userAgency = remember(agencies, currentUser) {
        agencies.firstOrNull { it.id == currentUser?.agencyId || it.ownerId == currentUser?.id }
    }
    val isAgencyOwner = remember(userAgency, currentUser) {
        userAgency != null && userAgency.ownerId == currentUser?.id
    }

    val agencyMembers by repository.getAgencyMembersFlow(userAgency?.id ?: "").collectAsState(initial = emptyList())
    val pendingJoinRequests by repository.getAgencyJoinRequestsFlow(userAgency?.id ?: "").collectAsState(initial = emptyList())

    val filteredAgencies = remember(agencies, searchQuery) {
        if (searchQuery.isBlank()) agencies
        else agencies.filter {
            it.name.contains(searchQuery, ignoreCase = true) ||
            it.id.contains(searchQuery, ignoreCase = true) ||
            it.agencyCode.contains(searchQuery, ignoreCase = true)
        }
    }

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
            // Top Bar
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = Color.White)
                    }
                    Text(
                        text = "Voice Agency Hub 🏢",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }

                if (userAgency == null) {
                    Button(
                        onClick = { showCreateAgencyDialog = true },
                        colors = ButtonDefaults.buttonColors(containerColor = NeonPink),
                        shape = RoundedCornerShape(14.dp),
                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 2.dp),
                        modifier = Modifier.height(34.dp)
                    ) {
                        Icon(Icons.Default.Add, contentDescription = null, tint = Color.White, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Create Agency", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }

            if (userAgency != null) {
                // User has an Agency -> Display Detailed Agency Dashboard
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 14.dp),
                    contentPadding = PaddingValues(top = 8.dp, bottom = 40.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    // Agency Header Banner
                    item {
                        Surface(
                            shape = RoundedCornerShape(20.dp),
                            color = Color.Transparent,
                            border = BorderStroke(1.5.dp, Brush.linearGradient(listOf(NeonPink, ElectricBlue))),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(
                                        Brush.verticalGradient(
                                            listOf(Color(0x44EC4899), Color(0x333B82F6), Color(0x99130E29))
                                        )
                                    )
                                    .padding(16.dp)
                            ) {
                                Column(modifier = Modifier.fillMaxWidth()) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            AsyncImage(
                                                model = userAgency.logoUrl,
                                                contentDescription = userAgency.name,
                                                modifier = Modifier
                                                    .size(60.dp)
                                                    .clip(RoundedCornerShape(14.dp))
                                                    .border(2.dp, NeonPink, RoundedCornerShape(14.dp))
                                            )
                                            Spacer(modifier = Modifier.width(12.dp))
                                            Column {
                                                Text(userAgency.name, color = Color.White, fontWeight = FontWeight.ExtraBold, fontSize = 17.sp)
                                                Text("Agency Code: ${userAgency.agencyCode}", color = ElectricBlue, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                                                Text("Owner: ${userAgency.ownerName}", color = TextSecondary, fontSize = 11.sp)
                                            }
                                        }

                                        if (isAgencyOwner) {
                                            IconButton(onClick = { showEditAgencyDialog = true }) {
                                                Icon(Icons.Default.Edit, contentDescription = "Edit", tint = Color.White)
                                            }
                                        }
                                    }

                                    Spacer(modifier = Modifier.height(12.dp))
                                    Text(
                                        text = "📢 ${userAgency.description.ifBlank { "Welcome to ${userAgency.name}!" }}",
                                        color = TextPrimary,
                                        fontSize = 12.sp
                                    )

                                    Spacer(modifier = Modifier.height(14.dp))

                                    // Agency Stats Row
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                            Text("${agencyMembers.size}", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                                            Text("Active Hosts", color = TextSecondary, fontSize = 11.sp)
                                        }
                                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                            Text("🪙 ${userAgency.totalIncome}", color = GoldYellow, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                                            Text("Agency Income", color = TextSecondary, fontSize = 11.sp)
                                        }
                                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                            Text("Lv ${userAgency.level}", color = NeonPink, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                                            Text("Agency Level", color = TextSecondary, fontSize = 11.sp)
                                        }
                                    }

                                    if (isAgencyOwner) {
                                        Spacer(modifier = Modifier.height(12.dp))
                                        NeonButton(
                                            text = "➕ Invite User to Agency",
                                            onClick = { showInviteDialog = true },
                                            modifier = Modifier.fillMaxWidth()
                                        )
                                    } else {
                                        Spacer(modifier = Modifier.height(12.dp))
                                        OutlinedButton(
                                            onClick = {
                                                coroutineScope.launch {
                                                    repository.leaveAgency(currentUser?.id ?: "")
                                                    Toast.makeText(context, "Left agency", Toast.LENGTH_SHORT).show()
                                                }
                                            },
                                            colors = ButtonDefaults.outlinedButtonColors(contentColor = DarkRed),
                                            border = BorderStroke(1.dp, DarkRed),
                                            shape = RoundedCornerShape(12.dp),
                                            modifier = Modifier.fillMaxWidth()
                                        ) {
                                            Text("Leave Agency", fontSize = 12.sp)
                                        }
                                    }
                                }
                            }
                        }
                    }

                    // Pending Join Requests (Visible to Owner)
                    if (isAgencyOwner && pendingJoinRequests.isNotEmpty()) {
                        item {
                            Text("Pending Join Applications (${pendingJoinRequests.size})", color = GoldYellow, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                        }

                        items(pendingJoinRequests) { req ->
                            Surface(
                                color = SurfaceCard,
                                shape = RoundedCornerShape(12.dp),
                                border = BorderStroke(1.dp, SurfaceCardBorder),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Row(
                                    modifier = Modifier.padding(12.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Column {
                                        Text(req.userName, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                        Text("User ID: ${req.userId}", color = TextSecondary, fontSize = 11.sp)
                                    }

                                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                        Button(
                                            onClick = {
                                                coroutineScope.launch {
                                                    repository.respondToAgencyJoinRequest(req.id, true)
                                                    Toast.makeText(context, "Accepted ${req.userName}!", Toast.LENGTH_SHORT).show()
                                                }
                                            },
                                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF00E676)),
                                            contentPadding = PaddingValues(horizontal = 10.dp, vertical = 2.dp),
                                            shape = RoundedCornerShape(10.dp),
                                            modifier = Modifier.height(30.dp)
                                        ) {
                                            Text("Accept", color = Color.Black, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                        }

                                        Button(
                                            onClick = {
                                                coroutineScope.launch {
                                                    repository.respondToAgencyJoinRequest(req.id, false)
                                                    Toast.makeText(context, "Rejected application", Toast.LENGTH_SHORT).show()
                                                }
                                            },
                                            colors = ButtonDefaults.buttonColors(containerColor = DarkRed),
                                            contentPadding = PaddingValues(horizontal = 10.dp, vertical = 2.dp),
                                            shape = RoundedCornerShape(10.dp),
                                            modifier = Modifier.height(30.dp)
                                        ) {
                                            Text("Reject", color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                        }
                                    }
                                }
                            }
                        }
                    }

                    // Agency Members List
                    item {
                        Text("Agency Hosts & Members (${agencyMembers.size})", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                    }

                    if (agencyMembers.isEmpty()) {
                        item {
                            Text("No other members currently in agency.", color = TextSecondary, fontSize = 12.sp)
                        }
                    } else {
                        items(agencyMembers) { member ->
                            Surface(
                                color = SurfaceCard,
                                shape = RoundedCornerShape(12.dp),
                                border = BorderStroke(1.dp, SurfaceCardBorder),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Row(
                                    modifier = Modifier.padding(12.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        AsyncImage(
                                            model = member.avatarUrl,
                                            contentDescription = member.username,
                                            modifier = Modifier.size(40.dp).clip(CircleShape)
                                        )
                                        Spacer(modifier = Modifier.width(10.dp))
                                        Column {
                                            Text(member.username, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                            Text("ID: ${member.id} • Lv ${member.userLevel}", color = TextSecondary, fontSize = 11.sp)
                                        }
                                    }

                                    if (isAgencyOwner && member.id != currentUser?.id) {
                                        IconButton(
                                            onClick = {
                                                coroutineScope.launch {
                                                    repository.removeUserFromAgency(userAgency.id, member.id)
                                                    Toast.makeText(context, "Removed ${member.username}", Toast.LENGTH_SHORT).show()
                                                }
                                            }
                                        ) {
                                            Icon(Icons.Default.PersonRemove, contentDescription = "Remove", tint = DarkRed)
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            } else {
                // User has no agency -> Search, Explore & Apply
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 14.dp),
                    contentPadding = PaddingValues(top = 8.dp, bottom = 40.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    item {
                        GlassCard(modifier = Modifier.fillMaxWidth()) {
                            Column {
                                Text("Agency Talent Program 🌟", color = GoldYellow, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                Spacer(modifier = Modifier.height(4.dp))
                                Text("Join a verified voice talent agency to unlock host salaries, official badge perks, and exclusive event invitations.", color = TextSecondary, fontSize = 11.5.sp)
                            }
                        }
                    }

                    item {
                        OutlinedTextField(
                            value = searchQuery,
                            onValueChange = { searchQuery = it },
                            placeholder = { Text("Search agency by ID, code, or name...") },
                            leadingIcon = {
                                Icon(Icons.Default.Search, contentDescription = null, tint = NeonPink)
                            },
                            modifier = Modifier.fillMaxWidth(),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = NeonPink,
                                unfocusedBorderColor = SurfaceCardBorder,
                                focusedTextColor = TextPrimary,
                                unfocusedTextColor = TextPrimary
                            ),
                            singleLine = true
                        )
                    }

                    if (filteredAgencies.isEmpty()) {
                        item {
                            Box(
                                modifier = Modifier.fillMaxWidth().padding(32.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text("No agencies found. Create your own agency!", color = TextSecondary, fontSize = 13.sp)
                            }
                        }
                    } else {
                        items(filteredAgencies) { ag ->
                            Surface(
                                color = SurfaceCard,
                                shape = RoundedCornerShape(14.dp),
                                border = BorderStroke(1.dp, SurfaceCardBorder),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Row(
                                    modifier = Modifier.padding(12.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        AsyncImage(
                                            model = ag.logoUrl,
                                            contentDescription = ag.name,
                                            modifier = Modifier.size(46.dp).clip(CircleShape)
                                        )
                                        Spacer(modifier = Modifier.width(10.dp))
                                        Column {
                                            Text(text = ag.name, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                            Text(text = "Code: ${ag.agencyCode} • ${ag.memberCount} Hosts", color = TextSecondary, fontSize = 11.sp)
                                            Text(text = "Owner: ${ag.ownerName}", color = ElectricBlue, fontSize = 10.sp)
                                        }
                                    }

                                    Button(
                                        onClick = {
                                            coroutineScope.launch {
                                                repository.requestToJoinAgency(ag.id)
                                                Toast.makeText(context, "Application submitted to ${ag.name}!", Toast.LENGTH_SHORT).show()
                                            }
                                        },
                                        colors = ButtonDefaults.buttonColors(containerColor = NeonPink),
                                        shape = RoundedCornerShape(10.dp),
                                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 2.dp),
                                        modifier = Modifier.height(32.dp)
                                    ) {
                                        Text("Apply", fontSize = 11.5.sp, fontWeight = FontWeight.Bold)
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        // Dialog: Create Agency
        if (showCreateAgencyDialog) {
            CreateAgencyDialog(
                onDismiss = { showCreateAgencyDialog = false },
                onCreate = { name, code, bdId ->
                    coroutineScope.launch {
                        repository.createAgency(
                            name = name,
                            agencyCode = code,
                            bdId = bdId,
                            logoUrl = "https://images.unsplash.com/photo-1579546929518-9e396f3cc809?w=400"
                        )
                        showCreateAgencyDialog = false
                        Toast.makeText(context, "Agency $name created successfully! 🎉", Toast.LENGTH_SHORT).show()
                    }
                }
            )
        }

        // Dialog: Invite User
        if (showInviteDialog && userAgency != null) {
            InviteUserToAgencyDialog(
                onDismiss = { showInviteDialog = false },
                onInvite = { targetUserId ->
                    coroutineScope.launch {
                        repository.inviteUserToAgency(userAgency.id, targetUserId)
                        showInviteDialog = false
                        Toast.makeText(context, "Invitation sent to user $targetUserId! 💌", Toast.LENGTH_SHORT).show()
                    }
                }
            )
        }

        // Dialog: Edit Agency
        if (showEditAgencyDialog && userAgency != null) {
            EditAgencyDialog(
                agency = userAgency,
                onDismiss = { showEditAgencyDialog = false },
                onSave = { newName, newDesc ->
                    coroutineScope.launch {
                        repository.updateAgencyProfile(userAgency.id, newName, newDesc, userAgency.logoUrl)
                        showEditAgencyDialog = false
                        Toast.makeText(context, "Agency details updated!", Toast.LENGTH_SHORT).show()
                    }
                }
            )
        }
    }
}

@Composable
fun CreateAgencyDialog(
    onDismiss: () -> Unit,
    onCreate: (String, String, String) -> Unit
) {
    var name by remember { mutableStateOf("") }
    var code by remember { mutableStateOf("") }
    var bdId by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = SurfaceDark,
        title = { Text("Create Voice Agency", color = TextPrimary, fontWeight = FontWeight.Bold) },
        text = {
            Column(modifier = Modifier.fillMaxWidth().verticalScroll(rememberScrollState()), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Agency Name") },
                    placeholder = { Text("e.g. Royal Voice Guild") },
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = NeonPink,
                        unfocusedBorderColor = SurfaceCardBorder,
                        focusedTextColor = TextPrimary,
                        unfocusedTextColor = TextPrimary
                    )
                )

                OutlinedTextField(
                    value = code,
                    onValueChange = { code = it },
                    label = { Text("Agency Code") },
                    placeholder = { Text("e.g. RVG888") },
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = NeonPink,
                        unfocusedBorderColor = SurfaceCardBorder,
                        focusedTextColor = TextPrimary,
                        unfocusedTextColor = TextPrimary
                    )
                )

                OutlinedTextField(
                    value = bdId,
                    onValueChange = { bdId = it },
                    label = { Text("Assigned BD ID (Optional)") },
                    placeholder = { Text("e.g. BD_OFFICIAL_1") },
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = NeonPink,
                        unfocusedBorderColor = SurfaceCardBorder,
                        focusedTextColor = TextPrimary,
                        unfocusedTextColor = TextPrimary
                    )
                )
            }
        },
        confirmButton = {
            NeonButton(
                text = "Register Agency",
                onClick = {
                    if (name.isNotBlank() && code.isNotBlank()) {
                        onCreate(name.trim(), code.trim().uppercase(), bdId.trim())
                    }
                }
            )
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel", color = TextSecondary) }
        }
    )
}

@Composable
fun InviteUserToAgencyDialog(
    onDismiss: () -> Unit,
    onInvite: (String) -> Unit
) {
    var userId by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = SurfaceDark,
        title = { Text("Invite Host to Agency", color = TextPrimary, fontWeight = FontWeight.Bold) },
        text = {
            Column(modifier = Modifier.fillMaxWidth()) {
                Text("Enter the User ID of the host you want to recruit:", color = TextSecondary, fontSize = 12.sp)
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = userId,
                    onValueChange = { userId = it },
                    label = { Text("Target User ID") },
                    placeholder = { Text("e.g. 10002") },
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = NeonPink,
                        unfocusedBorderColor = SurfaceCardBorder,
                        focusedTextColor = TextPrimary,
                        unfocusedTextColor = TextPrimary
                    ),
                    singleLine = true
                )
            }
        },
        confirmButton = {
            NeonButton(
                text = "Send Invite",
                onClick = {
                    if (userId.isNotBlank()) {
                        onInvite(userId.trim())
                    }
                }
            )
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel", color = TextSecondary) }
        }
    )
}

@Composable
fun EditAgencyDialog(
    agency: Agency,
    onDismiss: () -> Unit,
    onSave: (String, String) -> Unit
) {
    var name by remember { mutableStateOf(agency.name) }
    var description by remember { mutableStateOf(agency.description) }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = SurfaceDark,
        title = { Text("Edit Agency Profile", color = TextPrimary, fontWeight = FontWeight.Bold) },
        text = {
            Column(modifier = Modifier.fillMaxWidth().verticalScroll(rememberScrollState()), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Agency Name") },
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = NeonPink,
                        unfocusedBorderColor = SurfaceCardBorder,
                        focusedTextColor = TextPrimary,
                        unfocusedTextColor = TextPrimary
                    )
                )

                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("Agency Announcement") },
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = NeonPink,
                        unfocusedBorderColor = SurfaceCardBorder,
                        focusedTextColor = TextPrimary,
                        unfocusedTextColor = TextPrimary
                    )
                )
            }
        },
        confirmButton = {
            NeonButton(
                text = "Save Changes",
                onClick = {
                    if (name.isNotBlank()) {
                        onSave(name.trim(), description.trim())
                    }
                }
            )
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel", color = TextSecondary) }
        }
    )
}
