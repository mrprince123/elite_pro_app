package com.example.elite_fitness_app.presentation.exercise

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import android.widget.Toast
import com.example.elite_fitness_app.domain.model.Exercise
import com.example.elite_fitness_app.presentation.workout.WorkoutViewModel
import com.example.elite_fitness_app.presentation.components.EliteChip
import com.example.elite_fitness_app.presentation.components.EliteSearchBar
import com.example.elite_fitness_app.presentation.components.EmptyState
import com.example.elite_fitness_app.presentation.components.ShimmerBox
import com.example.elite_fitness_app.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExerciseLibraryScreen(
    viewModel: ExerciseViewModel,
    onNavigateToDetail: (String) -> Unit,
    workoutViewModel: WorkoutViewModel? = null,
    onAddSuccessful: (() -> Unit)? = null
) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current
    val workoutUiState = workoutViewModel?.uiState?.collectAsState()?.value

    Scaffold(
        topBar = {
            Surface(color = Surface) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .statusBarsPadding()
                        .padding(horizontal = 20.dp, vertical = 12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Library",
                        style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold),
                        color = OnSurface,
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        },
        containerColor = Surface
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            // Search Bar
            EliteSearchBar(
                query = uiState.searchQuery,
                onQueryChange = viewModel::updateSearchQuery,
                placeholder = "Search exercises, muscles, gear...",
                modifier = Modifier.padding(horizontal = 20.dp, vertical = 8.dp)
            )

            // Body Part Tabs
            LazyRow(
                contentPadding = PaddingValues(horizontal = 20.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(uiState.bodyParts) { part ->
                    EliteChip(
                        label = part.replaceFirstChar { it.uppercase() },
                        selected = uiState.selectedBodyPart == part,
                        onClick = { viewModel.selectBodyPart(part) }
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Exercise List
            if (uiState.isLoading && uiState.exercises.isEmpty()) {
                Column(
                    modifier = Modifier.padding(horizontal = 20.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    repeat(4) {
                        ShimmerBox(modifier = Modifier.fillMaxWidth().height(240.dp))
                    }
                }
            } else if (uiState.exercises.isEmpty()) {
                EmptyState(
                    title = "No exercises found",
                    subtitle = "Try adjusting your search query or choosing another target body part."
                )
            } else {
                LazyColumn(
                    contentPadding = PaddingValues(start = 20.dp, end = 20.dp, top = 8.dp, bottom = 88.dp),
                    verticalArrangement = Arrangement.spacedBy(20.dp),
                    modifier = Modifier.fillMaxSize()
                ) {
                    items(uiState.exercises) { exercise ->
                        ExerciseListItem(
                            exercise = exercise,
                            onClick = { onNavigateToDetail(exercise.id) },
                            onAddClick = if (workoutViewModel != null) {
                                {
                                    val currentWorkout = workoutUiState?.currentWorkout
                                    if (currentWorkout != null) {
                                        workoutViewModel.addExerciseToCurrentWorkout(exercise.id, exercise.name) {
                                            Toast.makeText(context, "Added to ${currentWorkout.name}", Toast.LENGTH_SHORT).show()
                                        }
                                    } else {
                                        workoutViewModel.addExerciseToNewWorkout(exercise.id, exercise.name)
                                        Toast.makeText(context, "Added to new routine template", Toast.LENGTH_SHORT).show()
                                    }
                                    onAddSuccessful?.invoke()
                                }
                            } else null
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun ExerciseListItem(
    exercise: Exercise,
    onClick: () -> Unit,
    onAddClick: (() -> Unit)? = null
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
                    .height(180.dp)
            ) {
                AsyncImage(
                    model = exercise.gifUrl.ifBlank { "https://images.unsplash.com/photo-1534438327276-14e5300c3a48?q=80&w=600" },
                    contentDescription = exercise.name,
                    modifier = Modifier
                        .fillMaxSize()
                        .clip(RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)),
                    contentScale = ContentScale.Crop
                )

                // Difficulty badge (colored)
                val badgeColor = when {
                    exercise.equipment.contains("body", ignoreCase = true) -> ActivityGreen
                    exercise.equipment.contains("barbell", ignoreCase = true) -> ActivityRed
                    exercise.equipment.contains("dumbbell", ignoreCase = true) -> ActivityBlue
                    else -> ActivityYellow
                }
                Box(
                    modifier = Modifier
                        .padding(12.dp)
                        .background(badgeColor, shape = RoundedCornerShape(8.dp))
                        .padding(horizontal = 10.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = exercise.equipment.replaceFirstChar { it.uppercase() },
                        style = MaterialTheme.typography.labelSmall,
                        color = Color.White,
                        fontWeight = FontWeight.Bold
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

                if (onAddClick != null) {
                    FloatingActionButton(
                        onClick = onAddClick,
                        modifier = Modifier.size(40.dp),
                        containerColor = ActivityGreen,
                        contentColor = Color.White,
                        shape = CircleShape,
                        elevation = FloatingActionButtonDefaults.elevation(0.dp)
                    ) {
                        Icon(
                            Icons.Default.Add,
                            contentDescription = "Add to routine",
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }
        }
    }
}
