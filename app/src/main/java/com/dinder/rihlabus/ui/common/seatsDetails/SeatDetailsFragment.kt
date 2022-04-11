package com.dinder.rihlabus.ui.common.seatsDetails

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.dinder.rihlabus.adapters.SeatAdapter
import com.dinder.rihlabus.common.RihlaFragment
import com.dinder.rihlabus.databinding.SeatDetailsFragmentBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

@AndroidEntryPoint
class SeatDetailsFragment : RihlaFragment() {
    private val viewModel: SeatDetailsViewModel by viewModels()
    private val args: SeatDetailsFragmentArgs by navArgs()
    private lateinit var binding: SeatDetailsFragmentBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = SeatDetailsFragmentBinding.inflate(inflater, container, false)
        setUI()
        return binding.root
    }

    private fun setUI() {
        val seatAdapter = SeatAdapter()
        binding.seatDetailsRecyclerView.apply {
            adapter = seatAdapter
            layoutManager = LinearLayoutManager(requireContext())
        }

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.getTrip(args.tripId)

                viewModel.state.collect {
                    it.messages.firstOrNull()?.let { message ->
                        showSnackbar(message.content)
                        viewModel.userMessageShown(message.id)
                    }

                    binding.seatDetailProgressBar.isVisible = it.loading
                    seatAdapter.submitList(it.seats)
                }
            }
        }
    }
}
