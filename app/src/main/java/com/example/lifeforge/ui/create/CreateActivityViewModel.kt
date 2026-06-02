package com.example.lifeforge.ui.create

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.lifeforge.data.local.entity.CategoryEntity
import com.example.lifeforge.data.model.ActivityKind
import com.example.lifeforge.data.model.CreateActivityDraft
import com.example.lifeforge.data.model.HabitTrackingType
import com.example.lifeforge.data.model.RecurrenceRule
import com.example.lifeforge.data.model.ScheduleType
import com.example.lifeforge.data.repository.CategoryRepository
import com.example.lifeforge.data.repository.TaskRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDate

enum class WizardStep {
    TYPE,
    CATEGORY,
    TRACKING,
    NAME,
    SCHEDULE_TYPE,
    SCHEDULE_DETAIL,
    SINGLE_DATE
}

class CreateActivityViewModel(
    private val taskRepository: TaskRepository,
    categoryRepository: CategoryRepository
) : ViewModel() {

    val categories: StateFlow<List<CategoryEntity>> = categoryRepository.observeCategories()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    private val _draft = MutableStateFlow(CreateActivityDraft())
    val draft: StateFlow<CreateActivityDraft> = _draft.asStateFlow()

    private val _step = MutableStateFlow(WizardStep.TYPE)
    val step: StateFlow<WizardStep> = _step.asStateFlow()

    private val _editingTaskId = MutableStateFlow<Long?>(null)
    val editingTaskId: StateFlow<Long?> = _editingTaskId.asStateFlow()

    fun start(kind: ActivityKind? = null) {
        _editingTaskId.value = null
        _draft.value = CreateActivityDraft(activityKind = kind)
        _step.value = if (kind != null) WizardStep.CATEGORY else WizardStep.TYPE
    }

    fun startEdit(taskId: Long, existing: CreateActivityDraft) {
        _editingTaskId.value = taskId
        _draft.value = existing
        _step.value = WizardStep.NAME
    }

    fun setActivityKind(kind: ActivityKind) {
        _draft.update { it.copy(activityKind = kind) }
        _step.value = WizardStep.CATEGORY
    }

    fun setCategory(category: CategoryEntity) {
        _draft.update { it.copy(categoryId = category.id, categoryName = category.name) }
        _step.value = WizardStep.TRACKING
    }

    fun setTracking(type: HabitTrackingType) {
        _draft.update {
            it.copy(
                trackingType = type,
                checklistItems = if (type == HabitTrackingType.CHECKLIST) listOf("") else emptyList()
            )
        }
    }

    fun updateChecklistItem(index: Int, text: String) {
        _draft.update { d ->
            val items = d.checklistItems.toMutableList()
            if (index in items.indices) items[index] = text
            d.copy(checklistItems = items)
        }
    }

    fun addChecklistItem() {
        _draft.update { it.copy(checklistItems = it.checklistItems + "") }
    }

    fun removeChecklistItem(index: Int) {
        _draft.update { d ->
            d.copy(checklistItems = d.checklistItems.filterIndexed { i, _ -> i != index })
        }
    }

    fun setName(name: String) {
        _draft.update { it.copy(name = name) }
    }

    fun confirmTrackingAndNext() {
        _step.value = WizardStep.NAME
    }

    fun confirmNameAndNext() {
        val kind = _draft.value.activityKind
        _step.value = when (kind) {
            ActivityKind.SINGLE_TASK -> WizardStep.SINGLE_DATE
            else -> WizardStep.SCHEDULE_TYPE
        }
    }

    fun setScheduleType(type: ScheduleType) {
        _draft.update {
            it.copy(
                scheduleType = type,
                recurrenceRule = RecurrenceRule(scheduleType = type)
            )
        }
        _step.value = WizardStep.SCHEDULE_DETAIL
    }

    fun updateRecurrenceRule(rule: RecurrenceRule) {
        _draft.update { it.copy(recurrenceRule = rule) }
    }

    fun setSingleDueDate(date: LocalDate) {
        _draft.update { it.copy(singleDueDate = date) }
    }

    fun setScheduleStartDate(date: LocalDate) {
        _draft.update { it.copy(scheduleStartDate = date) }
    }

    fun back() {
        _step.value = when (_step.value) {
            WizardStep.TYPE -> WizardStep.TYPE
            WizardStep.CATEGORY -> WizardStep.TYPE
            WizardStep.TRACKING -> WizardStep.CATEGORY
            WizardStep.NAME -> WizardStep.TRACKING
            WizardStep.SCHEDULE_TYPE -> WizardStep.NAME
            WizardStep.SCHEDULE_DETAIL -> WizardStep.SCHEDULE_TYPE
            WizardStep.SINGLE_DATE -> WizardStep.NAME
        }
    }

    fun save(onComplete: () -> Unit) {
        viewModelScope.launch {
            val d = _draft.value
            if (d.name.isBlank() || d.categoryId == null || d.activityKind == null) return@launch
            val editId = _editingTaskId.value
            if (editId != null) {
                taskRepository.updateFromDraft(editId, d)
            } else {
                taskRepository.createFromDraft(d)
            }
            onComplete()
        }
    }
}
