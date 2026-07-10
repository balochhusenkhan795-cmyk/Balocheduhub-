package com.example.data

import android.os.Build
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import com.example.BuildConfig
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.UUID

// --- Platform Central Data Repository ---
object Repository {

    // Current logged-in user
    val currentUser = MutableStateFlow(
        User(
            id = "user_student_1",
            name = "Aman Kumar",
            email = "aman.student@balocheduhub.com",
            role = UserRole.STUDENT,
            streak = 5,
            coins = 240,
            rank = 42,
            progress = mapOf("course_physics_1" to 35f, "course_biology_1" to 15f),
            badges = listOf("Curious Mind", "Streak Starter", "Math Explorer")
        )
    )

    // Dark/Light Mode Configuration
    val isDarkMode = MutableStateFlow(true)

    // Dynamic lists to simulate MongoDB collections
    val courses = mutableStateListOf<Course>()
    val chapters = mutableStateListOf<Chapter>()
    val videos = mutableStateListOf<Video>()
    val notes = mutableStateListOf<Note>()
    val tests = mutableStateListOf<Test>()
    val testResults = mutableStateListOf<TestResult>()
    val doubts = mutableStateListOf<Doubt>()
    val discussions = mutableStateListOf<DiscussionPost>()
    val notifications = mutableStateListOf<Notification>()
    val coupons = mutableStateListOf<Coupon>()

    // Local lists of leaderboard players
    val leaderboardUsers = listOf(
        User("l_1", "Rohan Gupta", "rohan@gmail.com", UserRole.STUDENT, streak = 15, coins = 850, rank = 1),
        User("l_2", "Isha Patel", "isha@gmail.com", UserRole.STUDENT, streak = 12, coins = 720, rank = 2),
        User("l_3", "Tanmay Sen", "tanmay@gmail.com", UserRole.STUDENT, streak = 10, coins = 640, rank = 3),
        User("l_4", "Priya Verma", "priya@gmail.com", UserRole.STUDENT, streak = 8, coins = 490, rank = 4),
        User("l_5", "Siddharth Jha", "sid@gmail.com", UserRole.STUDENT, streak = 7, coins = 420, rank = 5),
        User("user_student_1", "Aman Kumar (You)", "aman@gmail.com", UserRole.STUDENT, streak = 5, coins = 240, rank = 6)
    )

    init {
        loadPrepopulatedData()
    }

