package com.example.elite_fitness_app.presentation.workout

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.SelfImprovement
import androidx.compose.material.icons.filled.DirectionsRun
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.ArrowDropUp
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.elite_fitness_app.domain.model.Workout
import com.example.elite_fitness_app.presentation.components.EliteChip
import com.example.elite_fitness_app.presentation.components.EliteInputField
import com.example.elite_fitness_app.presentation.components.EmptyState
import com.example.elite_fitness_app.presentation.components.GlassCard
import com.example.elite_fitness_app.presentation.components.PrimaryButton
import com.example.elite_fitness_app.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyWorkoutScreen(
    viewModel: WorkoutViewModel,
    onNavigateToWorkoutDetail: (String) -> Unit,
    onStartWorkoutSession: (String) -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    var showCreateDialog by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            Surface(color = Surface) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .statusBarsPadding()
                        .padding(horizontal = 20.dp, vertical = 12.dp)
                ) {
                    Text(
                        text = "My Workouts",
                        style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold),
                        color = OnSurface
                    )
                    Text(
                        text = "Manage your personalized training routines and track your progress.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = OnSurfaceVariant,
                        modifier = Modifier.padding(top = 4.dp)
                    )
                }
            }
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showCreateDialog = true },
                containerColor = Primary,
                contentColor = OnPrimary,
                shape = CircleShape
            ) {
                Icon(Icons.Default.Add, contentDescription = "Create Routine")
            }
        },
        containerColor = Surface
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            Column {
                // Filter Chips
                LazyRow(
                    contentPadding = PaddingValues(horizontal = 20.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    val filters = listOf("All Routines", "Strength", "Cardio", "Flexibility")
                    items(filters) { filter ->
                        EliteChip(
                            label = filter,
                            selected = filter == "All Routines",
                            onClick = { }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                if (uiState.isLoading && uiState.workouts.isEmpty()) {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator(color = Primary)
                    }
                } else if (uiState.workouts.isEmpty()) {
                    EmptyState(
                        icon = Icons.Default.FitnessCenter,
                        title = "No routines created yet",
                        subtitle = "Tap the '+' button to build your first customized workout routine."
                    )
                } else {
                    LazyColumn(
                        contentPadding = PaddingValues(start = 20.dp, end = 20.dp, top = 8.dp, bottom = 88.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp),
                        modifier = Modifier.fillMaxSize()
                    ) {
                        items(uiState.workouts) { workout ->
                            RoutineCard(
                                workout = workout,
                                onClick = { onNavigateToWorkoutDetail(workout.id) },
                                onStart = { onStartWorkoutSession(workout.id) },
                                onDelete = { viewModel.deleteWorkout(workout.id) {} }
                            )
                        }

                        // Import Workout card
                        item {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(120.dp)
                                    .clip(RoundedCornerShape(28.dp))
                                    .border(2.dp, OutlineVariant.copy(alpha = 0.4f), RoundedCornerShape(28.dp))
                                    .clickable { showCreateDialog = true },
                                contentAlignment = Alignment.Center
                            ) {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Icon(
                                        Icons.Outlined.Add,
                                        contentDescription = null,
                                        tint = OnSurfaceVariant,
                                        modifier = Modifier.size(32.dp)
                                    )
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Text(
                                        text = "Import Workout",
                                        style = MaterialTheme.typography.labelLarge,
                                        color = OnSurfaceVariant
                                    )
                                }
                            }
                        }
                    }
                }
            }

            if (showCreateDialog) {
                CreateRoutineDialog(
                    viewModel = viewModel,
                    onDismiss = { showCreateDialog = false },
                    onCreated = { showCreateDialog = false }
                )
            }
        }
    }
}

@Composable
fun RoutineCard(
    workout: Workout,
    onClick: () -> Unit,
    onStart: () -> Unit,
    onDelete: () -> Unit
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
fun CreateRoutineDialog(
    viewModel: WorkoutViewModel,
    onDismiss: () -> Unit,
    onCreated: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = "New Routine",
                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                color = OnSurface
            )
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                EliteInputField(
                    value = uiState.newWorkoutName,
                    onValueChange = viewModel::updateName,
                    label = "Routine Name *"
                )
                EliteInputField(
                    value = uiState.newWorkoutDesc,
                    onValueChange = viewModel::updateDesc,
                    label = "Description"
                )
                Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                    var expanded by remember { mutableStateOf(false) }
                    val difficultyOptions = listOf("Beginner", "Intermediate", "Advanced")
                    Box(modifier = Modifier.weight(1f)) {
                        Column {
                            Text(
                                text = "Difficulty",
                                style = MaterialTheme.typography.labelMedium,
                                color = OnSurfaceVariant,
                                modifier = Modifier.padding(bottom = 6.dp)
                            )
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(56.dp)
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(SurfaceContainerLow)
                                    .border(1.dp, OutlineVariant, RoundedCornerShape(12.dp))
                                    .clickable { expanded = true }
                                    .padding(horizontal = 16.dp),
                                contentAlignment = Alignment.CenterStart
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = uiState.newWorkoutDifficulty,
                                        style = MaterialTheme.typography.bodyLarge,
                                        color = OnSurface
                                    )
                                    Icon(
                                        imageVector = if (expanded) Icons.Default.ArrowDropUp else Icons.Default.ArrowDropDown,
                                        contentDescription = null,
                                        tint = OnSurfaceVariant
                                    )
                                }
                            }
                            DropdownMenu(
                                expanded = expanded,
                                onDismissRequest = { expanded = false },
                                modifier = Modifier.background(SurfaceContainerLowest)
                            ) {
                                difficultyOptions.forEach { option ->
                                    DropdownMenuItem(
                                        text = { Text(text = option) },
                                        onClick = {
                                            viewModel.updateDifficulty(option)
                                            expanded = false
                                        }
                                    )
                                }
                            }
                        }
                    }
                    EliteInputField(
                        value = uiState.newWorkoutDuration.toString(),
                        onValueChange = { viewModel.updateDuration(it.toIntOrNull() ?: 45) },
                        label = "Est. Duration",
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        },
        confirmButton = {
            PrimaryButton(
                text = "Create",
                onClick = {
                    viewModel.createWorkout {
                        onCreated()
                    }
                },
                modifier = Modifier.fillMaxWidth()
            )
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(text = "Cancel", color = OnSurfaceVariant)
            }
        },
        containerColor = SurfaceContainerLowest,
        shape = RoundedCornerShape(28.dp)
    )
}
