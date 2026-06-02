package com.example.lifeforge.data.repository

import com.example.lifeforge.data.local.dao.CategoryDao
import com.example.lifeforge.data.local.dao.HabitCompletionDao
import com.example.lifeforge.data.local.dao.HabitDao
import com.example.lifeforge.data.local.dao.TaskDao
import com.example.lifeforge.data.local.dao.TaskDayStateDao
import com.example.lifeforge.data.model.DayEntryState
import com.example.lifeforge.data.model.HabitStatus
import com.example.lifeforge.domain.RecurrenceEngine
import com.example.lifeforge.util.DayBounds
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId

class TodayRepository(
    private val taskDao: TaskDao,
    private val taskDayStateDao: TaskDayStateDao,
    private val categoryDao: CategoryDao,
    private val habitDao: HabitDao,
    private val habitCompletionDao: HabitCompletionDao
) {
    private val zoneId: ZoneId = ZoneId.systemDefault()

    fun observeDayActivities(date: LocalDate): Flow<DayActivities> {
        val bounds = DayBounds.forDate(date, zoneId)
        return combine(
            taskDao.observeAll(),
            categoryDao.observeAll(),
            taskDayStateDao.observeForDay(bounds.startMillis, bounds.endMillis),
            habitDao.observeByStatus(HabitStatus.ACTIVE),
            habitCompletionDao.observeCompletedHabitCountForDay(bounds.startMillis, bounds.endMillis)
        ) { tasks, categories, dayStates, habits, completedHabitCount ->
            val categoryMap = categories.associate { it.id to it.name }
            val stateByTaskId = dayStates.associateBy { it.taskId }

            val scheduled = tasks
                .filter { it.status != com.example.lifeforge.data.model.TaskStatus.CANCELLED }
                .filter { task ->
                    val anchor = task.scheduleStartDate?.let {
                        Instant.ofEpochMilli(it).atZone(zoneId).toLocalDate()
                    }
                    val singleDue = task.dueDate?.let {
                        Instant.ofEpochMilli(it).atZone(zoneId).toLocalDate()
                    }
                    RecurrenceEngine.occursOnDate(
                        task.activityKind,
                        task.recurrenceRuleJson,
                        anchor,
                        singleDue,
                        date
                    )
                }
                .map { task ->
                    val state = stateByTaskId[task.id]
                    val completed = state?.state == DayEntryState.COMPLETED
                    val skipped = state?.state == DayEntryState.SKIPPED
                    TodayActivityItem(
                        task = task,
                        categoryName = task.categoryId?.let { categoryMap[it] },
                        dayState = state,
                        isCompleted = completed,
                        isSkipped = skipped
                    )
                }

            val pending = scheduled.filter { !it.isCompleted && !it.isSkipped }
            val completed = scheduled.filter { it.isCompleted }
            val total = scheduled.size + habits.size
            val done = completed.size + completedHabitCount
            val percent = if (total == 0) 0 else ((done * 100f) / total).toInt()

            DayActivities(
                date = date,
                pending = pending,
                completed = completed,
                completionPercent = percent,
                tasksCompleted = completed.size,
                tasksTotal = scheduled.size,
                habitsCompleted = completedHabitCount,
                habitsTotal = habits.size
            )
        }
    }
}

data class DayActivities(
    val date: LocalDate,
    val pending: List<TodayActivityItem>,
    val completed: List<TodayActivityItem>,
    val completionPercent: Int,
    val tasksCompleted: Int,
    val tasksTotal: Int,
    val habitsCompleted: Int,
    val habitsTotal: Int
)

data class TodayProgress(
    val completionPercent: Int,
    val tasksCompleted: Int,
    val tasksTotal: Int,
    val habitsCompleted: Int,
    val habitsTotal: Int
)
