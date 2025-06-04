package com.piwew.jaki.utils

import android.view.View
import android.widget.RadioButton
import androidx.core.view.AccessibilityDelegateCompat
import androidx.core.view.ViewCompat
import androidx.core.view.accessibility.AccessibilityNodeInfoCompat

fun RadioButton.setRadioGroupHeading(radioGroupLabelView: View) {
    ViewCompat.setAccessibilityDelegate(this, object : AccessibilityDelegateCompat() {
        override fun onInitializeAccessibilityNodeInfo(
            host: View,
            info: AccessibilityNodeInfoCompat
        ) {
            super.onInitializeAccessibilityNodeInfo(host, info)

            // Technique: Use setLabeledBy() to associate a group label View with a RadioButton.
            // This is done in addition to setting the RadioButton's field label with android:text.
            info.setLabeledBy(radioGroupLabelView)
        }
    })
}