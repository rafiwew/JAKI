package com.piwew.jaki.laporan

import android.content.Context
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
import com.piwew.jaki.databinding.BottomSheetEditLocationBinding
import com.piwew.jaki.utils.setAsAccessibilityCustomActionLabel

class EditLocationBottomSheet : BottomSheetDialogFragment() {

    interface OnLocationSavedListener {
        fun onLocationSaved(editAddress: String)
    }

    private lateinit var binding: BottomSheetEditLocationBinding
    private var listener: OnLocationSavedListener? = null

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is OnLocationSavedListener) {
            listener = context
        } else {
            throw RuntimeException("$context must implement OnLocationSavedListener")
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = BottomSheetEditLocationBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.ivClose.setOnClickListener { dismiss() }

        binding.btnSaveLocation.setOnClickListener {
            val address = binding.editAddressEditText.text.toString().trim()
            if (address.isNotEmpty()) {
                listener?.onLocationSaved(address)
                dismiss()
            }
        }

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
        binding.btnSaveLocation.setAsAccessibilityCustomActionLabel(getString(R.string.announce_action_location_updated))

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            ViewCompat.setAccessibilityPaneTitle(
                binding.bottomSheetRoot,
                getString(R.string.announce_instruction_sheet_edit_location)
            )
        } else {
            binding.bottomSheetRoot.post {
                binding.bottomSheetRoot.announceForAccessibility(getString(R.string.announce_instruction_sheet_edit_location))
            }
        }

        ViewCompat.addAccessibilityAction(
            binding.bottomSheetRoot,
            getString(R.string.close_custom_action_edit_location)
        ) { _, _ ->
            dismiss()
            true
        }
    }
}