package commanderpepper.labophase.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import commanderpepper.labophase.data.entity.EntryEntity
import commanderpepper.labophase.data.entity.OpponentEntity
import commanderpepper.labophase.data.entity.RoundEntity

val MIGRATION_1_2 = object : Migration(1, 2) {
    override fun migrate(database: SupportSQLiteDatabase) {
        database.execSQL("ALTER TABLE entries ADD COLUMN title TEXT")
        database.execSQL("ALTER TABLE entries ADD COLUMN locationId INTEGER")
        database.execSQL("ALTER TABLE entries ADD COLUMN metaId INTEGER")
        database.execSQL("ALTER TABLE entries ADD COLUMN date TEXT")
        database.execSQL("UPDATE entries SET metaId = 1 WHERE metaId IS NULL")
        database.execSQL("UPDATE entries SET date = date('now') WHERE date IS NULL")
    }
}

val MIGRATION_2_3 = object : Migration(2, 3) {
    override fun migrate(database: SupportSQLiteDatabase) {
        database.execSQL("ALTER TABLE rounds ADD COLUMN opponent TEXT")
    }
}

val MIGRATION_3_4 = object : Migration(3, 4) {
    override fun migrate(database: SupportSQLiteDatabase) {
        database.execSQL(
            "CREATE TABLE IF NOT EXISTS `opponents` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `name` TEXT NOT NULL, `nickname` TEXT NOT NULL)"
        )
        database.execSQL(
            "INSERT INTO `opponents` (`name`, `nickname`) VALUES " +
            "('10000orbs', 'riley'), ('yz', 'Dbandit'), ('atran23.', 'ponder'), " +
            "('bolbyy', 'bolby'), ('bphimmasone', 'Ben'), ('commanderpepper', 'Pepper'), " +
            "('cozytony', 'cozytony'), ('derpinic', 'Dom'), ('jamericano', 'JAM!'), " +
            "('kevindu', 'KEV'), ('moondoggyhero', 'moondoggy'), ('nathanahn', 'ahn'), " +
            "('oldtuna', 'oncloud2'), ('oncloud', 'on.D.cloud'), ('pierre0019', 'pierre'), " +
            "('scooterd22', 'scooter'), ('tamdin54', 'tamdin'), ('tftren', 'oncloud3'), " +
            "('zayuh03', 'ZAYUH')"
        )
        database.execSQL(
            "CREATE TABLE IF NOT EXISTS `rounds_new` (" +
            "`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, " +
            "`entryId` INTEGER NOT NULL, " +
            "`roundNumber` INTEGER NOT NULL, " +
            "`leaderCardId` TEXT NOT NULL, " +
            "`roundResult` TEXT NOT NULL, " +
            "`turnOrder` TEXT NOT NULL, " +
            "`dieRoll` TEXT, " +
            "`opponentId` INTEGER, " +
            "FOREIGN KEY(`entryId`) REFERENCES `entries`(`id`) ON UPDATE NO ACTION ON DELETE CASCADE)"
        )
        database.execSQL(
            "INSERT INTO `rounds_new` (`id`, `entryId`, `roundNumber`, `leaderCardId`, `roundResult`, `turnOrder`, `dieRoll`, `opponentId`) " +
            "SELECT `id`, `entryId`, `roundNumber`, `leaderCardId`, `roundResult`, `turnOrder`, `dieRoll`, NULL FROM `rounds`"
        )
        database.execSQL("DROP TABLE `rounds`")
        database.execSQL("ALTER TABLE `rounds_new` RENAME TO `rounds`")
        database.execSQL("CREATE INDEX IF NOT EXISTS `index_rounds_entryId` ON `rounds` (`entryId`)")
    }
}

@Database(entities = [EntryEntity::class, RoundEntity::class, OpponentEntity::class], version = 4)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun entryDao(): EntryDao
    abstract fun opponentDao(): OpponentDao

    companion object {
        fun create(context: Context): AppDatabase =
            Room.databaseBuilder(context, AppDatabase::class.java, "labophase.db")
                .addMigrations(MIGRATION_1_2, MIGRATION_2_3, MIGRATION_3_4)
                .build()
    }
}
