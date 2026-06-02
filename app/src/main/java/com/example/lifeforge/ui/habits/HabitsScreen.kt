package com.example.lifeforge.ui.habits

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.lifeforge.ui.lifeForgeViewModel
import com.example.lifeforge.ui.theme.LifeForgeColors

@Composable
fun HabitsScreen(
    modifier: Modifier = Modifier,
    viewModel: HabitsViewModel = lifeForgeViewModel()
) {
    val habits by viewModel.habits.collectAsStateWithLifecycle()

    if (habits.isEmpty()) {
        Column(
            modifier = modifier
                .fillMaxSize()
                .background(LifeForgeColors.background)
                .padding(24.dp),
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = "No habits yet",
                style = MaterialTheme.typography.headlineSmall,
                color = LifeForgeColors.textPrimary,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "Tap + to create your first habit and start building streaks.",
                style = MaterialTheme.typography.bodyMedium,
                color = LifeForgeColors.textSecondary,
                modifier = Modifier.padding(top = 8.dp)
            )
        }
    } else {
        LazyColumn(
            modifier = modifier
                .fillMaxSize()
                .background(LifeForgeColors.background),
            contentPadding = PaddingValues(20.dp, bottom = 88.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(habits, key = { it.id }) { habit ->
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(LifeForgeColors.card)
                        .padding(16.dp)
                ) {
                    Text(
                        text = habit.name,
                        style = MaterialTheme.typography.titleMedium,
                        color = LifeForgeColors.textPrimary
                    )
                    Text(
                        text = "Streak ${habit.currentStreak} · Best ${habit.bestStreak}",
                        style = MaterialTheme.typography.bodySmall,
                        color = LifeForgeColors.textSecondary,
                        modifier = Modifier.padding(top = 4.dp)
                    )
                }
            }
        }
    }
}
