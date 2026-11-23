package com.example.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.serialization.Serializable

enum class ScheduleCycleType(
    val id: String,
    val shortTitle: String,
    val fullTitle: String,
    val badge: String,
    val description: String,
    val isComingSoon: Boolean = false
) {
    REGULAR_24H(
        id = "REGULAR_24H",
        shortTitle = "I] 24-Hour",
        fullTitle = "Regular Time Table (24-Hour Cycle)",
        badge = "24H CONTINUOUS",
        description = "Day-by-day continuous rendering with cognitive-split morning absorption & evening execution."
    ),
    WEEKLY_7D(
        id = "WEEKLY_7D",
        shortTitle = "II] Weekly (7D)",
        fullTitle = "Weekly Time Table (7-Day Cycle)",
        badge = "7-DAY + WEEKEND RULES",
        description = "Structural patterns for the whole week with deep customization rules for Saturdays & Sundays."
    ),
    FORTNIGHTLY_15D(
        id = "FORTNIGHTLY_15D",
        shortTitle = "III] 15-Day",
        fullTitle = "15-Day Time Table (Fortnightly Cycle)",
        badge = "15-DAY SPRINT",
        description = "Long-range 15-day schedules tracking core school routines, weekend shifts, and Special Events."
    ),
    MONTHLY_30D(
        id = "MONTHLY_30D",
        shortTitle = "IV] Monthly (30D)",
        fullTitle = "Monthly Time Table (30-Day Cycle)",
        badge = "30-DAY MACRO",
        description = "Macro-level schedules highlighting monthly performance metrics, weekends, and major milestones."
    ),
    YEARLY_COMING_SOON(
        id = "YEARLY_COMING_SOON",
        shortTitle = "V] Yearly",
        fullTitle = "Yearly Time Table (365-Day Academic Roadmap)",
        badge = "COMING SOON",
        description = "Full-year academic Periodization Engine across terms, board exams, and seasonal breaks.",
        isComingSoon = true
    );

    companion object {
        fun fromId(id: String): ScheduleCycleType =
            entries.find { it.id == id } ?: REGULAR_24H
    }
}

object FixedCurriculumCatalog {
    val allowedSubjects = listOf(
        "Science",
        "Mathematics",
        "History",
        "English",
        "Geography",
        "Hindi / Marathi"
    )

    val coreExploreTrio = listOf("Science", "Mathematics", "History")

    fun chaptersFor(classGrade: String, subject: String): List<String> {
        val gradeNum = classGrade.filter { it.isDigit() }.toIntOrNull() ?: 10
        return when (subject) {
            "Science" -> when {
                gradeNum <= 4 -> listOf("Living & Non-Living Things", "Plants & Parts", "Human Body & Sense Organs", "Water, Air & Weather")
                gradeNum <= 8 -> listOf("Force, Friction & Pressure", "Light & Reflection", "Cell Structure & Microorganisms", "Acids, Bases & Salts")
                else -> listOf("Laws of Motion & Gravitation", "Chemical Reactions & Equations", "Periodic Classification & Bonding", "Life Processes & Heredity")
            }
            "Mathematics" -> when {
                gradeNum <= 4 -> listOf("Place Value & Addition", "Multiplication & Division", "Fractions & Money", "Basic Geometry & Shapes")
                gradeNum <= 8 -> listOf("Linear Equations", "Exponents & Powers", "Triangles & Congruence", "Mensuration & Area")
                else -> listOf("Quadratic Equations", "Trigonometry & Identities", "Coordinate Geometry", "Arithmetic Progressions & Probability")
            }
            "History" -> when {
                gradeNum <= 4 -> listOf("Our Early Ancestors", "Great Indian Rulers", "Festivals & Heritage", "Freedom Fighters")
                gradeNum <= 8 -> listOf("Harappan Civilization", "Mughal Empire & Administration", "Revolt of 1857", "Indian National Movement")
                else -> listOf("Rise of Nationalism in Europe", "Nationalism in India", "Industrialization & Print Culture", "Post-Independence India")
            }
            "English" -> listOf("Grammar & Tenses", "Reading Comprehension", "Formal Letter & Essay Writing", "Prose & Poetry Analysis")
            "Geography" -> listOf("Resources & Development", "Climate & Monsoon", "Agriculture & Minerals", "Maps & Drainage Systems")
            else -> listOf("Vyakaran (Grammar)", "Prose Comprehension", "Creative Composition", "Poetry & Idioms")
        }
    }
}

@Serializable
data class ScheduleTimeBlock(
    val id: String,
    val timeRange: String,
    val title: String,
    val subjectOrDetail: String,
    val durationLabel: String,
    val metaHeader: String, // "STUDY TIME", "RECHARGE", "SLEEPING", "LUNCH", "SCHOOL", "PRACTICE", "ROUTINE"
    val dayOrPhaseLabel: String = "Daily Core"
)

