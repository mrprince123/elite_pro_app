package com.example.elite_fitness_app.core.navigation

/**
 * Sealed class defining all navigation routes in the app.
 */
sealed class Screen(val route: String) {
    // Auth
    data object Splash : Screen("splash")
    data object Login : Screen("login")
    data object Register : Screen("register")

    // Main (Bottom Nav)
    data object Home : Screen("home")
    data object MyWorkouts : Screen("my_workouts")
    data object ExerciseLibrary : Screen("exercise_library")
    data object Training : Screen("training")
    data object Account : Screen("account")

    // Detail screens
    data object WorkoutDetail : Screen("workout_detail/{workoutId}") {
        fun createRoute(workoutId: String) = "workout_detail/$workoutId"
    }
    data object ExerciseDetail : Screen("exercise_detail/{exerciseId}") {
        fun createRoute(exerciseId: String) = "exercise_detail/$exerciseId"
    }
    data object TrainingPlanDetail : Screen("training_plan_detail/{planId}") {
        fun createRoute(planId: String) = "training_plan_detail/$planId"
    }
    data object WorkoutSession : Screen("workout_session/{workoutId}") {
        fun createRoute(workoutId: String) = "workout_session/$workoutId"
    }
    data object Notifications : Screen("notifications")
    data object Settings : Screen("settings")
    data object Favorites : Screen("favorites")
}

/**
 * Navigation graph identifiers.
 */
object NavGraph {
    const val AUTH = "auth_graph"
    const val MAIN = "main_graph"
}
