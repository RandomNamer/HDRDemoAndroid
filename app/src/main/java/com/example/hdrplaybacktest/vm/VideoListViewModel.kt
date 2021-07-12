package com.example.hdrplaybacktest.vm;

import android.content.Intent
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.example.hdrplaybacktest.data.VideoFileInfo
import com.example.hdrplaybacktest.data.VideoHistoryDao
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class VideoListViewModel @Inject constructor(
    private val videoHistoryDao: VideoHistoryDao,
    private val state: SavedStateHandle
): ViewModel() {

    private val videoHistoryFlow = videoHistoryDao.getVideoList()
    val videoHistoryLiveData = videoHistoryFlow.asLiveData()

    fun addToHistory(video: VideoFileInfo) = viewModelScope.launch(Dispatchers.IO){
        if(videoHistoryLiveData.value?.contains(video) == false){
            if(videoHistoryLiveData.value?.find{ it.uri == video.uri } != null)
                videoHistoryDao.update(video)
            else videoHistoryDao.insertVideo(video)
        }
    }

    fun deleteVideoEntry(video: VideoFileInfo) = viewModelScope.launch(Dispatchers.IO){
        videoHistoryDao.delete(video)
    }

}
