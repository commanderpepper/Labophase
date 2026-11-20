package commanderpepper.labophase.screens.roundentry.models

import commanderpepper.labophase.models.DEFAULT_OPPONENT_LEADER
import commanderpepper.labophase.models.Leader
import commanderpepper.labophase.models.RoundResult
import commanderpepper.labophase.models.TurnOrder

/** The round being edited in the round sheet. Nothing reaches the entry until the user taps Save. */
data class RoundDraft(
    val leader: Leader = DEFAULT_OPPONENT_LEADER,
    val roundResult: RoundResult = RoundResult.Win,
    val turnOrder: TurnOrder = TurnOrder.First,
    val dieRoll: String? = null,
    val opponentId: Int? = null,
    val notes: String = ""
)

fun RoundUI.toDraft() = RoundDraft(
    leader = leader,
    roundResult = roundResult,
    turnOrder = turnOrder,
    dieRoll = dieRoll,
    opponentId = opponentId,
    notes = notes.orEmpty()
)
