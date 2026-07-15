package commanderpepper.labophase.screens.roundentry

import android.content.ClipData
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateFloat
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.DeleteForever
import androidx.compose.material.icons.filled.Expand
import androidx.compose.material3.Button
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import commanderpepper.labophase.models.Round
import commanderpepper.labophase.models.RoundResult
import commanderpepper.labophase.models.TurnOrder
import commanderpepper.labophase.ui.theme.LabophaseTheme
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel
import org.koin.core.parameter.parametersOf

@Composable
fun RoundEntryScreen(
    modifier: Modifier = Modifier,
    entryId: Int = -1,
    roundEntryViewModel: RoundEntryViewModel = koinViewModel<RoundEntryViewModelImpl>(
        parameters = { parametersOf(entryId) }
    )
) {
    val playerLeaderList = roundEntryViewModel.playerLeaderList.collectAsState()
    val roundLeaderList = roundEntryViewModel.roundLeaderList.collectAsState()
    val leaderSelected = roundEntryViewModel.leaderSelected.collectAsState()
    val rounds = roundEntryViewModel.rounds.collectAsState()
    val punkRecordEntry = roundEntryViewModel.punkRecordEntry.collectAsState()
    RoundEntryScreen(
        modifier = modifier,
        leaderSelectExpanded = entryId == -1,
        leaderSelected = leaderSelected.value,
        playerLeaderList = playerLeaderList.value,
        roundLeaderList = roundLeaderList.value,
        rounds = rounds.value,
        punkRecordEntry = punkRecordEntry.value,
        addNewRound = roundEntryViewModel::addNewRound,
        transformEntry = roundEntryViewModel::transformEntry,
        chooseLeader = roundEntryViewModel::chooseLeader,
        chooseRoundLeader = roundEntryViewModel::roundLeaderSelect,
        chooseRoundTurnOrder = roundEntryViewModel::roundTurnOrderSelect,
        chooseRoundResult = roundEntryViewModel::roundResultSelect,
        removeRound = roundEntryViewModel::removeRound
    )
}

