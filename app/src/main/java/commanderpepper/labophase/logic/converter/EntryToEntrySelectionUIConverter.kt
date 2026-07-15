package commanderpepper.labophase.logic.converter

import commanderpepper.labophase.data.EntryWithRounds
import commanderpepper.labophase.data.RoundEntity
import commanderpepper.labophase.models.Leader
import commanderpepper.labophase.models.leaderByCardId
import commanderpepper.labophase.screens.entries.models.EntrySelectionUI

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
            )
        )
    }
}

fun getWins(rounds: List<RoundEntity>): Int {
    return rounds.count { round -> round.roundResult == "Win" }
}

fun getLosses(rounds: List<RoundEntity>): Int {
    return rounds.count { round -> round.roundResult == "Loss" }
}

fun getPunkRecord(leader: Leader, rounds: List<RoundEntity>): String {
    val formatted = rounds.map { round ->
        "${if (round.roundResult == "Win") "W" else "L"} ${leaderByCardId(round.leaderCardId)} ${if (round.turnOrder == "First") "1st" else "2nd"}"
    }.joinToString(separator = "\n")
    return "!PR add\n${leader.name}\n$formatted"
}