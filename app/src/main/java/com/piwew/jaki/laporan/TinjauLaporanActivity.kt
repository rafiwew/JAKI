package com.piwew.jaki.laporan

import android.net.Uri
import android.os.Bundle
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.piwew.jaki.R
import com.piwew.jaki.databinding.ActivityTinjauLaporanBinding
import com.piwew.jaki.laporan.CameraActivity.Companion.EXTRA_LAPORAN
import com.piwew.jaki.model.LaporanData
import com.piwew.jaki.utils.setAsAccessibilityHeading
import com.piwew.jaki.utils.setAsAccessibilityRoleButton

class TinjauLaporanActivity : AppCompatActivity() {

    private lateinit var binding: ActivityTinjauLaporanBinding
    private var laporanData: LaporanData? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        binding = ActivityTinjauLaporanBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setEdgeToEdgeInsets()
        binding.ivActionBack.setOnClickListener { onSupportNavigateUp() }

        binding.tvTitlePage.setAsAccessibilityHeading()
        binding.titleJenisLaporan.setAsAccessibilityHeading()
        binding.titleBuktiLaporan.setAsAccessibilityHeading()
        binding.titleLokasiLaporan.setAsAccessibilityHeading()
        binding.titleDeskripsiLaporan.setAsAccessibilityHeading()
        binding.titleKategoriLaporan.setAsAccessibilityHeading()
        binding.titlePernyataanLaporan.setAsAccessibilityHeading()

        binding.ivActionBack.setAsAccessibilityRoleButton()
        binding.editJenisLaporan.setAsAccessibilityRoleButton()
        binding.editLokasiLaporan.setAsAccessibilityRoleButton()
        binding.editDeskripsiLaporan.setAsAccessibilityRoleButton()
        binding.editKategoriLaporan.setAsAccessibilityRoleButton()

        // Get data from intent
        @Suppress("DEPRECATION")
        laporanData = intent.getParcelableExtra(EXTRA_LAPORAN)

        val alamat = laporanData?.alamat
        val detailAlamat = laporanData?.detailAlamat
        val detail = laporanData?.detail
        val kategori = laporanData?.kategori

        setupImagePreview()
        binding.tvValueJenisLaporan.text = getString(R.string.private_rahasia)
        binding.editJenisLaporan.contentDescription = getString(R.string.ganti_jenis_laporan, binding.tvValueJenisLaporan.text)
        binding.tvValueLokasiLaporan.text = alamat
        binding.editLokasiLaporan.contentDescription = getString(R.string.ganti_lokasi_laporan, binding.tvValueLokasiLaporan.text)
        binding.tvValueDeskripsiLaporan.text = detail
        binding.editDeskripsiLaporan.contentDescription = getString(R.string.ganti_deskripsi_laporan, binding.tvValueDeskripsiLaporan.text)
        binding.tvValueKategoriLaporan.text = kategori
        binding.editKategoriLaporan.contentDescription = getString(R.string.ganti_kategori_laporan, binding.tvValueKategoriLaporan.text)

        if (!detailAlamat.isNullOrBlank()) {
            binding.titleCiriLokasiLaporan.text = getString(R.string.ciri_khusus_lokasi)
            binding.tvValueCiriLokasiLaporan.text = detailAlamat
        } else {
            binding.titleCiriLokasiLaporan.visibility = View.GONE
            binding.tvValueCiriLokasiLaporan.visibility = View.GONE
        }

    }

    private fun setupImagePreview() {
        val imageUriString = laporanData?.imageUri
        imageUriString?.let {
            val imageUri = Uri.parse(it)
            binding.previewImageView.setImageURI(imageUri)
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