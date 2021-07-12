package com.example.hdrplaybacktest.ui

import android.app.AlertDialog
import android.content.ComponentName
import android.content.DialogInterface
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.addCallback
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.hdrplaybacktest.R
import com.example.hdrplaybacktest.SpannableStringBuilderExt
import com.example.hdrplaybacktest.buildSpannable
import com.example.hdrplaybacktest.data.VideoInfoHelper
import com.example.hdrplaybacktest.databinding.CardVideoBasicInfoBinding
import com.example.hdrplaybacktest.databinding.CardVideoInfoMetadataBinding
import com.example.hdrplaybacktest.databinding.FragmentSecondBinding
import com.example.hdrplaybacktest.asFileSizeString
import com.example.hdrplaybacktest.data.Preferences
import com.example.hdrplaybacktest.player.ExoPlayerActivity
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * A simple [Fragment] subclass as the second destination in the navigation.
 */

@AndroidEntryPoint
class VideoDetailFragment() : Fragment() {

    @Inject lateinit var preferences: Preferences
    private lateinit var binding: FragmentSecondBinding
    private lateinit var basicCardBinding: CardVideoBasicInfoBinding
    private lateinit var ffmpegMetadataCardBinding: CardVideoInfoMetadataBinding
    private lateinit var exoMetadataBinding: CardVideoInfoMetadataBinding
    private val uri by lazy {
        arguments?.get(getString(R.string.video_detail_frag_arg_bundle_name)) as? Uri
    }
    private val videoFileInfo by lazy{
        if(uri == null) null
        else VideoInfoHelper.getVideoFileData(uri!!)
    }
    private val listViewModel by lazy {
        (activity as MainActivity).listViewModel
    }

    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentSecondBinding.inflate(inflater, container, false)
        basicCardBinding = binding.basicInfoCard
        ffmpegMetadataCardBinding = binding.metadataInfoCardFfmpeg
        exoMetadataBinding = binding.metadataInfoCardExo
        return binding.root
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val callback = requireActivity().onBackPressedDispatcher.addCallback(this){
            handleOnBackPressed()
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        with(binding){
            detailPageBack.background = null
            detailPageBack.setOnClickListener{
                handleBackPressed()
            }
            buttonPlay.setOnClickListener { showPlaySelectDialog()  }
        }
        with(basicCardBinding){
            openInMediainfo.visibility = View.VISIBLE
            detailPageBasicCardTitleTv.text = videoFileInfo?.fileName?:"Error"
            detailPageBasicCardInfoTv.text = videoFileInfo?.run {
                buildSpannable(SpannableStringBuilderExt.STYLE_NO_SECTION_TITLE){
                    section(""){
                        title(getString(R.string.file_size)) text fileSize.asFileSizeString
                        title(getString(R.string.media_type)) text (mimeType?:"Unknown")
                    }
                }
            }?:""
            detailPageBasicCardTitleTv.post {
                detailPageBasicCardCoverIv.setImageBitmap(
                    uri?.let{
                        VideoInfoHelper.getVideoThumbImage(it)
                    }
                )
            }
            openInMediainfo.setOnClickListener{ fireIntentToMediaInfo() }
        }
        lifecycleScope.launchWhenResumed {
            preferences.ffmpegMetadataEnabledFlow.collect {
                ffmpegMetadataCardBinding.apply {
                    root.visibility = if(it) View.VISIBLE else View.GONE
                    detailPageMetadataCardTitleTv.text = getString(R.string.metadata_title)
                    if(it) detailPageMetadataCardInfoTv.post {
                        uri?.let {
                            val metadata = VideoInfoHelper.getAllVideoMetadataFFMpeg(uri!!)
                            detailPageMetadataCardInfoTv.text =
                                    VideoInfoHelper.getVideoMetadataForDisplayFFMpeg(metadata)
                        }
                    }
                }
            }
        }
        with(exoMetadataBinding){
            detailPageMetadataCardCourtesyTv.visibility = View.GONE
            detailPageMetadataCardTitleTv.text = getString(R.string.metadata_title)
            lifecycleScope.launch {
                VideoInfoHelper.getAllVideoMetadataExo(uri!!).collect { detailPageMetadataCardInfoTv.text = it } }
        }
    }

    private fun showPlaySelectDialog(){
        context?.let{
            AlertDialog.Builder(it).apply {
                setTitle(getString(R.string.select_play_source))
                setItems(arrayOf(
                    getString(R.string.open_with_exo),
                    getString(R.string.open_with_vlc),
                    getString(R.string.open_with_other)
                )) { _: DialogInterface, i: Int ->
                    when(i){
                        0 -> openWithInternalExoPlayer()
                        1 -> openWithVLC()
                        2 -> openWithSystemDialog()
                    }
                }
                create()
                show()
            }
        }

    }
    private fun openWithInternalExoPlayer(){
        uri?.let {
            ExoPlayerActivity.start(requireActivity(), it)
        }
    }

    private fun openWithVLC(){
        try {
            uri?.let {
                startActivity(
                    Intent(Intent.ACTION_VIEW).apply {
                        setPackage("org.videolan.vlc");
                        setDataAndTypeAndNormalize(it, "video/*")
//                        putExtra("title", "From my app")
                    }
                )
            }
        }catch (e: Exception){
            Toast.makeText(activity,"Open VLC failed",Toast.LENGTH_SHORT).show()
            e.printStackTrace()
        }
    }

    private fun openWithSystemDialog(){
        uri?.let {
            val intent = Intent(Intent.ACTION_VIEW).apply {
                setDataAndTypeAndNormalize(it, "video/*")
            }
            startActivity(Intent.createChooser(intent,"Choose an app to open"))
        }
    }

    private fun fireIntentToMediaInfo(){
        Intent().apply {
            setClassName("net.mediaarea.mediainfo","net.mediaarea.mediainfo.ReportListActivity")
            action = Intent.ACTION_SEND
            putExtra(Intent.EXTRA_STREAM, uri)
            flags = Intent.FLAG_ACTIVITY_NEW_TASK
        }.let {
            try {
                startActivity(it)
            }catch (e: Exception){
                Toast.makeText(activity, "Starting Activity Error", Toast.LENGTH_SHORT).show()
            }
        }
    }

    fun handleBackPressed(){
        videoFileInfo?.let { listViewModel.addToHistory(it) }
        findNavController().navigate(R.id.action_SecondFragment_to_FirstFragment)
    }

}