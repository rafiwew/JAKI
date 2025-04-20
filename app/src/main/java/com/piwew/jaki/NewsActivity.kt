package com.piwew.jaki

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.AccessibilityDelegateCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.accessibility.AccessibilityNodeInfoCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.piwew.jaki.NewsDetailActivity.Companion.EXTRA_DATA
import com.piwew.jaki.databinding.ActivityNewsBinding
import com.piwew.jaki.model.News
import com.piwew.jaki.ui.NewsAdapter

class NewsActivity : AppCompatActivity() {

    private lateinit var binding: ActivityNewsBinding
    private val newsMainAdapter = NewsAdapter()
    private val databaseReference: DatabaseReference by lazy {
        FirebaseDatabase.getInstance().getReference("News")
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        binding = ActivityNewsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setEdgeToEdgeInsets()
        setupAccessibilityDelegates()
        setupClickListeners()

        setUpRecyclerView()
        getNewsData()
    }

    private fun setEdgeToEdgeInsets() {
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    private fun setupAccessibilityDelegates() {
        listOf(
            binding.clBeritaJakarta,
            binding.clMajalahJakita,
            binding.clNotFound,
            binding.clSavedNews
        ).forEach { view ->
            ViewCompat.setAccessibilityDelegate(view, object : AccessibilityDelegateCompat() {
                override fun onInitializeAccessibilityNodeInfo(
                    host: View,
                    info: AccessibilityNodeInfoCompat
                ) {
                    super.onInitializeAccessibilityNodeInfo(host, info)
                    info.roleDescription = "Button"
                }
            })
        }
    }

    private fun setupClickListeners() {
        binding.ivActionBack.setOnClickListener { onSupportNavigateUp() }

        binding.clBeritaJakarta.setOnClickListener {
            startActivity(Intent(this, BeritaJakartaActivity::class.java))
        }

        binding.btnViewAll.setOnClickListener {
            startActivity(Intent(this, InformasiTerkiniActivity::class.java))
        }

        // Optional
        binding.clMajalahJakita.setOnClickListener { }
        binding.clNotFound.setOnClickListener { }
        binding.clSavedNews.setOnClickListener { }
    }

    private fun getNewsData() {
        databaseReference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val tempNewsList = ArrayList<News>()
                for (getDataSnapshot in snapshot.children) {
                    val news = getDataSnapshot.getValue(News::class.java)

                    if (news != null) {
                        tempNewsList.add(news)
                    } else {
                        showToast("Tidak ada data berita")
                    }
                }

                newsMainAdapter.submitList(tempNewsList)
            }

            override fun onCancelled(error: DatabaseError) {
                showToast("Error: ${error.message}")
            }
        })
    }

    private fun setUpRecyclerView() {
        binding.rvNewsMain.apply {
            layoutManager = LinearLayoutManager(this@NewsActivity)
            adapter = newsMainAdapter
        }

        // Handle news item onClick
        newsMainAdapter.onItemClick = { selectedNews ->
            startActivity(Intent(this, NewsDetailActivity::class.java).apply {
                putExtra(EXTRA_DATA, selectedNews)
            })
        }
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressedDispatcher.onBackPressed()
        return true
    }
}