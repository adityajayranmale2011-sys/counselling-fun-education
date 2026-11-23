package com.example.util

import android.content.Context
import android.content.Intent
import android.net.Uri
import com.example.data.local.ScheduleTimeBlock
import java.util.UUID

enum class ExploreSelectionSection(val number: Int, val shortTitle: String, val fullTitle: String) {
    GAMES(1, "1. 12 Games", "1st Selection: 12 Unlimited Educational Games"),
    EXAM_MAKER(2, "2. Exam & Weekly", "2nd Selection: Custom Exam Paper, Auto-Check & Weekly 20M"),
    COIN_SOCIAL(3, "3. Coins & Apps", "3rd Selection: Coin Vault (1 Coin = 15s) & Social Media Lock"),
    BOARD_PATTERNS(4, "4. Board Marks", "4th Selection: Board Patterns & Full-Marks Writing Coach"),
    SUBJECT_TIMETABLE(5, "5. Smart Timetable", "5th Selection: Exam + Optional School/Tuition Timetable")
}

enum class ExploreGameType(val id: String, val label: String, val hint: String) {
    MCQ("MCQ", "1. MCQ", "Choose the single best option"),
    FILL_BLANKS("FILL_BLANKS", "2. Fill in the Blanks", "Type the missing exact keyword"),
    ANALOGY("ANALOGY", "3. Analogy", "Complete A : B :: C : ?"),
    MATCH_PAIR("MATCH_PAIR", "4. Match the Pair", "Pick the correct matching partner"),
    ODD_MAN_OUT("ODD_MAN_OUT", "5. Odd Man Out", "Select the item that does not belong"),
    TRUE_FALSE("TRUE_FALSE", "6. True or False", "Verify statement accuracy"),
    GIVE_NAMES("GIVE_NAMES", "7. Give the Names", "Name the scientific/historical/math term"),
    MOLECULAR_FORMULA("MOLECULAR_FORMULA", "8. Molecular Formula", "Identify exact chemical/math formula"),
    FILL_DIAGRAM("FILL_DIAGRAM", "9. Fill the Diagram", "Complete the missing box inside the diagram"),
    MAKE_DIAGRAM("MAKE_DIAGRAM", "10. Make Diagram", "Arrange diagram sequence from subject"),
    LABEL_DIAGRAM("LABEL_DIAGRAM", "11. Label Diagram", "Identify labeled part [A], [B], or [C]"),
    UNLIMITED_MIX("UNLIMITED_MIX", "12. Rapid Mix", "Unlimited cross-topic challenge")
}

data class ExploreGameQuestion(
    val id: String = UUID.randomUUID().toString(),
    val gameType: ExploreGameType,
    val subject: String,
    val chapterScope: String,
    val prompt: String,
    val diagramVisualAscii: String? = null,
    val options: List<String>, // Empty if typed input, or choice buttons
    val correctAnswer: String,
    val quickFact: String
)

data class ExamQuestionItem(
    val id: String = UUID.randomUUID().toString(),
    val number: Int,
    val sectionLabel: String,
    val maxMarks: Int,
    val questionText: String,
    val expectedKeywords: List<String>,
    val modelFullMarksAnswer: String,
    val topicTag: String
)

data class CheckedExamEvaluation(
    val totalScore: Int,
    val maxMarks: Int,
    val percentage: Int,
    val coinsEarned: Int,
    val perQuestionFeedback: List<String>,
    val languageSuggestions: String,
    val mistakesList: List<String>
)

data class BoardPatternGuide(
    val boardName: String,
    val structureSummary: String,
    val fullMarksChecklist: List<String>,
    val sampleFullMarkAnswer: String
)

object ExploreEngine {

    fun generateGameQuestion(
        gameType: ExploreGameType,
        classGrade: String,
        subject: String,
        chapterScope: String,
        roundSeed: Int
    ): ExploreGameQuestion {
        val resolvedType = if (gameType == ExploreGameType.UNLIMITED_MIX) {
            val pool = ExploreGameType.entries.filter { it != ExploreGameType.UNLIMITED_MIX }
            pool[roundSeed % pool.size]
        } else {
            gameType
        }

        return when (resolvedType) {
            ExploreGameType.MCQ -> buildMcq(subject, chapterScope, classGrade, roundSeed)
            ExploreGameType.FILL_BLANKS -> buildFillBlank(subject, chapterScope, roundSeed)
            ExploreGameType.ANALOGY -> buildAnalogy(subject, chapterScope, roundSeed)
            ExploreGameType.MATCH_PAIR -> buildMatchPair(subject, chapterScope, roundSeed)
            ExploreGameType.ODD_MAN_OUT -> buildOddManOut(subject, chapterScope, roundSeed)
            ExploreGameType.TRUE_FALSE -> buildTrueFalse(subject, chapterScope, roundSeed)
            ExploreGameType.GIVE_NAMES -> buildGiveNames(subject, chapterScope, roundSeed)
            ExploreGameType.MOLECULAR_FORMULA -> buildMolecularFormula(subject, chapterScope, roundSeed)
            ExploreGameType.FILL_DIAGRAM -> buildFillDiagram(subject, chapterScope, roundSeed)
            ExploreGameType.MAKE_DIAGRAM -> buildMakeDiagram(subject, chapterScope, roundSeed)
            ExploreGameType.LABEL_DIAGRAM -> buildLabelDiagram(subject, chapterScope, roundSeed)
            ExploreGameType.UNLIMITED_MIX -> buildMcq(subject, chapterScope, classGrade, roundSeed)
        }
    }

