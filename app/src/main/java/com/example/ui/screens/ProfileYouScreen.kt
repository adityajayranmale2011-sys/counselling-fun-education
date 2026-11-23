package com.example.ui.screens

import android.Manifest
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.AutoStories
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.DeleteSweep
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.FilterChip
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.R
import com.example.data.local.ChatHistoryEntity
import com.example.data.local.UserProfileEntity
import com.example.ui.theme.AcademicGold
import com.example.ui.theme.AppThemeSkin
import com.example.ui.theme.CinzelFontFamily
import com.example.ui.theme.CoralAlert
import com.example.ui.theme.ElectricCyan
import com.example.ui.theme.EmeraldRecharge

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun ProfileYouScreen(
    userProfile: UserProfileEntity?,
    chatHistory: List<ChatHistoryEntity>,
    onUpdateProfile: (UserProfileEntity) -> Unit,
    onClearChatHistory: () -> Unit,
    onSwitchAccountOrSignOut: () -> Unit
) {
    val profile = userProfile ?: UserProfileEntity()

    var dpUri by remember(profile.profilePictureUri) { mutableStateOf(profile.profilePictureUri) }
    var name by remember(profile.name) { mutableStateOf(profile.name) }
    var classGrade by remember(profile.classGrade) { mutableStateOf(profile.classGrade) }
    var mobileNumber by remember(profile.mobileNumber) { mutableStateOf(profile.mobileNumber) }
    var schoolCollege by remember(profile.schoolCollege) { mutableStateOf(profile.schoolCollege) }
    var academicGoal by remember(profile.academicGoal) { mutableStateOf(profile.academicGoal) }
    var age by remember(profile.age) { mutableStateOf(profile.age) }

    var showGradeDropdown by remember { mutableStateOf(false) }
    var showUpperSettingsGearMenu by remember { mutableStateOf(false) }
    var showChatHistoryDrawer by remember { mutableStateOf(false) }

    val gradesList = (1..12).map {
        when (it) {
            1 -> "1st Grade"
            2 -> "2nd Grade"
            3 -> "3rd Grade"
            else -> "${it}th Grade"
        }
    }

    // Profile Picture (DP) Circular Gallery Upload Launcher
    val dpPhotoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri ->
        if (uri != null) {
            dpUri = uri.toString()
            onUpdateProfile(profile.copy(profilePictureUri = uri.toString()))
        }
    }

    // Custom Local Audio File Picker for Alarm Audio Customization
    val customAudioPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocument()
    ) { uri ->
        if (uri != null) {
            onUpdateProfile(
                profile.copy(
                    alarmAudioSource = "CUSTOM_LOCAL_AUDIO",
                    customAudioUri = uri.toString(),
                    customAudioName = "Custom_Audio_${uri.lastPathSegment ?: "File"}"
                )
            )
        }
    }

    // Runtime Permission Request Launcher for System Access Permissions
    val multiplePermissionsLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { _ ->
        // Permissions updated in system
    }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 12.dp, bottom = 104.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Top Header with Upper Settings Gear Menu Button
        item {
            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant
                ),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(18.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "\"YOU\" PROFILE & SYSTEM ARCHITECTURE",
                            fontFamily = CinzelFontFamily,
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = profile.authAccountLabel,
                            style = MaterialTheme.typography.bodyMedium,
                            color = EmeraldRecharge
                        )
                    }

                    // Upper Settings Gear Menu Trigger
                    Surface(
                        shape = CircleShape,
                        color = if (showUpperSettingsGearMenu)
                            MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surface,
                        modifier = Modifier
                            .size(48.dp)
                            .clip(CircleShape)
                            .clickable { showUpperSettingsGearMenu = !showUpperSettingsGearMenu }
                            .testTag("upper_settings_gear_button")
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector = Icons.Default.Settings,
                                contentDescription = "Upper Settings Gear Menu",
                                tint = if (showUpperSettingsGearMenu)
                                    MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                }
            }
        }

        // Upper Settings Gear Menu (Expandable System Architecture Panel)
        item {
            AnimatedVisibility(visible = showUpperSettingsGearMenu) {
                Card(
                    shape = RoundedCornerShape(22.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surface
                    ),
                    border = androidx.compose.foundation.BorderStroke(
                        1.5.dp,
                        MaterialTheme.colorScheme.primary
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("upper_settings_gear_panel")
                ) {
                    Column(
                        modifier = Modifier.padding(18.dp),
                        verticalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Security,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "UPPER SETTINGS GEAR MENU • SYSTEM CONFIG",
                                style = MaterialTheme.typography.titleMedium,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }

                        // 1. Themes: Dark, Light, and Custom UI Skin Modes
                        HorizontalDivider()
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Palette,
                                contentDescription = null,
                                tint = AcademicGold,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Themes: Dark, Light & Custom UI Skin Modes",
                                style = MaterialTheme.typography.titleSmall
                            )
                        }
                        FlowRow(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            AppThemeSkin.entries.forEach { skin ->
                                FilterChip(
                                    selected = profile.themeSkin == skin.name,
                                    onClick = {
                                        onUpdateProfile(profile.copy(themeSkin = skin.name))
                                    },
                                    label = { Text(skin.label) },
                                    modifier = Modifier.testTag("theme_skin_chip_${skin.name}")
                                )
                            }
                        }

                        // 2. Audio Customization for Alarms
                        HorizontalDivider()
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.MusicNote,
                                contentDescription = null,
                                tint = ElectricCyan,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Alarm Audio Customization (Stage 2 Hardware Alarm)",
                                style = MaterialTheme.typography.titleSmall
                            )
                        }

                        FlowRow(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            listOf(
                                "SYSTEM_ALARM" to "Native System Alarm Tone",
                                "APP_SOUND_PACK" to "AR Creator Sound Pack",
                                "CUSTOM_LOCAL_AUDIO" to "Custom Local Audio File"
                            ).forEach { (code, label) ->
                                FilterChip(
                                    selected = profile.alarmAudioSource == code,
                                    onClick = {
                                        if (code == "CUSTOM_LOCAL_AUDIO") {
                                            customAudioPickerLauncher.launch(arrayOf("audio/*"))
                                        } else {
                                            onUpdateProfile(profile.copy(alarmAudioSource = code))
                                        }
                                    },
                                    label = { Text(label) }
                                )
                            }
                        }
                        if (profile.alarmAudioSource == "CUSTOM_LOCAL_AUDIO") {
                            Text(
                                text = "Selected Local Audio: ${profile.customAudioName}",
                                style = MaterialTheme.typography.bodyMedium,
                                color = EmeraldRecharge
                            )
                        }

                        // 3. System Access Permissions Toggles
                        HorizontalDivider()
                        Text(
                            text = "System Access Permissions & Hardware Toggles:",
                            style = MaterialTheme.typography.titleSmall,
                            color = MaterialTheme.colorScheme.primary
                        )

                        PermissionToggleRow(
                            label = "Device Gallery (Photo Picker Access)",
                            checked = profile.permGallery,
                            onCheckedChange = { onUpdateProfile(profile.copy(permGallery = it)) }
                        )
                        PermissionToggleRow(
                            label = "Camera (Native Photo Parsing & DP)",
                            checked = profile.permCamera,
                            onCheckedChange = {
                                onUpdateProfile(profile.copy(permCamera = it))
                                if (it) {
                                    multiplePermissionsLauncher.launch(arrayOf(Manifest.permission.CAMERA))
                                }
                            }
                        )
                        PermissionToggleRow(
                            label = "File Manager (Internal Storage PDF Export)",
                            checked = profile.permFileManager,
                            onCheckedChange = { onUpdateProfile(profile.copy(permFileManager = it)) }
                        )
                        PermissionToggleRow(
                            label = "Audio (Microphone & Sound Packs)",
                            checked = profile.permAudio,
                            onCheckedChange = {
                                onUpdateProfile(profile.copy(permAudio = it))
                                if (it) {
                                    multiplePermissionsLauncher.launch(arrayOf(Manifest.permission.RECORD_AUDIO))
                                }
                            }
                        )
                        PermissionToggleRow(
                            label = "Hardware Speakers (Persistent Alarm Output)",
                            checked = profile.permHardwareSpeakers,
                            onCheckedChange = { onUpdateProfile(profile.copy(permHardwareSpeakers = it)) }
                        )
                        PermissionToggleRow(
                            label = "System Alarms (T+5s Hardware Trigger)",
                            checked = profile.permSystemAlarms,
                            onCheckedChange = { onUpdateProfile(profile.copy(permSystemAlarms = it)) }
                        )
                        PermissionToggleRow(
                            label = "Push Notifications (T-0s Instant Alert)",
                            checked = profile.permPushNotifications,
                            onCheckedChange = {
                                onUpdateProfile(profile.copy(permPushNotifications = it))
                                if (it && Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                                    multiplePermissionsLauncher.launch(
                                        arrayOf(Manifest.permission.POST_NOTIFICATIONS)
                                    )
                                }
                            }
                        )

                        // 4. ChatStudy History Database Viewer inside Gear Menu
                        HorizontalDivider()
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.History,
                                    contentDescription = null,
                                    tint = AcademicGold
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "ChatStudy History (${chatHistory.size} Logs)",
                                    style = MaterialTheme.typography.titleSmall
                                )
                            }
                            Row {
                                OutlinedButton(
                                    onClick = { showChatHistoryDrawer = !showChatHistoryDrawer }
                                ) {
                                    Text(if (showChatHistoryDrawer) "Hide Logs" else "View Logs")
                                }
                                if (chatHistory.isNotEmpty()) {
                                    IconButton(onClick = onClearChatHistory) {
                                        Icon(
                                            imageVector = Icons.Default.DeleteSweep,
                                            contentDescription = "Clear History",
                                            tint = CoralAlert
                                        )
                                    }
                                }
                            }
                        }

                        AnimatedVisibility(visible = showChatHistoryDrawer) {
                            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                if (chatHistory.isEmpty()) {
                                    Text(
                                        text = "No ChatStudy AI interactions recorded yet.",
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                } else {
                                    chatHistory.take(5).forEach { item ->
                                        Surface(
                                            shape = RoundedCornerShape(10.dp),
                                            color = MaterialTheme.colorScheme.surfaceVariant,
                                            modifier = Modifier.fillMaxWidth()
                                        ) {
                                            Column(modifier = Modifier.padding(10.dp)) {
                                                Text(
                                                    text = item.promptText,
                                                    style = MaterialTheme.typography.labelLarge
                                                )
                                                Text(
                                                    text = "Files: ${item.attachmentSummary}",
                                                    style = MaterialTheme.typography.labelSmall,
                                                    color = ElectricCyan
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

        // User Profile Card (Form Fields)
        item {
            Card(
                shape = RoundedCornerShape(22.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface
                ),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Profile Picture (DP): Circular image container with gallery upload capability
                    Box(
                        modifier = Modifier
                            .size(108.dp)
                            .clip(CircleShape)
                            .border(2.5.dp, AcademicGold, CircleShape)
                            .background(MaterialTheme.colorScheme.surfaceVariant)
                            .clickable {
                                dpPhotoPickerLauncher.launch(
                                    PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                                )
                            }
                            .testTag("profile_picture_dp_container"),
                        contentAlignment = Alignment.Center
                    ) {
                        if (dpUri.isNotBlank()) {
                            AsyncImage(
                                model = dpUri,
                                contentDescription = "User Profile Picture DP",
                                modifier = Modifier.fillMaxSize(),
                                contentScale = ContentScale.Crop
                            )
                        } else {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Icon(
                                    imageVector = Icons.Default.Person,
                                    contentDescription = "Default Profile Avatar",
                                    tint = AcademicGold,
                                    modifier = Modifier.size(42.dp)
                                )
                                Text(
                                    text = "Upload DP",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    OutlinedButton(
                        onClick = {
                            dpPhotoPickerLauncher.launch(
                                PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                            )
                        },
                        shape = RoundedCornerShape(50)
                    ) {
                        Icon(
                            imageVector = Icons.Default.CameraAlt,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Change Circular Profile Picture (DP)")
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Form Field 1: Name
                    OutlinedTextField(
                        value = name,
                        onValueChange = { name = it },
                        label = { Text("Name") },
                        singleLine = true,
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("profile_name_input")
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    // Form Field 2 & 7: Class / Grade [Dropdown: 1st to 12th Grade] + Age [Numeric Field]
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Box(modifier = Modifier.weight(1.3f)) {
                            OutlinedTextField(
                                value = classGrade,
                                onValueChange = {},
                                readOnly = true,
                                label = { Text("Class / Grade (1st–12th)") },
                                trailingIcon = {
                                    IconButton(onClick = { showGradeDropdown = true }) {
                                        Icon(Icons.Default.ArrowDropDown, contentDescription = "Select Grade")
                                    }
                                },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { showGradeDropdown = true }
                                    .testTag("profile_grade_dropdown")
                            )
                            DropdownMenu(
                                expanded = showGradeDropdown,
                                onDismissRequest = { showGradeDropdown = false }
                            ) {
                                gradesList.forEach { g ->
                                    DropdownMenuItem(
                                        text = { Text(g) },
                                        onClick = {
                                            classGrade = g
                                            showGradeDropdown = false
                                        }
                                    )
                                }
                            }
                        }

                        OutlinedTextField(
                            value = age,
                            onValueChange = { age = it.filter { ch -> ch.isDigit() } },
                            label = { Text("Age") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            singleLine = true,
                            modifier = Modifier
                                .weight(0.7f)
                                .testTag("profile_age_input")
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Form Field 3: Mobile Number [Phone Field]
                    OutlinedTextField(
                        value = mobileNumber,
                        onValueChange = { mobileNumber = it },
                        label = { Text("Mobile Number") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                        singleLine = true,
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("profile_mobile_input")
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    // Form Field 4: School / College [Text Field]
                    OutlinedTextField(
                        value = schoolCollege,
                        onValueChange = { schoolCollege = it },
                        label = { Text("School / College") },
                        singleLine = true,
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("profile_school_input")
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    // Form Field 5: Academic Goal [Text Field]
                    OutlinedTextField(
                        value = academicGoal,
                        onValueChange = { academicGoal = it },
                        label = { Text("Academic Goal") },
                        minLines = 2,
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("profile_goal_input")
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Button(
                        onClick = {
                            onUpdateProfile(
                                profile.copy(
                                    profilePictureUri = dpUri,
                                    name = name.trim(),
                                    classGrade = classGrade,
                                    mobileNumber = mobileNumber.trim(),
                                    schoolCollege = schoolCollege.trim(),
                                    academicGoal = academicGoal.trim(),
                                    age = age.trim()
                                )
                            )
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp)
                            .testTag("save_profile_button"),
                        shape = RoundedCornerShape(14.dp)
                    ) {
                        Icon(Icons.Default.Save, contentDescription = null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Save User Profile Details", fontWeight = FontWeight.Bold)
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    OutlinedButton(
                        onClick = onSwitchAccountOrSignOut,
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(14.dp)
                    ) {
                        Icon(Icons.AutoMirrored.Filled.Logout, contentDescription = null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Switch Entry Mode (Guest / Google OAuth / Mobile OTP)")
                    }
                }
            }
        }

        // Bottom of this viewport explicitly features AR CREATOR in a stylish font
        item {
            ArCreatorPageFooter(
                pageSubtitle = "Counseling : fun education • Profile & System Architecture"
            )
        }
    }
}

@Composable
fun ArCreatorPageFooter(
    pageSubtitle: String = "Counseling : fun education • Architecture & AI Engine"
) {
    Surface(
        shape = RoundedCornerShape(22.dp),
        color = MaterialTheme.colorScheme.surfaceVariant,
        modifier = Modifier
            .fillMaxWidth()
            .border(
                width = 1.5.dp,
                brush = Brush.horizontalGradient(listOf(AcademicGold, ElectricCyan)),
                shape = RoundedCornerShape(22.dp)
            )
            .testTag("ar_creator_footer_branding")
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 20.dp, horizontal = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Image(
                painter = painterResource(id = R.drawable.img_app_icon_1790778660683),
                contentDescription = "AR CREATOR Open Book Emblem",
                modifier = Modifier
                    .size(50.dp)
                    .clip(RoundedCornerShape(14.dp)),
                contentScale = ContentScale.Crop
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "CREATED BY",
                style = MaterialTheme.typography.labelSmall,
                color = ElectricCyan,
                letterSpacing = 2.sp,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "ADITYA RANMALE",
                fontFamily = CinzelFontFamily,
                fontWeight = FontWeight.Bold,
                fontSize = 24.sp,
                letterSpacing = 3.sp,
                color = MaterialTheme.colorScheme.primary,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = "AR CREATOR",
                fontFamily = CinzelFontFamily,
                fontWeight = FontWeight.Bold,
                fontSize = 15.sp,
                letterSpacing = 3.sp,
                color = AcademicGold,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = pageSubtitle,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
private fun PermissionToggleRow(
    label: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.weight(1f)
        )
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange
        )
    }
}
