package com.example.hdrplaybacktest.player

import android.annotation.SuppressLint
import android.content.Context
import android.net.Uri
import android.util.Log
import com.example.hdrplaybacktest.MainApplication
import com.google.android.exoplayer2.*
import com.google.android.exoplayer2.metadata.Metadata
import com.google.android.exoplayer2.metadata.MetadataOutput
import com.google.android.exoplayer2.source.TrackGroupArray
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector
import kotlinx.coroutines.GlobalScope.coroutineContext
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.guava.await
import java.util.concurrent.Executors
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.coroutines.resumeWithException

@SuppressLint("StaticFieldLeak")
object ExoPlayerBridge: MetadataOutput{
    private lateinit var context: Context
    fun registerContext(cxt: Context){
        context = cxt
    }

    private var mediaItem: MediaItem? = null
    private val executor = Executors.newSingleThreadExecutor()
    private val trackSelector by lazy {
        DefaultTrackSelector(context)
    }
    private val player by lazy {
        SimpleExoPlayer.Builder(context)
            .setTrackSelector(trackSelector)
            .build()
    }
    private lateinit var currentMetadata: Metadata
    fun getMediaItem():MediaItem {
        if(mediaItem == null )
            if((context as MainApplication).isDebug()) Log.d("ExoPlayerBridge", "getting null mediaInfo")
        return mediaItem?:MediaItem.Builder().build()
    }

    fun prepareMediaItem(uri: Uri){
        mediaItem = MediaItem.fromUri(uri)
        player.setMediaItem(mediaItem!!)
    }

    fun getPlayer(): Player = player

    suspend fun retrieveMetadataWithPlayer(uri: Uri):Pair<Format?,Format?> = coroutineScope{
        val mi = MediaItem.fromUri(uri)
        player.setMediaItem(mi)
        player.prepare()
        player.play()
        player.pause()
        delay(1000)
        Pair(player.videoFormat, player.audioFormat)
    }



    suspend fun retrieveMetadata(uri: Uri): TrackGroupArray {
        val mi = MediaItem.fromUri(uri)
        val listenableFuture = MetadataRetriever.retrieveMetadata(context,mi)
        return listenableFuture.await()
    }

    override fun onMetadata(metadata: Metadata) {
        currentMetadata = metadata
    }

}