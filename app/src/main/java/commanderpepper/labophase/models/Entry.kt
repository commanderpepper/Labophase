package commanderpepper.labophase.models

import androidx.compose.ui.graphics.Color
import commanderpepper.labophase.ui.theme.OptcgBlack
import commanderpepper.labophase.ui.theme.OptcgBlue
import commanderpepper.labophase.ui.theme.OptcgGreen
import commanderpepper.labophase.ui.theme.OptcgPurple
import commanderpepper.labophase.ui.theme.OptcgRed
import commanderpepper.labophase.ui.theme.OptcgYellow

data class Entry(val leaderPlayed: Leader, val rounds: List<Round> = emptyList())

data class Round(
    val roundId: Int,
    val roundNumber: Int,
    val leader: Leader = Leader.PBLuffy,
    val roundResult: RoundResult = RoundResult.Win,
    val turnOrder: TurnOrder = TurnOrder.First
) {
    fun singleLine(): String {
        return "${this.leader.name}, ${if (this.roundResult == RoundResult.Win) "W" else "L"}, ${if (this.turnOrder == TurnOrder.First) "1" else "2"}"
    }
}

sealed class RoundResult {
    object Win : RoundResult()
    object Loss : RoundResult()
}

sealed class TurnOrder {
    object First : TurnOrder()
    object Second : TurnOrder()
}

