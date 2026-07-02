package commanderpepper.labophase.models

data class Entry(val leaderPlayed: Leader, val rounds: List<Round> = emptyList())

data class Round(
    val roundId: Int,
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

sealed class Leader(val name: String, val set: Set) {
    object RGOden : Leader(name = "RG Oden", set = Set.EB01)
    object UPHannyabal : Leader(name = "UP Hannyabal", set = Set.EB01)
    object BYKyros : Leader(name = "BY Kyros", set = Set.EB01)
    object GPLuffy : Leader(name = "GP Luffy", set = Set.EB02)
    object URVivi : Leader(name = "UR Vivi", set = Set.EB03)
    object RYBonney : Leader(name = "RY Bonney", set = Set.EB04)
    object RBSabo05 : Leader(name = "RB Sabo05", set = Set.OP05)
    object RYBetty : Leader(name = "RY Betty", set = Set.OP05)
    object UGRosinante : Leader(name = "UG Rosinante", set = Set.OP05)
    object UBSakazuki : Leader(name = "UB Sakazuki", set = Set.OP05)
    object PLuffy : Leader(name = "P Luffy", set = Set.OP05)
    object YEnel : Leader(name = "Y Enel", set = Set.OP05)
    object RPUta : Leader(name = "RP Uta", set = Set.OP06)
    object GHody : Leader(name = "G Hody", set = Set.OP06)
    object GBPerona : Leader(name = "GB Perona", set = Set.OP06)
    object GYYamato : Leader(name = "GY Yamato", set = Set.OP06)
    object UPReiju : Leader(name = "UP Reiju", set = Set.OP06)
    object BGecko : Leader(name = "B Gecko", set = Set.OP06)
    object RDragon : Leader(name = "R Dragon", set = Set.OP07)
    object GBonney : Leader(name = "G Bonney", set = Set.OP07)
    object UBoa : Leader(name = "U Boa", set = Set.OP07)
    object PFoxy : Leader(name = "P Foxy", set = Set.OP07)
    object BLucci : Leader(name = "B Lucci", set = Set.OP07)
    object YVegapunk : Leader(name = "Y Vegapunk", set = Set.OP07)
    object RGChopper : Leader(name = "RG Chopper", set = Set.OP08)
    object URMarco : Leader(name = "UR Marco", set = Set.OP08)
    object GCarrot : Leader(name = "G Carrot", set = Set.OP08)
    object PBKing : Leader(name = "PB King", set = Set.OP08)
    object PYPudding : Leader(name = "PY Pudding", set = Set.OP08)
    object YKalgara : Leader(name = "Y Kalgara", set = Set.OP08)
    object RShanks : Leader(name = "R Shanks", set = Set.OP09)
    object GPLim : Leader(name = "GP Lim", set = Set.OP09)
    object UBuggy09 : Leader(name = "U Buggy09", set = Set.OP09)
    object PBLuffy : Leader(name = "PB Luffy", set = Set.OP09)
    object PYRobin : Leader(name = "PY Robin", set = Set.OP09)
    object BBlackbeard : Leader(name = "B Blackbeard", set = Set.OP09)
    object RGSmoker : Leader(name = "RG Smoker", set = Set.OP10)
    object URCaesar : Leader(name = "UR Caesar", set = Set.OP10)
    object RPSugar : Leader(name = "RP Sugar", set = Set.OP10)
    object GYLaw : Leader(name = "GY Law", set = Set.OP10)
    object UBUsopp : Leader(name = "UB Usopp", set = Set.OP10)
    object YKid : Leader(name = "Y Kid", set = Set.OP10)
    object RBKoby : Leader(name = "RB Koby", set = Set.OP11)
    object GJinbe : Leader(name = "G Jinbe", set = Set.OP11)
    object GYShirahoshi : Leader(name = "GY Shirahoshi", set = Set.OP11)
    object UPLuffy : Leader(name = "UP Luffy", set = Set.OP11)
    object UYNami : Leader(name = "UY Nami", set = Set.OP11)
    object PKatakuri : Leader(name = "P Katakuri", set = Set.OP11)
    object RRayleigh : Leader(name = "R Rayleigh", set = Set.OP12)
    object GZoro : Leader(name = "G Zoro", set = Set.OP12)
    object UKuzan : Leader(name = "U Kuzan", set = Set.OP12)
    object UPSanji : Leader(name = "UP Sanji", set = Set.OP12)
    object PYRosinante : Leader(name = "PY Rosinante", set = Set.OP12)
    object BYKoala : Leader(name = "BY Koala", set = Set.OP12)
    object RGLuffy : Leader(name = "RG Luffy", set = Set.OP13)
    object URAce : Leader(name = "UR Ace", set = Set.OP13)
    object RPRoger : Leader(name = "RP Roger", set = Set.OP13)
    object RBSabo : Leader(name = "RB Sabo", set = Set.OP13)
    object BImu : Leader(name = "B Imu", set = Set.OP13)
    object YBonney : Leader(name = "Y Bonney", set = Set.OP13)
    object UYBoa : Leader(name = "UY Boa", set = Set.OP14)
    object GMihawk : Leader(name = "G Mihawk", set = Set.OP14)
    object UJinbe : Leader(name = "U Jinbe", set = Set.OP14)
    object RLaw : Leader(name = "R Law", set = Set.OP14)
    object PDoflamingo : Leader(name = "P Doflamingo", set = Set.OP14)
    object BYGecko : Leader(name = "BY Gecko", set = Set.OP14)
    object BCrocodile : Leader(name = "B Crocodile", set = Set.OP14)
    object YLuffy : Leader(name = "Y Luffy", set = Set.OP15)
    object PEnel : Leader(name = "P Enel", set = Set.OP15)
    object URebecca : Leader(name = "U Rebecca", set = Set.OP15)
    object GBBrook : Leader(name = "GB Brook", set = Set.OP15)
    object RGKrieg : Leader(name = "RG Krieg", set = Set.OP15)
    object URLucy : Leader(name = "UR Lucy", set = Set.OP15)
    object PSengoku : Leader(name = "P Sengoku", set = Set.OP16)
    object BYamato : Leader(name = "B Yamato", set = Set.OP16)
    object UBuggy : Leader(name = "U Buggy", set = Set.OP16)
    object BYBlackbeard : Leader(name = "BY Blackbeard", set = Set.OP16)
    object RAce : Leader(name = "R Ace", set = Set.OP16)
    object UGLuffy : Leader(name = "UG Luffy", set = Set.OP16)
    object ULuffy : Leader(name = "U Luffy", set = Set.PROMOS)
    object UBSakazukiP : Leader(name = "UB SakazukiP", set = Set.PROMOS)
    object RPLawP : Leader(name = "RP LawP", set = Set.PROMOS)
    object UNami : Leader(name = "U Nami", set = Set.PROMOS)
    object RSanji : Leader(name = "R Sanji", set = Set.PRB01)
    object RPLaw : Leader(name = "RP Law", set = Set.ST10)
    object RPLuffy : Leader(name = "RP Luffy", set = Set.ST10)
    object RPKid : Leader(name = "RP Kid", set = Set.ST10)
    object GUta : Leader(name = "G Uta", set = Set.ST11)
    object UGZonji : Leader(name = "UG Zonji", set = Set.ST12)
    object RYSabo : Leader(name = "RY Sabo", set = Set.ST13)
    object UYAce : Leader(name = "UY Ace", set = Set.ST13)
    object BYLuffy : Leader(name = "BY Luffy", set = Set.ST13)
    object BLuffy : Leader(name = "B Luffy", set = Set.ST14)
    object RLuffy : Leader(name = "R Luffy", set = Set.ST21)
    object UAceNewgate : Leader(name = "U AceNewgate", set = Set.ST22)
    object YLuffyST29 : Leader(name = "Y LuffyST29", set = Set.ST29)
    object RGLuffyAce : Leader(name = "RG LuffyAce", set = Set.ST30)
}

sealed class Set(val name: String, val code: String, val number: Int) {
    object OP01 : Set(name = "Romance Dawn", code = "OP01", number = 1)
    object ST01 : Set(name = "Straw Hat Crew", code = "ST01", number = 2)
    object ST02 : Set(name = "Worst Generation", code = "ST02", number = 3)
    object ST03 : Set(name = "The Seven Warlords of the Sea", code = "ST03", number = 4)
    object ST04 : Set(name = "Animal Kingdom Pirates", code = "ST04", number = 5)
    object ST05 : Set(name = "ONE PIECE FILM edition", code = "ST05", number = 6)
    object OP02 : Set(name = "Paramount War", code = "OP02", number = 7)
    object ST06 : Set(name = "Absolute Justice", code = "ST06", number = 8)
    object OP03 : Set(name = "Pillars of Strength", code = "OP03", number = 9)
    object ST07 : Set(name = "Big Mom Pirates", code = "ST07", number = 10)
    object ST08 : Set(name = "Monkey D. Luffy", code = "ST08", number = 11)
    object ST09 : Set(name = "Yamato", code = "ST09", number = 12)
    object OP04 : Set(name = "Kingdoms of Intrigue", code = "OP04", number = 13)
    object ST10 : Set(name = "Ultra Deck: The Three Captains", code = "ST10", number = 14)
    object OP05 : Set(name = "Awakening of the New Era", code = "OP05", number = 15)
    object ST11 : Set(name = "Uta", code = "ST11", number = 16)
    object OP06 : Set(name = "Wings of the Captain", code = "OP06", number = 17)
    object ST12 : Set(name = "Zoro & Sanji", code = "ST12", number = 18)
    object ST13 : Set(name = "Ultra Deck: The Three Brothers", code = "ST13", number = 19)
    object EB01 : Set(name = "Memorial Collection", code = "EB01", number = 20)
    object OP07 : Set(name = "500 Years in the Future", code = "OP07", number = 21)
    object ST14 : Set(name = "3D2Y", code = "ST14", number = 22)
    object OP08 : Set(name = "Two Legends", code = "OP08", number = 23)
    object ST15 : Set(name = "RED Edward.Newgate", code = "ST15", number = 24)
    object ST16 : Set(name = "GREEN Uta", code = "ST16", number = 25)
    object ST17 : Set(name = "BLUE Donquixote Doflamingo", code = "ST17", number = 26)
    object ST18 : Set(name = "PURPLE Monkey.D.Luffy", code = "ST18", number = 27)
    object ST19 : Set(name = "BLACK Smoker", code = "ST19", number = 28)
    object ST20 : Set(name = "YELLOW Charlotte Katakuri", code = "ST20", number = 29)
    object PRB01 : Set(name = "One Piece The Best", code = "PRB01", number = 30)
    object OP09 : Set(name = "Emperors in the New World", code = "OP09", number = 31)
    object ST21 : Set(name = "EX Gear 5", code = "ST21", number = 32)
    object OP10 : Set(name = "Royal Blood", code = "OP10", number = 33)
    object EB02 : Set(name = "Anime 25th Collection", code = "EB02", number = 34)
    object OP11 : Set(name = "A Fist of Divine Speed", code = "OP11", number = 35)
    object ST23 : Set(name = "RED Shanks", code = "ST23", number = 36)
    object ST24 : Set(name = "GREEN Jewelry Bonney", code = "ST24", number = 37)
    object ST25 : Set(name = "BLUE Buggy", code = "ST25", number = 38)
    object ST26 : Set(name = "PURPLE/BLACK Monkey.D.Luffy", code = "ST26", number = 39)
    object ST27 : Set(name = "BLACK Marshall.D.Teach", code = "ST27", number = 40)
    object ST28 : Set(name = "GREEN/YELLOW Yamato", code = "ST28", number = 41)
    object OP12 : Set(name = "Legacy of the Master", code = "OP12", number = 42)
    object ST22 : Set(name = "Ace & Newgate", code = "ST22", number = 43)
    object PRB02 : Set(name = "One Piece Card The Best Vol.2", code = "PRB02", number = 44)
    object OP13 : Set(name = "Carrying on His Will", code = "OP13", number = 45)
    object OP14 : Set(name = "The Azure Sea's Seven", code = "OP14", number = 46)
    object EB04 : Set(name = "Egghead Crisis", code = "EB04", number = 47)
    object ST29 : Set(name = "Egghead", code = "ST29", number = 48)
    object EB03 : Set(name = "One Piece Heroines Edition", code = "EB03", number = 49)
    object OP15 : Set(name = "Adventure on Kami's Island", code = "OP15", number = 50)
    object OP16 : Set(name = "The Time of Battle", code = "OP16", number = 51)
    object ST30 : Set(name = "EX Luffy & Ace", code = "ST30", number = 52)
    object PROMOS : Set(name = "Promotional Cards", code = "PROMOS", number = 0)
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