    private fun buildMcq(subject: String, chapter: String, grade: String, seed: Int): ExploreGameQuestion {
        val items = when (subject) {
            "Mathematics" -> listOf(
                Triple("[$grade • $chapter] What is the discriminant (Δ) of ax² + bx + c = 0?", listOf("b² - 4ac", "b² + 4ac", "4ac - b²", "2a / -b"), "b² - 4ac"),
                Triple("[$grade • $chapter] What is the exact value of sin²θ + cos²θ?", listOf("1", "0", "2", "tan²θ"), "1"),
                Triple("[$grade • $chapter] The sum of interior angles of any triangle is:", listOf("180°", "90°", "360°", "270°"), "180°"),
                Triple("[$grade • $chapter] If 3x - 9 = 12, find the value of x:", listOf("7", "5", "4", "9"), "7")
            )
            "History" -> listOf(
                Triple("[$grade • $chapter] In which year did the Revolt of 1857 begin?", listOf("1857", "1757", "1947", "1885"), "1857"),
                Triple("[$grade • $chapter] Who led the Dandi Salt March in 1930?", listOf("Mahatma Gandhi", "Subhas Chandra Bose", "Bhagat Singh", "Tilak"), "Mahatma Gandhi"),
                Triple("[$grade • $chapter] The Harappan civilization is famous for its:", listOf("Grid town planning & drainage", "Steam locomotives", "Feudal castles", "Printing press"), "Grid town planning & drainage"),
                Triple("[$grade • $chapter] When was the Constitution of India adopted (Republic Day)?", listOf("26 January 1950", "15 August 1947", "2 October 1948", "14 November 1952"), "26 January 1950")
            )
            else -> listOf(
                Triple("[$grade • $chapter] What is the SI unit of Force?", listOf("Newton (N)", "Joule (J)", "Watt (W)", "Pascal (Pa)"), "Newton (N)"),
                Triple("[$grade • $chapter] Which organelle is the powerhouse of the cell?", listOf("Mitochondria", "Ribosome", "Golgi body", "Vacuole"), "Mitochondria"),
                Triple("[$grade • $chapter] What gas is released during photosynthesis?", listOf("Oxygen (O₂)", "Nitrogen (N₂)", "Carbon Monoxide", "Methane"), "Oxygen (O₂)"),
                Triple("[$grade • $chapter] Speed of light in vacuum is approximately:", listOf("3 × 10⁸ m/s", "3 × 10⁵ m/s", "1.5 × 10⁸ m/s", "340 m/s"), "3 × 10⁸ m/s")
            )
        }
        val chosen = items[seed % items.size]
        val shuffled = chosen.second.shuffled()
        return ExploreGameQuestion(
            gameType = ExploreGameType.MCQ,
            subject = subject,
            chapterScope = chapter,
            prompt = chosen.first,
            options = shuffled,
            correctAnswer = chosen.third,
            quickFact = "Correct Answer: ${chosen.third}"
        )
    }

    private fun buildFillBlank(subject: String, chapter: String, seed: Int): ExploreGameQuestion {
        val bank = when (subject) {
            "Mathematics" -> listOf(
                Triple("[$chapter] A line intersecting a circle at two distinct points is called a ______.", listOf("Secant", "Tangent", "Radius", "Chord"), "Secant"),
                Triple("[$chapter] The point (0, 0) on the Cartesian plane is known as the ______.", listOf("Origin", "Abscissa", "Ordinate", "Vertex"), "Origin")
            )
            "History" -> listOf(
                Triple("[$chapter] The Jallianwala Bagh massacre took place in the city of ______ in 1919.", listOf("Amritsar", "Delhi", "Lahore", "Surat"), "Amritsar"),
                Triple("[$chapter] The storming of the Bastille occurred in ______ on 14 July 1789.", listOf("Paris", "London", "Rome", "Berlin"), "Paris")
            )
            else -> listOf(
                Triple("[$chapter] The bending of light as it passes from one medium to another is called ______.", listOf("Refraction", "Reflection", "Diffraction", "Dispersion"), "Refraction"),
                Triple("[$chapter] Acids turn blue litmus paper into ______.", listOf("Red", "Green", "Yellow", "Colorless"), "Red")
            )
        }
        val item = bank[seed % bank.size]
        return ExploreGameQuestion(
            gameType = ExploreGameType.FILL_BLANKS,
            subject = subject,
            chapterScope = chapter,
            prompt = item.first,
            options = item.second.shuffled(),
            correctAnswer = item.third,
            quickFact = "Key Term: ${item.third}"
        )
    }

    private fun buildAnalogy(subject: String, chapter: String, seed: Int): ExploreGameQuestion {
        val bank = when (subject) {
            "Mathematics" -> listOf(
                Triple("[$chapter] Square : 4 sides :: Hexagon : ?", listOf("6 sides", "5 sides", "8 sides", "7 sides"), "6 sides"),
                Triple("[$chapter] sin θ : Opposite/Hypotenuse :: cos θ : ?", listOf("Adjacent/Hypotenuse", "Opposite/Adjacent", "Hypotenuse/Adjacent", "1/sin θ"), "Adjacent/Hypotenuse")
            )
            "History" -> listOf(
                Triple("[$chapter] 1947 : Independence Day :: 1950 : ?", listOf("Republic Day", "Quit India", "Dandi March", "Partition"), "Republic Day"),
                Triple("[$chapter] Lokmanya Tilak : Swaraj :: Subhas Bose : ?", listOf("Azad Hind Fauj", "Brahmo Samaj", "Arya Samaj", "Satyagraha"), "Azad Hind Fauj")
            )
            else -> listOf(
                Triple("[$chapter] Newton : Force :: Joule : ?", listOf("Work / Energy", "Power", "Pressure", "Electric Current"), "Work / Energy"),
                Triple("[$chapter] Xylem : Water transport :: Phloem : ?", listOf("Food transport", "Oxygen intake", "Cell division", "Root anchor"), "Food transport")
            )
        }
        val item = bank[seed % bank.size]
        return ExploreGameQuestion(
            gameType = ExploreGameType.ANALOGY,
            subject = subject,
            chapterScope = chapter,
            prompt = "Complete the Analogy: ${item.first}",
            options = item.second.shuffled(),
            correctAnswer = item.third,
            quickFact = "Analogy Pair: ${item.third}"
        )
    }

