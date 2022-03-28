package com.dinder.rihlabus.ui.home.currentTrips

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.dinder.rihlabus.common.RihlaFragment
import com.dinder.rihlabus.databinding.CurrentTripsFragmentBinding
import com.dinder.rihlabus.ui.home.HomeFragmentDirections
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class CurrentTripsFragment : RihlaFragment() {
    private val viewModel: CurrentTripsViewModel by viewModels()
    private lateinit var binding: CurrentTripsFragmentBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = CurrentTripsFragmentBinding.inflate(inflater, container, false)
        setUI()
        return binding.root
    }

    private fun setUI() {
        binding.newTripFab.setOnClickListener {
            navigateToNewTrip()
        }
    }

    private fun navigateToNewTrip() {
        findNavController().navigate(HomeFragmentDirections.actionHomeFragmentToNewTripFragment())
    }
}