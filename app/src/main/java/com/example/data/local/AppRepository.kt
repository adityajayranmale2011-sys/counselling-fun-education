package com.example.data.local

import kotlinx.coroutines.flow.Flow
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.util.UUID

sealed class ScheduleRuleResult {
    data class Success(val message: String, val insertedId: Long = 0L) : ScheduleRuleResult()
    data class RuleViolation(val reason: String) : ScheduleRuleResult()
}

class AppRepository(private val dao: AppDao) {

    companion object {
        const val MAX_STORED_SCHEDULES = 4
        const val MAX_ACTIVE_SCHEDULES = 2

        private val json = Json { ignoreUnknownKeys = true; encodeDefaults = true }

        fun encodeBlocks(blocks: List<ScheduleTimeBlock>): String = json.encodeToString(blocks)

        fun decodeBlocks(blocksJson: String): List<ScheduleTimeBlock> {
            return try {
                json.decodeFromString<List<ScheduleTimeBlock>>(blocksJson)
            } catch (e: Exception) {
                emptyList()
            }
        }

        fun buildScheduleExplanation(
            studentName: String,
            classGrade: String,
            subjectsText: String,
            blocks: List<ScheduleTimeBlock>
        ): String {
            return buildString {
                appendLine("Full Timetable Explanation for $studentName ($classGrade):")
                appendLine("1. Early Morning Cognitive Absorption (04:00 – 05:55): Absorption-heavy subjects ($subjectsText) are placed during peak morning alertness for maximum memory encoding.")
                appendLine("2. 5-Minute Transition Buffers: Short recharge breaks prevent cognitive fatigue between consecutive deep study blocks.")
                appendLine("3. School, Commute & Tuition Alignment: Institutional hours, travel buffers, lunch, and coaching slots are balanced so zero academic time is wasted.")
                appendLine("4. Evening Physical Recharge & Active Execution (17:45 – 22:00): Outdoor playtime clears mental stress before Mathematics/Homework execution and strict sleep rest.")
                appendLine("5. Total Scheduled Blocks: ${blocks.size} chronological blocks strictly locked to your selected subjects (no extra unlisted subjects added).")
            }.trim()
        }

        fun defaultReference24HourBlocks(): List<ScheduleTimeBlock> = listOf(
            ScheduleTimeBlock(
                id = UUID.randomUUID().toString(),
                timeRange = "04:00",
                title = "Wake Up & Preparation",
                subjectOrDetail = "Hard Wake-up Baseline • Hydration & Mindfulness",
                durationLabel = "Baseline",
                metaHeader = "ROUTINE",
                dayOrPhaseLabel = "24H Cycle"
            ),
            ScheduleTimeBlock(
                id = UUID.randomUUID().toString(),
                timeRange = "04:00 - 04:05",
                title = "Morning Routine / Light Exercise",
                subjectOrDetail = "Stretching & Oxygenation Activation",
                durationLabel = "5 Mins",
                metaHeader = "RECHARGE",
                dayOrPhaseLabel = "24H Cycle"
            ),
            ScheduleTimeBlock(
                id = UUID.randomUUID().toString(),
                timeRange = "04:05 - 04:50",
                title = "High-Focus Conceptual Learning",
                subjectOrDetail = "Subject: Science / History (Morning Absorption Slot)",
                durationLabel = "45 Mins",
                metaHeader = "STUDY TIME",
                dayOrPhaseLabel = "24H Cycle"
            ),
            ScheduleTimeBlock(
                id = UUID.randomUUID().toString(),
                timeRange = "04:50 - 04:55",
                title = "Transition Buffer",
                subjectOrDetail = "Cognitive Reset & Eye Relaxation",
                durationLabel = "5 Mins",
                metaHeader = "RECHARGE",
                dayOrPhaseLabel = "24H Cycle"
            ),
            ScheduleTimeBlock(
                id = UUID.randomUUID().toString(),
                timeRange = "04:55 - 05:35",
                title = "History & Concept Synthesis",
                subjectOrDetail = "Subject: History (Chronology & Key Events)",
                durationLabel = "40 Mins",
                metaHeader = "STUDY TIME",
                dayOrPhaseLabel = "24H Cycle"
            ),
            ScheduleTimeBlock(
                id = UUID.randomUUID().toString(),
                timeRange = "05:35 - 05:40",
                title = "Transition Buffer",
                subjectOrDetail = "Short Breathwork & Desk Reset",
                durationLabel = "5 Mins",
                metaHeader = "RECHARGE",
                dayOrPhaseLabel = "24H Cycle"
            ),
            ScheduleTimeBlock(
                id = UUID.randomUUID().toString(),
                timeRange = "05:40 - 05:55",
                title = "Revision & Material Review",
                subjectOrDetail = "Active Recall Flashcards & Formula Sheet Scan",
                durationLabel = "15 Mins",
                metaHeader = "STUDY TIME",
                dayOrPhaseLabel = "24H Cycle"
            ),
            ScheduleTimeBlock(
                id = UUID.randomUUID().toString(),
                timeRange = "05:55 - 07:00",
                title = "Preparation and School Commute",
                subjectOrDetail = "Breakfast, Uniform & Morning Transit",
                durationLabel = "65 Mins",
                metaHeader = "ROUTINE",
                dayOrPhaseLabel = "24H Cycle"
            ),
            ScheduleTimeBlock(
                id = UUID.randomUUID().toString(),
                timeRange = "07:00 - 12:00",
                title = "Institutional School Hours",
                subjectOrDetail = "Core Classroom Lectures, Labs & Peer Learning",
                durationLabel = "5 Hours",
                metaHeader = "SCHOOL",
                dayOrPhaseLabel = "24H Cycle"
            ),
            ScheduleTimeBlock(
                id = UUID.randomUUID().toString(),
                timeRange = "12:00 - 12:30",
                title = "Commute Return",
                subjectOrDetail = "Transit Home & Decompression",
                durationLabel = "30 Mins",
                metaHeader = "ROUTINE",
                dayOrPhaseLabel = "24H Cycle"
            ),
            ScheduleTimeBlock(
                id = UUID.randomUUID().toString(),
                timeRange = "12:30 - 13:00",
                title = "Lunch & Cognitive Rest",
                subjectOrDetail = "Balanced Nutrition & Zero-Screen Recovery",
                durationLabel = "30 Mins",
                metaHeader = "LUNCH",
                dayOrPhaseLabel = "24H Cycle"
            ),
            ScheduleTimeBlock(
                id = UUID.randomUUID().toString(),
                timeRange = "13:00 - 14:00",
                title = "Independent Review or Rest Block",
                subjectOrDetail = "Power Nap / Class Notes Consolidation",
                durationLabel = "60 Mins",
                metaHeader = "RECHARGE",
                dayOrPhaseLabel = "24H Cycle"
            ),
            ScheduleTimeBlock(
                id = UUID.randomUUID().toString(),
                timeRange = "14:00 - 17:15",
                title = "Structured Coaching / Tuition Classes",
                subjectOrDetail = "Guided Problem Solving (Science & Mathematics)",
                durationLabel = "3 Hrs 15 Mins",
                metaHeader = "STUDY TIME",
                dayOrPhaseLabel = "24H Cycle"
            ),
            ScheduleTimeBlock(
                id = UUID.randomUUID().toString(),
                timeRange = "17:15 - 17:45",
                title = "Commute Return",
                subjectOrDetail = "Evening Transit & Light Snack",
                durationLabel = "30 Mins",
                metaHeader = "ROUTINE",
                dayOrPhaseLabel = "24H Cycle"
            ),
            ScheduleTimeBlock(
                id = UUID.randomUUID().toString(),
                timeRange = "17:45 - 19:00",
                title = "Recreation / Recharge / Physical Playtime",
                subjectOrDetail = "Outdoor Sports, Dopamine Reset & Physical Activity",
                durationLabel = "75 Mins",
                metaHeader = "RECHARGE",
                dayOrPhaseLabel = "24H Cycle"
            ),
            ScheduleTimeBlock(
                id = UUID.randomUUID().toString(),
                timeRange = "19:00 - 20:00",
                title = "Dinner & Family Interaction",
                subjectOrDetail = "Evening Meal & Mindful Conversation",
                durationLabel = "60 Mins",
                metaHeader = "LUNCH",
                dayOrPhaseLabel = "24H Cycle"
            ),
            ScheduleTimeBlock(
                id = UUID.randomUUID().toString(),
                timeRange = "20:00 - 22:00",
                title = "Active Practice & Execution",
                subjectOrDetail = "Subject: Mathematics & School Homework Assignments",
                durationLabel = "120 Mins",
                metaHeader = "PRACTICE",
                dayOrPhaseLabel = "24H Cycle"
            ),
            ScheduleTimeBlock(
                id = UUID.randomUUID().toString(),
                timeRange = "22:00 / 23:00",
                title = "Sleep Window Initiation (Enforce Hard Rest)",
                subjectOrDetail = "Deep REM Memory Consolidation • Screens Off",
                durationLabel = "6-7 Hours",
                metaHeader = "SLEEPING",
                dayOrPhaseLabel = "24H Cycle"
            )
        )

        fun defaultWeekly7DayBlocks(): List<ScheduleTimeBlock> = listOf(
            ScheduleTimeBlock(
                id = UUID.randomUUID().toString(),
                timeRange = "04:00 - 06:00",
                title = "Mon–Fri Morning Absorption Block",
                subjectOrDetail = "Science & History Deep Study",
                durationLabel = "120 Mins",
                metaHeader = "STUDY TIME",
                dayOrPhaseLabel = "Mon - Fri"
            ),
            ScheduleTimeBlock(
                id = UUID.randomUUID().toString(),
                timeRange = "20:00 - 22:00",
                title = "Mon–Fri Evening Execution Block",
                subjectOrDetail = "Mathematics Drills, Numericals & Written Homework",
                durationLabel = "120 Mins",
                metaHeader = "PRACTICE",
                dayOrPhaseLabel = "Mon - Fri"
            ),
            ScheduleTimeBlock(
                id = UUID.randomUUID().toString(),
                timeRange = "05:00 - 08:30",
                title = "Saturday Full-Length Timed Mock Exam",
                subjectOrDetail = "3-Hour Exam Simulation + Error Log Audit",
                durationLabel = "210 Mins",
                metaHeader = "STUDY TIME",
                dayOrPhaseLabel = "Saturday Custom"
            ),
            ScheduleTimeBlock(
                id = UUID.randomUUID().toString(),
                timeRange = "14:00 - 17:00",
                title = "Saturday Weak-Topic Remediation Lab",
                subjectOrDetail = "Targeted Doubt Clearing & Concept Patching",
                durationLabel = "180 Mins",
                metaHeader = "PRACTICE",
                dayOrPhaseLabel = "Saturday Custom"
            ),
            ScheduleTimeBlock(
                id = UUID.randomUUID().toString(),
                timeRange = "05:30 - 08:00",
                title = "Sunday Weekly 20-Mark Exam & Synthesis",
                subjectOrDetail = "End-of-Week Assessment + Spaced Repetition",
                durationLabel = "150 Mins",
                metaHeader = "STUDY TIME",
                dayOrPhaseLabel = "Sunday Custom"
            ),
            ScheduleTimeBlock(
                id = UUID.randomUUID().toString(),
                timeRange = "16:00 - 19:00",
                title = "Sunday Extended Recharge & Next-Week Planning",
                subjectOrDetail = "Creative Hobbies, Sports & Bag/Material Prep",
                durationLabel = "180 Mins",
                metaHeader = "RECHARGE",
                dayOrPhaseLabel = "Sunday Custom"
            )
        )
    }

