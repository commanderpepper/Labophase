package commanderpepper.labophase.logic

import commanderpepper.labophase.data.entity.RoundEntity
import commanderpepper.labophase.models.Leader
import commanderpepper.labophase.models.Opponent
import commanderpepper.labophase.models.Round

interface PunkRecordCreator {
    fun createForRoundEntry(prLocation: String, leader: Leader, rounds: List<Round>, opponents: List<Opponent>): String
    fun createForEntrySelection(prLocation: String, leader: Leader, rounds: List<RoundEntity>, opponents: List<Opponent>): String
}
