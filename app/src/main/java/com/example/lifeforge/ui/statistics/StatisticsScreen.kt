package com.example.lifeforge.ui.statistics

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
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
fun StatisticsScreen(
    modifier: Modifier = Modifier,
    viewModel: StatisticsViewModel = lifeForgeViewModel()
) {
    val profile by viewModel.profile.collectAsStateWithLifecycle()
    val analytics by viewModel.analytics.collectAsStateWithLifecycle()

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(LifeForgeColors.background)
            .verticalScroll(rememberScrollState())
            .padding(20.dp, bottom = 88.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(
            text = "Statistics",
            style = MaterialTheme.typography.headlineMedium,
            color = LifeForgeColors.textPrimary,
            fontWeight = FontWeight.Bold
        )
        StatCard("Level", "${profile?.level ?: 1}")
        StatCard("Total XP", "${profile?.totalXp ?: 0}")
        StatCard("Coins", "${profile?.coins ?: 0}")
        StatCard("Total tasks", "${analytics?.totalTasks ?: 0}")
        StatCard("Completed single tasks", "${analytics?.completedSingleTasks ?: 0}")
        StatCard("Active habits", "${analytics?.activeHabits ?: 0}")
        StatCard("Last 7 days task completions", "${analytics?.last7DaysTaskCompletions ?: 0}")
        StatCard("Last 7 days habit completions", "${analytics?.last7DaysHabitCompletions ?: 0}")
        Text(
            text = "Charts (line, bar, pie, heat map) will use MPAndroidChart in a future update.",
            style = MaterialTheme.typography.bodyMedium,
            color = LifeForgeColors.textSecondary,
            modifier = Modifier.padding(top = 8.dp)
        )
    }
}

@Composable
private fun StatCard(label: String, value: String) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(LifeForgeColors.card)
            .padding(16.dp)
    ) {
        Text(text = label, style = MaterialTheme.typography.labelMedium, color = LifeForgeColors.textSecondary)
        Text(
            text = value,
            style = MaterialTheme.typography.headlineSmall,
            color = LifeForgeColors.primary,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(top = 4.dp)
        )
    }
}
