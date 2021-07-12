package com.example.hdrplaybacktest.player

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.hdrplaybacktest.data.VideoInfoHelper
import com.example.hdrplaybacktest.databinding.ActivityExoPlayerBinding
import com.google.android.exoplayer2.SimpleExoPlayer
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class ExoPlayerActivity: AppCompatActivity() {
    private lateinit var binding: ActivityExoPlayerBinding
    private lateinit var uri: Uri
    private val player by lazy {
        ExoPlayerBridge.getPlayer()
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityExoPlayerBinding.inflate(layoutInflater)
        initMedia()
        supportActionBar?.hide()
        with(binding){
            getInfo.setOnClickListener { getCurrentViedeoInfo() }
            exoPlayerView.player = player
            player.play()
        }
        setContentView(binding.root)
    }

    private fun initMedia() {
        intent.extras?.getParcelable<Uri>(EXTRA_URI)?.let {
            ExoPlayerBridge.prepareMediaItem(it)
            player.prepare()
        }
    }

    private fun getCurrentViedeoInfo(){
        player.currentTrackGroups
        player.audioAttributes
        player.mediaMetadata
        player.videoSize
        //nothing
    }


    companion object{
        fun start(context: Context, uri: Uri){
            context.startActivity(Intent(context,ExoPlayerActivity::class.java).apply { putExtra(EXTRA_URI, uri) })
        }
        private const val EXTRA_URI = "extra_uri"
    }
}