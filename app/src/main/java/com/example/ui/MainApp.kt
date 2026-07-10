package com.example.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import com.example.R
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.*
import com.example.ui.theme.MyApplicationTheme
import androidx.compose.foundation.BorderStroke
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.Date
import java.util.UUID
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun MainApp() {
    var isUserLoggedIn by remember { mutableStateOf(false) }
    var currentAuthMode by remember { mutableStateOf("LOGIN") } // "LOGIN", "REGISTER", "OTP"
    var authEmail by remember { mutableStateOf("") }
    var authPassword by remember { mutableStateOf("") }
    var authName by remember { mutableStateOf("") }
    var otpCode by remember { mutableStateOf("") }
    
    val currentUserState by Repository.currentUser.collectAsStateWithLifecycle()
    val isDarkTheme by Repository.isDarkMode.collectAsStateWithLifecycle()

    MyApplicationTheme(darkTheme = isDarkTheme) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
        ) {
            // Mesh Background Gradients for Frosted Glass theme
            Canvas(modifier = Modifier.fillMaxSize()) {
                // Top-left blue-600/20 glow (semi-translucent)
                drawCircle(
                    brush = Brush.radialGradient(
                        colors = listOf(
                            Color(0x333B82F6), // blue-500 @ 20% opacity
                            Color.Transparent
                        ),
                        center = androidx.compose.ui.geometry.Offset(-100f, -100f),
                        radius = size.width * 0.9f
                    )
                )
                // Center-right teal-500/10 glow (semi-translucent)
                drawCircle(
                    brush = Brush.radialGradient(
                        colors = listOf(
                            Color(0x1F14B8A6), // teal-500 @ 12% opacity
                            Color.Transparent
                        ),
                        center = androidx.compose.ui.geometry.Offset(size.width * 1.1f, size.height * 0.55f),
                        radius = size.width * 0.95f
                    )
                )
            }

            Surface(
                modifier = Modifier.fillMaxSize(),
                color = Color.Transparent
            ) {
                if (!isUserLoggedIn) {
                    // Immersive Auth Screen
                    AuthScreen(
                        mode = currentAuthMode,
                        email = authEmail,
                        password = authPassword,
                        name = authName,
                        otp = otpCode,
                        onEmailChange = { authEmail = it },
                        onPasswordChange = { authPassword = it },
                        onNameChange = { authName = it },
                        onOtpChange = { otpCode = it },
                        onAuthSubmit = {
                            if (currentAuthMode == "LOGIN") {
                                isUserLoggedIn = true
                            } else if (currentAuthMode == "REGISTER") {
                                currentAuthMode = "OTP"
                            } else if (currentAuthMode == "OTP") {
                                if (authName.isNotEmpty()) {
                                    Repository.currentUser.value = currentUserState.copy(name = authName, email = authEmail)
                                }
                                isUserLoggedIn = true
                            }
                        },
                        onSwitchMode = {
                            currentAuthMode = if (it == "LOGIN") "LOGIN" else "REGISTER"
                        }
                    )
                } else {
                    // Workspace Core
                    MainWorkspace(
                        onLogout = {
                            isUserLoggedIn = false
                            currentAuthMode = "LOGIN"
                            authEmail = ""
                            authPassword = ""
                            authName = ""
                            otpCode = ""
                        }
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AuthScreen(
    mode: String,
    email: String,
    password: String,
    name: String,
    otp: String,
    onEmailChange: (String) -> Unit,
    onPasswordChange: (String) -> Unit,
    onNameChange: (String) -> Unit,
    onOtpChange: (String) -> Unit,
    onAuthSubmit: () -> Unit,
    onSwitchMode: (String) -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth(0.9f)
                .padding(16.dp)
                .border(
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.surfaceVariant),
                    shape = RoundedCornerShape(24.dp)
                ),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Branding Icon
                Box(
                    modifier = Modifier
                        .size(64.dp)
                        .background(
                            Brush.radialGradient(
                                colors = listOf(Color(0xFF818CF8), Color(0xFF4F46E5))
                            ),
                            CircleShape
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_balocheduhub_logo),
                        contentDescription = "Balocheduhub Logo",
                        tint = Color.Unspecified,
                        modifier = Modifier.size(56.dp).clip(CircleShape)
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = if (mode == "LOGIN") "Welcome to Balocheduhub" else if (mode == "REGISTER") "Create Premium Account" else "OTP Verification Required",
                    style = MaterialTheme.typography.titleLarge,
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center
                )

                Text(
                    text = if (mode == "OTP") "We have simulated a secure 4-digit code to $email" else "Access video lectures, mock test series, and AI doubts",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color(0xFF94A3B8),
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(top = 8.dp, bottom = 24.dp)
                )

                if (mode == "REGISTER") {
                    OutlinedTextField(
                        value = name,
                        onValueChange = onNameChange,
                        label = { Text("Aspirant Name", color = Color(0xFF94A3B8)) },
                        leadingIcon = { Icon(Icons.Default.Person, contentDescription = "Name", tint = Color(0xFF818CF8)) },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Color(0xFF818CF8),
                            unfocusedBorderColor = Color(0xFF475569),
                            focusedLabelColor = Color(0xFF818CF8),
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("name_input"),
                        shape = RoundedCornerShape(12.dp)
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                }

                if (mode != "OTP") {
                    OutlinedTextField(
                        value = email,
                        onValueChange = onEmailChange,
                        label = { Text("Email Address", color = Color(0xFF94A3B8)) },
                        leadingIcon = { Icon(Icons.Default.Email, contentDescription = "Email", tint = Color(0xFF818CF8)) },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Color(0xFF818CF8),
                            unfocusedBorderColor = Color(0xFF475569),
                            focusedLabelColor = Color(0xFF818CF8),
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("email_input"),
                        shape = RoundedCornerShape(12.dp)
                    )
                    Spacer(modifier = Modifier.height(12.dp))

                    OutlinedTextField(
                        value = password,
                        onValueChange = onPasswordChange,
                        label = { Text("Secure Password", color = Color(0xFF94A3B8)) },
                        leadingIcon = { Icon(Icons.Default.Lock, contentDescription = "Password", tint = Color(0xFF818CF8)) },
                        visualTransformation = PasswordVisualTransformation(),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Color(0xFF818CF8),
                            unfocusedBorderColor = Color(0xFF475569),
                            focusedLabelColor = Color(0xFF818CF8),
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("password_input"),
                        shape = RoundedCornerShape(12.dp)
                    )
                } else {
                    OutlinedTextField(
                        value = otp,
                        onValueChange = onOtpChange,
                        label = { Text("Enter 4-Digit OTP (Try '1234')", color = Color(0xFF94A3B8)) },
                        leadingIcon = { Icon(Icons.Default.Pin, contentDescription = "OTP", tint = Color(0xFFF59E0B)) },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Color(0xFFF59E0B),
                            unfocusedBorderColor = Color(0xFF475569),
                            focusedLabelColor = Color(0xFFF59E0B),
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("otp_input"),
                        shape = RoundedCornerShape(12.dp)
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))

                Button(
                    onClick = onAuthSubmit,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (mode == "OTP") Color(0xFFF59E0B) else Color(0xFF4F46E5)
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp)
                        .testTag("auth_submit_btn"),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text(
                        text = if (mode == "LOGIN") "Enter Dashboard" else if (mode == "REGISTER") "Get Activation Code" else "Verify & Activate Portal",
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                if (mode != "OTP") {
                    TextButton(
                        onClick = {
                            if (mode == "LOGIN") onSwitchMode("REGISTER") else onSwitchMode("LOGIN")
                        }
                    ) {
                        Text(
                            text = if (mode == "LOGIN") "New Aspirant? Register Here" else "Already Registered? Login Now",
                            color = Color(0xFF818CF8),
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                } else {
                    TextButton(onClick = { onSwitchMode("LOGIN") }) {
                        Text("Back to Login", color = Color(0xFF94A3B8))
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainWorkspace(onLogout: () -> Unit) {
    val currentUserState by Repository.currentUser.collectAsStateWithLifecycle()
    val isDarkTheme by Repository.isDarkMode.collectAsStateWithLifecycle()
    
    // Bottom Tab State
    var currentTab by remember { mutableStateOf("HOME") } // "HOME", "LIBRARY", "MOCKS", "AI_DOUBTS", "FORUM"
    
    // Course Lecture Detail State
    var selectedCourseForDetails by remember { mutableStateOf<Course?>(null) }
    var activeStudyCourseId by remember { mutableStateOf<String?>(null) }
    var activePlayVideo by remember { mutableStateOf<Video?>(null) }
    var activeReadNote by remember { mutableStateOf<Note?>(null) }
    
    // Active Test State
    var activeTestExam by remember { mutableStateOf<Test?>(null) }
    var viewTestResult by remember { mutableStateOf<TestResult?>(null) }

    // Navigation Drawer or Top Notification State
    var showNotificationDrawer by remember { mutableStateOf(false) }

    Scaffold(
        containerColor = Color.Transparent,
        topBar = {
            TopAppBar(
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                    titleContentColor = MaterialTheme.colorScheme.onSurface
                ),
                title = {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column {
                            Text(
                                text = "NEET JEE Prep",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "Hi, ${currentUserState.name}",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                },
                actions = {
                    // Streak Badge
                    if (currentUserState.role == UserRole.STUDENT) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .padding(end = 8.dp)
                                .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.15f), RoundedCornerShape(12.dp))
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.LocalFireDepartment,
                                contentDescription = "Streak",
                                tint = Color(0xFFF59E0B),
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "${currentUserState.streak} Days",
                                style = MaterialTheme.typography.bodySmall,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                        
                        // Coin Wallet Badge
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .padding(end = 8.dp)
                                .background(Color(0xFFF59E0B).copy(alpha = 0.15f), RoundedCornerShape(12.dp))
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.MonetizationOn,
                                contentDescription = "Coins",
                                tint = Color(0xFFF59E0B),
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "${currentUserState.coins}",
                                style = MaterialTheme.typography.bodySmall,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }

                    // Notification Alert
                    IconButton(onClick = { showNotificationDrawer = !showNotificationDrawer }) {
                        BadgedBox(
                            badge = {
                                Badge { Text("${Repository.notifications.size}") }
                            }
                        ) {
                            Icon(Icons.Default.Notifications, contentDescription = "Notifications")
                        }
                    }

                    // Theme Toggle
                    IconButton(onClick = { Repository.isDarkMode.value = !isDarkTheme }) {
                        Icon(
                            imageVector = if (isDarkTheme) Icons.Default.LightMode else Icons.Default.DarkMode,
                            contentDescription = "Toggle Theme"
                        )
                    }

                    // Exit
                    IconButton(onClick = onLogout) {
                        Icon(Icons.Default.ExitToApp, contentDescription = "Logout")
                    }
                }
            )
        },
        bottomBar = {
            if (currentUserState.role == UserRole.STUDENT && activeTestExam == null) {
                NavigationBar(
                    containerColor = MaterialTheme.colorScheme.surface,
                    tonalElevation = 8.dp
                ) {
                    NavigationBarItem(
                        selected = currentTab == "HOME",
                        onClick = {
                            currentTab = "HOME"
                            selectedCourseForDetails = null
                            activeStudyCourseId = null
                            activePlayVideo = null
                            activeReadNote = null
                        },
                        icon = { Icon(Icons.Default.Home, contentDescription = "Home") },
                        label = { Text("Home", fontSize = 11.sp) }
                    )
                    NavigationBarItem(
                        selected = currentTab == "LIBRARY",
                        onClick = {
                            currentTab = "LIBRARY"
                            selectedCourseForDetails = null
                            activePlayVideo = null
                            activeReadNote = null
                        },
                        icon = { Icon(Icons.Default.LibraryBooks, contentDescription = "My Courses") },
                        label = { Text("My Prep", fontSize = 11.sp) }
                    )
                    NavigationBarItem(
                        selected = currentTab == "MOCKS",
                        onClick = {
                            currentTab = "MOCKS"
                            selectedCourseForDetails = null
                        },
                        icon = { Icon(Icons.Default.Quiz, contentDescription = "Tests") },
                        label = { Text("Tests", fontSize = 11.sp) }
                    )
                    NavigationBarItem(
                        selected = currentTab == "AI_DOUBTS",
                        onClick = { currentTab = "AI_DOUBTS" },
                        icon = { Icon(Icons.Default.SmartToy, contentDescription = "AI Assistant") },
                        label = { Text("AI Doubt", fontSize = 11.sp) }
                    )
                    NavigationBarItem(
                        selected = currentTab == "FORUM",
                        onClick = { currentTab = "FORUM" },
                        icon = { Icon(Icons.Default.Forum, contentDescription = "Forum") },
                        label = { Text("Forum", fontSize = 11.sp) }
                    )
                }
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            Column(modifier = Modifier.fillMaxSize()) {
                // Persistent Role Selector Tab (Top-Level Switching)
                RoleSwitcherHeader(currentUserState.role) { newRole ->
                    Repository.switchUserRole(newRole)
                    selectedCourseForDetails = null
                    activeStudyCourseId = null
                    activePlayVideo = null
                    activeReadNote = null
                    activeTestExam = null
                }

                // Render respective views based on Role
                when (currentUserState.role) {
                    UserRole.STUDENT -> {
                        when {
                            activeTestExam != null -> {
                                LiveTestScreen(
                                    test = activeTestExam!!,
                                    onExamSubmitted = { result ->
                                        activeTestExam = null
                                        viewTestResult = result
                                    },
                                    onBackClicked = { activeTestExam = null }
                                )
                            }
                            viewTestResult != null -> {
                                TestResultScreen(
                                    result = viewTestResult!!,
                                    test = Repository.tests.first { it.id == viewTestResult!!.testId },
                                    onDismiss = { viewTestResult = null }
                                )
                            }
                            activePlayVideo != null -> {
                                VideoPlayerScreen(
                                    video = activePlayVideo!!,
                                    allVideosInChapter = Repository.videos.filter { it.chapterId == activePlayVideo!!.chapterId },
                                    onVideoSelected = { activePlayVideo = it },
                                    onBackClicked = { activePlayVideo = null }
                                )
                            }
                            activeReadNote != null -> {
                                NoteViewerScreen(
                                    note = activeReadNote!!,
                                    onBackClicked = { activeReadNote = null }
                                )
                            }
                            activeStudyCourseId != null -> {
                                val currentCourse = Repository.courses.first { it.id == activeStudyCourseId }
                                StudyPortalScreen(
                                    course = currentCourse,
                                    onLaunchVideo = { video -> activePlayVideo = video },
                                    onLaunchNote = { note -> activeReadNote = note },
                                    onBackClicked = { activeStudyCourseId = null }
                                )
                            }
                            selectedCourseForDetails != null -> {
                                CourseDetailScreen(
                                    course = selectedCourseForDetails!!,
                                    isEnrolled = currentUserState.progress.containsKey(selectedCourseForDetails!!.id),
                                    onEnrollClicked = { course ->
                                        Repository.enrollInCourse(course.id)
                                    },
                                    onStudyClicked = { course ->
                                        activeStudyCourseId = course.id
                                        currentTab = "LIBRARY"
                                    },
                                    onBackClicked = { selectedCourseForDetails = null }
                                )
                            }
                            else -> {
                                when (currentTab) {
                                    "HOME" -> HomeScreen(
                                        onCourseSelected = { selectedCourseForDetails = it }
                                    )
                                    "LIBRARY" -> MyLibraryScreen(
                                        enrolledProgress = currentUserState.progress,
                                        onCourseSelected = { activeStudyCourseId = it.id }
                                    )
                                    "MOCKS" -> MockTestLobbyScreen(
                                        testsList = Repository.tests,
                                        resultsList = Repository.testResults,
                                        onStartTest = { activeTestExam = it }
                                    )
                                    "AI_DOUBTS" -> AiDoubtAssistantScreen()
                                    "FORUM" -> DiscussionForumScreen()
                                }
                            }
                        }
                    }
                    UserRole.TEACHER -> {
                        TeacherPanelScreen()
                    }
                    UserRole.ADMIN -> {
                        AdminPanelScreen()
                    }
                }
            }

            // Notification Drawer Side Overlay
            if (showNotificationDrawer) {
                Dialog(onDismissRequest = { showNotificationDrawer = false }) {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .fillMaxHeight(0.7f),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "System Alerts",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold
                                )
                                IconButton(onClick = { showNotificationDrawer = false }) {
                                    Icon(Icons.Default.Close, contentDescription = "Close")
                                }
                            }

                            Divider(modifier = Modifier.padding(vertical = 8.dp))

                            LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                items(Repository.notifications) { notif ->
                                    Card(
                                        modifier = Modifier.fillMaxWidth(),
                                        colors = CardDefaults.cardColors(
                                            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                                        )
                                    ) {
                                        Column(modifier = Modifier.padding(12.dp)) {
                                            Row(
                                                modifier = Modifier.fillMaxWidth(),
                                                horizontalArrangement = Arrangement.SpaceBetween
                                            ) {
                                                Text(
                                                    text = notif.title,
                                                    style = MaterialTheme.typography.bodyMedium,
                                                    fontWeight = FontWeight.Bold,
                                                    color = MaterialTheme.colorScheme.primary
                                                )
                                                Text(
                                                    text = notif.timestamp,
                                                    style = MaterialTheme.typography.bodySmall,
                                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                                )
                                            }
                                            Spacer(modifier = Modifier.height(4.dp))
                                            Text(
                                                text = notif.message,
                                                style = MaterialTheme.typography.bodySmall,
                                                color = MaterialTheme.colorScheme.onSurface
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
}

@Composable
fun RoleSwitcherHeader(currentRole: UserRole, onRoleChanged: (UserRole) -> Unit) {
    Surface(
        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "SWITCH WORKSPACE PREVIEW:",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.sp
            )
            Spacer(modifier = Modifier.height(4.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                UserRole.values().forEach { role ->
                    val isSelected = currentRole == role
                    val bg = if (isSelected) MaterialTheme.colorScheme.primary else Color.Transparent
                    val tc = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurfaceVariant
                    
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .background(bg)
                            .clickable { onRoleChanged(role) }
                            .padding(horizontal = 12.dp, vertical = 6.dp)
                            .testTag("role_pill_${role.name}"),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = when(role) {
                                UserRole.STUDENT -> "🎓 Student"
                                UserRole.TEACHER -> "👨‍🏫 Teacher"
                                UserRole.ADMIN -> "⚙️ Admin"
                            },
                            style = MaterialTheme.typography.bodySmall,
                            fontWeight = FontWeight.Bold,
                            color = tc
                        )
                    }
                }
            }
        }
    }
}

// --- STUDENT HOMEPAGE COMPOSABLE ---
@Composable
fun HomeScreen(onCourseSelected: (Course) -> Unit) {
    var selectedSubjectFilter by remember { mutableStateOf<SubjectCategory?>(null) }
    var selectedCategoryFilter by remember { mutableStateOf<CourseCategory?>(null) }
    
    // Streak Quiz helper states
    var showStreakQuizDialog by remember { mutableStateOf(false) }
    var quizStep by remember { mutableStateOf(1) } // 1 or 2
    var quizCompleted by remember { mutableStateOf(false) }
    var selectedQuizOption by remember { mutableStateOf<Char?>(null) }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Hero Banner
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(
                        border = BorderStroke(1.dp, Color(0x22FFFFFF)),
                        shape = RoundedCornerShape(24.dp)
                    ),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = Color.Transparent)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            Brush.linearGradient(
                                colors = listOf(Color(0x3D2563EB), Color(0x1F4F46E5))
                            )
                        )
                        .padding(20.dp)
                ) {
                    Column {
                        Text(
                            text = "BALOCHEDUHUB PREMIUM BATCHES",
                            style = MaterialTheme.typography.labelMedium,
                            color = Color(0xFFFBBF24),
                            fontWeight = FontWeight.ExtraBold
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Target Rank 1 in 2027",
                            style = MaterialTheme.typography.headlineSmall,
                            color = Color.White,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Learn from top medical & IIT faculty with structured schedules, instant AI doubt clearing, and curated mock test series.",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color(0xFFE2E8F0),
                            modifier = Modifier.padding(vertical = 8.dp)
                        )
                        
                        Button(
                            onClick = { showStreakQuizDialog = true },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFF59E0B)),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.Bolt, contentDescription = "Quiz", tint = Color.Black)
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Take Quick Daily Quiz (+20 coins)", color = Color.Black, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }
        }

        // Category Filters Row
        item {
            Column {
                Text(
                    text = "Aspirant Stream",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(8.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    CourseCategory.values().forEach { cat ->
                        val isSelected = selectedCategoryFilter == cat
                        FilterChip(
                            selected = isSelected,
                            onClick = { selectedCategoryFilter = if (isSelected) null else cat },
                            label = {
                                Text(
                                    when(cat) {
                                        CourseCategory.NEET -> "NEET 🩺"
                                        CourseCategory.JEE_MAIN -> "JEE Main 🚀"
                                        CourseCategory.JEE_ADVANCED -> "JEE Advanced 🏆"
                                    }
                                )
                            }
                        )
                    }
                }
            }
        }

        // Subject Quick Filters
        item {
            Column {
                Text(
                    text = "Search by Subject",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(8.dp))
                LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(SubjectCategory.values()) { sub ->
                        val isSelected = selectedSubjectFilter == sub
                        val tintColor = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant
                        val textCol = if (isSelected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurface
                        
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(12.dp))
                                .background(tintColor)
                                .clickable { selectedSubjectFilter = if (isSelected) null else sub }
                                .padding(horizontal = 16.dp, vertical = 10.dp)
                                .border(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.3f), RoundedCornerShape(12.dp)),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = when(sub) {
                                    SubjectCategory.PHYSICS -> "⚛️ Physics"
                                    SubjectCategory.CHEMISTRY -> "🧪 Chemistry"
                                    SubjectCategory.BIOLOGY -> "🧬 Biology"
                                    SubjectCategory.MATHEMATICS -> "🧮 Math"
                                },
                                style = MaterialTheme.typography.bodySmall,
                                fontWeight = FontWeight.SemiBold,
                                color = textCol
                            )
                        }
                    }
                }
            }
        }

        // Dynamic Courses Feed
        item {
            Text(
                text = "Featured Courses",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(top = 8.dp)
            )
        }

        val filteredCourses = Repository.courses.filter { course ->
            (selectedSubjectFilter == null || course.subject == selectedSubjectFilter) &&
            (selectedCategoryFilter == null || course.category == selectedCategoryFilter)
        }

        if (filteredCourses.isEmpty()) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text("No active courses found for these filters. Try switching roles or categories.", color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
        } else {
            items(filteredCourses) { course ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onCourseSelected(course) },
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.surfaceVariant)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            // Subject tag
                            Text(
                                text = course.subject.name,
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.primary,
                                fontWeight = FontWeight.Bold
                            )
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.Star, contentDescription = "rating", tint = Color(0xFFF59E0B), modifier = Modifier.size(14.dp))
                                Text(text = "${course.rating}", style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.Bold)
                            }
                        }

                        Spacer(modifier = Modifier.height(4.dp))

                        Text(
                            text = course.title,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis
                        )

                        Text(
                            text = "by ${course.instructor}",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            text = course.description,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = if (course.price == 0.0) "FREE" else "₹${course.price.toInt()}",
                                style = MaterialTheme.typography.titleMedium,
                                color = Color(0xFF10B981),
                                fontWeight = FontWeight.Bold
                            )
                            Button(
                                onClick = { onCourseSelected(course) },
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Text("Explore", fontSize = 12.sp)
                            }
                        }
                    }
                }
            }
        }

        // FAQs Section
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 16.dp)
            ) {
                Text(
                    text = "Frequently Asked Questions",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(8.dp))

                FAQItem("Is the AI Doubt Assistant powered by Gemini?", "Yes! Our AI doubts assistant queries real-time Gemini 3.5 Flash models to provide accurate, comprehensive scientific breakdowns and formulas.")
                FAQItem("How are the offline test marking computed?", "We simulate the standard NEET & JEE markings. You earn +4 marks for correct answers, -1 mark for incorrect answers, and 0 for unattempted questions, complete with live rankings.")
                FAQItem("Where can I find my DPP notes?", "Every video lecture has high-quality theory and Practice (DPP) notes uploaded below it in your library portal.")
            }
        }
    }

    // Daily Streak Quiz Dialog
    if (showStreakQuizDialog) {
        Dialog(onDismissRequest = { showStreakQuizDialog = false }) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "⚡ Daily Streak Builder",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFFF59E0B)
                    )
                    Spacer(modifier = Modifier.height(12.dp))

                    if (!quizCompleted) {
                        Text(
                            text = if (quizStep == 1) "Q1 (Physics): Which physical quantity remains constant in uniform circular motion?" else "Q2 (Chemistry): What is the oxidation state of Oxygen in OF₂?",
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Bold,
                            textAlign = TextAlign.Center
                        )
                        Spacer(modifier = Modifier.height(16.dp))

                        if (quizStep == 1) {
                            QuizOption('A', "Velocity") { selectedQuizOption = 'A' }
                            QuizOption('B', "Speed") { selectedQuizOption = 'B' }
                            QuizOption('C', "Acceleration") { selectedQuizOption = 'C' }
                            QuizOption('D', "Displacement") { selectedQuizOption = 'D' }
                        } else {
                            QuizOption('A', "-2") { selectedQuizOption = 'A' }
                            QuizOption('B', "-1") { selectedQuizOption = 'B' }
                            QuizOption('C', "+2") { selectedQuizOption = 'C' }
                            QuizOption('D', "+1") { selectedQuizOption = 'D' }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        Button(
                            onClick = {
                                if (quizStep == 1) {
                                    quizStep = 2
                                    selectedQuizOption = null
                                } else {
                                    quizCompleted = true
                                    Repository.earnCoins(20)
                                }
                            },
                            enabled = selectedQuizOption != null,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(if (quizStep == 1) "Next Question" else "Submit Answers")
                        }
                    } else {
                        Icon(Icons.Default.Celebration, contentDescription = "Done", tint = Color(0xFF10B981), modifier = Modifier.size(64.dp))
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = "Awesome Job!",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "You earned +20 Coins and extended your prep streak to ${Repository.currentUser.value.streak} days!",
                            style = MaterialTheme.typography.bodySmall,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.padding(vertical = 8.dp)
                        )
                        Button(
                            onClick = {
                                showStreakQuizDialog = false
                                quizStep = 1
                                quizCompleted = false
                                selectedQuizOption = null
                            },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("Keep Studying")
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun QuizOption(label: Char, text: String, onSelect: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
            .clickable { onSelect() },
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.3f))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "$label. ",
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
            Text(text = text, style = MaterialTheme.typography.bodySmall)
        }
    }
}

