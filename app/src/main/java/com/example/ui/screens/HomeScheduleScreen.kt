package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccessAlarm
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.NotificationsOff
import androidx.compose.material.icons.filled.RocketLaunch
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material3.AssistChip
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.FilterChip
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.local.AppRepository
import com.example.data.local.ScheduleCycleType
import com.example.data.local.ScheduleEntity
import com.example.data.local.ScheduleFormInput
import com.example.data.local.ScheduleTimeBlock
import com.example.data.local.UserProfileEntity
import com.example.ui.theme.AcademicGold
import com.example.ui.theme.CinzelFontFamily
import com.example.ui.theme.CoralAlert
import com.example.ui.theme.ElectricCyan
import com.example.ui.theme.EmeraldRecharge
import com.example.ui.theme.RobotoMonoFontFamily
import com.example.ui.theme.VioletSleep

@Composable
fun HomeScheduleScreen(
    schedules: List<ScheduleEntity>,
    selectedCycleType: ScheduleCycleType,
    userProfile: UserProfileEntity?,
    isGeneratingSchedule: Boolean,
    onSelectCycle: (ScheduleCycleType) -> Unit,
    onGenerateSchedule: (ScheduleFormInput) -> Unit,
    onToggleScheduleActive: (ScheduleEntity) -> Unit,
    onToggleScheduleMasterAlarm: (ScheduleEntity) -> Unit,
    onDeleteSchedule: (Int) -> Unit,
    onSaveBlock: (ScheduleEntity, ScheduleTimeBlock, Boolean) -> Unit,
    onDeleteBlock: (ScheduleEntity, String) -> Unit,
    onTriggerTwoStageAlert: (ScheduleEntity, ScheduleTimeBlock) -> Unit,
    onLaunchSystemClock: (ScheduleTimeBlock) -> Unit,
    onViewExplanation: (ScheduleEntity) -> Unit
) {
    var showGenerateFormDialog by remember { mutableStateOf(false) }
    var editingScheduleAndBlock by remember {
        mutableStateOf<Pair<ScheduleEntity, ScheduleTimeBlock?>?>(null)
    }
    var filterByCycleOnly by remember { mutableStateOf(false) }

    val activeCount = schedules.count { it.isActive }
    val storedCount = schedules.size

    val displayedSchedules = remember(schedules, selectedCycleType, filterByCycleOnly) {
        if (filterByCycleOnly && !selectedCycleType.isComingSoon) {
            schedules.filter { it.cycleTypeId == selectedCycleType.id }
        } else {
            schedules
        }
    }

    Scaffold(
        floatingActionButton = {
            if (!selectedCycleType.isComingSoon) {
                ExtendedFloatingActionButton(
                    onClick = { showGenerateFormDialog = true },
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary,
                    modifier = Modifier.testTag("open_ai_schedule_form_fab")
                ) {
                    Icon(Icons.Default.AutoAwesome, contentDescription = "Generate AI Schedule")
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("New AI Time Table", fontWeight = FontWeight.Bold)
                }
            }
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 12.dp, bottom = 96.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // 1. Hero Banner & AR CREATOR Header
            item {
                Card(
                    shape = RoundedCornerShape(22.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Box(modifier = Modifier.height(175.dp)) {
                        Image(
                            painter = painterResource(id = R.drawable.img_academic_hero_1790778675070),
                            contentDescription = "Academic Schedule Engine Banner",
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop
                        )
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(
                                    Brush.verticalGradient(
                                        listOf(
                                            Color(0x88080D1A),
                                            Color(0xEE080D1A)
                                        )
                                    )
                                )
                                .padding(18.dp)
                        ) {
                            Column(
                                modifier = Modifier.fillMaxSize(),
                                verticalArrangement = Arrangement.SpaceBetween
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Surface(
                                        color = AcademicGold.copy(alpha = 0.22f),
                                        shape = RoundedCornerShape(50)
                                    ) {
                                        Text(
                                            text = "AR CREATOR • SCHEDULE ENGINE",
                                            fontFamily = CinzelFontFamily,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 11.sp,
                                            color = AcademicGold,
                                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 5.dp)
                                        )
                                    }
                                    Surface(
                                        color = ElectricCyan.copy(alpha = 0.2f),
                                        shape = RoundedCornerShape(50)
                                    ) {
                                        Text(
                                            text = userProfile?.classGrade ?: "10th Grade",
                                            style = MaterialTheme.typography.labelSmall,
                                            color = ElectricCyan,
                                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                                        )
                                    }
                                }

                                Column {
                                    Text(
                                        text = "Counseling : fun education",
                                        style = MaterialTheme.typography.headlineMedium,
                                        color = Color.White
                                    )
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = "04:00 AM Absorption • 5-Min Buffers • 20:00 Evening Execution",
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = Color(0xFFCBD5E1)
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // 2. System Rule Capacity Bar (Max 4 Stored, Max 2 Active Simultaneously)
            item {
                Card(
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant
                    ),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "SYSTEM RULE CAPACITY MONITOR",
                                style = MaterialTheme.typography.labelLarge,
                                color = MaterialTheme.colorScheme.primary
                            )
                            Text(
                                text = "Max 4 Stored • Max 2 Active",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Surface(
                                modifier = Modifier.weight(1f),
                                shape = RoundedCornerShape(14.dp),
                                color = MaterialTheme.colorScheme.surface
                            ) {
                                Row(
                                    modifier = Modifier.padding(12.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Column {
                                        Text(
                                            text = "Stored Profiles",
                                            style = MaterialTheme.typography.bodyMedium,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                        Text(
                                            text = "$storedCount / ${AppRepository.MAX_STORED_SCHEDULES}",
                                            style = MaterialTheme.typography.titleLarge,
                                            color = if (storedCount >= AppRepository.MAX_STORED_SCHEDULES)
                                                CoralAlert else MaterialTheme.colorScheme.onSurface
                                        )
                                    }
                                    Icon(
                                        imageVector = Icons.Default.CalendarMonth,
                                        contentDescription = null,
                                        tint = MaterialTheme.colorScheme.primary
                                    )
                                }
                            }

                            Surface(
                                modifier = Modifier.weight(1f),
                                shape = RoundedCornerShape(14.dp),
                                color = MaterialTheme.colorScheme.surface
                            ) {
                                Row(
                                    modifier = Modifier.padding(12.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Column {
                                        Text(
                                            text = "Active / ON Now",
                                            style = MaterialTheme.typography.bodyMedium,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                        Text(
                                            text = "$activeCount / ${AppRepository.MAX_ACTIVE_SCHEDULES}",
                                            style = MaterialTheme.typography.titleLarge,
                                            color = EmeraldRecharge
                                        )
                                    }
                                    Icon(
                                        imageVector = Icons.Default.NotificationsActive,
                                        contentDescription = null,
                                        tint = EmeraldRecharge
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // 3. Five Time Boundary Tabs (I] Regular 24H, II] Weekly 7D, III] 15-Day, IV] Monthly 30D, V] Yearly Coming Soon)
            item {
                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Time Boundary Cycles (I – V)",
                            style = MaterialTheme.typography.titleMedium
                        )
                        FilterChip(
                            selected = filterByCycleOnly,
                            onClick = { filterByCycleOnly = !filterByCycleOnly },
                            label = {
                                Text(if (filterByCycleOnly) "Showing Selected Cycle" else "Showing All ($storedCount)")
                            }
                        )
                    }

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        ScheduleCycleType.entries.forEach { cycle ->
                            FilterChip(
                                selected = selectedCycleType == cycle,
                                onClick = { onSelectCycle(cycle) },
                                label = { Text(cycle.shortTitle) },
                                modifier = Modifier.testTag("cycle_chip_${cycle.id}")
                            )
                        }
                    }

                    // Selected Cycle Info Banner
                    Card(
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surface
                        ),
                        border = androidx.compose.foundation.BorderStroke(
                            1.dp,
                            MaterialTheme.colorScheme.primary.copy(alpha = 0.35f)
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 6.dp)
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = selectedCycleType.fullTitle,
                                    style = MaterialTheme.typography.titleSmall,
                                    color = MaterialTheme.colorScheme.primary
                                )
                                Surface(
                                    color = if (selectedCycleType.isComingSoon)
                                        CoralAlert.copy(alpha = 0.2f)
                                    else MaterialTheme.colorScheme.primaryContainer,
                                    shape = RoundedCornerShape(50)
                                ) {
                                    Text(
                                        text = selectedCycleType.badge,
                                        style = MaterialTheme.typography.labelSmall,
                                        color = if (selectedCycleType.isComingSoon)
                                            CoralAlert else MaterialTheme.colorScheme.onPrimaryContainer,
                                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = selectedCycleType.description,
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )

                            if (selectedCycleType.isComingSoon) {
                                Spacer(modifier = Modifier.height(12.dp))
                                Surface(
                                    color = MaterialTheme.colorScheme.surfaceVariant,
                                    shape = RoundedCornerShape(12.dp),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Row(
                                        modifier = Modifier.padding(14.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.RocketLaunch,
                                            contentDescription = null,
                                            tint = AcademicGold,
                                            modifier = Modifier.size(28.dp)
                                        )
                                        Spacer(modifier = Modifier.width(12.dp))
                                        Column {
                                            Text(
                                                text = "V] Yearly Time Table — Status: Coming Soon",
                                                style = MaterialTheme.typography.titleSmall,
                                            )
                                            Text(
                                                text = "Annual 365-Day Academic Periodization, Board Exam Countdown & Term Macro-Cycles by AR CREATOR are under active development.",
                                                style = MaterialTheme.typography.bodyMedium,
                                                color = MaterialTheme.colorScheme.onSurfaceVariant
                                            )
                                        }
                                    }
                                }
                            } else {
                                Spacer(modifier = Modifier.height(10.dp))
                                Button(
                                    onClick = { showGenerateFormDialog = true },
                                    enabled = !isGeneratingSchedule,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .testTag("generate_cycle_schedule_button"),
                                    shape = RoundedCornerShape(12.dp)
                                ) {
                                    if (isGeneratingSchedule) {
                                        CircularProgressIndicator(
                                            modifier = Modifier.size(18.dp),
                                            strokeWidth = 2.dp,
                                            color = MaterialTheme.colorScheme.onPrimary
                                        )
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text("Synthesizing Cognitive-Split Schedule…")
                                    } else {
                                        Icon(Icons.Default.AutoAwesome, contentDescription = null)
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text("Configure & Generate ${selectedCycleType.shortTitle} with AI")
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // 4. Stored Schedule Cards
            items(displayedSchedules, key = { it.id }) { schedule ->
                ScheduleProfileCard(
                    schedule = schedule,
                    onToggleActive = { onToggleScheduleActive(schedule) },
                    onToggleMasterAlarm = { onToggleScheduleMasterAlarm(schedule) },
                    onDelete = { onDeleteSchedule(schedule.id) },
                    onAddBlock = { editingScheduleAndBlock = schedule to null },
                    onEditBlock = { block -> editingScheduleAndBlock = schedule to block },
                    onDeleteBlock = { blockId -> onDeleteBlock(schedule, blockId) },
                    onTriggerTwoStageAlert = { block -> onTriggerTwoStageAlert(schedule, block) },
                    onLaunchSystemClock = onLaunchSystemClock,
                    onViewExplanation = { onViewExplanation(schedule) }
                )
            }

            // 5. Creator Name Footer at the bottom of Home Page
            item {
                ArCreatorPageFooter(
                    pageSubtitle = "Counseling : fun education • Dynamic Schedule Engine"
                )
            }
        }
    }

    if (showGenerateFormDialog) {
        AiScheduleGenerationFormDialog(
            initialCycleType = selectedCycleType,
            userProfile = userProfile,
            onDismiss = { showGenerateFormDialog = false },
            onGenerate = { formInput ->
                showGenerateFormDialog = false
                onGenerateSchedule(formInput)
            }
        )
    }

    editingScheduleAndBlock?.let { (sched, existingBlock) ->
        EditTimeBlockDialog(
            initialBlock = existingBlock,
            onDismiss = { editingScheduleAndBlock = null },
            onSave = { savedBlock ->
                onSaveBlock(sched, savedBlock, existingBlock == null)
                editingScheduleAndBlock = null
            }
        )
    }
}

@Composable
private fun ScheduleProfileCard(
    schedule: ScheduleEntity,
    onToggleActive: () -> Unit,
    onToggleMasterAlarm: () -> Unit,
    onDelete: () -> Unit,
    onAddBlock: () -> Unit,
    onEditBlock: (ScheduleTimeBlock) -> Unit,
    onDeleteBlock: (String) -> Unit,
    onTriggerTwoStageAlert: (ScheduleTimeBlock) -> Unit,
    onLaunchSystemClock: (ScheduleTimeBlock) -> Unit,
    onViewExplanation: () -> Unit
) {
    var expanded by remember { mutableStateOf(schedule.isActive) }
    val blocks = remember(schedule.blocksJson) { AppRepository.decodeBlocks(schedule.blocksJson) }
    val cycle = ScheduleCycleType.fromId(schedule.cycleTypeId)

    Card(
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        border = androidx.compose.foundation.BorderStroke(
            width = if (schedule.isActive) 2.dp else 1.dp,
            color = if (schedule.isActive) EmeraldRecharge else MaterialTheme.colorScheme.surfaceVariant
        ),
        modifier = Modifier
            .fillMaxWidth()
            .testTag("schedule_card_${schedule.id}")
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Top Row: Cycle Badge + Active Status Toggle
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    color = MaterialTheme.colorScheme.primaryContainer,
                    shape = RoundedCornerShape(50)
                ) {
                    Text(
                        text = cycle.shortTitle,
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onPrimaryContainer,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                    )
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = if (schedule.isActive) "ACTIVE / ON" else "STANDBY / OFF",
                        style = MaterialTheme.typography.labelLarge,
                        color = if (schedule.isActive) EmeraldRecharge else MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Switch(
                        checked = schedule.isActive,
                        onCheckedChange = { onToggleActive() },
                        modifier = Modifier.testTag("toggle_active_switch_${schedule.id}")
                    )
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = schedule.title,
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.onSurface
            )

            Text(
                text = "${schedule.studentName} (${schedule.classGrade}, Age ${schedule.age}) • ${schedule.institution}",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Text(
                text = "Goal: ${schedule.academicGoal}",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.primary
            )

            Spacer(modifier = Modifier.height(10.dp))

            // Master Switch Row: Independent global toggle switch per schedule profile
            Surface(
                color = MaterialTheme.colorScheme.surfaceVariant,
                shape = RoundedCornerShape(14.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.weight(1f)
                    ) {
                        Icon(
                            imageVector = if (schedule.alarmsMasterEnabled)
                                Icons.Default.NotificationsActive else Icons.Default.NotificationsOff,
                            contentDescription = null,
                            tint = if (schedule.alarmsMasterEnabled)
                                AcademicGold else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Column {
                            Text(
                                text = "Master Alarm & Push Switch",
                                style = MaterialTheme.typography.labelLarge
                            )
                            Text(
                                text = "T-0s Push Notification + T+5s Hardware Alarm",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    Switch(
                        checked = schedule.alarmsMasterEnabled,
                        onCheckedChange = { onToggleMasterAlarm() },
                        modifier = Modifier.testTag("master_alarm_switch_${schedule.id}")
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Action Bar: Expand/Collapse Timeline, Add Custom Block, Delete Schedule
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedButton(
                    onClick = { expanded = !expanded },
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Icon(
                        imageVector = if (expanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                        contentDescription = null
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("${blocks.size} Time Blocks")
                }

                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    AssistChip(
                        onClick = onViewExplanation,
                        label = { Text("Explain") },
                        leadingIcon = {
                            Icon(Icons.Default.Info, contentDescription = "Explain Timetable", modifier = Modifier.size(16.dp))
                        },
                        modifier = Modifier.testTag("explain_schedule_button_${schedule.id}")
                    )
                    AssistChip(
                        onClick = onAddBlock,
                        label = { Text("Add") },
                        leadingIcon = {
                            Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                        }
                    )
                    IconButton(onClick = onDelete) {
                        Icon(
                            imageVector = Icons.Default.DeleteOutline,
                            contentDescription = "Delete Schedule",
                            tint = CoralAlert
                        )
                    }
                }
            }

            AnimatedVisibility(visible = expanded) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 12.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    HorizontalDivider()
                    blocks.forEach { block ->
                        TimeBlockRowCard(
                            block = block,
                            alarmsEnabled = schedule.alarmsMasterEnabled,
                            onTriggerTwoStageAlert = { onTriggerTwoStageAlert(block) },
                            onLaunchSystemClock = { onLaunchSystemClock(block) },
                            onEdit = { onEditBlock(block) },
                            onDelete = { onDeleteBlock(block.id) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun TimeBlockRowCard(
    block: ScheduleTimeBlock,
    alarmsEnabled: Boolean,
    onTriggerTwoStageAlert: () -> Unit,
    onLaunchSystemClock: () -> Unit,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    val metaColor = when (block.metaHeader.uppercase()) {
        "STUDY TIME" -> ElectricCyan
        "PRACTICE" -> AcademicGold
        "RECHARGE" -> EmeraldRecharge
        "SLEEPING" -> VioletSleep
        "LUNCH" -> Color(0xFFF97316)
        "SCHOOL" -> Color(0xFF38BDF8)
        else -> Color(0xFF94A3B8)
    }

    Surface(
        shape = RoundedCornerShape(14.dp),
        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.65f),
        modifier = Modifier
            .fillMaxWidth()
            .border(
                width = 1.dp,
                color = metaColor.copy(alpha = 0.4f),
                shape = RoundedCornerShape(14.dp)
            )
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // 24-Hour Marker in Roboto Mono
                Surface(
                    color = MaterialTheme.colorScheme.surface,
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        text = block.timeRange,
                        fontFamily = RobotoMonoFontFamily,
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }

                // Dynamic Alarm Meta Header Badge
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Surface(
                        color = metaColor.copy(alpha = 0.2f),
                        shape = RoundedCornerShape(50)
                    ) {
                        Text(
                            text = block.metaHeader,
                            fontFamily = RobotoMonoFontFamily,
                            fontWeight = FontWeight.Bold,
                            fontSize = 10.sp,
                            color = metaColor,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                        )
                    }
                    Surface(
                        color = MaterialTheme.colorScheme.surface,
                        shape = RoundedCornerShape(50)
                    ) {
                        Text(
                            text = block.durationLabel,
                            style = MaterialTheme.typography.labelSmall,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = block.title,
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = block.subjectOrDetail,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    Button(
                        onClick = onTriggerTwoStageAlert,
                        enabled = alarmsEnabled,
                        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                        modifier = Modifier.height(36.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = metaColor.copy(alpha = 0.22f),
                            contentColor = metaColor
                        ),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.NotificationsActive,
                            contentDescription = "Test 2-Stage Alarm",
                            modifier = Modifier.size(15.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("T-0s + T+5s Alert", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }

                    OutlinedButton(
                        onClick = onLaunchSystemClock,
                        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                        modifier = Modifier.height(36.dp),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.AccessAlarm,
                            contentDescription = "Set System Clock Alarm",
                            modifier = Modifier.size(15.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("OS Alarm", fontSize = 11.sp)
                    }
                }

                Row {
                    IconButton(onClick = onEdit, modifier = Modifier.size(34.dp)) {
                        Icon(
                            imageVector = Icons.Default.Edit,
                            contentDescription = "Edit Block",
                            modifier = Modifier.size(17.dp)
                        )
                    }
                    IconButton(onClick = onDelete, modifier = Modifier.size(34.dp)) {
                        Icon(
                            imageVector = Icons.Default.DeleteOutline,
                            contentDescription = "Delete Block",
                            tint = CoralAlert,
                            modifier = Modifier.size(17.dp)
                        )
                    }
                }
            }
        }
    }
}
