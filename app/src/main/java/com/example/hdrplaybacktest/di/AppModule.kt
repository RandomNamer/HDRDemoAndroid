package com.example.hdrplaybacktest.di

import android.app.Application
import android.content.Context
import androidx.room.Room
import com.example.hdrplaybacktest.data.VideoHistoryDao
import com.example.hdrplaybacktest.data.VideoHistoryDatabase
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
    fun provideDb(
        @ApplicationContext context: Context
    ): VideoHistoryDatabase =
        Room.databaseBuilder(context, VideoHistoryDatabase::class.java,"video_history_database").build()

    @Provides
    fun provideDao(db: VideoHistoryDatabase) = db.videoHistoryDao()

}

