package commanderpepper.labophase

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import commanderpepper.labophase.navigation.Screen
import commanderpepper.labophase.screens.entries.EntrySelectionScreen
import commanderpepper.labophase.screens.roundentry.RoundEntryScreen
import commanderpepper.labophase.ui.theme.LabophaseTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            LabophaseTheme {
                val navController = rememberNavController()
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
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
                            RoundEntryScreen(entryId = entryId)
                        }
                    }
                }
            }
        }
    }
}
