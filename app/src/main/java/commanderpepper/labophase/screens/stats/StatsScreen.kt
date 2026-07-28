package commanderpepper.labophase.screens.stats

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuAnchorType
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
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
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil3.compose.AsyncImage
import commanderpepper.labophase.R
import commanderpepper.labophase.models.Leader
import commanderpepper.labophase.models.LeaderColor
import commanderpepper.labophase.models.Location
import commanderpepper.labophase.models.LOCATIONS_LIST
import commanderpepper.labophase.models.METAS_LIST
import commanderpepper.labophase.models.Meta
import commanderpepper.labophase.screens.roundentry.LeaderThumbnail
import commanderpepper.labophase.screens.roundentry.animatedBorder
import commanderpepper.labophase.screens.stats.models.LeaderSelectedOption
import commanderpepper.labophase.screens.stats.models.LocationOption
import commanderpepper.labophase.screens.stats.models.MetaOption
import commanderpepper.labophase.screens.stats.models.StatLeaderInfo
import commanderpepper.labophase.screens.stats.models.StatsUIState
import commanderpepper.labophase.ui.theme.LabophaseTheme
import org.koin.androidx.compose.koinViewModel

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
        onAllLocationsSelected = statsViewModel::allLocationSelected
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StatsScreen(
    statsUIState: StatsUIState,
    onLeaderSelected: (Leader) -> Unit,
    onAllLeadersSelected: () -> Unit,
    onMetaSelected: (Meta) -> Unit,
    onAllMetasSelected: () -> Unit,
    onLocationSelected: (Location) -> Unit,
    onAllLocationsSelected: () -> Unit
) {
    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(title = { Text(stringResource(R.string.stats_title)) })
        }
    ) { innerPadding ->
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
                    HorizontalDivider(modifier = Modifier.padding(vertical = 4.dp))
                    Text(
                        text = stringResource(R.string.stats_leaders_against),
                        style = MaterialTheme.typography.titleSmall,
                        modifier = Modifier.padding(bottom = 4.dp)
                    )
                }
                items(statsUIState.leadersPlayerAgainst) { opponent ->
                    OpponentRow(opponent = opponent)
                }
            }
        }
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
                percentage = if (total > 0) "${wins * 100 / total}%" else "N/A",
                firstWins = fw,
                firstLosses = fl,
                firstPercentage = if (fw + fl > 0) "${fw * 100 / (fw + fl)}%" else "N/A",
                secondWins = sw,
                secondLosses = sl,
                secondPercentage = if (sw + sl > 0) "${sw * 100 / (sw + sl)}%" else "N/A"
            )
        }
    }
    val isAllLeaders = statsUIState.leaderSelected is LeaderSelectedOption.All

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Left: leader card or "All" placeholder
        if (!isAllLeaders) {
            val borderColors = if (leaderInfo.leader.leaderColors.size == 1) {
                listOf(leaderInfo.leader.leaderColors.first().color, Color.White, leaderInfo.leader.leaderColors.first().color)
            } else {
                leaderInfo.leader.leaderColors.map { it.color }
            }
            ElevatedCard(modifier = Modifier.weight(0.4f).animatedBorder(colors = borderColors)) {
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
            ElevatedCard(modifier = Modifier.weight(0.4f).animatedBorder(colors = allColors)) {
                AsyncImage(
                    modifier = Modifier.fillMaxWidth(),
                    model = "file:///android_asset/leader_images/rainbow_luffy.webp",
                    contentDescription = leaderInfo.leader.name
                )
            }
        }

        // Right: W/L%, filters
        Column(
            modifier = Modifier.weight(0.6f),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = stringResource(
                    R.string.wins_losses_pct_format,
                    leaderInfo.wins,
                    leaderInfo.losses,
                    leaderInfo.percentage.dropLast(1).toIntOrNull() ?: 0
                ),
                style = MaterialTheme.typography.bodyMedium
            )
            Text(
                text = stringResource(
                    R.string.stats_going_first,
                    leaderInfo.firstWins,
                    leaderInfo.firstLosses,
                    leaderInfo.firstPercentage.dropLast(1).toIntOrNull() ?: 0
                ),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = stringResource(
                    R.string.stats_going_second,
                    leaderInfo.secondWins,
                    leaderInfo.secondLosses,
                    leaderInfo.secondPercentage.dropLast(1).toIntOrNull() ?: 0
                ),
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
    locationSelected: LocationOption,
    onLocationSelected: (Location) -> Unit,
    onAllLocationsSelected: () -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    val displayName = when (locationSelected) {
        is LocationOption.All -> stringResource(R.string.stats_all_locations)
        is LocationOption.SpecificLocation -> locationSelected.location.name
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
            LOCATIONS_LIST.forEach { location ->
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
private fun OpponentRow(opponent: StatLeaderInfo) {
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
                text = stringResource(R.string.wins_losses_pct_format, opponent.wins, opponent.losses,
                    opponent.percentage.dropLast(1).toIntOrNull() ?: 0),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = stringResource(R.string.stats_going_first, opponent.firstWins, opponent.firstLosses,
                    opponent.firstPercentage.dropLast(1).toIntOrNull() ?: 0),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = stringResource(R.string.stats_going_second, opponent.secondWins, opponent.secondLosses,
                    opponent.secondPercentage.dropLast(1).toIntOrNull() ?: 0),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

private val previewOpponents = listOf(
    StatLeaderInfo(Leader.RShanks, wins = 2, losses = 0, percentage = "100%",
        firstWins = 1, firstLosses = 0, firstPercentage = "100%",
        secondWins = 1, secondLosses = 0, secondPercentage = "100%"),
    StatLeaderInfo(Leader.GZoro, wins = 1, losses = 1, percentage = "50%",
        firstWins = 0, firstLosses = 1, firstPercentage = "0%",
        secondWins = 1, secondLosses = 0, secondPercentage = "100%"),
    StatLeaderInfo(Leader.PBLuffy, wins = 0, losses = 1, percentage = "0%",
        firstWins = 0, firstLosses = 1, firstPercentage = "0%",
        secondWins = 0, secondLosses = 0, secondPercentage = "N/A")
)

@Preview(showBackground = true)
@Composable
private fun PreviewStatsScreenLeaderSelected() {
    LabophaseTheme {
        StatsScreen(
            statsUIState = StatsUIState(
                leaderSelected = LeaderSelectedOption.LeaderSelected(
                    StatLeaderInfo(Leader.PBLuffy, wins = 3, losses = 1, percentage = "75%",
                        firstWins = 2, firstLosses = 0, firstPercentage = "100%",
                        secondWins = 1, secondLosses = 1, secondPercentage = "50%")
                ),
                metaSelected = MetaOption.All,
                locationSelected = LocationOption.All,
                leadersPlayed = listOf(Leader.PBLuffy, Leader.RShanks, Leader.GZoro),
                leadersPlayerAgainst = previewOpponents,
                isLoading = false
            ),
            onLeaderSelected = {},
            onAllLeadersSelected = {},
            onMetaSelected = {},
            onAllMetasSelected = {},
            onLocationSelected = {},
            onAllLocationsSelected = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun PreviewStatsScreenAllLeaders() {
    LabophaseTheme {
        StatsScreen(
            statsUIState = StatsUIState(
                leaderSelected = LeaderSelectedOption.All,
                metaSelected = MetaOption.SpecificMeta(Meta.OP16),
                locationSelected = LocationOption.All,
                leadersPlayed = listOf(Leader.PBLuffy, Leader.RShanks, Leader.GZoro),
                leadersPlayerAgainst = previewOpponents,
                isLoading = false
            ),
            onLeaderSelected = {},
            onAllLeadersSelected = {},
            onMetaSelected = {},
            onAllMetasSelected = {},
            onLocationSelected = {},
            onAllLocationsSelected = {}
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
                leadersPlayed = emptyList(),
                leadersPlayerAgainst = emptyList(),
                isLoading = true
            ),
            onLeaderSelected = {},
            onAllLeadersSelected = {},
            onMetaSelected = {},
            onAllMetasSelected = {},
            onLocationSelected = {},
            onAllLocationsSelected = {}
        )
    }
}
