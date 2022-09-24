package com.dinder.rihlabus.ui.tripDetails

import android.app.AlertDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.core.view.setPadding
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.dinder.rihlabus.R
import com.dinder.rihlabus.common.RihlaFragment
import com.dinder.rihlabus.data.model.Seat
import com.dinder.rihlabus.databinding.ConfirmPaymentBottomSheetDialogBinding
import com.dinder.rihlabus.databinding.SeatDetailItemListBinding
import com.dinder.rihlabus.databinding.SecondPaymentConfirmationBottomSheetDialogBindingImpl
import com.dinder.rihlabus.databinding.TripDetailsFragmentBinding
import com.dinder.rihlabus.utils.SeatUtils
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.textfield.TextInputLayout
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

                        // Show Passenger
                        binding.tripDetailSeatView.setOnShowPassengerDetailsListener { seatNumber -> // ktlint-disable max-line-length
                            val seat = it.seats.firstOrNull { seat -> seat.number == seatNumber }
                            if (seat == null) {
                                // TODO: 1) Track this 2) Show message 3) Investigate cause
                            } else {
                                showPassengerDetailDialog(seat)
                            }
                        }

                        // Book Seat
                        binding.tripDetailSeatView.setOnBookSeatListener { seatNumber -> // ktlint-disable max-line-length
                            val seat = it.seats.firstOrNull { seat -> seat.number == seatNumber }
                            if (seat == null) {
                                // TODO: 1) Track this 2) Show message 3) Investigate cause
                            } else {
                                showBookSeatDialog(seat)
                            }
                        }

                        // Payment Confirmation
                        binding.tripDetailSeatView.setOnPaymentConfirmationListener { seatNumber -> // ktlint-disable max-line-length
                            val seat = it.seats.firstOrNull { seat -> seat.number == seatNumber }
                            showConfirmPaymentDialog(seat)
                        }
                    }
                }
            }
        }
    }

    private fun showPassengerDetailDialog(seat: Seat?) {
        val dialogBinding = SeatDetailItemListBinding.inflate(layoutInflater, null, false)
        dialogBinding.seat = seat
        dialogBinding.constrantLayout3.setPadding(50)
        dialogBinding.callPassengerButton.setOnClickListener {
            // Call Passenger Number
            val intent = Intent(Intent.ACTION_DIAL)
            intent.data = Uri.parse("tel:${seat?.passengerPhoneNumber}")
            startActivity(intent)
        }

        val dialogBuilder = AlertDialog
            .Builder(requireContext())
            .setView(dialogBinding.root)

        dialogBuilder.show()
    }

    private fun showBookSeatDialog(seat: Seat?) {
        val bottomSheetDialog = BottomSheetDialog(requireContext())
        bottomSheetDialog.setContentView(R.layout.book_seat_bottom_sheet_dialog)
        bottomSheetDialog.findViewById<Button>(R.id.reserveSeatButton)?.setOnClickListener {
            bottomSheetDialog.dismiss()
            val name =
                bottomSheetDialog.findViewById<TextInputLayout>(R.id.reserveSeatNameContainer)
                    ?.editText
                    ?.text
                    .toString()
            viewModel.bookSeat(args.id, seat?.number!!, name)
        }

        bottomSheetDialog.show()
    }

    private fun showConfirmPaymentDialog(seat: Seat?) {
        val dialogBinding =
            ConfirmPaymentBottomSheetDialogBinding.inflate(layoutInflater, null, false)
        dialogBinding.seat = seat

        val bottomSheetDialog = BottomSheetDialog(requireContext())
        bottomSheetDialog.setContentView(dialogBinding.root)

        // Payment Confirmed
        bottomSheetDialog.findViewById<Button>(R.id.paymentConfirmedButton)?.setOnClickListener {
            bottomSheetDialog.dismiss()
            viewModel.confirmPayment(args.id, seat?.passenger!!)
        }
        // Payment Declined
        bottomSheetDialog.findViewById<Button>(R.id.paymentDeclinedButton)?.setOnClickListener {
            bottomSheetDialog.dismiss()
            showPaymentNotMadeConfirmationDialog(seat)
        }

        bottomSheetDialog.show()
    }

    private fun showPaymentNotMadeConfirmationDialog(seat: Seat?) {
        val dialogBinding =
            SecondPaymentConfirmationBottomSheetDialogBindingImpl.inflate(
                layoutInflater,
                null,
                false
            )
        dialogBinding.seat = seat

        val bottomSheetDialog = BottomSheetDialog(requireContext())
        bottomSheetDialog.setContentView(dialogBinding.root)

        bottomSheetDialog.findViewById<Button>(R.id.secondPaymentDeclineConfirmation)
            ?.setOnClickListener {
                bottomSheetDialog.dismiss()
                viewModel.disprovePayment(args.id, seat?.passenger!!)
            }

        bottomSheetDialog.show()
    }

    private fun navigateToSeatDetails() {
        findNavController().navigate(
            TripDetailsFragmentDirections.actionTripDetailsFragmentToSeatDetailsFragment(
                args.id
            )
        )
    }
}