    val allSchedules: Flow<List<ScheduleEntity>> = dao.getAllSchedules()
    val allChatHistory: Flow<List<ChatHistoryEntity>> = dao.getAllChatHistory()
    val allExamResults: Flow<List<ExamResultEntity>> = dao.getAllExamResults()
    val coinSocialState: Flow<CoinSocialStateEntity?> = dao.getCoinSocialState()
    val userProfile: Flow<UserProfileEntity?> = dao.getUserProfile()

    suspend fun ensureSeedData() {
        val currentProfile = dao.getUserProfileOnce()
        if (currentProfile == null) {
            dao.upsertUserProfile(UserProfileEntity(name = "govunda suroshe"))
        } else if (currentProfile.name.equals("Aarav Sharma", ignoreCase = true)) {
            dao.upsertUserProfile(currentProfile.copy(name = "govunda suroshe"))
        }

        val currentCoinState = dao.getCoinSocialStateOnce()
        if (currentCoinState == null) {
            dao.upsertCoinSocialState(CoinSocialStateEntity())
        }

        val existingSchedules = dao.getAllSchedulesOnce()
        if (existingSchedules.isEmpty()) {
            val regBlocks = defaultReference24HourBlocks()
            val regTimeline = regBlocks.joinToString("\n") {
                "- ${it.timeRange} | ${it.title} [${it.subjectOrDetail}] (${it.durationLabel})"
            }
            dao.insertSchedule(
                ScheduleEntity(
                    title = "Standard 24H Cognitive-Split Timetable",
                    cycleTypeId = ScheduleCycleType.REGULAR_24H.id,
                    studentName = "govunda suroshe",
                    classGrade = "10th Grade",
                    age = 15,
                    institution = "St. Xavier's Academy",
                    academicGoal = "96%+ Board Distinction & Core Mastery",
                    homeEnvironment = "Independent Study Space",
                    subjectMatrix = "3 | Science, Mathematics, History",
                    weekendAndEventNotes = "Strict 04:00 AM wake-up & 22:00 sleep window with 5-min recharge buffers",
                    isActive = true,
                    alarmsMasterEnabled = true,
                    blocksJson = encodeBlocks(regBlocks),
                    rawAiTimelineText = regTimeline,
                    explanationText = buildScheduleExplanation(
                        studentName = "govunda suroshe",
                        classGrade = "10th Grade",
                        subjectsText = "Science, Mathematics, History",
                        blocks = regBlocks
                    )
                )
            )

            val weeklyBlocks = defaultWeekly7DayBlocks()
            val weeklyTimeline = weeklyBlocks.joinToString("\n") {
                "- [${it.dayOrPhaseLabel}] ${it.timeRange} | ${it.title} — ${it.subjectOrDetail}"
            }
            dao.insertSchedule(
                ScheduleEntity(
                    title = "7-Day Balanced + Weekly 20-Mark Blueprint",
                    cycleTypeId = ScheduleCycleType.WEEKLY_7D.id,
                    studentName = "govunda suroshe",
                    classGrade = "10th Grade",
                    age = 15,
                    institution = "St. Xavier's Academy",
                    academicGoal = "Weekly Spaced Repetition & End-of-Week 20-Mark Exam",
                    homeEnvironment = "Independent Study Space",
                    subjectMatrix = "3 | Science, Mathematics, History",
                    weekendAndEventNotes = "Sat: Mock Exam & Remediation | Sun: Weekly 20-Mark Exam & Recharge",
                    isActive = false,
                    alarmsMasterEnabled = true,
                    blocksJson = encodeBlocks(weeklyBlocks),
                    rawAiTimelineText = weeklyTimeline,
                    explanationText = buildScheduleExplanation(
                        studentName = "govunda suroshe",
                        classGrade = "10th Grade",
                        subjectsText = "Science, Mathematics, History",
                        blocks = weeklyBlocks
                    )
                )
            )
        }
    }

