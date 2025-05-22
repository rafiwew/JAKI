package com.piwew.jaki.laporan

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.material.chip.Chip
import com.piwew.jaki.R
import com.piwew.jaki.databinding.ActivityTulisDetailLaporanBinding
import com.piwew.jaki.laporan.CameraActivity.Companion.EXTRA_LAPORAN
import com.piwew.jaki.model.LaporanData
import com.piwew.jaki.utils.setAsAccessibilityHeading
import com.piwew.jaki.utils.setAsAccessibilityRoleButton
import java.util.Random

class TulisDetailLaporanActivity : AppCompatActivity() {

    private lateinit var binding: ActivityTulisDetailLaporanBinding
    private var laporanData: LaporanData? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        binding = ActivityTulisDetailLaporanBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setEdgeToEdgeInsets()

        // Get data from intent
        @Suppress("DEPRECATION")
        laporanData = intent.getParcelableExtra(EXTRA_LAPORAN)

        binding.ivActionBack.setOnClickListener { onSupportNavigateUp() }

        binding.ivActionBack.setAsAccessibilityRoleButton()
        binding.tvTitlePage.setAsAccessibilityHeading()

        val suggestionList = arrayListOf(
            "Ada pelanggaran...",
            "Segera tindak lanjuti masalah...",
            "Masalah ini terjadi saat...",
            "Butuh segera ditindaklajuti karena..."
        )

        suggestionList.forEach { suggestionText ->
            val chip = LayoutInflater.from(this)
                .inflate(R.layout.chip_suggestion, binding.chipSuggest, false) as Chip
            chip.id = Random().nextInt()
            chip.text = suggestionText

            // Atur contentDescription tanpa titik-titik
            val cleanText = suggestionText.replace("...", "")
            //chip.contentDescription = cleanText

            chip.setOnClickListener {
                // Ambil teks dari chip yang diklik
                val selectedText = chip.text.toString()

                // Dapatkan teks yang sudah ada di EditText
                val currentText = binding.detailLaporanEditText.text.toString()

                // Gabungkan teks yang ada dengan teks baru dari chip
                val newText = if (currentText.isEmpty()) {
                    selectedText
                } else {
                    "$currentText $selectedText"
                }

                // Set teks baru ke EditText
                binding.detailLaporanEditText.setText(newText)

                // Pindahkan kursor ke akhir teks
                binding.detailLaporanEditText.setSelection(newText.length)
            }

            chip.setAsAccessibilityRoleButton()
            chip.contentDescription = getString(R.string.suggestions_text, cleanText)

            binding.chipSuggest.addView(chip)
        }

        binding.btnNextToPilihKategoriLaporan.setOnClickListener {
            val detailLaporan = binding.detailLaporanEditText.text.toString().trim()

            val updatedLaporan = laporanData?.copy(detail = detailLaporan)

            val intent = Intent(this, PilihKategoriLaporanActivity::class.java).apply {
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