sealed class Leader(val name: String, val set: Set, val cardId: String, val leaderColors: List<LeaderColor>, val blockNumber: Int = set.blockNumber) {
    object RGOden : Leader(name = "RG Oden", set = Set.EB01, cardId = "EB01-001", leaderColors = listOf(LeaderColor.Red, LeaderColor.Green))
    object UPHannyabal : Leader(name = "UP Hannyabal", set = Set.EB01, cardId = "EB01-021", leaderColors = listOf(LeaderColor.Blue, LeaderColor.Purple))
    object BYKyros : Leader(name = "BY Kyros", set = Set.EB01, cardId = "EB01-040", leaderColors = listOf(LeaderColor.Black, LeaderColor.Yellow))
    object GPLuffy : Leader(name = "GP Luffy", set = Set.EB02, cardId = "EB02-010", leaderColors = listOf(LeaderColor.Green, LeaderColor.Purple))
    object URVivi : Leader(name = "UR Vivi", set = Set.EB03, cardId = "EB03-001", leaderColors = listOf(LeaderColor.Blue, LeaderColor.Red))
    object RYBonney : Leader(name = "RY Bonney", set = Set.EB04, cardId = "EB04-001", leaderColors = listOf(LeaderColor.Red, LeaderColor.Yellow))
    object RBSabo05 : Leader(name = "RB Sabo05", set = Set.OP05, cardId = "OP05-001", leaderColors = listOf(LeaderColor.Red, LeaderColor.Black))
    object RYBetty : Leader(name = "RY Betty", set = Set.OP05, cardId = "OP05-002", leaderColors = listOf(LeaderColor.Red, LeaderColor.Yellow))
    object UGRosinante : Leader(name = "UG Rosinante", set = Set.OP05, cardId = "OP05-022", leaderColors = listOf(LeaderColor.Blue, LeaderColor.Green))
    object UBSakazuki : Leader(name = "UB Sakazuki", set = Set.OP05, cardId = "OP05-041", leaderColors = listOf(LeaderColor.Blue, LeaderColor.Black))
    object PLuffy : Leader(name = "P Luffy", set = Set.OP05, cardId = "OP05-060", leaderColors = listOf(LeaderColor.Purple))
    object YEnel : Leader(name = "Y Enel", set = Set.OP05, cardId = "OP05-098", leaderColors = listOf(LeaderColor.Yellow))
    object RPUta : Leader(name = "RP Uta", set = Set.OP06, cardId = "OP06-001", leaderColors = listOf(LeaderColor.Red, LeaderColor.Purple))
    object GHody : Leader(name = "G Hody", set = Set.OP06, cardId = "OP06-020", leaderColors = listOf(LeaderColor.Green))
    object GBPerona : Leader(name = "GB Perona", set = Set.OP06, cardId = "OP06-021", leaderColors = listOf(LeaderColor.Green, LeaderColor.Black))
    object GYYamato : Leader(name = "GY Yamato", set = Set.OP06, cardId = "OP06-022", leaderColors = listOf(LeaderColor.Green, LeaderColor.Yellow))
    object UPReiju : Leader(name = "UP Reiju", set = Set.OP06, cardId = "OP06-042", leaderColors = listOf(LeaderColor.Blue, LeaderColor.Purple))
    object BGecko : Leader(name = "B Gecko", set = Set.OP06, cardId = "OP06-080", leaderColors = listOf(LeaderColor.Black))
    object RDragon : Leader(name = "R Dragon", set = Set.OP07, cardId = "OP07-001", leaderColors = listOf(LeaderColor.Red))
    object GBonney : Leader(name = "G Bonney", set = Set.OP07, cardId = "OP07-019", leaderColors = listOf(LeaderColor.Green))
    object UBoa : Leader(name = "U Boa", set = Set.OP07, cardId = "OP07-038", leaderColors = listOf(LeaderColor.Blue))
    object PFoxy : Leader(name = "P Foxy", set = Set.OP07, cardId = "OP07-059", leaderColors = listOf(LeaderColor.Purple))
    object BLucci : Leader(name = "B Lucci", set = Set.OP07, cardId = "OP07-079", leaderColors = listOf(LeaderColor.Black))
    object YVegapunk : Leader(name = "Y Vegapunk", set = Set.OP07, cardId = "OP07-097", leaderColors = listOf(LeaderColor.Yellow))
    object RGChopper : Leader(name = "RG Chopper", set = Set.OP08, cardId = "OP08-001", leaderColors = listOf(LeaderColor.Red, LeaderColor.Green))
    object URMarco : Leader(name = "UR Marco", set = Set.OP08, cardId = "OP08-002", leaderColors = listOf(LeaderColor.Blue, LeaderColor.Red))
    object GCarrot : Leader(name = "G Carrot", set = Set.OP08, cardId = "OP08-021", leaderColors = listOf(LeaderColor.Green))
    object PBKing : Leader(name = "PB King", set = Set.OP08, cardId = "OP08-057", leaderColors = listOf(LeaderColor.Purple, LeaderColor.Black))
    object PYPudding : Leader(name = "PY Pudding", set = Set.OP08, cardId = "OP08-058", leaderColors = listOf(LeaderColor.Purple, LeaderColor.Yellow))
    object YKalgara : Leader(name = "Y Kalgara", set = Set.OP08, cardId = "OP08-098", leaderColors = listOf(LeaderColor.Yellow))
    object RShanks : Leader(name = "R Shanks", set = Set.OP09, cardId = "OP09-001", leaderColors = listOf(LeaderColor.Red))
    object GPLim : Leader(name = "GP Lim", set = Set.OP09, cardId = "OP09-022", leaderColors = listOf(LeaderColor.Green, LeaderColor.Purple))
    object UBuggy09 : Leader(name = "U Buggy09", set = Set.OP09, cardId = "OP09-042", leaderColors = listOf(LeaderColor.Blue))
    object PBLuffy : Leader(name = "PB Luffy", set = Set.OP09, cardId = "OP09-061", leaderColors = listOf(LeaderColor.Purple, LeaderColor.Black))
    object PYRobin : Leader(name = "PY Robin", set = Set.OP09, cardId = "OP09-062", leaderColors = listOf(LeaderColor.Purple, LeaderColor.Yellow))
    object BBlackbeard : Leader(name = "B Blackbeard", set = Set.OP09, cardId = "OP09-081", leaderColors = listOf(LeaderColor.Black))
    object RGSmoker : Leader(name = "RG Smoker", set = Set.OP10, cardId = "OP10-001", leaderColors = listOf(LeaderColor.Red, LeaderColor.Green))
    object URCaesar : Leader(name = "UR Caesar", set = Set.OP10, cardId = "OP10-002", leaderColors = listOf(LeaderColor.Blue, LeaderColor.Red))
    object RPSugar : Leader(name = "RP Sugar", set = Set.OP10, cardId = "OP10-003", leaderColors = listOf(LeaderColor.Red, LeaderColor.Purple))
    object GYLaw : Leader(name = "GY Law", set = Set.OP10, cardId = "OP10-022", leaderColors = listOf(LeaderColor.Green, LeaderColor.Yellow))
    object UBUsopp : Leader(name = "UB Usopp", set = Set.OP10, cardId = "OP10-042", leaderColors = listOf(LeaderColor.Blue, LeaderColor.Black))
    object YKid : Leader(name = "Y Kid", set = Set.OP10, cardId = "OP10-099", leaderColors = listOf(LeaderColor.Yellow))
    object RBKoby : Leader(name = "RB Koby", set = Set.OP11, cardId = "OP11-001", leaderColors = listOf(LeaderColor.Red, LeaderColor.Black))
    object GJinbe : Leader(name = "G Jinbe", set = Set.OP11, cardId = "OP11-021", leaderColors = listOf(LeaderColor.Green))
    object GYShirahoshi : Leader(name = "GY Shirahoshi", set = Set.OP11, cardId = "OP11-022", leaderColors = listOf(LeaderColor.Green, LeaderColor.Yellow))
    object UPLuffy : Leader(name = "UP Luffy", set = Set.OP11, cardId = "OP11-040", leaderColors = listOf(LeaderColor.Blue, LeaderColor.Purple))
    object UYNami : Leader(name = "UY Nami", set = Set.OP11, cardId = "OP11-041", leaderColors = listOf(LeaderColor.Blue, LeaderColor.Yellow))
    object PKatakuri : Leader(name = "P Katakuri", set = Set.OP11, cardId = "OP11-062", leaderColors = listOf(LeaderColor.Purple))
    object RRayleigh : Leader(name = "R Rayleigh", set = Set.OP12, cardId = "OP12-001", leaderColors = listOf(LeaderColor.Red))
    object GZoro : Leader(name = "G Zoro", set = Set.OP12, cardId = "OP12-020", leaderColors = listOf(LeaderColor.Green))
    object UKuzan : Leader(name = "U Kuzan", set = Set.OP12, cardId = "OP12-040", leaderColors = listOf(LeaderColor.Blue))
    object UPSanji : Leader(name = "UP Sanji", set = Set.OP12, cardId = "OP12-041", leaderColors = listOf(LeaderColor.Blue, LeaderColor.Purple))
    object PYRosinante : Leader(name = "PY Rosinante", set = Set.OP12, cardId = "OP12-061", leaderColors = listOf(LeaderColor.Purple, LeaderColor.Yellow))
    object BYKoala : Leader(name = "BY Koala", set = Set.OP12, cardId = "OP12-081", leaderColors = listOf(LeaderColor.Black, LeaderColor.Yellow))
    object RGLuffy : Leader(name = "RG Luffy", set = Set.OP13, cardId = "OP13-001", leaderColors = listOf(LeaderColor.Red, LeaderColor.Green))
    object URAce : Leader(name = "UR Ace", set = Set.OP13, cardId = "OP13-002", leaderColors = listOf(LeaderColor.Blue, LeaderColor.Red))
    object RPRoger : Leader(name = "RP Roger", set = Set.OP13, cardId = "OP13-003", leaderColors = listOf(LeaderColor.Red, LeaderColor.Purple))
    object RBSabo : Leader(name = "RB Sabo", set = Set.OP13, cardId = "OP13-004", leaderColors = listOf(LeaderColor.Red, LeaderColor.Black))
    object BImu : Leader(name = "B Imu", set = Set.OP13, cardId = "OP13-079", leaderColors = listOf(LeaderColor.Black))
    object YBonney : Leader(name = "Y Bonney", set = Set.OP13, cardId = "OP13-100", leaderColors = listOf(LeaderColor.Yellow))
    object UYBoa : Leader(name = "UY Boa", set = Set.OP14, cardId = "OP14-041", leaderColors = listOf(LeaderColor.Blue, LeaderColor.Yellow))
    object GMihawk : Leader(name = "G Mihawk", set = Set.OP14, cardId = "OP14-020", leaderColors = listOf(LeaderColor.Green))
    object UJinbe : Leader(name = "U Jinbe", set = Set.OP14, cardId = "OP14-040", leaderColors = listOf(LeaderColor.Blue))
    object RLaw : Leader(name = "R Law", set = Set.OP14, cardId = "OP14-001", leaderColors = listOf(LeaderColor.Red))
    object PDoflamingo : Leader(name = "P Doflamingo", set = Set.OP14, cardId = "OP14-060", leaderColors = listOf(LeaderColor.Purple))
    object BYGecko : Leader(name = "BY Gecko", set = Set.OP14, cardId = "OP14-080", leaderColors = listOf(LeaderColor.Black, LeaderColor.Yellow))
    object BCrocodile : Leader(name = "B Crocodile", set = Set.OP14, cardId = "OP14-079", leaderColors = listOf(LeaderColor.Black))
    object YLuffy : Leader(name = "Y Luffy", set = Set.OP15, cardId = "OP15-098", leaderColors = listOf(LeaderColor.Yellow))
    object PEnel : Leader(name = "P Enel", set = Set.OP15, cardId = "OP15-058", leaderColors = listOf(LeaderColor.Purple))
    object URebecca : Leader(name = "U Rebecca", set = Set.OP15, cardId = "OP15-039", leaderColors = listOf(LeaderColor.Blue))
    object GBBrook : Leader(name = "GB Brook", set = Set.OP15, cardId = "OP15-022", leaderColors = listOf(LeaderColor.Green, LeaderColor.Black))
    object RGKrieg : Leader(name = "RG Krieg", set = Set.OP15, cardId = "OP15-001", leaderColors = listOf(LeaderColor.Red, LeaderColor.Green))
    object URLucy : Leader(name = "UR Lucy", set = Set.OP15, cardId = "OP15-002", leaderColors = listOf(LeaderColor.Blue, LeaderColor.Red))
    object PSengoku : Leader(name = "P Sengoku", set = Set.OP16, cardId = "OP16-060", leaderColors = listOf(LeaderColor.Purple))
    object BYamato : Leader(name = "B Yamato", set = Set.OP16, cardId = "OP16-079", leaderColors = listOf(LeaderColor.Black))
    object UBuggy : Leader(name = "U Buggy", set = Set.OP16, cardId = "OP16-041", leaderColors = listOf(LeaderColor.Blue))
    object BYBlackbeard : Leader(name = "BY Blackbeard", set = Set.OP16, cardId = "OP16-080", leaderColors = listOf(LeaderColor.Black, LeaderColor.Yellow))
    object RAce : Leader(name = "R Ace", set = Set.OP16, cardId = "OP16-001", leaderColors = listOf(LeaderColor.Red))
    object UGLuffy : Leader(name = "UG Luffy", set = Set.OP16, cardId = "OP16-022", leaderColors = listOf(LeaderColor.Blue, LeaderColor.Green))
    object ULuffy : Leader(name = "U Luffy", set = Set.PROMOS, cardId = "P-047", leaderColors = listOf(LeaderColor.Blue), blockNumber = 2)
    object UBSakazukiP : Leader(name = "UB SakazukiP", set = Set.PROMOS, cardId = "P-076", leaderColors = listOf(LeaderColor.Blue, LeaderColor.Black), blockNumber = 2)
    object RPLawP : Leader(name = "RP LawP", set = Set.PROMOS, cardId = "P-086", leaderColors = listOf(LeaderColor.Red, LeaderColor.Purple), blockNumber = 3)
    object UNami : Leader(name = "U Nami", set = Set.PROMOS, cardId = "P-117", leaderColors = listOf(LeaderColor.Blue), blockNumber = 4)
    object RSanji : Leader(name = "R Sanji", set = Set.PRB01, cardId = "PRB01-001", leaderColors = listOf(LeaderColor.Red))
    object RPLaw : Leader(name = "RP Law", set = Set.ST10, cardId = "ST10-001", leaderColors = listOf(LeaderColor.Red, LeaderColor.Purple))
    object RPLuffy : Leader(name = "RP Luffy", set = Set.ST10, cardId = "ST10-002", leaderColors = listOf(LeaderColor.Red, LeaderColor.Purple))
    object RPKid : Leader(name = "RP Kid", set = Set.ST10, cardId = "ST10-003", leaderColors = listOf(LeaderColor.Red, LeaderColor.Purple))
    object GUta : Leader(name = "G Uta", set = Set.ST11, cardId = "ST11-001", leaderColors = listOf(LeaderColor.Green))
    object UGZonji : Leader(name = "UG Zonji", set = Set.ST12, cardId = "ST12-001", leaderColors = listOf(LeaderColor.Blue, LeaderColor.Green))
    object RYSabo : Leader(name = "RY Sabo", set = Set.ST13, cardId = "ST13-001", leaderColors = listOf(LeaderColor.Red, LeaderColor.Yellow))
    object UYAce : Leader(name = "UY Ace", set = Set.ST13, cardId = "ST13-002", leaderColors = listOf(LeaderColor.Blue, LeaderColor.Yellow))
    object BYLuffy : Leader(name = "BY Luffy", set = Set.ST13, cardId = "ST13-003", leaderColors = listOf(LeaderColor.Black, LeaderColor.Yellow))
    object BLuffy : Leader(name = "B Luffy", set = Set.ST14, cardId = "ST14-001", leaderColors = listOf(LeaderColor.Black))
    object RLuffy : Leader(name = "R Luffy", set = Set.ST21, cardId = "ST21-001", leaderColors = listOf(LeaderColor.Red))
    object UAceNewgate : Leader(name = "U AceNewgate", set = Set.ST22, cardId = "ST22-001", leaderColors = listOf(LeaderColor.Blue))
    object YLuffyST29 : Leader(name = "Y LuffyST29", set = Set.ST29, cardId = "ST29-001", leaderColors = listOf(LeaderColor.Yellow))
    object RGLuffyAce : Leader(name = "RG LuffyAce", set = Set.ST30, cardId = "ST30-001", leaderColors = listOf(LeaderColor.Red, LeaderColor.Green))
}

