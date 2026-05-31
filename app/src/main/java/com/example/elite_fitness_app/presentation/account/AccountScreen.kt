package com.example.elite_fitness_app.presentation.account

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.Height
import androidx.compose.material.icons.filled.MonitorWeight
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.elite_fitness_app.presentation.components.EliteInputField
import com.example.elite_fitness_app.presentation.components.GlassCard
import com.example.elite_fitness_app.presentation.components.PrimaryButton
import com.example.elite_fitness_app.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AccountScreen(
    viewModel: AccountViewModel,
    onLogoutSuccess: () -> Unit,
    onNavigateToSettings: () -> Unit,
    onNavigateToFavorites: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    val scrollState = rememberScrollState()
    var showDeleteDialog by remember { mutableStateOf(false) }
    val context = LocalContext.current

    // Photo picker launcher
    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri: Uri? ->
        uri?.let { viewModel.onProfileImageSelected(it, context) }
    }

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
                        text = "Profile",
                        style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold),
                        color = OnSurface,
                        modifier = Modifier.weight(1f)
                    )
                    IconButton(onClick = onNavigateToSettings) {
                        Icon(
                            imageVector = Icons.Default.Settings,
                            contentDescription = "Settings",
                            tint = OnSurface
                        )
                    }
                }
            }
        },
        containerColor = Surface
    ) { innerPadding ->
        if (uiState.isLoading && uiState.user == null) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = Primary)
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .verticalScroll(scrollState)
                    .padding(horizontal = 20.dp, vertical = 8.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Profile Header Card
                GlassCard {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        // Profile avatar with camera overlay
                        Box(
                            modifier = Modifier.size(72.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            val profileImage = uiState.profileImageUri
                            if (!profileImage.isNullOrBlank()) {
                                AsyncImage(
                                    model = profileImage,
                                    contentDescription = "Profile Picture",
                                    modifier = Modifier
                                        .size(72.dp)
                                        .clip(CircleShape),
                                    contentScale = ContentScale.Crop
                                )
                            } else {
                                Box(
                                    modifier = Modifier
                                        .size(72.dp)
                                        .clip(CircleShape)
                                        .background(PrimaryContainer.copy(alpha = 0.15f)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = (uiState.user?.name?.firstOrNull()?.uppercase() ?: "A"),
                                        style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold),
                                        color = Primary
                                    )
                                }
                            }

                            // Camera icon overlay
                            Box(
                                modifier = Modifier
                                    .align(Alignment.BottomEnd)
                                    .size(26.dp)
                                    .clip(CircleShape)
                                    .background(Primary)
                                    .clickable {
                                        photoPickerLauncher.launch(
                                            PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                                        )
                                    },
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    Icons.Default.CameraAlt,
                                    contentDescription = "Change Photo",
                                    tint = OnPrimary,
                                    modifier = Modifier.size(14.dp)
                                )
                            }
                        }

                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = uiState.user?.name ?: "Athlete",
                                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                                color = OnSurface,
                            )
                            Text(
                                text = uiState.user?.email ?: "",
                                style = MaterialTheme.typography.bodyMedium,
                                color = OnSurfaceVariant
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                Box(
                                    modifier = Modifier
                                        .background(Primary.copy(alpha = 0.1f), RoundedCornerShape(8.dp))
                                        .padding(horizontal = 10.dp, vertical = 4.dp)
                                ) {
                                    Text(
                                        text = "Pro Member",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = Primary,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }

                        // Edit / Close button
                        IconButton(onClick = {
                            if (uiState.isEditing) viewModel.cancelEdit() else viewModel.toggleEditMode()
                        }) {
                            Icon(
                                if (uiState.isEditing) Icons.Default.Close else Icons.Default.Edit,
                                contentDescription = if (uiState.isEditing) "Cancel Edit" else "Edit",
                                tint = if (uiState.isEditing) Error else OnSurfaceVariant
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Metric Cards Row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    MetricCard(
                        icon = Icons.Default.MonitorWeight,
                        label = "Weight",
                        value = "${uiState.editWeight.ifBlank { "—" }} kg",
                        color = ActivityBlue,
                        modifier = Modifier.weight(1f)
                    )
                    MetricCard(
                        icon = Icons.Default.Height,
                        label = "Height",
                        value = "${uiState.editHeight.ifBlank { "—" }} cm",
                        color = ActivityGreen,
                        modifier = Modifier.weight(1f)
                    )
                    MetricCard(
                        icon = Icons.Default.MonitorWeight,
                        label = "BMI",
                        value = calculateBmi(uiState.editWeight, uiState.editHeight),
                        color = Tertiary,
                        modifier = Modifier.weight(1f)
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))

                // ── Biometrics & Goals Section ──────────────────────────────

                Text(
                    text = "Biometrics & Goals",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = OnSurface,
                    modifier = Modifier.align(Alignment.Start)
                )

                Spacer(modifier = Modifier.height(16.dp))

                // ── READ-ONLY VIEW (when not editing) ───────────────────────
                AnimatedVisibility(
                    visible = !uiState.isEditing,
                    enter = fadeIn() + expandVertically(),
                    exit = fadeOut() + shrinkVertically()
                ) {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(28.dp),
                        colors = CardDefaults.cardColors(containerColor = SurfaceContainerLowest)
                    ) {
                        Column {
                            ProfileInfoRow(
                                label = "Display Name",
                                value = uiState.editName.ifBlank { "Not set" }
                            )
                            HorizontalDivider(
                                modifier = Modifier.padding(horizontal = 16.dp),
                                color = OutlineVariant.copy(alpha = 0.3f)
                            )
                            ProfileInfoRow(
                                label = "Phone Number",
                                value = uiState.editPhone.ifBlank { "Not set" }
                            )
                            HorizontalDivider(
                                modifier = Modifier.padding(horizontal = 16.dp),
                                color = OutlineVariant.copy(alpha = 0.3f)
                            )
                            ProfileInfoRow(
                                label = "Height",
                                value = if (uiState.editHeight.isNotBlank()) "${uiState.editHeight} cm" else "Not set"
                            )
                            HorizontalDivider(
                                modifier = Modifier.padding(horizontal = 16.dp),
                                color = OutlineVariant.copy(alpha = 0.3f)
                            )
                            ProfileInfoRow(
                                label = "Weight",
                                value = if (uiState.editWeight.isNotBlank()) "${uiState.editWeight} kg" else "Not set"
                            )
                            HorizontalDivider(
                                modifier = Modifier.padding(horizontal = 16.dp),
                                color = OutlineVariant.copy(alpha = 0.3f)
                            )
                            ProfileInfoRow(
                                label = "Fitness Goal",
                                value = uiState.editGoal.ifBlank { "Not set" }
                            )
                        }
                    }
                }

                // ── EDIT VIEW (when editing) ────────────────────────────────
                AnimatedVisibility(
                    visible = uiState.isEditing,
                    enter = fadeIn() + expandVertically(),
                    exit = fadeOut() + shrinkVertically()
                ) {
                    Column {
                        EliteInputField(
                            value = uiState.editName,
                            onValueChange = viewModel::updateName,
                            label = "Display Name",
                            modifier = Modifier.fillMaxWidth()
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        EliteInputField(
                            value = uiState.editPhone,
                            onValueChange = viewModel::updatePhone,
                            label = "Phone Number",
                            modifier = Modifier.fillMaxWidth()
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        Row(modifier = Modifier.fillMaxWidth()) {
                            EliteInputField(
                                value = uiState.editHeight,
                                onValueChange = viewModel::updateHeight,
                                label = "Height (cm)",
                                modifier = Modifier.weight(1f)
                            )
                            Spacer(modifier = Modifier.width(16.dp))
                            EliteInputField(
                                value = uiState.editWeight,
                                onValueChange = viewModel::updateWeight,
                                label = "Weight (kg)",
                                modifier = Modifier.weight(1f)
                            )
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        EliteInputField(
                            value = uiState.editGoal,
                            onValueChange = viewModel::updateGoal,
                            label = "Fitness Goal",
                            modifier = Modifier.fillMaxWidth()
                        )

                        if (uiState.error != null) {
                            Text(
                                text = uiState.error ?: "",
                                style = MaterialTheme.typography.bodySmall,
                                color = Error,
                                modifier = Modifier.padding(top = 12.dp)
                            )
                        }

                        if (uiState.success) {
                            Text(
                                text = "Profile updated successfully!",
                                style = MaterialTheme.typography.bodySmall,
                                color = Secondary,
                                modifier = Modifier.padding(top = 12.dp)
                            )
                        }

                        Spacer(modifier = Modifier.height(24.dp))

                        PrimaryButton(
                            text = "Save Changes",
                            onClick = { viewModel.saveProfile() },
                            isLoading = uiState.isLoading,
                            modifier = Modifier.fillMaxWidth()
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        OutlinedButton(
                            onClick = { viewModel.cancelEdit() },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(56.dp),
                            shape = RoundedCornerShape(28.dp)
                        ) {
                            Text(text = "Cancel", color = OnSurfaceVariant)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Account Settings Section
                Text(
                    text = "Account Settings",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = OnSurface,
                    modifier = Modifier.align(Alignment.Start)
                )

                Spacer(modifier = Modifier.height(12.dp))

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(28.dp),
                    colors = CardDefaults.cardColors(containerColor = SurfaceContainerLowest)
                ) {
                    SettingsRow(icon = Icons.Default.Favorite, title = "Favorites & Likes", onClick = onNavigateToFavorites)
                    HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp), color = OutlineVariant.copy(alpha = 0.3f))
                    SettingsRow(icon = Icons.Default.Person, title = "Personal Information", onClick = onNavigateToSettings)
                    HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp), color = OutlineVariant.copy(alpha = 0.3f))
                    SettingsRow(icon = Icons.Default.Notifications, title = "Notifications", onClick = onNavigateToSettings)
                    HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp), color = OutlineVariant.copy(alpha = 0.3f))
                    SettingsRow(icon = Icons.Default.Lock, title = "Privacy & Security", onClick = onNavigateToSettings)
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Sign Out button
                OutlinedButton(
                    onClick = { viewModel.logout(onLogoutSuccess) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    shape = RoundedCornerShape(28.dp),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = Error),
                    border = ButtonDefaults.outlinedButtonBorder(enabled = true)
                ) {
                    Icon(Icons.Default.ExitToApp, contentDescription = null, tint = Error)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(text = "Sign Out", color = Error)
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Delete Account
                TextButton(
                    onClick = { showDeleteDialog = true },
                    modifier = Modifier.padding(bottom = 80.dp)
                ) {
                    Text(
                        text = "Delete Account",
                        style = MaterialTheme.typography.labelLarge,
                        color = Error,
                    )
                }
            }
        }
    }

    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = {
                Text(
                    text = "Delete Account",
                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                    color = Error
                )
            },
            text = {
                Text(
                    text = "This action is permanent and cannot be undone. All your workout records, customized routines, and profile metrics will be deleted.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = OnSurface
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.deleteAccount {
                            showDeleteDialog = false
                            onLogoutSuccess()
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Error, contentColor = OnError)
                ) {
                    Text(text = "Delete")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) {
                    Text(text = "Cancel", color = OnSurfaceVariant)
                }
            },
            containerColor = SurfaceContainerLowest,
            shape = RoundedCornerShape(28.dp)
        )
    }
}

