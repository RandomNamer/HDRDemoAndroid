package com.example.hdrplaybacktest

import android.app.Application
import com.example.hdrplaybacktest.player.ExoPlayerBridge
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class MainApplication: Application() {
    val DEBUG = true
    override fun onCreate() {
        super.onCreate()
        ExoPlayerBridge.registerContext(this)
    }

    fun isDebug() = DEBUG

}