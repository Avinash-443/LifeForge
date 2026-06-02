package com.example.lifeforge.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Assignment
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.Category
import androidx.compose.material.icons.filled.Repeat
import androidx.compose.material.icons.filled.Today
import androidx.compose.material.icons.outlined.Assignment
import androidx.compose.material.icons.outlined.BarChart
import androidx.compose.material.icons.outlined.Category
import androidx.compose.material.icons.outlined.Repeat
import androidx.compose.material.icons.outlined.Today
import androidx.compose.ui.graphics.vector.ImageVector

enum class TopLevelDestination(
    val route: String,
    val label: String,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector
) {
    Today("today", "Today", Icons.Filled.Today, Icons.Outlined.Today),
    Habits("habits", "Habits", Icons.Filled.Repeat, Icons.Outlined.Repeat),
    Tasks("tasks", "Tasks", Icons.Filled.Assignment, Icons.Outlined.Assignment),
    Categories("categories", "Categories", Icons.Filled.Category, Icons.Outlined.Category),
    Statistics("statistics", "Statistics", Icons.Filled.BarChart, Icons.Outlined.BarChart)
}

object Routes {
    const val CREATE_HABIT = "create_habit"
    const val CREATE_TASK = "create_task"
    const val CREATE_GOAL = "create_goal"
}
