package com.example.hdrplaybacktest.ui

import android.app.Activity
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.Display
import androidx.appcompat.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.core.os.bundleOf
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.viewModelScope
import androidx.navigation.findNavController
import com.example.hdrplaybacktest.MainApplication
import com.example.hdrplaybacktest.R
import com.example.hdrplaybacktest.data.DocumentProviderHelper
import com.example.hdrplaybacktest.data.Preferences
import com.example.hdrplaybacktest.data.VideoInfoHelper
import com.example.hdrplaybacktest.databinding.ActivityMainBinding
import com.example.hdrplaybacktest.vm.MainActivityViewModel
import com.example.hdrplaybacktest.vm.VideoListViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private var isDebug = false

    private val viewModel: MainActivityViewModel by viewModels()

    val listViewModel: VideoListViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        VideoInfoHelper.registerContext(this)
        isDebug = (application as MainApplication).isDebug()
        val binding =  ActivityMainBinding.inflate(layoutInflater)
        with(binding){
            setContentView(root)
            setSupportActionBar(toolbar)
            fab.setOnClickListener {
                DocumentProviderHelper.pickVideoFile(this@MainActivity, DocumentProviderHelper.REQ_VIDEO_ADD)
            }
        }
        DocumentProviderHelper.addDocPickResultListener(DocumentProviderHelper.REQ_VIDEO_ADD){ requestCode: Int, intent: Intent? ->
            if(intent == null) return@addDocPickResultListener
            if(requestCode == Activity.RESULT_OK && intent != null){
                handleAddVideoResult(intent)
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_main, menu)
        viewModel.ffmpegMetadataEnabledLiveData.observe(this){
            menu.findItem(R.id.action_enable_ffmpeg_metadata).isChecked = it
        }
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        return when (item.itemId) {
            R.id.action_settings -> true
            R.id.action_check_hdr -> checkHDRCapabilities()
            R.id.action_enable_ffmpeg_metadata ->{
                checkFFMpegMetadataEnabledMenu()
                true
            }
            else -> super.onOptionsItemSelected(item)

        }
    }

    private fun checkHDRCapabilities():Boolean {
        if(display == null) return false
        val hdrInfo = viewModel.getFormattedHdrCapabilities(display!!)
        AlertDialog.Builder(this, R.style.ThemeOverlay_AppCompat_Dialog_Alert).apply {
            setTitle("HDR Capabilities")
            setMessage(hdrInfo)
            setPositiveButton("OK"){ dialogInterface: DialogInterface, _: Int ->
                dialogInterface.dismiss()
            }
            create()
            show()
        }
        return true
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        DocumentProviderHelper.handleActivityResult(requestCode,resultCode, data)
    }

    private fun handleAddVideoResult(intent: Intent){
        val uri = intent.data
        if(isDebug) Log.d(LOGTAG, uri.toString())
        if(uri?.let { DocumentProviderHelper.obtainPersistencePermission(this, it) } == true){
            findNavController(R.id.nav_host_fragment).navigate(
                R.id.action_add_video_to_inspect,
                bundleOf(getString(R.string.video_detail_frag_arg_bundle_name) to uri)
            )
        }
    }

    fun checkFFMpegMetadataEnabledMenu() {
        if (!viewModel.isFFmpegMetadataEnabled)
            android.app.AlertDialog.Builder(this).apply {
                setPositiveButton("Enable") { dialogInterface: DialogInterface, i: Int ->
                    lifecycleScope.launch {
                        viewModel.checkFFMpegMetadataEnable()
                    }
                    dialogInterface.dismiss()
                }
                setNegativeButton("Cancel") { dialog: DialogInterface, _: Int -> dialog.dismiss() }
                setTitle(getString(R.string.confirm_enable_metadata))
                setMessage(getString(R.string.confirm_enable_metadata_info))
                create()
                show()
            }
        else  lifecycleScope.launch {
            viewModel.checkFFMpegMetadataEnable()
        }
    }

    companion object{
        const val LOGTAG = "MainActivity"
    }

    override fun onDestroy() {
        super.onDestroy()
        VideoInfoHelper.unregisterContext()
    }

}