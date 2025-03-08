package com.piwew.jaki

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.text.Html
import android.text.method.LinkMovementMethod
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.piwew.jaki.databinding.ActivityNewsDetailBinding
import com.piwew.jaki.model.News
import com.piwew.jaki.utils.loadImage

class NewsDetailActivity : AppCompatActivity() {

    private lateinit var binding: ActivityNewsDetailBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityNewsDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.ivActionBack.setOnClickListener { onSupportNavigateUp() }

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        @Suppress("DEPRECATION")
        displayNews(intent.getParcelableExtra(EXTRA_DATA))
        @Suppress("DEPRECATION")
        setupShareActions(intent.getParcelableExtra(EXTRA_DATA))
    }

    private fun displayNews(news: News?) {
        news?.let {
            with(binding) {
                ivItemThumbnail.loadImage(it.thumbnailUrl)
                tvItemTitle.text = it.title
                tvItemPublished.text = it.publishedAt
                tvItemContent.text = Html.fromHtml(it.content, Html.FROM_HTML_MODE_LEGACY)
                tvItemContent.movementMethod = LinkMovementMethod.getInstance()
                tvItemLikes.text = getString(R.string.likes, it.likes.toString())
                ivItemThumbnail.contentDescription = getString(R.string.thumbnail_label, it.title)
                ibShare.contentDescription = getString(R.string.share_news, it.title)
                ibSave.contentDescription = getString(R.string.save_news, it.title)
            }
        }
    }

    private fun setupShareActions(news: News?) {

        val newsUrl = "https://google.com"
        val newsTitle = news?.title

        binding.ibFacebook.setOnClickListener {
            val facebookIntent = Intent(Intent.ACTION_VIEW)
            facebookIntent.data = Uri.parse("https://www.facebook.com/sharer/sharer.php?u=$newsUrl")
            startActivity(facebookIntent)
        }

        binding.ibTwitter.setOnClickListener {
            val twitterIntent = Intent(Intent.ACTION_VIEW)
            twitterIntent.data =
                Uri.parse("https://twitter.com/intent/tweet?text=$newsTitle&url=$newsUrl")
            startActivity(twitterIntent)
        }

        binding.ibEmail.setOnClickListener {
            val emailIntent = Intent(Intent.ACTION_SEND).apply {
                type = "text/plain"
                putExtra(Intent.EXTRA_SUBJECT, newsTitle)
                putExtra(Intent.EXTRA_TEXT, "Baca berita menarik ini: $newsTitle\n\n$newsUrl")
            }
            startActivity(Intent.createChooser(emailIntent, "Bagikan Berita via Email"))
        }

        binding.ibLink.setOnClickListener {
            val clipboard = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
            val clip = ClipData.newPlainText("Berita", newsUrl)
            clipboard.setPrimaryClip(clip)
            Toast.makeText(this, "Tautan berita disalin ke clipboard!", Toast.LENGTH_SHORT).show()
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