package commanderpepper.labophase.logic.converter

import commanderpepper.labophase.data.EntryWithRounds
import commanderpepper.labophase.data.entity.RoundEntity
import commanderpepper.labophase.logic.PunkRecordCreator
import commanderpepper.labophase.models.Round
import commanderpepper.labophase.models.RoundResult
import commanderpepper.labophase.models.leaderByCardId
import commanderpepper.labophase.models.locationById
import commanderpepper.labophase.screens.entries.models.EntrySelectionUI
import commanderpepper.labophase.screens.entries.models.RoundEntrySelectionUI

class EntryToEntrySelectionUIConverter(private val punkRecordCreator: PunkRecordCreator) {
    fun entryToEntrySelectionUI(entry: EntryWithRounds): EntrySelectionUI {
        return EntrySelectionUI(
            entryId = entry.entry.id,
            leader = leaderByCardId(entry.entry.leaderCardId),
            wins = getWins(entry.rounds),
            losses = getLosses(entry.rounds),
            punkRecord = punkRecordCreator.createForEntrySelection(
                prLocation = locationById(entry.entry.locationId)?.punkRecordAbbreviation ?: "other",
                leader = leaderByCardId(entry.entry.leaderCardId),
                rounds = entry.rounds
            ),
            rounds = entry.rounds.map { roundToRoundEntrySelectionUI(it) },
            title = entry.entry.title,
            locationId = entry.entry.locationId,
            metaId = entry.entry.metaId,
            date = entry.entry.date
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
