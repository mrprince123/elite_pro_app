package com.example.elite_fitness_app.presentation.session

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.NavigateBefore
import androidx.compose.material.icons.filled.NavigateNext
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.elite_fitness_app.presentation.components.EliteTopBar
import com.example.elite_fitness_app.presentation.components.GlassCard
import com.example.elite_fitness_app.presentation.components.PrimaryButton
import com.example.elite_fitness_app.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WorkoutSessionScreen(
    workoutId: String,
    viewModel: SessionViewModel,
    onNavigateBack: () -> Unit,
    onSessionFinished: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    var showEndDialog by remember { mutableStateOf(false) }
    var notesText by remember { mutableStateOf("") }

    LaunchedEffect(workoutId) {
        viewModel.startSession(workoutId)
    }

    val workout = uiState.workout
    val currentExercise = workout?.exercises?.getOrNull(uiState.activeExerciseIndex)

    // Formatted timer: MM:SS
    val minutes = uiState.elapsedSeconds / 60
    val seconds = uiState.elapsedSeconds % 60
    val formattedTime = String.format("%02d:%02d", minutes, seconds)

    Scaffold(
        topBar = {
            EliteTopBar(
                title = workout?.name ?: "Active Workout",
                onBackClick = { showEndDialog = true }
            )
        },
        containerColor = Surface
    ) { innerPadding ->
        if (uiState.isLoading && workout == null) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = Primary)
            }
        } else if (workout == null || currentExercise == null) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text(text = "Unable to start workout session.", color = OnSurfaceVariant)
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .padding(horizontal = 20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(modifier = Modifier.height(16.dp))

                // Timer Display Card
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(containerColor = SurfaceContainerLowest)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "Elapsed Time",
                            style = MaterialTheme.typography.labelMedium,
                            color = OnSurfaceVariant,
                        )
                        Text(
                            text = formattedTime,
                            style = MaterialTheme.typography.displayLarge.copy(
                                fontSize = 48.sp,
                                fontWeight = FontWeight.Bold,
                            ),
                            color = Primary
                        )
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Exercise Detail Progress Card
                GlassCard {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Exercise ${uiState.activeExerciseIndex + 1} of ${workout.exercises.size}",
                            style = MaterialTheme.typography.labelMedium,
                            color = Secondary,
                        )

                        Text(
                            text = "Sets completed: ${uiState.completedSetsCount}",
                            style = MaterialTheme.typography.labelMedium,
                            color = OnSurfaceVariant
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = currentExercise.exerciseName,
                        style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
                        color = OnSurface,
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Card(
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(containerColor = SurfaceContainerHigh)
                        ) {
                            Column(
                                modifier = Modifier.padding(12.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text(text = "Target Sets", style = MaterialTheme.typography.labelSmall, color = OnSurfaceVariant)
                                Text(text = currentExercise.sets.toString(), style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold), color = Primary)
                            }
                        }
                        Card(
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(containerColor = SurfaceContainerHigh)
                        ) {
                            Column(
                                modifier = Modifier.padding(12.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text(text = "Target Reps", style = MaterialTheme.typography.labelSmall, color = OnSurfaceVariant)
                                Text(text = currentExercise.reps.toString(), style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold), color = Primary)
                            }
                        }
                        Card(
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(containerColor = SurfaceContainerHigh)
                        ) {
                            Column(
                                modifier = Modifier.padding(12.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text(text = "Rest Time", style = MaterialTheme.typography.labelSmall, color = OnSurfaceVariant)
                                Text(text = "${currentExercise.restTime}s", style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold), color = Primary)
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Complete Set button
                Button(
                    onClick = { viewModel.completeSet() },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(72.dp),
                    shape = RoundedCornerShape(28.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Secondary,
                        contentColor = OnSecondary
                    )
                ) {
                    Icon(Icons.Default.Check, contentDescription = null, modifier = Modifier.size(24.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Mark Set as Completed",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    )
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Next/Previous Row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    OutlinedButton(
                        onClick = { viewModel.prevExercise() },
                        enabled = uiState.activeExerciseIndex > 0,
                        modifier = Modifier
                            .weight(1f)
                            .height(56.dp),
                        shape = RoundedCornerShape(28.dp),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = Primary)
                    ) {
                        Icon(Icons.Default.NavigateBefore, contentDescription = null)
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(text = "Previous")
                    }

                    Spacer(modifier = Modifier.width(16.dp))

                    OutlinedButton(
                        onClick = { viewModel.nextExercise() },
                        enabled = uiState.activeExerciseIndex < workout.exercises.lastIndex,
                        modifier = Modifier
                            .weight(1f)
                            .height(56.dp),
                        shape = RoundedCornerShape(28.dp),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = Primary)
                    ) {
                        Text(text = "Next")
                        Spacer(modifier = Modifier.width(4.dp))
                        Icon(Icons.Default.NavigateNext, contentDescription = null)
                    }
                }

                Spacer(modifier = Modifier.weight(1f))

                // Finish Workout button
                PrimaryButton(
                    text = "Finish Workout",
                    onClick = { showEndDialog = true },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 24.dp)
                )
            }
        }
    }

    // End session dialogue
    if (showEndDialog) {
        AlertDialog(
            onDismissRequest = { showEndDialog = false },
            title = {
                Text(
                    text = "End Workout Session",
                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                    color = OnSurface
                )
            },
            text = {
                Column {
                    Text(
                        text = "Are you finished with this training routine? Add quick progress notes below:",
                        style = MaterialTheme.typography.bodyMedium,
                        color = OnSurface
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    OutlinedTextField(
                        value = notesText,
                        onValueChange = { notesText = it },
                        modifier = Modifier.fillMaxWidth(),
                        placeholder = { Text(text = "Example: Felt strong on bench press today.", color = OnSurfaceVariant) },
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Primary,
                            unfocusedBorderColor = OutlineVariant,
                            cursorColor = Primary
                        )
                    )
                }
            },
            confirmButton = {
                PrimaryButton(
                    text = "End Workout",
                    onClick = {
                        viewModel.endSession(notesText) {
                            showEndDialog = false
                            onSessionFinished()
                        }
                    },
                    modifier = Modifier.fillMaxWidth()
                )
            },
            dismissButton = {
                TextButton(onClick = { showEndDialog = false }) {
                    Text(text = "Resume", color = OnSurfaceVariant)
                }
            },
            containerColor = SurfaceContainerLowest,
            shape = RoundedCornerShape(28.dp)
        )
    }
}