    private fun buildMatchPair(subject: String, chapter: String, seed: Int): ExploreGameQuestion {
        val bank = when (subject) {
            "Mathematics" -> listOf(
                Triple("[$chapter] Match 'Pythagoras Theorem' with its exact identity:", listOf("a² + b² = c²", "πr²", "2πr", "½ × b × h"), "a² + b² = c²"),
                Triple("[$chapter] Match 'Area of Circle' with its exact formula:", listOf("πr²", "2πr", "4πr²", "l × b"), "πr²")
            )
            "History" -> listOf(
                Triple("[$chapter] Match 'Harappan Port City' with the correct site:", listOf("Lothal", "Nalanda", "Sanchi", "Hampi"), "Lothal"),
                Triple("[$chapter] Match 'Quit India Resolution (1942)' slogan:", listOf("Do or Die", "Inquilab Zindabad", "Jai Hind", "Vande Mataram"), "Do or Die")
            )
            else -> listOf(
                Triple("[$chapter] Match 'Ohm's Law' with its correct equation:", listOf("V = I × R", "P = m × v", "F = G m₁m₂/r²", "E = mc²"), "V = I × R"),
                Triple("[$chapter] Match 'Universal Solvent' with its chemical identity:", listOf("Water (H₂O)", "Ethanol", "Benzene", "Acetone"), "Water (H₂O)")
            )
        }
        val item = bank[seed % bank.size]
        return ExploreGameQuestion(
            gameType = ExploreGameType.MATCH_PAIR,
            subject = subject,
            chapterScope = chapter,
            prompt = item.first,
            options = item.second.shuffled(),
            correctAnswer = item.third,
            quickFact = "Matched Partner: ${item.third}"
        )
    }

    private fun buildOddManOut(subject: String, chapter: String, seed: Int): ExploreGameQuestion {
        val bank = when (subject) {
            "Mathematics" -> listOf(
                Triple("[$chapter] Find the Odd Man Out (Non-Prime Number):", listOf("9", "2", "5", "11"), "9"),
                Triple("[$chapter] Find the Odd Man Out (3D Solid among 2D Shapes):", listOf("Cylinder", "Triangle", "Rectangle", "Rhombus"), "Cylinder")
            )
            "History" -> listOf(
                Triple("[$chapter] Find the Odd Man Out (Not an Ancient River Civilization):", listOf("Roman", "Harappan", "Mesopotamian", "Egyptian"), "Roman"),
                Triple("[$chapter] Find the Odd Man Out (Not a Mughal Emperor):", listOf("Shivaji Maharaj", "Akbar", "Babur", "Shah Jahan"), "Shivaji Maharaj")
            )
            else -> listOf(
                Triple("[$chapter] Find the Odd Man Out (Non-Metal among Metals):", listOf("Carbon", "Iron", "Copper", "Aluminium"), "Carbon"),
                Triple("[$chapter] Find the Odd Man Out (Scalar among Vector Quantities):", listOf("Mass", "Velocity", "Force", "Acceleration"), "Mass")
            )
        }
        val item = bank[seed % bank.size]
        return ExploreGameQuestion(
            gameType = ExploreGameType.ODD_MAN_OUT,
            subject = subject,
            chapterScope = chapter,
            prompt = item.first,
            options = item.second.shuffled(),
            correctAnswer = item.third,
            quickFact = "Odd One: ${item.third}"
        )
    }

    private fun buildTrueFalse(subject: String, chapter: String, seed: Int): ExploreGameQuestion {
        val bank = when (subject) {
            "Mathematics" -> listOf(
                Pair("[$chapter] Statement: Every square is a rectangle, but every rectangle is not necessarily a square.", "True"),
                Pair("[$chapter] Statement: Division by zero (5 / 0) produces a finite real number.", "False")
            )
            "History" -> listOf(
                Pair("[$chapter] Statement: The Indian National Congress was founded in 1885.", "True"),
                Pair("[$chapter] Statement: World War II ended in the year 1918.", "False")
            )
            else -> listOf(
                Pair("[$chapter] Statement: Arteries normally carry oxygenated blood away from the heart.", "True"),
                Pair("[$chapter] Statement: Sound waves can travel effortlessly through a complete vacuum.", "False")
            )
        }
        val item = bank[seed % bank.size]
        return ExploreGameQuestion(
            gameType = ExploreGameType.TRUE_FALSE,
            subject = subject,
            chapterScope = chapter,
            prompt = item.first,
            options = listOf("True", "False"),
            correctAnswer = item.second,
            quickFact = "Verified: ${item.second}"
        )
    }

    private fun buildGiveNames(subject: String, chapter: String, seed: Int): ExploreGameQuestion {
        val bank = when (subject) {
            "Mathematics" -> listOf(
                Triple("[$chapter] Give the name: A polygon with exactly 8 sides.", listOf("Octagon", "Heptagon", "Nonagon", "Pentagon"), "Octagon"),
                Triple("[$chapter] Give the name: The longest chord of a circle passing through the center.", listOf("Diameter", "Radius", "Arc", "Sector"), "Diameter")
            )
            "History" -> listOf(
                Triple("[$chapter] Give the name: The first President of independent India.", listOf("Dr. Rajendra Prasad", "Dr. B.R. Ambedkar", "Jawaharlal Nehru", "Sardar Patel"), "Dr. Rajendra Prasad"),
                Triple("[$chapter] Give the name: The Chairman of the Drafting Committee of India's Constitution.", listOf("Dr. B.R. Ambedkar", "Mahatma Gandhi", "C. Rajagopalachari", "Maulana Azad"), "Dr. B.R. Ambedkar")
            )
            else -> listOf(
                Triple("[$chapter] Give the name: The metal that remains liquid at room temperature.", listOf("Mercury", "Sodium", "Magnesium", "Zinc"), "Mercury"),
                Triple("[$chapter] Give the name: The green pigment essential for photosynthesis in plants.", listOf("Chlorophyll", "Hemoglobin", "Melanin", "Keratin"), "Chlorophyll")
            )
        }
        val item = bank[seed % bank.size]
        return ExploreGameQuestion(
            gameType = ExploreGameType.GIVE_NAMES,
            subject = subject,
            chapterScope = chapter,
            prompt = item.first,
            options = item.second.shuffled(),
            correctAnswer = item.third,
            quickFact = "Exact Name: ${item.third}"
        )
    }

