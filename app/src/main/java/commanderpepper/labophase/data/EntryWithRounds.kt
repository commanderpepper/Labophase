package commanderpepper.labophase.data

import androidx.room.Embedded
import androidx.room.Relation
import commanderpepper.labophase.data.entity.EntryEntity
import commanderpepper.labophase.data.entity.RoundEntity

data class EntryWithRounds(
    @Embedded val entry: EntryEntity,
    @Relation(parentColumn = "id", entityColumn = "entryId")
    val rounds: List<RoundEntity>
)
