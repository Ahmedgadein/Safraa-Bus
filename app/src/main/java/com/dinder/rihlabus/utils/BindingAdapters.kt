package com.dinder.rihlabus.utils

import androidx.databinding.BindingAdapter
import com.dinder.rihlabus.ui.custom_views.SeatsView

@BindingAdapter("viewOnly")
fun setIsViewOnly(view: SeatsView, viewOnly: Boolean) {
    view.setIsViewOnly(viewOnly)
}
