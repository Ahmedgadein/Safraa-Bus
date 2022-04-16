package com.dinder.rihlabus.common

import androidx.recyclerview.widget.DiffUtil
import com.dinder.rihlabus.data.model.Trip

class TripDiffCallback : DiffUtil.ItemCallback<Trip>() {
    override fun areItemsTheSame(oldItem: Trip, newItem: Trip): Boolean =
        oldItem.hashCode() == newItem.hashCode()

    override fun areContentsTheSame(oldItem: Trip, newItem: Trip): Boolean =
        oldItem == newItem
}
