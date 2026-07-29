package commanderpepper.labophase

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.ui.res.stringResource
import commanderpepper.labophase.R
import androidx.compose.material3.adaptive.navigationsuite.ExperimentalMaterial3AdaptiveNavigationSuiteApi
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteScaffold
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
import commanderpepper.labophase.navigation.Stats
import commanderpepper.labophase.screens.entries.EntrySelectionScreen
import commanderpepper.labophase.screens.roundentry.RoundEntryScreen
import commanderpepper.labophase.screens.settings.SettingsScreen
import commanderpepper.labophase.screens.stats.StatsScreen
import commanderpepper.labophase.ui.theme.LabophaseTheme

class MainActivity : ComponentActivity() {
    @OptIn(ExperimentalMaterial3AdaptiveNavigationSuiteApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            LabophaseTheme {
                val navController = rememberNavController()
                val currentBackStack by navController.currentBackStackEntryAsState()
                val currentDestination = currentBackStack?.destination

                NavigationSuiteScaffold(
                    navigationSuiteItems = {
                        item(
                            icon = { Icon(Icons.AutoMirrored.Filled.List, contentDescription = null) },
                            label = { Text(stringResource(R.string.nav_entries)) },
                            selected = currentDestination?.hasRoute<EntrySelection>() == true,
                            onClick = {
                                navController.navigate(EntrySelection) {
                                    popUpTo(navController.graph.startDestinationId) { saveState = true }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            }
                        )
                        item(
                            icon = { Icon(Icons.Default.BarChart, contentDescription = null) },
                            label = { Text(stringResource(R.string.nav_stats)) },
                            selected = currentDestination?.hasRoute<Stats>() == true,
                            onClick = {
                                navController.navigate(Stats) {
                                    popUpTo(navController.graph.startDestinationId) { saveState = true }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            }
                        )
                        item(
                            icon = { Icon(Icons.Default.Settings, contentDescription = null) },
                            label = { Text(stringResource(R.string.nav_settings)) },
                            selected = currentDestination?.hasRoute<Settings>() == true,
                            onClick = {
                                navController.navigate(Settings) {
                                    popUpTo(navController.graph.startDestinationId) { saveState = true }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            }
                        )
                    }
                ) {
                    NavHost(
                        navController = navController,
                        startDestination = EntrySelection,
                        modifier = Modifier.fillMaxSize()
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
                        composable<Stats> {
                            StatsScreen()
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