    private fun loadPrepopulatedData() {
        // --- 1. Populate Courses ---
        courses.addAll(
            listOf(
                Course(
                    id = "course_physics_1",
                    title = "JEE Ultimate Physics: Mechanics Mastery",
                    category = CourseCategory.JEE_ADVANCED,
                    subject = SubjectCategory.PHYSICS,
                    description = "Master Mechanics from basic kinematics to rigid body dynamics. Hand-crafted by Alok Pandey Sir (IIT Kharagpur Alumnus) specifically for JEE Main & Advanced aspirants. Includes daily practice questions, visualization notes, and high-quality homework sets.",
                    thumbnail = "ic_physics_card",
                    instructor = "Alok Pandey Sir",
                    price = 4999.0,
                    rating = 4.9f,
                    enrolledCount = 2840,
                    features = listOf("150+ Video Lectures", "Topic-wise DPP PDFs", "Mechanics Mock Test Arena", "1-on-1 AI Doubt Solver")
                ),
                Course(
                    id = "course_biology_1",
                    title = "NEET Absolute Biology: Human Physiology",
                    category = CourseCategory.NEET,
                    subject = SubjectCategory.BIOLOGY,
                    description = "Ace the highest-yielding section of NEET Biology. Comprehensive NCERT line-by-line analysis, interactive anatomy structures, and memory tips to easily secure 360/360 in NEET Biology. Taught by Swati Sharma, senior medical biologist.",
                    thumbnail = "ic_biology_card",
                    instructor = "Dr. Swati Sharma",
                    price = 2999.0,
                    rating = 4.8f,
                    enrolledCount = 4120,
                    features = listOf("NCERT Line-by-Line Decryption", "Mnemonics Cheat Sheets", "Daily Quizzes & Mock Arena", "Visual Lecture Notebooks")
                ),
                Course(
                    id = "course_chemistry_1",
                    title = "JEE & NEET Inorganic Chemistry: Periodic Properties",
                    category = CourseCategory.JEE_MAIN,
                    subject = SubjectCategory.CHEMISTRY,
                    description = "Eliminate rote learning in Inorganic Chemistry. Understand exceptions logically through chemical bonding principles. Perfect for both NEET and JEE Mains.",
                    thumbnail = "ic_chemistry_card",
                    instructor = "Dr. K. K. Verma",
                    price = 1999.0,
                    rating = 4.7f,
                    enrolledCount = 1890,
                    features = listOf("Logic-driven chemical bonding explanations", "PDF Cheat Sheets", "Inorganic Chapter practice tests")
                ),
                Course(
                    id = "course_math_1",
                    title = "JEE Main Calculus: Functions & Limits",
                    category = CourseCategory.JEE_MAIN,
                    subject = SubjectCategory.MATHEMATICS,
                    description = "Demystify graphs, limits, and continuity. Learn standard JEE-Main shortcuts and l'hopital rule variants to solve tough calculus questions in under 45 seconds.",
                    thumbnail = "ic_math_card",
                    instructor = "Sameer Chawla Sir",
                    price = 3499.0,
                    rating = 4.9f,
                    enrolledCount = 1560,
                    features = listOf("Shortcut Formula Cards", "PYQ Live Sessions", "High-speed Practice Drills")
                )
            )
        )

        // --- 2. Populate Chapters ---
        chapters.addAll(
            listOf(
                Chapter("chap_phys_1", "course_physics_1", "Kinematics in 1D & 2D", 1),
                Chapter("chap_phys_2", "course_physics_1", "Newton's Laws of Motion (NLM)", 2),
                Chapter("chap_bio_1", "course_biology_1", "Circulatory System & Fluids", 1),
                Chapter("chap_bio_2", "course_biology_1", "Nervous Coordination & Brain", 2),
                Chapter("chap_chem_1", "course_chemistry_1", "Atomic Structure & Periodicity", 1),
                Chapter("chap_math_1", "course_math_1", "Limits & Continuity Core", 1)
            )
        )

        // --- 3. Populate Videos ---
        videos.addAll(
            listOf(
                Video(
                    id = "vid_p1",
                    chapterId = "chap_phys_1",
                    title = "Kinematics 01: Projectile Motion & Equation of Trajectory",
                    videoUrl = "dQw4w9WgXcQ", // Simulated YouTube video ID
                    duration = "45:20",
                    noteSnippet = "Remember that vertical velocity becomes zero at the maximum height of a projectile. The time of flight is given by 2u sin(theta) / g.",
                    dppSnippet = "DPP-1 Mechanics: Solve Q1 to Q10 based on projectile ranges.",
                    views = 1250,
                    order = 1
                ),
                Video(
                    id = "vid_p2",
                    chapterId = "chap_phys_1",
                    title = "Kinematics 02: Relative Motion & Rain-Man Problems",
                    videoUrl = "dQw4w9WgXcQ",
                    duration = "52:10",
                    noteSnippet = "Relative velocity of A with respect to B is V_AB = V_A - V_B. Draw the vector diagram with B brought to rest.",
                    dppSnippet = "DPP-2 Rain-Man & River-Boat vector calculations.",
                    views = 980,
                    order = 2
                ),
                Video(
                    id = "vid_b1",
                    chapterId = "chap_bio_1",
                    title = "Physiology 01: Human Heart & Double Circulation",
                    videoUrl = "dQw4w9WgXcQ",
                    duration = "38:45",
                    noteSnippet = "Cardiac cycle lasts approximately 0.8 seconds. SA Node initiates the action potential, hence called the natural Pacemaker.",
                    dppSnippet = "DPP-1 Circulation: Diagram labelling and SA node functions.",
                    views = 2410,
                    order = 1
                ),
                Video(
                    id = "vid_c1",
                    chapterId = "chap_chem_1",
                    title = "Chemistry 01: Quantum Numbers & Aufbau Principle",
                    videoUrl = "dQw4w9WgXcQ",
                    duration = "40:15",
                    noteSnippet = "n represents size and energy shell, l represents shape of orbital, m represents spatial orientation.",
                    dppSnippet = "DPP-1 Chemistry: Quantum numbers verification rules.",
                    views = 1120,
                    order = 1
                ),
                Video(
                    id = "vid_m1",
                    chapterId = "chap_math_1",
                    title = "Calculus 01: Standard Limits & L'Hopital's Rule",
                    videoUrl = "dQw4w9WgXcQ",
                    duration = "55:30",
                    noteSnippet = "L'Hopital rule can only be applied directly in indeterminate forms of 0/0 or infinity/infinity.",
                    dppSnippet = "DPP-1 Calculus: Solve 15 limits using shortcuts.",
                    views = 840,
                    order = 1
                )
            )
        )

        // --- 4. Populate Notes ---
        notes.addAll(
            listOf(
                Note("note_p1", "chap_phys_1", "Kinematics Trajectory Formulae & Diagrams", 8),
                Note("note_p2", "chap_phys_2", "Free Body Diagram (FBD) Rules & Solved Examples", 12),
                Note("note_b1", "chap_bio_1", "Double Circulation Pathway Mind-Map", 6),
                Note("note_c1", "chap_chem_1", "Periodic Exceptions and Aufbau Deviation Notes", 10),
                Note("note_m1", "chap_math_1", "Calculus Standard Limit Formulas Sheet", 4)
            )
        )

        // --- 5. Populate Questions and Tests ---
        val physQuestions = listOf(
            Question(
                id = "q_p1",
                testId = "test_p1",
                text = "A projectile is thrown with an initial velocity u at an angle theta with the horizontal. What is the radius of curvature of its path at the highest point of its trajectory?",
                optionA = "u² sin²(theta) / g",
                optionB = "u² cos²(theta) / g",
                optionC = "u² / g",
                optionD = "u² cos(theta) / g",
                correctAnswer = 'B',
                explanation = "At the highest point, the velocity of the projectile is horizontal and equals v = u cos(theta). The acceleration at this point is vertically downward and equals g, which acts perpendicular to the velocity. The radius of curvature R is v² / a_perpendicular = (u cos(theta))² / g = u² cos²(theta) / g."
            ),
            Question(
                id = "q_p2",
                testId = "test_p1",
                text = "A block of mass m is placed on a smooth wedge of inclination theta. The wedge is accelerated horizontally with an acceleration 'a' so that the block remains stationary relative to the wedge. The value of 'a' is:",
                optionA = "g sin(theta)",
                optionB = "g cos(theta)",
                optionC = "g tan(theta)",
                optionD = "g / tan(theta)",
                correctAnswer = 'C',
                explanation = "In the accelerated reference frame of the wedge, a pseudo force 'ma' acts on the block horizontally opposite to the acceleration of the wedge. Resolving forces parallel to the inclined plane: mg sin(theta) = ma cos(theta) => a = g tan(theta)."
            ),
            Question(
                id = "q_p3",
                testId = "test_p1",
                text = "A body falls from rest under gravity with terminal drag resistance. If it travels a distance h in the first t seconds and then a distance 3h in the next t seconds under constant gravity (neglecting air drag for simplicity):",
                optionA = "h = 1/2 g t²",
                optionB = "h = 1/4 g t²",
                optionC = "h = 1/8 g t²",
                optionD = "Both acceleration and speed are constant",
                correctAnswer = 'A',
                explanation = "For a body starting from rest: S_1 = h = 1/2 g t². S_1 + S_2 = h + 3h = 4h = 1/2 g (2t)² = 4 * (1/2 g t²). This perfectly matches. Thus h = 1/2 g t²."
            )
        )

        val bioQuestions = listOf(
            Question(
                id = "q_b1",
                testId = "test_b1",
                text = "Which of the following is known as the natural pacemaker of the human heart?",
                optionA = "AV Node (Atrioventricular Node)",
                optionB = "SA Node (Sinoatrial Node)",
                optionC = "Purkinje Fibres",
                optionD = "Bundle of His",
                correctAnswer = 'B',
                explanation = "The SA (Sinoatrial) Node is located in the right upper corner of the right atrium. It generates action potentials rhythmically (70-75 per minute) and initiates cardiac contraction, which is why it is called the natural pacemaker."
            ),
            Question(
                id = "q_b2",
                testId = "test_b1",
                text = "What is the duration of a single cardiac cycle in a normal healthy human being?",
                optionA = "0.8 seconds",
                optionB = "1.2 seconds",
                optionC = "0.5 seconds",
                optionD = "1.0 seconds",
                correctAnswer = 'A',
                explanation = "A normal heart beats approximately 72 times per minute. Therefore, the duration of one cardiac cycle is 60 / 72 ≈ 0.8 seconds, containing auricular systole, ventricular systole, and joint diastole."
            ),
            Question(
                id = "q_b3",
                testId = "test_b1",
                text = "Which blood vessel carries oxygenated blood from the lungs back to the left atrium of the heart?",
                optionA = "Pulmonary Artery",
                optionB = "Aorta",
                optionC = "Pulmonary Vein",
                optionD = "Vena Cava",
                correctAnswer = 'C',
                explanation = "Pulmonary veins are the only veins in the human body that carry oxygenated blood. They transport oxygenated blood from the alveoli of the lungs to the left atrium of the heart."
            )
        )

        tests.addAll(
            listOf(
                Test("test_p1", "JEE Physics: Mechanics Chapter Mock", "course_physics_1", 10, 3, 12, physQuestions),
                Test("test_b1", "NEET Biology: Circulation Quick Mock", "course_biology_1", 10, 3, 12, bioQuestions)
            )
        )

        // --- 6. Populate Test Results ---
        testResults.addAll(
            listOf(
                TestResult(
                    id = "res_1",
                    testId = "test_p1",
                    testTitle = "JEE Physics: Mechanics Chapter Mock",
                    score = 8, // Correct: 2 (+8), Incorrect: 0, Unattempted: 1
                    totalQuestions = 3,
                    correctCount = 2,
                    incorrectCount = 0,
                    unattemptedCount = 1,
                    percentage = 66.7f,
                    rank = 12,
                    date = "2026-07-05"
                )
            )
        )

        // --- 7. Populate Doubts ---
        doubts.addAll(
            listOf(
                Doubt(
                    id = "doubt_1",
                    question = "Why does the value of acceleration due to gravity 'g' decrease as we go deep inside the Earth?",
                    response = "Inside the Earth at depth d, the mass of the outer shell of thickness d does not exert any net gravitational force at that point. Only the inner sphere of radius (R - d) attracts. Calculating this yields g_d = g * (1 - d/R). Thus, 'g' decreases linearly with depth.",
                    subject = "Physics",
                    timestamp = "Yesterday, 4:30 PM"
                )
            )
        )

        // --- 8. Populate Discussion Forum ---
        discussions.addAll(
            listOf(
                DiscussionPost(
                    id = "post_1",
                    authorName = "Aditya Sen",
                    authorRole = UserRole.STUDENT,
                    title = "Best strategy to solve JEE Organic Chemistry reaction mechanisms?",
                    content = "Hey guys, I am struggling to remember acid-catalyzed dehydration products. Do you recommend drawing carbo-cation intermediates every time or memorizing shortcuts?",
                    subject = SubjectCategory.CHEMISTRY,
                    likes = 14,
                    likedByMe = false,
                    replies = listOf(
                        DiscussionReply(
                            id = "rep_1",
                            authorName = "Dr. K. K. Verma",
                            authorRole = UserRole.TEACHER,
                            content = "Aditya, always draw the carbocation intermediate and check for any potential 1,2-hydride or 1,2-methyl shifts. Shortcuts will fail in JEE Advanced!",
                            timestamp = "3 hours ago"
                        ),
                        DiscussionReply(
                            id = "rep_2",
                            authorName = "Isha Patel",
                            authorRole = UserRole.STUDENT,
                            content = "Agreed! Rearrangements are super common in Advanced questions. Dr. Verma's advice is spot on.",
                            timestamp = "2 hours ago"
                        )
                    ),
                    timestamp = "5 hours ago"
                ),
                DiscussionPost(
                    id = "post_2",
                    authorName = "Rahul Roy",
                    authorRole = UserRole.STUDENT,
                    title = "How many cells are produced in spermatogenesis vs oogenesis?",
                    content = "In spermatogenesis, one primary spermatocyte produces 4 sperm. But in oogenesis, does one primary oocyte produce only 1 ovum? What happens to polar bodies?",
                    subject = SubjectCategory.BIOLOGY,
                    likes = 9,
                    likedByMe = true,
                    replies = listOf(
                        DiscussionReply(
                            id = "rep_3",
                            authorName = "Dr. Swati Sharma",
                            authorRole = UserRole.TEACHER,
                            content = "Yes, Rahul! Oogenesis is unequal. One primary oocyte yields 1 functional haploid ovum and 2 or 3 tiny non-functional polar bodies. This conserves cytoplasm for the future zygote.",
                            timestamp = "Yesterday"
                        )
                    ),
                    timestamp = "Yesterday"
                )
            )
        )

        // --- 9. Populate Notifications ---
        notifications.addAll(
            listOf(
                Notification("n_1", "Live Class Alert!", "Alok Sir is starting a Live Practice Session on 'Newton's Laws of Motion' in 15 minutes. Join now!", "Just Now", "Announcement"),
                Notification("n_2", "AI Doubt Bot Updated", "Our AI Doubt Assistant is now powered by Gemini 3.5 Flash for much faster science and math responses with step-by-step formatting.", "2 hours ago", "System"),
                Notification("n_3", "Weekly Leaderboard Out!", "Congratulations to Rohan Gupta for securing Rank 1 this week! Keep studying to climb the leaderboard.", "Yesterday", "Reward")
            )
        )

        // --- 10. Populate Coupons ---
        coupons.addAll(
            listOf(
                Coupon("NEET2026", 20, "20% Discount for NEET preparation courses", 500, 142),
                Coupon("JEEGOLD", 15, "15% discount for Premium JEE Advanced Mechanics", 200, 89),
                Coupon("FREEPREP", 100, "100% off selected Daily Practice Exams", 1000, 950)
            )
        )
    }

