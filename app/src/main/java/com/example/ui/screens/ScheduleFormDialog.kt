package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Code
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.data.local.FixedCurriculumCatalog
import com.example.data.local.ScheduleCycleType
import com.example.data.local.ScheduleFormInput
import com.example.data.local.ScheduleTimeBlock
import com.example.data.local.UserProfileEntity
import com.example.network.GeminiService
import com.example.ui.theme.AcademicGold
import com.example.ui.theme.RobotoMonoFontFamily
import java.util.UUID

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun AiScheduleGenerationFormDialog(
    initialCycleType: ScheduleCycleType,
    userProfile: UserProfileEntity?,
    onDismiss: () -> Unit,
    onGenerate: (ScheduleFormInput) -> Unit
) {
    val defaultAgeInt = userProfile?.age?.toIntOrNull() ?: 15
    val defaultWakeUp = if (defaultAgeInt <= 11) "05:00" else "04:00"
    val defaultSleep = if (defaultAgeInt <= 11) "22:00" else "23:00"

    var selectedCycle by remember {
        mutableStateOf(
            if (initialCycleType.isComingSoon) ScheduleCycleType.REGULAR_24H else initialCycleType
        )
    }
    var scheduleTitle by remember { mutableStateOf("${selectedCycle.shortTitle} Cognitive Plan") }
    var studentName by remember { mutableStateOf(userProfile?.name ?: "govunda suroshe") }
    var classGrade by remember { mutableStateOf(userProfile?.classGrade ?: "10th Grade") }
    var ageText by remember { mutableStateOf(userProfile?.age ?: "15") }
    var institution by remember { mutableStateOf(userProfile?.schoolCollege ?: "St. Xavier's Academy") }
    var academicGoal by remember {
        mutableStateOf(userProfile?.academicGoal ?: "Score 96%+ & Master Core Curriculum")
    }
    var homeEnvironment by remember { mutableStateOf("Independent Study Space") }

    var wakeUpTime by remember { mutableStateOf(defaultWakeUp) }
    var schoolHours by remember { mutableStateOf("07:00 to 12:00") }
    var commuteDuration by remember { mutableStateOf("30 Mins") }
    var lunchWindow by remember { mutableStateOf("12:30 to 13:00") }
    var tuitionClasses by remember { mutableStateOf("14:00 to 17:15") }
    var extracurriculars by remember { mutableStateOf("17:45 to 19:00 (Sports & Fitness)") }
    var dinnerWindow by remember { mutableStateOf("19:00 to 20:00") }
    var sleepWindow by remember { mutableStateOf(defaultSleep) }
    var playtimeBreaks by remember { mutableStateOf("17:45 to 19:00") }

    var selectedSubjects by remember {
        mutableStateOf(setOf("Science", "Mathematics", "History"))
    }
    val subjectCountInt = selectedSubjects.size
    val subjectNamesJoined = FixedCurriculumCatalog.allowedSubjects
        .filter { selectedSubjects.contains(it) }
        .joinToString(", ")

    var weekendRuleNotes by remember {
        mutableStateOf("Sat: 3-Hr Timed Mock + Weak Topic Lab | Sun: Mind-Map Synthesis + Recharge")
    }
    var specialEventsNotes by remember {
        mutableStateOf("Day 7: Unit Test | Day 14: Science Project Deadline | Day 28: Monthly Mock Exam")
    }
    var showSystemPromptPreview by remember { mutableStateOf(false) }

    val grades = (1..12).map {
        when (it) {
            1 -> "1st Grade"
            2 -> "2nd Grade"
            3 -> "3rd Grade"
            else -> "${it}th Grade"
        }
    }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth(0.95f)
                .padding(vertical = 16.dp),
            shape = RoundedCornerShape(24.dp),
            color = MaterialTheme.colorScheme.surface,
            tonalElevation = 6.dp
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
                    .padding(20.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "AI SCHEDULE GENERATION FORM",
                            style = MaterialTheme.typography.titleLarge,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Text(
                            text = "Cognitive-Split & Hard Time Boundary Engine by AR CREATOR",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.Close, contentDescription = "Close Form")
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = "Select Time Table Cycle Boundary:",
                    style = MaterialTheme.typography.labelLarge
                )
                Spacer(modifier = Modifier.height(6.dp))
                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    ScheduleCycleType.entries.filterNot { it.isComingSoon }.forEach { cycle ->
                        FilterChip(
                            selected = selectedCycle == cycle,
                            onClick = {
                                selectedCycle = cycle
                                scheduleTitle = "${cycle.shortTitle} Cognitive Plan"
                            },
                            label = { Text(cycle.shortTitle) }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                OutlinedTextField(
                    value = scheduleTitle,
                    onValueChange = { scheduleTitle = it },
                    label = { Text("Schedule Profile Name") },
                    singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("form_schedule_title_input")
                )

                Spacer(modifier = Modifier.height(8.dp))

                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    OutlinedTextField(
                        value = studentName,
                        onValueChange = { studentName = it },
                        label = { Text("Student Name") },
                        singleLine = true,
                        modifier = Modifier.weight(1.4f)
                    )
                    OutlinedTextField(
                        value = ageText,
                        onValueChange = {
                            ageText = it
                            val parsedAge = it.toIntOrNull() ?: 15
                            wakeUpTime = if (parsedAge <= 11) "05:00" else "04:00"
                            sleepWindow = if (parsedAge <= 11) "22:00" else "23:00"
                        },
                        label = { Text("Age") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        singleLine = true,
                        modifier = Modifier.weight(0.8f)
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "Class / Grade (1st - 12th):",
                    style = MaterialTheme.typography.labelLarge
                )
                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    grades.forEach { g ->
                        FilterChip(
                            selected = classGrade == g,
                            onClick = { classGrade = g },
                            label = { Text(g.replace(" Grade", "")) }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = institution,
                    onValueChange = { institution = it },
                    label = { Text("Institution (School / College Name)") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = academicGoal,
                    onValueChange = { academicGoal = it },
                    label = { Text("Core Academic Goal (Target Description)") },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(10.dp))

                Text(
                    text = "Home Environment:",
                    style = MaterialTheme.typography.labelLarge
                )
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    listOf("Independent Study Space", "Strict Structured Household").forEach { env ->
                        FilterChip(
                            selected = homeEnvironment == env,
                            onClick = { homeEnvironment = env },
                            label = { Text(env) }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                Text(
                    text = "Key Baseline Time Boundaries (24-Hour Markers):",
                    style = MaterialTheme.typography.titleSmall,
                    color = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.height(6.dp))

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = wakeUpTime,
                        onValueChange = { wakeUpTime = it },
                        label = { Text("Wake-up (04:00/05:00)") },
                        singleLine = true,
                        modifier = Modifier.weight(1f)
                    )
                    OutlinedTextField(
                        value = sleepWindow,
                        onValueChange = { sleepWindow = it },
                        label = { Text("Sleep (22:00/23:00)") },
                        singleLine = true,
                        modifier = Modifier.weight(1f)
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = schoolHours,
                        onValueChange = { schoolHours = it },
                        label = { Text("School/College Hours") },
                        singleLine = true,
                        modifier = Modifier.weight(1.2f)
                    )
                    OutlinedTextField(
                        value = commuteDuration,
                        onValueChange = { commuteDuration = it },
                        label = { Text("Commute Duration") },
                        singleLine = true,
                        modifier = Modifier.weight(0.9f)
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = lunchWindow,
                        onValueChange = { lunchWindow = it },
                        label = { Text("Lunch Window") },
                        singleLine = true,
                        modifier = Modifier.weight(1f)
                    )
                    OutlinedTextField(
                        value = dinnerWindow,
                        onValueChange = { dinnerWindow = it },
                        label = { Text("Dinner Window") },
                        singleLine = true,
                        modifier = Modifier.weight(1f)
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = tuitionClasses,
                        onValueChange = { tuitionClasses = it },
                        label = { Text("Tuition / Coaching") },
                        singleLine = true,
                        modifier = Modifier.weight(1f)
                    )
                    OutlinedTextField(
                        value = playtimeBreaks,
                        onValueChange = { playtimeBreaks = it },
                        label = { Text("Playtime / Breaks") },
                        singleLine = true,
                        modifier = Modifier.weight(1f)
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = extracurriculars,
                    onValueChange = { extracurriculars = it },
                    label = { Text("Alternative / Extracurricular Activities") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(10.dp))

                Text(
                    text = "Strict Subject Lock ($subjectCountInt Selected — Extra Subjects Prohibited):",
                    style = MaterialTheme.typography.titleSmall,
                    color = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.height(4.dp))
                Surface(
                    color = AcademicGold.copy(alpha = 0.14f),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "Select from approved curriculum subjects only. Adding extra unlisted subjects is disabled.",
                        style = MaterialTheme.typography.bodySmall,
                        color = AcademicGold,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                    )
                }
                Spacer(modifier = Modifier.height(6.dp))
                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    FixedCurriculumCatalog.allowedSubjects.forEach { subj ->
                        val isSelected = selectedSubjects.contains(subj)
                        FilterChip(
                            selected = isSelected,
                            onClick = {
                                selectedSubjects = if (isSelected && selectedSubjects.size > 1) {
                                    selectedSubjects - subj
                                } else {
                                    selectedSubjects + subj
                                }
                            },
                            label = { Text(subj) },
                            modifier = Modifier.testTag("form_subject_chip_${subj.lowercase().replace(" ", "_")}")
                        )
                    }
                }

                if (selectedCycle != ScheduleCycleType.REGULAR_24H) {
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = weekendRuleNotes,
                        onValueChange = { weekendRuleNotes = it },
                        label = { Text("Saturday & Sunday Deep Customization Rules") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = specialEventsNotes,
                        onValueChange = { specialEventsNotes = it },
                        label = { Text("Special Events (Exams, Deadlines, Monthly Metrics)") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                OutlinedButton(
                    onClick = { showSystemPromptPreview = !showSystemPromptPreview },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(Icons.Default.Code, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        if (showSystemPromptPreview) "Hide AI Schedule Engine System Prompt"
                        else "Inspect AI Schedule Engine System Prompt"
                    )
                }

                AnimatedVisibility(visible = showSystemPromptPreview) {
                    Surface(
                        color = MaterialTheme.colorScheme.surfaceVariant,
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 8.dp)
                    ) {
                        Text(
                            text = GeminiService.SCHEDULE_ENGINE_SYSTEM_PROMPT,
                            fontFamily = RobotoMonoFontFamily,
                            style = MaterialTheme.typography.labelSmall,
                            modifier = Modifier.padding(12.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    OutlinedButton(
                        onClick = onDismiss,
                        modifier = Modifier
                            .weight(1f)
                            .height(50.dp)
                    ) {
                        Text("Cancel")
                    }
                    Button(
                        onClick = {
                            onGenerate(
                                ScheduleFormInput(
                                    cycleTypeId = selectedCycle.id,
                                    scheduleCustomTitle = scheduleTitle.trim(),
                                    studentName = studentName.trim(),
                                    classGrade = classGrade,
                                    age = ageText.toIntOrNull() ?: 15,
                                    institution = institution.trim(),
                                    coreAcademicGoal = academicGoal.trim(),
                                    homeEnvironment = homeEnvironment,
                                    wakeUpTime = wakeUpTime.trim(),
                                    schoolHours = schoolHours.trim(),
                                    commuteDuration = commuteDuration.trim(),
                                    lunchWindow = lunchWindow.trim(),
                                    tuitionClasses = tuitionClasses.trim(),
                                    extracurriculars = extracurriculars.trim(),
                                    dinnerWindow = dinnerWindow.trim(),
                                    sleepWindow = sleepWindow.trim(),
                                    playtimeBreaks = playtimeBreaks.trim(),
                                    subjectCount = subjectCountInt,
                                    subjectNames = subjectNamesJoined,
                                    weekendRuleNotes = weekendRuleNotes.trim(),
                                    specialEventsNotes = specialEventsNotes.trim()
                                )
                            )
                        },
                        modifier = Modifier
                            .weight(1.5f)
                            .height(50.dp)
                            .testTag("submit_generate_schedule_button")
                    ) {
                        Icon(Icons.Default.AutoAwesome, contentDescription = null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Generate AI Timetable", fontWeight = FontWeight.Bold)
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                ArCreatorPageFooter(
                    pageSubtitle = "Counseling : fun education • Created by Aditya Ranmale"
                )
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun EditTimeBlockDialog(
    initialBlock: ScheduleTimeBlock?,
    onDismiss: () -> Unit,
    onSave: (ScheduleTimeBlock) -> Unit
) {
    var timeRange by remember { mutableStateOf(initialBlock?.timeRange ?: "06:00 - 06:45") }
    var title by remember { mutableStateOf(initialBlock?.title ?: "Custom Focus Session") }
    var detail by remember { mutableStateOf(initialBlock?.subjectOrDetail ?: "Subject: Science / Revision") }
    var duration by remember { mutableStateOf(initialBlock?.durationLabel ?: "45 Mins") }
    var metaHeader by remember { mutableStateOf(initialBlock?.metaHeader ?: "STUDY TIME") }
    var dayPhase by remember { mutableStateOf(initialBlock?.dayOrPhaseLabel ?: "24H Cycle") }

    val metaHeaders = listOf("STUDY TIME", "RECHARGE", "SLEEPING", "LUNCH", "SCHOOL", "PRACTICE", "ROUTINE")

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(if (initialBlock == null) "Add Custom Time Block" else "Edit Scheduled Block")
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(
                    value = timeRange,
                    onValueChange = { timeRange = it },
                    label = { Text("24-Hour Time Range (e.g., 04:05 - 04:50)") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Block Title") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = detail,
                    onValueChange = { detail = it },
                    label = { Text("Subject / Description (Push Notification Text)") },
                    modifier = Modifier.fillMaxWidth()
                )
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = duration,
                        onValueChange = { duration = it },
                        label = { Text("Duration") },
                        singleLine = true,
                        modifier = Modifier.weight(1f)
                    )
                    OutlinedTextField(
                        value = dayPhase,
                        onValueChange = { dayPhase = it },
                        label = { Text("Day / Phase") },
                        singleLine = true,
                        modifier = Modifier.weight(1f)
                    )
                }
                Text(
                    text = "Alarm Meta Header:",
                    style = MaterialTheme.typography.labelLarge
                )
                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    metaHeaders.forEach { header ->
                        FilterChip(
                            selected = metaHeader == header,
                            onClick = { metaHeader = header },
                            label = { Text(header) }
                        )
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    onSave(
                        ScheduleTimeBlock(
                            id = initialBlock?.id ?: UUID.randomUUID().toString(),
                            timeRange = timeRange.trim(),
                            title = title.trim(),
                            subjectOrDetail = detail.trim(),
                            durationLabel = duration.trim(),
                            metaHeader = metaHeader,
                            dayOrPhaseLabel = dayPhase.trim()
                        )
                    )
                }
            ) {
                Text("Save Block")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}
