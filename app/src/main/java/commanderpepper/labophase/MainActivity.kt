package commanderpepper.labophase

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import commanderpepper.labophase.navigation.EntrySelection
import commanderpepper.labophase.navigation.RoundEntry
import commanderpepper.labophase.navigation.Settings
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
                val currentDestination = currentBackStack?.destination
                val isTopLevel = currentDestination?.hasRoute<EntrySelection>() == true ||
                        currentDestination?.hasRoute<Settings>() == true
                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    bottomBar = {
                        if (isTopLevel) {
                            NavigationBar {
                                NavigationBarItem(
                                    selected = currentDestination.hasRoute<EntrySelection>(),
                                    onClick = {
                                        navController.navigate(EntrySelection) {
                                            popUpTo(navController.graph.startDestinationId) { saveState = true }
                                            launchSingleTop = true
                                            restoreState = true
                                        }
                                    },
                                    icon = { Icon(Icons.AutoMirrored.Filled.List, contentDescription = null) },
                                    label = { Text("Entries") }
                                )
                                NavigationBarItem(
                                    selected = currentDestination.hasRoute<Settings>(),
                                    onClick = {
                                        navController.navigate(Settings) {
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
                        startDestination = EntrySelection,
                        modifier = Modifier.padding(innerPadding)
                    ) {
                        composable<EntrySelection> {
                            EntrySelectionScreen(
                                onEntrySelect = { entryId -> navController.navigate(RoundEntry(entryId)) },
                                newEntry = { navController.navigate(RoundEntry()) }
                            )
                        }
                        composable<RoundEntry> { backStackEntry ->
                            val route: RoundEntry = backStackEntry.toRoute()
                            RoundEntryScreen(
                                entryId = route.entryId,
                                onBack = { navController.navigateUp() }
                            )
                        }
                        composable<Settings> {
                            SettingsScreen()
                        }
                    }
                }
            }
        }
    }
}
