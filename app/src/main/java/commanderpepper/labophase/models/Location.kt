package commanderpepper.labophase.models

sealed class Location(val id: Int, val name: String, val abbreviation: String) {
    object ChronosGamesAndGifts : Location(id = 1,  name = "Chronos Games & Gifts",   abbreviation = "Chronos")
    object TheClubhouse          : Location(id = 2,  name = "The Clubhouse",            abbreviation = "Clubhouse")
    object MoxValleyGames        : Location(id = 3,  name = "Mox Valley Games",         abbreviation = "Mox Valley")
    object TOTLGames             : Location(id = 4,  name = "TOTL Games",               abbreviation = "TOTL")
    object GongaiiGames          : Location(id = 5,  name = "Gongaii Games",            abbreviation = "Gongaii")
    object CapesAndCrepes        : Location(id = 6,  name = "Capes & Crepes",           abbreviation = "C&C")
    object TreasureTroveGames    : Location(id = 7,  name = "Treasure Trove Games",     abbreviation = "Treasure Trove")
    object GuardianGamesPDX      : Location(id = 8,  name = "Guardian Games (PDX)",     abbreviation = "GG PDX")
    object WildThingsGames       : Location(id = 9,  name = "Wild Things Games",        abbreviation = "Wild Things")
    object PortlandGameStore     : Location(id = 10, name = "Portland Game Store",      abbreviation = "PGS")
    object GuardianGamesCorv     : Location(id = 11, name = "Guardian Games (Corv)",    abbreviation = "GG Corv")
    object RedCastleGames        : Location(id = 12, name = "Red Castle Games",         abbreviation = "Red Castle")
    object WaywardCityGames      : Location(id = 13, name = "Wayward City Games",       abbreviation = "Wayward City")
    object ThunderKittyGames     : Location(id = 14, name = "Thunder Kitty Games",      abbreviation = "TKG")
    object CardboardDiamonds     : Location(id = 15, name = "Cardboard Diamonds",       abbreviation = "CD")
    object Gambits               : Location(id = 16, name = "Gambits",                  abbreviation = "Gambits")
    object T2                    : Location(id = 17, name = "T2",                       abbreviation = "T2")
}

val LOCATIONS_LIST = listOf(
    Location.ChronosGamesAndGifts,
    Location.TheClubhouse,
    Location.MoxValleyGames,
    Location.TOTLGames,
    Location.GongaiiGames,
    Location.CapesAndCrepes,
    Location.TreasureTroveGames,
    Location.GuardianGamesPDX,
    Location.WildThingsGames,
    Location.PortlandGameStore,
    Location.GuardianGamesCorv,
    Location.RedCastleGames,
    Location.WaywardCityGames,
    Location.ThunderKittyGames,
    Location.CardboardDiamonds,
    Location.Gambits,
    Location.T2
)

fun locationById(id: Int): Location? = LOCATIONS_LIST.firstOrNull { it.id == id }
