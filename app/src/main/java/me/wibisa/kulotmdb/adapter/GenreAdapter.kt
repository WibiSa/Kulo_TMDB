package me.wibisa.kulotmdb.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import me.wibisa.kulotmdb.core.data.remote.response.Genre
import me.wibisa.kulotmdb.databinding.ItemGenreBinding

class GenreAdapter(private val clickListener: GenreListener) :
    ListAdapter<Genre, GenreAdapter.GenreViewHolder>(COMPARATOR) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GenreViewHolder {
        val itemGenreBinding =
            ItemGenreBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return GenreViewHolder(itemGenreBinding)
    }

    override fun onBindViewHolder(holder: GenreViewHolder, position: Int) {
        val genre = getItem(position)
        if (genre != null) holder.bind(genre, clickListener)
    }

    inner class GenreViewHolder(private val binding: ItemGenreBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(genre: Genre, clickListener: GenreListener) {
            binding.tvGenre.text = genre.name
            itemView.setOnClickListener { clickListener.onClick(genre) }
        }
    }

    companion object {
        private val COMPARATOR = object : DiffUtil.ItemCallback<Genre>() {
            override fun areItemsTheSame(oldItem: Genre, newItem: Genre): Boolean =
                oldItem.id == newItem.id

            override fun areContentsTheSame(oldItem: Genre, newItem: Genre): Boolean =
                oldItem == newItem
        }
    }
}

class GenreListener(val clickListener: (genre: Genre) -> Unit) {
    fun onClick(genre: Genre) = clickListener(genre)
}