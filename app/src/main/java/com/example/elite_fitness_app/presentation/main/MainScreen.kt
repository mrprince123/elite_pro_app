package com.example.elite_fitness_app.presentation.main

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.LibraryBooks
import androidx.compose.material.icons.filled.ListAlt
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.elite_fitness_app.core.navigation.Screen
import com.example.elite_fitness_app.presentation.account.AccountScreen
import com.example.elite_fitness_app.presentation.account.AccountViewModel
import com.example.elite_fitness_app.presentation.exercise.ExerciseLibraryScreen
import com.example.elite_fitness_app.presentation.exercise.ExerciseViewModel
import com.example.elite_fitness_app.presentation.home.HomeScreen
import com.example.elite_fitness_app.presentation.home.HomeViewModel
import com.example.elite_fitness_app.presentation.training.TrainingScreen
import com.example.elite_fitness_app.presentation.training.TrainingViewModel
import com.example.elite_fitness_app.presentation.workout.MyWorkoutScreen
import com.example.elite_fitness_app.presentation.workout.WorkoutViewModel
import com.example.elite_fitness_app.ui.theme.*

sealed class BottomNavItem(val route: String, val icon: ImageVector, val label: String) {
    data object Home : BottomNavItem(Screen.Home.route, Icons.Default.Home, "Home")
    data object Workouts : BottomNavItem(Screen.MyWorkouts.route, Icons.Default.FitnessCenter, "Workouts")
    data object Library : BottomNavItem(Screen.ExerciseLibrary.route, Icons.Default.LibraryBooks, "Library")
    data object Training : BottomNavItem(Screen.Training.route, Icons.Default.ListAlt, "Training")
    data object Account : BottomNavItem(Screen.Account.route, Icons.Default.AccountCircle, "Profile")
}

@Composable
fun MainScreen(
    homeViewModel: HomeViewModel,
    workoutViewModel: WorkoutViewModel,
    exerciseViewModel: ExerciseViewModel,
    trainingViewModel: TrainingViewModel,
    accountViewModel: AccountViewModel,
    onNavigateToWorkoutDetail: (String) -> Unit,
    onNavigateToTrainingPlanDetail: (String) -> Unit,
    onNavigateToExerciseDetail: (String) -> Unit,
    onStartWorkoutSession: (String) -> Unit,
    onLogoutSuccess: () -> Unit,
    onNavigateToNotifications: () -> Unit,
    onNavigateToSettings: () -> Unit,
    onNavigateToFavorites: () -> Unit
) {
    val navController = rememberNavController()
    val workoutUiState by workoutViewModel.uiState.collectAsState()

    LaunchedEffect(workoutUiState.shouldNavigateToLibrary) {
        if (workoutUiState.shouldNavigateToLibrary) {
            navController.navigate(Screen.ExerciseLibrary.route) {
                popUpTo(navController.graph.startDestinationId) {
                    saveState = true
                }
                launchSingleTop = true
                restoreState = true
            }
            workoutViewModel.setNavigateToLibrary(false)
        }
    }

    val items = listOf(
        BottomNavItem.Home,
        BottomNavItem.Workouts,
        BottomNavItem.Library,
        BottomNavItem.Training,
        BottomNavItem.Account
    )

    Scaffold(
        bottomBar = {
            BottomNavigation(
                navController = navController,
                items = items,
                onItemClick = {
                    workoutViewModel.stopAddingExercise()
                }
            )
        },
        containerColor = Surface
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Screen.Home.route,
            modifier = Modifier.padding(bottom = innerPadding.calculateBottomPadding())
        ) {
            composable(Screen.Home.route) {
                HomeScreen(
                    viewModel = homeViewModel,
                    onNavigateToWorkoutDetail = onNavigateToWorkoutDetail,
                    onNavigateToTrainingPlanDetail = onNavigateToTrainingPlanDetail,
                    onStartWorkoutSession = onStartWorkoutSession,
                    onNavigateToNotifications = onNavigateToNotifications,
                    onNavigateToTraining = {
                        navController.navigate(Screen.Training.route) {
                            popUpTo(navController.graph.startDestinationId) {
                                saveState = true
                            }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                )
            }
            composable(Screen.MyWorkouts.route) {
                MyWorkoutScreen(
                    viewModel = workoutViewModel,
                    onNavigateToWorkoutDetail = onNavigateToWorkoutDetail,
                    onStartWorkoutSession = onStartWorkoutSession
                )
            }
            composable(Screen.ExerciseLibrary.route) {
                ExerciseLibraryScreen(
                    viewModel = exerciseViewModel,
                    onNavigateToDetail = onNavigateToExerciseDetail,
                    workoutViewModel = workoutViewModel
                )
            }
            composable(Screen.Training.route) {
                TrainingScreen(
                    viewModel = trainingViewModel,
                    onNavigateToPlanDetail = onNavigateToTrainingPlanDetail
                )
            }
            composable(Screen.Account.route) {
                AccountScreen(
                    viewModel = accountViewModel,
                    onLogoutSuccess = onLogoutSuccess,
                    onNavigateToSettings = onNavigateToSettings,
                    onNavigateToFavorites = onNavigateToFavorites
                )
            }
        }
    }
}

@Composable
fun BottomNavigation(
    navController: NavController,
    items: List<BottomNavItem>,
    onItemClick: () -> Unit
) {
    NavigationBar(
        modifier = Modifier
            .fillMaxWidth(),
        containerColor = SurfaceContainerLowest,
        tonalElevation = 2.dp
    ) {
        val navBackStackEntry by navController.currentBackStackEntryAsState()
        val currentRoute = navBackStackEntry?.destination?.route

        items.forEach { item ->
            NavigationBarItem(
                icon = { Icon(item.icon, contentDescription = item.label) },
                label = { Text(item.label, style = MaterialTheme.typography.labelSmall) },
                selected = currentRoute == item.route,
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = Primary,
                    selectedTextColor = Primary,
                    indicatorColor = PrimaryContainer.copy(alpha = 0.15f),
                    unselectedIconColor = OnSurfaceVariant,
                    unselectedTextColor = OnSurfaceVariant
                ),
                onClick = {
                    onItemClick()
                    if (currentRoute != item.route) {
                        navController.navigate(item.route) {
                            popUpTo(navController.graph.startDestinationId) {
                                saveState = true
                            }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                }
            )
        }
    }
}
