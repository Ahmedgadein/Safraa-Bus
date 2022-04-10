package com.dinder.rihlabus.ui.newTrip

import android.app.DatePickerDialog
import android.app.TimePickerDialog
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
import com.dinder.rihlabus.R
import com.dinder.rihlabus.common.RihlaFragment
import com.dinder.rihlabus.data.model.Trip
import com.dinder.rihlabus.databinding.NewTripFragmentBinding
import com.dinder.rihlabus.utils.DateTimeUtils
import com.dinder.rihlabus.utils.SeatState
import com.dinder.rihlabus.utils.SeatUtils
import dagger.hilt.android.AndroidEntryPoint
import java.util.*
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

@AndroidEntryPoint
class NewTripFragment : RihlaFragment() {
    private val viewModel: NewTripViewModel by viewModels()
    private lateinit var binding: NewTripFragmentBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = NewTripFragmentBinding.inflate(inflater, container, false)
        setUI()
        return binding.root
    }

    private fun setUI() {
        binding.newTripSeatsCount.setText(
            binding.newTripSeatView.getSeats().values.filter { it == SeatState.SELECTED }
                .size.toString()
        )

        binding.newTripSeatView.setOnSeatSelectedListener {
            binding.newTripSeatsCount.setText(it.toString())
        }

        binding.newTripSelectAllButton.setOnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked) {
                binding.newTripSeatView.selectAll()
            } else {
                binding.newTripSeatView.unselectAll()
            }
        }

        binding.addTripButton.setOnClickListener {
            if (!_validForm()) {
                return@setOnClickListener
            }

            val trip = Trip(
                date = DateTimeUtils.getDateInstance(
                    binding.newTripDateContainer.editText?.text.toString()
                ),
                time = DateTimeUtils.getTimeInstance(
                    binding.newTripTimeContainer.editText?.text.toString()
                ),
                to = binding.newTripDestinationContainer.editText?.text.toString(),
                price = binding.newTripPriceContainer.editText?.text.toString().toInt(),
                seats = SeatUtils.getSelectedSeatsAsUnbooked(binding.newTripSeatView.getSeats())
            )

            viewModel.addTrip(trip)
        }

        binding.newTripDateContainer.editText?.setOnClickListener {
            val currentDate = Calendar.getInstance()
            val dialog = DatePickerDialog(
                requireContext(),
                { _, year, month, dayOfMonth ->
                    val date = resources.getString(R.string.formatted_date, dayOfMonth, month, year)
                    binding.newTripDateContainer.editText?.setText(date)
                },
                currentDate.get(Calendar.YEAR),
                currentDate.get(Calendar.MONTH),
                currentDate.get(Calendar.DAY_OF_MONTH)
            )
            dialog.setTitle("Pick date")
            dialog.show()
        }

        binding.newTripTimeContainer.editText?.setOnClickListener {
            val currentDate = Calendar.getInstance()

            val dialog = TimePickerDialog(
                requireContext(),
                { _, hourOfDay, minute ->
                    val time = resources.getString(R.string.formatted_time, hourOfDay, minute)
                    binding.newTripTimeContainer.editText?.setText(time)
                },
                currentDate.get(Calendar.HOUR_OF_DAY),
                currentDate.get(Calendar.MINUTE),
                true
            )
            dialog.setTitle("Pick date")
            dialog.show()
        }

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.state.collect {
                    with(binding) {
                        this.newTripProgressBar.isVisible = it.loading
                        this.addTripButton.isEnabled = !it.loading
                    }

                    it.messages.firstOrNull()?.let { message ->
                        showSnackbar(message.content)
                        viewModel.userMessageShown(message.id)
                        return@collect
                    }

                    if (it.isAdded) {
                        showSnackbar("Added successfully")
                        findNavController().navigateUp()
                        return@collect
                    }
                }
            }
        }
    }

    private fun _validForm(): Boolean {
        _validateDestination()
        _validateDate()
        _validateTime()
        _validatePrice()
        return with(binding) {
            return@with this.newTripDestinationContainer.helperText.isNullOrEmpty() &&
                this.newTripDateContainer.helperText.isNullOrEmpty() &&
                this.newTripTimeContainer.helperText.isNullOrEmpty() &&
                this.newTripPriceContainer.helperText.isNullOrEmpty()
        }
    }

    private fun _validateDestination(): String? {
        return with(binding.newTripDestinationContainer) {
            val message = if (this.editText?.text.isNullOrEmpty()) "Required" else null
            this.helperText = message
            return@with message
        }
    }

    private fun _validateDate(): String? {
        return with(binding.newTripDateContainer) {
            val message = if (this.editText?.text.isNullOrEmpty()) "Required" else null
            this.helperText = message
            return@with message
        }
    }

    private fun _validateTime(): String? {
        return with(binding.newTripTimeContainer) {
            val message = if (this.editText?.text.isNullOrEmpty()) "Required" else null
            this.helperText = message
            return@with message
        }
    }

    private fun _validatePrice(): String? {
        return with(binding.newTripPriceContainer) {
            val message = if (this.editText?.text.isNullOrEmpty()) "Required" else null
            this.helperText = message
            return@with message
        }
    }
}
