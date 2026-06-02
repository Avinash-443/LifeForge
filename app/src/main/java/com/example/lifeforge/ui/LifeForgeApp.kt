package com.example.lifeforge.ui

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.lifeforge.data.model.ActivityKind
import com.example.lifeforge.data.repository.TodayActivityItem
import com.example.lifeforge.ui.create.CreateActivityWizard
import com.example.lifeforge.ui.create.CreateActivityViewModel
import com.example.lifeforge.ui.create.CreateOptionsSheet
import com.example.lifeforge.ui.habits.HabitsScreen
import com.example.lifeforge.ui.navigation.TopLevelDestination
import com.example.lifeforge.ui.statistics.StatisticsScreen
import com.example.lifeforge.ui.tasks.TasksScreen
import com.example.lifeforge.ui.theme.LifeForgeColors
import com.example.lifeforge.ui.today.TodayScreen

@Composable
fun LifeForgeApp() {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route
    val factory = LocalLifeForgeViewModelFactory.current
    val createViewModel: CreateActivityViewModel = viewModel(factory = factory)

    var showCreateOptions by remember { mutableStateOf(false) }
    var wizardKind by remember { mutableStateOf<ActivityKind?>(null) }
    var showWizard by remember { mutableStateOf(false) }
    var editTaskId by remember { mutableStateOf<Long?>(null) }

    val fabScale by animateFloatAsState(
        targetValue = if (showCreateOptions) 0.92f else 1f,
        animationSpec = tween(200),
        label = "fabScale"
    )

    fun openWizard(kind: ActivityKind?, taskId: Long? = null, draft: com.example.lifeforge.data.model.CreateActivityDraft? = null) {
        if (taskId != null && draft != null) {
            createViewModel.startEdit(taskId, draft)
        } else {
            createViewModel.start(kind)
        }
        wizardKind = kind
        showWizard = true
        showCreateOptions = false
    }

    Scaffold(
        containerColor = LifeForgeColors.background,
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showCreateOptions = true },
                containerColor = LifeForgeColors.primary,
                contentColor = Color.White,
                modifier = Modifier.scale(fabScale)
            ) {
                Icon(Icons.Default.Add, contentDescription = "Create activity")
            }
        },
        bottomBar = {
            NavigationBar(containerColor = LifeForgeColors.card) {
                TopLevelDestination.entries.forEach { destination ->
                    val selected = currentRoute == destination.route
                    NavigationBarItem(
                        selected = selected,
                        onClick = {
                            navController.navigate(destination.route) {
                                popUpTo(navController.graph.findStartDestination().id) {
                                    saveState = true
                                }
                                launchSingleTop = true
                                restoreState = true
                            }
                        },
                        icon = {
                            Icon(
                                imageVector = if (selected) destination.selectedIcon
                                else destination.unselectedIcon,
                                contentDescription = destination.label
                            )
                        },
                        label = { Text(destination.label) },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = LifeForgeColors.primary,
                            selectedTextColor = LifeForgeColors.primary,
                            unselectedIconColor = LifeForgeColors.textSecondary,
                            unselectedTextColor = LifeForgeColors.textSecondary,
                            indicatorColor = LifeForgeColors.primary.copy(alpha = 0.15f)
                        )
                    )
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = TopLevelDestination.Today.route,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(TopLevelDestination.Today.route) {
                val todayVm: com.example.lifeforge.ui.today.TodayViewModel = viewModel(factory = factory)
                TodayScreen(
                    viewModel = todayVm,
                    onEditItem = { item ->
                        editTaskId = item.task.id
                        openWizard(item.task.activityKind, item.task.id, todayVm.draftForEdit(item))
                    },
                    onScheduleItem = { item ->
                        editTaskId = item.task.id
                        openWizard(item.task.activityKind, item.task.id, todayVm.draftForEdit(item))
                    }
                )
            }
            composable(TopLevelDestination.Habits.route) { HabitsScreen() }
            composable(TopLevelDestination.Tasks.route) { TasksScreen() }
            composable(TopLevelDestination.Categories.route) { com.example.lifeforge.ui.categories.CategoriesScreen() }
            composable(TopLevelDestination.Statistics.route) { StatisticsScreen() }
        }
    }

    if (showCreateOptions) {
        CreateOptionsSheet(
            onDismiss = { showCreateOptions = false },
            onSelect = { kind -> openWizard(kind) }
        )
    }

    if (showWizard) {
        CreateActivityWizard(
            onDismiss = {
                showWizard = false
                wizardKind = null
                editTaskId = null
            },
            preselectedKind = wizardKind,
            viewModel = createViewModel
        )
    }
}
