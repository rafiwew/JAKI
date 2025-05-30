package com.piwew.jaki.ui

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.accessibility.AccessibilityNodeInfo
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.piwew.jaki.R
import com.piwew.jaki.databinding.ItemListNewsJakartaBinding
import com.piwew.jaki.model.News
import com.piwew.jaki.utils.loadImage

class NewsJakartaAdapter :
    ListAdapter<News, NewsJakartaAdapter.ListViewHolder>(NewsDiffCallback()) {

    var onItemClick: ((News) -> Unit)? = null

    inner class ListViewHolder(private val binding: ItemListNewsJakartaBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(data: News) {
            with(binding) {
                ivThumbnail.loadImage(data.thumbnailUrl)
                tvItemTitle.text = data.title
                tvItemReleaseDate.text = data.publishedAt
                tvItemReleaseDate.contentDescription = itemView.context.getString(R.string.news_date, data.publishedAt)
                ibShare.contentDescription = itemView.context.getString(R.string.share_news, data.title)
                ibShare.setOnClickListener {
                    val context = it.context
                    val shareIntent = Intent().apply {
                        action = Intent.ACTION_SEND
                        putExtra(Intent.EXTRA_TEXT, data.title)
                        type = "text/plain"
                    }
                    context.startActivity(
                        Intent.createChooser(
                            shareIntent,
                            context.getString(R.string.share_news)
                        )
                    )
                }
                itemView.accessibilityDelegate = object : View.AccessibilityDelegate() {
                    override fun onInitializeAccessibilityNodeInfo(
                        host: View,
                        info: AccessibilityNodeInfo
                    ) {
                        super.onInitializeAccessibilityNodeInfo(host, info)
                        val customClick = AccessibilityNodeInfo.AccessibilityAction(
                            AccessibilityNodeInfo.ACTION_CLICK,
                            itemView.context.getString(R.string.announce_action_read_news)
                        )
                        info.addAction(customClick)
                    }
                }
            }
        }

        init {
            binding.root.setOnClickListener {
                onItemClick?.invoke(getItem(adapterPosition))
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ListViewHolder {
        val view = ItemListNewsJakartaBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ListViewHolder(view)
    }

    override fun onBindViewHolder(holder: ListViewHolder, position: Int) {
        val data = getItem(position)
        holder.bind(data)
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