@Composable
fun FAQItem(question: String, answer: String) {
    var isExpanded by remember { mutableStateOf(false) }
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
            .clickable { isExpanded = !isExpanded },
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.15f))
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = question,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.fillMaxWidth(0.9f)
                )
                Icon(
                    imageVector = if (isExpanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                    contentDescription = "Expand FAQ"
                )
            }
            if (isExpanded) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = answer,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

// --- COURSE DETAILS DIALOG ---
@Composable
fun CourseDetailScreen(
    course: Course,
    isEnrolled: Boolean,
    onEnrollClicked: (Course) -> Unit,
    onStudyClicked: (Course) -> Unit,
    onBackClicked: () -> Unit
) {
    var activePaymentDialog by remember { mutableStateOf(false) }
    var couponText by remember { mutableStateOf("") }
    var discountedPrice by remember { mutableStateOf(course.price) }
    var appliedCouponDescription by remember { mutableStateOf("") }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            IconButton(onClick = onBackClicked) {
                Icon(Icons.Default.ArrowBack, contentDescription = "Back")
            }
        }

        // Details Card
        item {
            Card(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = course.category.name.replace("_", " "),
                        style = MaterialTheme.typography.labelSmall,
                        color = Color(0xFFF59E0B),
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = course.title,
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Instructor: ${course.instructor}",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = course.description,
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
        }

        item {
            Text(
                text = "Key Features Offered",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
        }

        items(course.features) { feat ->
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
            ) {
                Row(
                    modifier = Modifier.padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Default.CheckCircle, contentDescription = "Feature", tint = Color(0xFF10B981))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(text = feat, style = MaterialTheme.typography.bodyMedium)
                }
            }
        }

        item {
            Divider(modifier = Modifier.padding(vertical = 8.dp))
            if (isEnrolled) {
                Button(
                    onClick = { onStudyClicked(course) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF10B981))
                ) {
                    Text("Enrolled! Enter Study Workspace", fontWeight = FontWeight.Bold)
                }
            } else {
                Button(
                    onClick = { activePaymentDialog = true },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp)
                ) {
                    Text("Enroll Now (₹${course.price.toInt()})", fontWeight = FontWeight.Bold)
                }
            }
        }
    }

    // Payment Sandbox Dialog
    if (activePaymentDialog) {
        Dialog(onDismissRequest = { activePaymentDialog = false }) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text("Secure Gateway Integration", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = "Course: ${course.title}",
                        style = MaterialTheme.typography.bodyMedium,
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = "Total Price: ₹${discountedPrice.toInt()}",
                        style = MaterialTheme.typography.titleLarge,
                        color = Color(0xFF10B981),
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    // Coupon Input
                    OutlinedTextField(
                        value = couponText,
                        onValueChange = { couponText = it },
                        label = { Text("Have coupon? (e.g., NEET2026)") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Button(
                        onClick = {
                            val activeCoupon = Repository.coupons.firstOrNull { it.code.equals(couponText.trim(), true) }
                            if (activeCoupon != null) {
                                val discount = (course.price * activeCoupon.discountPercent) / 100
                                discountedPrice = course.price - discount
                                appliedCouponDescription = "Applied! ${activeCoupon.discountPercent}% off via ${activeCoupon.code}"
                            } else {
                                appliedCouponDescription = "Invalid Coupon Code!"
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Apply Code")
                    }

                    if (appliedCouponDescription.isNotEmpty()) {
                        Text(
                            text = appliedCouponDescription,
                            style = MaterialTheme.typography.bodySmall,
                            color = if (appliedCouponDescription.startsWith("Applied")) Color(0xFF10B981) else Color(0xFFEF4444),
                            modifier = Modifier.padding(vertical = 4.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Payment Buttons
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Button(
                            onClick = {
                                onEnrollClicked(course)
                                activePaymentDialog = false
                            },
                            modifier = Modifier.weight(1f),
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF10B981))
                        ) {
                            Text("Simulate Pay")
                        }
                        Button(
                            onClick = { activePaymentDialog = false },
                            modifier = Modifier.weight(1f),
                            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                        ) {
                            Text("Cancel")
                        }
                    }
                }
            }
        }
    }
}

// --- MY LIBRARY / ENROLLED COURSES ---
@Composable
fun MyLibraryScreen(enrolledProgress: Map<String, Float>, onCourseSelected: (Course) -> Unit) {
    val enrolledCoursesList = Repository.courses.filter { enrolledProgress.containsKey(it.id) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = "My Enrolled Batches",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = "Track your learning progress and resume your studies",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        if (enrolledCoursesList.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(24.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(Icons.Default.AutoStories, contentDescription = "Empty", modifier = Modifier.size(64.dp), tint = MaterialTheme.colorScheme.outline)
                    Spacer(modifier = Modifier.height(12.dp))
                    Text("You have not enrolled in any batches yet.", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                    Text("Explore the catalog on the Home screen to begin.", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
        } else {
            LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                items(enrolledCoursesList) { course ->
                    val progressVal = enrolledProgress[course.id] ?: 0f
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onCourseSelected(course) },
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.surfaceVariant)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(
                                text = course.subject.name,
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.primary,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = course.title,
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "Instructor: ${course.instructor}",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )

                            Spacer(modifier = Modifier.height(12.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = "Syllabus Progress",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                Text(
                                    text = "${progressVal.toInt()}%",
                                    style = MaterialTheme.typography.bodySmall,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.primary
                                )
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            LinearProgressIndicator(
                                progress = { progressVal / 100f },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(6.dp)
                                    .clip(CircleShape)
                            )
                        }
                    }
                }
            }
        }
    }
}

// --- STUDY PORTAL SCREEN ---
@Composable
fun StudyPortalScreen(
    course: Course,
    onLaunchVideo: (Video) -> Unit,
    onLaunchNote: (Note) -> Unit,
    onBackClicked: () -> Unit
) {
    var selectedPortalTab by remember { mutableStateOf("VIDEOS") } // "VIDEOS", "NOTES"
    val courseChapters = Repository.chapters.filter { it.courseId == course.id }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            IconButton(onClick = onBackClicked) {
                Icon(Icons.Default.ArrowBack, contentDescription = "Back")
            }
            Text(
                text = course.title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Tabs Row
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Button(
                onClick = { selectedPortalTab = "VIDEOS" },
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (selectedPortalTab == "VIDEOS") MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant
                ),
                modifier = Modifier.weight(1f)
            ) {
                Text("Video Lectures")
            }
            Button(
                onClick = { selectedPortalTab = "NOTES" },
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (selectedPortalTab == "NOTES") MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant
                ),
                modifier = Modifier.weight(1f)
            ) {
                Text("Theory & DPPs")
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        if (courseChapters.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(24.dp),
                contentAlignment = Alignment.Center
            ) {
                Text("Lectures are currently being scheduled by the faculty. Check back soon!")
            }
        } else {
            LazyColumn(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                items(courseChapters) { chapter ->
                    Column(modifier = Modifier.fillMaxWidth()) {
                        Text(
                            text = "Chapter ${chapter.order}: ${chapter.title}",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Spacer(modifier = Modifier.height(8.dp))

                        if (selectedPortalTab == "VIDEOS") {
                            val chapterVideos = Repository.videos.filter { it.chapterId == chapter.id }
                            if (chapterVideos.isEmpty()) {
                                Text("No videos uploaded for this chapter.", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            } else {
                                chapterVideos.forEach { vid ->
                                    Card(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(vertical = 4.dp)
                                            .clickable { onLaunchVideo(vid) },
                                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                                    ) {
                                        Row(
                                            modifier = Modifier.padding(12.dp),
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Icon(Icons.Default.PlayCircle, contentDescription = "Play", tint = Color(0xFFF59E0B), modifier = Modifier.size(36.dp))
                                            Spacer(modifier = Modifier.width(12.dp))
                                            Column {
                                                Text(text = vid.title, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold)
                                                Text(text = "Duration: ${vid.duration}", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                            }
                                        }
                                    }
                                }
                            }
                        } else {
                            val chapterNotes = Repository.notes.filter { it.chapterId == chapter.id }
                            if (chapterNotes.isEmpty()) {
                                Text("No PDF notes uploaded for this chapter.", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            } else {
                                chapterNotes.forEach { note ->
                                    Card(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(vertical = 4.dp)
                                            .clickable { onLaunchNote(note) },
                                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                                    ) {
                                        Row(
                                            modifier = Modifier.padding(12.dp),
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Icon(Icons.Default.Description, contentDescription = "Doc", tint = Color(0xFF5A67D8), modifier = Modifier.size(36.dp))
                                            Spacer(modifier = Modifier.width(12.dp))
                                            Column {
                                                Text(text = note.title, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold)
                                                Text(text = "Syllabus Notes • ${note.pages} pages", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
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
    }
}

// --- VIDEO PLAYER COMPOSABLE ---
@Composable
fun VideoPlayerScreen(
    video: Video,
    allVideosInChapter: List<Video>,
    onVideoSelected: (Video) -> Unit,
    onBackClicked: () -> Unit
) {
    var playbackSpeed by remember { mutableStateOf(1.0f) }
    var resumeProgress by remember { mutableStateOf(0.35f) }
    var isPlaying by remember { mutableStateOf(true) }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                IconButton(onClick = onBackClicked) {
                    Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                }
                Text(
                    text = "Active Prep Lecture",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        // Animated Immersive Player Window
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = Color.Black)
            ) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            imageVector = if (isPlaying) Icons.Default.PauseCircle else Icons.Default.PlayCircle,
                            contentDescription = "Playback state",
                            tint = Color.White,
                            modifier = Modifier
                                .size(64.dp)
                                .clickable { isPlaying = !isPlaying }
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = "Streaming: ${video.title}",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color.LightGray,
                            textAlign = TextAlign.Center
                        )
                    }

                    // Progress indicators
                    LinearProgressIndicator(
                        progress = { resumeProgress },
                        color = Color(0xFFF59E0B),
                        modifier = Modifier
                            .fillMaxWidth()
                            .align(Alignment.BottomCenter)
                    )
                }
            }
        }

        // Lecture Controls
        item {
            Card(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(text = "Playback Controller", style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Playback Speed", style = MaterialTheme.typography.bodySmall)
                        Row {
                            listOf(1.0f, 1.25f, 1.5f, 2.0f).forEach { speed ->
                                Button(
                                    onClick = { playbackSpeed = speed },
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = if (playbackSpeed == speed) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant
                                    ),
                                    modifier = Modifier.padding(horizontal = 4.dp),
                                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp)
                                ) {
                                    Text("${speed}x", fontSize = 10.sp)
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Resume Watching", style = MaterialTheme.typography.bodySmall)
                        Slider(
                            value = resumeProgress,
                            onValueChange = { resumeProgress = it },
                            modifier = Modifier.fillMaxWidth(0.7f)
                        )
                    }
                }
            }
        }

        // Notes and Related Materials below Video
        item {
            Card(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(text = "📚 Essential Formulas & Notes", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = video.noteSnippet.ifEmpty { "Important notes from Swati Sharma. Understand molecular bonding exceptions, draw FBD diagrams and memorize core formula guidelines." },
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(text = "📝 Daily Practice Problem (DPP)", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold, color = Color(0xFFF59E0B))
                    Text(
                        text = video.dppSnippet.ifEmpty { "Homework Challenge: Solve practice questions 1-15 on mechanics and periodic traits from Swati Ma'am's worksheet." },
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }

        // Related Videos list below
        item {
            Text(text = "Related Lectures in Chapter", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
        }

        items(allVideosInChapter) { otherVid ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onVideoSelected(otherVid) },
                colors = CardDefaults.cardColors(
                    containerColor = if (otherVid.id == video.id) MaterialTheme.colorScheme.primary.copy(alpha = 0.15f) else MaterialTheme.colorScheme.surfaceVariant
                )
            ) {
                Row(
                    modifier = Modifier.padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Default.PlayArrow, contentDescription = "Lecture")
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(text = otherVid.title, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold)
                        Text(text = "Duration: ${otherVid.duration}", style = MaterialTheme.typography.bodySmall)
                    }
                }
            }
        }
    }
}

// --- STUDY NOTES / PDF VIEWER SCREEN ---
@Composable
fun NoteViewerScreen(note: Note, onBackClicked: () -> Unit) {
    var currentPage by remember { mutableStateOf(1) }
    val totalPages = note.pages
    var isBookmarked by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBackClicked) {
                Icon(Icons.Default.ArrowBack, contentDescription = "Back")
            }
            Text(
                text = note.title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.weight(1f)
            )
            IconButton(onClick = { isBookmarked = !isBookmarked }) {
                Icon(
                    imageVector = if (isBookmarked) Icons.Default.Bookmark else Icons.Default.BookmarkBorder,
                    contentDescription = "Bookmark",
                    tint = if (isBookmarked) Color(0xFFF59E0B) else MaterialTheme.colorScheme.onSurface
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Simulated PDF Viewer Card
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                // Header
                Text(
                    text = "CHAPTER FORMULAS • PAGE $currentPage OF $totalPages",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Bold
                )

                // Body content
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "CONCEPT MATRIX SUMMARY",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = if (currentPage == 1) {
                            "Kinematics Equation Rules:\n1. v = u + at\n2. s = ut + 0.5at²\n3. v² = u² + 2as\n\nThese equations can only be applied when the acceleration is strictly constant in both magnitude and direction."
                        } else if (currentPage == 2) {
                            "Anatomy Pathway Rules:\n- Right atrium receives deoxygenated blood via Vena Cava.\n- Right ventricle pumps it to lungs via Pulmonary Artery.\n- Left atrium receives oxygenated blood via Pulmonary Veins."
                        } else {
                            "Chapter Summary notes and expert guidelines.\n\nRead the NCERT textbook line-by-line twice before attempting multiple-choice questions. High scoring pathways lie in exceptional configurations!"
                        },
                        style = MaterialTheme.typography.bodyMedium,
                        textAlign = TextAlign.Center,
                        lineHeight = 22.sp
                    )
                }

                // Page Actions
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Button(
                        onClick = { if (currentPage > 1) currentPage-- },
                        enabled = currentPage > 1
                    ) {
                        Text("Prev")
                    }
                    Text("$currentPage / $totalPages", fontWeight = FontWeight.Bold)
                    Button(
                        onClick = { if (currentPage < totalPages) currentPage++ },
                        enabled = currentPage < totalPages
                    ) {
                        Text("Next")
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = { /* simulated download trigger */ },
            modifier = Modifier.fillMaxWidth()
        ) {
            Icon(Icons.Default.Download, contentDescription = "Download")
            Spacer(modifier = Modifier.width(8.dp))
            Text("Download Offline PDF Note")
        }
    }
}

// --- MOCK TEST SERIES TAB LOBBY ---
@Composable
fun MockTestLobbyScreen(
    testsList: List<Test>,
    resultsList: List<TestResult>,
    onStartTest: (Test) -> Unit
) {
    var activeTestLobbyTab by remember { mutableStateOf("AVAILABLE") } // "AVAILABLE", "RESULTS"

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = "Practice Mock Test Arena",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = "Test your skills under real exam timers and marking rules",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(modifier = Modifier.height(16.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Button(
                onClick = { activeTestLobbyTab = "AVAILABLE" },
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (activeTestLobbyTab == "AVAILABLE") MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant
                ),
                modifier = Modifier.weight(1f)
            ) {
                Text("Available Tests")
            }
            Button(
                onClick = { activeTestLobbyTab = "RESULTS" },
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (activeTestLobbyTab == "RESULTS") MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant
                ),
                modifier = Modifier.weight(1f)
            ) {
                Text("Performance History")
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        if (activeTestLobbyTab == "AVAILABLE") {
            LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                items(testsList) { test ->
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.surfaceVariant)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(
                                text = "OFFLINE TEST SERIES",
                                style = MaterialTheme.typography.labelSmall,
                                color = Color(0xFFF59E0B),
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = test.title,
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.height(8.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(16.dp)
                            ) {
                                Column {
                                    Text("Questions", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                    Text("${test.totalQuestions}", style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold)
                                }
                                Column {
                                    Text("Duration", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                    Text("${test.durationMinutes} Min", style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold)
                                }
                                Column {
                                    Text("Total Marks", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                    Text("${test.totalMarks} Marks", style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold)
                                }
                            }

                            Spacer(modifier = Modifier.height(12.dp))

                            Text(
                                text = "Rule: Correct +4 • Incorrect -1 • Unattempted 0",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.error,
                                fontWeight = FontWeight.SemiBold
                            )

                            Spacer(modifier = Modifier.height(12.dp))

                            Button(
                                onClick = { onStartTest(test) },
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text("Enter Exam Hall")
                            }
                        }
                    }
                }
            }
        } else {
            if (resultsList.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxWidth().padding(32.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text("No tests submitted yet. Go take your first exam!")
                }
            } else {
                LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    items(resultsList) { res ->
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                            border = BorderStroke(1.dp, MaterialTheme.colorScheme.surfaceVariant)
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Text(
                                    text = res.testTitle,
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = "Date Attempted: ${res.date}",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )

                                Spacer(modifier = Modifier.height(12.dp))

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Column {
                                        Text("My Score", style = MaterialTheme.typography.bodySmall)
                                        Text("${res.score}/${res.totalQuestions * 4}", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = Color(0xFF10B981))
                                    }
                                    Column {
                                        Text("Correct", style = MaterialTheme.typography.bodySmall)
                                        Text("${res.correctCount}", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = Color(0xFF10B981))
                                    }
                                    Column {
                                        Text("Incorrect", style = MaterialTheme.typography.bodySmall)
                                        Text("${res.incorrectCount}", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.error)
                                    }
                                    Column {
                                        Text("Rank", style = MaterialTheme.typography.bodySmall)
                                        Text("#${res.rank}", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
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

// --- LIVE TEST / COUNTDOWN SCREEN ---
@Composable
fun LiveTestScreen(
    test: Test,
    onExamSubmitted: (TestResult) -> Unit,
    onBackClicked: () -> Unit
) {
    var activeQuestionIndex by remember { mutableStateOf(0) }
    var secondsRemaining by remember { mutableStateOf(test.durationMinutes * 60) }
    
    // Store selected option ('A', 'B', 'C', 'D' or null) for each question index
    val selectedAnswers = remember { mutableStateMapOf<Int, Char?>() }

    // Countdown Timer Coroutine
    LaunchedEffect(key1 = secondsRemaining) {
        if (secondsRemaining > 0) {
            delay(1000)
            secondsRemaining--
        } else {
            // Auto Submit on Time out!
            val results = calculateTestResults(test, selectedAnswers)
            onExamSubmitted(results)
        }
    }

    val currentQuestion = test.questions.getOrNull(activeQuestionIndex)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // Exam header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(text = test.title, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold)
                Text(
                    text = "Question ${activeQuestionIndex + 1} of ${test.questions.size}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            // Ticking Timer
            val minutesLeft = secondsRemaining / 60
            val secondsLeft = secondsRemaining % 60
            val timerColor = if (minutesLeft < 2) MaterialTheme.colorScheme.error else Color(0xFFF59E0B)

            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.HourglassEmpty, contentDescription = "Timer", tint = timerColor)
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = String.format("%02d:%02d", minutesLeft, secondsLeft),
                    style = MaterialTheme.typography.titleLarge,
                    color = timerColor,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Progress bar
        LinearProgressIndicator(
            progress = { (activeQuestionIndex + 1) / test.questions.size.toFloat() },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        if (currentQuestion != null) {
            // Question Text Card
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f))
            ) {
                LazyColumn(modifier = Modifier.padding(16.dp)) {
                    item {
                        Text(
                            text = currentQuestion.text,
                            style = MaterialTheme.typography.bodyLarge,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(bottom = 16.dp)
                        )
                    }

                    // Options list
                    val currentSelected = selectedAnswers[activeQuestionIndex]

                    item {
                        ExamOptionCard('A', currentQuestion.optionA, currentSelected == 'A') {
                            selectedAnswers[activeQuestionIndex] = 'A'
                        }
                    }
                    item {
                        ExamOptionCard('B', currentQuestion.optionB, currentSelected == 'B') {
                            selectedAnswers[activeQuestionIndex] = 'B'
                        }
                    }
                    item {
                        ExamOptionCard('C', currentQuestion.optionC, currentSelected == 'C') {
                            selectedAnswers[activeQuestionIndex] = 'C'
                        }
                    }
                    item {
                        ExamOptionCard('D', currentQuestion.optionD, currentSelected == 'D') {
                            selectedAnswers[activeQuestionIndex] = 'D'
                        }
                    }

                    item {
                        Spacer(modifier = Modifier.height(24.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Button(
                                onClick = { if (activeQuestionIndex > 0) activeQuestionIndex-- },
                                enabled = activeQuestionIndex > 0
                            ) {
                                Text("Previous")
                            }

                            if (activeQuestionIndex < test.questions.size - 1) {
                                Button(
                                    onClick = { activeQuestionIndex++ }
                                ) {
                                    Text("Next Question")
                                }
                            } else {
                                Button(
                                    onClick = {
                                        val results = calculateTestResults(test, selectedAnswers)
                                        onExamSubmitted(results)
                                    },
                                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF10B981))
                                ) {
                                    Text("Submit Exam")
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ExamOptionCard(optionLetter: Char, optionText: String, isSelected: Boolean, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp)
            .clickable { onClick() },
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected) MaterialTheme.colorScheme.primary.copy(alpha = 0.15f) else MaterialTheme.colorScheme.surface
        ),
        border = BorderStroke(1.dp, if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline.copy(alpha = 0.3f))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            RadioButton(selected = isSelected, onClick = onClick)
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "($optionLetter) $optionText",
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
            )
        }
    }
}

private fun calculateTestResults(test: Test, selectedAnswers: Map<Int, Char?>): TestResult {
    var score = 0
    var correctCount = 0
    var incorrectCount = 0
    var unattemptedCount = 0

    test.questions.forEachIndexed { idx, q ->
        val answer = selectedAnswers[idx]
        if (answer == null) {
            unattemptedCount++
        } else if (answer == q.correctAnswer) {
            correctCount++
            score += 4
        } else {
            incorrectCount++
            score -= 1
        }
    }

    val pct = (score.toFloat() / (test.questions.size * 4)) * 100f
    val formattedDate = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())

    val finalResult = TestResult(
        id = UUID.randomUUID().toString(),
        testId = test.id,
        testTitle = test.title,
        score = score,
        totalQuestions = test.questions.size,
        correctCount = correctCount,
        incorrectCount = incorrectCount,
        unattemptedCount = unattemptedCount,
        percentage = if (pct < 0f) 0f else pct,
        rank = (3..15).random(),
        date = formattedDate
    )

    Repository.testResults.add(0, finalResult)
    return finalResult
}

// --- TEST RESULTS SCREEN ---
@Composable
fun TestResultScreen(
    result: TestResult,
    test: Test,
    onDismiss: () -> Unit
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f))
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(Icons.Default.Stars, contentDescription = "Award", tint = Color(0xFFF59E0B), modifier = Modifier.size(64.dp))
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = "Exam Performance Card",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold
                    )
                    Text(text = result.testTitle, style = MaterialTheme.typography.bodyMedium, textAlign = TextAlign.Center)

                    Spacer(modifier = Modifier.height(16.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("Total Score", style = MaterialTheme.typography.bodySmall)
                            Text("${result.score} pts", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold, color = Color(0xFF10B981))
                        }
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("Rank Achieved", style = MaterialTheme.typography.bodySmall)
                            Text("#${result.rank}", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold, color = Color(0xFFF59E0B))
                        }
                    }
                }
            }
        }

        // Questions Details Accordion
        item {
            Text(text = "Solution & Explanations", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
        }

        items(test.questions) { q ->
            var expandedSol by remember { mutableStateOf(false) }
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { expandedSol = !expandedSol },
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.2f))
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = q.text,
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Bold,
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis,
                            modifier = Modifier.weight(1f)
                        )
                        Icon(
                            imageVector = if (expandedSol) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                            contentDescription = "Expand explanation"
                        )
                    }

                    if (expandedSol) {
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(text = "Options:", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold)
                        Text(text = "A. ${q.optionA}", style = MaterialTheme.typography.bodySmall)
                        Text(text = "B. ${q.optionB}", style = MaterialTheme.typography.bodySmall)
                        Text(text = "C. ${q.optionC}", style = MaterialTheme.typography.bodySmall)
                        Text(text = "D. ${q.optionD}", style = MaterialTheme.typography.bodySmall)

                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            text = "Correct Answer: (${q.correctAnswer})",
                            style = MaterialTheme.typography.bodySmall,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF10B981)
                        )

                        Spacer(modifier = Modifier.height(4.dp))

                        Card(
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = "Explanation:\n${q.explanation}",
                                style = MaterialTheme.typography.bodySmall,
                                modifier = Modifier.padding(8.dp),
                                lineHeight = 18.sp
                            )
                        }
                    }
                }
            }
        }

        item {
            Button(onClick = onDismiss, modifier = Modifier.fillMaxWidth()) {
                Text("Back to Test Lobby")
            }
        }
    }
}

