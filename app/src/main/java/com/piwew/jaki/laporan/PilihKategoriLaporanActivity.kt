package com.piwew.jaki.laporan

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.SearchView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.chip.Chip
import com.piwew.jaki.R
import com.piwew.jaki.databinding.ActivityPilihKategoriLaporanBinding
import com.piwew.jaki.laporan.CameraActivity.Companion.EXTRA_LAPORAN
import com.piwew.jaki.model.Category
import com.piwew.jaki.model.LaporanData
import com.piwew.jaki.ui.CategoryAdapter
import com.piwew.jaki.utils.setAsAccessibilityHeading
import com.piwew.jaki.utils.setAsAccessibilityRoleButton
import java.util.Random

class PilihKategoriLaporanActivity : AppCompatActivity() {

    private lateinit var binding: ActivityPilihKategoriLaporanBinding
    private val categoryAdapter = CategoryAdapter()
    private var laporanData: LaporanData? = null
    private var allCategories = listOf<Category>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        binding = ActivityPilihKategoriLaporanBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setEdgeToEdgeInsets()

        binding.searchViewCategory.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(query: String?): Boolean {
                filterCategories(query.orEmpty())
                return true
            }

        })

        // Get data from intent
        @Suppress("DEPRECATION")
        laporanData = intent.getParcelableExtra(EXTRA_LAPORAN)

        binding.ivActionBack.setOnClickListener { onSupportNavigateUp() }
        binding.ivActionBack.setAsAccessibilityRoleButton()
        binding.tvTitlePage.setAsAccessibilityHeading()
        binding.tvRekomendasi.setAsAccessibilityHeading()
        binding.tvSemuaKategori.setAsAccessibilityHeading()

        setUpRecyclerViewCategory()
        getCategoryData()

        val categoryList = arrayListOf(
            "Jalan",
            "Parkir Liar",
            "Pohon",
            "Sampah"
        )

        categoryList.forEach { selectedCategory ->
            val chip = LayoutInflater.from(this)
                .inflate(R.layout.chip_layout, binding.chipCategory, false) as Chip
            chip.id = Random().nextInt()
            chip.text = selectedCategory
            chip.setOnClickListener {
                val updatedLaporan = laporanData?.copy(
                    kategori = selectedCategory
                )
                val intent = Intent(this, TinjauLaporanActivity::class.java).apply {
                    putExtra(EXTRA_LAPORAN, updatedLaporan)
                }
                startActivity(intent)
            }
            binding.chipCategory.addView(chip)
        }
    }

    private fun filterCategories(query: String) {
        val filteredList = if (query.isEmpty()) {
            allCategories
        } else {
            allCategories.filter { category ->
                category.categoryName.contains(query, ignoreCase = true)
            }
        }
        categoryAdapter.submitList(filteredList)
    }

    private fun getCategoryData() {
        val dummyCategories = listOf(
            Category("Jalan Rusak"),
            Category("Lampu Jalan Mati"),
            Category("Parkir Liar"),
            Category("Pohon Tumbang"),
            Category("Sampah Menumpuk"),
            Category("Banjir"),
            Category("Lubang di Jalan"),
            Category("Fasilitas Umum Rusak"),
            Category("Trotoar Tidak Layak"),
            Category("Penerangan Jalan")
        )

        allCategories = dummyCategories
        categoryAdapter.submitList(dummyCategories)
    }

    private fun setUpRecyclerViewCategory() {
        binding.rvCategory.apply {
            layoutManager = LinearLayoutManager(this@PilihKategoriLaporanActivity)
            adapter = categoryAdapter
        }

        categoryAdapter.onItemClick = { selectedCategory ->
            val updatedLaporan = laporanData?.copy(
                kategori = selectedCategory.categoryName
            )
            val intent = Intent(this, TinjauLaporanActivity::class.java).apply {
                putExtra(EXTRA_LAPORAN, updatedLaporan)
            }
            startActivity(intent)
        }
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