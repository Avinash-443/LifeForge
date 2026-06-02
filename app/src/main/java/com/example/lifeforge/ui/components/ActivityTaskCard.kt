package com.example.lifeforge.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Checklist
import androidx.compose.material.icons.outlined.TaskAlt
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.lifeforge.data.model.ActivityKind
import com.example.lifeforge.data.model.HabitTrackingType
import com.example.lifeforge.data.repository.TodayActivityItem
import com.example.lifeforge.ui.theme.LifeForgeColors
import java.time.LocalDate

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ActivityTaskCard(
    item: TodayActivityItem,
    viewDate: LocalDate,
    onToggle: () -> Unit,
    onLongPress: () -> Unit,
    expanded: Boolean,
    onToggleExpanded: () -> Unit,
    onToggleChecklistItem: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    val task = item.task
    val canToggle = item.canToggleCompletion(viewDate)
    val locked = item.isFutureLocked(viewDate)
    val alpha = when {
        locked -> 0.55f
        item.isCompleted -> 0.75f
        else -> 1f
    }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .alpha(alpha)
            .clip(RoundedCornerShape(12.dp))
            .background(LifeForgeColors.card)
            .combinedClickable(
                onClick = {
                    if (task.trackingType == HabitTrackingType.CHECKLIST) onToggleExpanded()
                },
                onLongClick = onLongPress
            )
            .animateContentSize()
            .padding(12.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Icon(
                imageVector = if (task.trackingType == HabitTrackingType.CHECKLIST) {
                    Icons.Outlined.Checklist
                } else {
                    Icons.Outlined.TaskAlt
                },
                contentDescription = null,
                tint = kindColor(task.activityKind),
                modifier = Modifier.size(24.dp)
            )
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    text = task.title,
                    style = MaterialTheme.typography.titleMedium,
                    color = LifeForgeColors.textPrimary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    textDecoration = if (item.isCompleted) TextDecoration.LineThrough else null
                )
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    item.categoryName?.let {
                        Text(it, style = MaterialTheme.typography.labelSmall, color = LifeForgeColors.secondary)
                    }
                    Text(
                        text = kindLabel(task.activityKind),
                        style = MaterialTheme.typography.labelSmall,
                        color = LifeForgeColors.textSecondary
                    )
                    if (locked) {
                        Text(
                            text = "Locked",
                            style = MaterialTheme.typography.labelSmall,
                            color = LifeForgeColors.textSecondary.copy(alpha = 0.7f)
                        )
                    }
                    item.checklistProgress?.let {
                        Text(it, style = MaterialTheme.typography.labelSmall, color = LifeForgeColors.warning)
                    }
                }
            }
            CompletionToggle(
                isCompleted = item.isCompleted,
                enabled = canToggle,
                locked = locked,
                onToggle = onToggle
            )
        }

        AnimatedVisibility(visible = expanded && task.trackingType == HabitTrackingType.CHECKLIST) {
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                task.checklistItems.forEachIndexed { idx, label ->
                    val done = item.dayState?.checklistCompleted?.getOrNull(idx) == true
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(10.dp))
                            .background(LifeForgeColors.background.copy(alpha = 0.55f))
                            .padding(horizontal = 10.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Checkbox(
                            checked = done,
                            onCheckedChange = { onToggleChecklistItem(idx) },
                            enabled = canToggle && !locked
                        )
                        Text(
                            text = label,
                            color = LifeForgeColors.textPrimary,
                            style = MaterialTheme.typography.bodyMedium,
                            modifier = Modifier.weight(1f),
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }
                Text(
                    text = "Tap again to collapse",
                    color = LifeForgeColors.textSecondary,
                    style = MaterialTheme.typography.labelSmall
                )
            }
        }
    }
}

private fun kindColor(kind: ActivityKind) = when (kind) {
    ActivityKind.HABIT -> LifeForgeColors.primary
    ActivityKind.RECURRING_TASK -> LifeForgeColors.secondary
    ActivityKind.SINGLE_TASK -> LifeForgeColors.warning
}

private fun kindLabel(kind: ActivityKind) = when (kind) {
    ActivityKind.HABIT -> "Habit"
    ActivityKind.RECURRING_TASK -> "Recurring"
    ActivityKind.SINGLE_TASK -> "Single"
}
