package commanderpepper.labophase.logic

import commanderpepper.labophase.data.entity.RoundEntity
import commanderpepper.labophase.models.Leader
import commanderpepper.labophase.models.Round
import commanderpepper.labophase.models.RoundResult
import commanderpepper.labophase.models.TurnOrder
import commanderpepper.labophase.models.leaderByCardId

class PunkRecordCreatorImpl : PunkRecordCreator {
    override fun createForRoundEntry(prLocation: String, leader: Leader, rounds: List<Round>): String {
        val formatted = rounds.joinToString(separator = "\n") { round ->
            "${if (round.roundResult == RoundResult.Win) "W" else "L"} ${round.leader.name} ${if (round.turnOrder == TurnOrder.First) "1st" else "2nd"}"
        }
        return "!PR add\n$prLocation\n${leader.name}\n$formatted"
    }

    override fun createForEntrySelection(prLocation: String, leader: Leader, rounds: List<RoundEntity>): String {
        val formatted = rounds.joinToString(separator = "\n") { round ->
            "${if (round.roundResult == RoundResult.Win) "W" else "L"} ${leaderByCardId(round.leaderCardId).name} ${if (round.turnOrder == TurnOrder.First) "1st" else "2nd"}"
        }
        return "!PR add\n$prLocation\n${leader.name}\n$formatted"
    }
}
