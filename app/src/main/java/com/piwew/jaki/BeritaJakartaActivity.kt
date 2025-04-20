package com.piwew.jaki

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
import com.piwew.jaki.NewsDetailActivity.Companion.EXTRA_DATA
import com.piwew.jaki.databinding.ActivityBeritaJakartaBinding
import com.piwew.jaki.model.News
import com.piwew.jaki.ui.NewsJakartaAdapter

class BeritaJakartaActivity : AppCompatActivity() {

    private lateinit var binding: ActivityBeritaJakartaBinding
    private lateinit var firebaseDatabase: FirebaseDatabase
    private lateinit var databaseReference: DatabaseReference

    private val newsJakartaAdapter = NewsJakartaAdapter()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityBeritaJakartaBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.ivActionBack.setOnClickListener { onSupportNavigateUp() }

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Firebase setup
        firebaseDatabase = FirebaseDatabase.getInstance()
        databaseReference = firebaseDatabase.getReference("News")

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
                        showToast("Tidak ada data berita")
                    }
                }
                newsJakartaAdapter.submitList(tempNewsList)
            }

            override fun onCancelled(error: DatabaseError) {
                showToast("Error: ${error.message}")
            }
        })
    }

    private fun setUpRecyclerView() {
        val mLayoutManager = LinearLayoutManager(this)
        binding.rvNewsJakarta.apply {
            layoutManager = mLayoutManager
            setHasFixedSize(true)
            adapter = newsJakartaAdapter
        }

        // Handle news item onClick
        newsJakartaAdapter.onItemClick = { selectedNews ->
            startActivity(Intent(this, NewsDetailActivity::class.java).apply {
                putExtra(
                    EXTRA_DATA,
                    selectedNews
                )
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