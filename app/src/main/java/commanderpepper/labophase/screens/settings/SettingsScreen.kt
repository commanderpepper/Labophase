package commanderpepper.labophase.screens.settings

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.ui.unit.dp
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun SettingsScreen(settingsViewModel: SettingsViewModel = koinViewModel<SettingsViewModelImpl>()){
    val showingAllLeaders = settingsViewModel.showingAllLeaders.collectAsState()
    val showingDieRolls = settingsViewModel.showingDieRoll.collectAsState()
    var showDialog by remember { mutableStateOf(false) }

    SettingsScreen(
        showingAllLeaders = showingAllLeaders.value,
        showingDieRolls = showingDieRolls.value,
        toggleLeaders = settingsViewModel::toggleLeaders,
        toggleDieRoll = settingsViewModel::toggleDieRoll
    ) {
        showDialog = true
    }

    if (showDialog) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            title = { Text("Clear history") },
            text = { Text("This will permanently delete all entries.") },
            icon = { Icon(Icons.Default.Warning, contentDescription = null) },  // optional
            confirmButton = {
                TextButton(onClick = { settingsViewModel.deleteHistory(); showDialog = false }) {
                    Text("Confirm")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }

}

@Composable
fun SettingsScreen(
    showingAllLeaders: Boolean,
    showingDieRolls: Boolean,
    toggleLeaders: () -> Unit,
    toggleDieRoll: () -> Unit,
    clearHistory: () -> Unit
){
    Column(modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp, vertical = 8.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("Show all leaders")
            Switch(
                checked = showingAllLeaders,
                onCheckedChange = { toggleLeaders() }
            )
        }
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("Show die rolls")
            Switch(
                checked = showingDieRolls,
                onCheckedChange = { toggleDieRoll() }
            )
        }
        Button(onClick = { clearHistory() }) { Text("Clear Entries") }
    }
}