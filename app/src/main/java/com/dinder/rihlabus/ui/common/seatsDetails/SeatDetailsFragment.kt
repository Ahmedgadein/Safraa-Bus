package com.dinder.rihlabus.ui.common.seatsDetails

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.navArgs
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.dinder.rihlabus.R
import com.dinder.rihlabus.common.RihlaFragment
import com.dinder.rihlabus.data.model.Seat
import com.dinder.rihlabus.databinding.SeatDetailsFragmentBinding
import com.dinder.rihlabus.ui.common.SeatsInfoFragment
import com.dinder.rihlabus.utils.SeatState
import com.google.android.material.tabs.TabLayoutMediator
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
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.getTrip(args.tripId)

                viewModel.state.collect {
                    binding.seatDetailProgressBar.isVisible = it.loading

                    it.messages.firstOrNull()?.let {
                        showSnackbar(it.content)
                        viewModel.userMessageShown(it.id)
                    }

                    val adapter = object : FragmentStateAdapter(this@SeatDetailsFragment) {
                        override fun getItemCount() = 3

                        override fun createFragment(position: Int): Fragment {
                            return SeatsInfoFragment.newInstance(
                                mapSeatsToIndex(
                                    it.seats,
                                    position
                                )
                            )
                        }
                    }

                    binding.seatsDetailsViewPager.adapter = adapter

                    TabLayoutMediator(
                        binding.tabLayout,
                        binding.seatsDetailsViewPager
                    ) { tab, position ->
                        tab.text = mapTabTitle(position)
                    }.attach()
                }
            }
        }
    }

    private fun mapSeatsToIndex(seats: List<Seat>, index: Int): List<Seat> {
        return when (index) {
            0 -> seats
            1 -> seats.filter { it.status == SeatState.UN_SELECTED }
            else -> seats.filter { it.status == SeatState.UNBOOKED }
        }
    }

    private fun mapTabTitle(position: Int): String {
        return when (position) {
            0 -> getString(R.string.all_seats)
            1 -> resources.getString(R.string.reserved_capitalized)
            else -> resources.getString(R.string.not_booked)
        }
    }
}
