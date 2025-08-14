package com.piwew.jaki.ui

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.piwew.jaki.R
import com.piwew.jaki.databinding.ItemListMainNewsBinding
import com.piwew.jaki.model.News
import com.piwew.jaki.utils.loadImage
import com.piwew.jaki.utils.setAsAccessibilityCustomActionLabel

class NewsMainAdapter : ListAdapter<News, NewsMainAdapter.ListViewHolder>(NewsMainDiffCallback()) {

    var onItemClick: ((News) -> Unit)? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ListViewHolder {
        val view = ItemListMainNewsBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ListViewHolder(view)
    }

    override fun onBindViewHolder(holder: ListViewHolder, position: Int) {
        val data = getItem(position)
        holder.bind(data)
    }

    inner class ListViewHolder(private val binding: ItemListMainNewsBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(data: News) {
            with(binding) {
                ivThumbnail.loadImage(data.thumbnailUrl)
                tvItemTitle.text = data.title
                tvItemReleaseDate.text = data.publishedAt
                dateSection.contentDescription = itemView.context.getString(R.string.cd_date, data.publishedAt)
                itemView.setAsAccessibilityCustomActionLabel(itemView.context.getString(R.string.announce_action_read_news))
            }
        }
        init {
            binding.root.setOnClickListener {
                onItemClick?.invoke(getItem(adapterPosition))
            }
        }
    }

    class NewsMainDiffCallback : DiffUtil.ItemCallback<News>() {
        override fun areItemsTheSame(oldItem: News, newItem: News): Boolean {
            return oldItem.title == newItem.title
        }

        override fun areContentsTheSame(oldItem: News, newItem: News): Boolean {
            return oldItem == newItem
        }
    }

}