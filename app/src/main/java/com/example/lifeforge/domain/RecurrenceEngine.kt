package com.example.lifeforge.domain

import com.example.lifeforge.data.model.ActivityKind
import com.example.lifeforge.data.model.RecurrenceRule
import com.example.lifeforge.data.model.ScheduleType
import com.google.gson.Gson
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit

object RecurrenceEngine {
    private val gson = Gson()
    private val yearFormatter = DateTimeFormatter.ofPattern("MM-dd")

    fun toJson(rule: RecurrenceRule): String = gson.toJson(rule)

    fun fromJson(json: String?): RecurrenceRule {
        if (json.isNullOrBlank()) return RecurrenceRule()
        return runCatching { gson.fromJson(json, RecurrenceRule::class.java) }.getOrDefault(RecurrenceRule())
    }

    fun occursOnDate(
        activityKind: ActivityKind,
        recurrenceRuleJson: String,
        scheduleStartDate: LocalDate?,
        singleDueDate: LocalDate?,
        date: LocalDate
    ): Boolean {
        if (scheduleStartDate != null && date.isBefore(scheduleStartDate)) return false

        return when (activityKind) {
            ActivityKind.SINGLE_TASK -> {
                val due = singleDueDate ?: scheduleStartDate ?: return false
                due == date
            }
            ActivityKind.HABIT, ActivityKind.RECURRING_TASK -> {
                val rule = fromJson(recurrenceRuleJson)
                val anchor = scheduleStartDate ?: date
                matchesRule(rule, anchor, date)
            }
        }
    }

    private fun matchesRule(rule: RecurrenceRule, anchor: LocalDate, date: LocalDate): Boolean {
        if (date.isBefore(anchor)) return false
        return when (rule.scheduleType) {
            ScheduleType.EVERYDAY -> true
            ScheduleType.WEEKLY -> {
                val dow = date.dayOfWeek.value
                rule.weekdays.isEmpty() || dow in rule.weekdays
            }
            ScheduleType.MONTHLY -> {
                val dom = date.dayOfMonth
                rule.monthDays.isEmpty() || dom in rule.monthDays
            }
            ScheduleType.YEARLY -> {
                val key = date.format(yearFormatter)
                rule.yearDates.isEmpty() || key in rule.yearDates
            }
            ScheduleType.DAYS_PER_PERIOD -> {
                val daysBetween = ChronoUnit.DAYS.between(anchor, date).toInt()
                if (daysBetween < 0) return false
                val dayInPeriod = daysBetween % rule.periodLengthDays.coerceAtLeast(1)
                dayInPeriod < rule.daysPerPeriod.coerceAtLeast(1)
            }
            ScheduleType.REPEAT_INTERVAL -> {
                val daysBetween = ChronoUnit.DAYS.between(anchor, date)
                val interval = rule.repeatEveryDays.coerceAtLeast(1)
                daysBetween % interval == 0L
            }
        }
    }
}
