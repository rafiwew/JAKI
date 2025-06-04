package com.piwew.jaki.laporan

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.SearchView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.res.ResourcesCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.chip.Chip
import com.piwew.jaki.R
import com.piwew.jaki.databinding.ActivityPilihKategoriLaporanBinding
import com.piwew.jaki.laporan.CameraActivity.Companion.EXTRA_LAPORAN
import com.piwew.jaki.laporan.TinjauLaporanActivity.Companion.EXTRA_IS_FROM_REVIEW
import com.piwew.jaki.model.Category
import com.piwew.jaki.model.LaporanData
import com.piwew.jaki.ui.CategoryAdapter
import com.piwew.jaki.utils.setAsAccessibilityCustomActionLabel
import com.piwew.jaki.utils.setAsAccessibilityHeading
import com.piwew.jaki.utils.setAsAccessibilityRoleButton
import java.util.Random

class PilihKategoriLaporanActivity : AppCompatActivity() {

    private lateinit var binding: ActivityPilihKategoriLaporanBinding
    private val categoryAdapter = CategoryAdapter()
    private var laporanData: LaporanData? = null
    private var allCategories = listOf<Category>()
    private var isFromReviewScreen: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        binding = ActivityPilihKategoriLaporanBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setEdgeToEdgeInsets()

        @Suppress("DEPRECATION")
        laporanData = intent.getParcelableExtra(EXTRA_LAPORAN)
        isFromReviewScreen = intent.getBooleanExtra(EXTRA_IS_FROM_REVIEW, false)

        setupClickListeners()
        setupAccessibilityDelegates()
        setupSearchView()
        setUpRecyclerViewCategory()
        getCategoryData()
        setupChipCategory()
    }

    private fun setupChipCategory() {
        val categoryList = resources.getStringArray(R.array.categories).toList()

        categoryList.forEach { selectedCategory ->
            val chip = LayoutInflater.from(this)
                .inflate(R.layout.chip_category, binding.chipCategory, false) as Chip
            chip.id = Random().nextInt()
            chip.text = selectedCategory
            chip.typeface = ResourcesCompat.getFont(this, R.font.plus_jakarta_sans_medium)

            chip.setOnClickListener {
                handleCategorySelection(selectedCategory)
            }

            chip.setAsAccessibilityCustomActionLabel(
                getString(R.string.announce_action_select_this_category)
            )

            binding.chipCategory.addView(chip)
        }
    }

    private fun handleCategorySelection(selectedCategory: String) {
        val updatedLaporan = laporanData?.copy(kategori = selectedCategory)

        if (isFromReviewScreen) {
            val resultIntent = Intent().apply {
                putExtra(EXTRA_LAPORAN, updatedLaporan)
            }
            setResult(RESULT_OK, resultIntent)

            announceForAccessibility(
                getString(R.string.category_updated, selectedCategory)
            )

            finish()
        } else {
            val intent = Intent(this, TinjauLaporanActivity::class.java).apply {
                putExtra(EXTRA_LAPORAN, updatedLaporan)
            }
            startActivity(intent)
        }
    }

    private fun setupAccessibilityDelegates() {
        binding.ivActionBack.setAsAccessibilityRoleButton()
        binding.tvTitlePage.setAsAccessibilityHeading()
        binding.tvRekomendasi.setAsAccessibilityHeading()
        binding.tvSemuaKategori.setAsAccessibilityHeading()
    }

    private fun setupClickListeners() {
        binding.ivActionBack.setOnClickListener { onSupportNavigateUp() }
    }

    private fun setupSearchView() {
        binding.searchViewCategory.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                if (query != null) {
                    announceForAccessibility(getString(R.string.display_search_results_category, query))
                }
                return false
            }

            override fun onQueryTextChange(query: String?): Boolean {
                filterCategories(query.orEmpty())
                return true
            }
        })
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

        val message = if (filteredList.isEmpty()) {
            getString(R.string.no_category_found)
        } else {
            getString(R.string.search_result_count, filteredList.size)
        }
        announceForAccessibility(message)
    }

    private fun announceForAccessibility(message: String) {
        val liveRegionView = binding.liveRegionAnnouncement
        liveRegionView.apply {
            text = message
            announceForAccessibility(message)
        }
    }

    private fun getCategoryData() {
        val dummyCategories =
            this.resources.getStringArray(R.array.dummy_categories).map { Category(it) }

        allCategories = dummyCategories
        categoryAdapter.submitList(dummyCategories)
    }

    private fun setUpRecyclerViewCategory() {
        binding.rvCategory.apply {
            layoutManager = LinearLayoutManager(this@PilihKategoriLaporanActivity)
            adapter = categoryAdapter
        }

        categoryAdapter.onItemClick = { selectedCategory ->
            handleCategorySelection(selectedCategory.categoryName)
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