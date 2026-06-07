package com.example.elite_fitness_app.presentation.health

import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Sync
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material.icons.filled.Whatshot
import androidx.compose.material.icons.filled.DirectionsRun
import androidx.compose.material.icons.outlined.DirectionsWalk
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.health.connect.client.PermissionController
import com.example.elite_fitness_app.core.utils.HealthData
import com.example.elite_fitness_app.presentation.components.GlassCard
import com.example.elite_fitness_app.presentation.components.ProgressRing
import com.example.elite_fitness_app.ui.theme.*
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HealthScreen(
    viewModel: HealthViewModel
) {
    val uiState by viewModel.uiState.collectAsState()
    val scrollState = rememberScrollState()
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    val requestPermissionActivityContract = PermissionController.createRequestPermissionResultContract()
    val requestPermissionsLauncher = rememberLauncherForActivityResult(requestPermissionActivityContract) { granted ->
        viewModel.syncHealthData(context)
    }

    // Auto-sync health data on load if permissions are granted
    LaunchedEffect(Unit) {
        viewModel.loadUserProfile()
        val hasPermissions = viewModel.hasHealthPermissions(context)
        if (hasPermissions) {
            viewModel.syncHealthData(context)
        } else if (viewModel.isHealthConnectAvailable(context)) {
            requestPermissionsLauncher.launch(com.example.elite_fitness_app.core.utils.HealthConnectManager.PERMISSIONS)
        }
    }

    // Sync rotation animation
    val infiniteTransition = rememberInfiniteTransition(label = "sync_rotation")
    val syncRotation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "sync_rotation"
    )

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
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Health Dashboard",
                            style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold),
                            color = OnSurface
                        )
                        Text(
                            text = "Biometric telemetry and Google Health Connect metrics.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = OnSurfaceVariant,
                            modifier = Modifier.padding(top = 4.dp)
                        )
                    }
                    IconButton(
                        onClick = {
                            coroutineScope.launch {
                                val hasPermissions = viewModel.hasHealthPermissions(context)
                                if (hasPermissions) {
                                    viewModel.syncHealthData(context)
                                    Toast.makeText(context, "Data Synced", Toast.LENGTH_SHORT).show()
                                } else {
                                    requestPermissionsLauncher.launch(com.example.elite_fitness_app.core.utils.HealthConnectManager.PERMISSIONS)
                                }
                            }
                        },
                        enabled = !uiState.isSyncing
                    ) {
                        Icon(
                            Icons.Default.Sync,
                            contentDescription = "Sync Health Connect",
                            tint = if (uiState.isSyncing) OnSurfaceVariant else Primary,
                            modifier = if (uiState.isSyncing) {
                                Modifier.size(24.dp).rotate(syncRotation)
                            } else {
                                Modifier.size(24.dp)
                            }
                        )
                    }
                }
            }
        },
        containerColor = Surface
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(scrollState)
                .padding(bottom = 88.dp)
        ) {
            // Section 1: Biometrics (BMI, Height, Weight)
            BiometricsSection(uiState = uiState)

            Spacer(modifier = Modifier.height(24.dp))

            // Section 2: Today's detailed metrics
            TodayMetricsSection(uiState = uiState)

            Spacer(modifier = Modifier.height(24.dp))

            // Section 3: Weekly Trends
            WeeklyTrendsSection(uiState = uiState)
        }
    }
}

