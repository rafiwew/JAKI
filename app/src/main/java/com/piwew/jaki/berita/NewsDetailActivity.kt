package com.piwew.jaki.berita

import android.content.ActivityNotFoundException
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.text.Html
import android.text.method.LinkMovementMethod
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.AccessibilityDelegateCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.accessibility.AccessibilityNodeInfoCompat
import androidx.lifecycle.lifecycleScope
import com.piwew.jaki.R
import com.piwew.jaki.data.database.AppDatabase
import com.piwew.jaki.data.database.dao.NewsDao
import com.piwew.jaki.data.database.entities.NewsEntity
import com.piwew.jaki.databinding.ActivityNewsDetailBinding
import com.piwew.jaki.model.News
import com.piwew.jaki.utils.loadImage
import com.piwew.jaki.utils.setAsAccessibilityCustomActionLabel
import com.piwew.jaki.utils.setAsAccessibilityHeading
import kotlinx.coroutines.launch

class NewsDetailActivity : AppCompatActivity() {

    private lateinit var binding: ActivityNewsDetailBinding
    private var news: News? = null
    private var isLiked = false
    private var isBookmarked = false
    private lateinit var newsDao: NewsDao
    private lateinit var appDatabase: AppDatabase

    @RequiresApi(Build.VERSION_CODES.R)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityNewsDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setEdgeToEdgeInsets()
        binding.ivActionBack.setOnClickListener { onSupportNavigateUp() }

        // Inisialisasi database
        appDatabase = AppDatabase.getDatabase(this)
        newsDao = appDatabase.newsDao()

        @Suppress("DEPRECATION")
        news = intent.getParcelableExtra(EXTRA_DATA)

        displayNews(news)
        setupShareActions(news)

        setupAccessibilityDelegates()
        setupLikeAccessibility()
        setupBookmarkAccessibility()

