package com.example.lifeforge.ui.create

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.lifeforge.data.local.entity.CategoryEntity
import com.example.lifeforge.data.model.ActivityKind
import com.example.lifeforge.data.model.HabitTrackingType
import com.example.lifeforge.data.model.RecurrenceRule
import com.example.lifeforge.data.model.ScheduleType
import com.example.lifeforge.ui.lifeForgeViewModel
import com.example.lifeforge.ui.theme.LifeForgeColors
import java.time.DayOfWeek
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.TextStyle
import java.util.Locale

@Composable
fun CreateActivityWizard(
    onDismiss: () -> Unit,
    preselectedKind: ActivityKind? = null,
    viewModel: CreateActivityViewModel = lifeForgeViewModel()
) {
    val step by viewModel.step.collectAsStateWithLifecycle()
    val draft by viewModel.draft.collectAsStateWithLifecycle()
    val categories by viewModel.categories.collectAsStateWithLifecycle()

    androidx.compose.runtime.LaunchedEffect(preselectedKind) {
        viewModel.start(preselectedKind)
    }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth(0.95f)
                .clip(RoundedCornerShape(16.dp))
                .background(LifeForgeColors.card)
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (step != WizardStep.TYPE) {
                    IconButton(onClick = viewModel::back) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, null, tint = LifeForgeColors.textPrimary)
                    }
                } else {
                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.Close, null, tint = LifeForgeColors.textPrimary)
                    }
                }
                Text(
                    text = stepTitle(step),
                    color = LifeForgeColors.textPrimary,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.weight(1f)
                )
                IconButton(onClick = onDismiss) {
                    Icon(Icons.Default.Close, null, tint = LifeForgeColors.textSecondary)
                }
            }

            Column(
                modifier = Modifier
                    .verticalScroll(rememberScrollState())
                    .padding(vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                when (step) {
                    WizardStep.TYPE -> TypeStep(
                        onSelect = viewModel::setActivityKind
                    )
                    WizardStep.CATEGORY -> CategoryStep(
                        categories = categories,
                        onSelect = viewModel::setCategory
                    )
                    WizardStep.TRACKING -> TrackingStep(
                        draft = draft,
                        onSelectTracking = viewModel::setTracking,
                        onChecklistChange = viewModel::updateChecklistItem,
                        onAddItem = viewModel::addChecklistItem,
                        onRemoveItem = viewModel::removeChecklistItem,
                        onNext = viewModel::confirmTrackingAndNext
                    )
                    WizardStep.NAME -> NameStep(
                        name = draft.name,
                        onNameChange = viewModel::setName,
                        onNext = viewModel::confirmNameAndNext
                    )
                    WizardStep.SCHEDULE_TYPE -> ScheduleTypeStep(
                        onSelect = viewModel::setScheduleType
                    )
                    WizardStep.SCHEDULE_DETAIL -> ScheduleDetailStep(
                        scheduleType = draft.scheduleType ?: ScheduleType.EVERYDAY,
                        rule = draft.recurrenceRule,
                        startDate = draft.scheduleStartDate,
                        onRuleChange = viewModel::updateRecurrenceRule,
                        onStartDateChange = viewModel::setScheduleStartDate,
                        onSave = { viewModel.save(onDismiss) }
                    )
                    WizardStep.SINGLE_DATE -> SingleDateStep(
                        date = draft.singleDueDate,
                        onDateChange = viewModel::setSingleDueDate,
                        onSave = { viewModel.save(onDismiss) }
                    )
                }
            }
        }
    }
}

private fun stepTitle(step: WizardStep): String = when (step) {
    WizardStep.TYPE -> "Create Activity"
    WizardStep.CATEGORY -> "Choose Category"
    WizardStep.TRACKING -> "Tracking Type"
    WizardStep.NAME -> "Name"
    WizardStep.SCHEDULE_TYPE -> "Schedule"
    WizardStep.SCHEDULE_DETAIL -> "Schedule Details"
    WizardStep.SINGLE_DATE -> "Due Date"
}

@Composable
private fun TypeStep(onSelect: (ActivityKind) -> Unit) {
    WizardOption("Habit", "Recurring behavior with streaks") { onSelect(ActivityKind.HABIT) }
    WizardOption("Recurring Task", "Repeats on a schedule") { onSelect(ActivityKind.RECURRING_TASK) }
    WizardOption("Single Task", "One-time activity") { onSelect(ActivityKind.SINGLE_TASK) }
}

