package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.AssignmentTurnedIn
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Cancel
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Explore
import androidx.compose.material.icons.filled.Games
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.LockOpen
import androidx.compose.material.icons.filled.Message
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Sms
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.CoinSocialStateEntity
import com.example.data.local.ExamResultEntity
import com.example.data.local.FixedCurriculumCatalog
import com.example.data.local.UserProfileEntity
import com.example.ui.theme.AcademicGold
import com.example.ui.theme.CinzelFontFamily
import com.example.ui.theme.CoralAlert
import com.example.ui.theme.ElectricCyan
import com.example.ui.theme.EmeraldRecharge
import com.example.ui.theme.RobotoMonoFontFamily
import com.example.util.CheckedExamEvaluation
import com.example.util.ExamQuestionItem
import com.example.util.ExploreEngine
import com.example.util.ExploreGameType
import com.example.util.ExploreSelectionSection
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun ExploreScreen(
    selectedSection: ExploreSelectionSection,
    userProfile: UserProfileEntity?,
    coinSocialState: CoinSocialStateEntity?,
    examResults: List<ExamResultEntity>,
    activeSocialAppSession: String?,
    socialSessionClosedNotice: String?,
    onSelectSection: (ExploreSelectionSection) -> Unit,
    onAwardCoinFromGame: () -> Unit,
    onSubmitAndEvaluateExam: (
        examTitle: String,
        classGrade: String,
        subject: String,
        chapterScope: String,
        patternName: String,
        questions: List<ExamQuestionItem>,
        studentAnswers: Map<String, String>,
        isWeeklyExam: Boolean
    ) -> CheckedExamEvaluation,
    onMarkExamSentToParent: (ExamResultEntity) -> Unit,
    onUpdateWeeklyExamChapter: (String) -> Unit,
    onToggleCancelWeeklyExam: () -> Unit,
    onUpdateSocialLimitsAndParent: (Int, String, Boolean) -> Unit,
    onRedeemCoinsForSocial: (Int) -> Unit,
    onOpenSocialMediaApp: (String, String?) -> Unit,
    onCloseActiveSocialSession: () -> Unit,
    onCreateSubjectTimetable: (
        customTitle: String,
        classGrade: String,
        selectedSubjects: List<String>,
        examTarget: String,
        includeSchool: Boolean,
        schoolTiming: String,
        includeTuition: Boolean,
        tuitionTiming: String
    ) -> Unit
) {
    val wallet = coinSocialState ?: CoinSocialStateEntity()
    val defaultGrade = userProfile?.classGrade ?: "10th Grade"
    val studentName = userProfile?.name ?: "govunda suroshe"

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .testTag("explore_screen"),
        contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 12.dp, bottom = 96.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Explore Hero Banner + Live Coin Status
        item {
            Card(
                shape = RoundedCornerShape(22.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Surface(
                            color = AcademicGold.copy(alpha = 0.2f),
                            shape = RoundedCornerShape(50)
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 5.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Explore,
                                    contentDescription = null,
                                    tint = AcademicGold,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "EXPLORE HUB • 5 SELECTIONS",
                                    fontFamily = CinzelFontFamily,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 11.sp,
                                    color = AcademicGold
                                )
                            }
                        }

                        Surface(
                            color = EmeraldRecharge.copy(alpha = 0.2f),
                            shape = RoundedCornerShape(50),
                            modifier = Modifier.testTag("coin_balance_badge")
                        ) {
                            Text(
                                text = "${wallet.coinBalance} Coins (${wallet.coinBalance * 15}s)",
                                fontFamily = RobotoMonoFontFamily,
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp,
                                color = EmeraldRecharge,
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 5.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        text = "Counseling : fun education — Explore",
                        style = MaterialTheme.typography.titleLarge,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = "12 Unlimited Games (+1 Coin/Q) • Exam Checker & Weekly 20M Test • Social Lock (1 Coin = 15s) • Board Marks • Exam Timetable",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }

        // 5 Selection Switcher Chips
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                ExploreSelectionSection.entries.forEach { section ->
                    FilterChip(
                        selected = selectedSection == section,
                        onClick = { onSelectSection(section) },
                        label = { Text(section.shortTitle, fontWeight = FontWeight.SemiBold) },
                        modifier = Modifier.testTag("explore_section_chip_${section.number}")
                    )
                }
            }
        }

        // Active Section Content
        item {
            when (selectedSection) {
                ExploreSelectionSection.GAMES -> {
                    ExploreSelection1GamesView(
                        defaultGrade = defaultGrade,
                        wallet = wallet,
                        onAwardCoinFromGame = onAwardCoinFromGame
                    )
                }
                ExploreSelectionSection.EXAM_MAKER -> {
                    ExploreSelection2ExamView(
                        defaultGrade = defaultGrade,
                        studentName = studentName,
                        wallet = wallet,
                        examResults = examResults,
                        onSubmitAndEvaluateExam = onSubmitAndEvaluateExam,
                        onMarkExamSentToParent = onMarkExamSentToParent,
                        onUpdateWeeklyExamChapter = onUpdateWeeklyExamChapter,
                        onToggleCancelWeeklyExam = onToggleCancelWeeklyExam
                    )
                }
                ExploreSelectionSection.COIN_SOCIAL -> {
                    ExploreSelection3CoinsSocialView(
                        wallet = wallet,
                        activeSocialAppSession = activeSocialAppSession,
                        socialSessionClosedNotice = socialSessionClosedNotice,
                        onUpdateSocialLimitsAndParent = onUpdateSocialLimitsAndParent,
                        onRedeemCoinsForSocial = onRedeemCoinsForSocial,
                        onOpenSocialMediaApp = onOpenSocialMediaApp,
                        onCloseActiveSocialSession = onCloseActiveSocialSession
                    )
                }
                ExploreSelectionSection.BOARD_PATTERNS -> {
                    ExploreSelection4BoardPatternsView()
                }
                ExploreSelectionSection.SUBJECT_TIMETABLE -> {
                    ExploreSelection5SubjectTimetableView(
                        defaultGrade = defaultGrade,
                        onCreateSubjectTimetable = onCreateSubjectTimetable
                    )
                }
            }
        }

        // Creator Footer on Explore Page
        item {
            ArCreatorPageFooter(
                pageSubtitle = "Counseling : fun education • Explore 5-Selection Hub"
            )
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun ExploreSelection1GamesView(
    defaultGrade: String,
    wallet: CoinSocialStateEntity,
    onAwardCoinFromGame: () -> Unit
) {
    val grades = (1..12).map {
        when (it) {
            1 -> "1st Grade"
            2 -> "2nd Grade"
            3 -> "3rd Grade"
            else -> "${it}th Grade"
        }
    }

    // Rule 8: First selection has two subject modes: 'Sci, Math, History' only vs All Class Subjects
    var onlyCoreTrioMode by remember { mutableStateOf(true) }
    var selectedGrade by remember { mutableStateOf(defaultGrade) }
    val availableSubjects = if (onlyCoreTrioMode) {
        FixedCurriculumCatalog.coreExploreTrio
    } else {
        FixedCurriculumCatalog.allowedSubjects
    }
    var selectedSubject by remember(onlyCoreTrioMode) { mutableStateOf(availableSubjects.first()) }
    val chaptersForSubject = remember(selectedGrade, selectedSubject) {
        FixedCurriculumCatalog.chaptersFor(selectedGrade, selectedSubject)
    }

    var chapterMode by remember { mutableStateOf("All Chapters") } // "All Chapters", "Half Chapter", "Custom"
    var customChapterSelection by remember(chaptersForSubject) {
        mutableStateOf(chaptersForSubject.firstOrNull() ?: "Chapter 1")
    }
    val effectiveChapterScope = when (chapterMode) {
        "Half Chapter" -> "Half Chapter (${chaptersForSubject.take(2).joinToString(" & ")})"
        "Custom" -> customChapterSelection
        else -> "All Chapters (${chaptersForSubject.size} Units)"
    }

    var selectedGameType by remember { mutableStateOf(ExploreGameType.MCQ) }
    var roundSeed by remember { mutableIntStateOf(0) }
    var solvedSessionCount by remember { mutableIntStateOf(0) }
    var feedbackBanner by remember { mutableStateOf<String?>(null) }

    val currentQuestion = remember(
        selectedGameType,
        selectedGrade,
        selectedSubject,
        effectiveChapterScope,
        roundSeed
    ) {
        ExploreEngine.generateGameQuestion(
            gameType = selectedGameType,
            classGrade = selectedGrade,
            subject = selectedSubject,
            chapterScope = effectiveChapterScope,
            roundSeed = roundSeed
        )
    }

    Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
        Card(
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            border = BorderStroke(1.dp, ElectricCyan.copy(alpha = 0.45f)),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Text(
                    text = "1st Selection: 12 Unlimited Games (+1 Coin per Question)",
                    style = MaterialTheme.typography.titleMedium,
                    color = ElectricCyan
                )
                Text(
                    text = "Solved Today: $solvedSessionCount • Total Archived Coins: ${wallet.totalCoinsArchived} (Next 15-Coin Alert at ${((wallet.totalCoinsArchived / 15) + 1) * 15})",
                    style = MaterialTheme.typography.labelMedium,
                    color = AcademicGold
                )

                // Sub-Selection Toggle: 'Sci, Math, History' only vs All Subjects
                Text("Step A — Subject Mode Selection (2 Modes):", style = MaterialTheme.typography.labelLarge)
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    FilterChip(
                        selected = onlyCoreTrioMode,
                        onClick = { onlyCoreTrioMode = true },
                        label = { Text("Sci, Math, History Only") },
                        modifier = Modifier.testTag("explore_trio_mode_chip")
                    )
                    FilterChip(
                        selected = !onlyCoreTrioMode,
                        onClick = { onlyCoreTrioMode = false },
                        label = { Text("Full Class Subjects") },
                        modifier = Modifier.testTag("explore_all_subjects_chip")
                    )
                }

                // Class Selector
                Text("Step B — Select Class (1st–12th):", style = MaterialTheme.typography.labelLarge)
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    grades.forEach { grade ->
                        FilterChip(
                            selected = selectedGrade == grade,
                            onClick = { selectedGrade = grade },
                            label = { Text(grade.replace(" Grade", "")) }
                        )
                    }
                }

                // Subject Selector
                Text("Step C — Select Subject:", style = MaterialTheme.typography.labelLarge)
                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    availableSubjects.forEach { subj ->
                        FilterChip(
                            selected = selectedSubject == subj,
                            onClick = { selectedSubject = subj },
                            label = { Text(subj) }
                        )
                    }
                }

                // Chapter Selector: All Chapters / Half Chapter / Custom
                Text("Step D — Select Chapter Coverage:", style = MaterialTheme.typography.labelLarge)
                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    listOf("All Chapters", "Half Chapter", "Custom").forEach { mode ->
                        FilterChip(
                            selected = chapterMode == mode,
                            onClick = { chapterMode = mode },
                            label = { Text(mode) }
                        )
                    }
                }
                if (chapterMode == "Custom") {
                    FlowRow(
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        chaptersForSubject.forEach { ch ->
                            FilterChip(
                                selected = customChapterSelection == ch,
                                onClick = { customChapterSelection = ch },
                                label = { Text(ch) }
                            )
                        }
                    }
                }

                // 12 Game Types Selector
                Text("Step E — Choose Any of 12 Unlimited Game Modes:", style = MaterialTheme.typography.labelLarge)
                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    ExploreGameType.entries.forEach { game ->
                        FilterChip(
                            selected = selectedGameType == game,
                            onClick = {
                                selectedGameType = game
                                feedbackBanner = null
                            },
                            label = { Text(game.label) },
                            modifier = Modifier.testTag("game_type_chip_${game.id}")
                        )
                    }
                }
            }
        }

        // Live Interactive Unlimited Game Arena Card
        Card(
            shape = RoundedCornerShape(22.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
            border = BorderStroke(1.5.dp, AcademicGold.copy(alpha = 0.5f)),
            modifier = Modifier
                .fillMaxWidth()
                .testTag("active_game_arena_card")
        ) {
            Column(modifier = Modifier.padding(18.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Games, contentDescription = null, tint = AcademicGold)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "${currentQuestion.gameType.label} • Round #${roundSeed + 1}",
                            style = MaterialTheme.typography.titleSmall,
                            color = AcademicGold,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    Surface(color = EmeraldRecharge.copy(alpha = 0.2f), shape = RoundedCornerShape(50)) {
                        Text(
                            text = "+1 Coin Reward",
                            style = MaterialTheme.typography.labelSmall,
                            color = EmeraldRecharge,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                        )
                    }
                }

                Text(
                    text = currentQuestion.prompt,
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurface
                )

                if (!currentQuestion.diagramVisualAscii.isNullOrBlank()) {
                    Surface(
                        color = Color(0xFF0A1120),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .border(1.dp, ElectricCyan.copy(alpha = 0.4f), RoundedCornerShape(12.dp))
                    ) {
                        Text(
                            text = currentQuestion.diagramVisualAscii,
                            fontFamily = RobotoMonoFontFamily,
                            fontSize = 12.sp,
                            color = ElectricCyan,
                            modifier = Modifier.padding(12.dp)
                        )
                    }
                }

                currentQuestion.options.forEachIndexed { idx, opt ->
                    OutlinedButton(
                        onClick = {
                            if (opt == currentQuestion.correctAnswer) {
                                solvedSessionCount++
                                onAwardCoinFromGame()
                                feedbackBanner =
                                    "Correct! +1 Coin (15s Social Time Earned). ${currentQuestion.quickFact}"
                                roundSeed++
                            } else {
                                feedbackBanner = "Try again! Hint: ${currentQuestion.gameType.hint}."
                            }
                        },
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp)
                            .testTag("game_option_btn_$idx")
                    ) {
                        Text(
                            text = opt,
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }

                AnimatedVisibility(visible = feedbackBanner != null) {
                    Surface(
                        color = if (feedbackBanner?.startsWith("Correct") == true)
                            EmeraldRecharge.copy(alpha = 0.2f) else CoralAlert.copy(alpha = 0.16f),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = feedbackBanner ?: "",
                            style = MaterialTheme.typography.bodyMedium,
                            color = if (feedbackBanner?.startsWith("Correct") == true)
                                EmeraldRecharge else CoralAlert,
                            modifier = Modifier.padding(12.dp)
                        )
                    }
                }

                Button(
                    onClick = {
                        roundSeed++
                        feedbackBanner = null
                    },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("Skip / Load Next Unlimited Question")
                }
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun ExploreSelection2ExamView(
    defaultGrade: String,
    studentName: String,
    wallet: CoinSocialStateEntity,
    examResults: List<ExamResultEntity>,
    onSubmitAndEvaluateExam: (
        examTitle: String,
        classGrade: String,
        subject: String,
        chapterScope: String,
        patternName: String,
        questions: List<ExamQuestionItem>,
        studentAnswers: Map<String, String>,
        isWeeklyExam: Boolean
    ) -> CheckedExamEvaluation,
    onMarkExamSentToParent: (ExamResultEntity) -> Unit,
    onUpdateWeeklyExamChapter: (String) -> Unit,
    onToggleCancelWeeklyExam: () -> Unit
) {
    val context = LocalContext.current
    var examGrade by remember { mutableStateOf(defaultGrade) }
    var examSubject by remember { mutableStateOf("Science") }
    var examChapterMode by remember { mutableStateOf("All Chapters") }
    var customChapterText by remember { mutableStateOf("Laws of Motion & Gravitation") }
    var examPattern by remember { mutableStateOf("Board Pattern (Section A-D)") }
    var totalMarks by remember { mutableIntStateOf(20) }
    var isWeeklyExamMode by remember { mutableStateOf(false) }

    var weeklyChapterDraft by remember(wallet.weeklyExamSelectedChapters) {
        mutableStateOf(wallet.weeklyExamSelectedChapters)
    }

    var activePaperQuestions by remember { mutableStateOf<List<ExamQuestionItem>>(emptyList()) }
    val studentAnswers = remember { mutableStateMapOf<String, String>() }
    var latestCheckedEval by remember { mutableStateOf<CheckedExamEvaluation?>(null) }

    val effectiveChapter = when (examChapterMode) {
        "Half Chapter" -> "Half Chapter Coverage"
        "Custom" -> customChapterText.ifBlank { "Selected Custom Chapter" }
        else -> "All Chapters"
    }

    val dueFormat = remember(wallet.weeklyExamDueEpochMs) {
        SimpleDateFormat("EEE, dd MMM hh:mm a", Locale.getDefault()).format(Date(wallet.weeklyExamDueEpochMs))
    }

    Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
        // Weekly 20-Mark Regular Exam Card (Subject by App, Chapter by User, 24h Notice, Emergency Cancel)
        Card(
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
            border = BorderStroke(1.5.dp, AcademicGold.copy(alpha = 0.55f)),
            modifier = Modifier
                .fillMaxWidth()
                .testTag("weekly_20m_exam_card")
        ) {
            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.NotificationsActive, contentDescription = null, tint = AcademicGold)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Weekend 20-Mark Regular Exam",
                            style = MaterialTheme.typography.titleMedium,
                            color = AcademicGold
                        )
                    }
                    Surface(
                        color = if (wallet.weeklyExamCancelled) CoralAlert.copy(alpha = 0.2f) else EmeraldRecharge.copy(alpha = 0.2f),
                        shape = RoundedCornerShape(50)
                    ) {
                        Text(
                            text = if (wallet.weeklyExamCancelled) "CANCELLED" else "24H NOTICE ACTIVE",
                            style = MaterialTheme.typography.labelSmall,
                            color = if (wallet.weeklyExamCancelled) CoralAlert else EmeraldRecharge,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                        )
                    }
                }

                Text(
                    text = "App Assigned Subject: ${wallet.weeklyExamSubject} (20 Marks) • Scheduled: $dueFormat",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    text = "App selects the Subject automatically every week & notifies 24 hours in advance so you choose your Chapters. Marks earned = Coins added!",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                OutlinedTextField(
                    value = weeklyChapterDraft,
                    onValueChange = { weeklyChapterDraft = it },
                    label = { Text("Your Selected Chapters for Weekly ${wallet.weeklyExamSubject} Exam") },
                    modifier = Modifier.fillMaxWidth()
                )

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedButton(
                        onClick = { onUpdateWeeklyExamChapter(weeklyChapterDraft) },
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Save Chapters")
                    }
                    OutlinedButton(
                        onClick = onToggleCancelWeeklyExam,
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = CoralAlert),
                        modifier = Modifier.weight(1f)
                    ) {
                        Icon(Icons.Default.Cancel, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(if (wallet.weeklyExamCancelled) "Resume Exam" else "Emergency Cancel")
                    }
                }

                if (!wallet.weeklyExamCancelled) {
                    Button(
                        onClick = {
                            examSubject = wallet.weeklyExamSubject
                            examPattern = "Weekly 20-Mark Exam Pattern"
                            totalMarks = 20
                            isWeeklyExamMode = true
                            studentAnswers.clear()
                            latestCheckedEval = null
                            activePaperQuestions = ExploreEngine.generateCustomExamPaper(
                                classGrade = examGrade,
                                subject = wallet.weeklyExamSubject,
                                chapterScope = weeklyChapterDraft.ifBlank { "Selected Chapters" },
                                patternName = "Weekly 20-Mark Mandatory Pattern",
                                totalMarks = 20
                            )
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                            .testTag("start_weekly_20m_exam_button"),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Icon(Icons.Default.AssignmentTurnedIn, contentDescription = null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Start Weekly 20-Mark ${wallet.weeklyExamSubject} Exam", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        // Custom Exam Paper Maker Card
        Card(
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.4f)),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Text(
                    text = "2nd Selection: Custom Exam Paper Maker & Auto-Checker",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.primary
                )

                FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    FixedCurriculumCatalog.allowedSubjects.forEach { subj ->
                        FilterChip(
                            selected = examSubject == subj,
                            onClick = { examSubject = subj },
                            label = { Text(subj) }
                        )
                    }
                }

                FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    listOf("All Chapters", "Half Chapter", "Custom").forEach { mode ->
                        FilterChip(
                            selected = examChapterMode == mode,
                            onClick = { examChapterMode = mode },
                            label = { Text(mode) }
                        )
                    }
                }
                if (examChapterMode == "Custom") {
                    OutlinedTextField(
                        value = customChapterText,
                        onValueChange = { customChapterText = it },
                        label = { Text("Custom Chapter Scope") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    listOf(
                        "Board Pattern (Section A-D)",
                        "State Board Activity Pattern",
                        "ICSE Analytical Pattern",
                        "Unit Final 40M Pattern"
                    ).forEach { pat ->
                        FilterChip(
                            selected = examPattern == pat,
                            onClick = {
                                examPattern = pat
                                totalMarks = if (pat.contains("40M")) 40 else 20
                            },
                            label = { Text(pat) }
                        )
                    }
                }

                Button(
                    onClick = {
                        isWeeklyExamMode = false
                        studentAnswers.clear()
                        latestCheckedEval = null
                        activePaperQuestions = ExploreEngine.generateCustomExamPaper(
                            classGrade = examGrade,
                            subject = examSubject,
                            chapterScope = effectiveChapter,
                            patternName = examPattern,
                            totalMarks = totalMarks
                        )
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .testTag("generate_custom_exam_button"),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(Icons.Default.AutoAwesome, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Generate $totalMarks-Mark $examSubject Paper", fontWeight = FontWeight.Bold)
                }
            }
        }

        // Active Exam Question Solving View
        if (activePaperQuestions.isNotEmpty()) {
            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text(
                        text = if (isWeeklyExamMode) "Weekly 20-Mark Exam Paper ($examSubject)" else "$examSubject Exam Paper ($totalMarks Marks)",
                        style = MaterialTheme.typography.titleMedium,
                        color = AcademicGold
                    )
                    activePaperQuestions.forEach { q ->
                        Surface(
                            color = MaterialTheme.colorScheme.surface,
                            shape = RoundedCornerShape(14.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                                Text(
                                    text = "Q${q.number} [${q.sectionLabel}] — ${q.maxMarks} Marks",
                                    style = MaterialTheme.typography.labelLarge,
                                    color = ElectricCyan
                                )
                                Text(text = q.questionText, style = MaterialTheme.typography.bodyMedium)
                                OutlinedTextField(
                                    value = studentAnswers[q.id] ?: "",
                                    onValueChange = { studentAnswers[q.id] = it },
                                    label = { Text("Write your full-marks answer here…") },
                                    minLines = 2,
                                    modifier = Modifier.fillMaxWidth()
                                )
                            }
                        }
                    }

                    Button(
                        onClick = {
                            val title = if (isWeeklyExamMode) {
                                "Weekly 20M Exam ($examSubject)"
                            } else {
                                "$examPattern ($examSubject)"
                            }
                            latestCheckedEval = onSubmitAndEvaluateExam(
                                title,
                                examGrade,
                                examSubject,
                                effectiveChapter,
                                examPattern,
                                activePaperQuestions,
                                studentAnswers.toMap(),
                                isWeeklyExamMode
                            )
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp)
                            .testTag("submit_check_exam_button"),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = EmeraldRecharge,
                            contentColor = Color(0xFF002114)
                        ),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Icon(Icons.Default.CheckCircle, contentDescription = null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Auto-Check Paper, Award Coins & Fix Mistakes in Timetable", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        // Checked Evaluation Output + Language Tip + Mistake Remediation
        latestCheckedEval?.let { eval ->
            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = EmeraldRecharge.copy(alpha = 0.12f)),
                border = BorderStroke(1.5.dp, EmeraldRecharge),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        text = "Checked Result: ${eval.totalScore} / ${eval.maxMarks} Marks (${eval.percentage}%) • +${eval.coinsEarned} Coins Added!",
                        style = MaterialTheme.typography.titleMedium,
                        color = EmeraldRecharge
                    )
                    Text(
                        text = eval.languageSuggestions,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    HorizontalDivider()
                    eval.perQuestionFeedback.forEach { line ->
                        Text(text = "• $line", style = MaterialTheme.typography.bodySmall)
                    }
                    if (eval.mistakesList.isNotEmpty()) {
                        Surface(
                            color = AcademicGold.copy(alpha = 0.18f),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = "Timetable Mistake Remediation Scheduled: A dedicated study slot for (${eval.mistakesList.joinToString(", ")}) was automatically added to your active full-day Home Timetable!",
                                style = MaterialTheme.typography.bodySmall,
                                color = AcademicGold,
                                modifier = Modifier.padding(10.dp)
                            )
                        }
                    }
                }
            }
        }

        // Stored Exam Marks History + WhatsApp & SMS Parent Dispatch + Unit Final Message
        if (examResults.isNotEmpty()) {
            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text(
                        text = "Stored Exam Marks & Parent WhatsApp / SMS Reports (${examResults.size})",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.primary
                    )

                    val latestResult = examResults.first()
                    val unitFinalMessage = buildString {
                        append("UNIT FINAL PARENT REPORT (${studentName}):\n")
                        append("Exam: ${latestResult.examTitle} (${latestResult.subject})\n")
                        append("Score: ${latestResult.scoreObtained}/${latestResult.totalMarks} • Coins Earned: ${latestResult.coinsEarned}\n")
                        append("Mistakes Assigned to Timetable: ${latestResult.mistakesSummary}\n")
                        append("Feedback: ${latestResult.languageFeedback}\n")
                        append("- Counseling : fun education (AR CREATOR)")
                    }

                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Button(
                            onClick = {
                                ExploreEngine.sendReportViaWhatsApp(
                                    context,
                                    wallet.parentMobileNumber,
                                    unitFinalMessage
                                )
                                onMarkExamSentToParent(latestResult)
                            },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = EmeraldRecharge,
                                contentColor = Color(0xFF002114)
                            ),
                            modifier = Modifier
                                .weight(1f)
                                .height(48.dp)
                                .testTag("send_whatsapp_parent_button")
                        ) {
                            Icon(Icons.AutoMirrored.Filled.Send, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("WhatsApp Parent", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                        }

                        OutlinedButton(
                            onClick = {
                                ExploreEngine.sendReportViaSms(
                                    context,
                                    wallet.parentMobileNumber,
                                    unitFinalMessage
                                )
                                onMarkExamSentToParent(latestResult)
                            },
                            modifier = Modifier
                                .weight(1f)
                                .height(48.dp)
                                .testTag("send_sms_parent_button")
                        ) {
                            Icon(Icons.Default.Sms, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("SMS Parent", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                        }
                    }

                    examResults.take(5).forEach { res ->
                        val dateText = SimpleDateFormat("dd MMM, hh:mm a", Locale.getDefault()).format(Date(res.timestamp))
                        Surface(
                            color = MaterialTheme.colorScheme.surfaceVariant,
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(
                                        text = "${res.examTitle} (${res.subject})",
                                        style = MaterialTheme.typography.titleSmall
                                    )
                                    Text(
                                        text = "${res.scoreObtained}/${res.totalMarks}",
                                        fontFamily = RobotoMonoFontFamily,
                                        fontWeight = FontWeight.Bold,
                                        color = AcademicGold
                                    )
                                }
                                Text(
                                    text = "Recorded: $dateText • Coins: +${res.coinsEarned} • Parent Notified: ${if (res.parentReportSent) "Yes" else "Ready"}",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = ElectricCyan
                                )
                                Text(
                                    text = "Mistakes: ${res.mistakesSummary}",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun ExploreSelection3CoinsSocialView(
    wallet: CoinSocialStateEntity,
    activeSocialAppSession: String?,
    socialSessionClosedNotice: String?,
    onUpdateSocialLimitsAndParent: (Int, String, Boolean) -> Unit,
    onRedeemCoinsForSocial: (Int) -> Unit,
    onOpenSocialMediaApp: (String, String?) -> Unit,
    onCloseActiveSocialSession: () -> Unit
) {
    var parentPhoneDraft by remember(wallet.parentMobileNumber) { mutableStateOf(wallet.parentMobileNumber) }
    var limitHoursDraft by remember(wallet.dailySocialLimitHours) { mutableIntStateOf(wallet.dailySocialLimitHours) }
    var parentApprovalDraft by remember(wallet.parentApprovalActive) { mutableStateOf(wallet.parentApprovalActive) }
    var enteredParentPin by remember { mutableStateOf("") }

    val socialApps = listOf("TikTok", "Instagram", "Facebook", "Snapchat")

    Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
        // Coin Vault Summary
        Card(
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
            border = BorderStroke(1.5.dp, AcademicGold.copy(alpha = 0.5f)),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.AccountBalanceWallet, contentDescription = null, tint = AcademicGold)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "3rd Selection: Coin Bank & Social Media App Lock",
                        style = MaterialTheme.typography.titleMedium,
                        color = AcademicGold
                    )
                }

                Text(
                    text = "Rule: 1 Coin = 15 Seconds of Social Media • 1 Game Solved = +1 Coin • Exam Marks = Coins • 15-Coin Milestone Notifies You!",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Surface(
                        color = MaterialTheme.colorScheme.surface,
                        shape = RoundedCornerShape(14.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Text("Available Coins", style = MaterialTheme.typography.labelSmall)
                            Text(
                                text = "${wallet.coinBalance}",
                                style = MaterialTheme.typography.headlineSmall,
                                color = AcademicGold,
                                fontWeight = FontWeight.Bold
                            )
                            Text("Archived: ${wallet.totalCoinsArchived}", style = MaterialTheme.typography.labelSmall)
                        }
                    }

                    Surface(
                        color = MaterialTheme.colorScheme.surface,
                        shape = RoundedCornerShape(14.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Text("Social Timer Active", style = MaterialTheme.typography.labelSmall)
                            Text(
                                text = "${wallet.unlockedSocialSecondsRemaining}s",
                                style = MaterialTheme.typography.headlineSmall,
                                color = if (wallet.unlockedSocialSecondsRemaining > 0) EmeraldRecharge else CoralAlert,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "Limit: ${wallet.dailySocialUsedSeconds / 60}m / ${wallet.dailySocialLimitHours}h",
                                style = MaterialTheme.typography.labelSmall
                            )
                        }
                    }
                }

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Button(
                        onClick = { onRedeemCoinsForSocial(1) },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Text("Use 1 Coin (+15s)")
                    }
                    Button(
                        onClick = { onRedeemCoinsForSocial(4) },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Text("Use 4 Coins (+60s)")
                    }
                }
            }
        }

        // Active Running Social Media Window OR Rapid Auto-Close Notice
        if (activeSocialAppSession != null) {
            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = EmeraldRecharge.copy(alpha = 0.16f)),
                border = BorderStroke(2.dp, EmeraldRecharge),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("active_social_session_card")
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "$activeSocialAppSession UNLOCKED",
                            style = MaterialTheme.typography.titleMedium,
                            color = EmeraldRecharge,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "${wallet.unlockedSocialSecondsRemaining}s LEFT",
                            fontFamily = RobotoMonoFontFamily,
                            fontWeight = FontWeight.Bold,
                            color = AcademicGold
                        )
                    }
                    Text(
                        text = "Countdown running (1 Coin = 15s). App will close rapidly as soon as coins reach 0.",
                        style = MaterialTheme.typography.bodySmall
                    )
                    Button(
                        onClick = onCloseActiveSocialSession,
                        colors = ButtonDefaults.buttonColors(containerColor = CoralAlert),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Close $activeSocialAppSession & Save Remaining Time", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        AnimatedVisibility(visible = socialSessionClosedNotice != null) {
            Surface(
                color = CoralAlert.copy(alpha = 0.18f),
                shape = RoundedCornerShape(14.dp),
                border = BorderStroke(1.dp, CoralAlert),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Lock, contentDescription = null, tint = CoralAlert)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = socialSessionClosedNotice ?: "",
                        style = MaterialTheme.typography.bodyMedium,
                        color = CoralAlert
                    )
                }
            }
        }

        // 4 Social Media App Guard Launchers
        Card(
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Text(
                    text = "Protected Social Media Apps (Closes Rapidly Without Coins)",
                    style = MaterialTheme.typography.titleSmall,
                    color = MaterialTheme.colorScheme.primary
                )

                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    socialApps.forEach { appName ->
                        val isUnlocked = wallet.unlockedSocialSecondsRemaining > 0
                        OutlinedButton(
                            onClick = { onOpenSocialMediaApp(appName, enteredParentPin.trim().ifBlank { null }) },
                            shape = RoundedCornerShape(14.dp),
                            border = BorderStroke(1.dp, if (isUnlocked) EmeraldRecharge else CoralAlert),
                            modifier = Modifier
                                .height(50.dp)
                                .testTag("social_app_gate_${appName.lowercase()}")
                        ) {
                            Icon(
                                imageVector = if (isUnlocked) Icons.Default.LockOpen else Icons.Default.Lock,
                                contentDescription = null,
                                tint = if (isUnlocked) EmeraldRecharge else CoralAlert,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "$appName (${if (isUnlocked) "${wallet.unlockedSocialSecondsRemaining}s" else "Locked"})",
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }
                }
            }
        }

        // Parent Approval Override & 6h / 12h Daily Limit Controls
        Card(
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Security, contentDescription = null, tint = ElectricCyan)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Parent Approval & Daily Limits (6h / 12h)",
                        style = MaterialTheme.typography.titleSmall,
                        color = ElectricCyan
                    )
                }

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    listOf(6, 12).forEach { hrs ->
                        FilterChip(
                            selected = limitHoursDraft == hrs,
                            onClick = { limitHoursDraft = hrs },
                            label = { Text("$hrs Hours Daily Max") }
                        )
                    }
                }

                OutlinedTextField(
                    value = parentPhoneDraft,
                    onValueChange = { parentPhoneDraft = it },
                    label = { Text("Parent WhatsApp / SMS Mobile Number") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Allow Parent Override (Ignore Coin Lock)",
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.weight(1f)
                    )
                    Switch(
                        checked = parentApprovalDraft,
                        onCheckedChange = { parentApprovalDraft = it }
                    )
                }

                if (parentApprovalDraft) {
                    OutlinedTextField(
                        value = enteredParentPin,
                        onValueChange = { enteredParentPin = it },
                        label = { Text("Enter Parent PIN (Default: ${wallet.parentPinCode})") },
                        visualTransformation = PasswordVisualTransformation(),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword),
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                Button(
                    onClick = {
                        onUpdateSocialLimitsAndParent(limitHoursDraft, parentPhoneDraft.trim(), parentApprovalDraft)
                    },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("Save Guardian Limit & Parent Contact")
                }
            }
        }
    }
}

