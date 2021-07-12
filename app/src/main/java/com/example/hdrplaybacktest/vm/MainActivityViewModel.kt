package com.example.hdrplaybacktest.vm

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.view.Display
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.example.hdrplaybacktest.data.Preferences
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ActivityContext
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
@SuppressLint("StaticFieldLeak")
class MainActivityViewModel @Inject constructor(
    private val preferences: Preferences,
     @ApplicationContext private val applicationContext: Context
): ViewModel() {

    val ffmpegMetadataEnabledLiveData  = preferences.ffmpegMetadataEnabledFlow.asLiveData()

    val isFFmpegMetadataEnabled: Boolean
        get() = preferences.isFFMPegMetadataEnabled()

    suspend fun checkFFMpegMetadataEnable(){
        preferences.checkFFMpegMetadataRetriever()
    }

    fun getFormattedHdrCapabilities(display: Display):String{
        return display.hdrCapabilities.let{
            com.example.hdrplaybacktest.buildString {
                +"Desired Max Average Luminance: ${it.desiredMaxAverageLuminance.toInt()}nits"
                +"Desired Max Luminance: ${it.desiredMaxLuminance.toInt()}nits"
                +"Desired Min Luminance: ${it.desiredMinLuminance}nit"
                if (it.supportedHdrTypes.isNotEmpty()) {
                    +"Supported HDR Types:"
                    it.supportedHdrTypes.forEach { type ->
                        when (type) {
                            Display.HdrCapabilities.HDR_TYPE_DOLBY_VISION -> +"Dolby Vision"
                            Display.HdrCapabilities.HDR_TYPE_HDR10 -> +"HDR10"
                            Display.HdrCapabilities.HDR_TYPE_HDR10_PLUS -> +"HDR10+"
                            Display.HdrCapabilities.HDR_TYPE_HLG -> +"HLG"
                        }
                    }
                } else +"This Devices does not support HDR Playback"
            }
        }
    }


}