@Composable
fun BiometricsSection(uiState: HealthUiState) {
    val user = uiState.user
    val bmi = uiState.bmi
    val bmiCategory = uiState.bmiCategory

    val bmiColor = when (bmiCategory) {
        "Normal weight" -> ActivityGreen
        "Underweight" -> ActivityBlue
        "Overweight" -> ActivityYellow
        "Obese" -> ActivityRed
        else -> OnSurfaceVariant
    }

    Column(modifier = Modifier.padding(horizontal = 20.dp)) {
        Text(
            text = "Your Biometrics",
            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
            color = OnSurface,
            modifier = Modifier.padding(bottom = 12.dp)
        )

        GlassCard {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Height & Weight Group
                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(text = "📏", fontSize = 18.sp)
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Height: ",
                            style = MaterialTheme.typography.bodyMedium,
                            color = OnSurfaceVariant
                        )
                        Text(
                            text = if (user?.height != null) "${user.height} cm" else "--",
                            style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold),
                            color = OnSurface
                        )
                    }
                    Spacer(modifier = Modifier.height(10.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(text = "⚖️", fontSize = 18.sp)
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Weight: ",
                            style = MaterialTheme.typography.bodyMedium,
                            color = OnSurfaceVariant
                        )
                        Text(
                            text = if (user?.weight != null) "${user.weight} kg" else "--",
                            style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold),
                            color = OnSurface
                        )
                    }
                    Spacer(modifier = Modifier.height(10.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(text = "👤", fontSize = 18.sp)
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Age: ",
                            style = MaterialTheme.typography.bodyMedium,
                            color = OnSurfaceVariant
                        )
                        Text(
                            text = if (user?.age != null) "${user.age} yrs" else "--",
                            style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold),
                            color = OnSurface
                        )
                    }
                }

                // Calculated BMI Ring Display
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Box(contentAlignment = Alignment.Center) {
                        ProgressRing(
                            progress = (bmi / 40.0).toFloat().coerceIn(0f, 1f),
                            size = 84.dp,
                            strokeWidth = 8.dp,
                            color = bmiColor,
                            trackColor = SurfaceContainerHigh,
                            label = ""
                        )
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = if (bmi > 0) "%.1f".format(bmi) else "--",
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                color = OnSurface
                            )
                            Text(
                                text = "BMI",
                                style = MaterialTheme.typography.labelSmall,
                                color = OnSurfaceVariant
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = bmiCategory,
                        style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                        color = bmiColor
                    )
                }
            }
        }
    }
}

@Composable
fun TodayMetricsSection(uiState: HealthUiState) {
    val data = uiState.todayData

    Column(modifier = Modifier.padding(horizontal = 20.dp)) {
        Text(
            text = "Today's Activity Data",
            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
            color = OnSurface,
            modifier = Modifier.padding(bottom = 12.dp)
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Steps today
            Card(
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = SurfaceContainerLowest),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Icon(Icons.Outlined.DirectionsWalk, contentDescription = null, tint = ActivityGreen)
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(text = "Steps", style = MaterialTheme.typography.bodyMedium, color = OnSurfaceVariant)
                    Text(
                        text = "%,d".format(data.steps),
                        style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                        color = OnSurface
                    )
                }
            }

            // Calories today
            Card(
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = SurfaceContainerLowest),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Icon(Icons.Default.Whatshot, contentDescription = null, tint = ActivityRed)
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(text = "Calories", style = MaterialTheme.typography.bodyMedium, color = OnSurfaceVariant)
                    Text(
                        text = "%.0f kcal".format(data.caloriesBurned),
                        style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                        color = OnSurface
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Distance today
            Card(
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = SurfaceContainerLowest),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Icon(Icons.Default.DirectionsRun, contentDescription = null, tint = ActivityYellow)
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(text = "Distance", style = MaterialTheme.typography.bodyMedium, color = OnSurfaceVariant)
                    Text(
                        text = "%.2f km".format(data.distanceKm),
                        style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                        color = OnSurface
                    )
                }
            }

            // Active Minutes today
            Card(
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = SurfaceContainerLowest),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Icon(Icons.Default.Timer, contentDescription = null, tint = ActivityBlue)
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(text = "Active Minutes", style = MaterialTheme.typography.bodyMedium, color = OnSurfaceVariant)
                    Text(
                        text = "${data.activeMinutes} min",
                        style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                        color = OnSurface
                    )
                }
            }
        }
    }
}