// --- AI DOUBT SOLVER (GEMINI CHAT INTERACTIVE) ---
@Composable
fun AiDoubtAssistantScreen() {
    var queryText by remember { mutableStateOf("") }
    var selectedSubject by remember { mutableStateOf("Physics") }
    var isAILoading by remember { mutableStateOf(false) }

    val scope = rememberCoroutineScope()
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = "AI Doubt Assistant",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = "Ask any Physics, Chemistry, Biology, or Math doubt and get explanations instantly",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(modifier = Modifier.height(12.dp))

        // Subject filter selector row
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            listOf("Physics", "Chemistry", "Biology", "Mathematics").forEach { sub ->
                val isSel = selectedSubject == sub
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(if (isSel) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant)
                        .clickable { selectedSubject = sub }
                        .padding(horizontal = 12.dp, vertical = 6.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = sub, style = MaterialTheme.typography.bodySmall, color = if (isSel) Color.White else MaterialTheme.colorScheme.onSurface)
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Chat List
        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            reverseLayout = false
        ) {
            items(Repository.doubts) { doubt ->
                Column(modifier = Modifier.fillMaxWidth()) {
                    // Question Bubble
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End
                    ) {
                        Card(
                            shape = RoundedCornerShape(16.dp, 16.dp, 0.dp, 16.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primary),
                            modifier = Modifier.fillMaxWidth(0.8f)
                        ) {
                            Text(
                                text = "(${doubt.subject}) ${doubt.question}",
                                style = MaterialTheme.typography.bodySmall,
                                color = Color.White,
                                modifier = Modifier.padding(12.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(4.dp))

                    // AI Response Bubble
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Start
                    ) {
                        Card(
                            shape = RoundedCornerShape(16.dp, 16.dp, 16.dp, 0.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                            modifier = Modifier.fillMaxWidth(0.85f)
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Text(
                                    text = "🤖 AI Doubt Solver",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = Color(0xFFF59E0B),
                                    fontWeight = FontWeight.Bold
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = doubt.response,
                                    style = MaterialTheme.typography.bodySmall,
                                    lineHeight = 18.sp
                                )
                            }
                        }
                    }
                }
            }
        }

        if (isAILoading) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                CircularProgressIndicator(modifier = Modifier.size(24.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text("Analyzing doubt with Gemini 3.5 Flash...", style = MaterialTheme.typography.bodySmall)
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Input Box
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            OutlinedTextField(
                value = queryText,
                onValueChange = { queryText = it },
                placeholder = { Text("Ask. e.g. What is Aufbau principle?") },
                modifier = Modifier
                    .weight(1f)
                    .testTag("ai_query_input"),
                shape = RoundedCornerShape(12.dp)
            )

            Spacer(modifier = Modifier.width(8.dp))

            IconButton(
                onClick = {
                    if (queryText.trim().isNotEmpty()) {
                        isAILoading = true
                        val text = queryText
                        queryText = ""
                        scope.launch {
                            Repository.askDoubt(text, selectedSubject)
                            isAILoading = false
                        }
                    }
                },
                modifier = Modifier
                    .background(MaterialTheme.colorScheme.primary, CircleShape)
                    .testTag("ai_send_btn")
            ) {
                Icon(Icons.Default.Send, contentDescription = "Send", tint = Color.White)
            }
        }
    }
}

// --- DISCUSSION FORUM COMPOSABLE ---
@Composable
fun DiscussionForumScreen() {
    var postTitle by remember { mutableStateOf("") }
    var postContent by remember { mutableStateOf("") }
    var selectedForumSubject by remember { mutableStateOf(SubjectCategory.PHYSICS) }
    var showCreatePostDialog by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(text = "Aspirants Forum", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                Text(text = "Connect, debate, and discuss with teachers & peers", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
            Button(onClick = { showCreatePostDialog = true }) {
                Icon(Icons.Default.Add, contentDescription = "Add")
                Text("Ask")
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            items(Repository.discussions) { post ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.surfaceVariant)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(32.dp)
                                        .background(MaterialTheme.colorScheme.primary, CircleShape),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(text = post.authorName.take(1), color = Color.White, fontWeight = FontWeight.Bold)
                                }
                                Spacer(modifier = Modifier.width(8.dp))
                                Column {
                                    Text(text = post.authorName, style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.Bold)
                                    Text(
                                        text = when(post.authorRole) {
                                            UserRole.STUDENT -> "Aspirant"
                                            UserRole.TEACHER -> "Senior Faculty"
                                            UserRole.ADMIN -> "Admin"
                                        },
                                        style = MaterialTheme.typography.labelSmall,
                                        color = MaterialTheme.colorScheme.primary
                                    )
                                }
                            }

                            Text(text = post.timestamp, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        Text(text = post.title, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(text = post.content, style = MaterialTheme.typography.bodySmall)

                        Spacer(modifier = Modifier.height(12.dp))

                        // Actions Row
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(24.dp)
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.clickable {
                                    val index = Repository.discussions.indexOf(post)
                                    if (index != -1) {
                                        val liked = post.likedByMe
                                        Repository.discussions[index] = post.copy(
                                            likes = if (liked) post.likes - 1 else post.likes + 1,
                                            likedByMe = !liked
                                        )
                                    }
                                }
                            ) {
                                Icon(
                                    imageVector = if (post.likedByMe) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                                    contentDescription = "Likes",
                                    tint = if (post.likedByMe) Color(0xFFEF4444) else MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("${post.likes}", style = MaterialTheme.typography.bodySmall)
                            }

                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.Comment, contentDescription = "Replies", tint = MaterialTheme.colorScheme.onSurfaceVariant)
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("${post.replies.size}", style = MaterialTheme.typography.bodySmall)
                            }
                        }

                        // Replies section nested
                        if (post.replies.isNotEmpty()) {
                            Spacer(modifier = Modifier.height(8.dp))
                            Divider()
                            Spacer(modifier = Modifier.height(8.dp))

                            post.replies.forEach { rep ->
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(start = 16.dp, top = 4.dp, bottom = 4.dp),
                                    verticalAlignment = Alignment.Top
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(24.dp)
                                            .background(Color(0xFFF59E0B), CircleShape),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(rep.authorName.take(1), fontSize = 10.sp, color = Color.White)
                                    }
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Column {
                                        Text("${rep.authorName} (${rep.authorRole})", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.secondary)
                                        Text(rep.content, style = MaterialTheme.typography.bodySmall)
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    if (showCreatePostDialog) {
        Dialog(onDismissRequest = { showCreatePostDialog = false }) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text("Submit Forum Query", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(12.dp))

                    OutlinedTextField(
                        value = postTitle,
                        onValueChange = { postTitle = it },
                        label = { Text("Topic Title") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    OutlinedTextField(
                        value = postContent,
                        onValueChange = { postContent = it },
                        label = { Text("Describe details...") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(100.dp)
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Button(
                            onClick = {
                                if (postTitle.isNotEmpty() && postContent.isNotEmpty()) {
                                    val newPost = DiscussionPost(
                                        id = UUID.randomUUID().toString(),
                                        authorName = Repository.currentUser.value.name,
                                        authorRole = Repository.currentUser.value.role,
                                        title = postTitle,
                                        content = postContent,
                                        subject = selectedForumSubject,
                                        timestamp = "Just now"
                                    )
                                    Repository.discussions.add(0, newPost)
                                    showCreatePostDialog = false
                                    postTitle = ""
                                    postContent = ""
                                }
                            },
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("Post")
                        }
                        Button(
                            onClick = { showCreatePostDialog = false },
                            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error),
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("Cancel")
                        }
                    }
                }
            }
        }
    }
}

// --- TEACHER CONTROL PANEL VIEW ---
@Composable
fun TeacherPanelScreen() {
    var chapName by remember { mutableStateOf("") }
    var lectureTitle by remember { mutableStateOf("") }
    var lectureDuration by remember { mutableStateOf("45:00") }
    
    var activeTeacherTab by remember { mutableStateOf("LECTURES") } // "LECTURES", "DOUBTS"

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = "👨‍🏫 Teacher Workspace Hub",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Button(
                onClick = { activeTeacherTab = "LECTURES" },
                modifier = Modifier.weight(1f),
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (activeTeacherTab == "LECTURES") MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant
                )
            ) {
                Text("Publish Syllabus")
            }
            Button(
                onClick = { activeTeacherTab = "DOUBTS" },
                modifier = Modifier.weight(1f),
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (activeTeacherTab == "DOUBTS") MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant
                )
            ) {
                Text("Solve Student Doubts")
            }
        }

        if (activeTeacherTab == "LECTURES") {
            // Lecture Uploader Card
            Card(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(text = "Upload Lecture Video & Notes", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(12.dp))

                    OutlinedTextField(
                        value = chapName,
                        onValueChange = { chapName = it },
                        label = { Text("Chapter Title (e.g. Modern Physics)") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    OutlinedTextField(
                        value = lectureTitle,
                        onValueChange = { lectureTitle = it },
                        label = { Text("Lecture Video Title") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    OutlinedTextField(
                        value = lectureDuration,
                        onValueChange = { lectureDuration = it },
                        label = { Text("Duration (MM:SS)") },
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Button(
                        onClick = {
                            if (chapName.isNotEmpty() && lectureTitle.isNotEmpty()) {
                                // Simulate adding to database
                                val courseId = "course_physics_1"
                                val newChapId = "chap_t_" + UUID.randomUUID().toString().take(4)
                                val newChap = Chapter(newChapId, courseId, chapName, Repository.chapters.size + 1)
                                Repository.chapters.add(newChap)

                                val newVideo = Video(
                                    id = "vid_t_" + UUID.randomUUID().toString().take(4),
                                    chapterId = newChapId,
                                    title = lectureTitle,
                                    videoUrl = "dQw4w9WgXcQ",
                                    duration = lectureDuration,
                                    noteSnippet = "Lecture guidelines published by Swati Sharma. Solve the attached DPP challenges in your homework copy.",
                                    views = 12
                                )
                                Repository.videos.add(newVideo)
                                
                                val newNote = Note(
                                    id = "note_t_" + UUID.randomUUID().toString().take(4),
                                    chapterId = newChapId,
                                    title = "$chapName - Cheat Sheet Note",
                                    pages = 5
                                )
                                Repository.notes.add(newNote)

                                // Post system notification
                                Repository.notifications.add(
                                    0,
                                    Notification(
                                        UUID.randomUUID().toString(),
                                        "New Syllabus Update!",
                                        "Prof. Sharma published '$lectureTitle' in course workspace.",
                                        "Just now",
                                        "System"
                                    )
                                )

                                chapName = ""
                                lectureTitle = ""
                                lectureDuration = "45:00"
                            }
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Publish to Students Workspace")
                    }
                }
            }
        } else {
            // Live unresolved doubts solver
            LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                items(Repository.doubts) { doubt ->
                    var replyText by remember { mutableStateOf("") }
                    Card(modifier = Modifier.fillMaxWidth()) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(text = "Subject: ${doubt.subject}", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold)
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(text = doubt.question, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold)
                            
                            Spacer(modifier = Modifier.height(8.dp))

                            OutlinedTextField(
                                value = replyText,
                                onValueChange = { replyText = it },
                                label = { Text("Type teacher's response solution...") },
                                modifier = Modifier.fillMaxWidth()
                            )

                            Spacer(modifier = Modifier.height(8.dp))

                            Button(
                                onClick = {
                                    if (replyText.isNotEmpty()) {
                                        val idx = Repository.doubts.indexOf(doubt)
                                        if (idx != -1) {
                                            Repository.doubts[idx] = doubt.copy(
                                                response = replyText,
                                                isAiResolved = false
                                            )
                                        }
                                        replyText = ""
                                    }
                                }
                            ) {
                                Text("Publish Approved Solution")
                            }
                        }
                    }
                }
            }
        }
    }
}

