package commanderpepper.labophase.screens.entries

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.DeleteForever
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.VerticalDivider
import androidx.compose.material3.adaptive.ExperimentalMaterial3AdaptiveApi
import androidx.compose.material3.adaptive.currentWindowAdaptiveInfo
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.window.core.layout.WindowWidthSizeClass
import commanderpepper.labophase.R
import commanderpepper.labophase.models.Leader
import commanderpepper.labophase.models.locationById
import commanderpepper.labophase.screens.entries.models.EntrySelectionUI
import commanderpepper.labophase.screens.entries.models.RoundEntrySelectionUI
import commanderpepper.labophase.screens.roundentry.CopyableResult
import commanderpepper.labophase.screens.roundentry.LeaderThumbnail
import commanderpepper.labophase.screens.roundentry.RoundEntryScreen
import commanderpepper.labophase.ui.theme.LabophaseTheme
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import org.koin.androidx.compose.koinViewModel

@Composable
fun EntrySelectionScreen(
    entrySelectionViewModel: EntrySelectionViewModel = koinViewModel<EntrySelectionViewModelImpl>(),
    onEntrySelect: (Int) -> Unit,
    newEntry: () -> Unit
) {
    val uiState = entrySelectionViewModel.entrySelectionUiState.collectAsStateWithLifecycle()

    EntrySelectionScreen(
        entries = uiState.value.entries,
        isLoading = uiState.value.isLoading,
        errorMessage = uiState.value.errorMessage,
        onEntrySelect = onEntrySelect,
        onEntryDelete = entrySelectionViewModel::deleteEntry,
        newEntry = newEntry
    )
}

@OptIn(ExperimentalMaterial3AdaptiveApi::class)
@Composable
fun EntrySelectionScreen(
    entries: List<EntrySelectionUI>,
    isLoading: Boolean,
    errorMessage: String?,
    onEntrySelect: (Int) -> Unit,
    onEntryDelete: (Int) -> Unit,
    newEntry: () -> Unit
) {
    val widthSizeClass = currentWindowAdaptiveInfo().windowSizeClass.windowWidthSizeClass
    val isExpandedWidth = widthSizeClass != WindowWidthSizeClass.COMPACT

    if (isExpandedWidth) {
        // null = nothing selected, -1 = new entry, positive Int = existing entry ID
        var selectedEntryId by remember { mutableStateOf<Int?>(null) }

        Row(modifier = Modifier.fillMaxSize()) {
            Box(
                modifier = Modifier
                    .weight(2f)
                    .fillMaxHeight()
            ) {
                EntryListContent(
                    entries = entries,
                    isLoading = isLoading,
                    errorMessage = errorMessage,
                    onEntrySelect = { selectedEntryId = it },
                    onEntryDelete = onEntryDelete,
                    newEntry = { selectedEntryId = -1 }
                )
            }
            VerticalDivider(modifier = Modifier.padding(top = 92.dp, bottom = 16.dp))
            Box(
                modifier = Modifier
                    .weight(3f)
                    .fillMaxHeight()
            ) {
                key(selectedEntryId) {
                    when (val id = selectedEntryId) {
                        null -> Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("Select an entry")
                        }
                        -1 -> RoundEntryScreen(
                            entryId = null,
                            onBack = { selectedEntryId = null }
                        )
                        else -> RoundEntryScreen(
                            entryId = id,
                            onBack = { selectedEntryId = null }
                        )
                    }
                }
            }
        }
    } else {
        EntryListContent(
            entries = entries,
            isLoading = isLoading,
            errorMessage = errorMessage,
            onEntrySelect = onEntrySelect,
            onEntryDelete = onEntryDelete,
            newEntry = newEntry
        )
    }
}

@Composable
private fun EntryListContent(
    entries: List<EntrySelectionUI>,
    isLoading: Boolean,
    errorMessage: String?,
    onEntrySelect: (Int) -> Unit,
    onEntryDelete: (Int) -> Unit,
    newEntry: () -> Unit
) {
    Scaffold(
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = newEntry,
                icon = { Icon(Icons.Default.Add, contentDescription = null) },
                text = { Text(stringResource(R.string.fab_new_entry)) }
            )
        }
    ) { innerPadding ->
        if (isLoading) {
            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                CircularProgressIndicator()
            }
        } else if (errorMessage.isNullOrEmpty().not()) {
            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(errorMessage)
            }
        } else {
            LazyVerticalGrid(
                columns = GridCells.Adaptive(minSize = 360.dp),
                modifier = Modifier.padding(innerPadding),
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(items = entries, key = { it.entryId }) { entry ->
                    EntryRow(entrySelectionUI = entry, onEntrySelect = onEntrySelect, onEntryDelete = onEntryDelete)
                }
            }
        }
    }
}

