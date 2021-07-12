package com.example.hdrplaybacktest.data

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import android.provider.OpenableColumns
import android.text.Spannable
import android.util.Size
import com.example.hdrplaybacktest.SpannableStringBuilderExt
import com.example.hdrplaybacktest.buildSpannable
import com.example.hdrplaybacktest.player.ExoPlayerBridge
import com.google.android.exoplayer2.Format
import com.google.android.exoplayer2.metadata.mp4.MotionPhotoMetadata
import com.google.android.exoplayer2.source.TrackGroupArray
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import wseemann.media.FFmpegMediaMetadataRetriever
import javax.inject.Inject
import javax.inject.Singleton

@SuppressLint("StaticFieldLeak")
object VideoInfoHelper {

    const val EXO_PLAYER_METADATA_RETRIEVE_METHOD = true

    fun registerContext(c: Context) {
        context = c
    }

    fun unregisterContext() {
        context = null
    }

    private var context: Context? = null


    fun getVideoFileData(uri: Uri): VideoFileInfo {
        val vfi = VideoFileInfo(
            uri = uri,
            fileName = "",
            fileSize = 0L,
            mimeType = null
        )
        vfi.mimeType = context!!.contentResolver.getType(uri)
        val cursor = context!!.contentResolver.query(uri, null, null, null)
        if (cursor != null && cursor.moveToFirst()) {
            cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME).let {
                if (it >= 0) vfi.fileName = cursor.getString(it)
            }
            cursor.getColumnIndex(OpenableColumns.SIZE).let {
                if (it >= 0) vfi.fileSize = cursor.getLong(it)
            }
        }
        return vfi
    }

    fun getVideoThumbImage(
        uri: Uri,
        size: Size = Size(300, 300)
    ): Bitmap = context!!.contentResolver.loadThumbnail(uri, size, null)


    fun getAllVideoMetadataFFMpeg(uri: Uri): Map<String?, String?> {
        val mmr = FFmpegMediaMetadataRetriever().apply { setDataSource(context, uri) }
        return mmr.metadata.all
    }

    fun getVideoMetadataForDisplayFFMpeg(metadata: Map<String?, String?>) =
        buildSpannable(SpannableStringBuilderExt.STYLE_NO_SECTION_TITLE) {
            section("") {
                metadata.entries.forEach {
                    if (it.key != null && it.value != null) title(it.key!!) text it.value!!
                }
            }
        }

    private fun getVideoMetadataForDisplayExo(trackGroupArray: TrackGroupArray): Spannable {
        return buildSpannable(SpannableStringBuilderExt.STYLE_NORMAL) {
            for (i in 0 until trackGroupArray.length) {
                val metadata = trackGroupArray[i].getFormat(0)
                if (metadata != null) {
                    section("Track $i") {
                        title("None") text "not implemented"
                    }
                }
            }
        }
    }

    private fun getVideoMetadataForDisplayExo(av: Pair<Format?, Format?>): Spannable {
        return buildSpannable(SpannableStringBuilderExt.STYLE_NORMAL) {
            section("Video") {
                av.first?.let {
                    title("id") text "${it.id}"
                    title("Average Bitrate") text "${it.averageBitrate}"
                    title("Bitrate") text "${it.bitrate}"
                    title("Codecs") text  "${it.codecs}"
                    title("Framerate") text "${it.frameRate}"
                    title("Resolution") text "${it.width} * ${it.height}"
                    title("Pixel Width to Height Ratio") text "${it.pixelWidthHeightRatio}"
                    title("Language") text "${it.language}"
                    title("Sample MIME Type") text "${it.sampleMimeType}"
                }
            }
            section("Audio") {
                av.second?.let {
                    title("id") text "${it.id}"
                    title("Average Bitrate") text "${it.averageBitrate}"
                    title("Bitrate") text "${it.bitrate}"
                    title("Channel Count") text "${it.channelCount}"
                    title("Codecs") text "${it.codecs}"
                    title("Language") text "${it.language}"
                    title("pcmEncoding") text "${it.pcmEncoding}"
                    title("Sample MIME Type") text "${it.sampleMimeType}"
                    title("SampleRate") text "${it.sampleRate}"
                }
            }
        }
    }


    fun getAllVideoMetadataExo(uri: Uri): Flow<Spannable> = flow {
        if (EXO_PLAYER_METADATA_RETRIEVE_METHOD) {
            val avInfo = ExoPlayerBridge.retrieveMetadataWithPlayer(uri)
            emit(getVideoMetadataForDisplayExo(avInfo))
        }else{
            val trackGroupArray = ExoPlayerBridge.retrieveMetadata(uri)
            if (trackGroupArray != null)
                emit(
                    getVideoMetadataForDisplayExo(trackGroupArray)
                )
        }
    }

}