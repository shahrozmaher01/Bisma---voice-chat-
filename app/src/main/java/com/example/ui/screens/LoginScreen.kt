package com.example.ui.screens

import android.accounts.AccountManager
import android.app.Activity
import android.app.DatePickerDialog
import android.content.Context
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import androidx.credentials.CredentialManager
import androidx.credentials.GetCredentialRequest
import androidx.credentials.CustomCredential
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.example.R
import com.example.data.repository.BismaRepository
import com.example.ui.components.GlassCard
import com.example.ui.components.NeonButton
import com.example.ui.theme.*
import kotlinx.coroutines.launch
import java.util.Calendar

val PresetAvatars = listOf(
    "https://images.unsplash.com/photo-1534528741775-53994a69daeb?w=300", // Female 1
    "https://images.unsplash.com/photo-1494790108377-be9c29b29330?w=300", // Female 2
    "https://images.unsplash.com/photo-1517841905240-472988babdf9?w=300", // Female 3
    "https://images.unsplash.com/photo-1544005313-94ddf0286df2?w=300", // Female 4
    "https://images.unsplash.com/photo-1507003211169-0a1dd7228f2d?w=300", // Male 1
    "https://images.unsplash.com/photo-1500648767791-00dcc994a43e?w=300", // Male 2
    "https://images.unsplash.com/photo-1535713875002-d1d0cf377fde?w=300", // Male 3
    "https://images.unsplash.com/photo-1522075469751-3a6694fb2f61?w=300"  // Male 4
)

