package com.example.lifeforge.data.model

import java.time.LocalDate

data class RecurrenceRule(
    val scheduleType: ScheduleType = ScheduleType.EVERYDAY,
    val weekdays: List<Int> = emptyList(),
    val monthDays: List<Int> = emptyList(),
    val yearDates: List<String> = emptyList(),
    val daysPerPeriod: Int = 1,
    val periodLengthDays: Int = 7,
    val repeatEveryDays: Int = 1
)

data class CreateActivityDraft(
    val activityKind: ActivityKind? = null,
    val categoryId: Long? = null,
    val categoryName: String? = null,
    val trackingType: HabitTrackingType = HabitTrackingType.YES_NO,
    val checklistItems: List<String> = emptyList(),
    val name: String = "",
    val scheduleType: ScheduleType? = null,
    val recurrenceRule: RecurrenceRule = RecurrenceRule(),
    val singleDueDate: LocalDate = LocalDate.now(),
    val scheduleStartDate: LocalDate = LocalDate.now()
)
