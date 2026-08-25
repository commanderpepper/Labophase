package commanderpepper.labophase.screens.stats

import androidx.compose.foundation.background
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.AlertDialogDefaults
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuAnchorType
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.VerticalDivider
import androidx.compose.material3.adaptive.ExperimentalMaterial3AdaptiveApi
import androidx.compose.material3.adaptive.currentWindowAdaptiveInfo
import androidx.window.core.layout.WindowWidthSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil3.compose.AsyncImage
import commanderpepper.labophase.R
import commanderpepper.labophase.models.Leader
import commanderpepper.labophase.models.LeaderColor
import commanderpepper.labophase.models.Location
import commanderpepper.labophase.models.METAS_LIST
import commanderpepper.labophase.models.Meta
import commanderpepper.labophase.models.Opponent
import commanderpepper.labophase.screens.roundentry.LeaderThumbnail
import commanderpepper.labophase.screens.roundentry.WithSharedBorderAngle
import commanderpepper.labophase.screens.roundentry.animatedBorder
import commanderpepper.labophase.screens.stats.models.LeaderSelectedOption
import commanderpepper.labophase.screens.stats.models.LocationOption
import commanderpepper.labophase.screens.stats.models.MetaOption
import commanderpepper.labophase.screens.stats.models.StatLeaderInfo
import commanderpepper.labophase.screens.stats.models.StatOpponentInfo
import commanderpepper.labophase.screens.stats.models.StatsTab
import commanderpepper.labophase.screens.stats.models.StatsUIState
import commanderpepper.labophase.ui.theme.LabophaseTheme
import org.koin.androidx.compose.koinViewModel

private sealed class OpponentDialogState {
    data object Add : OpponentDialogState()
    data class Edit(val opponent: Opponent) : OpponentDialogState()
}

