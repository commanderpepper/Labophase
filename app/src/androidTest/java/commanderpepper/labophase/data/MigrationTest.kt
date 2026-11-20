package commanderpepper.labophase.data

import androidx.room.testing.MigrationTestHelper
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class MigrationTest {

    @get:Rule
    val helper = MigrationTestHelper(InstrumentationRegistry.getInstrumentation(), AppDatabase::class.java)

    @Test
    fun migrate4To5_keepsExistingRoundsAndAddsEmptyNotes() {
        helper.createDatabase(TEST_DB, 4).apply {
            execSQL(
                "INSERT INTO entries (id, leaderCardId, title, locationId, metaId, date) " +
                    "VALUES (1, 'OP16-022', 'Weekly', 1, 1, '2026-11-01')"
            )
            execSQL(
                "INSERT INTO rounds (id, entryId, roundNumber, leaderCardId, roundResult, turnOrder, dieRoll, opponentId) " +
                    "VALUES (1, 1, 1, 'OP17-020', 'Win', 'First', 'Win', 3)"
            )
            close()
        }

        // Also validates the migrated schema against the exported version 5 schema
        val db = helper.runMigrationsAndValidate(TEST_DB, 5, true, MIGRATION_4_5)

        db.query("SELECT entryId, leaderCardId, roundResult, turnOrder, dieRoll, opponentId, notes FROM rounds WHERE id = 1").use { cursor ->
            assertTrue(cursor.moveToFirst())
            assertEquals(1, cursor.getInt(0))
            assertEquals("OP17-020", cursor.getString(1))
            assertEquals("Win", cursor.getString(2))
            assertEquals("First", cursor.getString(3))
            assertEquals("Win", cursor.getString(4))
            assertEquals(3, cursor.getInt(5))
            assertTrue(cursor.isNull(6))
        }
        db.query("SELECT title FROM entries WHERE id = 1").use { cursor ->
            assertTrue(cursor.moveToFirst())
            assertEquals("Weekly", cursor.getString(0))
        }
    }

    private companion object {
        const val TEST_DB = "migration-test"
    }
}
