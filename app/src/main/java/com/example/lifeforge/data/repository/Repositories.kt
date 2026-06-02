package com.example.lifeforge.data.repository

import com.example.lifeforge.data.DefaultData
import com.example.lifeforge.data.local.dao.AchievementDao
import com.example.lifeforge.data.local.dao.unlockNow
import com.example.lifeforge.data.local.dao.CategoryDao
import com.example.lifeforge.data.local.dao.GoalDao
import com.example.lifeforge.data.local.dao.HabitCompletionDao
import com.example.lifeforge.data.local.dao.HabitDao
import com.example.lifeforge.data.local.dao.UserProfileDao
import com.example.lifeforge.data.local.entity.GoalEntity
import com.example.lifeforge.data.local.entity.HabitCompletionEntity
import com.example.lifeforge.data.local.entity.HabitEntity
import com.example.lifeforge.data.local.entity.UserProfileEntity
import com.example.lifeforge.data.model.HabitStatus
import com.example.lifeforge.domain.GamificationEngine
import com.example.lifeforge.util.DayBounds
import kotlinx.coroutines.flow.Flow
class AppInitializer(
    private val categoryDao: CategoryDao,
    private val achievementDao: AchievementDao,
    private val userProfileDao: UserProfileDao
) {
    suspend fun initializeIfNeeded() {
        if (categoryDao.count() == 0) {
            categoryDao.insertAll(DefaultData.categories)
        }
        achievementDao.insertAll(DefaultData.achievements)
        if (userProfileDao.getProfile() == null) {
            userProfileDao.insert(UserProfileEntity())
        }
    }
}

class CategoryRepository(
    private val categoryDao: CategoryDao
) {
    fun observeCategories(): Flow<List<com.example.lifeforge.data.local.entity.CategoryEntity>> =
        categoryDao.observeAll()

    suspend fun insert(category: com.example.lifeforge.data.local.entity.CategoryEntity): Long =
        categoryDao.insert(category)
}

class HabitRepository(
    private val habitDao: HabitDao,
    private val habitCompletionDao: HabitCompletionDao,
    private val gamificationEngine: GamificationEngine,
    private val achievementDao: AchievementDao
) {
    fun observeActiveHabits(): Flow<List<HabitEntity>> =
        habitDao.observeByStatus(HabitStatus.ACTIVE)

    suspend fun insert(habit: HabitEntity): Long {
        val wasEmpty = habitDao.count() == 0
        val id = habitDao.insert(habit)
        if (wasEmpty) achievementDao.unlockNow("first_habit")
        return id
    }

    suspend fun completeHabit(habit: HabitEntity, dayBounds: DayBounds) {
        val existing = habitCompletionDao.getForDay(
            habit.id,
            dayBounds.startMillis,
            dayBounds.endMillis
        )
        if (existing != null) return

        habitCompletionDao.insert(
            HabitCompletionEntity(
                habitId = habit.id,
                completedDate = System.currentTimeMillis()
            )
        )
        val updated = habit.copy(
            currentStreak = habit.currentStreak + 1,
            bestStreak = maxOf(habit.bestStreak, habit.currentStreak + 1)
        )
        habitDao.update(updated)
        gamificationEngine.awardHabitCompletion()
    }
}

class GoalRepository(
    private val goalDao: GoalDao
) {
    fun observeActiveGoals() = goalDao.observeActive()

    suspend fun insert(goal: GoalEntity): Long = goalDao.insert(goal)
}

class UserRepository(
    private val userProfileDao: UserProfileDao
) {
    fun observeProfile(): Flow<UserProfileEntity?> = userProfileDao.observeProfile()
}

