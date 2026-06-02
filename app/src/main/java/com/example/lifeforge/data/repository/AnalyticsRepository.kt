package com.example.lifeforge.data.repository

import com.example.lifeforge.data.local.dao.HabitCompletionDao
import com.example.lifeforge.data.local.dao.HabitDao
import com.example.lifeforge.data.local.dao.TaskDao
import com.example.lifeforge.data.local.dao.TaskDayStateDao
import com.example.lifeforge.data.model.HabitStatus
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import java.time.LocalDate
import java.time.ZoneId

data class AnalyticsSnapshot(
    val totalTasks: Int,
    val completedSingleTasks: Int,
    val activeHabits: Int,
    val last7DaysTaskCompletions: Int,
    val last7DaysHabitCompletions: Int
)

class AnalyticsRepository(
    private val taskDao: TaskDao,
    private val taskDayStateDao: TaskDayStateDao,
    private val habitDao: HabitDao,
    private val habitCompletionDao: HabitCompletionDao
) {
    private val zoneId = ZoneId.systemDefault()

    fun observeSnapshot(): Flow<AnalyticsSnapshot> {
        val today = LocalDate.now(zoneId)
        val start = today.minusDays(6).atStartOfDay(zoneId).toInstant().toEpochMilli()
        val end = today.plusDays(1).atStartOfDay(zoneId).toInstant().toEpochMilli()

        return combine(
            taskDao.observeTotalCount(),
            taskDao.observeCompletedCount(),
            habitDao.observeCount(),
            taskDayStateDao.observeCompletedCountInRange(start, end),
            habitCompletionDao.observeCompletionCountInRange(start, end)
        ) { totalTasks, completedSingleTasks, activeHabits, taskCompletions7, habitCompletions7 ->
            AnalyticsSnapshot(
                totalTasks = totalTasks,
                completedSingleTasks = completedSingleTasks,
                activeHabits = activeHabits,
                last7DaysTaskCompletions = taskCompletions7,
                last7DaysHabitCompletions = habitCompletions7
            )
        }
    }
}

