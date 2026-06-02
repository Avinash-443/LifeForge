package com.example.lifeforge.ui.create

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.scaleIn
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.lifeforge.data.model.ActivityKind
import com.example.lifeforge.ui.theme.LifeForgeColors

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateOptionsSheet(
    onDismiss: () -> Unit,
    onSelect: (ActivityKind) -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = LifeForgeColors.card
    ) {
        AnimatedVisibility(visible = true, enter = scaleIn() + fadeIn()) {
            Column(modifier = Modifier.padding(bottom = 32.dp)) {
                Text(
                    text = "Create Activity",
                    color = LifeForgeColors.textPrimary,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(horizontal = 24.dp, vertical = 8.dp)
                )
                optionRow("Habit", "Build consistency over time") {
                    onSelect(ActivityKind.HABIT)
                }
                optionRow("Recurring Task", "Repeats on your schedule") {
                    onSelect(ActivityKind.RECURRING_TASK)
                }
                optionRow("Single Task", "One-time activity") {
                    onSelect(ActivityKind.SINGLE_TASK)
                }
            }
        }
    }
}

@Composable
private fun optionRow(title: String, subtitle: String, onClick: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 24.dp, vertical = 14.dp)
    ) {
        Text(title, color = LifeForgeColors.textPrimary, fontWeight = FontWeight.SemiBold)
        Text(subtitle, color = LifeForgeColors.textSecondary, modifier = Modifier.padding(top = 4.dp))
    }
}
