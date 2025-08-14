package com.piwew.jaki

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.piwew.jaki.berita.NewsActivity
import com.piwew.jaki.databinding.ActivityHomeBinding
import com.piwew.jaki.laporan.LaporanWargaMainActivity
import com.piwew.jaki.utils.setAsAccessibilityCustomActionLabel

class HomeActivity : AppCompatActivity() {

    private lateinit var binding: ActivityHomeBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setEdgeToEdgeInsets()
        setupClickListeners()
        setupAccessibilityDelegates()
    }

    private fun setupClickListeners() {
        binding.btnNewsFeature.setOnClickListener {
            startActivity(Intent(this, NewsActivity::class.java))
        }

        binding.btnLaporanWargaFeature.setOnClickListener {
            startActivity(Intent(this, LaporanWargaMainActivity::class.java))
        }
    }

    private fun setupAccessibilityDelegates() {
        binding.btnNewsFeature.setAsAccessibilityCustomActionLabel(getString(R.string.try_news_feature))
        binding.btnLaporanWargaFeature.setAsAccessibilityCustomActionLabel(getString(R.string.try_citizen_report_feature))
    }

    private fun setEdgeToEdgeInsets() {
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

}