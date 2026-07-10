package com.example.data

import java.io.Serializable

enum class UserRole {
    STUDENT, TEACHER, ADMIN
}

enum class CourseCategory {
    NEET, JEE_MAIN, JEE_ADVANCED
}

enum class SubjectCategory {
    PHYSICS, CHEMISTRY, BIOLOGY, MATHEMATICS
}

data class User(
    val id: String,
    val name: String,
    val email: String,
    val role: UserRole,
    val avatarUrl: String = "",
    val streak: Int = 3,
    val coins: Int = 120,
    val rank: Int = 42,
    val progress: Map<String, Float> = emptyMap(), // CourseId -> Progress percentage
    val badges: List<String> = listOf("Curious Mind", "Streak Starter"),
    val bookmarkedVideos: Set<String> = emptySet(),
    val bookmarkedNotes: Set<String> = emptySet()
) : Serializable

data class Course(
    val id: String,
    val title: String,
    val category: CourseCategory,
    val subject: SubjectCategory,
    val description: String,
    val thumbnail: String, // Decorative visual tag
    val instructor: String,
    val price: Double,
    val rating: Float = 4.8f,
    val enrolledCount: Int = 1240,
    val features: List<String> = listOf("Video Lectures", "PDF Notes", "Daily Practice Problems (DPP)", "Live Doubt Support")
) : Serializable

data class Chapter(
    val id: String,
    val courseId: String,
    val title: String,
    val order: Int
) : Serializable

data class Video(
    val id: String,
    val chapterId: String,
    val title: String,
    val videoUrl: String, // YouTube or Vimeo ID / embed link
    val duration: String,
    val noteSnippet: String = "",
    val dppSnippet: String = "",
    val views: Int = 340,
    val order: Int = 1
) : Serializable

data class Note(
    val id: String,
    val chapterId: String,
    val title: String,
    val pages: Int,
    val description: String = "Comprehensive theory notes with solved examples and mind maps.",
    val url: String = ""
) : Serializable

data class Question(
    val id: String,
    val testId: String,
    val text: String,
    val optionA: String,
    val optionB: String,
    val optionC: String,
    val optionD: String,
    val correctAnswer: Char, // 'A', 'B', 'C', 'D'
    val explanation: String
) : Serializable

data class Test(
    val id: String,
    val title: String,
    val courseId: String,
    val durationMinutes: Int,
    val totalQuestions: Int,
    val totalMarks: Int,
    val questions: List<Question> = emptyList()
) : Serializable

data class TestResult(
    val id: String,
    val testId: String,
    val testTitle: String,
    val score: Int,
    val totalQuestions: Int,
    val correctCount: Int,
    val incorrectCount: Int,
    val unattemptedCount: Int,
    val percentage: Float,
    val rank: Int,
    val totalParticipants: Int = 500,
    val date: String
) : Serializable

data class Doubt(
    val id: String,
    val question: String,
    val response: String,
    val subject: String,
    val timestamp: String,
    val isAiResolved: Boolean = true
) : Serializable

data class DiscussionPost(
    val id: String,
    val authorName: String,
    val authorRole: UserRole,
    val authorAvatar: String = "",
    val title: String,
    val content: String,
    val subject: SubjectCategory,
    val likes: Int = 0,
    val likedByMe: Boolean = false,
    val replies: List<DiscussionReply> = emptyList(),
    val timestamp: String
) : Serializable

data class DiscussionReply(
    val id: String,
    val authorName: String,
    val authorRole: UserRole,
    val content: String,
    val timestamp: String
) : Serializable

data class Notification(
    val id: String,
    val title: String,
    val message: String,
    val timestamp: String,
    val type: String = "General" // Announcement, Doubt, Reward, System
) : Serializable

data class Coupon(
    val code: String,
    val discountPercent: Int,
    val description: String,
    val maxUses: Int = 100,
    val usedCount: Int = 12
) : Serializable
