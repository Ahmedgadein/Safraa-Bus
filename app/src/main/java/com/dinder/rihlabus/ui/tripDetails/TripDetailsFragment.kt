package com.dinder.rihlabus.ui.tripDetails

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
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
        setUI()
        return binding.root
    }

    private fun setUI() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.getTrip(args.id)
                viewModel.state.collect {
                    binding.tripDetailProgressBar.isVisible = it.loading

                    it.messages.firstOrNull()?.let {
                        showSnackbar(it.content)
                        viewModel.userMessageShown(it.id)
                    }

                    it.trip?.let {
                        binding.trip = it
                        binding.tripDetailSeatView.setSeats(SeatUtils.getRemoteSeats(it.seats))
                        binding.tripDetailSeatView.setOnSeatStateUpdateListener { seatNumber, seatState ->
                            viewModel.updateSeatState(it.id!!, seatNumber, seatState)
                        }
                        binding.tripDetailSeatView.setOnShowBookedSeatPassengerDetails { seatNumber ->
                            val passenger = it.seats["$seatNumber"]?.get("passenger") as String?
                            showPassengerDetailDialog(seatNumber, passenger)
                        }
                        Log.i("SeatView", "Seats: ${SeatUtils.getRemoteSeats(it.seats)}")
                    }
                }
            }
        }
    }

    private fun showPassengerDetailDialog(seatNumber: Int, passengerName: String?) {
        AlertDialog.Builder(requireContext())
            .setTitle("Seat $seatNumber")
            .setMessage(if(passengerName.isNullOrEmpty()) "Booked locally" else passengerName)
            .setPositiveButton("Ok",null)
            .create().show()
    }
}
