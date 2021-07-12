package com.example.hdrplaybacktest.data

import android.content.Context
import android.util.Log
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.map
import java.io.IOException
import javax.inject.Inject
import javax.inject.Singleton


val Context.dataStore by preferencesDataStore("settings")

@Singleton
class Preferences @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private var isFFMPegMetadataEnabled = false
    val ffmpegMetadataEnabledFlow = context.dataStore.data
        .catch { e ->
            if(e is IOException){
                Log.e("Preferences DataStore","IOException")
                e.printStackTrace()
                emit(emptyPreferences())
            }
            else throw e
        }
        .map{
            isFFMPegMetadataEnabled = it[FFMPEG_METADATA_RETRIEVAL_ENABLED]?: false
            isFFMPegMetadataEnabled
        }

    fun isFFMPegMetadataEnabled():Boolean = isFFMPegMetadataEnabled

    suspend fun checkFFMpegMetadataRetriever(){
        context.dataStore.edit {
            it[FFMPEG_METADATA_RETRIEVAL_ENABLED] = it[FFMPEG_METADATA_RETRIEVAL_ENABLED] == false
        }
    }


    companion object{
        val FFMPEG_METADATA_RETRIEVAL_ENABLED = booleanPreferencesKey("ffmpeg_metadata_retrieval_enabled")
    }
}

