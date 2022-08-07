package com.dinder.rihlabus.ui.tripDetails

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.dinder.rihlabus.common.RihlaFragment
import com.dinder.rihlabus.databinding.TripDetailsFragmentBinding
import com.dinder.rihlabus.utils.SeatUtils
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

@AndroidEntryPoint
class TripDetailsFragment : RihlaFragment() {
    private val viewModel: TripDetailsViewModel by viewModels()
    private lateinit var binding: TripDetailsFragmentBinding
    private val args: TripDetailsFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = TripDetailsFragmentBinding.inflate(inflater, container, false)
        binding.viewOnly = args.viewOnly
        setUI()
        return binding.root
    }

    private fun setUI() {
        binding.tripDetailsShimmer.startShimmer()

        binding.seatDetailsButton.setOnClickListener {
            navigateToSeatDetails()
        }

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.getTrip(args.id)
                viewModel.state.collect {
                    it.loading.let { loading ->
                        if (loading) {
                            binding.tripDetailsShimmer.startShimmer()
                            binding.tripDetailsContent.visibility = View.GONE
                            binding.tripDetailsShimmer.visibility = View.VISIBLE
                        } else {
                            binding.tripDetailsContent.visibility = View.VISIBLE
                            binding.tripDetailsShimmer.visibility = View.GONE
                            binding.tripDetailsShimmer.stopShimmer()
                        }
                    }

                    it.messages.firstOrNull()?.let {
                        showSnackbar(it.content)
                        viewModel.userMessageShown(it.id)
                    }

                    it.trip?.let {
                        binding.trip = it
                        binding.tripDetailSeatView.setSeats(
                            SeatUtils.getSeatsListAsStateMap(it.seats)
                        )
                        binding.tripDetailSeatView.setOnSeatStateUpdateListener { seatNumber, seatState, passenger -> // ktlint-disable max-line-length
                            viewModel.updateSeatState(it.id!!, seatNumber, passenger, seatState)
                        }
                        binding.tripDetailSeatView.setOnShowBookedSeatPassengerDetails { seatNumber -> // ktlint-disable max-line-length
                            val passenger =
                                it.seats.firstOrNull() { seat -> seat.number == seatNumber }?.passenger
                            showPassengerDetailDialog(seatNumber, passenger)
                        }
                    }
                }
            }
        }
    }

    private fun showPassengerDetailDialog(seatNumber: Int, passengerName: String?) {
        AlertDialog.Builder(requireContext())
            .setTitle("Seat $seatNumber")
            .setMessage(if (passengerName.isNullOrEmpty()) "Booked locally" else passengerName)
            .setPositiveButton("Ok", null)
            .create().show()
    }

    private fun navigateToSeatDetails() {
        findNavController().navigate(
            TripDetailsFragmentDirections.actionTripDetailsFragmentToSeatDetailsFragment(
                args.id
            )
        )
    }
}
