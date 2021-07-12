package com.example.hdrplaybacktest.data

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import dagger.hilt.android.qualifiers.ActivityContext
import dagger.hilt.android.scopes.ActivityScoped
import wseemann.media.FFmpegMediaMetadataRetriever


@SuppressLint("StaticFieldLeak")
@ActivityScoped
object DocumentProviderHelper {


    private val onDocPickResultListener = mutableMapOf<Int, (Int, Intent?) -> Unit>()
    
    const val REQ_VIDEO_ADD = 1
    const val REQ_VIDEO_LISTFRAGMENT = 2
    const val REQ_VIDEO_DETAILFRAGMENT = 3


    fun handleActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        onDocPickResultListener[requestCode]?.invoke(resultCode, data)
    }

    private fun handleAddVideoResult(requestCode: Int, data: Intent?) {
        TODO("Not yet implemented")
    }

    fun pickVideoFile(activity: Activity, requestCode: Int){
        val intent = Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
            addCategory(Intent.CATEGORY_OPENABLE)
            setType("video/*")
        }
        activity.startActivityForResult(intent, requestCode)
    }

    fun addDocPickResultListener(requestType: Int, l: (requestCode: Int, data: Intent?) -> Unit){
        onDocPickResultListener[requestType] = l
    }

    fun getFileInfoFromUri(@ActivityContext activity: Activity? = null, uri: Uri) {
        val mmr = FFmpegMediaMetadataRetriever().apply { setDataSource(activity, uri) }

    }

    fun obtainPersistencePermission(activity: Activity, document: Uri): Boolean {
        var permissionGranted = false
        val perms = (Intent.FLAG_GRANT_READ_URI_PERMISSION
                or Intent.FLAG_GRANT_WRITE_URI_PERMISSION)
        try {
            activity.contentResolver
                .takePersistableUriPermission(document, perms)
            for (perm in activity.contentResolver.persistedUriPermissions) {
                if (perm.uri == document) {
                    permissionGranted = true
                }
            }
        } catch (e: SecurityException) {
            Toast.makeText(activity, "Fuck, why don't you give any persistable permissions?",Toast.LENGTH_SHORT).show()
        }
        return permissionGranted
    }


}