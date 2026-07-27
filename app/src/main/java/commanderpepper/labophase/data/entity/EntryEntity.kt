package commanderpepper.labophase.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "entries")
data class EntryEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val leaderCardId: String,
    val title: String? = null,
    val locationId: Int? = null,
    val metaId: Int? = null,
    val date: String? = null
)
