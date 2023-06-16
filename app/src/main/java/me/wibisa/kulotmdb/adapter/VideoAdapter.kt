package me.wibisa.kulotmdb.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import me.wibisa.kulotmdb.core.data.remote.response.Video
import me.wibisa.kulotmdb.core.util.getYoutubeThumbnailPath
import me.wibisa.kulotmdb.core.util.loadBackdrop
import me.wibisa.kulotmdb.databinding.ItemVideoBinding

class VideoAdapter(private val clickListener: VideoListener) :
    ListAdapter<Video, VideoAdapter.VideoViewHolder>(COMPARATOR) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VideoViewHolder {
        val itemVideoBinding =
            ItemVideoBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return VideoViewHolder(itemVideoBinding)
    }

    override fun onBindViewHolder(holder: VideoViewHolder, position: Int) {
        val video = getItem(position)
        if (video != null) holder.bind(video, clickListener)
    }

    class VideoViewHolder(private val binding: ItemVideoBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(video: Video, clickListener: VideoListener) {
            binding.imgVideoThumb.loadBackdrop(video.key.getYoutubeThumbnailPath())
            binding.tvTitle.text = video.name
            binding.tvType.text = video.type

            itemView.setOnClickListener { clickListener.onClick(video) }
        }
    }

    companion object {
        private val COMPARATOR = object : DiffUtil.ItemCallback<Video>() {
            override fun areItemsTheSame(oldItem: Video, newItem: Video): Boolean =
                oldItem.id == newItem.id

            override fun areContentsTheSame(oldItem: Video, newItem: Video): Boolean =
                oldItem == newItem
        }
    }
}

class VideoListener(val clickListener: (video: Video) -> Unit) {
    fun onClick(video: Video) = clickListener(video)
}