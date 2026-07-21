package commanderpepper.labophase.logic.converter

import commanderpepper.labophase.data.EntryWithRounds
import commanderpepper.labophase.data.RoundEntity
import commanderpepper.labophase.models.Leader
import commanderpepper.labophase.models.Round
import commanderpepper.labophase.models.RoundResult
import commanderpepper.labophase.models.TurnOrder
import commanderpepper.labophase.models.leaderByCardId
import commanderpepper.labophase.screens.entries.models.EntrySelectionUI
import commanderpepper.labophase.screens.entries.models.RoundEntrySelectionUI

class EntryToEntrySelectionUIConverter {
    fun entryToEntrySelectionUI(entry: EntryWithRounds): EntrySelectionUI {
        return EntrySelectionUI(
            entryId = entry.entry.id,
            leader = leaderByCardId(entry.entry.leaderCardId),
            wins = getWins(entry.rounds),
            losses = getLosses(entry.rounds),
            punkRecord = getPunkRecord(
                leader = leaderByCardId(entry.entry.leaderCardId),
                rounds = entry.rounds
            ),
            rounds = entry.rounds.map { roundToRoundEntrySelectionUI(it) }
        )
    }

    private fun roundToRoundEntrySelectionUI(roundEntity: RoundEntity): RoundEntrySelectionUI {
        val leader = leaderByCardId(roundEntity.leaderCardId)
        val round = Round(
            roundId = 0,
            roundNumber = roundEntity.roundNumber,
            leader = leader,
            roundResult = roundEntity.roundResult,
            turnOrder = roundEntity.turnOrder,
            dieRoll = roundEntity.dieRoll
        )
        return RoundEntrySelectionUI(leader = leader, summary = round.singleLine())
    }
}

fun getWins(rounds: List<RoundEntity>): Int {
    return rounds.count { round -> round.roundResult == RoundResult.Win }
}

fun getLosses(rounds: List<RoundEntity>): Int {
    return rounds.count { round -> round.roundResult == RoundResult.Loss }
}

fun getPunkRecord(leader: Leader, rounds: List<RoundEntity>): String {
    val formatted = rounds.map { round ->
        "${if (round.roundResult == RoundResult.Win) "W" else "L"} ${leaderByCardId(round.leaderCardId).name} ${if (round.turnOrder == TurnOrder.First) "1st" else "2nd"}"
    }.joinToString(separator = "\n")
    return "!PR add\n${leader.name}\n$formatted"
}
