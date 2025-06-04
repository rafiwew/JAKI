package com.piwew.jaki.berita

import android.content.Intent
import android.os.Bundle
import android.widget.SearchView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.piwew.jaki.R
import com.piwew.jaki.berita.NewsDetailActivity.Companion.EXTRA_DATA
import com.piwew.jaki.databinding.ActivitySearchNewsBinding
import com.piwew.jaki.model.News
import com.piwew.jaki.ui.SearchNewsAdapter
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class SearchNewsActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySearchNewsBinding
    private val newsSearchAdapter = SearchNewsAdapter()
    private val databaseReference: DatabaseReference by lazy {
        FirebaseDatabase.getInstance().getReference("News")
    }
    private val originalNewsList = mutableListOf<News>()

    private var searchJob: Job? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivitySearchNewsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setEdgeToEdgeInsets()
        binding.ivActionBack.setOnClickListener { onSupportNavigateUp() }

        setUpRecyclerView()
        getNewsData()
        setupSearchView()
    }

    private fun getNewsData() {
        databaseReference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val tempNewsList = ArrayList<News>()
                for (getDataSnapshot in snapshot.children) {
                    val news = getDataSnapshot.getValue(News::class.java)
                    news?.let { tempNewsList.add(it) }
                }

                originalNewsList.clear()
                originalNewsList.addAll(tempNewsList)
                newsSearchAdapter.submitList(tempNewsList)
            }

            override fun onCancelled(error: DatabaseError) {
                showToast("Error: ${error.message}")
            }
        })
    }

    private fun setUpRecyclerView() {
        binding.rvSearchNews.apply {
            layoutManager = LinearLayoutManager(this@SearchNewsActivity)
            adapter = newsSearchAdapter
        }

        newsSearchAdapter.onItemClick = { selectedNews ->
            startActivity(Intent(this, NewsDetailActivity::class.java).apply {
                putExtra(EXTRA_DATA, selectedNews)
            })
        }
    }

    private fun setupSearchView() {
        binding.searchViewNews.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                if (query != null) {
                    announceForAccessibility(getString(R.string.display_search_results_news, query))
                }
                query?.let { filterNews(it) }
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                newText?.let { filterNews(it) }
                return true
            }
        })
    }

    private fun filterNews(query: String) {
        searchJob?.cancel()
        searchJob = CoroutineScope(Dispatchers.Main).launch {
            delay(300)

            val filteredList = if (query.isEmpty()) {
                originalNewsList
            } else {
                originalNewsList.filter { news ->
                    news.title.contains(query, ignoreCase = true) || news.content.contains(
                        query,
                        ignoreCase = true
                    )
                }
            }

            newsSearchAdapter.submitList(filteredList)

            val message = if (filteredList.isEmpty() && query.isNotEmpty()) {
                getString(R.string.no_news_found)
            } else {
                getString(R.string.search_result_news_count, filteredList.size)
            }
            announceForAccessibility(message)
        }
    }

    private fun announceForAccessibility(message: String) {
        val liveRegionView = binding.liveRegionAnnouncement
        liveRegionView.apply {
            text = message
            announceForAccessibility(message)
        }
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
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