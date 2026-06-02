package com.example.lifeforge.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import com.example.lifeforge.data.model.DayEntryState

@Entity(
    tableName = "task_day_states",
    foreignKeys = [
        ForeignKey(
            entity = TaskEntity::class,
            parentColumns = ["id"],
            childColumns = ["taskId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index(value = ["taskId", "dayStartMillis"], unique = true)]
)
data class TaskDayStateEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val taskId: Long,
    val dayStartMillis: Long,
    val state: DayEntryState = DayEntryState.PENDING,
    val checklistCompleted: List<Boolean> = emptyList()
)
