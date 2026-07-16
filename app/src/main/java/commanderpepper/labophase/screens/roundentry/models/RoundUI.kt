package commanderpepper.labophase.screens.roundentry.models

import commanderpepper.labophase.models.Leader

data class RoundUI(
    val roundId: Int,
    val leader: Leader,
    val summary: String,
    val roundResult: String,
    val turnOrder: String
)
