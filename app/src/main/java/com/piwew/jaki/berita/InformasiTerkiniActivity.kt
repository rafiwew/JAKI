package com.piwew.jaki.berita

import android.content.Intent
import android.os.Bundle
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
import com.piwew.jaki.databinding.ActivityInformasiTerkiniBinding
import com.piwew.jaki.model.News
import com.piwew.jaki.ui.NewsAdapter
import com.piwew.jaki.utils.setAsAccessibilityHeading

class InformasiTerkiniActivity : AppCompatActivity() {

    private lateinit var binding: ActivityInformasiTerkiniBinding
    private val databaseReference: DatabaseReference by lazy {
        FirebaseDatabase.getInstance().getReference("News")
    }

    private val newsAdapter = NewsAdapter()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityInformasiTerkiniBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setEdgeToEdgeInsets()

        binding.ivActionBack.setOnClickListener { onSupportNavigateUp() }
        binding.tvTitlePage.setAsAccessibilityHeading()

        setUpRecyclerView()
        getNewsData()
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
                        showToast(getString(R.string.no_news_data))
                    }
                }
                newsAdapter.submitList(tempNewsList)
            }

            override fun onCancelled(error: DatabaseError) {
                showToast("Error: ${error.message}")
            }
        })
    }

    private fun setUpRecyclerView() {
        val mLayoutManager = LinearLayoutManager(this)
        binding.rvNewsInformasiTerkini.apply {
            layoutManager = mLayoutManager
            setHasFixedSize(true)
            adapter = newsAdapter
        }

        newsAdapter.onItemClick = { selectedNews ->
            startActivity(Intent(this, NewsDetailActivity::class.java).apply {
                putExtra(EXTRA_DATA, selectedNews)
            })
        }
    }

    private fun setEdgeToEdgeInsets() {
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
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