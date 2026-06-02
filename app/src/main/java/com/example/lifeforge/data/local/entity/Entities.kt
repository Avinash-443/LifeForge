package com.example.lifeforge.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import com.example.lifeforge.data.model.ActivityKind
import com.example.lifeforge.data.model.GoalStatus
import com.example.lifeforge.data.model.HabitFrequency
import com.example.lifeforge.data.model.HabitStatus
import com.example.lifeforge.data.model.HabitTrackingType
import com.example.lifeforge.data.model.TaskPriority
import com.example.lifeforge.data.model.TaskStatus
import com.example.lifeforge.data.model.TaskType
import com.example.lifeforge.data.model.TimeOfDay

@Entity(tableName = "categories")
data class CategoryEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val colorHex: String,
    val iconName: String,
    val description: String = "",
    val isDefault: Boolean = false
)

@Entity(
    tableName = "habits",
    foreignKeys = [
        ForeignKey(
            entity = CategoryEntity::class,
            parentColumns = ["id"],
            childColumns = ["categoryId"],
            onDelete = ForeignKey.SET_NULL
        )
    ],
    indices = [Index("categoryId")]
)
data class HabitEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val description: String = "",
    val categoryId: Long? = null,
    val iconName: String = "fitness_center",
    val colorHex: String = "#E83D72",
    val trackingType: HabitTrackingType = HabitTrackingType.YES_NO,
    val frequency: HabitFrequency = HabitFrequency.DAILY,
    val reminderTimeMillis: Long? = null,
    val priority: TaskPriority = TaskPriority.MEDIUM,
    val createdAt: Long = System.currentTimeMillis(),
    val currentStreak: Int = 0,
    val bestStreak: Int = 0,
    val successRate: Float = 0f,
    val xpReward: Int = 20,
    val status: HabitStatus = HabitStatus.ACTIVE,
    val notes: String = "",
    val motivationQuote: String = "",
    val startDate: Long? = null,
    val endDate: Long? = null
)

@Entity(
    tableName = "tasks",
    foreignKeys = [
        ForeignKey(
            entity = CategoryEntity::class,
            parentColumns = ["id"],
            childColumns = ["categoryId"],
            onDelete = ForeignKey.SET_NULL
        )
    ],
    indices = [Index("categoryId"), Index("dueDate"), Index("status")]
)
data class TaskEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val title: String,
    val description: String = "",
    val categoryId: Long? = null,
    val priority: TaskPriority = TaskPriority.MEDIUM,
    val dueDate: Long? = null,
    val reminderTimeMillis: Long? = null,
    val tags: List<String> = emptyList(),
    val notes: String = "",
    val status: TaskStatus = TaskStatus.PENDING,
    val taskType: TaskType = TaskType.ONE_TIME,
    val timeOfDay: TimeOfDay = TimeOfDay.ANYTIME,
    val isPinned: Boolean = false,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis(),
    val completedAt: Long? = null,
    val activityKind: ActivityKind = ActivityKind.SINGLE_TASK,
    val trackingType: HabitTrackingType = HabitTrackingType.YES_NO,
    val checklistItems: List<String> = emptyList(),
    val recurrenceRuleJson: String = "",
    val scheduleStartDate: Long? = null
)

@Entity(tableName = "goals")
data class GoalEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val title: String,
    val description: String = "",
    val targetDate: Long? = null,
    val progress: Float = 0f,
    val reward: String = "",
    val notes: String = "",
    val status: GoalStatus = GoalStatus.ACTIVE,
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(
    tableName = "habit_completions",
    foreignKeys = [
        ForeignKey(
            entity = HabitEntity::class,
            parentColumns = ["id"],
            childColumns = ["habitId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("habitId"), Index("completedDate")]
)
data class HabitCompletionEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val habitId: Long,
    val completedDate: Long,
    val value: Float? = null
)

@Entity(tableName = "user_profile")
data class UserProfileEntity(
    @PrimaryKey val id: Int = 1,
    val totalXp: Int = 0,
    val level: Int = 1,
    val coins: Int = 0,
    val currentTitle: String = "Apprentice"
)

@Entity(tableName = "achievements")
data class AchievementEntity(
    @PrimaryKey val key: String,
    val title: String,
    val description: String,
    val unlockedAt: Long? = null
)
