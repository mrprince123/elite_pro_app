package com.example.elite_fitness_app.presentation.home

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.outlined.DirectionsWalk
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.elite_fitness_app.domain.model.TrainingPlan
import com.example.elite_fitness_app.domain.model.Workout
import com.example.elite_fitness_app.presentation.components.GlassCard
import com.example.elite_fitness_app.presentation.components.ProgressRing
import com.example.elite_fitness_app.presentation.components.EliteChip
import com.example.elite_fitness_app.presentation.components.ShimmerBox
import com.example.elite_fitness_app.ui.theme.*

@Composable
fun HomeScreen(
    viewModel: HomeViewModel,
    onNavigateToWorkoutDetail: (String) -> Unit,
    onNavigateToTrainingPlanDetail: (String) -> Unit,
    onStartWorkoutSession: (String) -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    val scrollState = rememberScrollState()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Surface)
            .statusBarsPadding()
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
                .padding(bottom = 80.dp)
        ) {
            // Header Bar with avatar and notification
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Profile avatar placeholder
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(SurfaceContainerHigh),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = (uiState.user?.name?.firstOrNull()?.uppercase() ?: "V"),
                        style = MaterialTheme.typography.titleMedium,
                        color = Primary,
                        fontWeight = FontWeight.Bold
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))

                Text(
                    text = "Elite Fitness Pro",
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.Bold,
                    ),
                    color = Primary,
                    modifier = Modifier.weight(1f)
                )

                IconButton(onClick = { /* TODO */ }) {
                    Icon(
                        Icons.Default.Notifications,
                        contentDescription = "Notifications",
                        tint = OnSurface
                    )
                }
            }

            // Dashboard Stats — Central Ring + Metrics
            DashboardStats(uiState = uiState)

            Spacer(modifier = Modifier.height(20.dp))

            // Category Chips
            LazyRow(
                contentPadding = PaddingValues(horizontal = 20.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                val categories = listOf("For You", "Chest", "Back", "Legs", "Cardio", "Arms")
                items(categories) { category ->
                    EliteChip(
                        label = category,
                        selected = category == "For You",
                        onClick = { /* TODO */ }
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Daily Focus Section
            SectionHeader(title = "Daily Focus")

            if (uiState.isLoading) {
                ShimmerBox(
                    modifier = Modifier
                        .padding(horizontal = 20.dp)
                        .fillMaxWidth()
                        .height(200.dp)
                )
            } else if (uiState.trainingPlans.isNotEmpty()) {
                val featuredPlan = uiState.trainingPlans.first()
                DailyFocusCard(
                    plan = featuredPlan,
                    onClick = { onNavigateToTrainingPlanDetail(featuredPlan.id) }
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Training Plans Section
            SectionHeader(title = "Training Plans", showSeeAll = true)
            if (uiState.isLoading) {
                LazyRow(
                    contentPadding = PaddingValues(horizontal = 20.dp),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    items(3) {
                        ShimmerBox(modifier = Modifier.size(width = 200.dp, height = 130.dp))
                    }
                }
            } else {
                LazyRow(
                    contentPadding = PaddingValues(horizontal = 20.dp),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    items(uiState.trainingPlans) { plan ->
                        TrainingPlanCard(
                            plan = plan,
                            onClick = { onNavigateToTrainingPlanDetail(plan.id) }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Recent Activity / Workouts Section
            SectionHeader(title = "Recent Activity")
            if (uiState.isLoading) {
                Column(
                    modifier = Modifier.padding(horizontal = 20.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    repeat(2) {
                        ShimmerBox(modifier = Modifier.fillMaxWidth().height(80.dp))
                    }
                }
            } else if (uiState.workouts.isEmpty()) {
                GlassCard(
                    modifier = Modifier.padding(horizontal = 20.dp)
                ) {
                    Text(
                        text = "No recent activity.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = OnSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Start a workout to see your activity here.",
                        style = MaterialTheme.typography.bodySmall,
                        color = OnSurfaceVariant.copy(alpha = 0.6f)
                    )
                }
            } else {
                Column(
                    modifier = Modifier.padding(horizontal = 20.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    uiState.workouts.take(4).forEach { workout ->
                        WorkoutRoutineRow(
                            workout = workout,
                            onClick = { onNavigateToWorkoutDetail(workout.id) },
                            onStartClick = { onStartWorkoutSession(workout.id) }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
        }

        // FAB
        FloatingActionButton(
            onClick = { /* TODO */ },
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(end = 20.dp, bottom = 88.dp),
            containerColor = Primary,
            contentColor = OnPrimary,
            shape = CircleShape
        ) {
            Icon(Icons.Default.Add, contentDescription = "New")
        }
    }
}

@Composable
fun SectionHeader(
    title: String,
    showSeeAll: Boolean = false
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleLarge.copy(
                fontWeight = FontWeight.Bold,
            ),
            color = OnSurface,
        )
        if (showSeeAll) {
            Text(
                text = "See all",
                style = MaterialTheme.typography.labelLarge,
                color = Primary,
            )
        }
    }
}

@Composable
fun DashboardStats(uiState: HomeUiState) {
    val calories = (uiState.stats["caloriesBurned"] as? Double)?.toInt() ?: 0
    val duration = (uiState.stats["totalDuration"] as? Double)?.toInt() ?: 0
    val sessions = (uiState.stats["completedSessions"] as? Double)?.toInt() ?: 0

    val targetSteps = 10000
    val steps = (calories * 3).coerceAtMost(targetSteps) // Approximate steps from calories
    val progress = (steps.toFloat() / targetSteps.toFloat()).coerceIn(0f, 1f)

    Column(
        modifier = Modifier.padding(horizontal = 20.dp)
    ) {
        // Main metric card with ring
        GlassCard {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.fillMaxWidth()
            ) {
                ProgressRing(
                    progress = progress,
                    size = 140.dp,
                    strokeWidth = 12.dp,
                    color = ActivityGreen,
                    trackColor = SurfaceContainerHigh,
                    label = ""
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Overlay text for ring center
                Text(
                    text = "%,d".format(steps),
                    style = MaterialTheme.typography.displaySmall.copy(
                        fontWeight = FontWeight.Bold,
                    ),
                    color = OnSurface,
                )
                Text(
                    text = "Steps Today",
                    style = MaterialTheme.typography.bodyMedium,
                    color = OnSurfaceVariant,
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Mini metrics row
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Move Minutes
            Row(
                modifier = Modifier.weight(1f),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    Icons.Default.Timer,
                    contentDescription = null,
                    tint = ActivityBlue,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "$duration",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                    ),
                    color = OnSurface,
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = "MOVE MIN",
                    style = MaterialTheme.typography.labelSmall,
                    color = OnSurfaceVariant,
                )
            }

            // Heart Points
            Row(
                modifier = Modifier.weight(1f),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    Icons.Default.Favorite,
                    contentDescription = null,
                    tint = ActivityGreen,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "$sessions",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                    ),
                    color = OnSurface,
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = "HEART PTS",
                    style = MaterialTheme.typography.labelSmall,
                    color = OnSurfaceVariant,
                )
            }
        }
    }
}

@Composable
fun DailyFocusCard(
    plan: TrainingPlan,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .padding(horizontal = 20.dp)
            .fillMaxWidth()
            .height(200.dp)
            .clickable { onClick() },
        shape = RoundedCornerShape(28.dp),
        colors = CardDefaults.cardColors(containerColor = SurfaceContainerHigh)
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            AsyncImage(
                model = plan.thumbnail.ifBlank { "https://images.unsplash.com/photo-1517838277536-f5f99be501cd?q=80&w=800" },
                contentDescription = plan.title,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )

            // Dark gradient for text legibility
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(Color.Transparent, Color.Black.copy(alpha = 0.7f))
                        )
                    )
            )

            Column(
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .padding(20.dp)
            ) {
                // Category badge
                Box(
                    modifier = Modifier
                        .background(Secondary, shape = RoundedCornerShape(12.dp))
                        .padding(horizontal = 10.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = plan.level.uppercase(),
                        style = MaterialTheme.typography.labelSmall,
                        color = OnSecondary,
                        fontWeight = FontWeight.Bold
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = plan.title,
                    style = MaterialTheme.typography.headlineSmall.copy(
                        fontWeight = FontWeight.Bold,
                    ),
                    color = Color.White,
                    maxLines = 1
                )

                Text(
                    text = "${plan.duration} mins • ${plan.bodyPart} & ${plan.level}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.White.copy(alpha = 0.8f)
                )
            }
        }
    }
}

@Composable
fun TrainingPlanCard(
    plan: TrainingPlan,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .width(200.dp)
            .clickable { onClick() },
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = SurfaceContainerLowest),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column {
            // Plan icon with colored background
            Box(
                modifier = Modifier
                    .padding(16.dp)
                    .size(44.dp)
                    .clip(RoundedCornerShape(14.dp))
                    .background(PrimaryContainer.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    Icons.Default.FitnessCenter,
                    contentDescription = null,
                    tint = Primary,
                    modifier = Modifier.size(24.dp)
                )
            }

            Column(modifier = Modifier.padding(horizontal = 16.dp)) {
                Text(
                    text = plan.title,
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                    ),
                    color = OnSurface,
                    maxLines = 1
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "${plan.level} • ${plan.duration} mins",
                    style = MaterialTheme.typography.bodySmall,
                    color = OnSurfaceVariant
                )
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
fun WorkoutRoutineRow(
    workout: Workout,
    onClick: () -> Unit,
    onStartClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = SurfaceContainerLowest),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            // Activity icon
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(CircleShape)
                    .background(ActivityBlue.copy(alpha = 0.1f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    Icons.Outlined.DirectionsWalk,
                    contentDescription = null,
                    tint = ActivityBlue,
                    modifier = Modifier.size(24.dp)
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = workout.name,
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.SemiBold,
                    ),
                    color = OnSurface
                )
                Text(
                    text = "${workout.exercises.size} Exercises • ${workout.difficulty}",
                    style = MaterialTheme.typography.bodySmall,
                    color = OnSurfaceVariant
                )
            }

            // Calorie estimate
            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = "${workout.estimatedDuration * 5}",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                    ),
                    color = OnSurface
                )
                Text(
                    text = "kcal",
                    style = MaterialTheme.typography.labelSmall,
                    color = OnSurfaceVariant
                )
            }
        }
    }
}
