package com.dinder.rihlabus.ui.home.currentTrips

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.dinder.rihlabus.adapters.CurrentTripsAdapter
import com.dinder.rihlabus.common.RihlaFragment
import com.dinder.rihlabus.databinding.CurrentTripsFragmentBinding
import com.dinder.rihlabus.ui.home.HomeFragmentDirections
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

@AndroidEntryPoint
class CurrentTripsFragment : RihlaFragment() {
    private val viewModel: CurrentTripsViewModel by viewModels()
    private lateinit var binding: CurrentTripsFragmentBinding
    private lateinit var tripsAdapter: CurrentTripsAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
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

        tripsAdapter = CurrentTripsAdapter()
        binding.currentTripsRecyclerView.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = tripsAdapter
        }
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.getTrips()
                viewModel.state.collect {
                    binding.currentTripsProgressBar.isVisible = it.loading

                    it.messages.firstOrNull()?.let { message ->
                        showSnackbar(message.content)
                        viewModel.userMessageShown(message.id)
                    }
                    it.trips.let { trips ->
                        tripsAdapter.submitList(trips)
                        binding.noCurrentTrips.isVisible = trips.isNullOrEmpty() && !it.loading
                    }
                }
            }
        }
    }

    private fun navigateToNewTrip() {
        findNavController().navigate(HomeFragmentDirections.actionHomeFragmentToNewTripFragment())
    }
}
