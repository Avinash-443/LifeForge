package com.example.lifeforge.ui.tasks

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.lifeforge.data.local.entity.TaskEntity
import com.example.lifeforge.data.model.TaskPriority
import com.example.lifeforge.data.model.TaskStatus
import com.example.lifeforge.data.repository.TaskRepository
import com.example.lifeforge.util.DayBounds
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDate
enum class TaskFilter(val label: String) {
    TODAY("Today"),
    UPCOMING("Upcoming"),
    OVERDUE("Overdue"),
    COMPLETED("Completed"),
    HIGH_PRIORITY("High Priority"),
    PINNED("Pinned")
}

class TasksViewModel(
    private val taskRepository: TaskRepository
) : ViewModel() {

    private val _filter = MutableStateFlow(TaskFilter.TODAY)
    val filter: StateFlow<TaskFilter> = _filter

    val filteredTasks: StateFlow<List<TaskEntity>> = combine(
        taskRepository.observeAll(),
        _filter
    ) { tasks, filter ->
        val today = LocalDate.now()
        val todayBounds = DayBounds.forDate(today)
        when (filter) {
            TaskFilter.TODAY -> tasks.filter { task ->
                task.dueDate == null ||
                    (task.dueDate!! >= todayBounds.startMillis && task.dueDate < todayBounds.endMillis)
            }
            TaskFilter.UPCOMING -> tasks.filter { task ->
                task.dueDate != null && task.dueDate >= todayBounds.endMillis
            }
            TaskFilter.OVERDUE -> tasks.filter { task ->
                task.dueDate != null && task.dueDate < todayBounds.startMillis
            }
            TaskFilter.COMPLETED -> tasks.filter { it.status == TaskStatus.COMPLETED }
            TaskFilter.HIGH_PRIORITY -> tasks.filter { it.priority == TaskPriority.HIGH }
            TaskFilter.PINNED -> tasks.filter { it.isPinned }
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    fun setFilter(filter: TaskFilter) {
        _filter.update { filter }
    }

    fun completeTask(task: TaskEntity) {
        viewModelScope.launch {
            taskRepository.completeForDay(task, LocalDate.now())
        }
    }
}
