package commanderpepper.labophase.screens.entries

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import commanderpepper.labophase.screens.entries.models.EntrySelectionUI
import commanderpepper.labophase.screens.roundentry.LeaderThumbnail
import org.koin.androidx.compose.koinViewModel

@Composable
fun EntrySelectionScreen(
    entrySelectionViewModel: EntrySelectionViewModel = koinViewModel<EntrySelectionViewModelImpl>(),
    onEntrySelect: (Int) -> Unit,
    newEntry: () -> Unit
) {
    val entries = entrySelectionViewModel.entries.collectAsState()
    EntrySelectionScreen(
        entries = entries.value,
        onEntrySelect = onEntrySelect,
        newEntry = newEntry
    )
}

@Composable
fun EntrySelectionScreen(
    entries: List<EntrySelectionUI>,
    onEntrySelect: (Int) -> Unit,
    newEntry: () -> Unit
) {
    LazyColumn() {
        item {
            Button(modifier = Modifier.fillMaxWidth(), onClick = { newEntry() }) {
                Text("New Round")
            }
        }
        items(items = entries) { entry ->
            EntryRow(entrySelectionUI = entry, onEntrySelect = onEntrySelect)
        }
    }
}

@Composable
fun EntryRow(entrySelectionUI: EntrySelectionUI, onEntrySelect: (Int) -> Unit) {
    val punkRecordVisibility = rememberSaveable() { mutableStateOf(false) }
    Column {
        Row(
            modifier = Modifier
                .clickable(onClick = { onEntrySelect(entrySelectionUI.entryId) })
                .fillMaxWidth()
        ) {
            LeaderThumbnail(entrySelectionUI.leader)
            Text("W: ${entrySelectionUI.wins} - L: ${entrySelectionUI.losses}")
            Button(onClick = {
                punkRecordVisibility.value = !punkRecordVisibility.value
            }) { Text("Show PR") }
        }
        AnimatedVisibility(visible = punkRecordVisibility.value) { Text(text = entrySelectionUI.punkRecord) }
    }
}