@Composable
fun StatsScreen(
    statsViewModel: StatsViewModel = koinViewModel<StatsViewModelImpl>()
) {
    val uiState = statsViewModel.statsUIState.collectAsStateWithLifecycle()
    StatsScreen(
        statsUIState = uiState.value,
        onLeaderSelected = statsViewModel::leaderSelected,
        onAllLeadersSelected = statsViewModel::allLeadersSelected,
        onMetaSelected = statsViewModel::metaSelected,
        onAllMetasSelected = statsViewModel::allMetaSelected,
        onLocationSelected = statsViewModel::locationSelected,
        onAllLocationsSelected = statsViewModel::allLocationSelected,
        onTabSelected = statsViewModel::tabSelected,
        onAddOpponent = statsViewModel::addOpponent,
        onUpdateOpponent = statsViewModel::updateOpponent,
        onDeleteOpponent = statsViewModel::deleteOpponent
    )
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3AdaptiveApi::class)
@Composable
fun StatsScreen(
    statsUIState: StatsUIState,
    onLeaderSelected: (Leader) -> Unit,
    onAllLeadersSelected: () -> Unit,
    onMetaSelected: (Meta) -> Unit,
    onAllMetasSelected: () -> Unit,
    onLocationSelected: (Location) -> Unit,
    onAllLocationsSelected: () -> Unit,
    onTabSelected: (StatsTab) -> Unit,
    onAddOpponent: (String, String) -> Unit,
    onUpdateOpponent: (Int, String, String) -> Unit,
    onDeleteOpponent: (Int) -> Unit
) {
    val isExpandedWidth = currentWindowAdaptiveInfo().windowSizeClass.windowWidthSizeClass != WindowWidthSizeClass.COMPACT
    var dialogState by remember { mutableStateOf<OpponentDialogState?>(null) }
    val showFab = statsUIState.activeTab == StatsTab.Opponents && !statsUIState.isLoading && statsUIState.errorMessage == null

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(title = { Text(stringResource(R.string.stats_title)) })
        },
        floatingActionButton = {
            if (showFab) {
                FloatingActionButton(onClick = { dialogState = OpponentDialogState.Add }) {
                    Icon(Icons.Filled.Add, contentDescription = stringResource(R.string.stats_add_opponent))
                }
            }
        }
    ) { innerPadding ->
        WithSharedBorderAngle {
        if (statsUIState.isLoading) {
            Column(
                modifier = Modifier.fillMaxSize().padding(innerPadding),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) { CircularProgressIndicator() }
        } else if (statsUIState.errorMessage != null) {
            Column(
                modifier = Modifier.fillMaxSize().padding(innerPadding),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) { Text(statsUIState.errorMessage) }
        } else if (isExpandedWidth) {
            Row(modifier = Modifier.fillMaxSize()) {
                Column(
                    modifier = Modifier
                        .weight(2f)
                        .fillMaxHeight()
                        .padding(innerPadding)
                        .verticalScroll(rememberScrollState())
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    StatsHeader(
                        statsUIState = statsUIState,
                        onLeaderSelected = onLeaderSelected,
                        onAllLeadersSelected = onAllLeadersSelected,
                        onMetaSelected = onMetaSelected,
                        onAllMetasSelected = onAllMetasSelected,
                        onLocationSelected = onLocationSelected,
                        onAllLocationsSelected = onAllLocationsSelected
                    )
                }
                VerticalDivider(modifier = Modifier.padding(vertical = 16.dp))
                RightPane(
                    statsUIState = statsUIState,
                    onTabSelected = onTabSelected,
                    onEditOpponent = { dialogState = OpponentDialogState.Edit(it) },
                    modifier = Modifier
                        .weight(3f)
                        .fillMaxHeight()
                        .padding(innerPadding)
                )
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                item {
                    StatsHeader(
                        statsUIState = statsUIState,
                        onLeaderSelected = onLeaderSelected,
                        onAllLeadersSelected = onAllLeadersSelected,
                        onMetaSelected = onMetaSelected,
                        onAllMetasSelected = onAllMetasSelected,
                        onLocationSelected = onLocationSelected,
                        onAllLocationsSelected = onAllLocationsSelected
                    )
                }
                item {
                    StatsTabRow(
                        activeTab = statsUIState.activeTab,
                        onTabSelected = onTabSelected
                    )
                }
                if (statsUIState.activeTab == StatsTab.Leaders) {
                    item {
                        HorizontalDivider(modifier = Modifier.padding(vertical = 4.dp))
                        Text(
                            text = stringResource(R.string.stats_leaders_against),
                            style = MaterialTheme.typography.titleSmall,
                            modifier = Modifier.padding(bottom = 4.dp)
                        )
                    }
                    items(statsUIState.leadersPlayerAgainst) { opponent ->
                        LeaderAgainstRow(opponent = opponent)
                    }
                } else {
                    item {
                        HorizontalDivider(modifier = Modifier.padding(vertical = 4.dp))
                        Text(
                            text = stringResource(R.string.stats_opponents),
                            style = MaterialTheme.typography.titleSmall,
                            modifier = Modifier.padding(bottom = 4.dp)
                        )
                    }
                    items(statsUIState.opponentStats) { stat ->
                        OpponentStatRow(
                            stat = stat,
                            onEditOpponent = { dialogState = OpponentDialogState.Edit(stat.opponent) }
                        )
                    }
                }
            }
        }
        } // WithSharedBorderAngle
    }

    dialogState?.let { state ->
        OpponentDialog(
            state = state,
            onDismiss = { dialogState = null },
            onAdd = { name, nick -> onAddOpponent(name, nick); dialogState = null },
            onUpdate = { id, name, nick -> onUpdateOpponent(id, name, nick); dialogState = null },
            onDelete = { id -> onDeleteOpponent(id); dialogState = null }
        )
    }
}

