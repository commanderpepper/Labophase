package commanderpepper.labophase.screens.entries.models

import commanderpepper.labophase.models.Leader

data class EntrySelectionUI(val entryId: Int, val leader: Leader, val wins: Int, val losses: Int, val punkRecord: String)
