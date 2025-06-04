package com.piwew.jaki.ui

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.piwew.jaki.R
import com.piwew.jaki.databinding.ItemListSearchNewsBinding
import com.piwew.jaki.model.News
import com.piwew.jaki.utils.loadImage
import com.piwew.jaki.utils.setAsAccessibilityCustomActionLabel

class SearchNewsAdapter : ListAdapter<News, SearchNewsAdapter.ListViewHolder>(NewsDiffCallback()) {

    var onItemClick: ((News) -> Unit)? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ListViewHolder {
        val view = ItemListSearchNewsBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ListViewHolder(view)
    }

    override fun onBindViewHolder(holder: ListViewHolder, position: Int) {
        val data = getItem(position)
        holder.bind(data)
    }

    inner class ListViewHolder(private val binding: ItemListSearchNewsBinding) :
        RecyclerView.ViewHolder(binding.root) {
            fun bind(data: News) {
                with(binding) {
                    ivThumbnailNews.loadImage(data.thumbnailUrl)
                    tvTitleSavedNews.text = data.title
                    tvItemLikes.text = itemView.context.getString(R.string.likes, data.likes.toString())
                    tvDateNews.text = data.publishedAt
                    likesDateSection.contentDescription = itemView.context.getString(
                        R.string.cd_likes_and_date,
                        data.likes.toString(),
                        data.publishedAt
                    )
                    itemView.setAsAccessibilityCustomActionLabel(itemView.context.getString(R.string.announce_action_read_news))
                }
            }

        init {
            binding.root.setOnClickListener {
                onItemClick?.invoke(getItem(adapterPosition))
            }
        }

    }

    class NewsDiffCallback : DiffUtil.ItemCallback<News>() {
        override fun areItemsTheSame(oldItem: News, newItem: News): Boolean {
            return oldItem.title == newItem.title
        }

        override fun areContentsTheSame(oldItem: News, newItem: News): Boolean {
            return oldItem == newItem
        }
    }

}
