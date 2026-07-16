package commanderpepper.labophase.navigation

sealed class Screen(val route: String) {
    object EntrySelection : Screen("entry_selection")
    object RoundEntry : Screen("round_entry?entryId={entryId}") {
        const val ARG = "entryId"
        fun navigate(entryId: Int = -1) = "round_entry?entryId=$entryId"
    }
    object Settings: Screen("settings")
}
