package com.example.lifeforge.data

import com.example.lifeforge.data.local.entity.AchievementEntity
import com.example.lifeforge.data.local.entity.CategoryEntity

object DefaultData {
    val categories = listOf(
        CategoryEntity(name = "Study", colorHex = "#E83D72", iconName = "school", isDefault = true),
        CategoryEntity(name = "Work", colorHex = "#FF5D8F", iconName = "work", isDefault = true),
        CategoryEntity(name = "Fitness", colorHex = "#2ECC71", iconName = "fitness_center", isDefault = true),
        CategoryEntity(name = "Health", colorHex = "#3498DB", iconName = "favorite", isDefault = true),
        CategoryEntity(name = "Finance", colorHex = "#F39C12", iconName = "account_balance", isDefault = true),
        CategoryEntity(name = "Reading", colorHex = "#9B59B6", iconName = "menu_book", isDefault = true),
        CategoryEntity(name = "Music", colorHex = "#1ABC9C", iconName = "music_note", isDefault = true),
        CategoryEntity(name = "Language", colorHex = "#E67E22", iconName = "translate", isDefault = true),
        CategoryEntity(name = "Gaming", colorHex = "#8E44AD", iconName = "sports_esports", isDefault = true),
        CategoryEntity(name = "Entertainment", colorHex = "#E74C3C", iconName = "movie", isDefault = true),
        CategoryEntity(name = "Social", colorHex = "#16A085", iconName = "groups", isDefault = true),
        CategoryEntity(name = "Custom", colorHex = "#AAAAAA", iconName = "category", isDefault = true)
    )

    val achievements = listOf(
        AchievementEntity("first_habit", "First Habit", "Created your first habit"),
        AchievementEntity("first_task", "First Task", "Created your first task"),
        AchievementEntity("streak_7", "7-Day Streak", "Maintained a 7-day streak"),
        AchievementEntity("streak_30", "30-Day Streak", "Maintained a 30-day streak"),
        AchievementEntity("tasks_100", "100 Tasks", "Completed 100 tasks"),
        AchievementEntity("habits_100", "100 Habits", "Completed 100 habits"),
        AchievementEntity("perfect_week", "Perfect Week", "Completed all activities for a week"),
        AchievementEntity("perfect_month", "Perfect Month", "Completed all activities for a month"),
        AchievementEntity("level_10", "Level 10", "Reached level 10"),
        AchievementEntity("level_25", "Level 25", "Reached level 25"),
        AchievementEntity("level_50", "Level 50", "Reached level 50"),
        AchievementEntity("level_100", "Level 100", "Reached level 100")
    )
}
