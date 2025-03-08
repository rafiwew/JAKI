package com.piwew.jaki.ui

import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.piwew.jaki.R
import com.piwew.jaki.databinding.ItemListNewsBinding
import com.piwew.jaki.model.News
import com.piwew.jaki.utils.loadImage

class NewsAdapter : ListAdapter<News, NewsAdapter.ListViewHolder>(NewsDiffCallback()) {

    var onItemClick: ((News) -> Unit)? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ListViewHolder {
        val view = ItemListNewsBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ListViewHolder(view)
    }

    override fun onBindViewHolder(holder: NewsAdapter.ListViewHolder, position: Int) {
        val data = getItem(position)
        holder.bind(data)
    }

    inner class ListViewHolder(private val binding: ItemListNewsBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(data: News) {
            with(binding) {
                ivThumbnail.loadImage(data.thumbnailUrl)
                tvItemTitle.text = data.title
                tvItemLikes.text = itemView.context.getString(R.string.likes, data.likes.toString())
                tvItemReleaseDate.text = data.publishedAt
                likesDateSection.contentDescription = itemView.context.getString(
                    R.string.likes_and_date,
                    data.likes.toString(),
                    data.publishedAt
                )
                ibShare.contentDescription = itemView.context.getString(R.string.share_news, data.title)
                ibShare.setOnClickListener {
                    val context = it.context
                    val shareIntent = Intent().apply {
                        action = Intent.ACTION_SEND
                        putExtra(Intent.EXTRA_TEXT, data.title)
                        type = "text/plain"
                    }
                    context.startActivity(Intent.createChooser(shareIntent, context.getString(R.string.share_news)))
                }
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