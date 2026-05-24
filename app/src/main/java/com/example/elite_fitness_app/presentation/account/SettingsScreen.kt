package com.example.elite_fitness_app.presentation.account

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.elite_fitness_app.presentation.components.EliteInputField
import com.example.elite_fitness_app.presentation.components.GlassCard
import com.example.elite_fitness_app.presentation.components.PrimaryButton
import com.example.elite_fitness_app.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    viewModel: SettingsViewModel,
    onNavigateBack: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    val isDarkMode by viewModel.isDarkMode.collectAsState()
    val isNotificationsEnabled by viewModel.isNotificationsEnabled.collectAsState()
    val scrollState = rememberScrollState()

    Scaffold(
        topBar = {
            Surface(color = Surface) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .statusBarsPadding()
                        .padding(horizontal = 12.dp, vertical = 12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Back",
                            tint = OnSurface
                        )
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Settings",
                        style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold),
                        color = OnSurface
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
                .verticalScroll(scrollState)
                .padding(horizontal = 20.dp, vertical = 16.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            // App Preferences Section
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text(
                    text = "Preferences",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = OnSurface
                )
                
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(28.dp),
                    colors = CardDefaults.cardColors(containerColor = SurfaceContainerLowest)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        // Dark Mode Row
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                Icon(Icons.Default.Palette, contentDescription = null, tint = OnSurfaceVariant)
                                Column {
                                    Text("Dark Mode", style = MaterialTheme.typography.bodyLarge, color = OnSurface)
                                    Text("Enable dark theme throughout the app", style = MaterialTheme.typography.bodySmall, color = OnSurfaceVariant)
                                }
                            }
                            Switch(
                                checked = isDarkMode,
                                onCheckedChange = viewModel::toggleDarkMode,
                                colors = SwitchDefaults.colors(checkedThumbColor = Primary, checkedTrackColor = PrimaryContainer.copy(alpha = 0.5f))
                            )
                        }

                        Spacer(modifier = Modifier.height(16.dp))
                        HorizontalDivider(color = OutlineVariant.copy(alpha = 0.3f))
                        Spacer(modifier = Modifier.height(16.dp))

                        // Push Notifications Row
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                Icon(Icons.Default.Notifications, contentDescription = null, tint = OnSurfaceVariant)
                                Column {
                                    Text("Push Notifications", style = MaterialTheme.typography.bodyLarge, color = OnSurface)
                                    Text("Receive exercise reminders & stats digest", style = MaterialTheme.typography.bodySmall, color = OnSurfaceVariant)
                                }
                            }
                            Switch(
                                checked = isNotificationsEnabled,
                                onCheckedChange = viewModel::toggleNotifications,
                                colors = SwitchDefaults.colors(checkedThumbColor = Primary, checkedTrackColor = PrimaryContainer.copy(alpha = 0.5f))
                            )
                        }
                    }
                }
            }

            // Change Password Section
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text(
                    text = "Security",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = OnSurface
                )

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(28.dp),
                    colors = CardDefaults.cardColors(containerColor = SurfaceContainerLowest)
                ) {
                    Column(
                        modifier = Modifier.padding(20.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Icon(Icons.Default.Lock, contentDescription = null, tint = OnSurfaceVariant)
                            Text(
                                "Change Password",
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                color = OnSurface
                            )
                        }

                        EliteInputField(
                            value = uiState.oldPassword,
                            onValueChange = viewModel::updateOldPassword,
                            label = "Current Password",
                            isPassword = true,
                            modifier = Modifier.fillMaxWidth()
                        )

                        EliteInputField(
                            value = uiState.newPassword,
                            onValueChange = viewModel::updateNewPassword,
                            label = "New Password",
                            isPassword = true,
                            modifier = Modifier.fillMaxWidth()
                        )

                        EliteInputField(
                            value = uiState.confirmPassword,
                            onValueChange = viewModel::updateConfirmPassword,
                            label = "Confirm New Password",
                            isPassword = true,
                            modifier = Modifier.fillMaxWidth()
                        )

                        if (uiState.passwordError != null) {
                            Text(
                                text = uiState.passwordError ?: "",
                                style = MaterialTheme.typography.bodySmall,
                                color = Error
                            )
                        }

                        if (uiState.passwordSuccess != null) {
                            Text(
                                text = uiState.passwordSuccess ?: "",
                                style = MaterialTheme.typography.bodySmall,
                                color = Secondary
                            )
                        }

                        PrimaryButton(
                            text = "Update Password",
                            onClick = { viewModel.changePassword() },
                            isLoading = uiState.isPasswordLoading,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
            }

            // Version info
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Elite Fitness Pro",
                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
                    color = OnSurfaceVariant
                )
                Text(
                    text = "Version 1.0.0 (Production)",
                    style = MaterialTheme.typography.bodySmall,
                    color = OnSurfaceVariant.copy(alpha = 0.6f)
                )
            }
        }
    }
}
