package commanderpepper.labophase.data

import androidx.room.TypeConverter
import commanderpepper.labophase.models.RoundResult
import commanderpepper.labophase.models.TurnOrder

class Converters {
    @TypeConverter
    fun roundResultToString(value: RoundResult): String =
        if (value == RoundResult.Win) "Win" else "Loss"

    @TypeConverter
    fun stringToRoundResult(value: String): RoundResult =
        if (value == "Win") RoundResult.Win else RoundResult.Loss

    @TypeConverter
    fun turnOrderToString(value: TurnOrder): String =
        if (value == TurnOrder.First) "First" else "Second"

    @TypeConverter
    fun stringToTurnOrder(value: String): TurnOrder =
        if (value == "First") TurnOrder.First else TurnOrder.Second
}
