package com.example.elite_fitness_app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.navigation.compose.rememberNavController
import com.example.elite_fitness_app.core.navigation.AppNavHost
import com.example.elite_fitness_app.ui.theme.ElitefitnessappTheme
import dagger.hilt.android.AndroidEntryPoint

import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.example.elite_fitness_app.core.datastore.AppDataStore
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    
    @Inject
    lateinit var appDataStore: AppDataStore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val isDarkMode by appDataStore.isDarkMode.collectAsState(initial = true)
            ElitefitnessappTheme(darkTheme = isDarkMode) {
                val navController = rememberNavController()
                AppNavHost(navController = navController)
            }
        }
    }
}