    private fun buildMolecularFormula(subject: String, chapter: String, seed: Int): ExploreGameQuestion {
        val bank = when (subject) {
            "Mathematics" -> listOf(
                Triple("[$chapter] Identify the exact Quadratic Formula for roots of ax² + bx + c = 0:", listOf("x = (-b ± √(b² - 4ac)) / 2a", "x = (b ± √(b² + 4ac)) / 2a", "x = (-b ± √(4ac)) / a", "x = -c / b"), "x = (-b ± √(b² - 4ac)) / 2a"),
                Triple("[$chapter] Identify the nth term formula of an Arithmetic Progression:", listOf("aₙ = a + (n - 1)d", "aₙ = a × rⁿ⁻¹", "aₙ = n(n + 1)/2", "aₙ = a + nd"), "aₙ = a + (n - 1)d")
            )
            else -> listOf(
                Triple("[$chapter] Molecular Formula of Glucose:", listOf("C₆H₁₂O₆", "C₁₂H₂₂O₁₁", "CH₄", "C₂H₅OH"), "C₆H₁₂O₆"),
                Triple("[$chapter] Molecular Formula of Sulfuric Acid:", listOf("H₂SO₄", "HCl", "HNO₃", "NaOH"), "H₂SO₄"),
                Triple("[$chapter] Molecular Formula of Calcium Carbonate (Limestone):", listOf("CaCO₃", "CaO", "Ca(OH)₂", "Na₂CO₃"), "CaCO₃"),
                Triple("[$chapter] Molecular Formula of Sodium Chloride (Common Salt):", listOf("NaCl", "KCl", "Na₂SO₄", "MgCl₂"), "NaCl")
            )
        }
        val item = bank[seed % bank.size]
        return ExploreGameQuestion(
            gameType = ExploreGameType.MOLECULAR_FORMULA,
            subject = subject,
            chapterScope = chapter,
            prompt = item.first,
            options = item.second.shuffled(),
            correctAnswer = item.third,
            quickFact = "Formula: ${item.third}"
        )
    }

    private fun buildFillDiagram(subject: String, chapter: String, seed: Int): ExploreGameQuestion {
        val (ascii, prompt, choices, ans) = when (subject) {
            "Mathematics" -> listOf(
                """
      Right Triangle (∠B = 90°)
          A
          |\
          | \  [ ? Hypotenuse ]
       3  |  \
          |___\
          B  4  C
                """.trimIndent()
            ).let {
                Quad(it[0], "[$chapter] Fill the missing Hypotenuse [?] in the Right Triangle Diagram:", listOf("5", "7", "6", "12"), "5")
            }
            "History" -> Quad(
                """
   Indian Freedom Timeline
   [1857 Revolt] ──▶ [1885 INC] ──▶ [1930 ?] ──▶ [1947 Independence]
                """.trimIndent(),
                "[$chapter] Fill the missing milestone [1930 ?] inside the History Diagram:",
                listOf("Dandi Salt March", "Battle of Plassey", "Quit India", "Partition of Bengal"),
                "Dandi Salt March"
            )
            else -> Quad(
                """
   Photosynthesis Flow Diagram:
   [Sunlight] + [CO₂] + [H₂O] ──( [ ? ] )──▶ [Glucose] + [O₂]
                """.trimIndent(),
                "[$chapter] Fill the missing catalyst [ ? ] inside the Leaf Diagram:",
                listOf("Chlorophyll", "Mitochondria", "Nitrogen", "Starch"),
                "Chlorophyll"
            )
        }
        return ExploreGameQuestion(
            gameType = ExploreGameType.FILL_DIAGRAM,
            subject = subject,
            chapterScope = chapter,
            prompt = prompt,
            diagramVisualAscii = ascii,
            options = choices.shuffled(),
            correctAnswer = ans,
            quickFact = "Diagram Slot Filled: $ans"
        )
    }

    private fun buildMakeDiagram(subject: String, chapter: String, seed: Int): ExploreGameQuestion {
        val (prompt, diagramPreview, options, ans) = when (subject) {
            "Mathematics" -> Quad(
                "[$chapter] Build the Coordinate Quadrant Sign Diagram in clockwise order (Q1 → Q2 → Q3 → Q4):",
                "[Q1] ──▶ [Q2] ──▶ [Q3] ──▶ [Q4]",
                listOf(
                    "(+,+) → (-,+) → (-,-) → (+,-)",
                    "(+,+) → (+,-) → (-,-) → (-,+)",
                    "(-,-) → (+,+) → (-,+) → (+,-)",
                    "(+,-) → (-,+) → (+,+) → (-,-)"
                ),
                "(+,+) → (-,+) → (-,-) → (+,-)"
            )
            "History" -> Quad(
                "[$chapter] Construct the Chronological Governance Diagram from Ancient to Modern India:",
                "[Node 1] ──▶ [Node 2] ──▶ [Node 3] ──▶ [Node 4]",
                listOf(
                    "Harappan → Mauryan → Mughal → Democratic Republic",
                    "Mughal → Harappan → Mauryan → Democratic Republic",
                    "Mauryan → Democratic Republic → Harappan → Mughal",
                    "Harappan → Mughal → Mauryan → Democratic Republic"
                ),
                "Harappan → Mauryan → Mughal → Democratic Republic"
            )
            else -> Quad(
                "[$chapter] Construct the Human Digestive Tract Diagram Sequence from start to end:",
                "[Start] ──▶ [Tube] ──▶ [Acid Bag] ──▶ [Absorption] ──▶ [Water]",
                listOf(
                    "Mouth → Esophagus → Stomach → Small Intestine → Large Intestine",
                    "Mouth → Stomach → Esophagus → Large Intestine → Small Intestine",
                    "Stomach → Mouth → Small Intestine → Esophagus → Large Intestine",
                    "Mouth → Small Intestine → Stomach → Esophagus → Large Intestine"
                ),
                "Mouth → Esophagus → Stomach → Small Intestine → Large Intestine"
            )
        }
        return ExploreGameQuestion(
            gameType = ExploreGameType.MAKE_DIAGRAM,
            subject = subject,
            chapterScope = chapter,
            prompt = prompt,
            diagramVisualAscii = diagramPreview,
            options = options.shuffled(),
            correctAnswer = ans,
            quickFact = "Diagram Blueprint Built: $ans"
        )
    }

