package commanderpepper.labophase.data

import androidx.room.Embedded
import androidx.room.Relation

data class EntryWithRounds(
    @Embedded val entry: EntryEntity,
    @Relation(parentColumn = "id", entityColumn = "entryId")
    val rounds: List<RoundEntity>
)
