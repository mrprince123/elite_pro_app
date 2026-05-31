package com.example.elite_fitness_app.core.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import androidx.navigation.navArgument
import com.example.elite_fitness_app.presentation.account.AccountViewModel
import com.example.elite_fitness_app.presentation.account.SettingsScreen
import com.example.elite_fitness_app.presentation.account.SettingsViewModel
import com.example.elite_fitness_app.presentation.account.FavoritesScreen
import com.example.elite_fitness_app.presentation.account.FavoritesViewModel
import com.example.elite_fitness_app.presentation.auth.AuthViewModel
import com.example.elite_fitness_app.presentation.auth.LoginScreen
import com.example.elite_fitness_app.presentation.auth.RegisterScreen
import com.example.elite_fitness_app.presentation.auth.SplashScreen
import com.example.elite_fitness_app.presentation.exercise.ExerciseDetailScreen
import com.example.elite_fitness_app.presentation.exercise.ExerciseLibraryScreen
import com.example.elite_fitness_app.presentation.exercise.ExerciseViewModel
import com.example.elite_fitness_app.presentation.home.HomeViewModel
import com.example.elite_fitness_app.presentation.main.MainScreen
import com.example.elite_fitness_app.presentation.notification.NotificationsScreen
import com.example.elite_fitness_app.presentation.notification.NotificationViewModel
import com.example.elite_fitness_app.presentation.session.SessionViewModel
import com.example.elite_fitness_app.presentation.session.WorkoutSessionScreen
import com.example.elite_fitness_app.presentation.training.TrainingPlanDetailScreen
import com.example.elite_fitness_app.presentation.training.TrainingViewModel
import com.example.elite_fitness_app.presentation.workout.WorkoutDetailScreen
import com.example.elite_fitness_app.presentation.workout.WorkoutViewModel
import com.example.elite_fitness_app.presentation.health.HealthViewModel

