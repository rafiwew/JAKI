package com.piwew.jaki.laporan

import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.text.method.LinkMovementMethod
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.piwew.jaki.R
import com.piwew.jaki.databinding.ActivityLaporanWargaMainBinding
import com.piwew.jaki.utils.setAccessibilityAsButton
import com.piwew.jaki.utils.setAsAccessibilityCustomActionLabel
import com.piwew.jaki.utils.setAsAccessibilityHeading
import com.piwew.jaki.utils.setAsAccessibilityRoleButton

class LaporanWargaMainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLaporanWargaMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        binding = ActivityLaporanWargaMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setEdgeToEdgeInsets()
        setupClickListeners()
        setupAccessibilityDelegates()
    }

    private fun setupAccessibilityDelegates() {
        listOf(
            binding.clCariLaporan,
            binding.clPantauLaporan,
            binding.clLihatLaporan,
            binding.clKetahuiKondisiLaporan
        ).forEach { view ->
            view.setAsAccessibilityRoleButton()
        }

        binding.tvTitlePage.setAsAccessibilityHeading()
        binding.tvEksplorLaporanWarga.setAsAccessibilityHeading()
        binding.tvButuhBantuan.setAsAccessibilityHeading()

        binding.ivActionSearch.setAsAccessibilityCustomActionLabel(getString(R.string.announce_action_search_report))
        binding.btnBuatLaporanFoto.setAsAccessibilityCustomActionLabel(getString(R.string.announce_action_make_report))
        binding.btnBuatLaporanVideo.setAsAccessibilityCustomActionLabel(getString(R.string.announce_action_make_report))
        binding.ivCloseInfo.setAsAccessibilityCustomActionLabel(getString(R.string.announce_action_close_info))
        binding.clKirimPertanyaan.setAccessibilityAsButton(getString(R.string.announce_action_send_email))
    }

    private fun setupClickListeners() {
        binding.ivActionBack.setOnClickListener { onSupportNavigateUp() }

        binding.btnBuatLaporanFoto.setOnClickListener {
            val sheet = FullHeightBottomSheet()
            sheet.show(supportFragmentManager, sheet.tag)
        }

        binding.tvLinkPelajari.movementMethod = LinkMovementMethod.getInstance()
        binding.ivCloseInfo.setOnClickListener { binding.clInfoBelumPernahMelapor.visibility = View.GONE }
        binding.clKirimPertanyaan.setOnClickListener {
            val email = binding.tvEmailJsc.text.toString()
            val intent = Intent(Intent.ACTION_SENDTO).apply {
                data = Uri.parse("mailto:")
                putExtra(Intent.EXTRA_EMAIL, arrayOf(email))
            }
            try {
                startActivity(intent)
            } catch (e: ActivityNotFoundException) {
                Toast.makeText(this, getString(R.string.not_found_email_app), Toast.LENGTH_LONG).show()
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