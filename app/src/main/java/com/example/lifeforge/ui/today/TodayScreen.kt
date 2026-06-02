package com.example.lifeforge.ui.today

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.lifeforge.data.repository.TodayActivityItem
import com.example.lifeforge.ui.components.ActivityTaskCard
import com.example.lifeforge.ui.lifeForgeViewModel
import com.example.lifeforge.ui.theme.LifeForgeColors
import java.time.LocalDate
import java.time.format.TextStyle
import java.util.Locale

@Composable
fun TodayScreen(
    modifier: Modifier = Modifier,
    viewModel: TodayViewModel = lifeForgeViewModel(),
    onEditItem: (TodayActivityItem) -> Unit = {},
    onScheduleItem: (TodayActivityItem) -> Unit = {}
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val selectedItem by viewModel.selectedItem.collectAsStateWithLifecycle()
    var expandedTaskIds by remember { mutableStateOf(setOf<Long>()) }
    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(LifeForgeColors.background),
        contentPadding = PaddingValues(bottom = 88.dp)
    ) {
        item {
            Column(modifier = Modifier.padding(horizontal = 20.dp, vertical = 16.dp)) {
                Text(
                    text = state.dayName,
                    style = MaterialTheme.typography.headlineLarge,
                    color = LifeForgeColors.textPrimary,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = state.formattedDate,
                    style = MaterialTheme.typography.bodyLarge,
                    color = LifeForgeColors.textSecondary
                )
            }
        }

        item {
            CenteredCalendarStrip(
                days = state.calendarDays,
                todayIndex = state.todayIndexInCalendar,
                selected = state.selectedDate,
                onSelect = viewModel::selectDate
            )
        }

        item {
            ProgressSection(
                percent = state.completionPercent,
                tasksDone = state.tasksCompleted,
                tasksTotal = state.tasksTotal,
                habitsDone = state.habitsCompleted,
                habitsTotal = state.habitsTotal,
                modifier = Modifier.padding(horizontal = 20.dp, vertical = 8.dp)
            )
        }

        if (state.pendingItems.isEmpty() && state.completedItems.isEmpty()) {
            item {
                Text(
                    text = "No activities for this day. Tap + to create one.",
                    color = LifeForgeColors.textSecondary,
                    modifier = Modifier.padding(20.dp)
                )
            }
        }

        activitySection(
            title = "To Do",
            items = state.pendingItems,
            viewDate = state.selectedDate,
            onToggle = viewModel::toggleItem,
            onLongPress = viewModel::openItemActions,
            expandedTaskIds = expandedTaskIds,
            onToggleExpanded = { id ->
                expandedTaskIds =
                    if (id in expandedTaskIds) expandedTaskIds - id else expandedTaskIds + id
            },
            onToggleChecklistItem = viewModel::toggleChecklistItem
        )
        activitySection(
            title = "Completed",
            items = state.completedItems,
            viewDate = state.selectedDate,
            onToggle = viewModel::toggleItem,
            onLongPress = viewModel::openItemActions,
            expandedTaskIds = expandedTaskIds,
            onToggleExpanded = { id ->
                expandedTaskIds =
                    if (id in expandedTaskIds) expandedTaskIds - id else expandedTaskIds + id
            },
            onToggleChecklistItem = viewModel::toggleChecklistItem
        )
    }

    selectedItem?.let { item ->
        TaskActionSheet(
            item = item,
            onDismiss = viewModel::dismissItemActions,
            onAddReminder = {
                val tomorrow = System.currentTimeMillis() + 24 * 60 * 60 * 1000
                viewModel.updateReminder(item, tomorrow)
            },
            onAddNote = { viewModel.updateNote(item, it) },
            onReschedule = { viewModel.rescheduleItem(item, it) },
            onSkip = { viewModel.skipItem(item) },
            onReset = { viewModel.resetItem(item) },
            onDelete = { viewModel.deleteItem(item) },
            onEdit = {
                viewModel.dismissItemActions()
                onEditItem(item)
            },
            onSchedule = {
                viewModel.dismissItemActions()
                onScheduleItem(item)
            }
        )
    }
}

