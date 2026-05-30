package com.android.rickmortyandroid.di

import android.content.Context
import androidx.room.Room
import com.android.rickmortyandroid.core.data.db.AppDatabase
import com.android.rickmortyandroid.feature.characters.data.local.CharacterDao
import com.android.rickmortyandroid.feature.characters.data.local.CharacterDetailDao
import com.android.rickmortyandroid.feature.characters.data.local.CharacterRemoteKeysDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideAppDatabase(@ApplicationContext context: Context): AppDatabase {
        return Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            "rick_morty_database"
        )
            .addMigrations(AppDatabase.MIGRATION_1_2)
            .build()
    }

    @Provides
    @Singleton
    fun provideCharacterDao(appDatabase: AppDatabase): CharacterDao {
        return appDatabase.characterDao()
    }

    @Provides
    @Singleton
    fun provideCharacterRemoteKeysDao(appDatabase: AppDatabase): CharacterRemoteKeysDao {
        return appDatabase.characterRemoteKeysDao()
    }

    @Provides
    @Singleton
    fun provideCharacterDetailDao(appDatabase: AppDatabase): CharacterDetailDao {
        return appDatabase.characterDetailDao()
    }
}
