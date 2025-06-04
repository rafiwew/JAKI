package com.piwew.jaki.laporan

import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.marginTop
import com.google.android.material.chip.Chip
import com.piwew.jaki.R
import com.piwew.jaki.databinding.ActivityTulisDetailLaporanBinding
import com.piwew.jaki.laporan.CameraActivity.Companion.EXTRA_LAPORAN
import com.piwew.jaki.laporan.TinjauLaporanActivity.Companion.EXTRA_IS_FROM_REVIEW
import com.piwew.jaki.model.LaporanData
import com.piwew.jaki.utils.setAsAccessibilityCustomActionLabel
import com.piwew.jaki.utils.setAsAccessibilityHeading
import com.piwew.jaki.utils.setAsAccessibilityRoleButton
import java.util.Random

class TulisDetailLaporanActivity : AppCompatActivity() {

    private lateinit var binding: ActivityTulisDetailLaporanBinding
    private var laporanData: LaporanData? = null
    private var isFromReviewScreen: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        binding = ActivityTulisDetailLaporanBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setEdgeToEdgeInsets()

        @Suppress("DEPRECATION")
        laporanData = intent.getParcelableExtra(EXTRA_LAPORAN)
        isFromReviewScreen = intent.getBooleanExtra(EXTRA_IS_FROM_REVIEW, false)

        binding.detailLaporanEditText.setText(laporanData?.detail.orEmpty())

        binding.btnNextToPilihKategoriLaporan.text = getString(
            if (isFromReviewScreen) R.string.save_changes else R.string.next
        )

        setupClickListeners()
        showPanduan()
        setupAccessibilityDelegates()
        setupChipSuggestions()
    }

    private fun setupChipSuggestions() {
        val suggestionList = arrayListOf(
            getString(R.string.suggest_ada_pelanggaran),
            getString(R.string.suggest_segera_tindak_lanjuti),
            getString(R.string.suggest_masalah_ini_terjadi_saat),
            getString(R.string.suggest_butuh_segera_ditindaklajuti)
        )

        suggestionList.forEach { suggestionText ->
            val chip = LayoutInflater.from(this)
                .inflate(R.layout.chip_suggestion, binding.chipSuggest, false) as Chip
            chip.id = Random().nextInt()
            chip.text = suggestionText
            chip.typeface = ResourcesCompat.getFont(this, R.font.plus_jakarta_sans_medium)

            val cleanText = suggestionText.replace("...", "")

            chip.setOnClickListener {
                val selectedText = chip.text.toString()

                val currentText = binding.detailLaporanEditText.text.toString()

                val newText = if (currentText.isEmpty()) {
                    selectedText
                } else {
                    "$currentText $selectedText"
                }

                binding.detailLaporanEditText.setText(newText)

                binding.detailLaporanEditText.setSelection(newText.length)
            }

            chip.contentDescription = getString(R.string.cd_suggestions_text, cleanText)
            chip.setAsAccessibilityCustomActionLabel(getString(R.string.announce_action_select_this_suggestion))

            binding.chipSuggest.addView(chip)
        }
    }

    private fun setupAccessibilityDelegates() {
        binding.ivActionBack.setAsAccessibilityRoleButton()
        binding.tvTitlePage.setAsAccessibilityHeading()
        binding.tvSuggestions.setAsAccessibilityHeading()
    }

    private fun setupClickListeners() {
        binding.ivActionBack.setOnClickListener { onSupportNavigateUp() }

        binding.btnNextToPilihKategoriLaporan.setOnClickListener {
            val detailLaporan = binding.detailLaporanEditText.text.toString().trim()
            val updatedLaporan = laporanData?.copy(detail = detailLaporan)

            if (isFromReviewScreen) {
                val resultIntent = Intent().apply {
                    putExtra(EXTRA_LAPORAN, updatedLaporan)
                }
                setResult(RESULT_OK, resultIntent)

                binding.root.post {
                    binding.detailLaporanEditText.announceForAccessibility(
                        getString(R.string.description_updated, detailLaporan)
                    )
                }

                finish()
            } else {
                val intent = Intent(this, PilihKategoriLaporanActivity::class.java).apply {
                    putExtra(EXTRA_LAPORAN, updatedLaporan)
                }
                startActivity(intent)
            }
        }
    }

    private fun showPanduan() {
        binding.switchPanduan.apply {
            isChecked = true
            setOnCheckedChangeListener { _, isChecked ->
                binding.horizontalScroll.visibility = if (isChecked) View.VISIBLE else View.GONE
                if (!isChecked) {
                    val params = binding.inputDetail.layoutParams as ViewGroup.MarginLayoutParams
                    params.topMargin = resources.getDimensionPixelSize(R.dimen.margin_8dp)
                    binding.inputDetail.layoutParams = params
                }

                if (isChecked) {
                    binding.switchPanduan.thumbTintList = ColorStateList.valueOf(
                        ContextCompat.getColor(
                            this@TulisDetailLaporanActivity,
                            R.color.colorPrimary
                        )
                    )
                    binding.switchPanduan.trackTintList =
                        ColorStateList.valueOf(Color.parseColor("#94BAFF"))
                } else {
                    binding.switchPanduan.thumbTintList = ColorStateList.valueOf(
                        ContextCompat.getColor(
                            this@TulisDetailLaporanActivity,
                            R.color.gray
                        )
                    )
                    binding.switchPanduan.trackTintList =
                        ColorStateList.valueOf(Color.parseColor("#E0E0E0"))
                }

            }
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