@Composable
private fun CategoryStep(categories: List<CategoryEntity>, onSelect: (CategoryEntity) -> Unit) {
    categories.forEach { cat ->
        WizardOption(cat.name, cat.description.ifBlank { "Tap to select" }) { onSelect(cat) }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun TrackingStep(
    draft: com.example.lifeforge.data.model.CreateActivityDraft,
    onSelectTracking: (HabitTrackingType) -> Unit,
    onChecklistChange: (Int, String) -> Unit,
    onAddItem: () -> Unit,
    onRemoveItem: (Int) -> Unit,
    onNext: () -> Unit
) {
    Text("How do you want to track progress?", color = LifeForgeColors.textSecondary)
    WizardOption(
        "Yes or No",
        "Simple done / not done",
        selected = draft.trackingType == HabitTrackingType.YES_NO
    ) { onSelectTracking(HabitTrackingType.YES_NO) }
    WizardOption(
        "Checklist",
        "Evaluate based on sub-items",
        selected = draft.trackingType == HabitTrackingType.CHECKLIST
    ) { onSelectTracking(HabitTrackingType.CHECKLIST) }

    if (draft.trackingType == HabitTrackingType.CHECKLIST) {
        Text("Checklist items", color = LifeForgeColors.textPrimary, fontWeight = FontWeight.SemiBold)
        draft.checklistItems.forEachIndexed { index, item ->
            Row(verticalAlignment = Alignment.CenterVertically) {
                OutlinedTextField(
                    value = item,
                    onValueChange = { onChecklistChange(index, it) },
                    modifier = Modifier.weight(1f),
                    placeholder = { Text("Sub-item ${index + 1}") },
                    colors = fieldColors()
                )
                if (draft.checklistItems.size > 1) {
                    IconButton(onClick = { onRemoveItem(index) }) {
                        Icon(Icons.Default.Delete, null, tint = LifeForgeColors.danger)
                    }
                }
            }
        }
        TextButton(onClick = onAddItem) {
            Icon(Icons.Default.Add, null, tint = LifeForgeColors.primary)
            Text("Add item", color = LifeForgeColors.primary)
        }
    }

    Button(
        onClick = onNext,
        modifier = Modifier.fillMaxWidth(),
        colors = ButtonDefaults.buttonColors(containerColor = LifeForgeColors.primary)
    ) {
        Text("Continue")
    }
}

@Composable
private fun NameStep(name: String, onNameChange: (String) -> Unit, onNext: () -> Unit) {
    OutlinedTextField(
        value = name,
        onValueChange = onNameChange,
        label = { Text("Name") },
        modifier = Modifier.fillMaxWidth(),
        colors = fieldColors()
    )
    Button(
        onClick = onNext,
        enabled = name.isNotBlank(),
        modifier = Modifier.fillMaxWidth(),
        colors = ButtonDefaults.buttonColors(containerColor = LifeForgeColors.primary)
    ) {
        Text("Continue")
    }
}

@Composable
private fun ScheduleTypeStep(onSelect: (ScheduleType) -> Unit) {
    WizardOption("Every day", "Shows every day") { onSelect(ScheduleType.EVERYDAY) }
    WizardOption("Specific days of the week", "Mon, Tue, …") { onSelect(ScheduleType.WEEKLY) }
    WizardOption("Specific days of the month", "1st, 15th, …") { onSelect(ScheduleType.MONTHLY) }
    WizardOption("Specific days of the year", "Holidays, birthdays") { onSelect(ScheduleType.YEARLY) }
    WizardOption("Some days per period", "e.g. 3 days every 7 days") { onSelect(ScheduleType.DAYS_PER_PERIOD) }
    WizardOption("Repeat every N days", "Every 2 days, 3 days, …") { onSelect(ScheduleType.REPEAT_INTERVAL) }
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
private fun ScheduleDetailStep(
    scheduleType: ScheduleType,
    rule: RecurrenceRule,
    startDate: LocalDate,
    onRuleChange: (RecurrenceRule) -> Unit,
    onStartDateChange: (LocalDate) -> Unit,
    onSave: () -> Unit
) {
    var showDatePicker by remember { mutableStateOf(false) }
    Text("Starts: $startDate", color = LifeForgeColors.textSecondary, modifier = Modifier.clickable { showDatePicker = true })
    if (showDatePicker) {
        val state = rememberDatePickerState(
            initialSelectedDateMillis = startDate.atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli()
        )
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(onClick = {
                    state.selectedDateMillis?.let {
                        onStartDateChange(Instant.ofEpochMilli(it).atZone(ZoneId.systemDefault()).toLocalDate())
                    }
                    showDatePicker = false
                }) { Text("OK") }
            }
        ) { DatePicker(state = state) }
    }

    when (scheduleType) {
        ScheduleType.EVERYDAY -> Text("This activity appears every day.", color = LifeForgeColors.textSecondary)
        ScheduleType.WEEKLY -> {
            Text("Select weekdays", color = LifeForgeColors.textPrimary)
            FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                DayOfWeek.entries.forEach { dow ->
                    val value = dow.value
                    val selected = value in rule.weekdays
                    FilterChip(
                        selected = selected,
                        onClick = {
                            val updated = if (selected) rule.weekdays - value else rule.weekdays + value
                            onRuleChange(rule.copy(weekdays = updated.sorted()))
                        },
                        label = { Text(dow.getDisplayName(TextStyle.SHORT, Locale.getDefault())) }
                    )
                }
            }
        }
        ScheduleType.MONTHLY -> {
            Text("Enter days of month (1–31), comma-separated", color = LifeForgeColors.textSecondary)
            var text by remember(rule.monthDays) {
                mutableStateOf(rule.monthDays.joinToString(", "))
            }
            OutlinedTextField(
                value = text,
                onValueChange = { raw ->
                    text = raw
                    val days = raw.split(",").mapNotNull { it.trim().toIntOrNull() }.filter { it in 1..31 }
                    onRuleChange(rule.copy(monthDays = days))
                },
                modifier = Modifier.fillMaxWidth(),
                colors = fieldColors()
            )
        }
        ScheduleType.YEARLY -> {
            Text("Enter dates as MM-dd, comma-separated (e.g. 01-01, 12-25)", color = LifeForgeColors.textSecondary)
            var text by remember(rule.yearDates) {
                mutableStateOf(rule.yearDates.joinToString(", "))
            }
            OutlinedTextField(
                value = text,
                onValueChange = { raw ->
                    text = raw
                    onRuleChange(rule.copy(yearDates = raw.split(",").map { it.trim() }.filter { it.isNotBlank() }))
                },
                modifier = Modifier.fillMaxWidth(),
                colors = fieldColors()
            )
        }
        ScheduleType.DAYS_PER_PERIOD -> {
            IntField("Days per period", rule.daysPerPeriod) {
                onRuleChange(rule.copy(daysPerPeriod = it.coerceAtLeast(1)))
            }
            IntField("Period length (days)", rule.periodLengthDays) {
                onRuleChange(rule.copy(periodLengthDays = it.coerceAtLeast(1)))
            }
        }
        ScheduleType.REPEAT_INTERVAL -> {
            IntField("Repeat every (days)", rule.repeatEveryDays) {
                onRuleChange(rule.copy(repeatEveryDays = it.coerceAtLeast(1)))
            }
        }
    }

    Button(
        onClick = onSave,
        modifier = Modifier.fillMaxWidth(),
        colors = ButtonDefaults.buttonColors(containerColor = LifeForgeColors.primary)
    ) {
        Text("Create")
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SingleDateStep(date: LocalDate, onDateChange: (LocalDate) -> Unit, onSave: () -> Unit) {
    var showPicker by remember { mutableStateOf(false) }
    Text("Due date: $date", color = LifeForgeColors.textPrimary, modifier = Modifier.clickable { showPicker = true })
    if (showPicker) {
        val state = rememberDatePickerState(
            initialSelectedDateMillis = date.atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli()
        )
        DatePickerDialog(
            onDismissRequest = { showPicker = false },
            confirmButton = {
                TextButton(onClick = {
                    state.selectedDateMillis?.let {
                        onDateChange(Instant.ofEpochMilli(it).atZone(ZoneId.systemDefault()).toLocalDate())
                    }
                    showPicker = false
                }) { Text("OK") }
            }
        ) { DatePicker(state = state) }
    }
    Button(
        onClick = onSave,
        modifier = Modifier.fillMaxWidth(),
        colors = ButtonDefaults.buttonColors(containerColor = LifeForgeColors.primary)
    ) {
        Text("Create Task")
    }
}

@Composable
private fun IntField(label: String, value: Int, onChange: (Int) -> Unit) {
    var text by remember(value) { mutableStateOf(value.toString()) }
    OutlinedTextField(
        value = text,
        onValueChange = {
            text = it
            it.toIntOrNull()?.let(onChange)
        },
        label = { Text(label) },
        modifier = Modifier.fillMaxWidth(),
        colors = fieldColors()
    )
}

@Composable
private fun WizardOption(
    title: String,
    subtitle: String,
    selected: Boolean = false,
    onClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(
                if (selected) LifeForgeColors.primary.copy(alpha = 0.2f) else LifeForgeColors.background
            )
            .clickable(onClick = onClick)
            .padding(16.dp)
    ) {
        Text(title, color = LifeForgeColors.textPrimary, fontWeight = FontWeight.SemiBold)
        Text(subtitle, color = LifeForgeColors.textSecondary, modifier = Modifier.padding(top = 4.dp))
    }
}

@Composable
private fun fieldColors() = OutlinedTextFieldDefaults.colors(
    focusedTextColor = LifeForgeColors.textPrimary,
    unfocusedTextColor = LifeForgeColors.textPrimary,
    focusedBorderColor = LifeForgeColors.primary,
    cursorColor = LifeForgeColors.primary
)
