package com.example.lifeforge.data.repository

import com.example.lifeforge.data.local.entity.TaskDayStateEntity
import com.example.lifeforge.data.local.entity.TaskEntity
import com.example.lifeforge.data.model.HabitTrackingType
import com.example.lifeforge.domain.ActivityCompletionRules
import java.time.LocalDate

data class TodayActivityItem(
    val task: TaskEntity,
    val categoryName: String?,
    val dayState: TaskDayStateEntity?,
    val isCompleted: Boolean,
    val isSkipped: Boolean
) {
    val checklistProgress: String?
        get() {
            if (task.trackingType != HabitTrackingType.CHECKLIST || task.checklistItems.isEmpty()) return null
            val done = dayState?.checklistCompleted?.count { it } ?: 0
            return "$done/${task.checklistItems.size}"
        }

    fun canToggleCompletion(viewDate: LocalDate, today: LocalDate = LocalDate.now()): Boolean =
        ActivityCompletionRules.canToggleCompletion(task, viewDate, today)

    fun isFutureLocked(viewDate: LocalDate, today: LocalDate = LocalDate.now()): Boolean =
        ActivityCompletionRules.isFutureRecurringLocked(task, viewDate, today)
}

fun TodayActivityItem.isVisibleOnToday(): Boolean = !isSkipped
