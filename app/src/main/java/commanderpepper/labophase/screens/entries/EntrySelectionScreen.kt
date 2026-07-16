package commanderpepper.labophase.screens.entries

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Expand
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import commanderpepper.labophase.screens.entries.models.EntrySelectionUI
import commanderpepper.labophase.screens.roundentry.CopyableResult
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
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            LeaderThumbnail(entrySelectionUI.leader)
            Text("W: ${entrySelectionUI.wins} - L: ${entrySelectionUI.losses}")
            Spacer(modifier = Modifier.weight(1f))
            IconButton(
                onClick = { punkRecordVisibility.value = !punkRecordVisibility.value }) {
                Icon(Icons.Default.Expand, "")
            }
        }
        AnimatedVisibility(visible = punkRecordVisibility.value) {
            Row(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.weight(1f)) {
                    entrySelectionUI.rounds.forEach { round ->
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            LeaderThumbnail(round.leader)
                            Text(round.summary)
                        }
                    }
                }
                Column(modifier = Modifier.weight(1f)) {
                    CopyableResult(text = entrySelectionUI.punkRecord)
                }
            }
        }
    }
}