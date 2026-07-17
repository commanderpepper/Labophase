package commanderpepper.labophase.screens.roundentry

import android.content.ClipData
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.DeleteForever
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material3.Button
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Text
import androidx.compose.material3.carousel.CarouselState
import androidx.compose.material3.carousel.HorizontalCenteredHeroCarousel
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.platform.ClipEntry
import androidx.compose.ui.platform.LocalClipboard
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import commanderpepper.labophase.models.Leader
import commanderpepper.labophase.screens.roundentry.models.RoundUI
import commanderpepper.labophase.screens.settings.SettingsViewModel
import commanderpepper.labophase.screens.settings.SettingsViewModelImpl
import commanderpepper.labophase.ui.theme.LabophaseTheme
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel
import org.koin.core.parameter.parametersOf

@Composable
fun RoundEntryScreen(
    modifier: Modifier = Modifier,
    onBack: () -> Unit = {},
    entryId: Int = -1,
    roundEntryViewModel: RoundEntryViewModel = koinViewModel<RoundEntryViewModelImpl>(
        parameters = { parametersOf(entryId) }
    ),
    settingsViewModel: SettingsViewModel = koinViewModel<SettingsViewModelImpl>()
) {
    val playerLeaderList = roundEntryViewModel.playerLeaderList.collectAsState()
    val roundLeaderList = roundEntryViewModel.roundLeaderList.collectAsState()
    val leaderSelected = roundEntryViewModel.leaderSelected.collectAsState()
    val rounds = roundEntryViewModel.rounds.collectAsState()
    val punkRecordEntry = roundEntryViewModel.punkRecordEntry.collectAsState()
    val showingDieRoll = settingsViewModel.showingDieRoll.collectAsState()
    RoundEntryScreen(
        modifier = modifier,
        onBack = onBack,
        leaderSelectExpanded = entryId == -1,
        leaderSelected = leaderSelected.value,
        playerLeaderList = playerLeaderList.value,
        roundLeaderList = roundLeaderList.value,
        rounds = rounds.value,
        punkRecordEntry = punkRecordEntry.value,
        showingDieRoll = showingDieRoll.value,
        addNewRound = roundEntryViewModel::addNewRound,
        transformEntry = roundEntryViewModel::transformEntry,
        chooseLeader = roundEntryViewModel::chooseLeader,
        chooseRoundLeader = roundEntryViewModel::roundLeaderSelect,
        chooseRoundTurnOrder = roundEntryViewModel::roundTurnOrderSelect,
        chooseRoundResult = roundEntryViewModel::roundResultSelect,
        chooseDieRoll = roundEntryViewModel::roundDieRollSelect,
        removeRound = roundEntryViewModel::removeRound
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RoundEntryScreen(
    modifier: Modifier = Modifier,
    onBack: () -> Unit = {},
    leaderSelectExpanded: Boolean = true,
    leaderSelected: Leader,
    rounds: List<RoundUI>,
    playerLeaderList: List<Leader>,
    roundLeaderList: List<Leader>,
    punkRecordEntry: String,
    showingDieRoll: Boolean = false,
    addNewRound: () -> Unit,
    transformEntry: () -> Unit,
    chooseLeader: (Leader) -> Unit,
    chooseRoundLeader: (Int, Leader) -> Unit,
    chooseRoundTurnOrder: (Int, String) -> Unit,
    chooseRoundResult: (Int, String) -> Unit,
    chooseDieRoll: (Int, String?) -> Unit = { _, _ -> },
    removeRound: (Int) -> Unit
) {
    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text(leaderSelected.name) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = modifier
                .padding(innerPadding)
                .padding(horizontal = 16.dp)
                .fillMaxSize()
        ) {
            LeaderPlayerInTournamentSelection(leaderSelected = leaderSelected, leaders = playerLeaderList, rounds = rounds, onLeaderSelected = chooseLeader, initiallyExpanded = leaderSelectExpanded)
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Button(onClick = { transformEntry() }) { Text("Save punk record entry") }
                OutlinedButton(onClick = { addNewRound() }) { Text("New round") }
            }
            if (punkRecordEntry.isNotEmpty()) {
                val punkRecordVisible = rememberSaveable { mutableStateOf(true) }
                val punkRecordRotation by animateFloatAsState(
                    targetValue = if (punkRecordVisible.value) 180f else 0f,
                    label = "punk_record_rotation"
                )
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { punkRecordVisible.value = !punkRecordVisible.value },
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Punk Record", modifier = Modifier.weight(1f))
                    Icon(
                        Icons.Default.ExpandMore,
                        contentDescription = if (punkRecordVisible.value) "Hide" else "Show",
                        modifier = Modifier.rotate(punkRecordRotation)
                    )
                }
                AnimatedVisibility(visible = punkRecordVisible.value) {
                    CopyableResult(punkRecordEntry)
                }
            }
            if (rounds.isNotEmpty()) {
                LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(rounds) { round ->
                        RoundEntry(
                            round = round,
                            leaders = roundLeaderList,
                            initiallyExpanded = leaderSelectExpanded,
                            leaderSelected = chooseRoundLeader,
                            roundResult = chooseRoundResult,
                            turnOrder = chooseRoundTurnOrder,
                            dieRoll = chooseDieRoll,
                            showingDieRoll = showingDieRoll,
                            removeRound = removeRound
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun Modifier.animatedBorder(
    colors: List<Color>,
    shape: Shape = CardDefaults.elevatedShape,
    borderWidth: Dp = 2.dp,
    durationMs: Int = 2500
): Modifier {
    val infiniteTransition = rememberInfiniteTransition(label = "border")
    val angle by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = durationMs, easing = LinearEasing)
        ),
        label = "angle"
    )
    return this
        .clip(shape)
        .drawWithContent {
            rotate(degrees = angle) {
                drawCircle(
                    brush = Brush.sweepGradient(colors),
                    radius = size.maxDimension
                )
            }
            drawContent()
        }
        .padding(borderWidth)
        .clip(shape)
}

@Composable
fun LeaderThumbnail(leader: Leader) {
    val colors = if (leader.leaderColors.size == 1) {
        listOf(leader.leaderColors.first().color, Color.White, leader.leaderColors.first().color)
    } else {
        leader.leaderColors.map { it.color }
    }
    ElevatedCard(modifier = Modifier.animatedBorder(colors = colors)) {
        AsyncImage(
            modifier = Modifier.size(48.dp),
            model = "file:///android_asset/leader_thumbnails/${leader.cardId}.webp",
            contentDescription = leader.name
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LeaderPlayerInTournamentSelection(leaderSelected: Leader, leaders: List<Leader>, rounds: List<RoundUI>, onLeaderSelected: (Leader) -> Unit, initiallyExpanded: Boolean = true) {
    val isExpanded = rememberSaveable { mutableStateOf(initiallyExpanded) }
    val carouselState = rememberSaveable(leaders.size, saver = CarouselState.Saver) { CarouselState { leaders.count() } }
    val rotation by animateFloatAsState(
        targetValue = if (isExpanded.value) 180f else 0f,
        label = "leader_expand_rotation"
    )
    val wins = rounds.count { it.roundResult == "Win" }
    val losses = rounds.count { it.roundResult == "Loss" }
    Column(modifier = Modifier.fillMaxWidth()) {
        Row(modifier = Modifier.fillMaxWidth().clickable(onClick = { isExpanded.value = !isExpanded.value }), verticalAlignment = Alignment.CenterVertically) {
            LeaderThumbnail(leader = leaderSelected)
            Spacer(modifier = Modifier.width(8.dp))
            Text("W: $wins - L: $losses", modifier = Modifier.weight(1f))
            IconButton(onClick = { isExpanded.value = !isExpanded.value }) {
                Icon(
                    Icons.Default.ExpandMore,
                    contentDescription = if (isExpanded.value) "Collapse" else "Expand",
                    modifier = Modifier.rotate(rotation)
                )
            }
        }
        AnimatedVisibility(visible = isExpanded.value) {
            LeaderSelection(leaders = leaders, state = carouselState, onLeaderSelected = onLeaderSelected)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LeaderSelection(leaders: List<Leader>, state: CarouselState, onLeaderSelected: (Leader) -> Unit) {
    HorizontalCenteredHeroCarousel(state = state,
        maxItemWidth = 128.dp,
        itemSpacing = 8.dp,
        contentPadding = PaddingValues(horizontal = 16.dp)) { index ->
        val leader = leaders[index]
        ElevatedCard(modifier = Modifier.maskClip(shape = RoundedCornerShape(corner = CornerSize(2.dp)))) {
            AsyncImage(
                modifier = Modifier.clickable(onClick = { onLeaderSelected(leader) }),
                model = "file:///android_asset/leader_images/${leader.cardId}.webp",
                contentDescription = leader.name
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RoundResultSelection(roundResult: String, onRoundSelected: (String) -> Unit) {
    SingleChoiceSegmentedButtonRow {
        SegmentedButton(
            selected = roundResult == "Win",
            onClick = { onRoundSelected("Win") },
            shape = SegmentedButtonDefaults.itemShape(index = 0, count = 2)
        ) { Text("W") }
        SegmentedButton(
            selected = roundResult == "Loss",
            onClick = { onRoundSelected("Loss") },
            shape = SegmentedButtonDefaults.itemShape(index = 1, count = 2)
        ) { Text("L") }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TurnOrderSelection(turnOrder: String, onTurnOrderSelected: (String) -> Unit) {
    SingleChoiceSegmentedButtonRow {
        SegmentedButton(
            selected = turnOrder == "First",
            onClick = { onTurnOrderSelected("First") },
            shape = SegmentedButtonDefaults.itemShape(index = 0, count = 2)
        ) { Text("1st") }
        SegmentedButton(
            selected = turnOrder == "Second",
            onClick = { onTurnOrderSelected("Second") },
            shape = SegmentedButtonDefaults.itemShape(index = 1, count = 2)
        ) { Text("2nd") }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DieRollSelection(dieRoll: String?, onDieRollSelected: (String?) -> Unit) {
    SingleChoiceSegmentedButtonRow {
        SegmentedButton(
            selected = dieRoll == "Win",
            onClick = { onDieRollSelected(if (dieRoll == "Win") null else "Win") },
            shape = SegmentedButtonDefaults.itemShape(index = 0, count = 2)
        ) { Text("\uD83C\uDFB2") }
        SegmentedButton(
            selected = dieRoll == "Loss",
            onClick = { onDieRollSelected(if (dieRoll == "Loss") null else "Loss") },
            shape = SegmentedButtonDefaults.itemShape(index = 1, count = 2)
        ) { Text("❌") }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RoundEntry(
    modifier: Modifier = Modifier,
    round: RoundUI,
    leaders: List<Leader>,
    removeRound: (Int) -> Unit,
    leaderSelected: (Int, Leader) -> Unit,
    roundResult: (Int, String) -> Unit,
    turnOrder: (Int, String) -> Unit,
    dieRoll: (Int, String?) -> Unit = { _, _ -> },
    showingDieRoll: Boolean = false,
    initiallyExpanded: Boolean = true
) {
    val isExpanded = rememberSaveable { mutableStateOf(initiallyExpanded) }
    val carouselState = rememberSaveable(leaders.size, saver = CarouselState.Saver) { CarouselState { leaders.count() } }
    val rotation by animateFloatAsState(
        targetValue = if (isExpanded.value) 180f else 0f,
        label = "round_expand_rotation"
    )
    Column(modifier.padding(vertical = 4.dp)) {
        Row(modifier = Modifier.fillMaxWidth().clickable(onClick = { isExpanded.value = !isExpanded.value }), verticalAlignment = Alignment.CenterVertically) {
            LeaderThumbnail(leader = round.leader)
            Spacer(modifier = Modifier.width(8.dp))
            Text(round.summary, modifier = Modifier.weight(1f))
            IconButton(onClick = { isExpanded.value = !isExpanded.value }) {
                Icon(
                    Icons.Default.ExpandMore,
                    contentDescription = if (isExpanded.value) "Collapse" else "Expand",
                    modifier = Modifier.rotate(rotation)
                )
            }
            IconButton(onClick = { removeRound(round.roundId) }) { Icon(Icons.Default.DeleteForever, contentDescription = "Delete") }
        }
        AnimatedVisibility(visible = isExpanded.value) {
            Column(modifier = Modifier.fillMaxWidth()) {
                LeaderSelection(leaders = leaders, state = carouselState) { leader ->
                    leaderSelected(round.roundId, leader)
                }
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Start) {
                    RoundResultSelection(round.roundResult) { result ->
                        roundResult(round.roundId, result)
                    }
                    TurnOrderSelection(round.turnOrder) { order ->
                        turnOrder(round.roundId, order)
                    }
                    if (showingDieRoll) {
                        DieRollSelection(dieRoll = round.dieRoll) { result ->
                            dieRoll(round.roundId, result)
                        }
                    }
                }
//                if (showingDieRoll) {
//                    Row(
//                        modifier = Modifier.fillMaxWidth(),
//                        horizontalArrangement = Arrangement.SpaceBetween,
//                        verticalAlignment = Alignment.CenterVertically
//                    ) {
//                        Text("Die Roll")
//                        DieRollSelection(dieRoll = round.dieRoll) { result ->
//                            dieRoll(round.roundId, result)
//                        }
//                    }
//                }
            }
        }
    }
}

@Composable
fun CopyableResult(text: String) {
    val clipboard = LocalClipboard.current
    val scope = rememberCoroutineScope()

    Row(verticalAlignment = Alignment.CenterVertically) {
        Text(text = text, modifier = Modifier.weight(1f))
        IconButton(
            onClick = {
                scope.launch {
                    clipboard.setClipEntry(
                        ClipEntry(ClipData.newPlainText(null, text))
                    )
                }
            }
        ) {
            Icon(Icons.Default.ContentCopy, contentDescription = "Copy")
        }
    }
}

private val previewLeaders = listOf(Leader.PBLuffy, Leader.RShanks, Leader.GZoro)
private val previewRound1 = RoundUI(roundId = 1, leader = Leader.PBLuffy, summary = "PB Luffy, W, 1", roundResult = "Win", turnOrder = "First")
private val previewRound2 = RoundUI(roundId = 2, leader = Leader.RShanks, summary = "R Shanks, L, 2", roundResult = "Loss", turnOrder = "Second")
private val previewPunkRecord = "!PR add\n" +
        "UG Luffy\n" +
        "W G Bonney 2nd\n" +
        "W UY Nami 1st\n" +
        "W G Mihawk 1st\n" +
        "W UY Nami 1st"

@Preview(showBackground = true)
@Composable
private fun PreviewRoundEntryScreen() {
    LabophaseTheme {
        RoundEntryScreen(
            onBack = {},
            leaderSelected = Leader.PBLuffy,
            rounds = listOf(previewRound1, previewRound2),
            playerLeaderList = previewLeaders,
            roundLeaderList = previewLeaders,
            punkRecordEntry = previewPunkRecord,
            showingDieRoll = true,
            addNewRound = {},
            transformEntry = {},
            chooseLeader = {},
            chooseRoundLeader = { _, _ -> },
            chooseRoundTurnOrder = { _, _ -> },
            chooseRoundResult = { _, _ -> },
            removeRound = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun PreviewRoundEntryScreenEmpty() {
    LabophaseTheme {
        RoundEntryScreen(
            onBack = {},
            leaderSelected = Leader.PBLuffy,
            rounds = emptyList(),
            playerLeaderList = previewLeaders,
            roundLeaderList = previewLeaders,
            punkRecordEntry = "",
            showingDieRoll = true,
            addNewRound = {},
            transformEntry = {},
            chooseLeader = {},
            chooseRoundLeader = { _, _ -> },
            chooseRoundTurnOrder = { _, _ -> },
            chooseRoundResult = { _, _ -> },
            removeRound = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun PreviewLeaderPlayerInTournamentSelection() {
    LabophaseTheme {
        LeaderPlayerInTournamentSelection(
            leaderSelected = Leader.PBLuffy,
            leaders = previewLeaders,
            rounds = listOf(previewRound1, previewRound2),
            onLeaderSelected = {}
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Preview(showBackground = true)
@Composable
private fun PreviewLeaderSelection() {
    LabophaseTheme {
        val state = remember { CarouselState { previewLeaders.count() } }
        LeaderSelection(leaders = previewLeaders, state = state, onLeaderSelected = {})
    }
}

@Preview(showBackground = true)
@Composable
private fun PreviewRoundEntry() {
    LabophaseTheme {
        RoundEntry(
            round = previewRound1,
            leaders = previewLeaders,
            leaderSelected = { _, _ -> },
            roundResult = { _, _ -> },
            turnOrder = { _, _ -> },
            removeRound = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun PreviewRoundResultSelection() {
    LabophaseTheme {
        RoundResultSelection(roundResult = "Win", onRoundSelected = {})
    }
}

@Preview(showBackground = true)
@Composable
private fun PreviewTurnOrderSelection() {
    LabophaseTheme {
        TurnOrderSelection(turnOrder = "First", onTurnOrderSelected = {})
    }
}

@Preview(showBackground = true)
@Composable
private fun PreviewCopyableResult() {
    LabophaseTheme {
        CopyableResult(text = previewPunkRecord)
    }
}
