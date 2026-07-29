package commanderpepper.labophase.screens.roundentry.models

import commanderpepper.labophase.models.Leader
import commanderpepper.labophase.models.RoundResult
import commanderpepper.labophase.models.TurnOrder

data class RoundUI(
    val roundId: Int,
    val leader: Leader,
    val summary: String,
    val roundResult: RoundResult,
    val turnOrder: TurnOrder,
    val dieRoll: String? = null
)
