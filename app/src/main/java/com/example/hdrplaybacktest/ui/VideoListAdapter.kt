package com.example.hdrplaybacktest.ui

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.hdrplaybacktest.R
import com.example.hdrplaybacktest.asFileSizeString
import com.example.hdrplaybacktest.buildSpannable
import com.example.hdrplaybacktest.data.VideoFileInfo
import com.example.hdrplaybacktest.data.VideoInfoHelper
import com.example.hdrplaybacktest.databinding.CardVideoBasicInfoBinding

class VideoListAdapter: ListAdapter<VideoFileInfo, VideoListAdapter.ViewHolder>(
    object : DiffUtil.ItemCallback<VideoFileInfo>() {
        override fun areItemsTheSame(oldItem: VideoFileInfo, newItem: VideoFileInfo) = oldItem == newItem
        override fun areContentsTheSame(oldItem: VideoFileInfo, newItem: VideoFileInfo) = oldItem.fileName == oldItem.fileName
    }
) {
    private var onClickListener: ((VideoFileInfo) -> Unit)? = null

    fun setOnClickListener(l: ((VideoFileInfo) -> Unit)?){
        onClickListener = l
    }

    inner class ViewHolder(private val binding: CardVideoBasicInfoBinding) : RecyclerView.ViewHolder(binding.root){
        fun bind(video: VideoFileInfo){
            with(binding){
                root.setOnClickListener {
                    val position = adapterPosition
                    if(position != RecyclerView.NO_POSITION)
                        onClickListener?.invoke(getItem(position))
                }
                detailPageBasicCardTitleTv.text = video.fileName?:"Error"
                detailPageBasicCardInfoTv.text = video.run {
                    buildSpannable(com.example.hdrplaybacktest.SpannableStringBuilderExt.STYLE_NO_SECTION_TITLE){
                        section(""){
                            title(itemView.context.getString(R.string.file_size)) text fileSize.asFileSizeString
                            title(itemView.context.getString(R.string.media_type)) text (mimeType?:"Unknown")
                        }
                    }
                }
                detailPageBasicCardTitleTv.post {
                    detailPageBasicCardCoverIv.setImageBitmap(
                        video.uri.let{
                            VideoInfoHelper.getVideoThumbImage(it)
                        }
                    )
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = CardVideoBasicInfoBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }
}