package com.example.hdrplaybacktest.data

import android.net.Uri
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverter

@Entity(tableName = "videos")
data class VideoFileInfo(
    @PrimaryKey val uri: Uri,
    var fileName: String,
    @ColumnInfo(name = "file_size") var fileSize: Long,
    var mimeType: String?
)

class UriConverter{
    @TypeConverter
    fun fromString(s:String): Uri{
        return Uri.parse(s)
    }

    @TypeConverter
    fun toString(uri: Uri): String{
        return uri.toString()
    }
}
