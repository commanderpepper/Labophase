package commanderpepper.labophase.screens.settings

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.mikepenz.aboutlibraries.Libs
import com.mikepenz.aboutlibraries.entity.Library
import com.mikepenz.aboutlibraries.util.withContext
import commanderpepper.labophase.R
import commanderpepper.labophase.ui.theme.LabophaseTheme
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun SettingsScreen(settingsViewModel: SettingsViewModel = koinViewModel<SettingsViewModelImpl>()){
    val showingAllLeaders = settingsViewModel.showingAllLeaders.collectAsStateWithLifecycle()
    val showingDieRolls = settingsViewModel.showingDieRoll.collectAsStateWithLifecycle()
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
            title = { Text(stringResource(R.string.dialog_clear_history_title)) },
            text = { Text(stringResource(R.string.dialog_clear_history_body)) },
            icon = { Icon(Icons.Default.Warning, contentDescription = null) },
            confirmButton = {
                TextButton(onClick = { settingsViewModel.deleteHistory(); showDialog = false }) {
                    Text(stringResource(R.string.action_confirm))
                }
            },
            dismissButton = {
                TextButton(onClick = { showDialog = false }) {
                    Text(stringResource(R.string.action_cancel))
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
    val context = LocalContext.current
    val uriHandler = LocalUriHandler.current
    val privacyPolicyUrl = stringResource(R.string.url_privacy_policy)
    var librariesExpanded by remember { mutableStateOf(false) }
    val libraries = remember { runCatching { Libs.Builder().withContext(context).build() }.getOrNull() }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(stringResource(R.string.settings_show_all_leaders))
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
            Text(stringResource(R.string.settings_show_die_rolls))
            Switch(
                checked = showingDieRolls,
                onCheckedChange = { toggleDieRoll() }
            )
        }
        Button(onClick = { clearHistory() }) { Text(stringResource(R.string.btn_clear_entries)) }
        HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
        TextButton(onClick = { uriHandler.openUri(privacyPolicyUrl) }) {
            Text(stringResource(R.string.settings_privacy_policy))
        }
        HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
        Text(
            text = stringResource(R.string.settings_about_title),
            style = MaterialTheme.typography.titleSmall,
            modifier = Modifier.padding(bottom = 4.dp)
        )
        Text(
            text = stringResource(R.string.settings_about_body),
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        Text(
            text = stringResource(R.string.settings_about_disclaimer),
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { librariesExpanded = !librariesExpanded }
                .padding(vertical = 4.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = stringResource(R.string.settings_libraries_title),
                style = MaterialTheme.typography.titleSmall
            )
            Icon(
                imageVector = if (librariesExpanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                contentDescription = stringResource(if (librariesExpanded) R.string.cd_collapse else R.string.cd_expand)
            )
        }
        AnimatedVisibility(visible = librariesExpanded) {
            Column {
                OptcgApiLibraryItem()
                libraries?.libraries?.sortedBy { it.name }?.forEach { library ->
                    LibraryItem(library)
                }
            }
        }
    }
}

@Composable
private fun OptcgApiLibraryItem() {
    Column(modifier = Modifier.padding(vertical = 8.dp)) {
        Text(
            text = stringResource(R.string.settings_optcg_api_name),
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Medium
        )
        Text(
            text = stringResource(R.string.settings_optcg_api_url),
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
    HorizontalDivider()
}

@Composable
private fun LibraryItem(library: Library) {
    Column(modifier = Modifier.padding(vertical = 8.dp)) {
        Text(
            text = library.name,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Medium
        )
        library.licenses.firstOrNull()?.name?.let { licenseName ->
            Text(
                text = licenseName,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
    HorizontalDivider()
}

@Preview(showBackground = true)
@Composable
private fun PreviewSettingsScreen() {
    LabophaseTheme {
        SettingsScreen(
            showingAllLeaders = false,
            showingDieRolls = false,
            toggleLeaders = {},
            toggleDieRoll = {},
            clearHistory = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun PreviewSettingsScreenAllEnabled() {
    LabophaseTheme {
        SettingsScreen(
            showingAllLeaders = true,
            showingDieRolls = true,
            toggleLeaders = {},
            toggleDieRoll = {},
            clearHistory = {}
        )
    }
}