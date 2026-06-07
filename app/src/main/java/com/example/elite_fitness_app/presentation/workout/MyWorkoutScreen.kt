package com.example.elite_fitness_app.presentation.workout

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.ui.platform.LocalContext
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.ui.graphics.Brush
import coil.compose.AsyncImage
import com.example.elite_fitness_app.domain.model.Workout
import com.example.elite_fitness_app.domain.model.TrainingPlan
import com.example.elite_fitness_app.presentation.training.TrainingViewModel
import com.example.elite_fitness_app.presentation.components.EliteChip
import com.example.elite_fitness_app.presentation.components.EliteInputField
import com.example.elite_fitness_app.presentation.components.EmptyState
import com.example.elite_fitness_app.presentation.components.GlassCard
import com.example.elite_fitness_app.presentation.components.PrimaryButton
import com.example.elite_fitness_app.presentation.components.ShimmerBox
import com.example.elite_fitness_app.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyWorkoutScreen(
    viewModel: WorkoutViewModel,
    trainingViewModel: TrainingViewModel,
    onNavigateToWorkoutDetail: (String) -> Unit,
    onNavigateToTrainingPlanDetail: (String) -> Unit,
    onStartWorkoutSession: (String) -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    val trainingUiState by trainingViewModel.uiState.collectAsState()
    var activeTab by remember { mutableStateOf("My Routines") } // "My Routines" or "Prebuilt Plans"
    var showCreateDialog by remember { mutableStateOf(false) }
    var selectedFilter by remember { mutableStateOf("All Routines") }

    val filteredWorkouts = remember(uiState.workouts, selectedFilter) {
        if (selectedFilter == "All Routines") {
            uiState.workouts
        } else {
            uiState.workouts.filter { workout ->
                when (selectedFilter) {
                    "Strength" -> {
                        workout.name.contains("strength", ignoreCase = true) ||
                        workout.description.contains("strength", ignoreCase = true) ||
                        workout.name.contains("lift", ignoreCase = true) ||
                        workout.name.contains("weight", ignoreCase = true) ||
                        workout.name.contains("chest", ignoreCase = true) ||
                        workout.name.contains("back", ignoreCase = true) ||
                        workout.name.contains("leg", ignoreCase = true) ||
                        workout.name.contains("arm", ignoreCase = true) ||
                        workout.exercises.any { 
                            it.exerciseName.contains("press", ignoreCase = true) ||
                            it.exerciseName.contains("curl", ignoreCase = true) ||
                            it.exerciseName.contains("squat", ignoreCase = true) ||
                            it.exerciseName.contains("deadlift", ignoreCase = true)
                        } ||
                        (!workout.name.contains("cardio", ignoreCase = true) && 
                         !workout.name.contains("stretch", ignoreCase = true) &&
                         !workout.name.contains("yoga", ignoreCase = true))
                    }
                    "Cardio" -> {
                        workout.name.contains("cardio", ignoreCase = true) ||
                        workout.description.contains("cardio", ignoreCase = true) ||
                        workout.name.contains("run", ignoreCase = true) ||
                        workout.name.contains("walk", ignoreCase = true) ||
                        workout.name.contains("hiit", ignoreCase = true) ||
                        workout.name.contains("cycle", ignoreCase = true) ||
                        workout.exercises.any {
                            it.exerciseName.contains("run", ignoreCase = true) ||
                            it.exerciseName.contains("walk", ignoreCase = true) ||
                            it.exerciseName.contains("jump", ignoreCase = true) ||
                            it.exerciseName.contains("cardio", ignoreCase = true)
                        }
                    }
                    "Flexibility" -> {
                        workout.name.contains("flexibility", ignoreCase = true) ||
                        workout.description.contains("flexibility", ignoreCase = true) ||
                        workout.name.contains("stretch", ignoreCase = true) ||
                        workout.name.contains("yoga", ignoreCase = true) ||
                        workout.name.contains("mobility", ignoreCase = true) ||
                        workout.exercises.any {
                            it.exerciseName.contains("stretch", ignoreCase = true) ||
                            it.exerciseName.contains("yoga", ignoreCase = true) ||
                            it.exerciseName.contains("mobility", ignoreCase = true)
                        }
                    }
                    else -> false
                }
            }
        }
    }

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
                        text = "Workouts",
                        style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold),
                        color = OnSurface
                    )
                    Text(
                        text = if (activeTab == "My Routines") "Manage your routine templates and track your progress." else "Discover plans for every body part and fitness level.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = OnSurfaceVariant,
                        modifier = Modifier.padding(top = 4.dp)
                    )
                }
            }
        },
        floatingActionButton = {
            if (activeTab == "My Routines") {
                FloatingActionButton(
                    onClick = { showCreateDialog = true },
                    containerColor = Primary,
                    contentColor = OnPrimary,
                    shape = CircleShape
                ) {
                    Icon(Icons.Default.Add, contentDescription = "Create Routine")
                }
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
                // Segmented Tab Pill Switcher
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp, vertical = 8.dp)
                        .background(SurfaceContainerLow, shape = RoundedCornerShape(24.dp))
                        .padding(4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    TabPill(
                        text = "My Routines",
                        isSelected = activeTab == "My Routines",
                        onClick = { activeTab = "My Routines" },
                        modifier = Modifier.weight(1f)
                    )
                    TabPill(
                        text = "Prebuilt Plans",
                        isSelected = activeTab == "Prebuilt Plans",
                        onClick = { activeTab = "Prebuilt Plans" },
                        modifier = Modifier.weight(1f)
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                if (activeTab == "My Routines") {
                    // Filter Chips
                    LazyRow(
                        contentPadding = PaddingValues(horizontal = 20.dp, vertical = 8.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        val filters = listOf("All Routines", "Strength", "Cardio", "Flexibility")
                        items(filters) { filter ->
                            EliteChip(
                                label = filter,
                                selected = filter == selectedFilter,
                                onClick = { selectedFilter = filter }
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
                    } else if (filteredWorkouts.isEmpty()) {
                        EmptyState(
                            icon = Icons.Default.FitnessCenter,
                            title = "No routines found",
                            subtitle = "No routines matching the '$selectedFilter' category."
                        )
                    } else {
                        LazyVerticalGrid(
                            columns = GridCells.Fixed(2),
                            contentPadding = PaddingValues(start = 20.dp, end = 20.dp, top = 8.dp, bottom = 88.dp),
                            horizontalArrangement = Arrangement.spacedBy(16.dp),
                            verticalArrangement = Arrangement.spacedBy(16.dp),
                            modifier = Modifier.fillMaxSize()
                        ) {
                            items(filteredWorkouts) { workout ->
                                RoutineGridCard(
                                    workout = workout,
                                    onClick = { onNavigateToWorkoutDetail(workout.id) },
                                    onDelete = { viewModel.deleteWorkout(workout.id) {} }
                                )
                            }
                        }
                    }
                } else {
                    // Prebuilt Plans Content
                    LazyRow(
                        contentPadding = PaddingValues(horizontal = 20.dp, vertical = 8.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(trainingUiState.parts) { part ->
                            EliteChip(
                                label = part.replaceFirstChar { it.uppercase() },
                                selected = trainingUiState.selectedPart == part,
                                onClick = { trainingViewModel.selectPart(part) }
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    if (trainingUiState.isLoading && trainingUiState.trainingPlans.isEmpty()) {
                        LazyVerticalGrid(
                            columns = GridCells.Fixed(2),
                            contentPadding = PaddingValues(horizontal = 20.dp, vertical = 8.dp),
                            horizontalArrangement = Arrangement.spacedBy(16.dp),
                            verticalArrangement = Arrangement.spacedBy(16.dp),
                            modifier = Modifier.fillMaxSize()
                        ) {
                            items(6) {
                                ShimmerBox(modifier = Modifier.fillMaxWidth().height(200.dp))
                            }
                        }
                    } else if (trainingUiState.trainingPlans.isEmpty()) {
                        EmptyState(
                            title = "No training plans found",
                            subtitle = "We couldn't find training plans for this category. Check back later!"
                        )
                    } else {
                        LazyVerticalGrid(
                            columns = GridCells.Fixed(2),
                            contentPadding = PaddingValues(start = 20.dp, end = 20.dp, top = 8.dp, bottom = 88.dp),
                            horizontalArrangement = Arrangement.spacedBy(16.dp),
                            verticalArrangement = Arrangement.spacedBy(16.dp),
                            modifier = Modifier.fillMaxSize()
                        ) {
                            items(trainingUiState.trainingPlans) { plan ->
                                TrainingGridCard(
                                    plan = plan,
                                    onClick = { onNavigateToTrainingPlanDetail(plan.id) }
                                )
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
    val context = LocalContext.current

    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri: Uri? ->
        uri?.let { viewModel.updateNewWorkoutImage(it.toString()) }
    }

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
                // Cover Image Selector
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(100.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(SurfaceContainerLow)
                        .border(1.dp, OutlineVariant, RoundedCornerShape(12.dp))
                        .clickable {
                            photoPickerLauncher.launch(
                                PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                            )
                        },
                    contentAlignment = Alignment.Center
                ) {
                    if (!uiState.newWorkoutImageUri.isNullOrBlank()) {
                        AsyncImage(
                            model = uiState.newWorkoutImageUri,
                            contentDescription = "Cover Image Preview",
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop
                        )
                    } else {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(text = "📸", style = MaterialTheme.typography.titleMedium)
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "Select Cover Image",
                                style = MaterialTheme.typography.labelSmall,
                                color = OnSurfaceVariant
                            )
                        }
                    }
                }

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
                    viewModel.createWorkout(context) {
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

@Composable
fun TabPill(
    text: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(20.dp))
            .background(if (isSelected) Primary else Color.Transparent)
            .clickable { onClick() }
            .padding(vertical = 10.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
            color = if (isSelected) OnPrimary else OnSurfaceVariant
        )
    }
}

@Composable
fun TrainingGridCard(
    plan: TrainingPlan,
    onClick: () -> Unit
) {
    val badgeColor = when (plan.level.lowercase()) {
        "beginner" -> ActivityGreen
        "advanced" -> ActivityRed
        else -> ActivityBlue
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = SurfaceContainerLowest),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp)
            ) {
                AsyncImage(
                    model = plan.thumbnail.ifBlank { "https://images.unsplash.com/photo-1517838277536-f5f99be501cd?q=80&w=400" },
                    contentDescription = plan.title,
                    modifier = Modifier
                        .fillMaxSize()
                        .clip(RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp)),
                    contentScale = ContentScale.Crop
                )

                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .clip(RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp))
                        .background(
                            Brush.verticalGradient(
                                colors = listOf(Color.Transparent, Color.Black.copy(alpha = 0.5f))
                            )
                        )
                )

                Box(
                    modifier = Modifier
                        .padding(8.dp)
                        .background(badgeColor, shape = RoundedCornerShape(8.dp))
                        .padding(horizontal = 8.dp, vertical = 3.dp)
                ) {
                    Text(
                        text = plan.level.replaceFirstChar { it.uppercase() },
                        style = MaterialTheme.typography.labelSmall,
                        color = Color.White,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Column(
                modifier = Modifier.padding(12.dp)
            ) {
                Text(
                    text = plan.bodyPart.replaceFirstChar { it.uppercase() },
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = OnSurface,
                    maxLines = 1
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = plan.title,
                    style = MaterialTheme.typography.bodySmall,
                    color = OnSurfaceVariant,
                    maxLines = 1
                )

                Spacer(modifier = Modifier.height(8.dp))

                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text(
                        text = "🔥 ${plan.caloriesEstimate} kcal",
                        style = MaterialTheme.typography.labelSmall,
                        color = OnSurfaceVariant
                    )
                    Text(
                        text = "⏱️ ${plan.duration} min",
                        style = MaterialTheme.typography.labelSmall,
                        color = OnSurfaceVariant
                    )
                }
            }
        }
    }
}

fun getWorkoutImage(workoutName: String): String {
    val name = workoutName.lowercase()
    return when {
        name.contains("chest") || name.contains("push") || name.contains("press") -> 
            "https://images.unsplash.com/photo-1571019614242-c5c5dee9f50b?q=80&w=400"
        name.contains("back") || name.contains("pull") || name.contains("row") -> 
            "https://images.unsplash.com/photo-1605296867304-46d5465a25f1?q=80&w=400"
        name.contains("leg") || name.contains("squat") || name.contains("thigh") -> 
            "https://images.unsplash.com/photo-1574680096145-d05b474e2155?q=80&w=400"
        name.contains("arm") || name.contains("bicep") || name.contains("tricep") || name.contains("curl") -> 
            "https://images.unsplash.com/photo-1581009146145-b5ef050c2e1e?q=80&w=400"
        name.contains("cardio") || name.contains("run") || name.contains("hiit") || name.contains("walk") || name.contains("cycle") -> 
            "https://images.unsplash.com/photo-1476480862126-209bfaa8edc8?q=80&w=400"
        name.contains("flex") || name.contains("stretch") || name.contains("yoga") || name.contains("mobility") -> 
            "https://images.unsplash.com/photo-1544367567-0f2fcb009e0b?q=80&w=400"
        else -> 
            "https://images.unsplash.com/photo-1517838277536-f5f99be501cd?q=80&w=400"
    }
}

@Composable
fun RoutineGridCard(
    workout: Workout,
    onClick: () -> Unit,
    onDelete: () -> Unit
) {
    val badgeColor = when (workout.difficulty.lowercase()) {
        "beginner" -> ActivityGreen
        "advanced" -> ActivityRed
        else -> ActivityBlue
    }

    val bodyPart = when {
        workout.name.contains("chest", ignoreCase = true) -> "Chest"
        workout.name.contains("back", ignoreCase = true) -> "Back"
        workout.name.contains("leg", ignoreCase = true) -> "Legs"
        workout.name.contains("arm", ignoreCase = true) -> "Arms"
        workout.name.contains("cardio", ignoreCase = true) -> "Cardio"
        workout.name.contains("yoga", ignoreCase = true) || workout.name.contains("stretch", ignoreCase = true) -> "Flexibility"
        else -> "Full Body"
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = SurfaceContainerLowest),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column {
            // Image section
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp)
            ) {
                AsyncImage(
                    model = workout.image?.ifBlank { null } ?: getWorkoutImage(workout.name),
                    contentDescription = workout.name,
                    modifier = Modifier
                        .fillMaxSize()
                        .clip(RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp)),
                    contentScale = ContentScale.Crop
                )

                // Gradient overlay
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .clip(RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp))
                        .background(
                            Brush.verticalGradient(
                                colors = listOf(Color.Transparent, Color.Black.copy(alpha = 0.5f))
                            )
                        )
                )

                // Difficulty badge
                Box(
                    modifier = Modifier
                        .align(Alignment.BottomStart)
                        .padding(8.dp)
                        .background(badgeColor, shape = RoundedCornerShape(8.dp))
                        .padding(horizontal = 8.dp, vertical = 3.dp)
                ) {
                    Text(
                        text = workout.difficulty.replaceFirstChar { it.uppercase() },
                        style = MaterialTheme.typography.labelSmall,
                        color = Color.White,
                        fontWeight = FontWeight.Bold
                    )
                }

                // Delete button overlay
                IconButton(
                    onClick = onDelete,
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(8.dp)
                        .size(28.dp)
                        .background(Color.Black.copy(alpha = 0.5f), CircleShape)
                ) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Delete",
                        tint = Color.White,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }

            // Info section
            Column(
                modifier = Modifier.padding(12.dp)
            ) {
                Text(
                    text = bodyPart,
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = OnSurface,
                    maxLines = 1
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = workout.name,
                    style = MaterialTheme.typography.bodySmall,
                    color = OnSurfaceVariant,
                    maxLines = 1
                )

                Spacer(modifier = Modifier.height(8.dp))

                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text(
                        text = "🔥 ${workout.estimatedDuration * 5} kcal",
                        style = MaterialTheme.typography.labelSmall,
                        color = OnSurfaceVariant
                    )
                    Text(
                        text = "⏱️ ${workout.estimatedDuration} min",
                        style = MaterialTheme.typography.labelSmall,
                        color = OnSurfaceVariant
                    )
                }
            }
        }
    }
}