private fun androidx.compose.foundation.lazy.LazyListScope.activitySection(
    title: String,
    items: List<TodayActivityItem>,
    viewDate: LocalDate,
    onToggle: (TodayActivityItem) -> Unit,
    onLongPress: (TodayActivityItem) -> Unit,
    expandedTaskIds: Set<Long>,
    onToggleExpanded: (Long) -> Unit,
    onToggleChecklistItem: (TodayActivityItem, Int) -> Unit
) {
    if (items.isEmpty()) return
    item {
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium,
            color = LifeForgeColors.textPrimary,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.padding(horizontal = 20.dp, vertical = 12.dp)
        )
    }
    this.items(items, key = { "${it.task.id}-${it.isCompleted}-$viewDate" }) { activity ->
        ActivityTaskCard(
            item = activity,
            viewDate = viewDate,
            onToggle = { onToggle(activity) },
            onLongPress = { onLongPress(activity) },
            expanded = activity.task.id in expandedTaskIds,
            onToggleExpanded = { onToggleExpanded(activity.task.id) },
            onToggleChecklistItem = { index -> onToggleChecklistItem(activity, index) },
            modifier = Modifier.padding(horizontal = 20.dp, vertical = 4.dp)
        )
    }
}

@Composable
private fun CenteredCalendarStrip(
    days: List<LocalDate>,
    todayIndex: Int,
    selected: LocalDate,
    onSelect: (LocalDate) -> Unit
) {
    val today = remember { LocalDate.now() }
    val listState = rememberLazyListState()
    val itemWidth = 56.dp
    val density = LocalDensity.current

    BoxWithConstraints(modifier = Modifier.fillMaxWidth()) {
        val sidePadding = ((maxWidth - itemWidth) / 2).coerceAtLeast(0.dp)

        LaunchedEffect(days, todayIndex, maxWidth) {
            if (days.isEmpty()) return@LaunchedEffect
            val itemWidthPx = with(density) { itemWidth.roundToPx() }
            val viewportPx = with(density) { maxWidth.roundToPx() }
            val scrollOffset = -(viewportPx / 2 - itemWidthPx / 2)
            listState.scrollToItem(todayIndex, scrollOffset = scrollOffset)
        }

        LazyRow(
            state = listState,
            contentPadding = PaddingValues(horizontal = sidePadding),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            items(days, key = { it.toEpochDay() }) { day ->
                CalendarDayCell(
                    day = day,
                    isSelected = day == selected,
                    isToday = day == today,
                    onSelect = { onSelect(day) }
                )
            }
        }
    }
}

@Composable
private fun CalendarDayCell(
    day: LocalDate,
    isSelected: Boolean,
    isToday: Boolean,
    onSelect: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .width(56.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(
                when {
                    isSelected -> LifeForgeColors.primary.copy(alpha = 0.25f)
                    isToday -> LifeForgeColors.primary.copy(alpha = 0.12f)
                    else -> LifeForgeColors.card
                }
            )
            .clickable(onClick = onSelect)
            .padding(horizontal = 8.dp, vertical = 10.dp)
    ) {
        Text(
            text = day.dayOfWeek.getDisplayName(TextStyle.SHORT, Locale.getDefault()),
            style = MaterialTheme.typography.labelSmall,
            color = when {
                isSelected -> LifeForgeColors.primary
                isToday -> LifeForgeColors.primary.copy(alpha = 0.85f)
                else -> LifeForgeColors.textSecondary
            }
        )
        Box(
            modifier = Modifier
                .padding(top = 4.dp)
                .size(32.dp)
                .clip(CircleShape)
                .background(
                    when {
                        isSelected -> LifeForgeColors.primary
                        isToday -> LifeForgeColors.primary.copy(alpha = 0.35f)
                        else -> LifeForgeColors.background
                    }
                ),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = day.dayOfMonth.toString(),
                color = if (isSelected) LifeForgeColors.textPrimary else LifeForgeColors.textPrimary,
                fontWeight = if (isSelected || isToday) FontWeight.Bold else FontWeight.Normal
            )
        }
    }
}

@Composable
private fun ProgressSection(
    percent: Int,
    tasksDone: Int,
    tasksTotal: Int,
    habitsDone: Int,
    habitsTotal: Int,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(LifeForgeColors.card)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("Daily Progress", style = MaterialTheme.typography.titleMedium, color = LifeForgeColors.textPrimary)
            Text(
                text = "$percent%",
                style = MaterialTheme.typography.titleLarge,
                color = LifeForgeColors.primary,
                fontWeight = FontWeight.Bold
            )
        }
        LinearProgressIndicator(
            progress = { percent / 100f },
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(4.dp)),
            color = LifeForgeColors.primary,
            trackColor = LifeForgeColors.background
        )
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Text("Tasks $tasksDone/$tasksTotal", color = LifeForgeColors.textSecondary)
            Text("Habits $habitsDone/$habitsTotal", color = LifeForgeColors.textSecondary)
        }
    }
}