// --- ADMIN PANEL CONTROL PANEL VIEW ---
@Composable
fun AdminPanelScreen() {
    var isUnlocked by remember { mutableStateOf(false) }
    var passwordInput by remember { mutableStateOf("") }
    var isPasswordVisible by remember { mutableStateOf(false) }
    var decryptingState by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf("") }
    val coroutineScope = rememberCoroutineScope()

    val isCorrectPassword = { input: String ->
        try {
            val base64Encoded = java.util.Base64.getEncoder().encodeToString(input.trim().uppercase(Locale.ROOT).toByteArray())
            base64Encoded == "QURNSU45OTk="
        } catch (e: Exception) {
            input.trim().uppercase(Locale.ROOT) == "ADMIN999"
        }
    }

    var statStudentsCount by remember { mutableStateOf(4120) }
    var promoCouponCode by remember { mutableStateOf("") }
    var promoDiscount by remember { mutableStateOf("15") }
    
    var showCreateCourseDialog by remember { mutableStateOf(false) }
    var newCourseTitle by remember { mutableStateOf("") }
    var newCourseDesc by remember { mutableStateOf("") }
    var newCourseInstructor by remember { mutableStateOf("") }
    var newCoursePrice by remember { mutableStateOf("2999") }
    var newCourseCategory by remember { mutableStateOf(CourseCategory.NEET) }
    var newCourseSubject by remember { mutableStateOf(SubjectCategory.PHYSICS) }

    if (!isUnlocked) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            contentAlignment = Alignment.Center
        ) {
            Card(
                modifier = Modifier
                    .fillMaxWidth(0.95f)
                    .border(
                        border = BorderStroke(1.dp, Color(0x22FFFFFF)),
                        shape = RoundedCornerShape(24.dp)
                    ),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0x14FFFFFF)),
                elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
            ) {
                Column(
                    modifier = Modifier
                        .padding(24.dp)
                        .fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(72.dp)
                            .clip(CircleShape)
                            .background(Color(0x1F3B82F6))
                            .border(BorderStroke(1.dp, Color(0x333B82F6)), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Lock,
                            contentDescription = "Encrypted Vault Lock",
                            tint = Color(0xFF60A5FA),
                            modifier = Modifier.size(36.dp)
                        )
                    }

                    Text(
                        text = "SECURE DECRYPTION PORTAL",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        letterSpacing = 1.5.sp
                    )

                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(Color(0xFF0F1115))
                            .border(BorderStroke(1.dp, Color(0x22FFFFFF)), RoundedCornerShape(8.dp))
                            .padding(horizontal = 12.dp, vertical = 6.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(8.dp)
                                    .clip(CircleShape)
                                    .background(Color(0xFF14B8A6))
                            )
                            Text(
                                text = "CIPHER: AES-256-GCM [LOCKED]",
                                style = MaterialTheme.typography.labelSmall,
                                fontFamily = FontFamily.Monospace,
                                color = Color(0xFF94A3B8)
                            )
                        }
                    }

                    Text(
                        text = "Enter the master decryption key to unlock the administrator control dashboard and view database telemetry.",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color(0xFF94A3B8),
                        textAlign = TextAlign.Center,
                        lineHeight = 16.sp
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    OutlinedTextField(
                        value = passwordInput,
                        onValueChange = {
                            passwordInput = it
                            errorMessage = ""
                        },
                        label = { Text("Decryption Key", color = Color(0xFF94A3B8)) },
                        placeholder = { Text("Type ADMIN999", color = Color(0x44FFFFFF)) },
                        singleLine = true,
                        visualTransformation = if (isPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                        trailingIcon = {
                            IconButton(onClick = { isPasswordVisible = !isPasswordVisible }) {
                                Icon(
                                    imageVector = if (isPasswordVisible) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                                    contentDescription = "Toggle password visibility",
                                    tint = Color(0xFF94A3B8)
                                )
                            }
                        },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White,
                            focusedBorderColor = Color(0xFF3B82F6),
                            unfocusedBorderColor = Color(0x33FFFFFF),
                            focusedContainerColor = Color(0x0AFFFFFF),
                            unfocusedContainerColor = Color(0x0AFFFFFF)
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )

                    if (errorMessage.isNotEmpty()) {
                        Text(
                            text = errorMessage,
                            color = Color(0xFFEF4444),
                            style = MaterialTheme.typography.bodySmall,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.align(Alignment.Start)
                        )
                    }

                    Spacer(modifier = Modifier.height(4.dp))

                    if (decryptingState) {
                        Column(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            LinearProgressIndicator(
                                color = Color(0xFF14B8A6),
                                trackColor = Color(0x1AFFFFFF),
                                modifier = Modifier.fillMaxWidth().height(4.dp)
                            )
                            Text(
                                text = "DECRYPTING SYSTEM REGISTERS...",
                                style = MaterialTheme.typography.labelSmall,
                                fontFamily = FontFamily.Monospace,
                                color = Color(0xFF14B8A6)
                            )
                        }
                    } else {
                        Button(
                            onClick = {
                                if (isCorrectPassword(passwordInput)) {
                                    decryptingState = true
                                    coroutineScope.launch {
                                        delay(1000)
                                        decryptingState = false
                                        isUnlocked = true
                                    }
                                } else {
                                    errorMessage = "⚠️ INVALID DECRYPTION KEY. SECURITY PROTOCOL ENFORCED."
                                }
                            },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color(0xFF3B82F6),
                                contentColor = Color.White
                            ),
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text(
                                text = "DECRYPT & UNLOCK",
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 1.sp
                            )
                        }
                    }
                }
            }
        }
    } else {
        LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Text(
                text = "⚙️ Admin Control Panel",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
        }

        // Global Stats Summary Cards
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Card(modifier = Modifier.weight(1f)) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Text("Active Registrations", style = MaterialTheme.typography.labelSmall)
                        Text("$statStudentsCount Students", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                    }
                }
                Card(modifier = Modifier.weight(1f)) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Text("Platform Batches", style = MaterialTheme.typography.labelSmall)
                        Text("${Repository.courses.size} Active", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = Color(0xFFF59E0B))
                    }
                }
            }
        }

        // Course Management Actions
        item {
            Card(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Course Manager Hub", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(8.dp))
                    Button(
                        onClick = { showCreateCourseDialog = true },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Add New Academic Course")
                    }
                }
            }
        }

        // Active Courses with Delete/Remove triggers
        item {
            Text("Active Courses Catalog", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
        }

        items(Repository.courses) { course ->
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(0.7f)) {
                        Text(text = course.title, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold)
                        Text(text = "Price: ₹${course.price.toInt()} • Instructor: ${course.instructor}", style = MaterialTheme.typography.labelSmall)
                    }
                    IconButton(
                        onClick = { Repository.courses.remove(course) },
                        modifier = Modifier.weight(0.15f)
                    ) {
                        Icon(Icons.Default.Delete, contentDescription = "Delete", tint = MaterialTheme.colorScheme.error)
                    }
                }
            }
        }

        // Coupon creator Sandbox
        item {
            Card(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Coupon Discount Creator", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = promoCouponCode,
                        onValueChange = { promoCouponCode = it },
                        label = { Text("Promo Code (e.g., JEE2027)") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = promoDiscount,
                        onValueChange = { promoDiscount = it },
                        label = { Text("Discount Percentage (%)") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Button(
                        onClick = {
                            val pct = promoDiscount.toIntOrNull() ?: 10
                            if (promoCouponCode.isNotEmpty()) {
                                Repository.coupons.add(
                                    Coupon(promoCouponCode.uppercase(), pct, "$pct% off coupon created by admin", 100, 0)
                                )
                                promoCouponCode = ""
                            }
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Create Promo Code")
                    }
                }
            }
        }
    }

    // Add Course Dialog Builder
    if (showCreateCourseDialog) {
        Dialog(onDismissRequest = { showCreateCourseDialog = false }) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                shape = RoundedCornerShape(16.dp)
            ) {
                LazyColumn(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    item {
                        Text("Configure Course Blueprint", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.height(12.dp))
                    }

                    item {
                        OutlinedTextField(
                            value = newCourseTitle,
                            onValueChange = { newCourseTitle = it },
                            label = { Text("Course Title") },
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                    item {
                        OutlinedTextField(
                            value = newCourseDesc,
                            onValueChange = { newCourseDesc = it },
                            label = { Text("Description") },
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                    item {
                        OutlinedTextField(
                            value = newCourseInstructor,
                            onValueChange = { newCourseInstructor = it },
                            label = { Text("Instructor Name") },
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                    item {
                        OutlinedTextField(
                            value = newCoursePrice,
                            onValueChange = { newCoursePrice = it },
                            label = { Text("Price (INR)") },
                            modifier = Modifier.fillMaxWidth()
                        )
                    }

                    item {
                        Text("Select Subject Stream:", style = MaterialTheme.typography.labelSmall, modifier = Modifier.align(Alignment.Start))
                        Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                            SubjectCategory.values().forEach { sub ->
                                val isSel = newCourseSubject == sub
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(6.dp))
                                        .background(if (isSel) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant)
                                        .clickable { newCourseSubject = sub }
                                        .padding(horizontal = 8.dp, vertical = 4.dp)
                                ) {
                                    Text(sub.name.take(4), fontSize = 10.sp, color = if (isSel) Color.White else MaterialTheme.colorScheme.onSurface)
                                }
                            }
                        }
                    }

                    item {
                        Text("Select Target Level:", style = MaterialTheme.typography.labelSmall, modifier = Modifier.align(Alignment.Start))
                        Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                            CourseCategory.values().forEach { cat ->
                                val isSel = newCourseCategory == cat
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(6.dp))
                                        .background(if (isSel) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant)
                                        .clickable { newCourseCategory = cat }
                                        .padding(horizontal = 8.dp, vertical = 4.dp)
                                ) {
                                    Text(cat.name.replace("_", " "), fontSize = 10.sp, color = if (isSel) Color.White else MaterialTheme.colorScheme.onSurface)
                                }
                            }
                        }
                    }

                    item {
                        Spacer(modifier = Modifier.height(16.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Button(
                                onClick = {
                                    if (newCourseTitle.isNotEmpty() && newCourseInstructor.isNotEmpty()) {
                                        val priceVal = newCoursePrice.toDoubleOrNull() ?: 2999.0
                                        val newC = Course(
                                            id = "course_admin_" + UUID.randomUUID().toString().take(4),
                                            title = newCourseTitle,
                                            category = newCourseCategory,
                                            subject = newCourseSubject,
                                            description = newCourseDesc,
                                            thumbnail = "ic_default_card",
                                            instructor = newCourseInstructor,
                                            price = priceVal
                                        )
                                        Repository.courses.add(newC)
                                        showCreateCourseDialog = false
                                        newCourseTitle = ""
                                        newCourseDesc = ""
                                        newCourseInstructor = ""
                                        newCoursePrice = "2999"
                                    }
                                },
                                modifier = Modifier.weight(1f)
                            ) {
                                Text("Create")
                            }
                            Button(
                                onClick = { showCreateCourseDialog = false },
                                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error),
                                modifier = Modifier.weight(1f)
                            ) {
                                Text("Cancel")
                            }
                        }
                    }
                }
            }
        }
    }
    }
}
