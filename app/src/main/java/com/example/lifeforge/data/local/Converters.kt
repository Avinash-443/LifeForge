package com.example.lifeforge.data.local

import androidx.room.TypeConverter
import com.example.lifeforge.data.model.ActivityKind
import com.example.lifeforge.data.model.DayEntryState
import com.example.lifeforge.data.model.GoalStatus
import com.example.lifeforge.data.model.HabitFrequency
import com.example.lifeforge.data.model.HabitStatus
import com.example.lifeforge.data.model.HabitTrackingType
import com.example.lifeforge.data.model.TaskPriority
import com.example.lifeforge.data.model.TaskStatus
import com.example.lifeforge.data.model.TaskType
import com.example.lifeforge.data.model.TimeOfDay

class Converters {
    @TypeConverter
    fun fromHabitTrackingType(value: HabitTrackingType): String = value.name

    @TypeConverter
    fun toHabitTrackingType(value: String): HabitTrackingType = HabitTrackingType.valueOf(value)

    @TypeConverter
    fun fromHabitFrequency(value: HabitFrequency): String = value.name

    @TypeConverter
    fun toHabitFrequency(value: String): HabitFrequency = HabitFrequency.valueOf(value)

    @TypeConverter
    fun fromHabitStatus(value: HabitStatus): String = value.name

    @TypeConverter
    fun toHabitStatus(value: String): HabitStatus = HabitStatus.valueOf(value)

    @TypeConverter
    fun fromTaskType(value: TaskType): String = value.name

    @TypeConverter
    fun toTaskType(value: String): TaskType = TaskType.valueOf(value)

    @TypeConverter
    fun fromTaskPriority(value: TaskPriority): String = value.name

    @TypeConverter
    fun toTaskPriority(value: String): TaskPriority = TaskPriority.valueOf(value)

    @TypeConverter
    fun fromTaskStatus(value: TaskStatus): String = value.name

    @TypeConverter
    fun toTaskStatus(value: String): TaskStatus = TaskStatus.valueOf(value)

    @TypeConverter
    fun fromTimeOfDay(value: TimeOfDay): String = value.name

    @TypeConverter
    fun toTimeOfDay(value: String): TimeOfDay = TimeOfDay.valueOf(value)

    @TypeConverter
    fun fromGoalStatus(value: GoalStatus): String = value.name

    @TypeConverter
    fun toGoalStatus(value: String): GoalStatus = GoalStatus.valueOf(value)

    @TypeConverter
    fun fromActivityKind(value: ActivityKind): String = value.name

    @TypeConverter
    fun toActivityKind(value: String): ActivityKind = ActivityKind.valueOf(value)

    @TypeConverter
    fun fromDayEntryState(value: DayEntryState): String = value.name

    @TypeConverter
    fun toDayEntryState(value: String): DayEntryState = DayEntryState.valueOf(value)

    @TypeConverter
    fun fromBooleanList(value: List<Boolean>): String = value.joinToString(SEPARATOR) { if (it) "1" else "0" }

    @TypeConverter
    fun toBooleanList(value: String): List<Boolean> =
        if (value.isBlank()) emptyList() else value.split(SEPARATOR).map { it == "1" }

    @TypeConverter
    fun fromStringList(value: List<String>): String = value.joinToString(SEPARATOR)

    @TypeConverter
    fun toStringList(value: String): List<String> =
        if (value.isBlank()) emptyList() else value.split(SEPARATOR)

    companion object {
        private const val SEPARATOR = "|||"
    }
}
