package com.piwew.jaki.laporan

import android.location.Geocoder
import android.net.Uri
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.piwew.jaki.R
import com.piwew.jaki.databinding.ActivityAturLokasiLaporanBinding
import com.piwew.jaki.laporan.CameraActivity.Companion.EXTRA_LATITUDE
import com.piwew.jaki.laporan.CameraActivity.Companion.EXTRA_LONGITUDE
import java.util.Locale

class AturLokasiLaporanActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAturLokasiLaporanBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        binding = ActivityAturLokasiLaporanBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setEdgeToEdgeInsets()
        setupImagePreview()
        setupLocation()
    }

    private fun setupImagePreview() {
        val imageUriString = intent.getStringExtra(CameraActivity.EXTRA_CAMERAX_IMAGE)
        imageUriString?.let {
            val imageUri = Uri.parse(it)
            binding.previewImageView.setImageURI(imageUri)
        }
    }

    private fun setupLocation() {
        val lat = intent.getDoubleExtra(EXTRA_LATITUDE, 0.0)
        val lon = intent.getDoubleExtra(EXTRA_LONGITUDE, 0.0)

        val address = getAddressFromCoordinates(lat, lon)
        updateLocationUI(address)

        binding.switchLocation.apply {
            isChecked = true
            setOnCheckedChangeListener { _, isChecked ->
                if (isChecked) {
                    binding.tvLokasiLaporan.text = address
                    binding.ivAction.setImageResource(R.drawable.ic_edit)
                } else {
                    binding.tvLokasiLaporan.text = getString(R.string.atur_lokasi_sendiri)
                    binding.ivAction.setImageResource(R.drawable.icon_right_arrow)
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

    private fun setEdgeToEdgeInsets() {
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }
}