package com.piwew.jaki.ui

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.piwew.jaki.R
import com.piwew.jaki.databinding.ItemListSavedNewsBinding
import com.piwew.jaki.model.News
import com.piwew.jaki.utils.loadImage
import com.piwew.jaki.utils.setAsAccessibilityCustomActionLabel

class SavedNewsAdapter(
    private val onItemClick: (News) -> Unit
) : ListAdapter<News, SavedNewsAdapter.SavedNewsViewHolder>(DIFF_CALLBACK) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SavedNewsViewHolder {
        val view = ItemListSavedNewsBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return SavedNewsViewHolder(view)
    }

    override fun onBindViewHolder(holder: SavedNewsViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class SavedNewsViewHolder(private val binding: ItemListSavedNewsBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(news: News) {
            with(binding) {
                ivThumbnailSavedNews.loadImage(news.thumbnailUrl)
                tvTitleSavedNews.text = news.title
                tvDateSavedNews.text = news.publishedAt
                ibBookmark.isSelected = news.isBookmarked
                ibBookmark.contentDescription = itemView.context.getString(R.string.unbookmarked, news.title)
                ibBookmark.setOnClickListener { }
                root.setOnClickListener { onItemClick(news) }
                itemView.setAsAccessibilityCustomActionLabel(itemView.context.getString(R.string.announce_action_read_news))
            }
        }
    }

    companion object {
        private val DIFF_CALLBACK = object : DiffUtil.ItemCallback<News>() {
            override fun areItemsTheSame(oldItem: News, newItem: News): Boolean {
                return oldItem.title == newItem.title
            }

            override fun areContentsTheSame(oldItem: News, newItem: News): Boolean {
                return oldItem == newItem
            }
        }
    }

}