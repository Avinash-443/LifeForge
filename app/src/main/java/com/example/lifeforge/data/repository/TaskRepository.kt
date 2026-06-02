package com.example.lifeforge.data.repository

import com.example.lifeforge.data.local.dao.AchievementDao
import com.example.lifeforge.data.local.dao.CategoryDao
import com.example.lifeforge.data.local.dao.TaskDao
import com.example.lifeforge.data.local.dao.TaskDayStateDao
import com.example.lifeforge.data.local.dao.unlockNow
import com.example.lifeforge.data.local.entity.TaskDayStateEntity
import com.example.lifeforge.data.local.entity.TaskEntity
import com.example.lifeforge.data.model.ActivityKind
import com.example.lifeforge.data.model.CreateActivityDraft
import com.example.lifeforge.data.model.DayEntryState
import com.example.lifeforge.data.model.HabitTrackingType
import com.example.lifeforge.data.model.ScheduleType
import com.example.lifeforge.data.model.TaskStatus
import com.example.lifeforge.data.model.TaskType
import com.example.lifeforge.domain.GamificationEngine
import com.example.lifeforge.domain.RecurrenceEngine
import com.example.lifeforge.util.DayBounds
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate
import java.time.ZoneId

class TaskRepository(
    private val taskDao: TaskDao,
    private val taskDayStateDao: TaskDayStateDao,
    private val categoryDao: CategoryDao,
    private val gamificationEngine: GamificationEngine,
    private val achievementDao: AchievementDao
) {
    private val zoneId: ZoneId = ZoneId.systemDefault()

    fun observeAll(): Flow<List<TaskEntity>> = taskDao.observeAll()

    fun observeAllActive(): Flow<List<TaskEntity>> = taskDao.observeAllActive()

    suspend fun getById(id: Long): TaskEntity? = taskDao.getById(id)

    suspend fun createFromDraft(draft: CreateActivityDraft): Long {
        val kind = draft.activityKind ?: ActivityKind.SINGLE_TASK
        val startDate = when (kind) {
            ActivityKind.SINGLE_TASK -> draft.singleDueDate
            else -> draft.scheduleStartDate
        }
        val startMillis = startDate.atStartOfDay(zoneId).toInstant().toEpochMilli()
        val rule = draft.recurrenceRule.copy(scheduleType = draft.scheduleType ?: ScheduleType.EVERYDAY)
        val taskType = when (kind) {
            ActivityKind.SINGLE_TASK -> TaskType.ONE_TIME
            ActivityKind.RECURRING_TASK -> TaskType.RECURRING
            ActivityKind.HABIT -> TaskType.DAILY
        }
        val task = TaskEntity(
            title = draft.name.trim(),
            categoryId = draft.categoryId,
            taskType = taskType,
            activityKind = kind,
            trackingType = draft.trackingType,
            checklistItems = draft.checklistItems.filter { it.isNotBlank() },
            recurrenceRuleJson = if (kind == ActivityKind.SINGLE_TASK) "" else RecurrenceEngine.toJson(rule),
            scheduleStartDate = startMillis,
            dueDate = if (kind == ActivityKind.SINGLE_TASK) startMillis else null,
            status = TaskStatus.PENDING
        )
        return insert(task)
    }

    suspend fun insert(task: TaskEntity): Long {
        val wasEmpty = taskDao.count() == 0
        val id = taskDao.insert(task)
        if (wasEmpty) achievementDao.unlockNow("first_task")
        return id
    }

    suspend fun update(task: TaskEntity) =
        taskDao.update(task.copy(updatedAt = System.currentTimeMillis()))

    suspend fun delete(id: Long) = taskDao.delete(id)

    suspend fun completeForDay(task: TaskEntity, date: LocalDate) {
        val bounds = DayBounds.forDate(date, zoneId)
        val existing = taskDayStateDao.getForTaskAndDay(task.id, bounds.startMillis, bounds.endMillis)
        val checklistDone = if (task.trackingType == HabitTrackingType.CHECKLIST) {
            List(task.checklistItems.size) { true }
        } else {
            emptyList()
        }
        taskDayStateDao.upsert(
            TaskDayStateEntity(
                id = existing?.id ?: 0,
                taskId = task.id,
                dayStartMillis = bounds.startMillis,
                state = DayEntryState.COMPLETED,
                checklistCompleted = checklistDone
            )
        )
        if (task.activityKind == ActivityKind.SINGLE_TASK) {
            taskDao.update(
                task.copy(
                    status = TaskStatus.COMPLETED,
                    completedAt = System.currentTimeMillis(),
                    updatedAt = System.currentTimeMillis()
                )
            )
        }
        gamificationEngine.awardTaskCompletion()
    }

    suspend fun toggleChecklistItemForDay(task: TaskEntity, date: LocalDate, index: Int) {
        if (task.trackingType != HabitTrackingType.CHECKLIST) return
        val bounds = DayBounds.forDate(date, zoneId)
        val existing = taskDayStateDao.getForTaskAndDay(task.id, bounds.startMillis, bounds.endMillis)
        val size = task.checklistItems.size
        if (index !in 0 until size) return

        val currentList = (existing?.checklistCompleted ?: emptyList()).toMutableList()
        while (currentList.size < size) currentList.add(false)
        currentList[index] = !currentList[index]

        val newState = if (currentList.all { it }) DayEntryState.COMPLETED else DayEntryState.PENDING
        taskDayStateDao.upsert(
            TaskDayStateEntity(
                id = existing?.id ?: 0,
                taskId = task.id,
                dayStartMillis = bounds.startMillis,
                state = newState,
                checklistCompleted = currentList.toList()
            )
        )

        // Single task overall status mirrors day completion.
        if (task.activityKind == ActivityKind.SINGLE_TASK) {
            if (newState == DayEntryState.COMPLETED) {
                taskDao.update(
                    task.copy(
                        status = TaskStatus.COMPLETED,
                        completedAt = System.currentTimeMillis(),
                        updatedAt = System.currentTimeMillis()
                    )
                )
            } else {
                taskDao.update(
                    task.copy(
                        status = TaskStatus.PENDING,
                        completedAt = null,
                        updatedAt = System.currentTimeMillis()
                    )
                )
            }
        }
    }

    suspend fun skipForDay(task: TaskEntity, date: LocalDate) {
        val bounds = DayBounds.forDate(date, zoneId)
        val existing = taskDayStateDao.getForTaskAndDay(task.id, bounds.startMillis, bounds.endMillis)
        taskDayStateDao.upsert(
            TaskDayStateEntity(
                id = existing?.id ?: 0,
                taskId = task.id,
                dayStartMillis = bounds.startMillis,
                state = DayEntryState.SKIPPED,
                checklistCompleted = existing?.checklistCompleted ?: emptyList()
            )
        )
    }

    suspend fun resetEntryForDay(task: TaskEntity, date: LocalDate) {
        val bounds = DayBounds.forDate(date, zoneId)
        taskDayStateDao.deleteForTaskAndDay(task.id, bounds.startMillis, bounds.endMillis)
        if (task.activityKind == ActivityKind.SINGLE_TASK) {
            taskDao.update(
                task.copy(
                    status = TaskStatus.PENDING,
                    completedAt = null,
                    updatedAt = System.currentTimeMillis()
                )
            )
        }
    }

    suspend fun updateReminder(task: TaskEntity, reminderMillis: Long?) {
        update(task.copy(reminderTimeMillis = reminderMillis))
    }

    suspend fun updateNote(task: TaskEntity, note: String) {
        update(task.copy(notes = note))
    }

    suspend fun reschedule(task: TaskEntity, newDate: LocalDate) {
        val millis = newDate.atStartOfDay(zoneId).toInstant().toEpochMilli()
        update(
            task.copy(
                dueDate = millis,
                scheduleStartDate = millis,
                status = TaskStatus.PENDING,
                completedAt = null
            )
        )
    }

    suspend fun updateFromDraft(taskId: Long, draft: CreateActivityDraft) {
        val existing = taskDao.getById(taskId) ?: return
        val kind = draft.activityKind ?: existing.activityKind
        val startDate = when (kind) {
            ActivityKind.SINGLE_TASK -> draft.singleDueDate
            else -> draft.scheduleStartDate
        }
        val startMillis = startDate.atStartOfDay(zoneId).toInstant().toEpochMilli()
        val rule = draft.recurrenceRule.copy(scheduleType = draft.scheduleType ?: ScheduleType.EVERYDAY)
        update(
            existing.copy(
                title = draft.name.trim(),
                categoryId = draft.categoryId,
                activityKind = kind,
                trackingType = draft.trackingType,
                checklistItems = draft.checklistItems.filter { it.isNotBlank() },
                recurrenceRuleJson = if (kind == ActivityKind.SINGLE_TASK) "" else RecurrenceEngine.toJson(rule),
                scheduleStartDate = startMillis,
                dueDate = if (kind == ActivityKind.SINGLE_TASK) startMillis else existing.dueDate
            )
        )
    }
}
