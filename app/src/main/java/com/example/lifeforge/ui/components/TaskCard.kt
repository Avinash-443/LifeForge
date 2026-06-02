package com.example.lifeforge.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Assignment
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.lifeforge.data.local.entity.TaskEntity
import com.example.lifeforge.data.model.TaskPriority
import com.example.lifeforge.ui.theme.LifeForgeColors

@Composable
fun TaskCard(
    task: TaskEntity,
    categoryName: String?,
    onComplete: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(LifeForgeColors.card)
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Icon(
            imageVector = Icons.Default.Assignment,
            contentDescription = null,
            tint = priorityColor(task.priority),
            modifier = Modifier.size(28.dp)
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
                overflow = TextOverflow.Ellipsis
            )
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                categoryName?.let {
                    Text(
                        text = it,
                        style = MaterialTheme.typography.labelSmall,
                        color = LifeForgeColors.secondary
                    )
                }
                Text(
                    text = task.priority.name.lowercase().replaceFirstChar { it.uppercase() },
                    style = MaterialTheme.typography.labelSmall,
                    color = LifeForgeColors.textSecondary
                )
            }
        }
        IconButton(onClick = onComplete) {
            Icon(
                imageVector = Icons.Outlined.CheckCircle,
                contentDescription = "Complete",
                tint = LifeForgeColors.success
            )
        }
    }
}

private fun priorityColor(priority: TaskPriority) = when (priority) {
    TaskPriority.HIGH -> LifeForgeColors.danger
    TaskPriority.MEDIUM -> LifeForgeColors.warning
    TaskPriority.LOW -> LifeForgeColors.textSecondary
}