@Composable
fun WeeklyTrendsSection(uiState: HealthUiState) {
    val weekly = uiState.weeklyData
    if (weekly.isEmpty()) return

    val totalSteps = weekly.sumOf { it.steps }
    val avgSteps = totalSteps / weekly.size
    val totalCalories = weekly.sumOf { it.caloriesBurned }
    val avgCalories = totalCalories / weekly.size

    Column(modifier = Modifier.padding(horizontal = 20.dp)) {
        Text(
            text = "Weekly Trends",
            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
            color = OnSurface,
            modifier = Modifier.padding(bottom = 12.dp)
        )

        GlassCard {
            Column {
                Text(
                    text = "Weekly Steps Progression",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = OnSurface
                )
                Spacer(modifier = Modifier.height(12.dp))

                // Steps Chart
                val maxSteps = weekly.maxOfOrNull { it.steps }?.coerceAtLeast(1L) ?: 10000L
                WeeklyMiniChart(
                    weeklyData = weekly,
                    metricSelector = { it.steps },
                    maxVal = maxSteps,
                    barColor = ActivityGreen
                )

                Spacer(modifier = Modifier.height(16.dp))
                HorizontalDivider(color = OutlineVariant.copy(alpha = 0.3f))
                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Text(
                            text = "Weekly Steps Total",
                            style = MaterialTheme.typography.labelSmall,
                            color = OnSurfaceVariant
                        )
                        Text(
                            text = "%,d".format(totalSteps),
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                            color = Primary
                        )
                    }
                    Column(horizontalAlignment = Alignment.End) {
                        Text(
                            text = "Daily Average Steps",
                            style = MaterialTheme.typography.labelSmall,
                            color = OnSurfaceVariant
                        )
                        Text(
                            text = "%,d".format(avgSteps),
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                            color = Primary
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Calories Trend Card
        GlassCard {
            Column {
                Text(
                    text = "Weekly Calories Burned",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = OnSurface
                )
                Spacer(modifier = Modifier.height(12.dp))

                // Calories Chart
                val maxCals = weekly.maxOfOrNull { it.caloriesBurned }?.toLong()?.coerceAtLeast(1L) ?: 600L
                WeeklyMiniChart(
                    weeklyData = weekly,
                    metricSelector = { it.caloriesBurned.toLong() },
                    maxVal = maxCals,
                    barColor = ActivityRed
                )

                Spacer(modifier = Modifier.height(16.dp))
                HorizontalDivider(color = OutlineVariant.copy(alpha = 0.3f))
                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Text(
                            text = "Weekly Calories Total",
                            style = MaterialTheme.typography.labelSmall,
                            color = OnSurfaceVariant
                        )
                        Text(
                            text = "%.0f kcal".format(totalCalories),
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                            color = Primary
                        )
                    }
                    Column(horizontalAlignment = Alignment.End) {
                        Text(
                            text = "Daily Average Calories",
                            style = MaterialTheme.typography.labelSmall,
                            color = OnSurfaceVariant
                        )
                        Text(
                            text = "%.0f kcal".format(avgCalories),
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                            color = Primary
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun WeeklyMiniChart(
    weeklyData: List<HealthData>,
    metricSelector: (HealthData) -> Long,
    maxVal: Long,
    barColor: Color
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(120.dp)
            .padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.Bottom
    ) {
        val days = listOf("Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun")
        weeklyData.take(7).forEachIndexed { index, data ->
            val value = metricSelector(data)
            val percentage = if (maxVal > 0) (value.toFloat() / maxVal.toFloat()).coerceIn(0f, 1f) else 0f
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Bottom,
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = if (value >= 1000) "%.1fk".format(value / 1000f) else "$value",
                    style = MaterialTheme.typography.labelSmall,
                    color = OnSurfaceVariant,
                    fontSize = 9.sp
                )
                Spacer(modifier = Modifier.height(4.dp))
                Box(
                    modifier = Modifier
                        .fillMaxHeight(0.8f * percentage + 0.1f) // Min height ensure
                        .width(16.dp)
                        .background(barColor, shape = RoundedCornerShape(topStart = 4.dp, topEnd = 4.dp))
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = days.getOrElse(index) { "Day" },
                    style = MaterialTheme.typography.labelSmall,
                    color = OnSurfaceVariant,
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}
