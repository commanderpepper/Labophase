package commanderpepper.labophase.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import commanderpepper.labophase.data.entity.EntryEntity
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

@Database(entities = [EntryEntity::class, RoundEntity::class], version = 2)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun entryDao(): EntryDao

    companion object {
        fun create(context: Context): AppDatabase =
            Room.databaseBuilder(context, AppDatabase::class.java, "labophase.db")
                .addMigrations(MIGRATION_1_2)
                .build()
    }
}
