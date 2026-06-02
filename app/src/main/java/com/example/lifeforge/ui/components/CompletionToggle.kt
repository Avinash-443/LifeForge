package com.example.lifeforge.ui.components

import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import com.example.lifeforge.ui.theme.LifeForgeColors

@Composable
fun CompletionToggle(
    isCompleted: Boolean,
    enabled: Boolean,
    locked: Boolean,
    onToggle: () -> Unit,
    modifier: Modifier = Modifier
) {
    val shape = CircleShape
    val size = 28.dp
    val borderColor = when {
        locked -> LifeForgeColors.textSecondary.copy(alpha = 0.35f)
        isCompleted -> LifeForgeColors.success
        enabled -> LifeForgeColors.textSecondary.copy(alpha = 0.7f)
        else -> LifeForgeColors.textSecondary.copy(alpha = 0.35f)
    }

    Box(
        modifier = modifier
            .size(size)
            .clip(shape)
            .border(width = 2.dp, color = borderColor, shape = shape)
            .then(
                if (enabled && !locked) {
                    Modifier.clickable(onClick = onToggle)
                } else {
                    Modifier
                }
            )
            .semantics {
                contentDescription = when {
                    locked -> "Locked for future dates"
                    isCompleted -> "Completed, tap to undo"
                    else -> "Not completed, tap to mark done"
                }
            },
        contentAlignment = Alignment.Center
    ) {
        when {
            locked -> Icon(
                imageVector = Icons.Default.Lock,
                contentDescription = null,
                tint = LifeForgeColors.textSecondary.copy(alpha = 0.5f),
                modifier = Modifier.size(14.dp)
            )
            isCompleted -> Icon(
                imageVector = Icons.Default.Check,
                contentDescription = null,
                tint = LifeForgeColors.success,
                modifier = Modifier.size(18.dp)
            )
        }
    }
}
