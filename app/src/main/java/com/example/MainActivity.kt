package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.data.local.AppDatabase
import com.example.data.repository.MentorRepository
import com.example.ui.screens.DashboardScreen
import com.example.ui.screens.HistoryScreen
import com.example.ui.screens.MentorChatScreen
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.viewmodel.MentorViewModel

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // Local Room Database and Repository initialization
        val database = AppDatabase.getDatabase(applicationContext)
        val repository = MentorRepository(database.mentorDao())

        // ViewModel Factory registration
        val viewModel: MentorViewModel by viewModels {
            MentorViewModel.Factory(repository)
        }

        setContent {
            MyApplicationTheme {
                MainAppScreen(viewModel)
            }
        }
    }
}

@Composable
fun MainAppScreen(viewModel: MentorViewModel) {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        bottomBar = {
            NavigationBar {
                NavigationBarItem(
                    selected = currentDestination?.route == "chat",
                    onClick = {
                        viewModel.clearLatestAnalysis()
                        navController.navigate("chat") {
                            popUpTo(navController.graph.findStartDestination().id) { saveState = true }
                            launchSingleTop = true
                            restoreState = true
                        }
                    },
                    icon = { Icon(Icons.Default.Home, contentDescription = "Mentor", modifier = Modifier.size(24.dp)) },
                    label = { Text("Mentor", fontWeight = FontWeight.Bold) },
                    modifier = Modifier.testTag("nav_mentor")
                )
                NavigationBarItem(
                    selected = currentDestination?.route == "dashboard",
                    onClick = {
                        navController.navigate("dashboard") {
                            popUpTo(navController.graph.findStartDestination().id) { saveState = true }
                            launchSingleTop = true
                            restoreState = true
                        }
                    },
                    icon = { Icon(Icons.Default.Star, contentDescription = "Evolução", modifier = Modifier.size(24.dp)) },
                    label = { Text("Evolução", fontWeight = FontWeight.Bold) },
                    modifier = Modifier.testTag("nav_evolution")
                )
                NavigationBarItem(
                    selected = currentDestination?.route == "history",
                    onClick = {
                        navController.navigate("history") {
                            popUpTo(navController.graph.findStartDestination().id) { saveState = true }
                            launchSingleTop = true
                            restoreState = true
                        }
                    },
                    icon = { Icon(Icons.Default.List, contentDescription = "Histórico", modifier = Modifier.size(24.dp)) },
                    label = { Text("Histórico", fontWeight = FontWeight.Bold) },
                    modifier = Modifier.testTag("nav_history")
                )
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = "chat",
            modifier = Modifier.padding(innerPadding)
        ) {
            composable("chat") {
                MentorChatScreen(viewModel = viewModel)
            }
            composable("dashboard") {
                DashboardScreen(viewModel = viewModel)
            }
            composable("history") {
                HistoryScreen(viewModel = viewModel)
            }
        }
    }
}
