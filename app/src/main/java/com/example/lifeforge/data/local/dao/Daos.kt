package com.example.lifeforge.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.lifeforge.data.local.entity.AchievementEntity
import com.example.lifeforge.data.local.entity.CategoryEntity
import com.example.lifeforge.data.local.entity.GoalEntity
import com.example.lifeforge.data.local.entity.HabitCompletionEntity
import com.example.lifeforge.data.local.entity.HabitEntity
import com.example.lifeforge.data.local.entity.TaskEntity
import com.example.lifeforge.data.local.entity.UserProfileEntity
import com.example.lifeforge.data.model.HabitStatus
import com.example.lifeforge.data.model.TaskStatus
import kotlinx.coroutines.flow.Flow

@Dao
interface CategoryDao {
    @Query("SELECT * FROM categories ORDER BY name ASC")
    fun observeAll(): Flow<List<CategoryEntity>>

    @Query("SELECT COUNT(*) FROM categories")
    suspend fun count(): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(categories: List<CategoryEntity>): List<Long>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(category: CategoryEntity): Long

    @Update
    suspend fun update(category: CategoryEntity)

    @Query("DELETE FROM categories WHERE id = :id AND isDefault = 0")
    suspend fun deleteCustom(id: Long): Int
}

@Dao
interface HabitDao {
    @Query("SELECT * FROM habits WHERE status = :status ORDER BY priority DESC, name ASC")
    fun observeByStatus(status: HabitStatus): Flow<List<HabitEntity>>

    @Query("SELECT * FROM habits WHERE id = :id")
    suspend fun getById(id: Long): HabitEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(habit: HabitEntity): Long

    @Update
    suspend fun update(habit: HabitEntity)

    @Query("DELETE FROM habits WHERE id = :id")
    suspend fun delete(id: Long): Int

    @Query("SELECT COUNT(*) FROM habits")
    fun observeCount(): Flow<Int>

    @Query("SELECT COUNT(*) FROM habits")
    suspend fun count(): Int
}

@Dao
interface HabitCompletionDao {
    @Query(
        """
        SELECT * FROM habit_completions
        WHERE habitId = :habitId AND completedDate >= :dayStart AND completedDate < :dayEnd
        LIMIT 1
        """
    )
    suspend fun getForDay(habitId: Long, dayStart: Long, dayEnd: Long): HabitCompletionEntity?

    @Query(
        """
        SELECT COUNT(DISTINCT habitId) FROM habit_completions
        WHERE completedDate >= :dayStart AND completedDate < :dayEnd
        """
    )
    fun observeCompletedHabitCountForDay(dayStart: Long, dayEnd: Long): Flow<Int>

    @Query(
        """
        SELECT COUNT(*) FROM habit_completions
        WHERE completedDate >= :rangeStart AND completedDate < :rangeEnd
        """
    )
    fun observeCompletionCountInRange(rangeStart: Long, rangeEnd: Long): Flow<Int>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(completion: HabitCompletionEntity): Long
}

@Dao
interface TaskDao {
    @Query(
        """
        SELECT * FROM tasks
        WHERE status != 'COMPLETED' AND status != 'CANCELLED'
        AND (dueDate IS NULL OR (dueDate >= :dayStart AND dueDate < :dayEnd))
        ORDER BY isPinned DESC, priority DESC, dueDate ASC
        """
    )
    fun observeTasksForDay(dayStart: Long, dayEnd: Long): Flow<List<TaskEntity>>

    @Query(
        """
        SELECT * FROM tasks
        WHERE status = 'COMPLETED'
        AND completedAt IS NOT NULL
        AND completedAt >= :dayStart AND completedAt < :dayEnd
        ORDER BY completedAt DESC
        """
    )
    fun observeCompletedTasksForDay(dayStart: Long, dayEnd: Long): Flow<List<TaskEntity>>

    @Query("SELECT * FROM tasks WHERE status != 'CANCELLED' ORDER BY isPinned DESC, dueDate ASC")
    fun observeAllActive(): Flow<List<TaskEntity>>

    @Query("SELECT * FROM tasks WHERE status != 'CANCELLED' ORDER BY isPinned DESC, updatedAt DESC")
    fun observeAll(): Flow<List<TaskEntity>>

    @Query("SELECT * FROM tasks WHERE id = :id")
    suspend fun getById(id: Long): TaskEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(task: TaskEntity): Long

    @Update
    suspend fun update(task: TaskEntity)

    @Query("DELETE FROM tasks WHERE id = :id")
    suspend fun delete(id: Long): Int

    @Query("SELECT COUNT(*) FROM tasks WHERE status = 'COMPLETED'")
    fun observeCompletedCount(): Flow<Int>

    @Query("SELECT COUNT(*) FROM tasks WHERE status != 'CANCELLED'")
    fun observeTotalCount(): Flow<Int>

    @Query("SELECT COUNT(*) FROM tasks")
    suspend fun count(): Int
}

@Dao
interface GoalDao {
    @Query("SELECT * FROM goals WHERE status = 'ACTIVE' ORDER BY targetDate ASC")
    fun observeActive(): Flow<List<GoalEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(goal: GoalEntity): Long

    @Update
    suspend fun update(goal: GoalEntity)
}

@Dao
interface UserProfileDao {
    @Query("SELECT * FROM user_profile WHERE id = 1")
    fun observeProfile(): Flow<UserProfileEntity?>

    @Query("SELECT * FROM user_profile WHERE id = 1")
    suspend fun getProfile(): UserProfileEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(profile: UserProfileEntity)

    @Update
    suspend fun update(profile: UserProfileEntity)
}

@Dao
interface AchievementDao {
    @Query("SELECT * FROM achievements ORDER BY title ASC")
    fun observeAll(): Flow<List<AchievementEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(achievements: List<AchievementEntity>): List<Long>

    @Query("UPDATE achievements SET unlockedAt = :timestamp WHERE key = :key AND unlockedAt IS NULL")
    suspend fun unlock(key: String, timestamp: Long): Int
}