    suspend fun addScheduleEnforcingRules(newSchedule: ScheduleEntity): ScheduleRuleResult {
        val current = dao.getAllSchedulesOnce()
        if (current.size >= MAX_STORED_SCHEDULES) {
            return ScheduleRuleResult.RuleViolation(
                "System Rule Limit Reached: You can store up to $MAX_STORED_SCHEDULES schedules simultaneously (${current.size}/$MAX_STORED_SCHEDULES stored). Please delete an existing schedule before creating a new one."
            )
        }
        val activeCount = current.count { it.isActive }
        val canBeActive = newSchedule.isActive && activeCount < MAX_ACTIVE_SCHEDULES
        val finalSchedule = newSchedule.copy(isActive = canBeActive)
        val insertedId = dao.insertSchedule(finalSchedule)
        val statusMsg = if (newSchedule.isActive && !canBeActive) {
            "Schedule saved in Standby mode because 2 schedules are already Active ($activeCount/$MAX_ACTIVE_SCHEDULES max active)."
        } else {
            "Schedule created and saved (${current.size + 1}/$MAX_STORED_SCHEDULES stored)."
        }
        return ScheduleRuleResult.Success(statusMsg, insertedId)
    }

    suspend fun toggleScheduleActiveStatus(schedule: ScheduleEntity): ScheduleRuleResult {
        val current = dao.getAllSchedulesOnce()
        val targetState = !schedule.isActive
        if (targetState) {
            val activeCount = current.count { it.isActive && it.id != schedule.id }
            if (activeCount >= MAX_ACTIVE_SCHEDULES) {
                return ScheduleRuleResult.RuleViolation(
                    "System Rule Violation: Only $MAX_ACTIVE_SCHEDULES schedules can be toggled to 'Active / On' at any single moment. Turn off one active schedule first."
                )
            }
        }
        dao.updateSchedule(schedule.copy(isActive = targetState))
        return ScheduleRuleResult.Success(
            if (targetState) "'${schedule.title}' is now ACTIVE / ON."
            else "'${schedule.title}' switched to Standby / OFF."
        )
    }

