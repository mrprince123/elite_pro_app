package com.example.elite_fitness_app.presentation.auth

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.ArrowDropUp
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.draw.clip
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.elite_fitness_app.presentation.components.EliteInputField
import com.example.elite_fitness_app.presentation.components.PrimaryButton
import com.example.elite_fitness_app.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegisterScreen(
    viewModel: AuthViewModel,
    onNavigateBack: () -> Unit,
    onRegisterSuccess: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    val scrollState = rememberScrollState()

    LaunchedEffect(uiState.isLoggedIn) {
        if (uiState.isLoggedIn) {
            onRegisterSuccess()
        }
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = "Create Account",
                        style = MaterialTheme.typography.titleMedium,
                        color = OnSurface
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            Icons.Default.ArrowBack,
                            contentDescription = "Back",
                            tint = OnSurface
                        )
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = Surface,
                    navigationIconContentColor = OnSurface,
                    titleContentColor = OnSurface
                )
            )
        },
        containerColor = Surface
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 24.dp)
                .verticalScroll(scrollState),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Join Elite Fitness Pro",
                style = MaterialTheme.typography.headlineLarge.copy(
                    fontWeight = FontWeight.Bold,
                ),
                color = OnSurface,
                modifier = Modifier.align(Alignment.Start)
            )

            Text(
                text = "Create your account to unlock personalized plans.",
                style = MaterialTheme.typography.bodyMedium,
                color = OnSurfaceVariant,
                modifier = Modifier
                    .align(Alignment.Start)
                    .padding(top = 4.dp)
            )

            Spacer(modifier = Modifier.height(24.dp))

            EliteInputField(
                value = uiState.registerName,
                onValueChange = viewModel::updateRegisterName,
                label = "Full Name *",
                keyboardType = KeyboardType.Text,
                imeAction = ImeAction.Next,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            EliteInputField(
                value = uiState.registerEmail,
                onValueChange = viewModel::updateRegisterEmail,
                label = "Email Address *",
                keyboardType = KeyboardType.Email,
                imeAction = ImeAction.Next,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            EliteInputField(
                value = uiState.registerPhone,
                onValueChange = viewModel::updateRegisterPhone,
                label = "Phone Number",
                keyboardType = KeyboardType.Phone,
                imeAction = ImeAction.Next,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            EliteInputField(
                value = uiState.registerPassword,
                onValueChange = viewModel::updateRegisterPassword,
                label = "Password *",
                keyboardType = KeyboardType.Password,
                isPassword = true,
                imeAction = ImeAction.Next,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            EliteInputField(
                value = uiState.registerConfirmPassword,
                onValueChange = viewModel::updateRegisterConfirmPassword,
                label = "Confirm Password *",
                keyboardType = KeyboardType.Password,
                isPassword = true,
                imeAction = ImeAction.Next,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            Row(modifier = Modifier.fillMaxWidth()) {
                EliteInputField(
                    value = uiState.registerHeight,
                    onValueChange = viewModel::updateRegisterHeight,
                    label = "Height (cm)",
                    keyboardType = KeyboardType.Number,
                    imeAction = ImeAction.Next,
                    modifier = Modifier.weight(1f)
                )
                Spacer(modifier = Modifier.width(16.dp))
                EliteInputField(
                    value = uiState.registerWeight,
                    onValueChange = viewModel::updateRegisterWeight,
                    label = "Weight (kg)",
                    keyboardType = KeyboardType.Number,
                    imeAction = ImeAction.Next,
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Fitness Goal Dropdown Selector
            var expandedGoal by remember { mutableStateOf(false) }
            val fitnessGoalOptions = listOf("Weight Loss", "Muscle Gain", "Endurance", "Flexibility", "General Fitness")
            
            Column(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = "Fitness Goal",
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
                        .clickable { expandedGoal = true }
                        .padding(horizontal = 16.dp),
                    contentAlignment = Alignment.CenterStart
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = uiState.registerFitnessGoal.ifBlank { "Select Fitness Goal" },
                            style = MaterialTheme.typography.bodyLarge,
                            color = if (uiState.registerFitnessGoal.isBlank()) OnSurfaceVariant.copy(alpha = 0.6f) else OnSurface
                        )
                        Icon(
                            imageVector = if (expandedGoal) Icons.Default.ArrowDropUp else Icons.Default.ArrowDropDown,
                            contentDescription = null,
                            tint = OnSurfaceVariant
                        )
                    }
                }
                DropdownMenu(
                    expanded = expandedGoal,
                    onDismissRequest = { expandedGoal = false },
                    modifier = Modifier
                        .fillMaxWidth(0.9f)
                        .background(SurfaceContainerLowest)
                ) {
                    fitnessGoalOptions.forEach { option ->
                        DropdownMenuItem(
                            text = { Text(text = option) },
                            onClick = {
                                viewModel.updateRegisterFitnessGoal(option)
                                expandedGoal = false
                            }
                        )
                    }
                }
            }

            if (uiState.error != null) {
                Text(
                    text = uiState.error ?: "",
                    style = MaterialTheme.typography.bodySmall,
                    color = Error,
                    modifier = Modifier
                        .align(Alignment.Start)
                        .padding(top = 12.dp)
                )
            }

            Spacer(modifier = Modifier.height(32.dp))

            PrimaryButton(
                text = "Create Account",
                onClick = { viewModel.register() },
                isLoading = uiState.isLoading,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}