    // Toggle Role for demo
    fun switchUserRole(newRole: UserRole) {
        val current = currentUser.value
        val newName = when (newRole) {
            UserRole.STUDENT -> "Aman Kumar"
            UserRole.TEACHER -> "Prof. Swati Sharma"
            UserRole.ADMIN -> "Platform Admin"
        }
        val newEmail = when (newRole) {
            UserRole.STUDENT -> "aman.student@balocheduhub.com"
            UserRole.TEACHER -> "swati.teacher@balocheduhub.com"
            UserRole.ADMIN -> "admin@balocheduhub.com"
        }
        currentUser.value = current.copy(name = newName, email = newEmail, role = newRole)
    }

    // Purchase/Enroll course
    fun enrollInCourse(courseId: String) {
        val currentProgress = currentUser.value.progress.toMutableMap()
        if (!currentProgress.containsKey(courseId)) {
            currentProgress[courseId] = 0f
            currentUser.value = currentUser.value.copy(
                progress = currentProgress,
                coins = currentUser.value.coins - 50 // Earn coins or pay virtual
            )
        }
    }

    // Toggle Video bookmark
    fun toggleBookmarkVideo(videoId: String) {
        val current = currentUser.value
        val bookmarks = current.bookmarkedVideos.toMutableSet()
        if (bookmarks.contains(videoId)) {
            bookmarks.remove(videoId)
        } else {
            bookmarks.add(videoId)
        }
        currentUser.value = current.copy(bookmarkedVideos = bookmarks)
    }

