package commanderpepper.labophase.models

import androidx.compose.ui.graphics.Color
import commanderpepper.labophase.ui.theme.OptcgBlack
import commanderpepper.labophase.ui.theme.OptcgBlue
import commanderpepper.labophase.ui.theme.OptcgGreen
import commanderpepper.labophase.ui.theme.OptcgPurple
import commanderpepper.labophase.ui.theme.OptcgRed
import commanderpepper.labophase.ui.theme.OptcgYellow

sealed class Leader(
    val name: String,
    val sets: List<Set>,
    val cardId: String,
    val leaderColors: List<LeaderColor>,
    blockNumber: Int? = null
) {
    val blockNumber: Int = blockNumber ?: sets.maxOf { it.blockNumber }
    val latestSet: Set = sets.maxByOrNull { it.number }!!

    object RGOden : Leader(name = "RG Oden", sets = listOf(Set.EB01), cardId = "EB01-001", leaderColors = listOf(LeaderColor.Red, LeaderColor.Green))
    object UPHannyabal : Leader(name = "UP Hannyabal", sets = listOf(Set.EB01), cardId = "EB01-021", leaderColors = listOf(LeaderColor.Blue, LeaderColor.Purple))
    object BYKyros : Leader(name = "BY Kyros", sets = listOf(Set.EB01), cardId = "EB01-040", leaderColors = listOf(LeaderColor.Black, LeaderColor.Yellow))
    object GPLuffy : Leader(name = "GP Luffy", sets = listOf(Set.EB02), cardId = "EB02-010", leaderColors = listOf(LeaderColor.Green, LeaderColor.Purple))
    object URVivi : Leader(name = "UR Vivi", sets = listOf(Set.EB03), cardId = "EB03-001", leaderColors = listOf(LeaderColor.Blue, LeaderColor.Red))
    object RYBonney : Leader(name = "RY Bonney", sets = listOf(Set.EB04), cardId = "EB04-001", leaderColors = listOf(LeaderColor.Red, LeaderColor.Yellow))
    object RZoro : Leader(name = "R Zoro", sets = listOf(Set.OP01), cardId = "OP01-001", leaderColors = listOf(LeaderColor.Red))
    object RGLaw : Leader(name = "RG Law", sets = listOf(Set.OP01), cardId = "OP01-002", leaderColors = listOf(LeaderColor.Red, LeaderColor.Green))
    object RGLuffy01 : Leader(name = "RG Luffy01", sets = listOf(Set.OP01), cardId = "OP01-003", leaderColors = listOf(LeaderColor.Red, LeaderColor.Green))
    object GOden : Leader(name = "G Oden", sets = listOf(Set.OP01), cardId = "OP01-031", leaderColors = listOf(LeaderColor.Green))
    object UDoflamingo : Leader(name = "U Doflamingo", sets = listOf(Set.OP01, Set.ST17), cardId = "OP01-060", leaderColors = listOf(LeaderColor.Blue))
    object UPKaido : Leader(name = "UP Kaido", sets = listOf(Set.OP01), cardId = "OP01-061", leaderColors = listOf(LeaderColor.Blue, LeaderColor.Purple))
    object UPCrocodile : Leader(name = "UP Crocodile", sets = listOf(Set.OP01), cardId = "OP01-062", leaderColors = listOf(LeaderColor.Blue, LeaderColor.Purple))
    object PKing : Leader(name = "P King", sets = listOf(Set.OP01), cardId = "OP01-091", leaderColors = listOf(LeaderColor.Purple))
    object RNewgate : Leader(name = "R Newgate", sets = listOf(Set.OP02, Set.ST15), cardId = "OP02-001", leaderColors = listOf(LeaderColor.Red))
    object RBGarp : Leader(name = "RB Garp", sets = listOf(Set.OP02), cardId = "OP02-002", leaderColors = listOf(LeaderColor.Red, LeaderColor.Black))
    object GKinemon : Leader(name = "G Kin'emon", sets = listOf(Set.OP02), cardId = "OP02-025", leaderColors = listOf(LeaderColor.Green))
    object UGSanji : Leader(name = "UG Sanji", sets = listOf(Set.OP02), cardId = "OP02-026", leaderColors = listOf(LeaderColor.Blue, LeaderColor.Green))
    object UIvankov : Leader(name = "U Ivankov", sets = listOf(Set.OP02), cardId = "OP02-049", leaderColors = listOf(LeaderColor.Blue))
    object PMagellan : Leader(name = "P Magellan", sets = listOf(Set.OP02), cardId = "OP02-071", leaderColors = listOf(LeaderColor.Purple))
    object PBZephyr : Leader(name = "PB Zephyr", sets = listOf(Set.OP02), cardId = "OP02-072", leaderColors = listOf(LeaderColor.Purple, LeaderColor.Black))
    object BSmoker : Leader(name = "B Smoker", sets = listOf(Set.OP02, Set.ST19), cardId = "OP02-093", leaderColors = listOf(LeaderColor.Black))
    object RAce03 : Leader(name = "R Ace03", sets = listOf(Set.OP03), cardId = "OP03-001", leaderColors = listOf(LeaderColor.Red))
    object GKuro : Leader(name = "G Kuro", sets = listOf(Set.OP03), cardId = "OP03-021", leaderColors = listOf(LeaderColor.Green))
    object GYArlong : Leader(name = "GY Arlong", sets = listOf(Set.OP03), cardId = "OP03-022", leaderColors = listOf(LeaderColor.Green, LeaderColor.Yellow))
    object PIceberg : Leader(name = "P Iceberg", sets = listOf(Set.OP03), cardId = "OP03-058", leaderColors = listOf(LeaderColor.Purple))
    object BLucci03 : Leader(name = "B Lucci03", sets = listOf(Set.OP03), cardId = "OP03-076", leaderColors = listOf(LeaderColor.Black))
    object BYLinlin : Leader(name = "BY Linlin", sets = listOf(Set.OP03), cardId = "OP03-077", leaderColors = listOf(LeaderColor.Black, LeaderColor.Yellow))
    object YKatakuri : Leader(name = "Y Katakuri", sets = listOf(Set.OP03, Set.ST20), cardId = "OP03-099", leaderColors = listOf(LeaderColor.Yellow))
    object URVivi04 : Leader(name = "UR Vivi04", sets = listOf(Set.OP04), cardId = "OP04-001", leaderColors = listOf(LeaderColor.Blue, LeaderColor.Red))
    object GPDoflamingo : Leader(name = "GP Doflamingo", sets = listOf(Set.OP04), cardId = "OP04-019", leaderColors = listOf(LeaderColor.Green, LeaderColor.Purple))
    object GBIssho : Leader(name = "GB Issho", sets = listOf(Set.OP04), cardId = "OP04-020", leaderColors = listOf(LeaderColor.Green, LeaderColor.Black))
    object UBRebecca : Leader(name = "UB Rebecca", sets = listOf(Set.OP04), cardId = "OP04-039", leaderColors = listOf(LeaderColor.Blue, LeaderColor.Black))
    object UYQueen : Leader(name = "UY Queen", sets = listOf(Set.OP04), cardId = "OP04-040", leaderColors = listOf(LeaderColor.Blue, LeaderColor.Yellow))
    object PYCrocodile : Leader(name = "PY Crocodile", sets = listOf(Set.OP04), cardId = "OP04-058", leaderColors = listOf(LeaderColor.Purple, LeaderColor.Yellow))
    object RBSabo05 : Leader(name = "RB Sabo05", sets = listOf(Set.OP05), cardId = "OP05-001", leaderColors = listOf(LeaderColor.Red, LeaderColor.Black))
    object RYBetty : Leader(name = "RY Betty", sets = listOf(Set.OP05), cardId = "OP05-002", leaderColors = listOf(LeaderColor.Red, LeaderColor.Yellow))
    object UGRosinante : Leader(name = "UG Rosinante", sets = listOf(Set.OP05), cardId = "OP05-022", leaderColors = listOf(LeaderColor.Blue, LeaderColor.Green))
    object UBSakazuki : Leader(name = "UB Sakazuki", sets = listOf(Set.OP05), cardId = "OP05-041", leaderColors = listOf(LeaderColor.Blue, LeaderColor.Black))
    object PLuffy : Leader(name = "P Luffy", sets = listOf(Set.OP05, Set.ST18), cardId = "OP05-060", leaderColors = listOf(LeaderColor.Purple))
    object YEnel : Leader(name = "Y Enel", sets = listOf(Set.OP05), cardId = "OP05-098", leaderColors = listOf(LeaderColor.Yellow))
    object RPUta : Leader(name = "RP Uta", sets = listOf(Set.OP06), cardId = "OP06-001", leaderColors = listOf(LeaderColor.Red, LeaderColor.Purple))
    object GHody : Leader(name = "G Hody", sets = listOf(Set.OP06), cardId = "OP06-020", leaderColors = listOf(LeaderColor.Green))
    object GBPerona : Leader(name = "GB Perona", sets = listOf(Set.OP06), cardId = "OP06-021", leaderColors = listOf(LeaderColor.Green, LeaderColor.Black))
    object GYYamato : Leader(name = "GY Yamato", sets = listOf(Set.OP06, Set.ST28), cardId = "OP06-022", leaderColors = listOf(LeaderColor.Green, LeaderColor.Yellow))
    object UPReiju : Leader(name = "UP Reiju", sets = listOf(Set.OP06), cardId = "OP06-042", leaderColors = listOf(LeaderColor.Blue, LeaderColor.Purple))
    object BGecko : Leader(name = "B Gecko", sets = listOf(Set.OP06), cardId = "OP06-080", leaderColors = listOf(LeaderColor.Black))
    object RDragon : Leader(name = "R Dragon", sets = listOf(Set.OP07), cardId = "OP07-001", leaderColors = listOf(LeaderColor.Red))
    object GBonney : Leader(name = "G Bonney", sets = listOf(Set.OP07, Set.ST24), cardId = "OP07-019", leaderColors = listOf(LeaderColor.Green))
    object UBoa : Leader(name = "U Boa", sets = listOf(Set.OP07), cardId = "OP07-038", leaderColors = listOf(LeaderColor.Blue))
    object PFoxy : Leader(name = "P Foxy", sets = listOf(Set.OP07), cardId = "OP07-059", leaderColors = listOf(LeaderColor.Purple))
    object BLucci : Leader(name = "B Lucci", sets = listOf(Set.OP07), cardId = "OP07-079", leaderColors = listOf(LeaderColor.Black))
    object YVegapunk : Leader(name = "Y Vegapunk", sets = listOf(Set.OP07), cardId = "OP07-097", leaderColors = listOf(LeaderColor.Yellow))
    object RGChopper : Leader(name = "RG Chopper", sets = listOf(Set.OP08), cardId = "OP08-001", leaderColors = listOf(LeaderColor.Red, LeaderColor.Green))
    object URMarco : Leader(name = "UR Marco", sets = listOf(Set.OP08), cardId = "OP08-002", leaderColors = listOf(LeaderColor.Blue, LeaderColor.Red))
    object GCarrot : Leader(name = "G Carrot", sets = listOf(Set.OP08), cardId = "OP08-021", leaderColors = listOf(LeaderColor.Green))
    object PBKing : Leader(name = "PB King", sets = listOf(Set.OP08), cardId = "OP08-057", leaderColors = listOf(LeaderColor.Purple, LeaderColor.Black))
    object PYPudding : Leader(name = "PY Pudding", sets = listOf(Set.OP08), cardId = "OP08-058", leaderColors = listOf(LeaderColor.Purple, LeaderColor.Yellow))
    object YKalgara : Leader(name = "Y Kalgara", sets = listOf(Set.OP08), cardId = "OP08-098", leaderColors = listOf(LeaderColor.Yellow))
    object RShanks : Leader(name = "R Shanks", sets = listOf(Set.OP09, Set.ST23), cardId = "OP09-001", leaderColors = listOf(LeaderColor.Red))
    object GPLim : Leader(name = "GP Lim", sets = listOf(Set.OP09), cardId = "OP09-022", leaderColors = listOf(LeaderColor.Green, LeaderColor.Purple))
    object UBuggy09 : Leader(name = "U Buggy09", sets = listOf(Set.OP09, Set.ST25), cardId = "OP09-042", leaderColors = listOf(LeaderColor.Blue))
    object PBLuffy : Leader(name = "PB Luffy", sets = listOf(Set.OP09, Set.ST26), cardId = "OP09-061", leaderColors = listOf(LeaderColor.Purple, LeaderColor.Black))
    object PYRobin : Leader(name = "PY Robin", sets = listOf(Set.OP09), cardId = "OP09-062", leaderColors = listOf(LeaderColor.Purple, LeaderColor.Yellow))
    object BBlackbeard : Leader(name = "B Blackbeard", sets = listOf(Set.OP09, Set.ST27), cardId = "OP09-081", leaderColors = listOf(LeaderColor.Black))
    object RGSmoker : Leader(name = "RG Smoker", sets = listOf(Set.OP10), cardId = "OP10-001", leaderColors = listOf(LeaderColor.Red, LeaderColor.Green))
    object URCaesar : Leader(name = "UR Caesar", sets = listOf(Set.OP10), cardId = "OP10-002", leaderColors = listOf(LeaderColor.Blue, LeaderColor.Red))
    object RPSugar : Leader(name = "RP Sugar", sets = listOf(Set.OP10), cardId = "OP10-003", leaderColors = listOf(LeaderColor.Red, LeaderColor.Purple))
    object GYLaw : Leader(name = "GY Law", sets = listOf(Set.OP10), cardId = "OP10-022", leaderColors = listOf(LeaderColor.Green, LeaderColor.Yellow))
    object UBUsopp : Leader(name = "UB Usopp", sets = listOf(Set.OP10), cardId = "OP10-042", leaderColors = listOf(LeaderColor.Blue, LeaderColor.Black))
    object YKid : Leader(name = "Y Kid", sets = listOf(Set.OP10, Set.ST36), cardId = "OP10-099", leaderColors = listOf(LeaderColor.Yellow))
    object RBKoby : Leader(name = "RB Koby", sets = listOf(Set.OP11), cardId = "OP11-001", leaderColors = listOf(LeaderColor.Red, LeaderColor.Black))
    object GJinbe : Leader(name = "G Jinbe", sets = listOf(Set.OP11), cardId = "OP11-021", leaderColors = listOf(LeaderColor.Green))
    object GYShirahoshi : Leader(name = "GY Shirahoshi", sets = listOf(Set.OP11), cardId = "OP11-022", leaderColors = listOf(LeaderColor.Green, LeaderColor.Yellow))
    object UPLuffy : Leader(name = "UP Luffy", sets = listOf(Set.OP11), cardId = "OP11-040", leaderColors = listOf(LeaderColor.Blue, LeaderColor.Purple))
    object UYNami : Leader(name = "UY Nami", sets = listOf(Set.OP11), cardId = "OP11-041", leaderColors = listOf(LeaderColor.Blue, LeaderColor.Yellow))
    object PKatakuri : Leader(name = "P Katakuri", sets = listOf(Set.OP11, Set.ST34), cardId = "OP11-062", leaderColors = listOf(LeaderColor.Purple))
    object RRayleigh : Leader(name = "R Rayleigh", sets = listOf(Set.OP12), cardId = "OP12-001", leaderColors = listOf(LeaderColor.Red))
    object GZoro : Leader(name = "G Zoro", sets = listOf(Set.OP12, Set.ST32), cardId = "OP12-020", leaderColors = listOf(LeaderColor.Green))
    object UKuzan : Leader(name = "U Kuzan", sets = listOf(Set.OP12, Set.ST33), cardId = "OP12-040", leaderColors = listOf(LeaderColor.Blue))
    object UPSanji : Leader(name = "UP Sanji", sets = listOf(Set.OP12), cardId = "OP12-041", leaderColors = listOf(LeaderColor.Blue, LeaderColor.Purple))
    object PYRosinante : Leader(name = "PY Rosinante", sets = listOf(Set.OP12), cardId = "OP12-061", leaderColors = listOf(LeaderColor.Purple, LeaderColor.Yellow))
    object BYKoala : Leader(name = "BY Koala", sets = listOf(Set.OP12), cardId = "OP12-081", leaderColors = listOf(LeaderColor.Black, LeaderColor.Yellow))
    object RGLuffy : Leader(name = "RG Luffy", sets = listOf(Set.OP13), cardId = "OP13-001", leaderColors = listOf(LeaderColor.Red, LeaderColor.Green))
    object URAce : Leader(name = "UR Ace", sets = listOf(Set.OP13), cardId = "OP13-002", leaderColors = listOf(LeaderColor.Blue, LeaderColor.Red))
    object RPRoger : Leader(name = "RP Roger", sets = listOf(Set.OP13), cardId = "OP13-003", leaderColors = listOf(LeaderColor.Red, LeaderColor.Purple))
    object RBSabo : Leader(name = "RB Sabo", sets = listOf(Set.OP13, Set.ST35), cardId = "OP13-004", leaderColors = listOf(LeaderColor.Red, LeaderColor.Black))
    object BImu : Leader(name = "B Imu", sets = listOf(Set.OP13), cardId = "OP13-079", leaderColors = listOf(LeaderColor.Black))
    object YBonney : Leader(name = "Y Bonney", sets = listOf(Set.OP13), cardId = "OP13-100", leaderColors = listOf(LeaderColor.Yellow))
    object UYBoa : Leader(name = "UY Boa", sets = listOf(Set.OP14), cardId = "OP14-041", leaderColors = listOf(LeaderColor.Blue, LeaderColor.Yellow))
    object GMihawk : Leader(name = "G Mihawk", sets = listOf(Set.OP14), cardId = "OP14-020", leaderColors = listOf(LeaderColor.Green))
    object UJinbe : Leader(name = "U Jinbe", sets = listOf(Set.OP14), cardId = "OP14-040", leaderColors = listOf(LeaderColor.Blue))
    object RLaw : Leader(name = "R Law", sets = listOf(Set.OP14), cardId = "OP14-001", leaderColors = listOf(LeaderColor.Red))
    object PDoflamingo : Leader(name = "P Doflamingo", sets = listOf(Set.OP14), cardId = "OP14-060", leaderColors = listOf(LeaderColor.Purple))
    object BYGecko : Leader(name = "BY Gecko", sets = listOf(Set.OP14), cardId = "OP14-080", leaderColors = listOf(LeaderColor.Black, LeaderColor.Yellow))
    object BCrocodile : Leader(name = "B Crocodile", sets = listOf(Set.OP14), cardId = "OP14-079", leaderColors = listOf(LeaderColor.Black))
    object YLuffy : Leader(name = "Y Luffy", sets = listOf(Set.OP15), cardId = "OP15-098", leaderColors = listOf(LeaderColor.Yellow))
    object PEnel : Leader(name = "P Enel", sets = listOf(Set.OP15), cardId = "OP15-058", leaderColors = listOf(LeaderColor.Purple))
    object URebecca : Leader(name = "U Rebecca", sets = listOf(Set.OP15), cardId = "OP15-039", leaderColors = listOf(LeaderColor.Blue))
    object GBBrook : Leader(name = "GB Brook", sets = listOf(Set.OP15), cardId = "OP15-022", leaderColors = listOf(LeaderColor.Green, LeaderColor.Black))
    object RGKrieg : Leader(name = "RG Krieg", sets = listOf(Set.OP15), cardId = "OP15-001", leaderColors = listOf(LeaderColor.Red, LeaderColor.Green))
    object URLucy : Leader(name = "UR Lucy", sets = listOf(Set.OP15), cardId = "OP15-002", leaderColors = listOf(LeaderColor.Blue, LeaderColor.Red))
    object PSengoku : Leader(name = "P Sengoku", sets = listOf(Set.OP16), cardId = "OP16-060", leaderColors = listOf(LeaderColor.Purple))
    object BYamato : Leader(name = "B Yamato", sets = listOf(Set.OP16), cardId = "OP16-079", leaderColors = listOf(LeaderColor.Black))
    object UBuggy : Leader(name = "U Buggy", sets = listOf(Set.OP16), cardId = "OP16-041", leaderColors = listOf(LeaderColor.Blue))
    object BYBlackbeard : Leader(name = "BY Blackbeard", sets = listOf(Set.OP16), cardId = "OP16-080", leaderColors = listOf(LeaderColor.Black, LeaderColor.Yellow))
    object RAce : Leader(name = "R Ace", sets = listOf(Set.OP16), cardId = "OP16-001", leaderColors = listOf(LeaderColor.Red))
    object UGLuffy : Leader(name = "UG Luffy", sets = listOf(Set.OP16), cardId = "OP16-022", leaderColors = listOf(LeaderColor.Blue, LeaderColor.Green))
    object ULuffy : Leader(name = "U Luffy", sets = listOf(Set.PROMOS), cardId = "P-047", leaderColors = listOf(LeaderColor.Blue), blockNumber = 2)
    object UBSakazukiP : Leader(name = "UB SakazukiP", sets = listOf(Set.PROMOS), cardId = "P-076", leaderColors = listOf(LeaderColor.Blue, LeaderColor.Black), blockNumber = 2)
    object RPLawP : Leader(name = "RP LawP", sets = listOf(Set.PROMOS), cardId = "P-086", leaderColors = listOf(LeaderColor.Red, LeaderColor.Purple), blockNumber = 3)
    object UNami : Leader(name = "U Nami", sets = listOf(Set.PROMOS), cardId = "P-117", leaderColors = listOf(LeaderColor.Blue), blockNumber = 4)
    object RSanji : Leader(name = "R Sanji", sets = listOf(Set.PRB01), cardId = "PRB01-001", leaderColors = listOf(LeaderColor.Red))
    object RLuffyST01 : Leader(name = "R LuffyST01", sets = listOf(Set.ST01), cardId = "ST01-001", leaderColors = listOf(LeaderColor.Red))
    object GKid : Leader(name = "G Kid", sets = listOf(Set.ST02), cardId = "ST02-001", leaderColors = listOf(LeaderColor.Green))
    object UCrocodile : Leader(name = "U Crocodile", sets = listOf(Set.ST03), cardId = "ST03-001", leaderColors = listOf(LeaderColor.Blue))
    object PKaido : Leader(name = "P Kaido", sets = listOf(Set.ST04), cardId = "ST04-001", leaderColors = listOf(LeaderColor.Purple))
    object PShanks : Leader(name = "P Shanks", sets = listOf(Set.ST05), cardId = "ST05-001", leaderColors = listOf(LeaderColor.Purple))
    object BSakazuki : Leader(name = "B Sakazuki", sets = listOf(Set.ST06), cardId = "ST06-001", leaderColors = listOf(LeaderColor.Black))
    object YLinlin : Leader(name = "Y Linlin", sets = listOf(Set.ST07), cardId = "ST07-001", leaderColors = listOf(LeaderColor.Yellow))
    object BLuffyST08 : Leader(name = "B LuffyST08", sets = listOf(Set.ST08), cardId = "ST08-001", leaderColors = listOf(LeaderColor.Black))
    object YYamato : Leader(name = "Y Yamato", sets = listOf(Set.ST09), cardId = "ST09-001", leaderColors = listOf(LeaderColor.Yellow))
    object RPLaw : Leader(name = "RP Law", sets = listOf(Set.ST10), cardId = "ST10-001", leaderColors = listOf(LeaderColor.Red, LeaderColor.Purple))
    object RPLuffy : Leader(name = "RP Luffy", sets = listOf(Set.ST10), cardId = "ST10-002", leaderColors = listOf(LeaderColor.Red, LeaderColor.Purple))
    object RPKid : Leader(name = "RP Kid", sets = listOf(Set.ST10), cardId = "ST10-003", leaderColors = listOf(LeaderColor.Red, LeaderColor.Purple))
    object GUta : Leader(name = "G Uta", sets = listOf(Set.ST11, Set.ST16), cardId = "ST11-001", leaderColors = listOf(LeaderColor.Green))
    object UGZonji : Leader(name = "UG Zonji", sets = listOf(Set.ST12), cardId = "ST12-001", leaderColors = listOf(LeaderColor.Blue, LeaderColor.Green))
    object RYSabo : Leader(name = "RY Sabo", sets = listOf(Set.ST13), cardId = "ST13-001", leaderColors = listOf(LeaderColor.Red, LeaderColor.Yellow))
    object UYAce : Leader(name = "UY Ace", sets = listOf(Set.ST13), cardId = "ST13-002", leaderColors = listOf(LeaderColor.Blue, LeaderColor.Yellow))
    object BYLuffy : Leader(name = "BY Luffy", sets = listOf(Set.ST13), cardId = "ST13-003", leaderColors = listOf(LeaderColor.Black, LeaderColor.Yellow))
    object BLuffy : Leader(name = "B Luffy", sets = listOf(Set.ST14), cardId = "ST14-001", leaderColors = listOf(LeaderColor.Black))
    object RLuffy : Leader(name = "R Luffy", sets = listOf(Set.ST21, Set.ST31), cardId = "ST21-001", leaderColors = listOf(LeaderColor.Red))
    object UAceNewgate : Leader(name = "U AceNewgate", sets = listOf(Set.ST22), cardId = "ST22-001", leaderColors = listOf(LeaderColor.Blue))
    object YLuffyST29 : Leader(name = "Y LuffyST29", sets = listOf(Set.ST29), cardId = "ST29-001", leaderColors = listOf(LeaderColor.Yellow))
    object RGLuffyAce : Leader(name = "RG LuffyAce", sets = listOf(Set.ST30), cardId = "ST30-001", leaderColors = listOf(LeaderColor.Red, LeaderColor.Green))
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
    object ST31 : Set(name = "RED Monkey.D.Luffy", code = "ST31", number = 53, blockNumber = 5)
    object ST32 : Set(name = "GREEN Roronoa Zoro", code = "ST32", number = 54, blockNumber = 5)
    object ST33 : Set(name = "BLUE Kuzan", code = "ST33", number = 55, blockNumber = 5)
    object ST34 : Set(name = "PURPLE Charlotte Katakuri", code = "ST34", number = 56, blockNumber = 5)
    object ST35 : Set(name = "RED/BLACK Sabo", code = "ST35", number = 57, blockNumber = 5)
    object ST36 : Set(name = "YELLOW Eustass\"Captain\"Kid", code = "ST36", number = 58, blockNumber = 5)
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
    Leader.RZoro,
    Leader.RGLaw,
    Leader.RGLuffy01,
    Leader.GOden,
    Leader.UDoflamingo,
    Leader.UPKaido,
    Leader.UPCrocodile,
    Leader.PKing,
    Leader.RNewgate,
    Leader.RBGarp,
    Leader.GKinemon,
    Leader.UGSanji,
    Leader.UIvankov,
    Leader.PMagellan,
    Leader.PBZephyr,
    Leader.BSmoker,
    Leader.RAce03,
    Leader.GKuro,
    Leader.GYArlong,
    Leader.PIceberg,
    Leader.BLucci03,
    Leader.BYLinlin,
    Leader.YKatakuri,
    Leader.URVivi04,
    Leader.GPDoflamingo,
    Leader.GBIssho,
    Leader.UBRebecca,
    Leader.UYQueen,
    Leader.PYCrocodile,
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
    Leader.RLuffyST01,
    Leader.GKid,
    Leader.UCrocodile,
    Leader.PKaido,
    Leader.PShanks,
    Leader.BSakazuki,
    Leader.YLinlin,
    Leader.BLuffyST08,
    Leader.YYamato,
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

fun leaderByCardId(cardId: String): Leader = LEADERS_LIST.firstOrNull { it.cardId == cardId } ?: Leader.PLuffy