    private fun buildLabelDiagram(subject: String, chapter: String, seed: Int): ExploreGameQuestion {
        val (ascii, prompt, options, ans) = when (subject) {
            "Mathematics" -> Quad(
                """
      Circle Parts Diagram
         . - ~ - .
      /      | [A]  \
     |   (Center O)  |  <-- [B] Boundary
      \      |      /
         ' - ~ - '
                """.trimIndent(),
                "[$chapter] Give the Label of Diagram part [A] (line from Center O to Boundary):",
                listOf("Radius", "Chord", "Secant", "Tangent"),
                "Radius"
            )
            "History" -> Quad(
                """
   3-Tier Indian Parliament Diagram
      [A: Upper House (245 Members)]
      [B: Lower House (543 Members)]
      [C: Head of State]
                """.trimIndent(),
                "[$chapter] Give the Label of Diagram part [B] (Lower House of People):",
                listOf("Lok Sabha", "Rajya Sabha", "Supreme Court", "NITI Aayog"),
                "Lok Sabha"
            )
            else -> Quad(
                """
     Atom Structure Diagram
         ( - ) Electron [A]
        /             \
       |   [B: + & n]  |  <-- Central Core
        \             /
                """.trimIndent(),
                "[$chapter] Give the Label of Diagram part [B] (Central Core with Protons & Neutrons):",
                listOf("Nucleus", "Orbit Shell", "Valence Cloud", "Photon"),
                "Nucleus"
            )
        }
        return ExploreGameQuestion(
            gameType = ExploreGameType.LABEL_DIAGRAM,
            subject = subject,
            chapterScope = chapter,
            prompt = prompt,
            diagramVisualAscii = ascii,
            options = options.shuffled(),
            correctAnswer = ans,
            quickFact = "Diagram Label: $ans"
        )
    }

    data class Quad<A, B, C, D>(val first: A, val second: B, val third: C, val fourth: D)

    // --- Selection 2: Customizable Exam Paper Generator + Checker ---

