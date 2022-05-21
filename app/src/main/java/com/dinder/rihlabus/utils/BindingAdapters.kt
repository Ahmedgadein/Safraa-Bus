package com.dinder.rihlabus.utils

import android.widget.TextView
import androidx.databinding.BindingAdapter
import com.dinder.rihlabus.data.model.Destination
import com.dinder.rihlabus.ui.custom_views.SeatsView
import java.util.*

@BindingAdapter("viewOnly")
fun setIsViewOnly(view: SeatsView, viewOnly: Boolean) {
    view.setIsViewOnly(viewOnly)
}

@BindingAdapter("destination")
fun setDestinationLabel(view: TextView, destination: Destination?) {
    val isArabic = Locale.getDefault().language.equals(Locale("ar").language)
    destination?.let {
        view.text = if (isArabic) it.arabicName else it.name
    }
}