    suspend fun toggleScheduleAlarmsMaster(schedule: ScheduleEntity): ScheduleRuleResult {
        val nextState = !schedule.alarmsMasterEnabled
        dao.updateSchedule(schedule.copy(alarmsMasterEnabled = nextState))
        return ScheduleRuleResult.Success(
            if (nextState) "Master Alarm & Notification Switch ON for '${schedule.title}'."
            else "Master Alarm & Notification Switch OFF for '${schedule.title}'."
        )
    }

    suspend fun updateScheduleBlocks(schedule: ScheduleEntity, updatedBlocks: List<ScheduleTimeBlock>) {
        val rawText = updatedBlocks.joinToString("\n") {
            "- ${it.timeRange} | ${it.title} [${it.subjectOrDetail}] (${it.durationLabel})"
        }
        dao.updateSchedule(
            schedule.copy(
                blocksJson = encodeBlocks(updatedBlocks),
                rawAiTimelineText = rawText
            )
        )
    }

    suspend fun injectMistakeRemediationIntoActiveSchedule(
        subject: String,
        mistakesSummary: String
    ): String {
        val all = dao.getAllSchedulesOnce()
        val target = all.firstOrNull { it.isActive } ?: all.firstOrNull()
            ?: return "No stored timetable found to attach mistake study slot."
        val currentBlocks = decodeBlocks(target.blocksJson).toMutableList()
        val remediationBlock = ScheduleTimeBlock(
            id = UUID.randomUUID().toString(),
            timeRange = "13:15 - 13:55",
            title = "Exam Mistake Remediation ($subject)",
            subjectOrDetail = "Full-Day Fix Slot for Mistakes: $mistakesSummary",
            durationLabel = "40 Mins",
            metaHeader = "PRACTICE",
            dayOrPhaseLabel = "Mistake Fix"
        )
        currentBlocks.add(remediationBlock)
        updateScheduleBlocks(target, currentBlocks)
        return "Added 40-min Mistake Remediation Block to '${target.title}' for $subject."
    }

    suspend fun deleteSchedule(id: Int) = dao.deleteScheduleById(id)

    suspend fun insertChatEntry(entry: ChatHistoryEntity) = dao.insertChatHistory(entry)

    suspend fun deleteChatEntry(id: Int) = dao.deleteChatHistoryById(id)

    suspend fun clearChatHistory() = dao.clearAllChatHistory()

    suspend fun insertExamResult(result: ExamResultEntity): Long = dao.insertExamResult(result)

    suspend fun markExamParentReportSent(result: ExamResultEntity) =
        dao.updateExamResult(result.copy(parentReportSent = true))

    suspend fun deleteExamResult(id: Int) = dao.deleteExamResultById(id)

    suspend fun getCoinStateOnce(): CoinSocialStateEntity =
        dao.getCoinSocialStateOnce() ?: CoinSocialStateEntity().also { dao.upsertCoinSocialState(it) }

    suspend fun updateCoinSocialState(state: CoinSocialStateEntity) = dao.upsertCoinSocialState(state)

    suspend fun updateProfile(profile: UserProfileEntity) = dao.upsertUserProfile(profile)
}