    fun generateCustomExamPaper(
        classGrade: String,
        subject: String,
        chapterScope: String,
        patternName: String,
        totalMarks: Int
    ): List<ExamQuestionItem> {
        val qCount = if (totalMarks >= 40) 5 else 4
        val marksPerQ = (totalMarks / qCount).coerceAtLeast(2)
        val remainder = totalMarks - (marksPerQ * qCount)

        return when (subject) {
            "Mathematics" -> listOf(
                ExamQuestionItem(
                    number = 1,
                    sectionLabel = "Section A (Concept & Formula)",
                    maxMarks = marksPerQ,
                    questionText = "[$classGrade • $patternName] State the Quadratic Formula and find the roots of x² - 5x + 6 = 0 ($chapterScope).",
                    expectedKeywords = listOf("2", "3", "4ac", "discriminant", "roots"),
                    modelFullMarksAnswer = "Discriminant Δ = b² - 4ac = 25 - 24 = 1. Roots x = (5 ± 1)/2 ⇒ x = 3 and x = 2.",
                    topicTag = "Quadratic Equations"
                ),
                ExamQuestionItem(
                    number = 2,
                    sectionLabel = "Section B (Application)",
                    maxMarks = marksPerQ,
                    questionText = "Prove that sin²θ + cos²θ = 1 using a right-angled triangle and find sin 30° + cos 60°.",
                    expectedKeywords = listOf("hypotenuse", "1", "0.5", "pythagoras"),
                    modelFullMarksAnswer = "By Pythagoras AB² + BC² = AC². Dividing by AC² gives sin²θ + cos²θ = 1. sin 30° + cos 60° = 0.5 + 0.5 = 1.",
                    topicTag = "Trigonometry"
                ),
                ExamQuestionItem(
                    number = 3,
                    sectionLabel = "Section C (Reasoning)",
                    maxMarks = marksPerQ,
                    questionText = "Find the 10th term of the Arithmetic Progression: 2, 7, 12, ... using the formula aₙ = a + (n - 1)d.",
                    expectedKeywords = listOf("47", "5", "9", "45"),
                    modelFullMarksAnswer = "Here a = 2, d = 5, n = 10. a₁₀ = 2 + (10 - 1) × 5 = 2 + 45 = 47.",
                    topicTag = "Arithmetic Progressions"
                ),
                ExamQuestionItem(
                    number = 4,
                    sectionLabel = "Section D (Long Answer)",
                    maxMarks = marksPerQ + remainder,
                    questionText = "Find the distance between points A(2, 3) and B(5, 7) using the distance formula.",
                    expectedKeywords = listOf("5", "9", "16", "25", "sqrt"),
                    modelFullMarksAnswer = "d = √[(5 - 2)² + (7 - 3)²] = √(3² + 4²) = √(9 + 16) = √25 = 5 units.",
                    topicTag = "Coordinate Geometry"
                )
            )
            "History" -> listOf(
                ExamQuestionItem(
                    number = 1,
                    sectionLabel = "Section A (Short Answer)",
                    maxMarks = marksPerQ,
                    questionText = "[$classGrade • $patternName] State two key causes of the Revolt of 1857 ($chapterScope).",
                    expectedKeywords = listOf("cartridge", "doctrine", "economic", "british", "sepoy"),
                    modelFullMarksAnswer = "1. Political annexation via Doctrine of Lapse. 2. Immediate trigger of greased Enfield cartridges hurting religious sentiments.",
                    topicTag = "Revolt of 1857"
                ),
                ExamQuestionItem(
                    number = 2,
                    sectionLabel = "Section B (National Movement)",
                    maxMarks = marksPerQ,
                    questionText = "Why did Mahatma Gandhi launch the Dandi Salt March in 1930?",
                    expectedKeywords = listOf("salt", "tax", "civil disobedience", "sabarmati", "1930"),
                    modelFullMarksAnswer = "To break the unjust British Salt Tax monopoly and unite all Indians under the Civil Disobedience Movement from Sabarmati to Dandi.",
                    topicTag = "Nationalism in India"
                ),
                ExamQuestionItem(
                    number = 3,
                    sectionLabel = "Section C (Source Analysis)",
                    maxMarks = marksPerQ,
                    questionText = "Explain the significance of the Quit India Resolution of August 1942.",
                    expectedKeywords = listOf("do or die", "1942", "gandhi", "independence", "bombay"),
                    modelFullMarksAnswer = "Passed on 8 August 1942 in Bombay with Gandhi's call 'Do or Die', demanding immediate end to British rule.",
                    topicTag = "Quit India 1942"
                ),
                ExamQuestionItem(
                    number = 4,
                    sectionLabel = "Section D (Essay)",
                    maxMarks = marksPerQ + remainder,
                    questionText = "Describe two features of Harappan urban town planning.",
                    expectedKeywords = listOf("grid", "drainage", "brick", "citadel", "bath"),
                    modelFullMarksAnswer = "1. Rectangular grid street pattern with right-angle intersections. 2. Covered underground brick drainage system & Great Bath.",
                    topicTag = "Harappan Civilization"
                )
            )
            else -> listOf(
                ExamQuestionItem(
                    number = 1,
                    sectionLabel = "Section A (Laws & Units)",
                    maxMarks = marksPerQ,
                    questionText = "[$classGrade • $patternName] State Newton's Second Law of Motion and write its formula and SI unit ($chapterScope).",
                    expectedKeywords = listOf("momentum", "ma", "newton", "force", "mass"),
                    modelFullMarksAnswer = "The rate of change of momentum is directly proportional to applied force. Formula: F = m × a; SI Unit: Newton (N).",
                    topicTag = "Laws of Motion"
                ),
                ExamQuestionItem(
                    number = 2,
                    sectionLabel = "Section B (Chemical Equation)",
                    maxMarks = marksPerQ,
                    questionText = "Write the balanced chemical equation for Photosynthesis and state the role of Chlorophyll.",
                    expectedKeywords = listOf("co2", "h2o", "glucose", "oxygen", "sunlight", "chlorophyll"),
                    modelFullMarksAnswer = "6CO₂ + 6H₂O → C₆H₁₂O₆ + 6O₂ (in sunlight & chlorophyll). Chlorophyll traps solar energy.",
                    topicTag = "Life Processes"
                ),
                ExamQuestionItem(
                    number = 3,
                    sectionLabel = "Section C (Electricity)",
                    maxMarks = marksPerQ,
                    questionText = "State Ohm's Law and calculate current I if Voltage V = 12V and Resistance R = 4Ω.",
                    expectedKeywords = listOf("3", "ampere", "v = ir", "constant", "temperature"),
                    modelFullMarksAnswer = "At constant temperature, V ∝ I (V = I × R). Thus I = V / R = 12 / 4 = 3 Amperes.",
                    topicTag = "Electricity & Ohm's Law"
                ),
                ExamQuestionItem(
                    number = 4,
                    sectionLabel = "Section D (Cell Biology)",
                    maxMarks = marksPerQ + remainder,
                    questionText = "Why are Mitochondria called the powerhouse of the cell? Name the energy currency produced.",
                    expectedKeywords = listOf("atp", "respiration", "energy", "cellular"),
                    modelFullMarksAnswer = "Mitochondria perform aerobic cellular respiration to oxidize glucose and generate ATP (Adenosine Triphosphate), the cell's energy currency.",
                    topicTag = "Cell Biology"
                )
            )
        }.take(qCount)
    }

