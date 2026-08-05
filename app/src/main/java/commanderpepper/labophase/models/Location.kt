package commanderpepper.labophase.models

sealed class Location(val id: Int, val name: String, val abbreviation: String, val punkRecordAbbreviation: String) {
    object ChronosGamesAndGifts : Location(id = 1,  name = "Chronos Games & Gifts",   abbreviation = "Chronos",       punkRecordAbbreviation = "Chronos")
    object TheClubhouse          : Location(id = 2,  name = "The Clubhouse",            abbreviation = "Clubhouse",     punkRecordAbbreviation = "Clubhouse")
    object MoxValleyGames        : Location(id = 3,  name = "Mox Valley Games",         abbreviation = "Mox Valley",    punkRecordAbbreviation = "other")
    object TOTLGames             : Location(id = 4,  name = "TOTL Games",               abbreviation = "TOTL",          punkRecordAbbreviation = "TOTL")
    object GongaiiGames          : Location(id = 5,  name = "Gongaii Games",            abbreviation = "Gongaii",       punkRecordAbbreviation = "Gongaii")
    object CapesAndCrepes        : Location(id = 6,  name = "Capes & Crepes",           abbreviation = "C&C",           punkRecordAbbreviation = "CC")
    object TreasureTroveGames    : Location(id = 7,  name = "Treasure Trove Games",     abbreviation = "Treasure Trove", punkRecordAbbreviation = "other")
    object GuardianGamesPDX      : Location(id = 8,  name = "Guardian Games (PDX)",     abbreviation = "GG PDX",        punkRecordAbbreviation = "GG")
    object WildThingsGames       : Location(id = 9,  name = "Wild Things Games",        abbreviation = "Wild Things",   punkRecordAbbreviation = "other")
    object PortlandGameStore     : Location(id = 10, name = "Portland Game Store",      abbreviation = "PGS",           punkRecordAbbreviation = "PGS")
    object GuardianGamesCorv     : Location(id = 11, name = "Guardian Games (Corv)",    abbreviation = "GG Corv",       punkRecordAbbreviation = "GG")
    object RedCastleGames        : Location(id = 12, name = "Red Castle Games",         abbreviation = "Red Castle",    punkRecordAbbreviation = "RC")
    object WaywardCityGames      : Location(id = 13, name = "Wayward City Games",       abbreviation = "Wayward City",  punkRecordAbbreviation = "Wayward")
    object ThunderKittyGames     : Location(id = 14, name = "Thunder Kitty Games",      abbreviation = "TKG",           punkRecordAbbreviation = "TKG")
    object CardboardDiamonds     : Location(id = 15, name = "Cardboard Diamonds",       abbreviation = "CD",            punkRecordAbbreviation = "other")
    object Gambits               : Location(id = 16, name = "Gambits",                  abbreviation = "Gambits",       punkRecordAbbreviation = "other")
    object T2                    : Location(id = 17, name = "T2",                       abbreviation = "T2",            punkRecordAbbreviation = "T2")
    object HiddenTreasures       : Location(id = 18, name = "Hidden Treasures",         abbreviation = "HT",            punkRecordAbbreviation = "HT")
    object EpicGaming            : Location(id = 19, name = "Epic Gaming",              abbreviation = "EPIC",          punkRecordAbbreviation = "EPIC")
    object OrbSports             : Location(id = 20, name = "Orb Sports & Collectibles", abbreviation = "ORB",          punkRecordAbbreviation = "ORB")
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
    Location.T2,
    Location.HiddenTreasures,
    Location.EpicGaming,
    Location.OrbSports
)

fun locationById(id: Int?): Location? = LOCATIONS_LIST.firstOrNull { it.id == id }