@Composable
fun AppNavHost(
    navController: NavHostController,
    startDestination: String = Screen.Splash.route
) {
    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {
        // Splash Screen
        composable(Screen.Splash.route) {
            val authViewModel: AuthViewModel = hiltViewModel()
            val authState by authViewModel.uiState.collectAsState()
            SplashScreen(
                isLoggedIn = authState.isLoggedIn,
                onNavigateToHome = {
                    navController.navigate(NavGraph.MAIN) {
                        popUpTo(Screen.Splash.route) { inclusive = true }
                    }
                },
                onNavigateToLogin = {
                    navController.navigate(NavGraph.AUTH) {
                        popUpTo(Screen.Splash.route) { inclusive = true }
                    }
                }
            )
        }

        // Auth Sub-Graph
        navigation(
            startDestination = Screen.Login.route,
            route = NavGraph.AUTH
        ) {
            composable(Screen.Login.route) { backStackEntry ->
                val authEntry = remember(backStackEntry) {
                    navController.getBackStackEntry(NavGraph.AUTH)
                }
                val authViewModel: AuthViewModel = hiltViewModel(authEntry)
                LoginScreen(
                    viewModel = authViewModel,
                    onNavigateToRegister = { navController.navigate(Screen.Register.route) },
                    onLoginSuccess = {
                        navController.navigate(NavGraph.MAIN) {
                            popUpTo(NavGraph.AUTH) { inclusive = true }
                        }
                    }
                )
            }
            composable(Screen.Register.route) { backStackEntry ->
                val authEntry = remember(backStackEntry) {
                    navController.getBackStackEntry(NavGraph.AUTH)
                }
                val authViewModel: AuthViewModel = hiltViewModel(authEntry)
                RegisterScreen(
                    viewModel = authViewModel,
                    onNavigateBack = { navController.popBackStack() },
                    onRegisterSuccess = {
                        navController.navigate(NavGraph.MAIN) {
                            popUpTo(NavGraph.AUTH) { inclusive = true }
                        }
                    }
                )
            }
        }

        // Main App Sub-Graph
        navigation(
            startDestination = "main_home",
            route = NavGraph.MAIN
        ) {
            composable("main_home") { backStackEntry ->
                val mainEntry = remember(backStackEntry) {
                    navController.getBackStackEntry(NavGraph.MAIN)
                }
                val homeViewModel: HomeViewModel = hiltViewModel(mainEntry)
                val workoutViewModel: WorkoutViewModel = hiltViewModel(mainEntry)
                val exerciseViewModel: ExerciseViewModel = hiltViewModel(mainEntry)
                val trainingViewModel: TrainingViewModel = hiltViewModel(mainEntry)
                val healthViewModel: HealthViewModel = hiltViewModel(mainEntry)
                val accountViewModel: AccountViewModel = hiltViewModel(mainEntry)
                
                MainScreen(
                    homeViewModel = homeViewModel,
                    workoutViewModel = workoutViewModel,
                    exerciseViewModel = exerciseViewModel,
                    trainingViewModel = trainingViewModel,
                    healthViewModel = healthViewModel,
                    accountViewModel = accountViewModel,
                    onNavigateToWorkoutDetail = { id ->
                        navController.navigate(Screen.WorkoutDetail.createRoute(id))
                    },
                    onNavigateToTrainingPlanDetail = { id ->
                        navController.navigate(Screen.TrainingPlanDetail.createRoute(id))
                    },
                    onNavigateToExerciseDetail = { id ->
                        navController.navigate(Screen.ExerciseDetail.createRoute(id))
                    },
                    onStartWorkoutSession = { id ->
                        navController.navigate(Screen.WorkoutSession.createRoute(id))
                    },
                    onLogoutSuccess = {
                        navController.navigate(NavGraph.AUTH) {
                            popUpTo(NavGraph.MAIN) { inclusive = true }
                        }
                    },
                    onNavigateToNotifications = {
                        navController.navigate(Screen.Notifications.route)
                    },
                    onNavigateToSettings = {
                        navController.navigate(Screen.Settings.route)
                    },
                    onNavigateToFavorites = {
                        navController.navigate(Screen.Favorites.route)
                    }
                )
            }

            // Detail Screens (Full Screen, outside bottom nav bar container)
            composable(
                route = Screen.WorkoutDetail.route,
                arguments = listOf(navArgument("workoutId") { type = NavType.StringType })
            ) { backStackEntry ->
                val id = backStackEntry.arguments?.getString("workoutId") ?: ""
                val mainEntry = remember(backStackEntry) {
                    navController.getBackStackEntry(NavGraph.MAIN)
                }
                val workoutViewModel: WorkoutViewModel = hiltViewModel(mainEntry)
                WorkoutDetailScreen(
                    workoutId = id,
                    viewModel = workoutViewModel,
                    onNavigateBack = { navController.popBackStack() },
                    onStartSession = { workoutId ->
                        navController.navigate(Screen.WorkoutSession.createRoute(workoutId))
                    },
                    onAddExerciseNavigate = {
                        navController.popBackStack("main_home", inclusive = false)
                    }
                )
            }

            composable(
                route = Screen.TrainingPlanDetail.route,
                arguments = listOf(navArgument("planId") { type = NavType.StringType })
            ) { backStackEntry ->
                val id = backStackEntry.arguments?.getString("planId") ?: ""
                val mainEntry = remember(backStackEntry) {
                    navController.getBackStackEntry(NavGraph.MAIN)
                }
                val trainingViewModel: TrainingViewModel = hiltViewModel(mainEntry)
                TrainingPlanDetailScreen(
                    planId = id,
                    viewModel = trainingViewModel,
                    onNavigateBack = { navController.popBackStack() },
                    onStartWorkout = { planId ->
                        navController.navigate(Screen.WorkoutSession.createRoute(planId))
                    }
                )
            }

            composable(
                route = Screen.ExerciseDetail.route,
                arguments = listOf(navArgument("exerciseId") { type = NavType.StringType })
            ) { backStackEntry ->
                val id = backStackEntry.arguments?.getString("exerciseId") ?: ""
                val mainEntry = remember(backStackEntry) {
                    navController.getBackStackEntry(NavGraph.MAIN)
                }
                val exerciseViewModel: ExerciseViewModel = hiltViewModel(mainEntry)
                ExerciseDetailScreen(
                    exerciseId = id,
                    viewModel = exerciseViewModel,
                    onNavigateBack = { navController.popBackStack() }
                )
            }

            composable(
                route = Screen.WorkoutSession.route,
                arguments = listOf(navArgument("workoutId") { type = NavType.StringType })
            ) { backStackEntry ->
                val id = backStackEntry.arguments?.getString("workoutId") ?: ""
                val mainEntry = remember(backStackEntry) {
                    navController.getBackStackEntry(NavGraph.MAIN)
                }
                val sessionViewModel: SessionViewModel = hiltViewModel()
                val homeViewModel: HomeViewModel = hiltViewModel(mainEntry)
                WorkoutSessionScreen(
                    workoutId = id,
                    viewModel = sessionViewModel,
                    onNavigateBack = { navController.popBackStack() },
                    onSessionFinished = {
                        // Refresh home data dashboard
                        homeViewModel.loadDashboardData()
                        navController.popBackStack("main_home", inclusive = false)
                    }
                )
            }

            composable(Screen.Notifications.route) {
                val notificationViewModel: NotificationViewModel = hiltViewModel()
                NotificationsScreen(
                    viewModel = notificationViewModel,
                    onNavigateBack = { navController.popBackStack() }
                )
            }

            composable(Screen.Settings.route) {
                val settingsViewModel: SettingsViewModel = hiltViewModel()
                SettingsScreen(
                    viewModel = settingsViewModel,
                    onNavigateBack = { navController.popBackStack() }
                )
            }

            composable(Screen.Favorites.route) {
                val favoritesViewModel: FavoritesViewModel = hiltViewModel()
                FavoritesScreen(
                    viewModel = favoritesViewModel,
                    onNavigateBack = { navController.popBackStack() },
                    onNavigateToWorkoutDetail = { id ->
                        navController.navigate(Screen.WorkoutDetail.createRoute(id))
                    },
                    onNavigateToExerciseDetail = { id ->
                        navController.navigate(Screen.ExerciseDetail.createRoute(id))
                    }
                )
            }
        }
    }
}