@Composable
fun RoundEntryScreen(
    modifier: Modifier = Modifier,
    leaderSelectExpanded: Boolean = true,
    leaderSelected: Leader,
    rounds: List<Round>,
    playerLeaderList: List<Leader>,
    roundLeaderList: List<Leader>,
    punkRecordEntry: String,
    addNewRound: () -> Unit,
    transformEntry: () -> Unit,
    chooseLeader: (Leader) -> Unit,
    chooseRoundLeader: (Int, Leader) -> Unit,
    chooseRoundTurnOrder: (Int, TurnOrder) -> Unit,
    chooseRoundResult: (Int, RoundResult) -> Unit,
    removeRound: (Int) -> Unit
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        LeaderPlayerInTournamentSelection(leaderSelected = leaderSelected, leaders = playerLeaderList, onLeaderSelected = chooseLeader, initiallyExpanded = leaderSelectExpanded)
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Button(onClick = { transformEntry() }) { Text("Make a punk record entry") }
            Button(onClick = { addNewRound() }) { Text("New round") }
        }
        if (punkRecordEntry.isNotEmpty()) {
            CopyableResult(punkRecordEntry)
        }
        if (rounds.isNotEmpty()) {
            LazyColumn() {
                items(rounds) { round ->
                    RoundEntry(
                        round = round,
                        leaders = roundLeaderList,
                        initiallyExpanded = leaderSelectExpanded,
                        leaderSelected = chooseRoundLeader,
                        roundResult = chooseRoundResult,
                        turnOrder = chooseRoundTurnOrder,
                        removeRound = removeRound
                    )
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
fun LeaderPlayerInTournamentSelection(leaderSelected: Leader, leaders: List<Leader>, onLeaderSelected: (Leader) -> Unit, initiallyExpanded: Boolean = true) {
    val isExpanded = rememberSaveable { mutableStateOf(initiallyExpanded) }
    val carouselState = rememberSaveable(leaders.size, saver = CarouselState.Saver) { CarouselState { leaders.count() } }
    Column(modifier = Modifier.fillMaxWidth()) {
        Row(modifier = Modifier.fillMaxWidth().clickable(onClick = { isExpanded.value = !isExpanded.value }), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
            LeaderThumbnail(leader = leaderSelected)
            Text(text = leaderSelected.name)
            IconButton(onClick = { isExpanded.value = !isExpanded.value }) { Icon(Icons.Default.Expand, contentDescription = "Expand / Close") }
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

@Composable
fun RoundResultSelection(roundResult: RoundResult, onRoundSelected: (RoundResult) -> Unit) {
    Row {
        Button(onClick = { onRoundSelected(RoundResult.Win) }) { Text("W") }
        Button(onClick = { onRoundSelected(RoundResult.Loss) }) { Text("L") }
    }
}

@Composable
fun TurnOrderSelection(turnOrder: TurnOrder, onTurnOrderSelected: (TurnOrder) -> Unit) {
    Row {
        Button(onClick = { onTurnOrderSelected(TurnOrder.First) }) { Text("1st") }
        Button(onClick = { onTurnOrderSelected(TurnOrder.Second) }) { Text("2nd") }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RoundEntry(
    modifier: Modifier = Modifier,
    round: Round,
    leaders: List<Leader>,
    removeRound: (Int) -> Unit,
    leaderSelected: (Int, Leader) -> Unit,
    roundResult: (Int, RoundResult) -> Unit,
    turnOrder: (Int, TurnOrder) -> Unit,
    initiallyExpanded: Boolean = true
) {
    val isExpanded = rememberSaveable { mutableStateOf(initiallyExpanded) }
    val carouselState = rememberSaveable(leaders.size, saver = CarouselState.Saver) { CarouselState { leaders.count() } }
    Column(modifier) {
        Row(modifier = Modifier.fillMaxWidth().clickable(onClick = { isExpanded.value = !isExpanded.value }), verticalAlignment = Alignment.CenterVertically) {
            LeaderThumbnail(leader = round.leader)
            Text(round.singleLine(), modifier = Modifier.weight(1f))
            IconButton(onClick = { isExpanded.value = !isExpanded.value }) { Icon(Icons.Default.Expand, contentDescription = "Expand / Close") }
            IconButton(onClick = { removeRound(round.roundId) }) { Icon(Icons.Default.DeleteForever, contentDescription = "Delete") }
        }
        AnimatedVisibility(visible = isExpanded.value) {
            Column(modifier = Modifier.fillMaxWidth()) {
                LeaderSelection(leaders = leaders, state = carouselState) { leader ->
                    leaderSelected(round.roundId, leader)
                }
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    RoundResultSelection(round.roundResult) { result ->
                        roundResult(round.roundId, result)
                    }
                    TurnOrderSelection(round.turnOrder) { turnOrder ->
                        turnOrder(round.roundId, turnOrder)
                    }
                }
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
private val previewRound1 = Round(roundId = 1, roundNumber = 1)
private val previewRound2 = Round(roundId = 2, roundNumber = 2, roundResult = RoundResult.Loss, turnOrder = TurnOrder.Second)
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
            leaderSelected = Leader.PBLuffy,
            rounds = listOf(previewRound1, previewRound2),
            playerLeaderList = previewLeaders,
            roundLeaderList = previewLeaders,
            punkRecordEntry = previewPunkRecord,
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
            leaderSelected = Leader.PBLuffy,
            rounds = emptyList(),
            playerLeaderList = previewLeaders,
            roundLeaderList = previewLeaders,
            punkRecordEntry = "",
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
        RoundResultSelection(roundResult = RoundResult.Win, onRoundSelected = {})
    }
}

@Preview(showBackground = true)
@Composable
private fun PreviewTurnOrderSelection() {
    LabophaseTheme {
        TurnOrderSelection(turnOrder = TurnOrder.First, onTurnOrderSelected = {})
    }
}

@Preview(showBackground = true)
@Composable
private fun PreviewCopyableResult() {
    LabophaseTheme {
        CopyableResult(text = previewPunkRecord)
    }
}
