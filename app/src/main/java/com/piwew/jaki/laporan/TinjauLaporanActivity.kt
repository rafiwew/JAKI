package com.piwew.jaki.laporan

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.piwew.jaki.R
import com.piwew.jaki.databinding.ActivityTinjauLaporanBinding
import com.piwew.jaki.laporan.CameraActivity.Companion.EXTRA_LAPORAN
import com.piwew.jaki.model.LaporanData
import com.piwew.jaki.utils.setAccessibilityAsButton
import com.piwew.jaki.utils.setAsAccessibilityHeading

class TinjauLaporanActivity : AppCompatActivity() {

    private lateinit var binding: ActivityTinjauLaporanBinding
    private var laporanData: LaporanData? = null

    private val editLaporanLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            @Suppress("DEPRECATION")
            val updatedLaporan = result.data?.getParcelableExtra<LaporanData>(EXTRA_LAPORAN)
            if (updatedLaporan != null) {
                val oldAlamat = laporanData?.alamat
                val oldDetailAlamat = laporanData?.detailAlamat
                val oldDetail = laporanData?.detail
                val oldCategory = laporanData?.kategori

                laporanData = updatedLaporan
                showAllPreviewData()

                val isAlamatChanged = oldAlamat != updatedLaporan.alamat
                val isDetailAlamatChanged = oldDetailAlamat != updatedLaporan.detailAlamat

                if (isAlamatChanged || isDetailAlamatChanged) {
                    val newText = buildString {
                        append(getString(R.string.location_updated_successfully))
                        append(". ")
                        append(getString(R.string.new_location_colon, updatedLaporan.alamat ?: ""))

                        if (!updatedLaporan.detailAlamat.isNullOrBlank()) {
                            append(". ")
                            append(
                                getString(
                                    R.string.location_detail_colon,
                                    updatedLaporan.detailAlamat
                                )
                            )
                        }
                    }

                    binding.root.post {
                        binding.tvValueLokasiLaporan.announceForAccessibility(newText)
                    }
                }

                if (oldDetail != updatedLaporan.detail) {
                    binding.tvValueDeskripsiLaporan.announceForAccessibility(
                        getString(R.string.description_updated, updatedLaporan.detail)
                    )
                }

                if (oldCategory != updatedLaporan.kategori) {
                    binding.tvValueKategoriLaporan.announceForAccessibility(
                        getString(R.string.category_updated, updatedLaporan.kategori)
                    )
                }

            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityTinjauLaporanBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setEdgeToEdgeInsets()

        @Suppress("DEPRECATION")
        laporanData = intent.getParcelableExtra(EXTRA_LAPORAN)

        setupClickListeners()
        setupAccessibilityDelegates()
        showAllPreviewData()

        binding.tvValueJenisLaporan.text = getString(R.string.private_rahasia)
    }

    private fun showAllPreviewData() {
        val addressLocation = laporanData?.alamat
        val detailAddress = laporanData?.detailAlamat
        val detail = laporanData?.detail
        val category = laporanData?.kategori

        setupImagePreview()
        binding.editJenisLaporan.contentDescription =
            getString(R.string.ganti_jenis_laporan, binding.tvValueJenisLaporan.text)

        binding.tvValueLokasiLaporan.text = addressLocation
        binding.editLokasiLaporan.contentDescription =
            getString(R.string.ganti_lokasi_laporan, binding.tvValueLokasiLaporan.text)

        binding.tvValueDeskripsiLaporan.text = detail
        binding.editDeskripsiLaporan.contentDescription =
            getString(R.string.ganti_deskripsi_laporan, binding.tvValueDeskripsiLaporan.text)

        binding.tvValueKategoriLaporan.text = category
        binding.editKategoriLaporan.contentDescription =
            getString(R.string.ganti_kategori_laporan, binding.tvValueKategoriLaporan.text)

        if (!detailAddress.isNullOrBlank()) {
            binding.titleCiriLokasiLaporan.visibility = View.VISIBLE
            binding.tvValueCiriLokasiLaporan.visibility = View.VISIBLE
            binding.titleCiriLokasiLaporan.text = getString(R.string.ciri_khusus_lokasi)
            binding.tvValueCiriLokasiLaporan.text = detailAddress
        } else {
            binding.titleCiriLokasiLaporan.visibility = View.GONE
            binding.tvValueCiriLokasiLaporan.visibility = View.GONE
        }
    }

    private fun setupAccessibilityDelegates() {
        binding.tvTitlePage.setAsAccessibilityHeading()
        binding.titleJenisLaporan.setAsAccessibilityHeading()
        binding.titleBuktiLaporan.setAsAccessibilityHeading()
        binding.titleLokasiLaporan.setAsAccessibilityHeading()
        binding.titleDeskripsiLaporan.setAsAccessibilityHeading()
        binding.titleKategoriLaporan.setAsAccessibilityHeading()
        binding.titlePernyataanLaporan.setAsAccessibilityHeading()

        binding.editJenisLaporan.setAccessibilityAsButton(getString(R.string.change_report_type))
        binding.editLokasiLaporan.setAccessibilityAsButton(getString(R.string.change_report_location))
        binding.editDeskripsiLaporan.setAccessibilityAsButton(getString(R.string.change_report_description))
        binding.editKategoriLaporan.setAccessibilityAsButton(getString(R.string.change_report_category))
        binding.btnKirimLaporan.setAccessibilityAsButton(getString(R.string.submit_report))
    }

    private fun setupClickListeners() {
        binding.ivActionBack.setOnClickListener { onSupportNavigateUp() }

        binding.editJenisLaporan.setOnClickListener {
            val sheet = PilihJenisLaporanBottomSheet()
            sheet.setJenisLaporanListener(object :
                PilihJenisLaporanBottomSheet.JenisLaporanListener {
                override fun onJenisLaporanDipilih(jenis: String) {
                    binding.tvValueJenisLaporan.text = jenis
                    binding.editJenisLaporan.contentDescription =
                        getString(R.string.ganti_jenis_laporan, binding.tvValueJenisLaporan.text)
                }
            })
            sheet.show(supportFragmentManager, sheet.tag)
        }

        binding.editLokasiLaporan.setOnClickListener {
            val intent = Intent(this, AturLokasiLaporanActivity::class.java).apply {
                putExtra(EXTRA_LAPORAN, laporanData)
                putExtra(EXTRA_IS_FROM_REVIEW, true)
            }
            editLaporanLauncher.launch(intent)
        }

        binding.editDeskripsiLaporan.setOnClickListener {
            val intent = Intent(this, TulisDetailLaporanActivity::class.java).apply {
                putExtra(EXTRA_LAPORAN, laporanData)
                putExtra(EXTRA_IS_FROM_REVIEW, true)
            }
            editLaporanLauncher.launch(intent)
        }

        binding.editKategoriLaporan.setOnClickListener {
            val intent = Intent(this, PilihKategoriLaporanActivity::class.java).apply {
                putExtra(EXTRA_LAPORAN, laporanData)
                putExtra(EXTRA_IS_FROM_REVIEW, true)
            }
            editLaporanLauncher.launch(intent)
        }

        binding.btnKirimLaporan.setOnClickListener {
            binding.btnKirimLaporan.announceForAccessibility(getString(R.string.report_successfully_created))
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

    companion object {
        const val EXTRA_IS_FROM_REVIEW = "EXTRA_IS_FROM_REVIEW"
    }

}