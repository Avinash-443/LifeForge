package com.example.lifeforge.ui.today

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.lifeforge.data.model.CreateActivityDraft
import com.example.lifeforge.data.repository.AppInitializer
import com.example.lifeforge.data.repository.TaskRepository
import com.example.lifeforge.data.repository.TodayActivityItem
import com.example.lifeforge.data.repository.TodayRepository
import com.example.lifeforge.domain.ActivityCompletionRules
import com.example.lifeforge.domain.RecurrenceEngine
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.time.format.TextStyle
import java.util.Locale

data class TodayUiState(
    val selectedDate: LocalDate = LocalDate.now(),
    val completionPercent: Int = 0,
    val tasksCompleted: Int = 0,
    val tasksTotal: Int = 0,
    val habitsCompleted: Int = 0,
    val habitsTotal: Int = 0,
    val pendingItems: List<TodayActivityItem> = emptyList(),
    val completedItems: List<TodayActivityItem> = emptyList(),
    val calendarDays: List<LocalDate> = emptyList(),
    val todayIndexInCalendar: Int = 0
) {
    val formattedDate: String
        get() = selectedDate.format(DateTimeFormatter.ofPattern("MMMM d, yyyy"))

    val dayName: String
        get() = selectedDate.dayOfWeek.getDisplayName(TextStyle.FULL, Locale.getDefault())
}

@OptIn(ExperimentalCoroutinesApi::class)
class TodayViewModel(
    private val todayRepository: TodayRepository,
    private val taskRepository: TaskRepository,
    private val appInitializer: AppInitializer
) : ViewModel() {

    private val zoneId = ZoneId.systemDefault()
    private val selectedDate = MutableStateFlow(LocalDate.now())

    private val calendarDays = buildCalendarStrip()
    private val todayIndex = calendarDays.indexOf(LocalDate.now()).coerceAtLeast(0)

    private val _uiState = MutableStateFlow(
        TodayUiState(
            calendarDays = calendarDays,
            todayIndexInCalendar = todayIndex
        )
    )
    val uiState: StateFlow<TodayUiState> = _uiState.asStateFlow()

    private val _selectedItem = MutableStateFlow<TodayActivityItem?>(null)
    val selectedItem: StateFlow<TodayActivityItem?> = _selectedItem.asStateFlow()

    init {
        viewModelScope.launch {
            appInitializer.initializeIfNeeded()
            selectedDate
                .flatMapLatest { date -> todayRepository.observeDayActivities(date) }
                .collect { day ->
                    _uiState.value = TodayUiState(
                        selectedDate = day.date,
                        completionPercent = day.completionPercent,
                        tasksCompleted = day.tasksCompleted,
                        tasksTotal = day.tasksTotal,
                        habitsCompleted = day.habitsCompleted,
                        habitsTotal = day.habitsTotal,
                        pendingItems = day.pending,
                        completedItems = day.completed,
                        calendarDays = calendarDays,
                        todayIndexInCalendar = todayIndex
                    )
                }
        }
    }

    fun selectDate(date: LocalDate) {
        selectedDate.update { date }
    }

    fun toggleItem(item: TodayActivityItem) {
        val date = _uiState.value.selectedDate
        if (!ActivityCompletionRules.canToggleCompletion(item.task, date)) return
        viewModelScope.launch {
            if (item.isCompleted) {
                taskRepository.resetEntryForDay(item.task, date)
            } else {
                taskRepository.completeForDay(item.task, date)
            }
        }
    }

    fun toggleChecklistItem(item: TodayActivityItem, index: Int) {
        val date = _uiState.value.selectedDate
        if (!ActivityCompletionRules.canToggleCompletion(item.task, date)) return
        viewModelScope.launch {
            taskRepository.toggleChecklistItemForDay(item.task, date, index)
        }
    }

    fun openItemActions(item: TodayActivityItem) {
        _selectedItem.value = item
    }

    fun dismissItemActions() {
        _selectedItem.value = null
    }

    fun skipItem(item: TodayActivityItem) {
        viewModelScope.launch {
            taskRepository.skipForDay(item.task, _uiState.value.selectedDate)
            dismissItemActions()
        }
    }

    fun resetItem(item: TodayActivityItem) {
        viewModelScope.launch {
            taskRepository.resetEntryForDay(item.task, _uiState.value.selectedDate)
            dismissItemActions()
        }
    }

    fun deleteItem(item: TodayActivityItem) {
        viewModelScope.launch {
            taskRepository.delete(item.task.id)
            dismissItemActions()
        }
    }

    fun updateNote(item: TodayActivityItem, note: String) {
        viewModelScope.launch {
            taskRepository.updateNote(item.task, note)
            dismissItemActions()
        }
    }

    fun updateReminder(item: TodayActivityItem, reminderMillis: Long?) {
        viewModelScope.launch {
            taskRepository.updateReminder(item.task, reminderMillis)
            dismissItemActions()
        }
    }

    fun rescheduleItem(item: TodayActivityItem, newDate: LocalDate) {
        viewModelScope.launch {
            taskRepository.reschedule(item.task, newDate)
            dismissItemActions()
        }
    }

    fun draftForEdit(item: TodayActivityItem): CreateActivityDraft {
        val task = item.task
        val rule = RecurrenceEngine.fromJson(task.recurrenceRuleJson)
        val anchor = task.scheduleStartDate?.let {
            Instant.ofEpochMilli(it).atZone(zoneId).toLocalDate()
        } ?: LocalDate.now()
        val due = task.dueDate?.let {
            Instant.ofEpochMilli(it).atZone(zoneId).toLocalDate()
        } ?: anchor
        return CreateActivityDraft(
            activityKind = task.activityKind,
            categoryId = task.categoryId,
            trackingType = task.trackingType,
            checklistItems = if (task.checklistItems.isEmpty() && task.trackingType == com.example.lifeforge.data.model.HabitTrackingType.CHECKLIST) {
                listOf("")
            } else {
                task.checklistItems
            },
            name = task.title,
            scheduleType = rule.scheduleType,
            recurrenceRule = rule,
            singleDueDate = due,
            scheduleStartDate = anchor
        )
    }

    companion object {
        private const val CALENDAR_DAYS_EACH_SIDE = 90

        fun buildCalendarStrip(): List<LocalDate> {
            val today = LocalDate.now()
            return (-CALENDAR_DAYS_EACH_SIDE..CALENDAR_DAYS_EACH_SIDE).map { offset ->
                today.plusDays(offset.toLong())
            }
        }
    }
}
