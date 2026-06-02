package com.example.lifeforge.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.lifeforge.di.AppContainer
import com.example.lifeforge.ui.categories.CategoriesViewModel
import com.example.lifeforge.ui.create.CreateActivityViewModel
import com.example.lifeforge.ui.habits.HabitsViewModel
import com.example.lifeforge.ui.statistics.StatisticsViewModel
import com.example.lifeforge.ui.tasks.TasksViewModel
import com.example.lifeforge.ui.today.TodayViewModel

class LifeForgeViewModelFactory : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return when {
            modelClass.isAssignableFrom(TodayViewModel::class.java) -> TodayViewModel(
                AppContainer.todayRepository,
                AppContainer.taskRepository,
                AppContainer.appInitializer
            )
            modelClass.isAssignableFrom(HabitsViewModel::class.java) -> HabitsViewModel(
                AppContainer.habitRepository
            )
            modelClass.isAssignableFrom(TasksViewModel::class.java) -> TasksViewModel(
                AppContainer.taskRepository
            )
            modelClass.isAssignableFrom(CategoriesViewModel::class.java) -> CategoriesViewModel(
                AppContainer.categoryRepository
            )
            modelClass.isAssignableFrom(StatisticsViewModel::class.java) -> StatisticsViewModel(
                AppContainer.userRepository,
                AppContainer.analyticsRepository
            )
            modelClass.isAssignableFrom(CreateActivityViewModel::class.java) -> CreateActivityViewModel(
                AppContainer.taskRepository,
                AppContainer.categoryRepository
            )
            modelClass.isAssignableFrom(MainViewModel::class.java) -> MainViewModel(
                AppContainer.taskRepository,
                AppContainer.habitRepository,
                AppContainer.goalRepository
            )
            else -> error("Unknown ViewModel: ${modelClass.name}")
        } as T
    }
}
