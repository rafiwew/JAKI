package com.piwew.jaki.laporan

import android.os.Bundle
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.AccessibilityDelegateCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.accessibility.AccessibilityNodeInfoCompat
import com.piwew.jaki.R
import com.piwew.jaki.databinding.ActivityLaporanWargaMainBinding
import com.piwew.jaki.utils.setAsAccessibilityHeading

class LaporanWargaMainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLaporanWargaMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        binding = ActivityLaporanWargaMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setEdgeToEdgeInsets()
        binding.ivActionBack.setOnClickListener { onSupportNavigateUp() }

        binding.btnBuatLaporanFoto.setOnClickListener {
            val sheet = FullHeightBottomSheet()
            sheet.show(supportFragmentManager, sheet.tag)
        }

        setupAccessibilityHeadings()
        setupAccessibilityDelegates()
    }

    private fun setupAccessibilityDelegates() {
        listOf(
            binding.clCariLaporan,
            binding.clPantauLaporan,
            binding.clLihatLaporan,
            binding.clKetahuiKondisiLaporan,
            binding.clKirimPertanyaan
        ).forEach { view ->
            ViewCompat.setAccessibilityDelegate(view, object : AccessibilityDelegateCompat() {
                override fun onInitializeAccessibilityNodeInfo(
                    host: View,
                    info: AccessibilityNodeInfoCompat
                ) {
                    super.onInitializeAccessibilityNodeInfo(host, info)
                    info.roleDescription = getString(R.string.role_button)
                }
            })
        }
    }

    private fun setupAccessibilityHeadings() {
        binding.tvTitlePage.setAsAccessibilityHeading()
        binding.tvEksplorLaporanWarga.setAsAccessibilityHeading()
        binding.tvButuhBantuan.setAsAccessibilityHeading()
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