    // Toggle Note bookmark
    fun toggleBookmarkNote(noteId: String) {
        val current = currentUser.value
        val bookmarks = current.bookmarkedNotes.toMutableSet()
        if (bookmarks.contains(noteId)) {
            bookmarks.remove(noteId)
        } else {
            bookmarks.add(noteId)
        }
        currentUser.value = current.copy(bookmarkedNotes = bookmarks)
    }

    // Daily quiz completed callback
    fun earnCoins(amount: Int) {
        currentUser.value = currentUser.value.copy(
            coins = currentUser.value.coins + amount,
            streak = currentUser.value.streak + 1
        )
    }

    // Ask AI Doubt with real Gemini integration
    suspend fun askDoubt(questionText: String, subject: String): Doubt {
        val defaultResponse = "I have processed your question about $subject. To give you a detailed breakdown: remember that the core formula for this topic is essential. Draw your diagrams clearly and double check standard assumptions."
        
        var answer = ""
        val apiKey = BuildConfig.GEMINI_API_KEY
        if (apiKey.isNotEmpty() && apiKey != "MY_GEMINI_API_KEY") {
            try {
                val prompt = """
                    You are a highly premium AI JEE & NEET Doubt Assistant.
                    Subject: $subject
                    Student's Question: $questionText
                    
                    Provide a professional, extremely detailed, step-by-step academic response suited for elite IIT-JEE and NEET aspirants. Use simple text formatting (e.g., bullet points, labeled steps). Do not use LaTeX symbols if they would break standard text displays, but represent mathematical equations clearly in plain text (e.g., instead of \frac{a}{b}, write a/b or use clear spacing). Be encouraging and end with a quick practice tip!
                """.trimIndent()

                val jsonBody = """
                    {
                        "contents": [
                            {
                                "parts": [
                                    { "text": ${JSONObject.quote(prompt)} }
                                ]
                            }
                        ],
                        "generationConfig": {
                            "temperature": 0.4
                        }
                    }
                """.trimIndent()

                val responseString = withContext(Dispatchers.IO) {
                    val client = OkHttpClient.Builder()
                        .connectTimeout(30, java.util.concurrent.TimeUnit.SECONDS)
                        .readTimeout(30, java.util.concurrent.TimeUnit.SECONDS)
                        .build()
                    val mediaType = "application/json; charset=utf-8".toMediaType()
                    val body = jsonBody.toRequestBody(mediaType)
                    val req = Request.Builder()
                        .url("https://generativelanguage.googleapis.com/v1beta/models/gemini-3.5-flash:generateContent?key=$apiKey")
                        .post(body)
                        .build()
                    client.newCall(req).execute().use { response ->
                        if (!response.isSuccessful) {
                            throw java.io.IOException("Network call failed: ${response.code} - ${response.message}")
                        }
                        response.body?.string() ?: ""
                    }
                }

                val jsonResponse = JSONObject(responseString)
                val candidates = jsonResponse.optJSONArray("candidates")
                val firstCandidate = candidates?.optJSONObject(0)
                val content = firstCandidate?.optJSONObject("content")
                val parts = content?.optJSONArray("parts")
                val firstPart = parts?.optJSONObject(0)
                val text = firstPart?.optString("text")

                answer = if (!text.isNullOrEmpty()) text else defaultResponse
            } catch (e: Exception) {
                answer = "Error fetching response from Gemini AI: ${e.localizedMessage}. Falling back to pre-loaded solver: \n\n$defaultResponse"
            }
        } else {
            // Fallback response with beautiful offline tutoring explanation
            answer = generateOfflineTutorAnswer(questionText, subject)
        }

        val formattedTime = SimpleDateFormat("h:mm a", Locale.getDefault()).format(Date())
        val newDoubt = Doubt(
            id = UUID.randomUUID().toString(),
            question = questionText,
            response = answer,
            subject = subject,
            timestamp = "Today, $formattedTime",
            isAiResolved = true
        )
        doubts.add(0, newDoubt)
        return newDoubt
    }