        lifecycleScope.launch {
            news?.id?.let { newsId ->
                isBookmarked = newsDao.isNewsBookmarked(newsId)
                updateBookmarkUI(news)
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.R)
    private fun setupBookmarkAccessibility() {
        binding.ibSave.setOnClickListener {
            news?.let { currentNews ->
                lifecycleScope.launch {
                    val newsEntity = currentNews.toNewsEntity()
                    if (isBookmarked) {
                        newsDao.deleteNews(newsEntity)
                    } else {
                        newsDao.saveNews(newsEntity)
                    }
                    isBookmarked = !isBookmarked
                    updateBookmarkUI(news)

                    val announcement = if (isBookmarked) {
                        getString(R.string.bookmarked_announcement, currentNews.title)
                    } else {
                        getString(R.string.unbookmarked_announcement, currentNews.title)
                    }
                    binding.ibSave.announceForAccessibility(announcement)
                }
            }
        }

        ViewCompat.setAccessibilityDelegate(
            binding.ibSave,
            object : AccessibilityDelegateCompat() {
                override fun onInitializeAccessibilityNodeInfo(
                    host: View,
                    info: AccessibilityNodeInfoCompat
                ) {
                    super.onInitializeAccessibilityNodeInfo(host, info)
                    info.addAction(
                        AccessibilityNodeInfoCompat.AccessibilityActionCompat(
                            AccessibilityNodeInfoCompat.ACTION_CLICK,
                            if (isBookmarked) getString(R.string.announce_action_unbookmarked) else getString(
                                R.string.announce_action_bookmarked
                            )
                        )
                    )
                }
            }
        )
    }

    @RequiresApi(Build.VERSION_CODES.R)
    private fun updateBookmarkUI(news: News?) {
        with(binding.ibSave) {
            isSelected = isBookmarked

            contentDescription = if (isBookmarked) {
                getString(R.string.saved_news, news?.title ?: "berita ini")
            } else {
                getString(R.string.save_news, news?.title ?: "berita ini")
            }
            stateDescription = if (isBookmarked) {
                getString(R.string.bookmarked_state)
            } else {
                getString(R.string.not_bookmarked_state)
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.R)
    private fun displayNews(news: News?) {
        this.news = news
        news?.let {
            with(binding) {
                ivItemThumbnail.loadImage(it.thumbnailUrl)
                tvItemTitle.text = it.title
                tvItemTitle.setAsAccessibilityHeading()
                tvItemPublished.text = it.publishedAt
                tvItemContent.text = Html.fromHtml(it.content, Html.FROM_HTML_MODE_LEGACY)
                tvItemContent.movementMethod = LinkMovementMethod.getInstance()

                updateLikesUI(it.likes)
                updateBookmarkUI(it)

                ivItemThumbnail.contentDescription = getString(R.string.thumbnail_label, it.title)
                ibShare.contentDescription = getString(R.string.share_news, it.title)

                tvItemLikes.importantForAccessibility = View.IMPORTANT_FOR_ACCESSIBILITY_YES
                tvItemLikes.accessibilityLiveRegion = View.ACCESSIBILITY_LIVE_REGION_POLITE
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.R)
    private fun setupLikeAccessibility() {
        binding.tvItemLikes.setOnClickListener {
            isLiked = !isLiked
            val currentLikes = binding.tvItemLikes.text.toString().replace(Regex("[^0-9]"), "").toInt()
            val newLikes = if (isLiked) currentLikes + 1 else currentLikes - 1

            updateLikesUI(newLikes)

            if (isLiked) {
                binding.tvItemLikes.announceForAccessibility(getString(R.string.liked_announcement))
            } else {
                binding.tvItemLikes.announceForAccessibility(getString(R.string.unliked_announcement))
            }
        }

        val label = getString(if (isLiked) R.string.announce_action_unliked else R.string.announce_action_liked)
        binding.tvItemLikes.setAsAccessibilityCustomActionLabel(label)

    }

    @RequiresApi(Build.VERSION_CODES.R)
    private fun updateLikesUI(likesCount: Int) {
        with(binding.tvItemLikes) {
            text = getString(R.string.likes, likesCount.toString())

            val iconRes = if (isLiked) R.drawable.ic_favorite_filled else R.drawable.ic_favorite_border
            setCompoundDrawablesWithIntrinsicBounds(
                ContextCompat.getDrawable(context, iconRes),
                null, null, null
            )

            // Update content description
            contentDescription = when {
                likesCount == 0 -> getString(R.string.no_likes)
                isLiked -> getString(R.string.you_and_others_liked, likesCount - 1)
                else -> getString(R.string.x_people_liked, likesCount)
            }

            stateDescription = if (isLiked) getString(R.string.liked_state) else getString(R.string.not_liked_state)

        }
    }

    private fun News.toNewsEntity(): NewsEntity {
        return NewsEntity(
            title = this.title,
            content = this.content,
            thumbnailUrl = this.thumbnailUrl,
            publishedAt = this.publishedAt,
            likes = this.likes,
            isBookmarked = true
        )
    }

    private fun setupShareActions(news: News?) {

        val newsUrl = "https://jaki.jakarta.go.id/v2/microsite/events"
        val newsTitle = news?.title

        binding.ibFacebook.setOnClickListener {
            val facebookIntent = Intent(Intent.ACTION_VIEW)
            facebookIntent.data = Uri.parse("https://www.facebook.com/sharer/sharer.php?u=$newsUrl")
            startActivity(facebookIntent)
        }

        binding.ibTwitter.setOnClickListener {
            val twitterIntent = Intent(Intent.ACTION_VIEW)
            twitterIntent.data = Uri.parse("https://twitter.com/intent/tweet?text=$newsTitle&url=$newsUrl")
            startActivity(twitterIntent)
        }

        binding.ibEmail.setOnClickListener {
            val emailIntent = Intent(Intent.ACTION_SENDTO).apply {
                data = Uri.parse("mailto:")
                putExtra(Intent.EXTRA_SUBJECT, newsTitle ?: "")

                val bodyText = getString(R.string.email_body_template, newsTitle ?: "", newsUrl)
                putExtra(Intent.EXTRA_TEXT, bodyText)
            }
            try {
                startActivity(emailIntent)
            } catch (e: ActivityNotFoundException) {
                showToast(getString(R.string.not_found_email_app))
            }

        }

        binding.ibLink.setOnClickListener {
            val clipboard = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
            val clip = ClipData.newPlainText(getString(R.string.title_news), newsUrl)
            clipboard.setPrimaryClip(clip)
            Toast.makeText(this, getString(R.string.copy_to_clipboard), Toast.LENGTH_LONG).show()
        }

    }

    private fun setupAccessibilityDelegates() {
        binding.ibLink.setAsAccessibilityCustomActionLabel(getString(R.string.announce_action_copy))

        listOf(
            binding.ibFacebook,
            binding.ibTwitter,
            binding.ibEmail
        ).forEach { view ->
            view.setAsAccessibilityCustomActionLabel(getString(R.string.announce_action_share))
        }
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show()
    }

    private fun setEdgeToEdgeInsets() {
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressedDispatcher.onBackPressed()
        return true
    }

    companion object {
        const val EXTRA_DATA = "DATA"
    }

}