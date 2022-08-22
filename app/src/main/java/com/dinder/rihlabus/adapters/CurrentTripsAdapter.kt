package com.dinder.rihlabus.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.navigation.findNavController
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.dinder.rihlabus.common.TripDiffCallback
import com.dinder.rihlabus.data.model.Trip
import com.dinder.rihlabus.databinding.CurrentTripItemListBinding
import com.dinder.rihlabus.ui.home.HomeFragmentDirections

class CurrentTripsAdapter :
    ListAdapter<Trip, CurrentTripsAdapter.CurrentTripHolder>(TripDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CurrentTripHolder =
        CurrentTripHolder(
            CurrentTripItemListBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )

    override fun onBindViewHolder(holder: CurrentTripHolder, position: Int) {
        val trip = getItem(position)
        holder.bind(trip)
    }

    class CurrentTripHolder(private val binding: CurrentTripItemListBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(trip: Trip) {
            binding.trip = trip

            itemView.setOnClickListener {
                trip.id?.let {
                    navigateToTripDetails(it)
                }
            }
        }

        private fun navigateToTripDetails(id: String) {
            itemView.findNavController()
                .navigate(HomeFragmentDirections.actionHomeFragmentToTripDetailsFragment(id))
        }
    }
}
