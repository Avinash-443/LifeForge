package com.example.lifeforge.ui.tasks

import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.lifeforge.ui.lifeForgeViewModel
import com.example.lifeforge.ui.components.TaskCard
import com.example.lifeforge.ui.theme.LifeForgeColors

@Composable
fun TasksScreen(
    modifier: Modifier = Modifier,
    viewModel: TasksViewModel = lifeForgeViewModel()
) {
    val tasks by viewModel.filteredTasks.collectAsStateWithLifecycle()
    val filter by viewModel.filter.collectAsStateWithLifecycle()

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(LifeForgeColors.background)
    ) {
        Text(
            text = "Tasks",
            style = MaterialTheme.typography.headlineMedium,
            color = LifeForgeColors.textPrimary,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(20.dp, top = 20.dp, bottom = 8.dp)
        )
        Row(
            modifier = Modifier
                .horizontalScroll(rememberScrollState())
                .padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            TaskFilter.entries.forEach { option ->
                FilterChip(
                    selected = filter == option,
                    onClick = { viewModel.setFilter(option) },
                    label = { Text(option.label) },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = LifeForgeColors.primary.copy(alpha = 0.25f),
                        selectedLabelColor = LifeForgeColors.primary
                    )
                )
            }
        }
        if (tasks.isEmpty()) {
            Text(
                text = "No tasks match this filter.",
                style = MaterialTheme.typography.bodyMedium,
                color = LifeForgeColors.textSecondary,
                modifier = Modifier.padding(20.dp)
            )
        } else {
            LazyColumn(
                contentPadding = PaddingValues(16.dp, bottom = 88.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(tasks, key = { it.id }) { task ->
                    TaskCard(
                        task = task,
                        categoryName = null,
                        onComplete = { viewModel.completeTask(task) }
                    )
                }
            }
        }
    }
}