@Composable
private fun ExploreSelection4BoardPatternsView() {
    val guides = remember { ExploreEngine.getBoardPatternGuides() }

    Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
        Card(
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.EmojiEvents, contentDescription = null, tint = AcademicGold)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "4th Selection: Board Patterns & Full-Marks Writing Coach",
                        style = MaterialTheme.typography.titleMedium,
                        color = AcademicGold
                    )
                }
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Exact paper structures and examiner-approved answer formatting to score 100% full marks in every board pattern.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        guides.forEach { guide ->
            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = BorderStroke(1.dp, ElectricCyan.copy(alpha = 0.35f)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        text = guide.boardName,
                        style = MaterialTheme.typography.titleMedium,
                        color = ElectricCyan
                    )
                    Text(
                        text = guide.structureSummary,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    HorizontalDivider()
                    Text(
                        text = "How to Write for Full Marks:",
                        style = MaterialTheme.typography.labelLarge,
                        color = AcademicGold
                    )
                    guide.fullMarksChecklist.forEach { tip ->
                        Text(text = "• $tip", style = MaterialTheme.typography.bodySmall)
                    }
                    Surface(
                        color = EmeraldRecharge.copy(alpha = 0.12f),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = guide.sampleFullMarkAnswer,
                            fontFamily = RobotoMonoFontFamily,
                            fontSize = 11.sp,
                            color = EmeraldRecharge,
                            modifier = Modifier.padding(10.dp)
                        )
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun ExploreSelection5SubjectTimetableView(
    defaultGrade: String,
    onCreateSubjectTimetable: (
        customTitle: String,
        classGrade: String,
        selectedSubjects: List<String>,
        examTarget: String,
        includeSchool: Boolean,
        schoolTiming: String,
        includeTuition: Boolean,
        tuitionTiming: String
    ) -> Unit
) {
    var titleDraft by remember { mutableStateOf("Exam + Subject Priority Plan") }
    var classGrade by remember { mutableStateOf(defaultGrade) }
    var selectedSubjects by remember { mutableStateOf(setOf("Science", "Mathematics", "History")) }
    var examTarget by remember { mutableStateOf("Unit Final & Weekly 20-Mark Mastery") }
    var includeSchool by remember { mutableStateOf(true) }
    var schoolTiming by remember { mutableStateOf("07:30 - 13:00") }
    var includeTuition by remember { mutableStateOf(true) }
    var tuitionTiming by remember { mutableStateOf("15:00 - 17:30") }

    Card(
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(1.5.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.45f)),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.CalendarMonth, contentDescription = null, tint = AcademicGold)
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "5th Selection: Smart Subject Timetable Maker",
                    style = MaterialTheme.typography.titleMedium,
                    color = AcademicGold
                )
            }
            Text(
                text = "Builds a subject timetable strictly from your Exam targets plus optional School and optional Tuition timetables (no extra subjects allowed).",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            OutlinedTextField(
                value = titleDraft,
                onValueChange = { titleDraft = it },
                label = { Text("Timetable Name") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = examTarget,
                onValueChange = { examTarget = it },
                label = { Text("Exam Target Focus (Mandatory)") },
                modifier = Modifier.fillMaxWidth()
            )

            Text(
                text = "Locked Subjects (Cannot add extra unlisted subjects):",
                style = MaterialTheme.typography.labelLarge,
                color = ElectricCyan
            )
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
                        label = { Text(subj) }
                    )
                }
            }

            HorizontalDivider()

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                    Icon(Icons.Default.School, contentDescription = null, tint = ElectricCyan, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Include School Timetable (Optional)", style = MaterialTheme.typography.bodyMedium)
                }
                Switch(checked = includeSchool, onCheckedChange = { includeSchool = it })
            }
            if (includeSchool) {
                OutlinedTextField(
                    value = schoolTiming,
                    onValueChange = { schoolTiming = it },
                    label = { Text("School Timing (e.g., 07:30 - 13:00)") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                    Icon(Icons.Default.AccessTime, contentDescription = null, tint = AcademicGold, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Include Tuition Timetable (Optional)", style = MaterialTheme.typography.bodyMedium)
                }
                Switch(checked = includeTuition, onCheckedChange = { includeTuition = it })
            }
            if (includeTuition) {
                OutlinedTextField(
                    value = tuitionTiming,
                    onValueChange = { tuitionTiming = it },
                    label = { Text("Tuition Timing (e.g., 15:00 - 17:30)") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
            }

            Button(
                onClick = {
                    onCreateSubjectTimetable(
                        titleDraft.trim(),
                        classGrade,
                        selectedSubjects.toList(),
                        examTarget.trim(),
                        includeSchool,
                        schoolTiming.trim(),
                        includeTuition,
                        tuitionTiming.trim()
                    )
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
                    .testTag("build_explore_section5_timetable_button"),
                shape = RoundedCornerShape(14.dp)
            ) {
                Icon(Icons.Default.AutoAwesome, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Build Subject Timetable & View Explanation", fontWeight = FontWeight.Bold)
            }
        }
    }
}
