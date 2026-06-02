package com.example.lifeforge.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.lifeforge.data.local.entity.GoalEntity
import com.example.lifeforge.data.local.entity.HabitEntity
import com.example.lifeforge.data.local.entity.TaskEntity
import com.example.lifeforge.data.repository.GoalRepository
import com.example.lifeforge.data.repository.HabitRepository
import com.example.lifeforge.data.repository.TaskRepository
import kotlinx.coroutines.launch

class MainViewModel(
    private val taskRepository: TaskRepository,
    private val habitRepository: HabitRepository,
    private val goalRepository: GoalRepository
) : ViewModel() {

    fun createTask(task: TaskEntity) {
        viewModelScope.launch { taskRepository.insert(task) }
    }

    fun createHabit(habit: HabitEntity) {
        viewModelScope.launch { habitRepository.insert(habit) }
    }

    fun createGoal(goal: GoalEntity) {
        viewModelScope.launch { goalRepository.insert(goal) }
    }
}
