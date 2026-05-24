package com.example.elite_fitness_app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.navigation.compose.rememberNavController
import com.example.elite_fitness_app.core.navigation.AppNavHost
import com.example.elite_fitness_app.ui.theme.ElitefitnessappTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ElitefitnessappTheme {
                val navController = rememberNavController()
                AppNavHost(navController = navController)
            }
        }
    }
}