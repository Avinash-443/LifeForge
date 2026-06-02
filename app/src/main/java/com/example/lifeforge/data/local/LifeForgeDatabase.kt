package com.example.lifeforge.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.lifeforge.data.local.dao.AchievementDao
import com.example.lifeforge.data.local.dao.CategoryDao
import com.example.lifeforge.data.local.dao.GoalDao
import com.example.lifeforge.data.local.dao.HabitCompletionDao
import com.example.lifeforge.data.local.dao.HabitDao
import com.example.lifeforge.data.local.dao.TaskDao
import com.example.lifeforge.data.local.dao.TaskDayStateDao
import com.example.lifeforge.data.local.dao.UserProfileDao
import com.example.lifeforge.data.local.entity.AchievementEntity
import com.example.lifeforge.data.local.entity.CategoryEntity
import com.example.lifeforge.data.local.entity.GoalEntity
import com.example.lifeforge.data.local.entity.HabitCompletionEntity
import com.example.lifeforge.data.local.entity.HabitEntity
import com.example.lifeforge.data.local.entity.TaskDayStateEntity
import com.example.lifeforge.data.local.entity.TaskEntity
import com.example.lifeforge.data.local.entity.UserProfileEntity

@Database(
    entities = [
        CategoryEntity::class,
        HabitEntity::class,
        TaskEntity::class,
        GoalEntity::class,
        HabitCompletionEntity::class,
        UserProfileEntity::class,
        AchievementEntity::class,
        TaskDayStateEntity::class
    ],
    version = 2,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class LifeForgeDatabase : RoomDatabase() {
    abstract fun categoryDao(): CategoryDao
    abstract fun habitDao(): HabitDao
    abstract fun habitCompletionDao(): HabitCompletionDao
    abstract fun taskDao(): TaskDao
    abstract fun taskDayStateDao(): TaskDayStateDao
    abstract fun goalDao(): GoalDao
    abstract fun userProfileDao(): UserProfileDao
    abstract fun achievementDao(): AchievementDao

    companion object {
        const val DATABASE_NAME = "lifeforge.db"
    }
}
