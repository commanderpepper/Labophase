package commanderpepper.labophase.models

data class Entry(val leaderPlayed: Leader, val rounds: List<Round> = emptyList())

data class Round(
    val roundId: Int,
    val roundNumber: Int,
    val leader: Leader = Leader.PBLuffy,
    val roundResult: RoundResult = RoundResult.Win,
    val turnOrder: TurnOrder = TurnOrder.First,
    val dieRoll: String? = null
) {
    fun singleLine(): String {
        return "${this.leader.name}, ${if (this.roundResult == RoundResult.Win) "W" else "L"}, ${if (this.turnOrder == TurnOrder.First) "1" else "2"}${transformDieRoll()}"
    }

    private fun transformDieRoll(): String {
        return if (this.dieRoll != null) {
            if (this.dieRoll == "Win") {
                ", \uD83C\uDFB2"

            } else {
                ", ❌"
            }
        } else ""
    }
}

sealed class RoundResult {
    object Win : RoundResult()
    object Loss : RoundResult()
}

sealed class TurnOrder {
    object First : TurnOrder()
    object Second : TurnOrder()
}
