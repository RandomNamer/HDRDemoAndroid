package com.example.hdrplaybacktest.data

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters

@Database(entities = arrayOf(VideoFileInfo::class), version = 1)
@TypeConverters(UriConverter::class)
abstract class VideoHistoryDatabase: RoomDatabase() {
    abstract fun videoHistoryDao(): VideoHistoryDao


}