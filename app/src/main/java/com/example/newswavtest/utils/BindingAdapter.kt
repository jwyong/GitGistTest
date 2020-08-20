package com.example.newswavtest.utils

import android.view.View
import androidx.databinding.BindingAdapter

object BindingAdapter {
    @JvmStatic
    @BindingAdapter("visibleGone")
    fun setGone(view: View, show: Boolean) {
        view.visibility = if (show) View.VISIBLE else View.GONE
    }
}