@Composable
private fun OpponentDialog(
    state: OpponentDialogState,
    onDismiss: () -> Unit,
    onAdd: (String, String) -> Unit,
    onUpdate: (Int, String, String) -> Unit,
    onDelete: (Int) -> Unit
) {
    val isEdit = state is OpponentDialogState.Edit
    val existing = (state as? OpponentDialogState.Edit)?.opponent
    var name by remember(state) { mutableStateOf(existing?.name ?: "") }
    var nickname by remember(state) { mutableStateOf(existing?.nickname ?: "") }
    val canSave = if (isEdit) {
        nickname.isNotBlank() && !nickname.contains(' ')
    } else {
        name.isNotBlank() && !name.contains(' ') && nickname.isNotBlank() && !nickname.contains(' ')
    }

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = MaterialTheme.shapes.large,
            tonalElevation = AlertDialogDefaults.TonalElevation
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    text = stringResource(if (isEdit) R.string.stats_edit_opponent else R.string.stats_add_opponent),
                    style = MaterialTheme.typography.headlineSmall
                )
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text(stringResource(R.string.label_handle)) },
                    singleLine = true,
                    enabled = !isEdit,
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = nickname,
                    onValueChange = { nickname = it },
                    label = { Text(stringResource(R.string.label_nickname)) },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    if (isEdit && existing != null) {
                        TextButton(
                            onClick = { onDelete(existing.id) },
                            colors = ButtonDefaults.textButtonColors(contentColor = MaterialTheme.colorScheme.error)
                        ) {
                            Text(stringResource(R.string.action_delete))
                        }
                    }
                    Spacer(modifier = Modifier.weight(1f))
                    TextButton(onClick = onDismiss) {
                        Text(stringResource(R.string.action_cancel))
                    }
                    TextButton(
                        onClick = {
                            if (isEdit && existing != null) {
                                onUpdate(existing.id, name.trim(), nickname.trim())
                            } else {
                                onAdd(name.trim(), nickname.trim())
                            }
                        },
                        enabled = canSave
                    ) {
                        Text(stringResource(R.string.action_save))
                    }
                }
            }
        }
    }
}

@Composable
private fun StatsTabRow(activeTab: StatsTab, onTabSelected: (StatsTab) -> Unit) {
    val selectedIndex = if (activeTab == StatsTab.Leaders) 0 else 1
    TabRow(selectedTabIndex = selectedIndex) {
        Tab(
            selected = selectedIndex == 0,
            onClick = { onTabSelected(StatsTab.Leaders) },
            text = { Text(stringResource(R.string.stats_tab_leaders)) }
        )
        Tab(
            selected = selectedIndex == 1,
            onClick = { onTabSelected(StatsTab.Opponents) },
            text = { Text(stringResource(R.string.stats_tab_opponents)) }
        )
    }
}

@Composable
private fun RightPane(
    statsUIState: StatsUIState,
    onTabSelected: (StatsTab) -> Unit,
    onEditOpponent: (Opponent) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        StatsTabRow(activeTab = statsUIState.activeTab, onTabSelected = onTabSelected)
        if (statsUIState.activeTab == StatsTab.Leaders) {
            LeadersAgainstPane(
                opponents = statsUIState.leadersPlayerAgainst,
                modifier = Modifier.fillMaxSize()
            )
        } else {
            OpponentsPane(
                opponentStats = statsUIState.opponentStats,
                onEditOpponent = onEditOpponent,
                modifier = Modifier.fillMaxSize()
            )
        }
    }
}

@Composable
private fun LeadersAgainstPane(opponents: List<StatLeaderInfo>, modifier: Modifier = Modifier) {
    LazyColumn(
        modifier = modifier,
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        item {
            Text(
                text = stringResource(R.string.stats_leaders_against),
                style = MaterialTheme.typography.titleSmall,
                modifier = Modifier.padding(bottom = 4.dp)
            )
        }
        items(opponents, key = { it.leader.cardId }) { LeaderAgainstRow(it) }
    }
}

