package com.piwew.jaki.laporan

import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.ViewCompat
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.piwew.jaki.R
import com.piwew.jaki.databinding.BottomSheetPilihJenisLaporanBinding
import com.piwew.jaki.utils.setAsAccessibilityCustomActionLabel
import com.piwew.jaki.utils.setRadioGroupHeading

class PilihJenisLaporanBottomSheet : BottomSheetDialogFragment() {

    interface JenisLaporanListener {
        fun onJenisLaporanDipilih(jenis: String)
    }

    private var listener: JenisLaporanListener? = null

    fun setJenisLaporanListener(listener: JenisLaporanListener) {
        this.listener = listener
    }

    private lateinit var binding: BottomSheetPilihJenisLaporanBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = BottomSheetPilihJenisLaporanBinding.inflate(inflater, container, false)

        binding.btnPilihJenisLaporan.setOnClickListener {
            val selectedId = binding.radioGroup.checkedRadioButtonId
            val jenis = when (selectedId) {
                R.id.radioPrivat -> getString(R.string.option_private)
                R.id.radioPublik -> getString(R.string.option_public)
                else -> getString(R.string.option_private)
            }

            listener?.onJenisLaporanDipilih(jenis)
            binding.btnPilihJenisLaporan.announceForAccessibility(getString(R.string.jenis_laporan_successfully_updated))
            dismiss()
        }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.ivClose.setOnClickListener { dismiss() }

        binding.radioPrivat.isChecked = true

        val radioPrivat = binding.radioPrivat
        val radioPublik = binding.radioPublik

        radioPrivat.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) radioPublik.isChecked = false
        }
        radioPublik.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) radioPrivat.isChecked = false
        }

        binding.radioPrivat.setRadioGroupHeading(binding.radioGroup)
        binding.radioPublik.setRadioGroupHeading(binding.radioGroup)
        bottomSheetAccessibility()
    }

    override fun onStart() {
        super.onStart()
        val dialog = dialog as BottomSheetDialog?
        val bottomSheet = dialog?.findViewById<View>(com.google.android.material.R.id.design_bottom_sheet)
        bottomSheet?.layoutParams?.height = ViewGroup.LayoutParams.WRAP_CONTENT

        val behavior = BottomSheetBehavior.from(bottomSheet!!)
        behavior.state = BottomSheetBehavior.STATE_EXPANDED
        behavior.skipCollapsed = true
        behavior.isDraggable = false

        dialog.setCanceledOnTouchOutside(false)
        dialog.setCancelable(false)
    }

    private fun bottomSheetAccessibility() {
        binding.ivClose.setAsAccessibilityCustomActionLabel(getString(R.string.announce_action_close_info))
        binding.btnPilihJenisLaporan.setAsAccessibilityCustomActionLabel(getString(R.string.announce_action_jenis_laporan_updated))

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            ViewCompat.setAccessibilityPaneTitle(
                binding.bottomSheetRoot,
                getString(R.string.announce_instruction_sheet_jenis_laporan)
            )
        } else {
            binding.bottomSheetRoot.post {
                binding.bottomSheetRoot.announceForAccessibility(getString(R.string.announce_instruction_sheet_jenis_laporan))
            }
        }
    }

}