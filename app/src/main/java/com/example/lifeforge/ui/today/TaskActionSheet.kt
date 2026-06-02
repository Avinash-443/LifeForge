package com.example.lifeforge.ui.today

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Note
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.SkipNext
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
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
import com.example.lifeforge.data.repository.TodayActivityItem
import com.example.lifeforge.ui.theme.LifeForgeColors
import java.time.LocalDate

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TaskActionSheet(
    item: TodayActivityItem,
    onDismiss: () -> Unit,
    onAddReminder: () -> Unit,
    onAddNote: (String) -> Unit,
    onReschedule: (LocalDate) -> Unit,
    onSkip: () -> Unit,
    onReset: () -> Unit,
    onDelete: () -> Unit,
    onEdit: () -> Unit,
    onSchedule: () -> Unit
) {
    var showNoteDialog by remember { mutableStateOf(false) }
    var showRescheduleDialog by remember { mutableStateOf(false) }
    var showDeleteConfirm by remember { mutableStateOf(false) }
    var noteText by remember(item.task.notes) { mutableStateOf(item.task.notes) }

    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = LifeForgeColors.card
    ) {
        Column(modifier = Modifier.padding(bottom = 24.dp)) {
            Text(
                text = item.task.title,
                color = LifeForgeColors.textPrimary,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(horizontal = 24.dp, vertical = 8.dp)
            )
            item.categoryName?.let {
                Text(
                    text = it,
                    color = LifeForgeColors.secondary,
                    modifier = Modifier.padding(horizontal = 24.dp)
                )
            }

            actionRow(Icons.Default.Notifications, "Add reminder", onAddReminder)
            actionRow(Icons.Default.Note, "Add note") { showNoteDialog = true }
            actionRow(Icons.Default.CalendarMonth, "Reschedule") { showRescheduleDialog = true }
            actionRow(Icons.Default.SkipNext, "Skip", onSkip)
            actionRow(Icons.Default.Refresh, "Reset entry", onReset)

            HorizontalDivider(color = LifeForgeColors.textSecondary.copy(alpha = 0.2f))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = { showDeleteConfirm = true }) {
                    Icon(Icons.Default.Delete, "Delete", tint = LifeForgeColors.danger)
                }
                IconButton(onClick = onEdit) {
                    Icon(Icons.Default.Edit, "Edit", tint = LifeForgeColors.primary)
                }
                IconButton(onClick = onSchedule) {
                    Icon(Icons.Default.CalendarMonth, "Schedule", tint = LifeForgeColors.primary)
                }
            }
        }
    }

    if (showNoteDialog) {
        AlertDialog(
            onDismissRequest = { showNoteDialog = false },
            title = { Text("Add note") },
            text = {
                OutlinedTextField(
                    value = noteText,
                    onValueChange = { noteText = it },
                    modifier = Modifier.fillMaxWidth(),
                    minLines = 3
                )
            },
            confirmButton = {
                TextButton(onClick = {
                    onAddNote(noteText)
                    showNoteDialog = false
                }) { Text("Save") }
            },
            dismissButton = {
                TextButton(onClick = { showNoteDialog = false }) { Text("Cancel") }
            }
        )
    }

    if (showRescheduleDialog) {
        var dayOffset by remember { mutableStateOf(1) }
        AlertDialog(
            onDismissRequest = { showRescheduleDialog = false },
            title = { Text("Reschedule") },
            text = { Text("Move to tomorrow?") },
            confirmButton = {
                TextButton(onClick = {
                    onReschedule(LocalDate.now().plusDays(dayOffset.toLong()))
                    showRescheduleDialog = false
                }) { Text("Tomorrow") }
            },
            dismissButton = {
                TextButton(onClick = { showRescheduleDialog = false }) { Text("Cancel") }
            }
        )
    }

    if (showDeleteConfirm) {
        AlertDialog(
            onDismissRequest = { showDeleteConfirm = false },
            title = { Text("Delete task?") },
            text = { Text("This removes \"${item.task.title}\" permanently.") },
            confirmButton = {
                TextButton(onClick = {
                    onDelete()
                    showDeleteConfirm = false
                }) { Text("Delete", color = LifeForgeColors.danger) }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteConfirm = false }) { Text("Cancel") }
            }
        )
    }
}

@Composable
private fun actionRow(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 4.dp)
            .clip(androidx.compose.foundation.shape.RoundedCornerShape(8.dp))
            .background(LifeForgeColors.background)
            .clickable(onClick = onClick)
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Icon(icon, null, tint = LifeForgeColors.primary)
        Text(text = label, color = LifeForgeColors.textPrimary)
    }
}
