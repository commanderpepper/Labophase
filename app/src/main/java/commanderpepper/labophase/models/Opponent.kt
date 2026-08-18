package commanderpepper.labophase.models

data class Opponent(
    val id: Int,
    val name: String,
    val nickname: String
)

val OPPONENTS_SEED_LIST = listOf(
    Opponent(id = 1, name = "10000orbs", nickname = "riley"),
    Opponent(id = 2, name = "yz", nickname = "Dbandit"),
    Opponent(id = 3, name = "atran23.", nickname = "ponder"),
    Opponent(id = 4, name = "bolbyy", nickname = "bolby"),
    Opponent(id = 5, name = "bphimmasone", nickname = "Ben"),
    Opponent(id = 6, name = "commanderpepper", nickname = "Pepper"),
    Opponent(id = 7, name = "cozytony", nickname = "cozytony"),
    Opponent(id = 8, name = "derpinic", nickname = "Dom"),
    Opponent(id = 9, name = "jamericano", nickname = "JAM!"),
    Opponent(id = 10, name = "kevindu", nickname = "KEV"),
    Opponent(id = 11, name = "moondoggyhero", nickname = "moondoggy"),
    Opponent(id = 12, name = "nathanahn", nickname = "ahn"),
    Opponent(id = 13, name = "oldtuna", nickname = "oncloud2"),
    Opponent(id = 14, name = "oncloud", nickname = "on.D.cloud"),
    Opponent(id = 15, name = "pierre0019", nickname = "pierre"),
    Opponent(id = 16, name = "scooterd22", nickname = "scooter"),
    Opponent(id = 17, name = "tamdin54", nickname = "tamdin"),
    Opponent(id = 18, name = "tftren", nickname = "oncloud3"),
    Opponent(id = 19, name = "zayuh03", nickname = "ZAYUH")
)

fun List<Opponent>.findById(id: Int?): Opponent? = id?.let { searchId -> find { it.id == searchId } }