@Composable
fun EntryRow(entrySelectionUI: EntrySelectionUI, onEntrySelect: (Int) -> Unit, onEntryDelete: (Int) -> Unit) {
    val formattedDate = remember(entrySelectionUI.date) {
        entrySelectionUI.date?.let {
            runCatching { LocalDate.parse(it).format(DateTimeFormatter.ofPattern("MMM d, yyyy")) }
                .getOrDefault(it)
        }
    }
    val nameLabel = entrySelectionUI.title ?: entrySelectionUI.leader.name
    val contextLabel = buildString {
        entrySelectionUI.locationId?.let { locationById(it) }?.let { append(it.abbreviation) }
        formattedDate?.let { if (isNotEmpty()) append(" on $it") else append("on $it") }
    }.takeIf { it.isNotEmpty() }
    val punkRecordVisibility = rememberSaveable { mutableStateOf(false) }
    var showDeleteDialog by rememberSaveable { mutableStateOf(false) }
    val rotation by animateFloatAsState(
        targetValue = if (punkRecordVisibility.value) 180f else 0f,
        label = "expand_rotation"
    )

    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            icon = { Icon(Icons.Default.Warning, contentDescription = null) },
            title = { Text(stringResource(R.string.dialog_delete_entry_title)) },
            text = { Text(stringResource(R.string.dialog_delete_entry_body)) },
            confirmButton = {
                TextButton(onClick = {
                    onEntryDelete(entrySelectionUI.entryId)
                    showDeleteDialog = false
                }) { Text(stringResource(R.string.action_delete)) }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) { Text(stringResource(R.string.action_cancel)) }
            }
        )
    }

    OutlinedCard(modifier = Modifier.fillMaxWidth()) {
        ListItem(
            modifier = Modifier.clickable { onEntrySelect(entrySelectionUI.entryId) },
            leadingContent = {
                Box(modifier = Modifier.fillMaxHeight(), contentAlignment = Alignment.Center) {
                    LeaderThumbnail(entrySelectionUI.leader)
                }
            },
            overlineContent = contextLabel?.let { { Text(it) } },
            headlineContent = { Text(nameLabel) },
            supportingContent = { Text("W: ${entrySelectionUI.wins} - L: ${entrySelectionUI.losses}") },
            trailingContent = {
                Row {
                    IconButton(onClick = { punkRecordVisibility.value = !punkRecordVisibility.value }) {
                        Icon(
                            Icons.Default.ExpandMore,
                            contentDescription = if (punkRecordVisibility.value) stringResource(R.string.cd_collapse) else stringResource(R.string.cd_expand),
                            modifier = Modifier.rotate(rotation)
                        )
                    }
                    IconButton(onClick = { showDeleteDialog = true }) {
                        Icon(Icons.Default.DeleteForever, contentDescription = stringResource(R.string.cd_delete_entry))
                    }
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
        EntryListContent(
            entries = emptyList(),
            isLoading = false,
            errorMessage = null,
            onEntrySelect = {},
            onEntryDelete = {},
            newEntry = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun PreviewEntrySelectionScreen() {
    LabophaseTheme {
        EntryListContent(
            entries = listOf(previewEntry1, previewEntry2),
            isLoading = false,
            errorMessage = null,
            onEntrySelect = {},
            onEntryDelete = {},
            newEntry = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun PreviewEntrySelectionScreenLoading() {
    LabophaseTheme {
        EntryListContent(
            entries = emptyList(),
            isLoading = true,
            errorMessage = null,
            onEntrySelect = {},
            onEntryDelete = {},
            newEntry = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun PreviewEntrySelectionScreenError() {
    LabophaseTheme {
        EntryListContent(
            entries = emptyList(),
            isLoading = false,
            errorMessage = "Something went wrong",
            onEntrySelect = {},
            onEntryDelete = {},
            newEntry = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun PreviewEntryRow() {
    LabophaseTheme {
        EntryRow(entrySelectionUI = previewEntry1, onEntrySelect = {}, onEntryDelete = {})
    }
}

// Wide previews — activate the two-pane layout and the adaptive grid

@Preview(showBackground = true, widthDp = 700, heightDp = 500)
@Composable
private fun PreviewEntryListContentMedium() {
    LabophaseTheme {
        EntryListContent(
            entries = listOf(previewEntry1, previewEntry2),
            isLoading = false,
            errorMessage = null,
            onEntrySelect = {},
            onEntryDelete = {},
            newEntry = {}
        )
    }
}

@Preview(showBackground = true, widthDp = 1000, heightDp = 700)
@Composable
private fun PreviewEntryListContentTablet() {
    LabophaseTheme {
        EntryListContent(
            entries = listOf(previewEntry1, previewEntry2),
            isLoading = false,
            errorMessage = null,
            onEntrySelect = {},
            onEntryDelete = {},
            newEntry = {}
        )
    }
}

@Preview(showBackground = true, widthDp = 900, heightDp = 600)
@Composable
private fun PreviewEntrySelectionTwoPaneMedium() {
    LabophaseTheme {
        EntrySelectionScreen(
            entries = listOf(previewEntry1, previewEntry2),
            isLoading = false,
            errorMessage = null,
            onEntrySelect = {},
            onEntryDelete = {},
            newEntry = {}
        )
    }
}

@Preview(showBackground = true, widthDp = 1280, heightDp = 800)
@Composable
private fun PreviewEntrySelectionTwoPaneExpanded() {
    LabophaseTheme {
        EntrySelectionScreen(
            entries = listOf(previewEntry1, previewEntry2),
            isLoading = false,
            errorMessage = null,
            onEntrySelect = {},
            onEntryDelete = {},
            newEntry = {}
        )
    }
}