@Composable
fun LoginScreen(
    repository: BismaRepository,
    onLoginSuccess: () -> Unit
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val credentialManager = remember { CredentialManager.create(context) }

    var selectedTabIndex by remember { mutableStateOf(0) } // 0 = Login with ID, 1 = Create Account
    var showForgotPasswordDialog by remember { mutableStateOf(false) }
    var isLoading by remember { mutableStateOf(false) }

    // Native Android Google Account Chooser launcher
    val accountPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK && result.data != null) {
            val accountName = result.data?.getStringExtra(AccountManager.KEY_ACCOUNT_NAME)
            if (!accountName.isNullOrBlank()) {
                isLoading = true
                coroutineScope.launch {
                    val displayName = accountName.substringBefore("@")
                        .replace(".", " ")
                        .split(" ")
                        .joinToString(" ") { word ->
                            word.replaceFirstChar { if (it.isLowerCase()) it.titlecase() else it.toString() }
                        }
                    val loginResult = repository.loginWithGoogle(
                        email = accountName,
                        displayName = displayName,
                        avatarUrl = "https://images.unsplash.com/photo-1535713875002-d1d0cf377fde?w=200"
                    )
                    isLoading = false
                    loginResult.onSuccess { user ->
                        Toast.makeText(context, "Signed in as ${user.email} ✨", Toast.LENGTH_SHORT).show()
                        onLoginSuccess()
                    }.onFailure { err ->
                        Toast.makeText(context, err.message ?: "Google Sign-In failed", Toast.LENGTH_LONG).show()
                    }
                }
            } else {
                isLoading = false
            }
        } else {
            isLoading = false
        }
    }

    // Login Form State
    var loginId by remember { mutableStateOf("") }
    var loginPassword by remember { mutableStateOf("") }
    var loginPasswordVisible by remember { mutableStateOf(false) }

    // Create Account Form State
    var registerName by remember { mutableStateOf("") }
    var registerGeneratedId by remember { mutableStateOf("") }
    var registerSelectedAvatar by remember { mutableStateOf(PresetAvatars[0]) }
    var registerGender by remember { mutableStateOf("Female") } // "Male" or "Female"
    var registerDob by remember { mutableStateOf("2002-05-14") }
    var registerPassword by remember { mutableStateOf("") }
    var registerConfirmPassword by remember { mutableStateOf("") }
    var registerPasswordVisible by remember { mutableStateOf(false) }

    // Generate initial unique ID for registration
    LaunchedEffect(Unit) {
        if (registerGeneratedId.isEmpty()) {
            registerGeneratedId = repository.generateUniqueUserId()
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkBackgroundGradient)
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .navigationBarsPadding()
                .padding(horizontal = 20.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp),
            contentPadding = PaddingValues(top = 20.dp, bottom = 40.dp)
        ) {
            // App Header & Logo
            item {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.padding(bottom = 6.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(72.dp)
                            .shadow(10.dp, RoundedCornerShape(20.dp), spotColor = NeonPink)
                            .clip(RoundedCornerShape(20.dp))
                            .border(
                                BorderStroke(1.5.dp, Brush.sweepGradient(listOf(NeonPink, ElectricBlue, NeonPink))),
                                RoundedCornerShape(20.dp)
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.ic_aura_logo),
                            contentDescription = "AURA Live Logo",
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Text(
                        text = "AURA LIVE",
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Black,
                        color = Color.White,
                        letterSpacing = 1.sp
                    )

                    Text(
                        text = "Live Voice Chat, Rooms & Real Connections",
                        fontSize = 12.sp,
                        color = TextSecondary,
                        modifier = Modifier.padding(top = 2.dp)
                    )
                }
            }

            // Google Login Button (Prominent)
            item {
                Button(
                    onClick = {
                        isLoading = true
                        try {
                            val intent = AccountManager.newChooseAccountIntent(
                                null,
                                null,
                                arrayOf("com.google"),
                                null,
                                null,
                                null,
                                null
                            )
                            accountPickerLauncher.launch(intent)
                        } catch (e: Exception) {
                            // Fallback to CredentialManager if AccountManager is not directly available
                            coroutineScope.launch {
                                try {
                                    val googleIdOption = GetGoogleIdOption.Builder()
                                        .setFilterByAuthorizedAccounts(false)
                                        .setServerClientId("987654321000-dummyclientid.apps.googleusercontent.com")
                                        .setAutoSelectEnabled(false)
                                        .build()
                                    val credRequest = GetCredentialRequest.Builder()
                                        .addCredentialOption(googleIdOption)
                                        .build()
                                    val result = credentialManager.getCredential(context = context, request = credRequest)
                                    val credential = result.credential
                                    if (credential is CustomCredential && credential.type == GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL) {
                                        val googleIdTokenCredential = GoogleIdTokenCredential.createFrom(credential.data)
                                        val email = googleIdTokenCredential.id
                                        val name = googleIdTokenCredential.displayName ?: email.substringBefore("@")
                                        val avatar = googleIdTokenCredential.profilePictureUri?.toString()
                                            ?: "https://images.unsplash.com/photo-1535713875002-d1d0cf377fde?w=200"
                                        val loginResult = repository.loginWithGoogle(email = email, displayName = name, avatarUrl = avatar)
                                        loginResult.onSuccess { user ->
                                            Toast.makeText(context, "Welcome, ${user.username}! ✨", Toast.LENGTH_SHORT).show()
                                            onLoginSuccess()
                                        }.onFailure { err ->
                                            Toast.makeText(context, err.message ?: "Sign in failed", Toast.LENGTH_LONG).show()
                                        }
                                    } else {
                                        Toast.makeText(context, "Google Sign-In canceled", Toast.LENGTH_SHORT).show()
                                    }
                                } catch (e2: Exception) {
                                    Toast.makeText(context, "No Google accounts found on device. Please sign in with ID or create an account.", Toast.LENGTH_LONG).show()
                                } finally {
                                    isLoading = false
                                }
                            }
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = SurfaceCard),
                    border = BorderStroke(1.dp, SurfaceCardBorder),
                    shape = RoundedCornerShape(14.dp),
                    enabled = !isLoading
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Text("🌐", fontSize = 18.sp)
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = if (isLoading) "Connecting to Google..." else "Continue with Google",
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp
                        )
                    }
                }
            }

            // Divider Or
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    HorizontalDivider(modifier = Modifier.weight(1f), color = SurfaceCardBorder)
                    Text(
                        text = "  OR  ",
                        color = TextMuted,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                    HorizontalDivider(modifier = Modifier.weight(1f), color = SurfaceCardBorder)
                }
            }

            // Tab Switcher: Login with ID | Create Account
            item {
                TabRow(
                    selectedTabIndex = selectedTabIndex,
                    containerColor = SurfaceCard,
                    contentColor = NeonPink,
                    indicator = { tabPositions ->
                        TabRowDefaults.SecondaryIndicator(
                            Modifier.tabIndicatorOffset(tabPositions[selectedTabIndex]),
                            color = NeonPink,
                            height = 3.dp
                        )
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                ) {
                    Tab(
                        selected = selectedTabIndex == 0,
                        onClick = { selectedTabIndex = 0 },
                        text = {
                            Text(
                                text = "Login with ID",
                                fontWeight = if (selectedTabIndex == 0) FontWeight.Bold else FontWeight.Normal,
                                color = if (selectedTabIndex == 0) Color.White else TextSecondary,
                                fontSize = 14.sp
                            )
                        }
                    )
                    Tab(
                        selected = selectedTabIndex == 1,
                        onClick = {
                            selectedTabIndex = 1
                            if (registerGeneratedId.isEmpty()) {
                                coroutineScope.launch {
                                    registerGeneratedId = repository.generateUniqueUserId()
                                }
                            }
                        },
                        text = {
                            Text(
                                text = "Create New Account",
                                fontWeight = if (selectedTabIndex == 1) FontWeight.Bold else FontWeight.Normal,
                                color = if (selectedTabIndex == 1) Color.White else TextSecondary,
                                fontSize = 14.sp
                            )
                        }
                    )
                }
            }

            // TAB 0: LOGIN WITH ID NUMBER
            if (selectedTabIndex == 0) {
                item {
                    GlassCard(modifier = Modifier.fillMaxWidth()) {
                        Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
                            // ID Number input
                            OutlinedTextField(
                                value = loginId,
                                onValueChange = { loginId = it },
                                label = { Text("ID Number", color = TextSecondary) },
                                leadingIcon = {
                                    Icon(Icons.Default.Badge, contentDescription = "ID", tint = NeonPink)
                                },
                                placeholder = { Text("e.g. 123456", color = TextMuted) },
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                modifier = Modifier.fillMaxWidth(),
                                singleLine = true,
                                shape = RoundedCornerShape(12.dp),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = NeonPink,
                                    unfocusedBorderColor = SurfaceCardBorder,
                                    focusedTextColor = Color.White,
                                    unfocusedTextColor = Color.White,
                                    cursorColor = NeonPink
                                )
                            )

                            // Password input
                            OutlinedTextField(
                                value = loginPassword,
                                onValueChange = { loginPassword = it },
                                label = { Text("Password", color = TextSecondary) },
                                leadingIcon = {
                                    Icon(Icons.Default.Lock, contentDescription = "Password", tint = ElectricBlue)
                                },
                                trailingIcon = {
                                    IconButton(onClick = { loginPasswordVisible = !loginPasswordVisible }) {
                                        Icon(
                                            if (loginPasswordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                                            contentDescription = "Toggle Password",
                                            tint = TextMuted
                                        )
                                    }
                                },
                                visualTransformation = if (loginPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                                modifier = Modifier.fillMaxWidth(),
                                singleLine = true,
                                shape = RoundedCornerShape(12.dp),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = NeonPink,
                                    unfocusedBorderColor = SurfaceCardBorder,
                                    focusedTextColor = Color.White,
                                    unfocusedTextColor = Color.White,
                                    cursorColor = NeonPink
                                )
                            )

                            // Forgot Password Row
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.End
                            ) {
                                TextButton(
                                    onClick = { showForgotPasswordDialog = true },
                                    contentPadding = PaddingValues(0.dp)
                                ) {
                                    Text(
                                        text = "Forgot Password?",
                                        color = BrightCyan,
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Medium
                                    )
                                }
                            }

                            // Login Button
                            NeonButton(
                                text = if (isLoading) "Logging In..." else "Login to AURA Live",
                                onClick = {
                                    if (loginId.isBlank()) {
                                        Toast.makeText(context, "Please enter your ID Number", Toast.LENGTH_SHORT).show()
                                        return@NeonButton
                                    }
                                    if (loginPassword.isBlank()) {
                                        Toast.makeText(context, "Please enter your password", Toast.LENGTH_SHORT).show()
                                        return@NeonButton
                                    }
                                    isLoading = true
                                    coroutineScope.launch {
                                        val result = repository.loginWithId(loginId, loginPassword)
                                        isLoading = false
                                        result.onSuccess { user ->
                                            Toast.makeText(context, "Welcome back, ${user.username}! ✨", Toast.LENGTH_SHORT).show()
                                            onLoginSuccess()
                                        }.onFailure { err ->
                                            Toast.makeText(context, err.message ?: "Login failed", Toast.LENGTH_LONG).show()
                                        }
                                    }
                                },
                                modifier = Modifier.fillMaxWidth(),
                                enabled = !isLoading
                            )
                        }
                    }
                }
            }

            // TAB 1: CREATE NEW ACCOUNT
            if (selectedTabIndex == 1) {
                item {
                    GlassCard(modifier = Modifier.fillMaxWidth()) {
                        Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
                            // Section: Profile Picture / DP Picker
                            Column(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text(
                                    text = "Select Profile Picture (DP)",
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                                Spacer(modifier = Modifier.height(8.dp))

                                // Main Preview Avatar
                                Box(
                                    modifier = Modifier
                                        .size(76.dp)
                                        .clip(CircleShape)
                                        .border(2.5.dp, Brush.sweepGradient(listOf(NeonPink, ElectricBlue, NeonPink)), CircleShape),
                                    contentAlignment = Alignment.Center
                                ) {
                                    AsyncImage(
                                        model = registerSelectedAvatar,
                                        contentDescription = "Selected DP",
                                        modifier = Modifier.fillMaxSize(),
                                        contentScale = ContentScale.Crop
                                    )
                                }

                                Spacer(modifier = Modifier.height(10.dp))

                                // Avatar Preset Row
                                LazyRow(
                                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    items(PresetAvatars) { avatarUrl ->
                                        val isSelected = registerSelectedAvatar == avatarUrl
                                        Box(
                                            modifier = Modifier
                                                .size(44.dp)
                                                .clip(CircleShape)
                                                .border(
                                                    if (isSelected) 2.5.dp else 1.dp,
                                                    if (isSelected) NeonPink else SurfaceCardBorder,
                                                    CircleShape
                                                )
                                                .clickable { registerSelectedAvatar = avatarUrl }
                                        ) {
                                            AsyncImage(
                                                model = avatarUrl,
                                                contentDescription = "Preset",
                                                modifier = Modifier.fillMaxSize(),
                                                contentScale = ContentScale.Crop
                                            )
                                        }
                                    }
                                }
                            }

                            // Full Name Input
                            OutlinedTextField(
                                value = registerName,
                                onValueChange = { registerName = it },
                                label = { Text("Your Name", color = TextSecondary) },
                                leadingIcon = {
                                    Icon(Icons.Default.Person, contentDescription = "Name", tint = NeonPink)
                                },
                                placeholder = { Text("e.g. Bisma Malik 🌸", color = TextMuted) },
                                modifier = Modifier.fillMaxWidth(),
                                singleLine = true,
                                shape = RoundedCornerShape(12.dp),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = NeonPink,
                                    unfocusedBorderColor = SurfaceCardBorder,
                                    focusedTextColor = Color.White,
                                    unfocusedTextColor = Color.White,
                                    cursorColor = NeonPink
                                )
                            )

                            // Automatically Generated Unique ID Number Display
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(Color(0x331E1235))
                                    .border(1.dp, SurfaceCardBorder, RoundedCornerShape(12.dp))
                                    .padding(horizontal = 14.dp, vertical = 10.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Column {
                                    Text(
                                        text = "Your Unique ID Number (Auto-Generated)",
                                        fontSize = 11.sp,
                                        color = TextSecondary
                                    )
                                    Text(
                                        text = "ID: $registerGeneratedId",
                                        fontSize = 17.sp,
                                        fontWeight = FontWeight.Black,
                                        color = GoldYellow
                                    )
                                }

                                IconButton(
                                    onClick = {
                                        coroutineScope.launch {
                                            registerGeneratedId = repository.generateUniqueUserId()
                                        }
                                    }
                                ) {
                                    Icon(Icons.Default.Refresh, contentDescription = "New ID", tint = BrightCyan)
                                }
                            }

                            // Gender Selector (Male / Female)
                            Column {
                                Text(
                                    text = "Gender",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = TextSecondary
                                )
                                Spacer(modifier = Modifier.height(6.dp))
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                                ) {
                                    GenderSelectionChip(
                                        title = "Female 👩",
                                        isSelected = registerGender == "Female",
                                        modifier = Modifier.weight(1f),
                                        onClick = { registerGender = "Female" }
                                    )
                                    GenderSelectionChip(
                                        title = "Male 👨",
                                        isSelected = registerGender == "Male",
                                        modifier = Modifier.weight(1f),
                                        onClick = { registerGender = "Male" }
                                    )
                                }
                            }

                            // Date of Birth (DatePicker Dialog)
                            Column {
                                Text(
                                    text = "Date of Birth",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = TextSecondary
                                )
                                Spacer(modifier = Modifier.height(6.dp))
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clip(RoundedCornerShape(12.dp))
                                        .background(SurfaceCard)
                                        .border(1.dp, SurfaceCardBorder, RoundedCornerShape(12.dp))
                                        .clickable {
                                            showDatePicker(context, registerDob) { selectedDate ->
                                                registerDob = selectedDate
                                            }
                                        }
                                        .padding(horizontal = 14.dp, vertical = 12.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(Icons.Default.Cake, contentDescription = "DOB", tint = NeonPink, modifier = Modifier.size(20.dp))
                                        Spacer(modifier = Modifier.width(10.dp))
                                        Text(
                                            text = registerDob,
                                            color = Color.White,
                                            fontSize = 14.sp,
                                            fontWeight = FontWeight.Medium
                                        )
                                    }
                                    Text(
                                        text = "Change 📅",
                                        color = BrightCyan,
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }

                            // Password
                            OutlinedTextField(
                                value = registerPassword,
                                onValueChange = { registerPassword = it },
                                label = { Text("Create Password (min 4 chars)", color = TextSecondary) },
                                leadingIcon = {
                                    Icon(Icons.Default.Lock, contentDescription = "Password", tint = ElectricBlue)
                                },
                                visualTransformation = if (registerPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                                modifier = Modifier.fillMaxWidth(),
                                singleLine = true,
                                shape = RoundedCornerShape(12.dp),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = NeonPink,
                                    unfocusedBorderColor = SurfaceCardBorder,
                                    focusedTextColor = Color.White,
                                    unfocusedTextColor = Color.White,
                                    cursorColor = NeonPink
                                )
                            )

                            // Confirm Password
                            OutlinedTextField(
                                value = registerConfirmPassword,
                                onValueChange = { registerConfirmPassword = it },
                                label = { Text("Confirm Password", color = TextSecondary) },
                                leadingIcon = {
                                    Icon(Icons.Default.LockReset, contentDescription = "Confirm", tint = ElectricBlue)
                                },
                                trailingIcon = {
                                    IconButton(onClick = { registerPasswordVisible = !registerPasswordVisible }) {
                                        Icon(
                                            if (registerPasswordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                                            contentDescription = "Toggle Password",
                                            tint = TextMuted
                                        )
                                    }
                                },
                                visualTransformation = if (registerPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                                modifier = Modifier.fillMaxWidth(),
                                singleLine = true,
                                shape = RoundedCornerShape(12.dp),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = NeonPink,
                                    unfocusedBorderColor = SurfaceCardBorder,
                                    focusedTextColor = Color.White,
                                    unfocusedTextColor = Color.White,
                                    cursorColor = NeonPink
                                )
                            )

                            // Create Account Button
                            NeonButton(
                                text = if (isLoading) "Creating Account..." else "Create Account & Get 2,000 Coins 🪙",
                                onClick = {
                                    if (registerName.isBlank()) {
                                        Toast.makeText(context, "Please enter your name", Toast.LENGTH_SHORT).show()
                                        return@NeonButton
                                    }
                                    if (registerPassword.length < 4) {
                                        Toast.makeText(context, "Password must be at least 4 characters", Toast.LENGTH_SHORT).show()
                                        return@NeonButton
                                    }
                                    if (registerPassword != registerConfirmPassword) {
                                        Toast.makeText(context, "Passwords do not match", Toast.LENGTH_SHORT).show()
                                        return@NeonButton
                                    }
                                    isLoading = true
                                    coroutineScope.launch {
                                        val result = repository.createAccount(
                                            username = registerName,
                                            avatarUrl = registerSelectedAvatar,
                                            gender = registerGender,
                                            dateOfBirth = registerDob,
                                            password = registerPassword,
                                            customId = registerGeneratedId
                                        )
                                        isLoading = false
                                        result.onSuccess { user ->
                                            Toast.makeText(context, "Account created successfully! Your ID is ${user.id} 🎉", Toast.LENGTH_LONG).show()
                                            onLoginSuccess()
                                        }.onFailure { err ->
                                            Toast.makeText(context, err.message ?: "Failed to create account", Toast.LENGTH_LONG).show()
                                        }
                                    }
                                },
                                modifier = Modifier.fillMaxWidth(),
                                enabled = !isLoading
                            )
                        }
                    }
                }
            }
        }

        // FORGOT PASSWORD DIALOG
        if (showForgotPasswordDialog) {
            ForgotPasswordDialog(
                repository = repository,
                onDismiss = { showForgotPasswordDialog = false },
                onSuccess = {
                    showForgotPasswordDialog = false
                    Toast.makeText(context, "Password reset successfully! You can now log in.", Toast.LENGTH_LONG).show()
                }
            )
        }
    }
}

