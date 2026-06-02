package com.example.lifeforge.di

import android.content.Context
import androidx.room.Room
import com.example.lifeforge.data.local.LifeForgeDatabase
import com.example.lifeforge.data.repository.AnalyticsRepository
import com.example.lifeforge.data.repository.AppInitializer
import com.example.lifeforge.data.repository.CategoryRepository
import com.example.lifeforge.data.repository.GoalRepository
import com.example.lifeforge.data.repository.HabitRepository
import com.example.lifeforge.data.repository.TaskRepository
import com.example.lifeforge.data.repository.TodayRepository
import com.example.lifeforge.data.repository.UserRepository
import com.example.lifeforge.domain.GamificationEngine

object AppContainer {
    private lateinit var database: LifeForgeDatabase

    lateinit var appInitializer: AppInitializer
        private set
    lateinit var categoryRepository: CategoryRepository
        private set
    lateinit var habitRepository: HabitRepository
        private set
    lateinit var taskRepository: TaskRepository
        private set
    lateinit var goalRepository: GoalRepository
        private set
    lateinit var todayRepository: TodayRepository
        private set
    lateinit var userRepository: UserRepository
        private set
    lateinit var analyticsRepository: AnalyticsRepository
        private set

    fun init(context: Context) {
        if (::database.isInitialized) return
        database = Room.databaseBuilder(
            context.applicationContext,
            LifeForgeDatabase::class.java,
            LifeForgeDatabase.DATABASE_NAME
        ).fallbackToDestructiveMigration().build()

        val gamificationEngine = GamificationEngine(database.userProfileDao(), database.achievementDao())
        appInitializer = AppInitializer(
            database.categoryDao(),
            database.achievementDao(),
            database.userProfileDao()
        )
        categoryRepository = CategoryRepository(database.categoryDao())
        habitRepository = HabitRepository(
            database.habitDao(),
            database.habitCompletionDao(),
            gamificationEngine,
            database.achievementDao()
        )
        taskRepository = TaskRepository(
            database.taskDao(),
            database.taskDayStateDao(),
            database.categoryDao(),
            gamificationEngine,
            database.achievementDao()
        )
        goalRepository = GoalRepository(database.goalDao())
        todayRepository = TodayRepository(
            database.taskDao(),
            database.taskDayStateDao(),
            database.categoryDao(),
            database.habitDao(),
            database.habitCompletionDao()
        )
        userRepository = UserRepository(database.userProfileDao())
        analyticsRepository = AnalyticsRepository(
            database.taskDao(),
            database.taskDayStateDao(),
            database.habitDao(),
            database.habitCompletionDao()
        )
    }
}
