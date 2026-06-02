package com.example.lifeforge.domain

import com.example.lifeforge.data.local.dao.AchievementDao
import com.example.lifeforge.data.local.dao.unlockNow
import com.example.lifeforge.data.local.dao.UserProfileDao
import com.example.lifeforge.data.local.entity.UserProfileEntity
import kotlin.math.floor
import kotlin.math.pow

class GamificationEngine(
    private val userProfileDao: UserProfileDao,
    private val achievementDao: AchievementDao
) {
    suspend fun awardTaskCompletion() {
        addXp(XP_TASK)
    }

    suspend fun awardHabitCompletion() {
        addXp(XP_HABIT)
    }

    suspend fun awardGoalCompletion() {
        addXp(XP_GOAL)
    }

    private suspend fun addXp(amount: Int) {
        val profile = userProfileDao.getProfile() ?: UserProfileEntity().also {
            userProfileDao.insert(it)
        }
        val newXp = profile.totalXp + amount
        val newLevel = levelForXp(newXp)
        val updated = profile.copy(
            totalXp = newXp,
            level = newLevel,
            coins = profile.coins + (amount / 5)
        )
        userProfileDao.update(updated)
        checkLevelAchievements(newLevel)
    }

    private suspend fun checkLevelAchievements(level: Int) {
        when {
            level >= 100 -> achievementDao.unlockNow("level_100")
            level >= 50 -> achievementDao.unlockNow("level_50")
            level >= 25 -> achievementDao.unlockNow("level_25")
            level >= 10 -> achievementDao.unlockNow("level_10")
        }
    }

    companion object {
        const val XP_TASK = 10
        const val XP_HABIT = 20
        const val XP_GOAL = 100
        const val MAX_LEVEL = 100

        fun xpRequiredForLevel(level: Int): Int {
            if (level <= 1) return 0
            return floor(100 * level.toDouble().pow(1.5)).toInt()
        }

        fun levelForXp(totalXp: Int): Int {
            var level = 1
            while (level < MAX_LEVEL && totalXp >= xpRequiredForLevel(level + 1)) {
                level++
            }
            return level
        }
    }
}
