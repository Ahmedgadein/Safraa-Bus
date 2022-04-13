package com.dinder.rihlabus.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.navigation.findNavController
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.dinder.rihlabus.common.TripDiffCallback
import com.dinder.rihlabus.data.model.Trip
import com.dinder.rihlabus.databinding.LastTripItemListBinding
import com.dinder.rihlabus.ui.home.HomeFragmentDirections

class LastTripsAdapter : ListAdapter<Trip, LastTripsAdapter.LastTripHolder>(TripDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LastTripHolder {
        return LastTripHolder(
            LastTripItemListBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: LastTripHolder, position: Int) {
        val trip = getItem(position)
        holder.bind(trip)
    }

    class LastTripHolder(private val binding: LastTripItemListBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(trip: Trip) {
            binding.trip = trip
            itemView.setOnClickListener {
                trip.id?.let {
                    navigateToTripDetails(it)
                }
            }
        }

        private fun navigateToTripDetails(id: Long) {
            itemView.findNavController()
                .navigate(HomeFragmentDirections.actionHomeFragmentToTripDetailsFragment(id))
        }
    }
}
