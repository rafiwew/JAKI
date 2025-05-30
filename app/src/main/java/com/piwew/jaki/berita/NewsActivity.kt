package com.piwew.jaki.berita

import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
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
import com.piwew.jaki.databinding.ActivityNewsBinding
import com.piwew.jaki.model.News
import com.piwew.jaki.ui.NewsAdapter
import com.piwew.jaki.utils.setAccessibilityAsButton
import com.piwew.jaki.utils.setAsAccessibilityHeading
import com.piwew.jaki.utils.setAsAccessibilityRoleButton

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

    private fun setupAccessibilityDelegates() {
        listOf(
            binding.ivActionBack,
            binding.clBeritaJakarta,
            binding.clMajalahJakita,
            binding.clNotFound,
            binding.clSavedNews
        ).forEach { view ->
            view.setAsAccessibilityRoleButton()
        }

        listOf(
            binding.tvTitlePage,
            binding.tvAksesCepatBerita,
            binding.tvInformasiTerkini,
            binding.tvBeritaDisimpan,
            binding.tvTidakMenemukan
        ).forEach { view ->
            view.setAsAccessibilityHeading()
        }

        binding.clBeritaJakarta.setAccessibilityAsButton(getString(R.string.announce_action_see_berita_jakarta))
        binding.searchBarNews.setAccessibilityAsButton(getString(R.string.announce_action_search_news))
        binding.btnViewAll.setAccessibilityAsButton(getString(R.string.announce_action_view_all_news))
        binding.clSavedNews.setAccessibilityAsButton(getString(R.string.announce_action_saved_news))
        binding.clNotFound.setAccessibilityAsButton(getString(R.string.announce_action_send_email))

    }

    private fun setupClickListeners() {
        binding.ivActionBack.setOnClickListener { onSupportNavigateUp() }

        binding.searchBarNews.setOnClickListener {
            val intent = Intent(this, SearchNewsActivity::class.java)
            startActivity(intent)
        }

        binding.clBeritaJakarta.setOnClickListener {
            startActivity(Intent(this, BeritaJakartaActivity::class.java))
        }

        binding.btnViewAll.setOnClickListener {
            startActivity(Intent(this, InformasiTerkiniActivity::class.java))
        }

        binding.clMajalahJakita.setOnClickListener { }

        binding.clSavedNews.setOnClickListener {
            val intent = Intent(this, BeritaTersimpanActivity::class.java)
            startActivity(intent)
        }

        binding.clNotFound.setOnClickListener {
            val email = binding.tvEmailJsc.text.toString()
            val intent = Intent(Intent.ACTION_SENDTO).apply {
                data = Uri.parse("mailto:")
                putExtra(Intent.EXTRA_EMAIL, arrayOf(email))
            }
            try {
                startActivity(intent)
            } catch (e: ActivityNotFoundException) {
                showToast(getString(R.string.not_found_email_app))
            }
        }
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

        newsMainAdapter.onItemClick = { selectedNews ->
            startActivity(Intent(this, NewsDetailActivity::class.java).apply {
                putExtra(EXTRA_DATA, selectedNews)
            })
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

}