@Serializable
data class ScheduleFormInput(
    val cycleTypeId: String = ScheduleCycleType.REGULAR_24H.id,
    val scheduleCustomTitle: String = "High-Focus Academic Blueprint",
    val studentName: String = "govunda suroshe",
    val classGrade: String = "10th Grade",
    val age: Int = 15,
    val institution: String = "St. Xavier's Academy",
    val coreAcademicGoal: String = "Score 96%+ in Board Examinations & Master Core Concepts",
    val homeEnvironment: String = "Independent Study Space", // or "Strict Structured Household"
    val wakeUpTime: String = "04:00",
    val schoolHours: String = "07:00 to 12:00",
    val commuteDuration: String = "30 Mins each way",
    val lunchWindow: String = "12:30 to 13:00",
    val tuitionClasses: String = "14:00 to 17:15",
    val extracurriculars: String = "17:45 to 19:00 (Outdoor Sports & Fitness)",
    val dinnerWindow: String = "19:00 to 20:00",
    val sleepWindow: String = "22:00",
    val playtimeBreaks: String = "17:45 to 19:00",
    val subjectCount: Int = 3,
    val subjectNames: String = "Science, Mathematics, History",
    val weekendRuleNotes: String = "Sat: Mock Test + Weak Topic Review | Sun: Creative Revision + Early Rest",
    val specialEventsNotes: String = "Day 7: Unit Test | Day 14: Project Submission | Day 28: Monthly Mock Assessment"
) {
    fun toFormattedMarkdownPrompt(): String {
        val cycle = ScheduleCycleType.fromId(cycleTypeId)
        return """
### AI SCHEDULE GENERATION INPUT FORM
- **Target Cycle Mode:** ${cycle.fullTitle}
- **Student Name:** $studentName
- **Class / Grade:** $classGrade
- **Age:** $age
- **Institution:** $institution
- **Core Academic Goal:** $coreAcademicGoal
- **Home Environment:** $homeEnvironment
- **Key Baseline Time Boundaries:**
  - Wake-up Time: $wakeUpTime
  - School/College Hours: $schoolHours
  - Commute / Transportation Duration: $commuteDuration
  - Lunch Window: $lunchWindow
  - Tuition / Coaching Classes: $tuitionClasses
  - Alternative / Extracurricular Activities: $extracurriculars
  - Dinner Window: $dinnerWindow
  - Nightly Sleep Window: $sleepWindow
  - Playtime / Recreational Breaks: $playtimeBreaks
- **Strict Subject Matrix (No extra subjects allowed):** $subjectCount | $subjectNames
- **Weekend / Special Event Rules:** $weekendRuleNotes | $specialEventsNotes
        """.trimIndent()
    }
}

@Entity(tableName = "schedules")
data class ScheduleEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val title: String,
    val cycleTypeId: String,
    val studentName: String,
    val classGrade: String,
    val age: Int,
    val institution: String,
    val academicGoal: String,
    val homeEnvironment: String,
    val subjectMatrix: String,
    val weekendAndEventNotes: String,
    val isActive: Boolean, // System rule: Max 2 active at any single moment
    val alarmsMasterEnabled: Boolean, // Independent global toggle switch per schedule profile
    val blocksJson: String,
    val rawAiTimelineText: String,
    val explanationText: String = "",
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "chat_history")
data class ChatHistoryEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val promptText: String,
    val attachmentSummary: String,
    val attachmentCount: Int,
    val extractedOcrAndSummary: String,
    val simplifiedAnswer: String,
    val academicAnswer: String,
    val timestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "exam_results")
data class ExamResultEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val examTitle: String,
    val classGrade: String,
    val subject: String,
    val chapterScope: String,
    val patternName: String,
    val scoreObtained: Int,
    val totalMarks: Int,
    val coinsEarned: Int,
    val mistakesSummary: String,
    val languageFeedback: String,
    val isWeeklyMandatoryExam: Boolean = false,
    val parentReportSent: Boolean = false,
    val timestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "coin_social_state")
data class CoinSocialStateEntity(
    @PrimaryKey val id: Int = 1,
    val coinBalance: Int = 5,
    val totalCoinsArchived: Int = 5,
    val unlockedSocialSecondsRemaining: Int = 0, // 1 coin = 15 seconds
    val dailySocialLimitHours: Int = 6, // 6 hours or 12 hours
    val dailySocialUsedSeconds: Int = 0,
    val parentApprovalActive: Boolean = false,
    val parentMobileNumber: String = "+91 98765 43210",
    val parentPinCode: String = "1234",
    val weeklyExamSubject: String = "Mathematics",
    val weeklyExamSelectedChapters: String = "Quadratic Equations, Trigonometry & Identities",
    val weeklyExamDueEpochMs: Long = System.currentTimeMillis() + 24L * 60L * 60L * 1000L,
    val weeklyExamCancelled: Boolean = false,
    val weeklyExamCompleted: Boolean = false,
    val last15CoinNotifiedMilestone: Int = 0
)

@Entity(tableName = "user_profile")
data class UserProfileEntity(
    @PrimaryKey val id: Int = 1,
    val hasCompletedOnboarding: Boolean = false,
    val authMode: String = "GUEST", // "GUEST", "GOOGLE_OAUTH", "MOBILE_OTP"
    val authAccountLabel: String = "Guest / Temporary Mode (Local Storage)",
    val profilePictureUri: String = "",
    val name: String = "govunda suroshe",
    val classGrade: String = "10th Grade",
    val mobileNumber: String = "+91 98765 43210",
    val schoolCollege: String = "St. Xavier's High School & Junior College",
    val academicGoal: String = "96%+ Board Distinction & Olympiad Excellence",
    val age: String = "15",
    val themeSkin: String = "MIDNIGHT_GOLD",
    val alarmAudioSource: String = "SYSTEM_ALARM", // "SYSTEM_ALARM", "APP_SOUND_PACK", "CUSTOM_LOCAL_AUDIO"
    val customAudioUri: String = "",
    val customAudioName: String = "None selected",
    // System Access Permission Toggles in Upper Settings Gear Menu
    val permGallery: Boolean = true,
    val permCamera: Boolean = true,
    val permFileManager: Boolean = true,
    val permAudio: Boolean = true,
    val permHardwareSpeakers: Boolean = true,
    val permSystemAlarms: Boolean = true,
    val permPushNotifications: Boolean = true
)
