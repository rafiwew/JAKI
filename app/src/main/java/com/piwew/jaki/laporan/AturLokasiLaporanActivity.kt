package com.piwew.jaki.laporan

import android.app.Activity
import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Color
import android.location.Geocoder
import android.net.Uri
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.piwew.jaki.R
import com.piwew.jaki.databinding.ActivityAturLokasiLaporanBinding
import com.piwew.jaki.laporan.CameraActivity.Companion.EXTRA_LAPORAN
import com.piwew.jaki.laporan.TinjauLaporanActivity.Companion.EXTRA_IS_FROM_REVIEW
import com.piwew.jaki.model.LaporanData
import com.piwew.jaki.utils.setAsAccessibilityCustomActionLabel
import com.piwew.jaki.utils.setAsAccessibilityHeading
import com.piwew.jaki.utils.setAsAccessibilityRoleButton
import java.util.Locale

class AturLokasiLaporanActivity : AppCompatActivity(),
    EditLocationBottomSheet.OnLocationSavedListener {

    private lateinit var binding: ActivityAturLokasiLaporanBinding
    private var laporanData: LaporanData? = null
    private var isFromReviewScreen: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        binding = ActivityAturLokasiLaporanBinding.inflate(layoutInflater)
        setContentView(binding.root)

        @Suppress("DEPRECATION")
        laporanData = intent.getParcelableExtra(EXTRA_LAPORAN)
        isFromReviewScreen = intent.getBooleanExtra(EXTRA_IS_FROM_REVIEW, false)

        binding.detailAddressEditText.setText(laporanData?.detailAlamat.orEmpty())

        binding.btnNextToTulisDetailLaporan.text = getString(
            if (isFromReviewScreen) R.string.save_changes else R.string.next
        )

        setEdgeToEdgeInsets()
        setupAccessibilityDelegates()
        setupClickListeners()
        setupImagePreview()
        setupLocation()
    }

    private fun setupAccessibilityDelegates() {
        binding.ivActionBack.setAsAccessibilityRoleButton()
        binding.tvTitlePage.setAsAccessibilityHeading()
        binding.tvTitleLokasiLaporan.setAsAccessibilityHeading()
        binding.tvDetailAlamatLaporan.setAsAccessibilityHeading()
        binding.btnGantiFoto.setAsAccessibilityCustomActionLabel(getString(R.string.announce_action_change_photo))
        binding.ivActionLokasiLaporan.setAsAccessibilityCustomActionLabel(getString(R.string.announce_action_edit_location))

        ViewCompat.addAccessibilityAction(
            binding.root,
            getString(R.string.text_ganti_foto)
        ) { _, _ ->
            startActivity(Intent(this, CameraActivity::class.java))
            true
        }

        ViewCompat.addAccessibilityAction(
            binding.root,
            getString(R.string.action_edit)
        ) { _, _ ->
            val sheet = EditLocationBottomSheet()
            sheet.show(supportFragmentManager, sheet.tag)
            true
        }
    }

    private fun setupClickListeners() {
        binding.ivActionBack.setOnClickListener { onSupportNavigateUp() }

        binding.btnGantiFoto.setOnClickListener {
            startActivity(Intent(this, CameraActivity::class.java))
        }

        binding.btnNextToTulisDetailLaporan.setOnClickListener {
            handleBackOrSubmit()
        }

        binding.ivActionLokasiLaporan.setOnClickListener {
            val sheet = EditLocationBottomSheet()
            sheet.show(supportFragmentManager, sheet.tag)
        }
    }

    private fun handleBackOrSubmit() {
        val detailAlamat = binding.detailAddressEditText.text.toString().trim()
        val alamat = binding.tvLokasiLaporan.text.toString().trim()

        val updatedLaporan = laporanData?.copy(
            alamat = alamat,
            detailAlamat = detailAlamat
        )

        if (isFromReviewScreen) {
            val resultIntent = Intent().apply {
                putExtra(EXTRA_LAPORAN, updatedLaporan)
            }
            setResult(Activity.RESULT_OK, resultIntent)
            finish()
        } else {
            val intent = Intent(this, TulisDetailLaporanActivity::class.java).apply {
                putExtra(EXTRA_LAPORAN, updatedLaporan)
            }
            startActivity(intent)
        }
    }

    private fun setupImagePreview() {
        val imageUriString = laporanData?.imageUri
        imageUriString?.let {
            val imageUri = Uri.parse(it)
            binding.previewImageView.setImageURI(imageUri)
        }
    }

    private fun setupLocation() {
        val lat = laporanData?.latitude ?: 0.0
        val lon = laporanData?.longitude ?: 0.0

        val address = getAddressFromCoordinates(lat, lon)
        updateLocationUI(address)

        binding.switchLocation.apply {
            isChecked = true
            setOnCheckedChangeListener { _, isChecked ->
                if (isChecked) {
                    binding.tvLokasiLaporan.text = address
                    binding.tvLokasiLaporan.setTextColor(
                        ContextCompat.getColor(
                            this@AturLokasiLaporanActivity,
                            R.color.black
                        )
                    )
                    binding.ivActionLokasiLaporan.setImageResource(R.drawable.ic_edit)
                } else {
                    binding.tvLokasiLaporan.text = getString(R.string.atur_lokasi_sendiri)
                    binding.tvLokasiLaporan.setTextColor(
                        ContextCompat.getColor(
                            this@AturLokasiLaporanActivity,
                            R.color.colorPrimary
                        )
                    )
                    binding.ivActionLokasiLaporan.setImageResource(R.drawable.icon_right_arrow)
                }

                if (isChecked) {
                    binding.switchLocation.thumbTintList = ColorStateList.valueOf(
                        ContextCompat.getColor(
                            this@AturLokasiLaporanActivity,
                            R.color.colorPrimary
                        )
                    )
                    binding.switchLocation.trackTintList =
                        ColorStateList.valueOf(Color.parseColor("#94BAFF"))
                } else {
                    binding.switchLocation.thumbTintList = ColorStateList.valueOf(
                        ContextCompat.getColor(
                            this@AturLokasiLaporanActivity,
                            R.color.gray
                        )
                    )
                    binding.switchLocation.trackTintList =
                        ColorStateList.valueOf(Color.parseColor("#E0E0E0"))
                }
            }
        }
    }

    @Suppress("DEPRECATION")
    private fun getAddressFromCoordinates(lat: Double, lon: Double): String {
        val geocoder = Geocoder(this, Locale.getDefault())
        val addresses = geocoder.getFromLocation(lat, lon, 1)
        return if (!addresses.isNullOrEmpty()) {
            addresses[0].getAddressLine(0)
        } else {
            getString(R.string.lokasi_not_found)
        }
    }

    private fun updateLocationUI(address: String) {
        binding.tvLokasi.text = address
        binding.tvLokasiLaporan.text = address
    }

    override fun onLocationSaved(editAddress: String) {
        binding.tvLokasiLaporan.text = editAddress
        binding.tvLokasiLaporan.setTextColor(ContextCompat.getColor(this, R.color.black))
        binding.tvLokasiLaporan.announceForAccessibility(getString(R.string.announce_location_updated))
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