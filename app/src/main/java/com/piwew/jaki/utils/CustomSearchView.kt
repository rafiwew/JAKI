package com.piwew.jaki.utils

import android.content.Context
import android.text.TextUtils
import android.util.AttributeSet
import android.util.TypedValue
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.widget.SearchView
import androidx.core.content.ContextCompat
import com.piwew.jaki.R

class CustomSearchView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = androidx.appcompat.R.attr.searchViewStyle
) : SearchView(context, attrs, defStyleAttr) {

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        adjustQueryHintAndCloseButtonTouchTarget()
    }

    private fun adjustQueryHintAndCloseButtonTouchTarget() {
        val closeButton = findViewById<ImageView>(androidx.appcompat.R.id.search_close_btn)
        val searchSrcText = findViewById<TextView>(androidx.appcompat.R.id.search_src_text)

        searchSrcText?.let {
            val params = it.layoutParams
            params.height = ViewGroup.LayoutParams.WRAP_CONTENT
            it.layoutParams = params

            val minHeightInPx = TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP, 48f, it.resources.displayMetrics
            ).toInt()
            it.minHeight = minHeightInPx

            val paddingVertical = TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP, 12f, it.resources.displayMetrics
            ).toInt()
            it.setPadding(it.paddingLeft, paddingVertical, it.paddingRight, paddingVertical)

            it.maxLines = 2
            it.ellipsize = TextUtils.TruncateAt.END

            it.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16f)
        }

        closeButton?.let {
            val sizeInDp = 50
            val sizeInPx = TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP,
                sizeInDp.toFloat(),
                resources.displayMetrics
            ).toInt()

            val params = it.layoutParams
            params.width = sizeInPx
            params.height = sizeInPx
            it.layoutParams = params

            val padding = TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP,
                12f,
                resources.displayMetrics
            ).toInt()
            it.setPadding(padding, padding, padding, padding)

            it.setImageDrawable(
                ContextCompat.getDrawable(context, R.drawable.ic_close)
            )

            it.contentDescription = context.getString(R.string.delete_search_query)
        }
    }
    
}