sealed class Set(val name: String, val code: String, val number: Int, val blockNumber: Int) {
    object OP01 : Set(name = "Romance Dawn", code = "OP01", number = 1, blockNumber = 1)
    object ST01 : Set(name = "Straw Hat Crew", code = "ST01", number = 2, blockNumber = 1)
    object ST02 : Set(name = "Worst Generation", code = "ST02", number = 3, blockNumber = 1)
    object ST03 : Set(name = "The Seven Warlords of the Sea", code = "ST03", number = 4, blockNumber = 1)
    object ST04 : Set(name = "Animal Kingdom Pirates", code = "ST04", number = 5, blockNumber = 1)
    object ST05 : Set(name = "ONE PIECE FILM edition", code = "ST05", number = 6, blockNumber = 1)
    object OP02 : Set(name = "Paramount War", code = "OP02", number = 7, blockNumber = 1)
    object ST06 : Set(name = "Absolute Justice", code = "ST06", number = 8, blockNumber = 1)
    object OP03 : Set(name = "Pillars of Strength", code = "OP03", number = 9, blockNumber = 1)
    object ST07 : Set(name = "Big Mom Pirates", code = "ST07", number = 10, blockNumber = 1)
    object ST08 : Set(name = "Monkey D. Luffy", code = "ST08", number = 11, blockNumber = 1)
    object ST09 : Set(name = "Yamato", code = "ST09", number = 12, blockNumber = 1)
    object OP04 : Set(name = "Kingdoms of Intrigue", code = "OP04", number = 13, blockNumber = 1)
    object ST10 : Set(name = "Ultra Deck: The Three Captains", code = "ST10", number = 14, blockNumber = 2)
    object OP05 : Set(name = "Awakening of the New Era", code = "OP05", number = 15, blockNumber = 2)
    object ST11 : Set(name = "Uta", code = "ST11", number = 16, blockNumber = 2)
    object OP06 : Set(name = "Wings of the Captain", code = "OP06", number = 17, blockNumber = 2)
    object ST12 : Set(name = "Zoro & Sanji", code = "ST12", number = 18, blockNumber = 2)
    object ST13 : Set(name = "Ultra Deck: The Three Brothers", code = "ST13", number = 19, blockNumber = 2)
    object EB01 : Set(name = "Memorial Collection", code = "EB01", number = 20, blockNumber = 2)
    object OP07 : Set(name = "500 Years in the Future", code = "OP07", number = 21, blockNumber = 2)
    object ST14 : Set(name = "3D2Y", code = "ST14", number = 22, blockNumber = 2)
    object OP08 : Set(name = "Two Legends", code = "OP08", number = 23, blockNumber = 2)
    object ST15 : Set(name = "RED Edward.Newgate", code = "ST15", number = 24, blockNumber = 2)
    object ST16 : Set(name = "GREEN Uta", code = "ST16", number = 25, blockNumber = 2)
    object ST17 : Set(name = "BLUE Donquixote Doflamingo", code = "ST17", number = 26, blockNumber = 2)
    object ST18 : Set(name = "PURPLE Monkey.D.Luffy", code = "ST18", number = 27, blockNumber = 2)
    object ST19 : Set(name = "BLACK Smoker", code = "ST19", number = 28, blockNumber = 2)
    object ST20 : Set(name = "YELLOW Charlotte Katakuri", code = "ST20", number = 29, blockNumber = 2)
    object PRB01 : Set(name = "One Piece The Best", code = "PRB01", number = 30, blockNumber = 3)
    object OP09 : Set(name = "Emperors in the New World", code = "OP09", number = 31, blockNumber = 3)
    object ST21 : Set(name = "EX Gear 5", code = "ST21", number = 32, blockNumber = 3)
    object OP10 : Set(name = "Royal Blood", code = "OP10", number = 33, blockNumber = 3)
    object EB02 : Set(name = "Anime 25th Collection", code = "EB02", number = 34, blockNumber = 3)
    object OP11 : Set(name = "A Fist of Divine Speed", code = "OP11", number = 35, blockNumber = 3)
    object ST23 : Set(name = "RED Shanks", code = "ST23", number = 36, blockNumber = 3)
    object ST24 : Set(name = "GREEN Jewelry Bonney", code = "ST24", number = 37, blockNumber = 3)
    object ST25 : Set(name = "BLUE Buggy", code = "ST25", number = 38, blockNumber = 3)
    object ST26 : Set(name = "PURPLE/BLACK Monkey.D.Luffy", code = "ST26", number = 39, blockNumber = 3)
    object ST27 : Set(name = "BLACK Marshall.D.Teach", code = "ST27", number = 40, blockNumber = 3)
    object ST28 : Set(name = "GREEN/YELLOW Yamato", code = "ST28", number = 41, blockNumber = 3)
    object OP12 : Set(name = "Legacy of the Master", code = "OP12", number = 42, blockNumber = 3)
    object ST22 : Set(name = "Ace & Newgate", code = "ST22", number = 43, blockNumber = 3)
    object PRB02 : Set(name = "One Piece Card The Best Vol.2", code = "PRB02", number = 44, blockNumber = 4)
    object OP13 : Set(name = "Carrying on His Will", code = "OP13", number = 45, blockNumber = 4)
    object OP14 : Set(name = "The Azure Sea's Seven", code = "OP14", number = 46, blockNumber = 4)
    object EB04 : Set(name = "Egghead Crisis", code = "EB04", number = 47, blockNumber = 4)
    object ST29 : Set(name = "Egghead", code = "ST29", number = 48, blockNumber = 4)
    object EB03 : Set(name = "One Piece Heroines Edition", code = "EB03", number = 49, blockNumber = 4)
    object OP15 : Set(name = "Adventure on Kami's Island", code = "OP15", number = 50, blockNumber = 4)
    object OP16 : Set(name = "The Time of Battle", code = "OP16", number = 51, blockNumber = 5)
    object ST30 : Set(name = "EX Luffy & Ace", code = "ST30", number = 52, blockNumber = 5)
    object PROMOS : Set(name = "Promotional Cards", code = "PROMOS", number = 0, blockNumber = 0)
}