@Composable
private fun OpponentsPane(
    opponentStats: List<StatOpponentInfo>,
    onEditOpponent: (Opponent) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier,
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        item {
            Text(
                text = stringResource(R.string.stats_opponents),
                style = MaterialTheme.typography.titleSmall,
                modifier = Modifier.padding(bottom = 4.dp)
            )
        }
        items(opponentStats, key = { it.opponent.id }) { OpponentStatRow(it, onEditOpponent = { onEditOpponent(it.opponent) }) }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun StatsHeader(
    statsUIState: StatsUIState,
    onLeaderSelected: (Leader) -> Unit,
    onAllLeadersSelected: () -> Unit,
    onMetaSelected: (Meta) -> Unit,
    onAllMetasSelected: () -> Unit,
    onLocationSelected: (Location) -> Unit,
    onAllLocationsSelected: () -> Unit
) {
    val leaderInfo = when (val sel = statsUIState.leaderSelected) {
        is LeaderSelectedOption.LeaderSelected -> sel.leader
        is LeaderSelectedOption.All -> {
            val wins = statsUIState.leadersPlayerAgainst.sumOf { it.wins }
            val losses = statsUIState.leadersPlayerAgainst.sumOf { it.losses }
            val total = wins + losses
            val fw = statsUIState.leadersPlayerAgainst.sumOf { it.firstWins }
            val fl = statsUIState.leadersPlayerAgainst.sumOf { it.firstLosses }
            val sw = statsUIState.leadersPlayerAgainst.sumOf { it.secondWins }
            val sl = statsUIState.leadersPlayerAgainst.sumOf { it.secondLosses }
            StatLeaderInfo(
                leader = Leader.UGLuffy,
                wins = wins,
                losses = losses,
                percentage = if (total > 0) wins * 100 / total else null,
                firstWins = fw,
                firstLosses = fl,
                firstPercentage = if (fw + fl > 0) fw * 100 / (fw + fl) else null,
                secondWins = sw,
                secondLosses = sl,
                secondPercentage = if (sw + sl > 0) sw * 100 / (sw + sl) else null
            )
        }
    }
    val isAllLeaders = statsUIState.leaderSelected is LeaderSelectedOption.All

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (!isAllLeaders) {
            val borderColors = if (leaderInfo.leader.leaderColors.size == 1) {
                listOf(leaderInfo.leader.leaderColors.first().color, Color.White, leaderInfo.leader.leaderColors.first().color)
            } else {
                leaderInfo.leader.leaderColors.map { it.color }
            }
            Box(modifier = Modifier
                .weight(0.4f)
                .animatedBorder(colors = borderColors)
                .background(MaterialTheme.colorScheme.surface)
            ) {
                AsyncImage(
                    modifier = Modifier.fillMaxWidth(),
                    model = "file:///android_asset/leader_images/${leaderInfo.leader.cardId}.webp",
                    contentDescription = leaderInfo.leader.name
                )
            }
        } else {
            val allColors = listOf(
                LeaderColor.Red.color, LeaderColor.Blue.color, LeaderColor.Green.color,
                LeaderColor.Yellow.color, LeaderColor.Purple.color, LeaderColor.Black.color
            )
            Box(modifier = Modifier
                .weight(0.4f)
                .animatedBorder(colors = allColors)
                .background(MaterialTheme.colorScheme.surface)
            ) {
                AsyncImage(
                    modifier = Modifier.fillMaxWidth(),
                    model = "file:///android_asset/leader_images/rainbow_luffy.webp",
                    contentDescription = leaderInfo.leader.name
                )
            }
        }

        Column(
            modifier = Modifier.weight(0.6f),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = when (val pct = leaderInfo.percentage) {
                    null -> stringResource(R.string.wins_losses_format, leaderInfo.wins, leaderInfo.losses)
                    else -> stringResource(R.string.wins_losses_pct_format, leaderInfo.wins, leaderInfo.losses, pct)
                },
                style = MaterialTheme.typography.bodyMedium
            )
            Text(
                text = when (val pct = leaderInfo.firstPercentage) {
                    null -> stringResource(R.string.stats_going_first_no_pct, leaderInfo.firstWins, leaderInfo.firstLosses)
                    else -> stringResource(R.string.stats_going_first, leaderInfo.firstWins, leaderInfo.firstLosses, pct)
                },
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = when (val pct = leaderInfo.secondPercentage) {
                    null -> stringResource(R.string.stats_going_second_no_pct, leaderInfo.secondWins, leaderInfo.secondLosses)
                    else -> stringResource(R.string.stats_going_second, leaderInfo.secondWins, leaderInfo.secondLosses, pct)
                },
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            LeaderDropdown(
                leadersPlayed = statsUIState.leadersPlayed,
                leaderSelected = statsUIState.leaderSelected,
                onLeaderSelected = onLeaderSelected,
                onAllLeadersSelected = onAllLeadersSelected
            )
            MetaFilterDropdown(
                metaSelected = statsUIState.metaSelected,
                onMetaSelected = onMetaSelected,
                onAllMetasSelected = onAllMetasSelected
            )
            LocationFilterDropdown(
                availableLocations = statsUIState.availableLocations,
                locationSelected = statsUIState.locationSelected,
                onLocationSelected = onLocationSelected,
                onAllLocationsSelected = onAllLocationsSelected
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun LeaderDropdown(
    leadersPlayed: List<Leader>,
    leaderSelected: LeaderSelectedOption,
    onLeaderSelected: (Leader) -> Unit,
    onAllLeadersSelected: () -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    val displayName = when (leaderSelected) {
        is LeaderSelectedOption.All -> stringResource(R.string.stats_all_leaders)
        is LeaderSelectedOption.LeaderSelected -> leaderSelected.leader.leader.name
    }
    ExposedDropdownMenuBox(expanded = expanded, onExpandedChange = { expanded = it }) {
        OutlinedTextField(
            value = displayName,
            onValueChange = {},
            readOnly = true,
            label = { Text(stringResource(R.string.stats_label_leader)) },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
            modifier = Modifier
                .menuAnchor(ExposedDropdownMenuAnchorType.PrimaryNotEditable)
                .fillMaxWidth()
        )
        ExposedDropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
            DropdownMenuItem(
                text = { Text(stringResource(R.string.stats_all_leaders)) },
                onClick = { onAllLeadersSelected(); expanded = false },
                contentPadding = ExposedDropdownMenuDefaults.ItemContentPadding
            )
            leadersPlayed.forEach { leader ->
                DropdownMenuItem(
                    text = { Text(leader.name) },
                    onClick = { onLeaderSelected(leader); expanded = false },
                    contentPadding = ExposedDropdownMenuDefaults.ItemContentPadding
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun MetaFilterDropdown(
    metaSelected: MetaOption,
    onMetaSelected: (Meta) -> Unit,
    onAllMetasSelected: () -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    val displayName = when (metaSelected) {
        is MetaOption.All -> stringResource(R.string.stats_all_metas)
        is MetaOption.SpecificMeta -> metaSelected.meta.name
    }
    ExposedDropdownMenuBox(expanded = expanded, onExpandedChange = { expanded = it }) {
        OutlinedTextField(
            value = displayName,
            onValueChange = {},
            readOnly = true,
            label = { Text(stringResource(R.string.label_meta)) },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
            modifier = Modifier
                .menuAnchor(ExposedDropdownMenuAnchorType.PrimaryNotEditable)
                .fillMaxWidth()
        )
        ExposedDropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
            DropdownMenuItem(
                text = { Text(stringResource(R.string.stats_all_metas)) },
                onClick = { onAllMetasSelected(); expanded = false },
                contentPadding = ExposedDropdownMenuDefaults.ItemContentPadding
            )
            METAS_LIST.forEach { meta ->
                DropdownMenuItem(
                    text = { Text(meta.name) },
                    onClick = { onMetaSelected(meta); expanded = false },
                    contentPadding = ExposedDropdownMenuDefaults.ItemContentPadding
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun LocationFilterDropdown(
    availableLocations: List<Location>,
    locationSelected: LocationOption,
    onLocationSelected: (Location) -> Unit,
    onAllLocationsSelected: () -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    val displayName = when (locationSelected) {
        is LocationOption.All -> stringResource(R.string.stats_all_locations)
        is LocationOption.SpecificLocation -> locationSelected.location.abbreviation
    }
    ExposedDropdownMenuBox(expanded = expanded, onExpandedChange = { expanded = it }) {
        OutlinedTextField(
            value = displayName,
            onValueChange = {},
            readOnly = true,
            label = { Text(stringResource(R.string.label_location)) },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
            modifier = Modifier
                .menuAnchor(ExposedDropdownMenuAnchorType.PrimaryNotEditable)
                .fillMaxWidth()
        )
        ExposedDropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
            DropdownMenuItem(
                text = { Text(stringResource(R.string.stats_all_locations)) },
                onClick = { onAllLocationsSelected(); expanded = false },
                contentPadding = ExposedDropdownMenuDefaults.ItemContentPadding
            )
            availableLocations.forEach { location ->
                DropdownMenuItem(
                    text = { Text(location.name) },
                    onClick = { onLocationSelected(location); expanded = false },
                    contentPadding = ExposedDropdownMenuDefaults.ItemContentPadding
                )
            }
        }
    }
}

@Composable
private fun LeaderAgainstRow(opponent: StatLeaderInfo) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        LeaderThumbnail(leader = opponent.leader)
        Spacer(modifier = Modifier.width(12.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(text = opponent.leader.name, style = MaterialTheme.typography.bodyMedium)
            Text(
                text = when (val pct = opponent.percentage) {
                    null -> stringResource(R.string.wins_losses_format, opponent.wins, opponent.losses)
                    else -> stringResource(R.string.wins_losses_pct_format, opponent.wins, opponent.losses, pct)
                },
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = when (val pct = opponent.firstPercentage) {
                    null -> stringResource(R.string.stats_going_first_no_pct, opponent.firstWins, opponent.firstLosses)
                    else -> stringResource(R.string.stats_going_first, opponent.firstWins, opponent.firstLosses, pct)
                },
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = when (val pct = opponent.secondPercentage) {
                    null -> stringResource(R.string.stats_going_second_no_pct, opponent.secondWins, opponent.secondLosses)
                    else -> stringResource(R.string.stats_going_second, opponent.secondWins, opponent.secondLosses, pct)
                },
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun OpponentStatRow(stat: StatOpponentInfo, onEditOpponent: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(text = "${stat.opponent.nickname} (${stat.opponent.name})", style = MaterialTheme.typography.bodyMedium)
            Text(
                text = when (val pct = stat.percentage) {
                    null -> stringResource(R.string.wins_losses_format, stat.wins, stat.losses)
                    else -> stringResource(R.string.wins_losses_pct_format, stat.wins, stat.losses, pct)
                },
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        IconButton(onClick = onEditOpponent) {
            Icon(Icons.Filled.Edit, contentDescription = stringResource(R.string.cd_edit_opponent))
        }
    }
}

private val previewOpponents = listOf(
    StatLeaderInfo(Leader.RShanks, wins = 2, losses = 0, percentage = 100,
        firstWins = 1, firstLosses = 0, firstPercentage = 100,
        secondWins = 1, secondLosses = 0, secondPercentage = 100),
    StatLeaderInfo(Leader.GZoro, wins = 1, losses = 1, percentage = 50,
        firstWins = 0, firstLosses = 1, firstPercentage = 0,
        secondWins = 1, secondLosses = 0, secondPercentage = 100),
    StatLeaderInfo(Leader.PBLuffy, wins = 0, losses = 1, percentage = 0,
        firstWins = 0, firstLosses = 1, firstPercentage = 0,
        secondWins = 0, secondLosses = 0, secondPercentage = null)
)

private val previewOpponentStats = listOf(
    StatOpponentInfo(Opponent(1, "scooterd22", "scooter"), wins = 3, losses = 1, percentage = 75),
    StatOpponentInfo(Opponent(2, "derpinic", "Dom"), wins = 2, losses = 2, percentage = 50)
)

@Preview(showBackground = true)
@Composable
private fun PreviewStatsScreenLeaderSelected() {
    LabophaseTheme {
        StatsScreen(
            statsUIState = StatsUIState(
                leaderSelected = LeaderSelectedOption.LeaderSelected(
                    StatLeaderInfo(Leader.PBLuffy, wins = 3, losses = 1, percentage = 75,
                        firstWins = 2, firstLosses = 0, firstPercentage = 100,
                        secondWins = 1, secondLosses = 1, secondPercentage = 50)
                ),
                metaSelected = MetaOption.All,
                locationSelected = LocationOption.All,
                availableLocations = emptyList(),
                leadersPlayed = listOf(Leader.PBLuffy, Leader.RShanks, Leader.GZoro),
                leadersPlayerAgainst = previewOpponents,
                opponentStats = previewOpponentStats,
                isLoading = false
            ),
            onLeaderSelected = {},
            onAllLeadersSelected = {},
            onMetaSelected = {},
            onAllMetasSelected = {},
            onLocationSelected = {},
            onAllLocationsSelected = {},
            onTabSelected = {},
            onAddOpponent = { _, _ -> },
            onUpdateOpponent = { _, _, _ -> },
            onDeleteOpponent = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun PreviewStatsScreenOpponentsTab() {
    LabophaseTheme {
        StatsScreen(
            statsUIState = StatsUIState(
                leaderSelected = LeaderSelectedOption.All,
                metaSelected = MetaOption.All,
                locationSelected = LocationOption.All,
                availableLocations = emptyList(),
                leadersPlayed = listOf(Leader.PBLuffy, Leader.RShanks, Leader.GZoro),
                leadersPlayerAgainst = previewOpponents,
                opponentStats = previewOpponentStats,
                activeTab = StatsTab.Opponents,
                isLoading = false
            ),
            onLeaderSelected = {},
            onAllLeadersSelected = {},
            onMetaSelected = {},
            onAllMetasSelected = {},
            onLocationSelected = {},
            onAllLocationsSelected = {},
            onTabSelected = {},
            onAddOpponent = { _, _ -> },
            onUpdateOpponent = { _, _, _ -> },
            onDeleteOpponent = {}
        )
    }
}

@Preview(showBackground = true, widthDp = 840, heightDp = 600)
@Composable
private fun PreviewStatsScreenTabletLeaderSelected() {
    LabophaseTheme {
        StatsScreen(
            statsUIState = StatsUIState(
                leaderSelected = LeaderSelectedOption.LeaderSelected(
                    StatLeaderInfo(Leader.PBLuffy, wins = 3, losses = 1, percentage = 75,
                        firstWins = 2, firstLosses = 0, firstPercentage = 100,
                        secondWins = 1, secondLosses = 1, secondPercentage = 50)
                ),
                metaSelected = MetaOption.All,
                locationSelected = LocationOption.All,
                availableLocations = emptyList(),
                leadersPlayed = listOf(Leader.PBLuffy, Leader.RShanks, Leader.GZoro),
                leadersPlayerAgainst = previewOpponents,
                opponentStats = previewOpponentStats,
                isLoading = false
            ),
            onLeaderSelected = {},
            onAllLeadersSelected = {},
            onMetaSelected = {},
            onAllMetasSelected = {},
            onLocationSelected = {},
            onAllLocationsSelected = {},
            onTabSelected = {},
            onAddOpponent = { _, _ -> },
            onUpdateOpponent = { _, _, _ -> },
            onDeleteOpponent = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun PreviewStatsScreenLoading() {
    LabophaseTheme {
        StatsScreen(
            statsUIState = StatsUIState(
                leaderSelected = LeaderSelectedOption.All,
                metaSelected = MetaOption.All,
                locationSelected = LocationOption.All,
                availableLocations = emptyList(),
                leadersPlayed = emptyList(),
                leadersPlayerAgainst = emptyList(),
                isLoading = true
            ),
            onLeaderSelected = {},
            onAllLeadersSelected = {},
            onMetaSelected = {},
            onAllMetasSelected = {},
            onLocationSelected = {},
            onAllLocationsSelected = {},
            onTabSelected = {},
            onAddOpponent = { _, _ -> },
            onUpdateOpponent = { _, _, _ -> },
            onDeleteOpponent = {}
        )
    }
}
