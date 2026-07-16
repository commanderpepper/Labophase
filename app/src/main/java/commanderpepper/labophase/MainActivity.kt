package commanderpepper.labophase

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import commanderpepper.labophase.navigation.Screen
import commanderpepper.labophase.screens.entries.EntrySelectionScreen
import commanderpepper.labophase.screens.roundentry.RoundEntryScreen
import commanderpepper.labophase.screens.settings.SettingsScreen
import commanderpepper.labophase.ui.theme.LabophaseTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            LabophaseTheme {
                val navController = rememberNavController()
                val currentBackStack by navController.currentBackStackEntryAsState()
                val currentRoute = currentBackStack?.destination?.route
                val topLevelRoutes = listOf(Screen.EntrySelection.route, Screen.Settings.route)
                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    bottomBar = {
                        if (currentRoute in topLevelRoutes) {
                            NavigationBar {
                                NavigationBarItem(
                                    selected = currentRoute == Screen.EntrySelection.route,
                                    onClick = {
                                        navController.navigate(Screen.EntrySelection.route) {
                                            popUpTo(navController.graph.startDestinationId) { saveState = true }
                                            launchSingleTop = true
                                            restoreState = true
                                        }
                                    },
                                    icon = { Icon(Icons.AutoMirrored.Filled.List, contentDescription = null) },
                                    label = { Text("Entries") }
                                )
                                NavigationBarItem(
                                    selected = currentRoute == Screen.Settings.route,
                                    onClick = {
                                        navController.navigate(Screen.Settings.route) {
                                            popUpTo(navController.graph.startDestinationId) { saveState = true }
                                            launchSingleTop = true
                                            restoreState = true
                                        }
                                    },
                                    icon = { Icon(Icons.Default.Settings, contentDescription = null) },
                                    label = { Text("Settings") }
                                )
                            }
                        }
                    }
                ) { innerPadding ->
                    NavHost(
                        navController = navController,
                        startDestination = Screen.EntrySelection.route,
                        modifier = Modifier.padding(innerPadding)
                    ) {
                        composable(Screen.EntrySelection.route) {
                            EntrySelectionScreen(
                                onEntrySelect = { entryId -> navController.navigate(Screen.RoundEntry.navigate(entryId)) },
                                newEntry = { navController.navigate(Screen.RoundEntry.navigate()) }
                            )
                        }
                        composable(
                            route = Screen.RoundEntry.route,
                            arguments = listOf(navArgument(Screen.RoundEntry.ARG) {
                                type = NavType.IntType
                                defaultValue = -1
                            })
                        ) { backStackEntry ->
                            val entryId = backStackEntry.arguments?.getInt(Screen.RoundEntry.ARG) ?: -1
                            RoundEntryScreen(
                                entryId = entryId,
                                onBack = { navController.navigateUp() }
                            )
                        }
                        composable(Screen.Settings.route) {
                            SettingsScreen()
                        }
                    }
                }
            }
        }
    }
}
