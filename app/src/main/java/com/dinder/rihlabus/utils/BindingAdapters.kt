package com.dinder.rihlabus.utils

import android.view.View
import androidx.databinding.BindingAdapter
import com.dinder.rihlabus.ui.custom_views.SeatsView

@BindingAdapter("viewOnly")
fun setIsViewOnly(view: SeatsView, viewOnly: Boolean) {
    view.setIsViewOnly(viewOnly)
}

@BindingAdapter("visible")
fun setVisibility(view: View, visible: Boolean) {
    view.visibility = if (visible) View.VISIBLE else View.GONE
}