    fun evaluateExamPaper(
        questions: List<ExamQuestionItem>,
        studentAnswers: Map<String, String>
    ): CheckedExamEvaluation {
        var totalScore = 0
        var totalPossible = 0
        val feedback = mutableListOf<String>()
        val mistakes = mutableListOf<String>()
        var weakGrammarCount = 0

        questions.forEach { q ->
            totalPossible += q.maxMarks
            val rawAns = studentAnswers[q.id]?.trim().orEmpty()
            val lowerAns = rawAns.lowercase()

            if (rawAns.isBlank()) {
                feedback.add("Q${q.number} (0/${q.maxMarks}): Unattempted. Model Key: ${q.modelFullMarksAnswer}")
                mistakes.add("${q.topicTag} (Unattempted)")
                weakGrammarCount++
            } else {
                val matchedCount = q.expectedKeywords.count { kw -> lowerAns.contains(kw.lowercase()) }
                val ratio = (matchedCount.toFloat() / q.expectedKeywords.size.coerceAtLeast(1)).coerceIn(0f, 1f)
                val earned = when {
                    ratio >= 0.6f -> q.maxMarks
                    ratio >= 0.3f -> (q.maxMarks * 0.6f).toInt().coerceAtLeast(1)
                    rawAns.length > 15 -> 1
                    else -> 0
                }
                totalScore += earned
                if (earned < q.maxMarks) {
                    mistakes.add("${q.topicTag} ($earned/${q.maxMarks})")
                }
                if (rawAns.length < 25 || !rawAns.contains("=") && q.sectionLabel.contains("Formula")) {
                    weakGrammarCount++
                }
                feedback.add("Q${q.number} ($earned/${q.maxMarks}): ${if (earned == q.maxMarks) "Full Marks! Well structured." else "Partial/Needs improvement. Include: ${q.expectedKeywords.joinToString(", ")}"}")
            }
        }

        val percentage = if (totalPossible > 0) (totalScore * 100) / totalPossible else 0
        val languageTip = buildString {
            append("Language & Presentation Feedback: ")
            if (weakGrammarCount == 0) {
                append("Clear academic phrasing and formula notation! Keep underlining final answers and stating SI units.")
            } else {
                append("Write complete sentences starting with formal stems ('By definition...', 'Given that...'), highlight exact formulas in a box, and avoid one-word fragments to gain full board-exam credit.")
            }
        }

        return CheckedExamEvaluation(
            totalScore = totalScore,
            maxMarks = totalPossible,
            percentage = percentage,
            coinsEarned = totalScore, // Rule: "Solve the exam to give its marks in coin"
            perQuestionFeedback = feedback,
            languageSuggestions = languageTip,
            mistakesList = mistakes
        )
    }

    // --- Selection 4: Board Patterns & Full-Marks Writing Coach ---

    fun getBoardPatternGuides(): List<BoardPatternGuide> = listOf(
        BoardPatternGuide(
            boardName = "CBSE / NCERT Board Pattern (80 Marks + 20 Internal)",
            structureSummary = "Sec A: 20 MCQs/Assertion-Reason (1M) • Sec B: 5 Very Short (2M) • Sec C: 6 Short (3M) • Sec D: 4 Long (5M) • Sec E: 3 Case-Based (4M).",
            fullMarksChecklist = listOf(
                "Underline all NCERT technical keywords (e.g., 'ATP', 'Electromagnetic Induction', 'Satyagraha').",
                "For numericals: Always write Given → Formula → Substitution → Answer with SI Unit in a box.",
                "Draw neat pencil-labeled diagrams for every 3-mark and 5-mark Science/Geography question.",
                "Divide 5-mark answers into exactly 5 numbered bullet points with bold sub-headings."
            ),
            sampleFullMarkAnswer = "Q (3M): State Ohm's Law.\n✔ Statement: At constant temperature, current (I) is directly proportional to potential difference (V).\n✔ Formula: V = I × R (R = Resistance in Ohms Ω).\n✔ Graph: Straight-line V-I graph passing through origin (0,0)."
        ),
        BoardPatternGuide(
            boardName = "State Board / SSC Pattern (40/80 Marks Activity & Concept)",
            structureSummary = "Q1: Objectives/MCQ/Odd Man Out/Correlation • Q2: Scientific Reasons & Definitions • Q3: Diagram/Flowchart Completion • Q4: 5-Mark Brief.",
            fullMarksChecklist = listOf(
                "For 'Give Scientific Reasons' (2 Marks): Write 4 crisp, cause-and-effect points.",
                "For 'Distinguish Between': Always draw a 2-column table with minimum 2 valid points per mark.",
                "For 'Complete the Flowchart / Diagram': Redraw the complete boxes neatly and underline filled blanks."
            ),
            sampleFullMarkAnswer = "Q (2M): Distinguish Speed vs Velocity.\n| Speed | Velocity |\n| 1. Scalar quantity (magnitude only) | 1. Vector quantity (magnitude + direction) |\n| 2. Always positive or zero | 2. Can be positive, negative, or zero |"
        ),
        BoardPatternGuide(
            boardName = "ICSE / ISC Pattern (Structured Analytical & Application)",
            structureSummary = "Part I (Compulsory Short Questions, Balanced Equations, Map/Diagram) + Part II (Choice-based Multi-part Analytical Problems).",
            fullMarksChecklist = listOf(
                "Write balanced chemical equations with state symbols and reaction conditions above the arrow.",
                "Show step-by-step rough working clearly on the right margin for Maths problems.",
                "Answer sub-parts (a), (b), (c) in exact sequential order without mixing sections."
            ),
            sampleFullMarkAnswer = "Q (2M): Write balanced equation for thermal decomposition of Calcium Carbonate.\n✔ CaCO₃(s) —(Δ Heat)→ CaO(s) + CO₂(g)↑\n✔ Observation: Brisk effervescence of CO₂ turns limewater milky."
        ),
        BoardPatternGuide(
            boardName = "Weekly 20-Mark School / Unit Test Pattern",
            structureSummary = "Q1: 4 MCQs/1-Line (4M) • Q2: 3 Short Questions (6M) • Q3: 2 Application/Diagram Questions (6M) • Q4: 1 Long Derivation (4M).",
            fullMarksChecklist = listOf(
                "Time budget: 5 mins Objectives, 10 mins Short, 15 mins Long/Diagrams, 5 mins Revision.",
                "Never leave any question blank — write relevant formula/definition for step-marking credit."
            ),
            sampleFullMarkAnswer = "Q (4M): Derive s = ut + ½at² or solve quadratic word problem.\n✔ Step 1: Define variables. Step 2: Plot/state Velocity-Time area. Step 3: Box final equation."
        )
    )

    // --- Selection 5: Build Subject Timetable from Exam + Optional School + Optional Tuition ---

