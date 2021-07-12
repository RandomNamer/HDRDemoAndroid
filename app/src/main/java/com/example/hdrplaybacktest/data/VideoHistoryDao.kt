package com.example.hdrplaybacktest.data

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface VideoHistoryDao {

    @Query("SELECT * FROM videos")
    fun getVideoList(): Flow<List<VideoFileInfo>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertVideo(video: VideoFileInfo)

    @Update
    fun update(video: VideoFileInfo)

    @Delete
    fun delete(video: VideoFileInfo)

}