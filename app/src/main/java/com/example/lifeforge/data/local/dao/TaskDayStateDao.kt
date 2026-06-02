package com.example.lifeforge.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.lifeforge.data.local.entity.TaskDayStateEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface TaskDayStateDao {
    @Query(
        """
        SELECT * FROM task_day_states
        WHERE dayStartMillis >= :dayStart AND dayStartMillis < :dayEnd
        """
    )
    fun observeForDay(dayStart: Long, dayEnd: Long): Flow<List<TaskDayStateEntity>>

    @Query(
        """
        SELECT COUNT(*) FROM task_day_states
        WHERE state = 'COMPLETED'
        AND dayStartMillis >= :rangeStart AND dayStartMillis < :rangeEnd
        """
    )
    fun observeCompletedCountInRange(rangeStart: Long, rangeEnd: Long): Flow<Int>

    @Query(
        """
        SELECT * FROM task_day_states
        WHERE taskId = :taskId AND dayStartMillis >= :dayStart AND dayStartMillis < :dayEnd
        LIMIT 1
        """
    )
    suspend fun getForTaskAndDay(taskId: Long, dayStart: Long, dayEnd: Long): TaskDayStateEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(state: TaskDayStateEntity): Long

    @Update
    suspend fun update(state: TaskDayStateEntity)

    @Query(
        """
        DELETE FROM task_day_states
        WHERE taskId = :taskId AND dayStartMillis >= :dayStart AND dayStartMillis < :dayEnd
        """
    )
    suspend fun deleteForTaskAndDay(taskId: Long, dayStart: Long, dayEnd: Long): Int
}
