package com.android.rickmortyandroid.core.data.db

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.android.rickmortyandroid.feature.characters.data.local.CharacterDao
import com.android.rickmortyandroid.feature.characters.data.local.CharacterDetailDao
import com.android.rickmortyandroid.feature.characters.data.local.CharacterDetailEntity
import com.android.rickmortyandroid.feature.characters.data.local.CharacterEntity
import com.android.rickmortyandroid.feature.characters.data.local.CharacterRemoteKeys
import com.android.rickmortyandroid.feature.characters.data.local.CharacterRemoteKeysDao
import com.android.rickmortyandroid.feature.characters.data.local.EpisodeEntity

@Database(
    entities = [
        CharacterEntity::class,
        CharacterRemoteKeys::class,
        CharacterDetailEntity::class,
        EpisodeEntity::class,
    ],
    version = 2,
    exportSchema = true
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun characterDao(): CharacterDao
    abstract fun characterRemoteKeysDao(): CharacterRemoteKeysDao
    abstract fun characterDetailDao(): CharacterDetailDao

    companion object {
        /**
         * Adds the two new tables introduced in version 2:
         *   - character_detail_cache
         *   - character_detail_episodes
         *
         * Existing character list cache (version 1) is preserved intact.
         */
        val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL(
                    """
                    CREATE TABLE IF NOT EXISTS `character_detail_cache` (
                        `id` INTEGER NOT NULL,
                        `name` TEXT NOT NULL,
                        `status` TEXT NOT NULL,
                        `species` TEXT NOT NULL,
                        `type` TEXT NOT NULL,
                        `gender` TEXT NOT NULL,
                        `origin` TEXT NOT NULL,
                        `location` TEXT NOT NULL,
                        `imageUrl` TEXT NOT NULL,
                        `totalEpisodeCount` INTEGER NOT NULL,
                        `cachedAt` INTEGER NOT NULL,
                        PRIMARY KEY(`id`)
                    )
                    """.trimIndent()
                )
                db.execSQL(
                    """
                    CREATE TABLE IF NOT EXISTS `character_detail_episodes` (
                        `id` INTEGER NOT NULL,
                        `characterId` INTEGER NOT NULL,
                        `name` TEXT NOT NULL,
                        `airDate` TEXT NOT NULL,
                        `episodeCode` TEXT NOT NULL,
                        PRIMARY KEY(`id`),
                        FOREIGN KEY(`characterId`) REFERENCES `character_detail_cache`(`id`)
                            ON DELETE CASCADE
                    )
                    """.trimIndent()
                )
                db.execSQL(
                    "CREATE INDEX IF NOT EXISTS `index_character_detail_episodes_characterId` " +
                        "ON `character_detail_episodes` (`characterId`)"
                )
            }
        }
    }
}
