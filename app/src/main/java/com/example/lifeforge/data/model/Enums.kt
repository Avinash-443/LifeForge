package com.example.lifeforge.data.model

enum class HabitTrackingType {
    YES_NO,
    NUMERIC,
    TIMER,
    CHECKLIST,
    PERCENTAGE,
    CUSTOM_METRIC
}

enum class HabitFrequency {
    DAILY,
    WEEKLY,
    MONTHLY,
    YEARLY,
    CUSTOM
}

enum class HabitStatus {
    ACTIVE,
    PAUSED,
    ARCHIVED
}

enum class TaskType {
    ONE_TIME,
    DAILY,
    WEEKLY,
    MONTHLY,
    RECURRING
}

enum class TaskPriority {
    LOW,
    MEDIUM,
    HIGH
}

enum class TaskStatus {
    PENDING,
    IN_PROGRESS,
    COMPLETED,
    CANCELLED
}

enum class TimeOfDay {
    MORNING,
    AFTERNOON,
    EVENING,
    ANYTIME
}

enum class GoalStatus {
    ACTIVE,
    COMPLETED,
    ARCHIVED
}

enum class ActivityKind {
    HABIT,
    RECURRING_TASK,
    SINGLE_TASK
}

enum class ScheduleType {
    EVERYDAY,
    WEEKLY,
    MONTHLY,
    YEARLY,
    DAYS_PER_PERIOD,
    REPEAT_INTERVAL
}

enum class DayEntryState {
    PENDING,
    COMPLETED,
    SKIPPED
}
