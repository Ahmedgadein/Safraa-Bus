package com.dinder.rihlabus.ui.home.lastTrips

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import com.dinder.rihlabus.R
import com.dinder.rihlabus.adapters.LastTripsAdapter
import com.dinder.rihlabus.common.RihlaFragment
import com.dinder.rihlabus.databinding.LastTripsFragmentBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

@AndroidEntryPoint
class LastTripsFragment : RihlaFragment() {
    private val viewModel: LastTripsViewModel by viewModels()
    private lateinit var binding: LastTripsFragmentBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = LastTripsFragmentBinding.inflate(inflater, container, false)
        setUI()
        return binding.root
    }

    private fun setUI() {
        binding.lastTripsShimmer.startShimmer()

        binding.lastTripsToolbar.setOnMenuItemClickListener {
            when (it.itemId) {
                R.id.filterTrips -> {
                    // TODO(implement trips filtering feature)
                    true
                }

                else -> false
            }
        }

        val tripsAdapter = LastTripsAdapter()
        binding.lastTripsRecyclerView.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = tripsAdapter
        }
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.getTrips()
                viewModel.state.collect {
                    it.loading.let { loading ->
                        if (loading) {
                            binding.lastTripsRecyclerView.visibility = View.GONE
                            binding.lastTripsShimmer.visibility = View.VISIBLE
                        } else {
                            binding.lastTripsRecyclerView.visibility = View.VISIBLE
                            binding.lastTripsShimmer.visibility = View.GONE
                            binding.lastTripsShimmer.stopShimmer()
                        }
                    }
                    it.messages.firstOrNull()?.let { message ->
                        showSnackbar(message.content)
                        viewModel.userMessageShown(message.id)
                    }
                    it.trips.let { trips ->
                        tripsAdapter.submitList(trips)
                        binding.noLastTrips.isVisible = trips.isNullOrEmpty() && !it.loading
                    }
                }
            }
        }
    }
}