sealed class LeaderColor(val symbol: String, val color: Color) {
    object Red    : LeaderColor(symbol = "R", color = OptcgRed)
    object Blue   : LeaderColor(symbol = "U", color = OptcgBlue)
    object Green  : LeaderColor(symbol = "G", color = OptcgGreen)
    object Yellow : LeaderColor(symbol = "Y", color = OptcgYellow)
    object Purple : LeaderColor(symbol = "P", color = OptcgPurple)
    object Black  : LeaderColor(symbol = "B", color = OptcgBlack)
}

val LEADERS_LIST = listOf(
    Leader.RGOden,
    Leader.UPHannyabal,
    Leader.BYKyros,
    Leader.GPLuffy,
    Leader.URVivi,
    Leader.RYBonney,
    Leader.RBSabo05,
    Leader.RYBetty,
    Leader.UGRosinante,
    Leader.UBSakazuki,
    Leader.PLuffy,
    Leader.YEnel,
    Leader.RPUta,
    Leader.GHody,
    Leader.GBPerona,
    Leader.GYYamato,
    Leader.UPReiju,
    Leader.BGecko,
    Leader.RDragon,
    Leader.GBonney,
    Leader.UBoa,
    Leader.PFoxy,
    Leader.BLucci,
    Leader.YVegapunk,
    Leader.RGChopper,
    Leader.URMarco,
    Leader.GCarrot,
    Leader.PBKing,
    Leader.PYPudding,
    Leader.YKalgara,
    Leader.RShanks,
    Leader.GPLim,
    Leader.UBuggy09,
    Leader.PBLuffy,
    Leader.PYRobin,
    Leader.BBlackbeard,
    Leader.RGSmoker,
    Leader.URCaesar,
    Leader.RPSugar,
    Leader.GYLaw,
    Leader.UBUsopp,
    Leader.YKid,
    Leader.RBKoby,
    Leader.GJinbe,
    Leader.GYShirahoshi,
    Leader.UPLuffy,
    Leader.UYNami,
    Leader.PKatakuri,
    Leader.RRayleigh,
    Leader.GZoro,
    Leader.UKuzan,
    Leader.UPSanji,
    Leader.PYRosinante,
    Leader.BYKoala,
    Leader.RGLuffy,
    Leader.URAce,
    Leader.RPRoger,
    Leader.RBSabo,
    Leader.BImu,
    Leader.YBonney,
    Leader.UYBoa,
    Leader.GMihawk,
    Leader.UJinbe,
    Leader.RLaw,
    Leader.PDoflamingo,
    Leader.BYGecko,
    Leader.BCrocodile,
    Leader.YLuffy,
    Leader.PEnel,
    Leader.URebecca,
    Leader.GBBrook,
    Leader.RGKrieg,
    Leader.URLucy,
    Leader.PSengoku,
    Leader.BYamato,
    Leader.UBuggy,
    Leader.BYBlackbeard,
    Leader.RAce,
    Leader.UGLuffy,
    Leader.ULuffy,
    Leader.UBSakazukiP,
    Leader.RPLawP,
    Leader.UNami,
    Leader.RSanji,
    Leader.RPLaw,
    Leader.RPLuffy,
    Leader.RPKid,
    Leader.GUta,
    Leader.UGZonji,
    Leader.RYSabo,
    Leader.UYAce,
    Leader.BYLuffy,
    Leader.BLuffy,
    Leader.RLuffy,
    Leader.UAceNewgate,
    Leader.YLuffyST29,
    Leader.RGLuffyAce
)

fun leaderByCardId(cardId: String): Leader = LEADERS_LIST.first { it.cardId == cardId }