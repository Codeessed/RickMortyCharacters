package com.android.rickmortyandroid.feature.characters.data.local

import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.android.rickmortyandroid.core.data.db.AppDatabase
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Tests Room DAOs using an in-memory database.
 */
@RunWith(AndroidJUnit4::class)
class CharacterDaoTest {

    // The in-memory database instance — created fresh for each test
    private lateinit var database: AppDatabase

    // The DAOs we're testing
    private lateinit var characterDao: CharacterDao
    private lateinit var remoteKeysDao: CharacterRemoteKeysDao

    @Before
    fun setup() {
        // Create an in-memory database. It only exists during this test.
        // ApplicationProvider.getApplicationContext() gives us the test app's Context.
        database = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            AppDatabase::class.java
        )
            .allowMainThreadQueries()  // OK for tests — no real UI thread
            .build()

        characterDao = database.characterDao()
        remoteKeysDao = database.characterRemoteKeysDao()
    }

    @After
    fun tearDown() {
        // Close the database after each test to free resources
        database.close()
    }

    // ── Reusable test data ──────────────────────────────────────────────

    private fun fakeEntity(id: Int, name: String, page: Int = 1) = CharacterEntity(
        id = id,
        name = name,
        status = "Alive",
        species = "Human",
        imageUrl = "https://example.com/avatar/$id.jpeg",
        page = page
    )

    // ═══════════════════════════════════════════════════════════════════
    //  CharacterDao Tests
    // ═══════════════════════════════════════════════════════════════════

    @Test
    fun insertAll_and_getCount_returnsCorrectCount() = runTest {
        // GIVEN an empty database
        assertEquals(0, characterDao.getCount())

        // WHEN we insert 3 characters
        val characters = listOf(
            fakeEntity(1, "Rick Sanchez"),
            fakeEntity(2, "Morty Smith"),
            fakeEntity(3, "Summer Smith")
        )
        characterDao.insertAll(characters)

        // THEN getCount returns 3
        assertEquals(3, characterDao.getCount())
    }

    @Test
    fun insertAll_withConflict_replacesExisting() = runTest {
        // GIVEN a character in the database
        characterDao.insertAll(listOf(fakeEntity(1, "Rick Sanchez")))

        // WHEN we insert another character with the SAME ID but different name
        // (OnConflictStrategy.REPLACE should overwrite the old one)
        characterDao.insertAll(listOf(fakeEntity(1, "Evil Rick")))

        // THEN count is still 1 (replaced, not duplicated)
        assertEquals(1, characterDao.getCount())
    }

    @Test
    fun clearAll_removesAllCharacters() = runTest {
        // GIVEN characters in the database
        characterDao.insertAll(listOf(
            fakeEntity(1, "Rick"),
            fakeEntity(2, "Morty")
        ))
        assertEquals(2, characterDao.getCount())

        // WHEN we clear all
        characterDao.clearAll()

        // THEN the table is empty
        assertEquals(0, characterDao.getCount())
    }

    // ═══════════════════════════════════════════════════════════════════
    //  CharacterRemoteKeysDao Tests
    // ═══════════════════════════════════════════════════════════════════

    @Test
    fun insertAll_and_remoteKeyByCharacterId_returnsCorrectKey() = runTest {
        // GIVEN remote keys inserted for characters
        val keys = listOf(
            CharacterRemoteKeys(id = 1, prevPage = null, nextPage = 2),
            CharacterRemoteKeys(id = 2, prevPage = 1, nextPage = 3)
        )
        remoteKeysDao.insertAll(keys)

        // WHEN we query by character ID
        val key = remoteKeysDao.remoteKeyByCharacterId(1)

        // THEN we get the correct key with correct prev/next pages
        assertEquals(1, key?.id)
        assertNull(key?.prevPage)        // First page has no prev
        assertEquals(2, key?.nextPage)
    }

    @Test
    fun remoteKeyByCharacterId_returnsNull_whenNotFound() = runTest {
        // GIVEN an empty remote keys table
        // WHEN we query for a non-existent ID
        val key = remoteKeysDao.remoteKeyByCharacterId(999)

        // THEN it returns null (not a crash)
        assertNull(key)
    }

    @Test
    fun clearAll_removesAllRemoteKeys() = runTest {
        // GIVEN keys in the database
        remoteKeysDao.insertAll(listOf(
            CharacterRemoteKeys(id = 1, prevPage = null, nextPage = 2)
        ))

        // WHEN we clear all
        remoteKeysDao.clearAll()

        // THEN querying returns null
        assertNull(remoteKeysDao.remoteKeyByCharacterId(1))
    }
}