    private fun generateOfflineTutorAnswer(question: String, subject: String): String {
        val qLower = question.lowercase()
        return when {
            subject.equals("Physics", true) || qLower.contains("force") || qLower.contains("gravity") || qLower.contains("speed") -> {
                """
                    [Offline Educator Pro - Physics Specialist]
                    
                    Based on your question regarding Mechanics:
                    
                    Step 1: Identify all forces acting on the particle/body.
                    - Draw a clean Free Body Diagram (FBD).
                    - Isolate the system and mark gravitational force (mg) downwards.
                    - Mark contact forces: Normal reaction (N) perpendicular to the contact surfaces, and Friction (f = mu * N) opposing the relative motion.
                    
                    Step 2: Setup your coordinate axes.
                    - Choose one axis parallel to the direction of motion or acceleration to avoid complex trigonometric components.
                    - Resolve forces along your chosen X and Y axes.
                    
                    Step 3: Apply Newton's Second Law.
                    - Net Force (Sigma F_x) = mass * acceleration (m * a)
                    - Net Force (Sigma F_y) = 0 (if in equilibrium along the perpendicular axis)
                    
                    Step 4: Solve the simultaneous equations.
                    
                    *Pro Practice Tip:* Double-check if a pseudo force is required when solving the system from an accelerated (non-inertial) frame of reference. This saves significant time in JEE multiple-choice tests!
                """.trimIndent()
            }
            subject.equals("Biology", true) || qLower.contains("cell") || qLower.contains("heart") || qLower.contains("dna") -> {
                """
                    [Offline Educator Pro - NEET Biology Specialist]
                    
                    Let's break down this crucial NEET Biology topic:
                    
                    1. Core Anatomy/Pathway Concept:
                    - Ensure you correlate the structure directly with its biological function. For example, thin walls of alveoli/capillaries facilitate rapid diffusion of gases.
                    
                    2. NCERT Line-by-Line Decryption:
                    - Keep in mind that high-yield statements often contrast related terms (e.g., active vs passive transport, SA node vs AV node).
                    
                    3. Mnemonic for quick recall:
                    - Use structural mnemonics to recall complex sequences like mitotic phases (Prophase, Metaphase, Anaphase, Telophase -> PMAT).
                    
                    *High-Yield NEET Tip:* Every year, NEET asks at least 3 direct questions from human physiology pathway feedback loops. Make sure to draw diagrammatic flowcharts in your biology notebook!
                """.trimIndent()
            }
            else -> {
                """
                    [Offline Educator Pro - Master Academic Coach]
                    
                    Excellent NEET/JEE level query! Let's approach this scientifically:
                    
                    Step 1: Outline your standard equations or definitions. Write down what is given in the question and what needs to be evaluated.
                    
                    Step 2: Apply basic constraints and look for standard exceptions (super critical in inorganic chemistry and calculus limits!).
                    
                    Step 3: Eliminate options based on dimensions, extreme boundary conditions, or simple counter-examples to save precious minutes.
                    
                    *Practice Tip:* Consistently maintaining a 'mistake register' containing tricky questions is what separates top rankers from the rest. Good luck!
                """.trimIndent()
            }
        }
    }
}
