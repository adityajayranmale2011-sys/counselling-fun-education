package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AlarmOff
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Explore
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.outlined.AutoAwesome
import androidx.compose.material.icons.outlined.Explore
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationRail
import androidx.compose.material3.NavigationRailItem
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ui.MainViewModel
import com.example.ui.PrimaryBottomTab
import com.example.ui.screens.AnimatedBootSplashScreen
import com.example.ui.screens.AuthenticationOnboardingScreen
import com.example.ui.screens.ChatStudyScreen
import com.example.ui.screens.ExploreScreen
import com.example.ui.screens.HomeScheduleScreen
import com.example.ui.screens.ProfileYouScreen
import com.example.ui.theme.AcademicGold
import com.example.ui.theme.AppThemeSkin
import com.example.ui.theme.CoralAlert
import com.example.ui.theme.EmeraldRecharge
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.theme.RobotoMonoFontFamily
import com.example.util.ActiveHardwareAlarmState

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val mainViewModel: MainViewModel = viewModel()
            CounsellingTimeTableApp(viewModel = mainViewModel)
        }
    }
}

@Composable
fun CounsellingTimeTableApp(viewModel: MainViewModel) {
    val userProfile by viewModel.userProfile.collectAsStateWithLifecycle()
    val schedules by viewModel.schedules.collectAsStateWithLifecycle()
    val chatHistory by viewModel.chatHistory.collectAsStateWithLifecycle()
    val examResults by viewModel.examResults.collectAsStateWithLifecycle()
    val coinSocialState by viewModel.coinSocialState.collectAsStateWithLifecycle()
    val selectedTab by viewModel.selectedTab.collectAsStateWithLifecycle()
    val selectedCycleType by viewModel.selectedCycleType.collectAsStateWithLifecycle()
    val statusBanner by viewModel.statusBannerMessage.collectAsStateWithLifecycle()
    val isGeneratingSchedule by viewModel.isGeneratingSchedule.collectAsStateWithLifecycle()
    val pendingScheduleExplanation by viewModel.pendingScheduleExplanation.collectAsStateWithLifecycle()
    val chatAttachments by viewModel.chatAttachments.collectAsStateWithLifecycle()
    val isAnalyzingChat by viewModel.isAnalyzingChat.collectAsStateWithLifecycle()
    val latestChatResult by viewModel.latestChatResult.collectAsStateWithLifecycle()
    val selectedExploreSection by viewModel.selectedExploreSection.collectAsStateWithLifecycle()
    val activeSocialAppSession by viewModel.activeSocialAppSession.collectAsStateWithLifecycle()
    val socialSessionClosedNotice by viewModel.socialSessionClosedNotice.collectAsStateWithLifecycle()
    val activeAlarmState by viewModel.activeAlarmState.collectAsStateWithLifecycle()

    val currentThemeSkin = try {
        AppThemeSkin.valueOf(userProfile?.themeSkin ?: AppThemeSkin.MIDNIGHT_GOLD.name)
    } catch (_: Exception) {
        AppThemeSkin.MIDNIGHT_GOLD
    }

    var isBootSplashCompleted by rememberSaveable { mutableStateOf(false) }

    MyApplicationTheme(themeSkin = currentThemeSkin) {
        when {
            !isBootSplashCompleted -> {
                AnimatedBootSplashScreen(
                    onSplashComplete = { isBootSplashCompleted = true }
                )
            }
            userProfile != null && userProfile?.hasCompletedOnboarding == false -> {
                AuthenticationOnboardingScreen(
                    onGuestContinue = { viewModel.completeGuestOnboarding() },
                    onGoogleSignInSuccess = { email -> viewModel.completeGoogleOAuthOnboarding(email) },
                    onMobileOtpSuccess = { phone -> viewModel.completeMobileOtpOnboarding(phone) }
                )
            }
            else -> {
                if (selectedTab != PrimaryBottomTab.HOME) {
                    BackHandler {
                        viewModel.selectTab(PrimaryBottomTab.HOME)
                    }
                }

                BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
                    val isExpandedScreen = maxWidth >= 720.dp

                    Scaffold(
                        contentWindowInsets = WindowInsets.safeDrawing,
                        bottomBar = {
                            if (!isExpandedScreen) {
                                GlobalFourTabBottomBar(
                                    selectedTab = selectedTab,
                                    onSelectTab = { viewModel.selectTab(it) }
                                )
                            }
                        }
                    ) { innerPadding ->
                        Row(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(innerPadding)
                        ) {
                            if (isExpandedScreen) {
                                GlobalFourTabNavigationRail(
                                    selectedTab = selectedTab,
                                    onSelectTab = { viewModel.selectTab(it) }
                                )
                            }

                            Column(modifier = Modifier.fillMaxSize()) {
                                // Live Multi-Stage Alarm Banner (T-0s Push + T+5s Persistent Hardware Alarm)
                                AnimatedVisibility(
                                    visible = activeAlarmState.isRinging || activeAlarmState.countdownSecondsToStage2 > 0
                                ) {
                                    MultiStageAlarmLiveBanner(
                                        alarmState = activeAlarmState,
                                        onDismissAlarm = { viewModel.dismissActiveHardwareAlarm() }
                                    )
                                }

                                // Status Toast / Feedback Banner
                                AnimatedVisibility(visible = statusBanner != null) {
                                    Surface(
                                        color = MaterialTheme.colorScheme.secondaryContainer,
                                        shape = RoundedCornerShape(12.dp),
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(horizontal = 16.dp, vertical = 6.dp)
                                    ) {
                                        Row(
                                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.SpaceBetween
                                        ) {
                                            Text(
                                                text = statusBanner ?: "",
                                                style = MaterialTheme.typography.bodyMedium,
                                                color = MaterialTheme.colorScheme.onSecondaryContainer,
                                                modifier = Modifier.weight(1f)
                                            )
                                            IconButton(
                                                onClick = { viewModel.postBannerMessage(null) },
                                                modifier = Modifier.size(28.dp)
                                            ) {
                                                Icon(
                                                    imageVector = Icons.Default.Close,
                                                    contentDescription = "Dismiss Message",
                                                    modifier = Modifier.size(16.dp)
                                                )
                                            }
                                        }
                                    }
                                }

                                Box(modifier = Modifier.fillMaxSize()) {
                                    when (selectedTab) {
                                        PrimaryBottomTab.HOME -> {
                                            HomeScheduleScreen(
                                                schedules = schedules,
                                                selectedCycleType = selectedCycleType,
                                                userProfile = userProfile,
                                                isGeneratingSchedule = isGeneratingSchedule,
                                                onSelectCycle = { viewModel.selectCycleType(it) },
                                                onGenerateSchedule = { viewModel.generateAndSaveAiSchedule(it) },
                                                onToggleScheduleActive = { viewModel.toggleScheduleActive(it) },
                                                onToggleScheduleMasterAlarm = { viewModel.toggleScheduleMasterAlarm(it) },
                                                onDeleteSchedule = { viewModel.deleteSchedule(it) },
                                                onSaveBlock = { sched, blk, isNew ->
                                                    viewModel.addOrUpdateBlockInSchedule(sched, blk, isNew)
                                                },
                                                onDeleteBlock = { sched, blkId ->
                                                    viewModel.deleteBlockFromSchedule(sched, blkId)
                                                },
                                                onTriggerTwoStageAlert = { sched, blk ->
                                                    viewModel.triggerTwoStageAlarmForBlock(sched, blk)
                                                },
                                                onLaunchSystemClock = { blk ->
                                                    viewModel.openSystemClockForBlock(blk)
                                                },
                                                onViewExplanation = { sched ->
                                                    viewModel.showScheduleExplanation(sched)
                                                }
                                            )
                                        }
                                        PrimaryBottomTab.CHAT_STUDY -> {
                                            ChatStudyScreen(
                                                attachments = chatAttachments,
                                                isAnalyzing = isAnalyzingChat,
                                                latestResult = latestChatResult,
                                                chatHistory = chatHistory,
                                                onAddUriAttachment = { uri, name, mime ->
                                                    viewModel.addChatAttachmentFromUri(uri, name, mime)
                                                },
                                                onAddCameraBitmap = { bmp ->
                                                    viewModel.addChatAttachmentFromCameraBitmap(bmp)
                                                },
                                                onRemoveAttachment = { id ->
                                                    viewModel.removeChatAttachment(id)
                                                },
                                                onSubmitPrompt = { prompt ->
                                                    viewModel.submitChatStudyPrompt(prompt)
                                                },
                                                onDeleteHistoryItem = { id ->
                                                    viewModel.deleteChatHistoryItem(id)
                                                }
                                            )
                                        }
                                        PrimaryBottomTab.EXPLORE -> {
                                            ExploreScreen(
                                                selectedSection = selectedExploreSection,
                                                userProfile = userProfile,
                                                coinSocialState = coinSocialState,
                                                examResults = examResults,
                                                activeSocialAppSession = activeSocialAppSession,
                                                socialSessionClosedNotice = socialSessionClosedNotice,
                                                onSelectSection = { viewModel.selectExploreSection(it) },
                                                onAwardCoinFromGame = { viewModel.awardCoinFromGameQuestion() },
                                                onSubmitAndEvaluateExam = { title, grade, subj, chap, pat, qs, ans, weekly ->
                                                    viewModel.submitAndEvaluateExam(
                                                        examTitle = title,
                                                        classGrade = grade,
                                                        subject = subj,
                                                        chapterScope = chap,
                                                        patternName = pat,
                                                        questions = qs,
                                                        studentAnswers = ans,
                                                        isWeeklyExam = weekly
                                                    )
                                                },
                                                onMarkExamSentToParent = { viewModel.markExamSentToParent(it) },
                                                onUpdateWeeklyExamChapter = { viewModel.updateWeeklyExamChapterSelection(it) },
                                                onToggleCancelWeeklyExam = { viewModel.toggleCancelWeeklyExamForEmergency() },
                                                onUpdateSocialLimitsAndParent = { hrs, phone, approval ->
                                                    viewModel.updateSocialLimitsAndParentContact(hrs, phone, approval)
                                                },
                                                onRedeemCoinsForSocial = { viewModel.redeemCoinsForSocialTime(it) },
                                                onOpenSocialMediaApp = { app, pin ->
                                                    viewModel.openSocialMediaAppWithCoinGate(app, pin)
                                                },
                                                onCloseActiveSocialSession = { viewModel.closeActiveSocialSessionNow() },
                                                onCreateSubjectTimetable = { title, grade, subjs, exam, incSchool, schoolTime, incTuition, tuitionTime ->
                                                    viewModel.createTimetableFromExploreSelection5(
                                                        customTitle = title,
                                                        classGrade = grade,
                                                        selectedSubjects = subjs,
                                                        examTarget = exam,
                                                        includeSchool = incSchool,
                                                        schoolTiming = schoolTime,
                                                        includeTuition = incTuition,
                                                        tuitionTiming = tuitionTime
                                                    )
                                                }
                                            )
                                        }
                                        PrimaryBottomTab.YOU_PROFILE -> {
                                            ProfileYouScreen(
                                                userProfile = userProfile,
                                                chatHistory = chatHistory,
                                                onUpdateProfile = { updated ->
                                                    viewModel.updateUserProfile(updated)
                                                },
                                                onClearChatHistory = {
                                                    viewModel.clearAllChatHistory()
                                                },
                                                onSwitchAccountOrSignOut = {
                                                    viewModel.signOutToAuthScreen()
                                                }
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }

                    // Cancelable Full Timetable Explanation Dialog (Rule 3)
                    pendingScheduleExplanation?.let { payload ->
                        AlertDialog(
                            onDismissRequest = { viewModel.dismissOrCancelExplanation() },
                            title = {
                                Text(
                                    text = "Timetable Explanation: ${payload.scheduleTitle}",
                                    style = MaterialTheme.typography.titleMedium,
                                    color = AcademicGold
                                )
                            },
                            text = {
                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .verticalScroll(rememberScrollState())
                                ) {
                                    Text(
                                        text = payload.explanationText,
                                        style = MaterialTheme.typography.bodyMedium
                                    )
                                }
                            },
                            confirmButton = {
                                Button(onClick = { viewModel.dismissOrCancelExplanation() }) {
                                    Text("Got It")
                                }
                            },
                            dismissButton = {
                                OutlinedButton(
                                    onClick = { viewModel.dismissOrCancelExplanation() },
                                    modifier = Modifier.testTag("cancel_timetable_explanation_button")
                                ) {
                                    Text("Cancel Explanation")
                                }
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun MultiStageAlarmLiveBanner(
    alarmState: ActiveHardwareAlarmState,
    onDismissAlarm: () -> Unit
) {
    Surface(
        color = if (alarmState.isRinging) CoralAlert else AcademicGold,
        shape = RoundedCornerShape(16.dp),
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .testTag("live_two_stage_alarm_banner")
    ) {
        Row(
            modifier = Modifier.padding(14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(1f)
            ) {
                Icon(
                    imageVector = Icons.Default.NotificationsActive,
                    contentDescription = null,
                    tint = Color(0xFF0F172A),
                    modifier = Modifier.size(28.dp)
                )
                Spacer(modifier = Modifier.width(10.dp))
                Column {
                    Text(
                        text = "[${alarmState.metaHeader}] ${alarmState.stageLabel}",
                        fontFamily = RobotoMonoFontFamily,
                        fontWeight = FontWeight.Bold,
                        fontSize = 11.sp,
                        color = Color(0xFF0F172A)
                    )
                    Text(
                        text = alarmState.blockTitle,
                        style = MaterialTheme.typography.titleSmall,
                        color = Color(0xFF0F172A),
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = alarmState.blockDescription,
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color(0xFF1E293B)
                    )
                }
            }

            Spacer(modifier = Modifier.width(8.dp))

            Button(
                onClick = onDismissAlarm,
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF0F172A),
                    contentColor = Color.White
                ),
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier.testTag("dismiss_hardware_alarm_button")
            ) {
                Icon(
                    imageVector = Icons.Default.AlarmOff,
                    contentDescription = "Dismiss Alarm",
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text("Dismiss")
            }
        }
    }
}

private data class TabNavMeta(
    val tab: PrimaryBottomTab,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector
)

private val fourTabsMeta = listOf(
    TabNavMeta(PrimaryBottomTab.HOME, Icons.Filled.Home, Icons.Outlined.Home),
    TabNavMeta(PrimaryBottomTab.CHAT_STUDY, Icons.Filled.AutoAwesome, Icons.Outlined.AutoAwesome),
    TabNavMeta(PrimaryBottomTab.EXPLORE, Icons.Filled.Explore, Icons.Outlined.Explore),
    TabNavMeta(PrimaryBottomTab.YOU_PROFILE, Icons.Filled.Person, Icons.Outlined.Person)
)

@Composable
private fun GlobalFourTabBottomBar(
    selectedTab: PrimaryBottomTab,
    onSelectTab: (PrimaryBottomTab) -> Unit
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Surface(
            color = MaterialTheme.colorScheme.surfaceVariant,
            tonalElevation = 4.dp,
            modifier = Modifier
                .fillMaxWidth()
                .testTag("persistent_creator_bottom_banner")
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 5.dp),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "CREATED BY ADITYA RANMALE  •  AR CREATOR",
                    fontFamily = com.example.ui.theme.CinzelFontFamily,
                    fontWeight = FontWeight.Bold,
                    fontSize = 11.sp,
                    letterSpacing = 1.8.sp,
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }
        NavigationBar(
            windowInsets = WindowInsets.navigationBars,
            modifier = Modifier.testTag("global_four_tab_bottom_bar")
        ) {
            fourTabsMeta.forEach { item ->
                val selected = selectedTab == item.tab
                NavigationBarItem(
                    selected = selected,
                    onClick = { onSelectTab(item.tab) },
                    icon = {
                        Icon(
                            imageVector = if (selected) item.selectedIcon else item.unselectedIcon,
                            contentDescription = item.tab.label
                        )
                    },
                    label = { Text(item.tab.label) },
                    modifier = Modifier.testTag("bottom_nav_${item.tab.route}")
                )
            }
        }
    }
}

@Composable
private fun GlobalFourTabNavigationRail(
    selectedTab: PrimaryBottomTab,
    onSelectTab: (PrimaryBottomTab) -> Unit
) {
    NavigationRail(
        modifier = Modifier
            .fillMaxHeight()
            .testTag("global_four_tab_nav_rail")
    ) {
        Spacer(modifier = Modifier.height(16.dp))
        fourTabsMeta.forEach { item ->
            val selected = selectedTab == item.tab
            NavigationRailItem(
                selected = selected,
                onClick = { onSelectTab(item.tab) },
                icon = {
                    Icon(
                        imageVector = if (selected) item.selectedIcon else item.unselectedIcon,
                        contentDescription = item.tab.label
                    )
                },
                label = { Text(item.tab.label) },
                modifier = Modifier.padding(vertical = 6.dp)
            )
        }
    }
}
