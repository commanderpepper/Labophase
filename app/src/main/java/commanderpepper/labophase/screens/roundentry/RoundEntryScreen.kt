package commanderpepper.labophase.screens.roundentry

import android.content.ClipData
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyHorizontalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.ClipEntry
import androidx.compose.ui.platform.LocalClipboard
import androidx.compose.ui.unit.dp
import commanderpepper.labophase.models.Leader
import commanderpepper.labophase.models.Round
import commanderpepper.labophase.models.RoundResult
import commanderpepper.labophase.models.TurnOrder
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel

@Composable
fun RoundEntryScreen(
    modifier: Modifier = Modifier,
    roundEntryViewModel: RoundEntryViewModel = koinViewModel<RoundEntryViewModelImpl>()
) {
    val leaders = roundEntryViewModel.leaders.collectAsState()
    val leaderSelected = roundEntryViewModel.leaderSelected.collectAsState()
    val rounds = roundEntryViewModel.rounds.collectAsState()
    val punkRecordEntry = roundEntryViewModel.punkRecordEntry.collectAsState()
    RoundEntryScreen(
        modifier = modifier,
        leaderSelected = leaderSelected.value,
        leaders = leaders.value,
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
    leaderSelected: Leader,
    rounds: List<Round>,
    leaders: List<Leader>,
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
        LeaderPlayerInTournamentSelection(leaderSelected = leaderSelected, leaders = leaders, onLeaderSelected = chooseLeader)
        Button(onClick = { transformEntry() }) { Text("Make a punk record entry") }
        Button(onClick = { addNewRound() }) { Text("Add a new round") }
        if (punkRecordEntry.isNotEmpty()) {
            CopyableResult(punkRecordEntry)
        }
        if (rounds.isNotEmpty()) {
            LazyColumn() {
                items(rounds) { round ->
                    RoundEntry(
                        round = round,
                        leaders = leaders,
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
fun LeaderPlayerInTournamentSelection(leaderSelected: Leader, leaders: List<Leader>, onLeaderSelected: (Leader) -> Unit) {
    val isExpanded = remember { mutableStateOf(true) }
    Column(modifier = Modifier.fillMaxWidth()) {
        Button(onClick = { isExpanded.value = !isExpanded.value }) { Text("Expand / Close") }
        if (isExpanded.value) {
            Text(text = "Your Leader: " + leaderSelected.name)
            LeaderSelection(leaders = leaders, onLeaderSelected = onLeaderSelected)
        }
        else {
            Text(text = "Your Leader: " + leaderSelected.name)
        }
    }
}

@Composable
fun LeaderSelection(leaders: List<Leader>, onLeaderSelected: (Leader) -> Unit) {
    Column(modifier = Modifier.fillMaxWidth()) {
        LazyHorizontalGrid(
            modifier = Modifier
                .fillMaxWidth()
                .height(96.dp), rows = GridCells.Fixed(2)
        ) {
            items(leaders) { leader ->
                TextButton(onClick = { onLeaderSelected(leader) }) { Text(text = leader.name) }
            }
        }
    }
}

@Composable
fun RoundSelection(roundResult: RoundResult, onRoundSelected: (RoundResult) -> Unit) {
    val isDub = roundResult == RoundResult.Win
    Column {
        Text(text = if (isDub) "You Won!" else "you lost...")
        Row {
            Button(onClick = { onRoundSelected(RoundResult.Win) }) { Text("WIN") }
            Button(onClick = { onRoundSelected(RoundResult.Loss) }) { Text("LOST") }
        }
    }
}

@Composable
fun TurnOrderSelection(turnOrder: TurnOrder, onTurnOrderSelected: (TurnOrder) -> Unit) {
    Column() {
        Text(if (turnOrder == TurnOrder.First) "First" else "Second")
        Row {
            Button(onClick = { onTurnOrderSelected(TurnOrder.First) }) { Text("FIRST") }
            Button(onClick = { onTurnOrderSelected(TurnOrder.Second) }) { Text("SECOND") }
        }
    }
}

@Composable
fun RoundEntry(
    modifier: Modifier = Modifier,
    round: Round,
    leaders: List<Leader>,
    removeRound: (Int) -> Unit,
    leaderSelected: (Int, Leader) -> Unit,
    roundResult: (Int, RoundResult) -> Unit,
    turnOrder: (Int, TurnOrder) -> Unit
) {
    val isExpanded = remember { mutableStateOf(true) }
    Column(modifier) {
        Row{
            Button(onClick = {isExpanded.value = !isExpanded.value}) { Text("Expand/Close") }
            Button(onClick = { removeRound(round.roundId) }) { Text("Remove Round") }
        }
        if (isExpanded.value) {
            Text("Opponent: " + round.leader.name)
            LeaderSelection(leaders = leaders) { leader ->
                leaderSelected(round.roundId, leader)
            }
            RoundSelection(round.roundResult) { result ->
                roundResult(round.roundId, result)
            }
            TurnOrderSelection(round.turnOrder) { turnOrder ->
                turnOrder(round.roundId, turnOrder)
            }
        }
        else {
            Text(round.singleLine())
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