/**
 * Read-only profile info row for displaying data when not in edit mode.
 */
@Composable
fun ProfileInfoRow(
    label: String,
    value: String
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 14.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            color = OnSurfaceVariant,
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Medium),
            color = OnSurface,
        )
    }
}

@Composable
fun MetricCard(
    icon: ImageVector,
    label: String,
    value: String,
    color: androidx.compose.ui.graphics.Color,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = SurfaceContainerLowest),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(color.copy(alpha = 0.1f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(icon, contentDescription = null, tint = color, modifier = Modifier.size(20.dp))
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = value,
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                color = OnSurface,
            )
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall,
                color = OnSurfaceVariant
            )
        }
    }
}

@Composable
fun SettingsRow(
    icon: ImageVector,
    title: String,
    onClick: () -> Unit = {}
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(horizontal = 16.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            icon,
            contentDescription = null,
            tint = OnSurfaceVariant,
            modifier = Modifier.size(22.dp)
        )
        Spacer(modifier = Modifier.width(16.dp))
        Text(
            text = title,
            style = MaterialTheme.typography.bodyLarge,
            color = OnSurface,
            modifier = Modifier.weight(1f)
        )
        Icon(
            Icons.Default.ChevronRight,
            contentDescription = null,
            tint = OnSurfaceVariant.copy(alpha = 0.6f),
            modifier = Modifier.size(20.dp)
        )
    }
}

private fun calculateBmi(weight: String, height: String): String {
    val w = weight.toDoubleOrNull() ?: return "—"
    val h = height.toDoubleOrNull() ?: return "—"
    if (h == 0.0) return "—"
    val bmi = w / ((h / 100) * (h / 100))
    return "%.1f".format(bmi)
}