@Composable
fun GenderSelectionChip(
    title: String,
    isSelected: Boolean,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Box(
        modifier = modifier
            .height(44.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(if (isSelected) NeonPink.copy(alpha = 0.2f) else SurfaceCard)
            .border(
                1.5.dp,
                if (isSelected) NeonPink else SurfaceCardBorder,
                RoundedCornerShape(12.dp)
            )
            .clickable { onClick() },
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = title,
            color = if (isSelected) Color.White else TextSecondary,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
            fontSize = 14.sp
        )
    }
}

@Composable
fun ForgotPasswordDialog(
    repository: BismaRepository,
    onDismiss: () -> Unit,
    onSuccess: () -> Unit
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    var accountQuery by remember { mutableStateOf("") }
    var newPassword by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var isResetting by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = SurfaceDark,
        title = {
            Text("Reset Password", color = Color.White, fontWeight = FontWeight.Bold)
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Text(
                    text = "Enter your ID Number or Email to set a new password:",
                    color = TextSecondary,
                    fontSize = 12.sp
                )

                OutlinedTextField(
                    value = accountQuery,
                    onValueChange = { accountQuery = it },
                    label = { Text("ID Number or Email") },
                    placeholder = { Text("e.g. 123456 or user@gmail.com") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = NeonPink,
                        unfocusedBorderColor = SurfaceCardBorder,
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White
                    )
                )

                OutlinedTextField(
                    value = newPassword,
                    onValueChange = { newPassword = it },
                    label = { Text("New Password") },
                    visualTransformation = PasswordVisualTransformation(),
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = NeonPink,
                        unfocusedBorderColor = SurfaceCardBorder,
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White
                    )
                )

                OutlinedTextField(
                    value = confirmPassword,
                    onValueChange = { confirmPassword = it },
                    label = { Text("Confirm New Password") },
                    visualTransformation = PasswordVisualTransformation(),
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = NeonPink,
                        unfocusedBorderColor = SurfaceCardBorder,
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White
                    )
                )
            }
        },
        confirmButton = {
            NeonButton(
                text = if (isResetting) "Resetting..." else "Save New Password",
                onClick = {
                    if (accountQuery.isBlank()) {
                        Toast.makeText(context, "Please enter your ID Number or Email", Toast.LENGTH_SHORT).show()
                        return@NeonButton
                    }
                    if (newPassword.length < 4) {
                        Toast.makeText(context, "Password must be at least 4 characters", Toast.LENGTH_SHORT).show()
                        return@NeonButton
                    }
                    if (newPassword != confirmPassword) {
                        Toast.makeText(context, "Passwords do not match", Toast.LENGTH_SHORT).show()
                        return@NeonButton
                    }
                    isResetting = true
                    coroutineScope.launch {
                        val result = repository.resetPassword(accountQuery, newPassword)
                        isResetting = false
                        result.onSuccess {
                            onSuccess()
                        }.onFailure { err ->
                            Toast.makeText(context, err.message ?: "Failed to reset password", Toast.LENGTH_LONG).show()
                        }
                    }
                },
                enabled = !isResetting
            )
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel", color = TextSecondary)
            }
        }
    )
}

fun showDatePicker(context: Context, initialDate: String, onDateSelected: (String) -> Unit) {
    val parts = initialDate.split("-")
    val defaultYear = parts.getOrNull(0)?.toIntOrNull() ?: 2002
    val defaultMonth = (parts.getOrNull(1)?.toIntOrNull() ?: 5) - 1
    val defaultDay = parts.getOrNull(2)?.toIntOrNull() ?: 14

    val datePickerDialog = DatePickerDialog(
        context,
        { _, year, monthOfYear, dayOfMonth ->
            val formatted = String.format("%04d-%02d-%02d", year, monthOfYear + 1, dayOfMonth)
            onDateSelected(formatted)
        },
        defaultYear,
        defaultMonth,
        defaultDay
    )
    datePickerDialog.show()
}
