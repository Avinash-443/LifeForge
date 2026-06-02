package com.example.lifeforge.data.local.dao

suspend fun AchievementDao.unlockNow(key: String) {
    unlock(key, System.currentTimeMillis())
}
