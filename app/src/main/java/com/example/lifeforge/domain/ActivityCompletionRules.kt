package com.example.lifeforge.domain

import com.example.lifeforge.data.local.entity.TaskEntity
import com.example.lifeforge.data.model.ActivityKind
import java.time.LocalDate

object ActivityCompletionRules {
    /**
     * On future dates, only single tasks (deadlines) can be marked complete.
     * Habits and recurring tasks are locked until that day arrives.
     */
    fun canToggleCompletion(task: TaskEntity, viewDate: LocalDate, today: LocalDate = LocalDate.now()): Boolean {
        if (viewDate.isAfter(today)) {
            return task.activityKind == ActivityKind.SINGLE_TASK
        }
        return true
    }

    fun isFutureRecurringLocked(task: TaskEntity, viewDate: LocalDate, today: LocalDate = LocalDate.now()): Boolean =
        viewDate.isAfter(today) &&
            (task.activityKind == ActivityKind.HABIT || task.activityKind == ActivityKind.RECURRING_TASK)
}
