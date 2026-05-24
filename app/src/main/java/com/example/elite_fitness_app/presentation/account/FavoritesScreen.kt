package com.example.elite_fitness_app.presentation.account

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DirectionsRun
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.SelfImprovement
import androidx.compose.material3.*
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.elite_fitness_app.domain.model.Exercise
import com.example.elite_fitness_app.domain.model.Workout
import com.example.elite_fitness_app.presentation.components.EliteTopBar
import com.example.elite_fitness_app.presentation.components.EmptyState
import com.example.elite_fitness_app.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FavoritesScreen(
    viewModel: FavoritesViewModel,
    onNavigateBack: () -> Unit,
    onNavigateToWorkoutDetail: (String) -> Unit,
    onNavigateToExerciseDetail: (String) -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    var selectedTab by remember { mutableStateOf(0) }
    val tabs = listOf("Workouts", "Exercises")

    // Reload favorites when opening the screen
    LaunchedEffect(Unit) {
        viewModel.loadFavorites()
    }

    Scaffold(
        topBar = {
            EliteTopBar(
                title = "Favorites & Likes",
                onBackClick = onNavigateBack
            )
        },
        containerColor = Surface
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            // Tab Header
            TabRow(
                selectedTabIndex = selectedTab,
                containerColor = Surface,
                contentColor = Primary,
                indicator = { tabPositions ->
                    if (selectedTab < tabPositions.size) {
                        TabRowDefaults.SecondaryIndicator(
                            modifier = Modifier.tabIndicatorOffset(tabPositions[selectedTab]),
                            color = Primary
                        )
                    }
                }
            ) {
                tabs.forEachIndexed { index, title ->
                    Tab(
                        selected = selectedTab == index,
                        onClick = { selectedTab = index },
                        text = {
                            Text(
                                text = title,
                                fontWeight = if (selectedTab == index) FontWeight.Bold else FontWeight.Normal
                            )
                        },
                        selectedContentColor = Primary,
                        unselectedContentColor = OnSurfaceVariant
                    )
                }
            }

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .weight(1f)
            ) {
                if (uiState.isLoading && uiState.favorites.isEmpty()) {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator(color = Primary)
                    }
                } else {
                    when (selectedTab) {
                        0 -> WorkoutsTabContent(
                            workouts = uiState.likedWorkouts,
                            onWorkoutClick = onNavigateToWorkoutDetail,
                            onUnlikeClick = viewModel::toggleFavoriteWorkout
                        )
                        1 -> ExercisesTabContent(
                            exercises = uiState.likedExercises,
                            onExerciseClick = onNavigateToExerciseDetail,
                            onUnlikeClick = viewModel::toggleFavoriteExercise
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun WorkoutsTabContent(
    workouts: List<Workout>,
    onWorkoutClick: (String) -> Unit,
    onUnlikeClick: (String) -> Unit
) {
    if (workouts.isEmpty()) {
        EmptyState(
            icon = Icons.Default.FitnessCenter,
            title = "No liked workouts yet",
            subtitle = "Workouts you favorite will appear here."
        )
    } else {
        LazyColumn(
            contentPadding = PaddingValues(horizontal = 20.dp, vertical = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier.fillMaxSize()
        ) {
            items(workouts) { workout ->
                LikedWorkoutCard(
                    workout = workout,
                    onClick = { onWorkoutClick(workout.id) },
                    onUnlikeClick = { onUnlikeClick(workout.id) }
                )
            }
        }
    }
}

@Composable
fun ExercisesTabContent(
    exercises: List<Exercise>,
    onExerciseClick: (String) -> Unit,
    onUnlikeClick: (String) -> Unit
) {
    if (exercises.isEmpty()) {
        EmptyState(
            icon = Icons.Default.FitnessCenter,
            title = "No liked exercises yet",
            subtitle = "Exercises you favorite from the library will appear here."
        )
    } else {
        LazyColumn(
            contentPadding = PaddingValues(horizontal = 20.dp, vertical = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier.fillMaxSize()
        ) {
            items(exercises) { exercise ->
                LikedExerciseCard(
                    exercise = exercise,
                    onClick = { onExerciseClick(exercise.id) },
                    onUnlikeClick = { onUnlikeClick(exercise.id) }
                )
            }
        }
    }
}

@Composable
fun LikedWorkoutCard(
    workout: Workout,
    onClick: () -> Unit,
    onUnlikeClick: () -> Unit
) {
    val iconColors = listOf(
        Pair(Primary, PrimaryContainer.copy(alpha = 0.15f)),
        Pair(ActivityGreen, ActivityGreen.copy(alpha = 0.15f)),
        Pair(Tertiary, TertiaryFixedDim.copy(alpha = 0.15f)),
        Pair(ActivityRed, ActivityRed.copy(alpha = 0.15f)),
    )
    val colorPair = iconColors[workout.name.length % iconColors.size]
    val icons = listOf(Icons.Default.FitnessCenter, Icons.Default.DirectionsRun, Icons.Default.SelfImprovement)
    val icon = icons[workout.name.length % icons.size]

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(28.dp),
        colors = CardDefaults.cardColors(containerColor = SurfaceContainerLowest),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Activity icon
                Box(
                    modifier = Modifier
                        .size(44.dp)
                        .clip(RoundedCornerShape(14.dp))
                        .background(colorPair.second),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(icon, contentDescription = null, tint = colorPair.first, modifier = Modifier.size(24.dp))
                }

                // Unlike heart button
                IconButton(onClick = onUnlikeClick) {
                    Icon(
                        imageVector = Icons.Default.Favorite,
                        contentDescription = "Remove Favorite",
                        tint = ActivityRed
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = workout.name,
                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                color = OnSurface
            )

            if (workout.description.isNotEmpty()) {
                Text(
                    text = workout.description,
                    style = MaterialTheme.typography.bodyMedium,
                    color = OnSurfaceVariant,
                    maxLines = 2,
                    modifier = Modifier.padding(top = 4.dp)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            HorizontalDivider(color = OutlineVariant.copy(alpha = 0.3f))

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(text = "⏱️", style = MaterialTheme.typography.bodySmall)
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "${workout.estimatedDuration} min",
                        style = MaterialTheme.typography.bodyMedium,
                        color = OnSurfaceVariant
                    )
                }
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(text = "≡", style = MaterialTheme.typography.bodySmall, color = OnSurfaceVariant)
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "${workout.exercises.size} exercises",
                        style = MaterialTheme.typography.bodyMedium,
                        color = OnSurfaceVariant
                    )
                }
            }
        }
    }
}

@Composable
fun LikedExerciseCard(
    exercise: Exercise,
    onClick: () -> Unit,
    onUnlikeClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = SurfaceContainerLowest),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column {
            // Exercise image
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(160.dp)
            ) {
                AsyncImage(
                    model = exercise.gifUrl.ifBlank { "https://images.unsplash.com/photo-1534438327276-14e5300c3a48?q=80&w=600" },
                    contentDescription = exercise.name,
                    modifier = Modifier
                        .fillMaxSize()
                        .clip(RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)),
                    contentScale = ContentScale.Crop
                )

                // Unlike heart button (floats on image top-right)
                IconButton(
                    onClick = onUnlikeClick,
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(8.dp)
                        .background(Color.White.copy(alpha = 0.8f), CircleShape)
                        .size(36.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Favorite,
                        contentDescription = "Remove Favorite",
                        tint = ActivityRed,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }

            // Info section
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = exercise.name.replaceFirstChar { it.uppercase() },
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                        ),
                        color = OnSurface,
                        maxLines = 1
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Primary: ${exercise.bodyPart.replaceFirstChar { it.uppercase() }}, ${exercise.target.replaceFirstChar { it.uppercase() }}",
                        style = MaterialTheme.typography.bodySmall,
                        color = OnSurfaceVariant
                    )
                }
            }
        }
    }
}
