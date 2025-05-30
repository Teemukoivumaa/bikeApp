package com.example.bikeapp.di

import android.content.Context
import androidx.room.Room
import com.example.bikeapp.data.local.AppDatabase
import com.example.bikeapp.data.local.SecureStorageManager
import com.example.bikeapp.data.local.migrations.MIGRATION_1_2
import com.example.bikeapp.data.local.migrations.MIGRATION_2_3
import com.example.bikeapp.data.local.migrations.MIGRATION_3_4
import com.example.bikeapp.data.remote.StravaRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideAppDatabase(@ApplicationContext context: Context): AppDatabase {
        return Room.databaseBuilder(
            context,
            AppDatabase::class.java, "bike-app-database"
        ).addMigrations(MIGRATION_1_2, MIGRATION_2_3, MIGRATION_3_4).build()
    }

    @Provides
    @Singleton
    fun provideSecureStorageManager(@ApplicationContext context: Context): SecureStorageManager {
        return SecureStorageManager(context)
    }

    @Provides
    @Singleton
    fun provideStravaRepository(
    ): StravaRepository {
        return StravaRepository()
    }
}
