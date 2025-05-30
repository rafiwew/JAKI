package com.piwew.jaki.berita

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.piwew.jaki.R
import com.piwew.jaki.data.database.AppDatabase
import com.piwew.jaki.data.database.dao.NewsDao
import com.piwew.jaki.data.database.entities.NewsEntity
import com.piwew.jaki.databinding.ActivityBeritaTersimpanBinding
import com.piwew.jaki.model.News
import com.piwew.jaki.ui.SavedNewsAdapter
import com.piwew.jaki.utils.setAsAccessibilityHeading

class BeritaTersimpanActivity : AppCompatActivity() {

    private lateinit var binding: ActivityBeritaTersimpanBinding
    private lateinit var newsDao: NewsDao
    private lateinit var newsAdapter: SavedNewsAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityBeritaTersimpanBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setEdgeToEdgeInsets()

        val appDatabase = AppDatabase.getDatabase(this)
        newsDao = appDatabase.newsDao()

        binding.ivActionBack.setOnClickListener { onSupportNavigateUp() }
        binding.tvTitlePage.setAsAccessibilityHeading()

        setupRecyclerView()
        observeSavedNews()
    }

    private fun setupRecyclerView() {
        newsAdapter = SavedNewsAdapter { news ->
            val intent = Intent(this, NewsDetailActivity::class.java).apply {
                putExtra(
                    NewsDetailActivity.EXTRA_DATA,
                    News(
                        title = news.title,
                        content = news.content,
                        thumbnailUrl = news.thumbnailUrl,
                        publishedAt = news.publishedAt,
                        likes = news.likes,
                        isBookmarked = true
                    )
                )
            }
            startActivity(intent)
            Log.d("DATA NEWS", news.toString())
        }
        binding.rvSavedNews.apply {
            adapter = newsAdapter
            layoutManager = LinearLayoutManager(this@BeritaTersimpanActivity)
        }
    }

    @Suppress("DEPRECATION")
    private fun observeSavedNews() {
        lifecycleScope.launchWhenCreated {
            newsDao.getAllSavedNews().collect { savedNews ->
                val newsList = savedNews.map { entity ->
                    entity.toNews().copy(isBookmarked = true)
                }
                newsAdapter.submitList(newsList)

                if (newsList.isEmpty()) {
                    binding.tvNoNewsSaved.visibility = View.VISIBLE
                    binding.rvSavedNews.visibility = View.GONE
                } else {
                    binding.tvNoNewsSaved.visibility = View.GONE
                    binding.rvSavedNews.visibility = View.VISIBLE
                }
            }
        }
    }

    private fun NewsEntity.toNews(): News {
        return News(
            title = this.title,
            content = this.content,
            thumbnailUrl = this.thumbnailUrl,
            publishedAt = this.publishedAt,
            likes = this.likes
        )
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

}