    fun buildSmartSubjectTimetableFromInputs(
        studentName: String,
        classGrade: String,
        selectedSubjects: List<String>,
        examTargetSummary: String,
        includeSchool: Boolean,
        schoolTiming: String,
        includeTuition: Boolean,
        tuitionTiming: String
    ): List<ScheduleTimeBlock> {
        val subjects = selectedSubjects.ifEmpty { listOf("Science", "Mathematics", "History") }
        val morningSubj = subjects.getOrElse(0) { "Science" }
        val secondSubj = subjects.getOrElse(1) { morningSubj }
        val eveningSubj = subjects.find { it.contains("Math", ignoreCase = true) } ?: subjects.last()

        val blocks = mutableListOf(
            ScheduleTimeBlock(
                id = UUID.randomUUID().toString(),
                timeRange = "04:30 - 05:25",
                title = "Exam Focus Absorption ($morningSubj)",
                subjectOrDetail = "Exam Target: $examTargetSummary",
                durationLabel = "55 Mins",
                metaHeader = "STUDY TIME",
                dayOrPhaseLabel = "Exam Priority"
            ),
            ScheduleTimeBlock(
                id = UUID.randomUUID().toString(),
                timeRange = "05:25 - 05:35",
                title = "Transition Buffer",
                subjectOrDetail = "10-Min Recharge Break",
                durationLabel = "10 Mins",
                metaHeader = "RECHARGE",
                dayOrPhaseLabel = "Exam Priority"
            ),
            ScheduleTimeBlock(
                id = UUID.randomUUID().toString(),
                timeRange = "05:35 - 06:25",
                title = "Second Subject Concept Block ($secondSubj)",
                subjectOrDetail = "Student: $studentName ($classGrade) • Locked Subject Only",
                durationLabel = "50 Mins",
                metaHeader = "STUDY TIME",
                dayOrPhaseLabel = "Exam Priority"
            )
        )

        if (includeSchool) {
            blocks.add(
                ScheduleTimeBlock(
                    id = UUID.randomUUID().toString(),
                    timeRange = schoolTiming.ifBlank { "07:30 - 13:00" },
                    title = "School Timetable (Optional Enabled)",
                    subjectOrDetail = "Institutional School Classes",
                    durationLabel = "School",
                    metaHeader = "SCHOOL",
                    dayOrPhaseLabel = "School"
                )
            )
        } else {
            blocks.add(
                ScheduleTimeBlock(
                    id = UUID.randomUUID().toString(),
                    timeRange = "09:00 - 11:30",
                    title = "Self-Study Exam Sprint (No School Mode)",
                    subjectOrDetail = "Deep Chapter Revision: ${subjects.joinToString(", ")}",
                    durationLabel = "150 Mins",
                    metaHeader = "STUDY TIME",
                    dayOrPhaseLabel = "Exam Sprint"
                )
            )
        }

        if (includeTuition) {
            blocks.add(
                ScheduleTimeBlock(
                    id = UUID.randomUUID().toString(),
                    timeRange = tuitionTiming.ifBlank { "15:00 - 17:30" },
                    title = "Tuition Timetable (Optional Enabled)",
                    subjectOrDetail = "Coaching Doubt Clearing for ${subjects.joinToString(", ")}",
                    durationLabel = "Tuition",
                    metaHeader = "STUDY TIME",
                    dayOrPhaseLabel = "Tuition"
                )
            )
        }

        blocks.add(
            ScheduleTimeBlock(
                id = UUID.randomUUID().toString(),
                timeRange = "17:45 - 18:45",
                title = "Physical Playtime & Recharge",
                subjectOrDetail = "Outdoor Sports & Screen-Free Reset",
                durationLabel = "60 Mins",
                metaHeader = "RECHARGE",
                dayOrPhaseLabel = "Recharge"
            )
        )
        blocks.add(
            ScheduleTimeBlock(
                id = UUID.randomUUID().toString(),
                timeRange = "20:00 - 21:45",
                title = "Evening Active Practice ($eveningSubj)",
                subjectOrDetail = "Solve Previous Exam Questions & Full-Marks Writing Practice",
                durationLabel = "105 Mins",
                metaHeader = "PRACTICE",
                dayOrPhaseLabel = "Exam Practice"
            )
        )
        blocks.add(
            ScheduleTimeBlock(
                id = UUID.randomUUID().toString(),
                timeRange = "22:00",
                title = "Hard Sleep Rest",
                subjectOrDetail = "Memory Consolidation before Exam Day",
                durationLabel = "8 Hours",
                metaHeader = "SLEEPING",
                dayOrPhaseLabel = "Rest"
            )
        )
        return blocks
    }

    // --- Parent Reporting via WhatsApp & SMS (Play-Policy Compliant Intents) ---

    fun sendReportViaWhatsApp(context: Context, phoneNumber: String, message: String) {
        val cleanDigits = phoneNumber.filter { it.isDigit() }
        val encodedMsg = Uri.encode(message)
        try {
            val waIntent = Intent(
                Intent.ACTION_VIEW,
                Uri.parse("https://wa.me/$cleanDigits?text=$encodedMsg")
            ).apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            context.startActivity(waIntent)
        } catch (_: Exception) {
            val shareIntent = Intent(Intent.ACTION_SEND).apply {
                type = "text/plain"
                putExtra(Intent.EXTRA_TEXT, message)
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            context.startActivity(
                Intent.createChooser(shareIntent, "Send Marks to Parents").addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            )
        }
    }

    fun sendReportViaSms(context: Context, phoneNumber: String, message: String) {
        try {
            val smsIntent = Intent(Intent.ACTION_SENDTO, Uri.parse("smsto:$phoneNumber")).apply {
                putExtra("sms_body", message)
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            context.startActivity(smsIntent)
        } catch (_: Exception) {
            val shareIntent = Intent(Intent.ACTION_SEND).apply {
                type = "text/plain"
                putExtra(Intent.EXTRA_TEXT, message)
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            context.startActivity(
                Intent.createChooser(shareIntent, "Send SMS to Parents").addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            )
        }
    }
}
