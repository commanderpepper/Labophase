package commanderpepper.labophase.screens.entries

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import commanderpepper.labophase.models.Leader
import commanderpepper.labophase.screens.entries.models.EntrySelectionUI
import commanderpepper.labophase.screens.entries.models.RoundEntrySelectionUI
import commanderpepper.labophase.screens.roundentry.CopyableResult
import commanderpepper.labophase.screens.roundentry.LeaderThumbnail
import commanderpepper.labophase.ui.theme.LabophaseTheme
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
    Scaffold(
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = newEntry,
                icon = { Icon(Icons.Default.Add, contentDescription = null) },
                text = { Text("New Round") }
            )
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier.padding(innerPadding),
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(items = entries) { entry ->
                EntryRow(entrySelectionUI = entry, onEntrySelect = onEntrySelect)
            }
        }
    }
}

@Composable
fun EntryRow(entrySelectionUI: EntrySelectionUI, onEntrySelect: (Int) -> Unit) {
    val punkRecordVisibility = rememberSaveable { mutableStateOf(false) }
    val rotation by animateFloatAsState(
        targetValue = if (punkRecordVisibility.value) 180f else 0f,
        label = "expand_rotation"
    )
    OutlinedCard(modifier = Modifier.fillMaxWidth()) {
        ListItem(
            modifier = Modifier.clickable { onEntrySelect(entrySelectionUI.entryId) },
            leadingContent = { LeaderThumbnail(entrySelectionUI.leader) },
            headlineContent = { Text("W: ${entrySelectionUI.wins} - L: ${entrySelectionUI.losses}") },
            trailingContent = {
                IconButton(onClick = { punkRecordVisibility.value = !punkRecordVisibility.value }) {
                    Icon(
                        Icons.Default.ExpandMore,
                        contentDescription = if (punkRecordVisibility.value) "Collapse" else "Expand",
                        modifier = Modifier.rotate(rotation)
                    )
                }
            }
        )
        AnimatedVisibility(visible = punkRecordVisibility.value) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 16.dp, end = 16.dp, bottom = 8.dp)
            ) {
                entrySelectionUI.rounds.forEach { round ->
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        LeaderThumbnail(round.leader)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(round.summary)
                    }
                }
                CopyableResult(text = entrySelectionUI.punkRecord)
            }
        }
    }
}

private val previewRounds = listOf(
    RoundEntrySelectionUI(leader = Leader.RShanks, summary = "R Shanks, W, 1"),
    RoundEntrySelectionUI(leader = Leader.PBLuffy, summary = "PB Luffy, L, 2")
)
private val previewEntry1 = EntrySelectionUI(
    entryId = 1,
    leader = Leader.UGLuffy,
    wins = 3,
    losses = 1,
    punkRecord = "!PR add\nUG Luffy\nW R Shanks 1st\nW PB Luffy 2nd\nW R Shanks 1st\nL PB Luffy 2nd",
    rounds = previewRounds
)
private val previewEntry2 = EntrySelectionUI(
    entryId = 2,
    leader = Leader.RShanks,
    wins = 1,
    losses = 2,
    punkRecord = "!PR add\nR Shanks\nW UG Luffy 1st\nL PB Luffy 2nd\nL UG Luffy 1st",
    rounds = previewRounds
)

@Preview(showBackground = true)
@Composable
private fun PreviewEntrySelectionScreenEmpty() {
    LabophaseTheme {
        EntrySelectionScreen(
            entries = emptyList(),
            onEntrySelect = {},
            newEntry = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun PreviewEntrySelectionScreen() {
    LabophaseTheme {
        EntrySelectionScreen(
            entries = listOf(previewEntry1, previewEntry2),
            onEntrySelect = {},
            newEntry = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun PreviewEntryRow() {
    LabophaseTheme {
        EntryRow(entrySelectionUI = previewEntry1, onEntrySelect = {})
    }
}
