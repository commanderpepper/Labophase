package commanderpepper.labophase.navigation

import kotlinx.serialization.Serializable

@Serializable
object EntrySelection

@Serializable
data class RoundEntry(val entryId: Int? = null)

@Serializable
object Settings
