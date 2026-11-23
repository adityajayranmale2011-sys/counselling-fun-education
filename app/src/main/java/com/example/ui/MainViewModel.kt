package com.example.ui

import android.app.Application
import android.graphics.Bitmap
import android.net.Uri
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.local.AppDatabase
import com.example.data.local.AppRepository
import com.example.data.local.ChatHistoryEntity
import com.example.data.local.CoinSocialStateEntity
import com.example.data.local.ExamResultEntity
import com.example.data.local.FixedCurriculumCatalog
import com.example.data.local.ScheduleCycleType
import com.example.data.local.ScheduleEntity
import com.example.data.local.ScheduleFormInput
import com.example.data.local.ScheduleRuleResult
import com.example.data.local.ScheduleTimeBlock
import com.example.data.local.UserProfileEntity
import com.example.network.DualPerspectiveAiResult
import com.example.network.GeminiService
import com.example.network.MultimodalAttachment
import com.example.util.ActiveHardwareAlarmState
import com.example.util.AlarmAndPdfEngine
import com.example.util.CheckedExamEvaluation
import com.example.util.ExamQuestionItem
import com.example.util.ExploreEngine
import com.example.util.ExploreSelectionSection
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

enum class PrimaryBottomTab(val route: String, val label: String) {
    HOME("tab_home", "Home Page"),
    CHAT_STUDY("tab_chat_study", "ChatStudy AI"),
    EXPLORE("tab_explore", "Explore"),
    YOU_PROFILE("tab_you_profile", "You")
}

data class ScheduleExplanationPayload(
    val scheduleTitle: String,
    val explanationText: String
)

class MainViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: AppRepository

    val schedules: StateFlow<List<ScheduleEntity>>
    val chatHistory: StateFlow<List<ChatHistoryEntity>>
    val examResults: StateFlow<List<ExamResultEntity>>
    val coinSocialState: StateFlow<CoinSocialStateEntity?>
    val userProfile: StateFlow<UserProfileEntity?>
    val activeAlarmState: StateFlow<ActiveHardwareAlarmState> = AlarmAndPdfEngine.alarmState

    private val _selectedTab = MutableStateFlow(PrimaryBottomTab.HOME)
    val selectedTab: StateFlow<PrimaryBottomTab> = _selectedTab.asStateFlow()

    private val _selectedCycleType = MutableStateFlow(ScheduleCycleType.REGULAR_24H)
    val selectedCycleType: StateFlow<ScheduleCycleType> = _selectedCycleType.asStateFlow()

    private val _statusBannerMessage = MutableStateFlow<String?>(null)
    val statusBannerMessage: StateFlow<String?> = _statusBannerMessage.asStateFlow()

    private val _isGeneratingSchedule = MutableStateFlow(false)
    val isGeneratingSchedule: StateFlow<Boolean> = _isGeneratingSchedule.asStateFlow()

    // Rule 3: Show explanation of all timetable after filling form (can be cancelled)
    private val _pendingScheduleExplanation = MutableStateFlow<ScheduleExplanationPayload?>(null)
    val pendingScheduleExplanation: StateFlow<ScheduleExplanationPayload?> = _pendingScheduleExplanation.asStateFlow()

    // ChatStudy AI state
    private val _chatAttachments = MutableStateFlow<List<MultimodalAttachment>>(emptyList())
    val chatAttachments: StateFlow<List<MultimodalAttachment>> = _chatAttachments.asStateFlow()

    private val _isAnalyzingChat = MutableStateFlow(false)
    val isAnalyzingChat: StateFlow<Boolean> = _isAnalyzingChat.asStateFlow()

    private val _latestChatResult = MutableStateFlow<DualPerspectiveAiResult?>(null)
    val latestChatResult: StateFlow<DualPerspectiveAiResult?> = _latestChatResult.asStateFlow()

    // Explore Tab state
    private val _selectedExploreSection = MutableStateFlow(ExploreSelectionSection.GAMES)
    val selectedExploreSection: StateFlow<ExploreSelectionSection> = _selectedExploreSection.asStateFlow()

    private val _activeSocialAppSession = MutableStateFlow<String?>(null) // e.g., "Instagram", "TikTok", etc.
    val activeSocialAppSession: StateFlow<String?> = _activeSocialAppSession.asStateFlow()

    private val _socialSessionClosedNotice = MutableStateFlow<String?>(null)
    val socialSessionClosedNotice: StateFlow<String?> = _socialSessionClosedNotice.asStateFlow()

    private var socialCountdownJob: Job? = null

    init {
        val dao = AppDatabase.getInstance(application).appDao()
        repository = AppRepository(dao)

        schedules = repository.allSchedules.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )
        chatHistory = repository.allChatHistory.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )
        examResults = repository.allExamResults.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )
        coinSocialState = repository.coinSocialState.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = null
        )
        userProfile = repository.userProfile.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = null
        )

        viewModelScope.launch {
            repository.ensureSeedData()
        }
    }

    fun selectTab(tab: PrimaryBottomTab) {
        _selectedTab.value = tab
    }

    fun selectCycleType(cycle: ScheduleCycleType) {
        _selectedCycleType.value = cycle
    }

    fun selectExploreSection(section: ExploreSelectionSection) {
        _selectedExploreSection.value = section
    }

    fun postBannerMessage(message: String?) {
        _statusBannerMessage.value = message
    }

    fun dismissOrCancelExplanation() {
        _pendingScheduleExplanation.value = null
        _statusBannerMessage.value = "Timetable explanation cancelled/closed."
    }

    fun showScheduleExplanation(schedule: ScheduleEntity) {
        val text = schedule.explanationText.ifBlank {
            AppRepository.buildScheduleExplanation(
                studentName = schedule.studentName,
                classGrade = schedule.classGrade,
                subjectsText = schedule.subjectMatrix,
                blocks = AppRepository.decodeBlocks(schedule.blocksJson)
            )
        }
        _pendingScheduleExplanation.value = ScheduleExplanationPayload(
            scheduleTitle = schedule.title,
            explanationText = text
        )
    }

    // --- Authentication & Onboarding ---

    fun completeGuestOnboarding() {
        viewModelScope.launch {
            val current = userProfile.value ?: UserProfileEntity()
            repository.updateProfile(
                current.copy(
                    hasCompletedOnboarding = true,
                    authMode = "GUEST",
                    authAccountLabel = "Guest / Temporary Mode (Local Storage)"
                )
            )
            _statusBannerMessage.value = "Entered Guest / Temporary Mode (All schedules saved locally)."
        }
    }

    fun completeGoogleOAuthOnboarding(emailOrAccountName: String) {
        viewModelScope.launch {
            val current = userProfile.value ?: UserProfileEntity()
            repository.updateProfile(
                current.copy(
                    hasCompletedOnboarding = true,
                    authMode = "GOOGLE_OAUTH",
                    authAccountLabel = "Google Verified: $emailOrAccountName",
                    name = if (current.name.isBlank()) emailOrAccountName.substringBefore("@") else current.name
                )
            )
            _statusBannerMessage.value = "Signed in with Google Account ($emailOrAccountName)."
        }
    }

    fun completeMobileOtpOnboarding(phoneNumber: String) {
        viewModelScope.launch {
            val current = userProfile.value ?: UserProfileEntity()
            repository.updateProfile(
                current.copy(
                    hasCompletedOnboarding = true,
                    authMode = "MOBILE_OTP",
                    authAccountLabel = "OTP Verified Mobile: $phoneNumber",
                    mobileNumber = phoneNumber
                )
            )
            _statusBannerMessage.value = "Permanent Account verified via Mobile OTP ($phoneNumber)."
        }
    }

    fun signOutToAuthScreen() {
        viewModelScope.launch {
            val current = userProfile.value ?: UserProfileEntity()
            repository.updateProfile(current.copy(hasCompletedOnboarding = false))
        }
    }

    // --- TAB 1: Dynamic Schedule Engine ---

    fun generateAndSaveAiSchedule(form: ScheduleFormInput) {
        viewModelScope.launch {
            val currentSchedules = schedules.value
            if (currentSchedules.size >= AppRepository.MAX_STORED_SCHEDULES) {
                _statusBannerMessage.value =
                    "System Rule Limit: Maximum ${AppRepository.MAX_STORED_SCHEDULES} schedules stored. Delete a schedule before generating a new one."
                return@launch
            }

            _isGeneratingSchedule.value = true
            val genResult = GeminiService.generateSchedule(form)
            val cycle = ScheduleCycleType.fromId(form.cycleTypeId)
            val newSchedule = ScheduleEntity(
                title = form.scheduleCustomTitle.ifBlank { "${cycle.shortTitle} Plan for ${form.studentName}" },
                cycleTypeId = cycle.id,
                studentName = form.studentName,
                classGrade = form.classGrade,
                age = form.age,
                institution = form.institution,
                academicGoal = form.coreAcademicGoal,
                homeEnvironment = form.homeEnvironment,
                subjectMatrix = "${form.subjectCount} | ${form.subjectNames}",
                weekendAndEventNotes = "${form.weekendRuleNotes} • ${form.specialEventsNotes}",
                isActive = true,
                alarmsMasterEnabled = true,
                blocksJson = AppRepository.encodeBlocks(genResult.blocks),
                rawAiTimelineText = genResult.rawChronologicalText,
                explanationText = genResult.explanationText
            )
            val ruleOutcome = repository.addScheduleEnforcingRules(newSchedule)
            _isGeneratingSchedule.value = false
            _selectedCycleType.value = cycle
            when (ruleOutcome) {
                is ScheduleRuleResult.Success -> {
                    val modeTag = if (genResult.usedLiveGeminiApi) "via Live Gemini AI" else "via Cognitive-Split Engine"
                    _statusBannerMessage.value = "${ruleOutcome.message} ($modeTag)"
                    // Show full explanation immediately after form creation (user can cancel/dismiss)
                    _pendingScheduleExplanation.value = ScheduleExplanationPayload(
                        scheduleTitle = newSchedule.title,
                        explanationText = genResult.explanationText
                    )
                }
                is ScheduleRuleResult.RuleViolation -> {
                    _statusBannerMessage.value = ruleOutcome.reason
                }
            }
        }
    }

    fun toggleScheduleActive(schedule: ScheduleEntity) {
        viewModelScope.launch {
            when (val res = repository.toggleScheduleActiveStatus(schedule)) {
                is ScheduleRuleResult.Success -> _statusBannerMessage.value = res.message
                is ScheduleRuleResult.RuleViolation -> _statusBannerMessage.value = res.reason
            }
        }
    }

    fun toggleScheduleMasterAlarm(schedule: ScheduleEntity) {
        viewModelScope.launch {
            when (val res = repository.toggleScheduleAlarmsMaster(schedule)) {
                is ScheduleRuleResult.Success -> _statusBannerMessage.value = res.message
                is ScheduleRuleResult.RuleViolation -> _statusBannerMessage.value = res.reason
            }
        }
    }

    fun deleteSchedule(scheduleId: Int) {
        viewModelScope.launch {
            repository.deleteSchedule(scheduleId)
            _statusBannerMessage.value = "Schedule deleted. Storage slot freed."
        }
    }

    fun addOrUpdateBlockInSchedule(
        schedule: ScheduleEntity,
        block: ScheduleTimeBlock,
        isNew: Boolean
    ) {
        viewModelScope.launch {
            val currentBlocks = AppRepository.decodeBlocks(schedule.blocksJson).toMutableList()
            if (isNew) {
                currentBlocks.add(block)
            } else {
                val idx = currentBlocks.indexOfFirst { it.id == block.id }
                if (idx >= 0) currentBlocks[idx] = block else currentBlocks.add(block)
            }
            repository.updateScheduleBlocks(schedule, currentBlocks)
            _statusBannerMessage.value = "Updated time block '${block.title}' in '${schedule.title}'."
        }
    }

    fun deleteBlockFromSchedule(schedule: ScheduleEntity, blockId: String) {
        viewModelScope.launch {
            val updated = AppRepository.decodeBlocks(schedule.blocksJson).filterNot { it.id == blockId }
            repository.updateScheduleBlocks(schedule, updated)
            _statusBannerMessage.value = "Removed time block from '${schedule.title}'."
        }
    }

    // --- Section 3: Multi-Stage Alarm & Notification Engine ---

    fun triggerTwoStageAlarmForBlock(schedule: ScheduleEntity, block: ScheduleTimeBlock) {
        if (!schedule.alarmsMasterEnabled) {
            _statusBannerMessage.value =
                "Master Alarm Switch is OFF for '${schedule.title}'. Turn it ON to trigger alerts."
            return
        }
        val profile = userProfile.value ?: UserProfileEntity()
        if (!profile.permPushNotifications && !profile.permSystemAlarms) {
            _statusBannerMessage.value =
                "Push Notifications and System Alarms are disabled in Upper Settings Gear Menu."
            return
        }
        AlarmAndPdfEngine.triggerTwoStageAlertSequence(
            context = getApplication(),
            block = block,
            audioSource = profile.alarmAudioSource,
            customAudioUri = profile.customAudioUri,
            pushEnabled = profile.permPushNotifications,
            hardwareAlarmEnabled = profile.permSystemAlarms,
            speakersEnabled = profile.permHardwareSpeakers && profile.permAudio
        )
    }

    fun openSystemClockForBlock(block: ScheduleTimeBlock) {
        AlarmAndPdfEngine.launchNativeSystemAlarmClock(getApplication(), block)
    }

    fun dismissActiveHardwareAlarm() {
        AlarmAndPdfEngine.stopPersistentHardwareAlarm(getApplication())
    }

    // --- TAB 2: ChatStudy AI (Small, Fully Correct Dual-Perspective Answers) ---

    fun addChatAttachmentFromUri(uri: Uri, name: String, forceMime: String? = null) {
        viewModelScope.launch {
            val attachment = GeminiService.uriToAttachment(
                context = getApplication(),
                uri = uri,
                defaultName = name,
                forceMime = forceMime
            )
            if (attachment != null) {
                _chatAttachments.value = _chatAttachments.value + attachment
                _statusBannerMessage.value = "Attached '${attachment.displayName}' for Multimodal AI analysis."
            } else {
                _statusBannerMessage.value = "Could not read selected file."
            }
        }
    }

    fun addChatAttachmentFromCameraBitmap(bitmap: Bitmap) {
        val base64 = GeminiService.bitmapToBase64(bitmap)
        val att = MultimodalAttachment(
            displayName = "Camera_Capture_${System.currentTimeMillis() % 10000}.jpg",
            mimeType = "image/jpeg",
            base64Data = base64
        )
        _chatAttachments.value = _chatAttachments.value + att
        _statusBannerMessage.value = "Captured photo added to ChatStudy multimodal queue."
    }

    fun removeChatAttachment(id: String) {
        _chatAttachments.value = _chatAttachments.value.filterNot { it.id == id }
    }

    fun submitChatStudyPrompt(prompt: String) {
        val currentAttachments = _chatAttachments.value
        if (prompt.isBlank() && currentAttachments.isEmpty()) {
            _statusBannerMessage.value = "Enter a question or upload images/PDFs to analyze."
            return
        }
        viewModelScope.launch {
            _isAnalyzingChat.value = true
            val result = GeminiService.analyzeMultimodalStudyPrompt(
                prompt = prompt.ifBlank { "Extract text, summarize, and solve briefly." },
                attachments = currentAttachments
            )
            _latestChatResult.value = result

            val attachmentNames = if (currentAttachments.isEmpty()) {
                "Text Prompt Only"
            } else {
                currentAttachments.joinToString(", ") { it.displayName }
            }
            repository.insertChatEntry(
                ChatHistoryEntity(
                    promptText = prompt.ifBlank { "Multimodal Image/PDF (${currentAttachments.size} files)" },
                    attachmentSummary = attachmentNames,
                    attachmentCount = currentAttachments.size,
                    extractedOcrAndSummary = result.extractedOcrAndSummary,
                    simplifiedAnswer = result.simplifiedConceptualAnswer,
                    academicAnswer = result.detailedAcademicAnswer
                )
            )
            _isAnalyzingChat.value = false
            _chatAttachments.value = emptyList()
        }
    }

    fun deleteChatHistoryItem(id: Int) {
        viewModelScope.launch {
            repository.deleteChatEntry(id)
        }
    }

    fun clearAllChatHistory() {
        viewModelScope.launch {
            repository.clearChatHistory()
            _latestChatResult.value = null
            _statusBannerMessage.value = "Cleared all ChatStudy AI history."
        }
    }

    // --- TAB 3: EXPLORE (12 Games, Exams + Mistake Remediation, Coins & Social Lock, Board Marks, Subject Timetable) ---

    fun awardCoinFromGameQuestion() {
        viewModelScope.launch {
            val current = repository.getCoinStateOnce()
            val nextBalance = current.coinBalance + 1
            val nextTotal = current.totalCoinsArchived + 1
            val milestoneBlock = nextTotal / 15

            val shouldNotify15 = milestoneBlock > current.last15CoinNotifiedMilestone
            repository.updateCoinSocialState(
                current.copy(
                    coinBalance = nextBalance,
                    totalCoinsArchived = nextTotal,
                    last15CoinNotifiedMilestone = if (shouldNotify15) milestoneBlock else current.last15CoinNotifiedMilestone
                )
            )
            if (shouldNotify15) {
                _statusBannerMessage.value =
                    "15-Coin Milestone Reached! Total Archived: $nextTotal Coins (${nextBalance * 15}s Social Media Time). You can unlock Social Media apps now!"
            }
        }
    }

    fun submitAndEvaluateExam(
        examTitle: String,
        classGrade: String,
        subject: String,
        chapterScope: String,
        patternName: String,
        questions: List<ExamQuestionItem>,
        studentAnswers: Map<String, String>,
        isWeeklyExam: Boolean
    ): CheckedExamEvaluation {
        val evaluation = ExploreEngine.evaluateExamPaper(questions, studentAnswers)
        viewModelScope.launch {
            val mistakesStr = if (evaluation.mistakesList.isEmpty()) {
                "Zero mistakes! Full mastery achieved."
            } else {
                evaluation.mistakesList.joinToString(", ")
            }

            repository.insertExamResult(
                ExamResultEntity(
                    examTitle = examTitle,
                    classGrade = classGrade,
                    subject = subject,
                    chapterScope = chapterScope,
                    patternName = patternName,
                    scoreObtained = evaluation.totalScore,
                    totalMarks = evaluation.maxMarks,
                    coinsEarned = evaluation.coinsEarned,
                    mistakesSummary = mistakesStr,
                    languageFeedback = evaluation.languageSuggestions,
                    isWeeklyMandatoryExam = isWeeklyExam
                )
            )

            // Award exam marks as coins!
            val wallet = repository.getCoinStateOnce()
            val newBal = wallet.coinBalance + evaluation.coinsEarned
            val newTot = wallet.totalCoinsArchived + evaluation.coinsEarned
            val milestone = newTot / 15
            repository.updateCoinSocialState(
                wallet.copy(
                    coinBalance = newBal,
                    totalCoinsArchived = newTot,
                    weeklyExamCompleted = if (isWeeklyExam) true else wallet.weeklyExamCompleted,
                    last15CoinNotifiedMilestone = maxOf(wallet.last15CoinNotifiedMilestone, milestone)
                )
            )

            // Mistake of user -> give full-day timetable study slot for this mistake
            val remediationNotice = if (evaluation.mistakesList.isNotEmpty()) {
                repository.injectMistakeRemediationIntoActiveSchedule(subject, mistakesStr)
            } else {
                "No mistakes found — no extra remediation block needed."
            }

            _statusBannerMessage.value =
                "Exam Checked: ${evaluation.totalScore}/${evaluation.maxMarks} (+${evaluation.coinsEarned} Coins)! $remediationNotice"
        }
        return evaluation
    }

    fun markExamSentToParent(result: ExamResultEntity) {
        viewModelScope.launch {
            repository.markExamParentReportSent(result)
            _statusBannerMessage.value = "Parent report prepared for '${result.examTitle}'."
        }
    }

    fun updateWeeklyExamChapterSelection(chapters: String) {
        viewModelScope.launch {
            val state = repository.getCoinStateOnce()
            repository.updateCoinSocialState(
                state.copy(
                    weeklyExamSelectedChapters = chapters,
                    weeklyExamCancelled = false
                )
            )
            _statusBannerMessage.value = "Weekly 20-Mark Exam chapters saved: $chapters"
        }
    }

    fun toggleCancelWeeklyExamForEmergency() {
        viewModelScope.launch {
            val state = repository.getCoinStateOnce()
            val nextCancelled = !state.weeklyExamCancelled
            // Rotate app-selected subject for next week if reset
            val nextSubject = if (!nextCancelled) {
                val pool = FixedCurriculumCatalog.coreExploreTrio
                val idx = (pool.indexOf(state.weeklyExamSubject) + 1) % pool.size
                pool[idx]
            } else state.weeklyExamSubject

            repository.updateCoinSocialState(
                state.copy(
                    weeklyExamCancelled = nextCancelled,
                    weeklyExamSubject = nextSubject
                )
            )
            _statusBannerMessage.value = if (nextCancelled) {
                "Weekly 20-Mark Exam cancelled due to emergency / external exam conflict."
            } else {
                "Weekly 20-Mark Exam active! App assigned subject: $nextSubject (24h chapter-select notice active)."
            }
        }
    }

    // --- Social Media Coin Vault & Rapid Auto-Close Gate ---

    fun updateSocialLimitsAndParentContact(
        limitHours: Int,
        parentPhone: String,
        parentApproval: Boolean
    ) {
        viewModelScope.launch {
            val state = repository.getCoinStateOnce()
            repository.updateCoinSocialState(
                state.copy(
                    dailySocialLimitHours = limitHours,
                    parentMobileNumber = parentPhone,
                    parentApprovalActive = parentApproval
                )
            )
            _statusBannerMessage.value =
                "Social Media Guardian updated: ${limitHours}h daily limit • Parent Approval=${if (parentApproval) "ON" else "OFF"}."
        }
    }

    fun redeemCoinsForSocialTime(coinsToSpend: Int) {
        viewModelScope.launch {
            val state = repository.getCoinStateOnce()
            if (coinsToSpend <= 0 || state.coinBalance < coinsToSpend) {
                _statusBannerMessage.value = "Not enough Coins! Solve games or exams to earn coins (1 Coin = 15s)."
                return@launch
            }
            val addedSeconds = coinsToSpend * 15
            repository.updateCoinSocialState(
                state.copy(
                    coinBalance = state.coinBalance - coinsToSpend,
                    unlockedSocialSecondsRemaining = state.unlockedSocialSecondsRemaining + addedSeconds
                )
            )
            _statusBannerMessage.value =
                "Converted $coinsToSpend Coin(s) → +${addedSeconds}s Social Media Time!"
        }
    }

    fun openSocialMediaAppWithCoinGate(appName: String, enteredParentPin: String? = null) {
        viewModelScope.launch {
            val state = repository.getCoinStateOnce()
            val limitSeconds = state.dailySocialLimitHours * 3600

            if (state.dailySocialUsedSeconds >= limitSeconds && !state.parentApprovalActive) {
                _statusBannerMessage.value =
                    "Daily ${state.dailySocialLimitHours}-Hour Social Media Limit Reached! Parent approval required."
                return@launch
            }

            if (state.unlockedSocialSecondsRemaining <= 0) {
                // Check if parent override PIN provided
                if (state.parentApprovalActive && enteredParentPin == state.parentPinCode) {
                    repository.updateCoinSocialState(
                        state.copy(unlockedSocialSecondsRemaining = 60) // 1 min parent-approved grace
                    )
                    startSocialCountdownSession(appName)
                } else {
                    _socialSessionClosedNotice.value =
                        "$appName LOCKED & CLOSED: 0 Coins active! Solve 1 game question = 1 Coin (15s), or get Parent Approval."
                    _statusBannerMessage.value =
                        "$appName cannot open without Coins (1 Coin = 15 seconds)."
                }
                return@launch
            }

            startSocialCountdownSession(appName)
        }
    }

    private fun startSocialCountdownSession(appName: String) {
        socialCountdownJob?.cancel()
        _socialSessionClosedNotice.value = null
        _activeSocialAppSession.value = appName

        socialCountdownJob = viewModelScope.launch {
            while (true) {
                delay(1000L)
                val current = repository.getCoinStateOnce()
                if (current.unlockedSocialSecondsRemaining <= 1) {
                    repository.updateCoinSocialState(
                        current.copy(
                            unlockedSocialSecondsRemaining = 0,
                            dailySocialUsedSeconds = current.dailySocialUsedSeconds + 1
                        )
                    )
                    _activeSocialAppSession.value = null
                    _socialSessionClosedNotice.value =
                        "$appName CLOSED RAPIDLY: Coin timer expired (0s remaining)! Earn more Coins in Explore Games to reopen."
                    _statusBannerMessage.value = "$appName closed automatically — Coins depleted!"
                    break
                } else {
                    repository.updateCoinSocialState(
                        current.copy(
                            unlockedSocialSecondsRemaining = current.unlockedSocialSecondsRemaining - 1,
                            dailySocialUsedSeconds = current.dailySocialUsedSeconds + 1
                        )
                    )
                }
            }
        }
    }

    fun closeActiveSocialSessionNow() {
        socialCountdownJob?.cancel()
        _activeSocialAppSession.value = null
        _statusBannerMessage.value = "Social Media session closed manually. Remaining seconds saved!"
    }

    // --- Selection 5: Build Subject Timetable from Exam + Optional School + Optional Tuition ---

    fun createTimetableFromExploreSelection5(
        customTitle: String,
        classGrade: String,
        selectedSubjects: List<String>,
        examTarget: String,
        includeSchool: Boolean,
        schoolTiming: String,
        includeTuition: Boolean,
        tuitionTiming: String
    ) {
        viewModelScope.launch {
            val studentName = userProfile.value?.name ?: "govunda suroshe"
            val lockedSubjects = selectedSubjects.ifEmpty { listOf("Science", "Mathematics", "History") }
            val blocks = ExploreEngine.buildSmartSubjectTimetableFromInputs(
                studentName = studentName,
                classGrade = classGrade,
                selectedSubjects = lockedSubjects,
                examTargetSummary = examTarget,
                includeSchool = includeSchool,
                schoolTiming = schoolTiming,
                includeTuition = includeTuition,
                tuitionTiming = tuitionTiming
            )
            val rawText = blocks.joinToString("\n") {
                "- [${it.dayOrPhaseLabel}] ${it.timeRange} | ${it.title} [${it.subjectOrDetail}] (${it.durationLabel})"
            }
            val explanation = AppRepository.buildScheduleExplanation(
                studentName = studentName,
                classGrade = classGrade,
                subjectsText = lockedSubjects.joinToString(", "),
                blocks = blocks
            )
            val newSchedule = ScheduleEntity(
                title = customTitle.ifBlank { "Exam + Subject Smart Timetable" },
                cycleTypeId = ScheduleCycleType.REGULAR_24H.id,
                studentName = studentName,
                classGrade = classGrade,
                age = userProfile.value?.age?.toIntOrNull() ?: 15,
                institution = userProfile.value?.schoolCollege ?: "St. Xavier's Academy",
                academicGoal = "Exam Focus: $examTarget",
                homeEnvironment = "Exam-Aligned Schedule",
                subjectMatrix = "${lockedSubjects.size} | ${lockedSubjects.joinToString(", ")}",
                weekendAndEventNotes = "School=${if (includeSchool) schoolTiming else "Skipped (Optional)"} • Tuition=${if (includeTuition) tuitionTiming else "Skipped (Optional)"}",
                isActive = true,
                alarmsMasterEnabled = true,
                blocksJson = AppRepository.encodeBlocks(blocks),
                rawAiTimelineText = rawText,
                explanationText = explanation
            )

            when (val res = repository.addScheduleEnforcingRules(newSchedule)) {
                is ScheduleRuleResult.Success -> {
                    _statusBannerMessage.value = res.message
                    _pendingScheduleExplanation.value = ScheduleExplanationPayload(
                        scheduleTitle = newSchedule.title,
                        explanationText = explanation
                    )
                    _selectedTab.value = PrimaryBottomTab.HOME
                }
                is ScheduleRuleResult.RuleViolation -> {
                    _statusBannerMessage.value = res.reason
                }
            }
        }
    }

    // --- TAB 4: "You" Profile & Upper Settings Gear Menu ---

    fun updateUserProfile(updated: UserProfileEntity) {
        viewModelScope.launch {
            repository.updateProfile(updated)
            _statusBannerMessage.value = "Profile & System Architecture settings saved."
        }
    }
}
