package com.dinder.rihlabus.ui.home.newTrip

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.os.Bundle
import android.text.Editable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.DatePicker
import android.widget.TimePicker
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.dinder.rihlabus.common.RihlaFragment
import com.dinder.rihlabus.data.model.Trip
import com.dinder.rihlabus.databinding.NewTripFragmentBinding
import com.google.type.DateTime
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import java.util.*


@AndroidEntryPoint
class NewTripFragment : RihlaFragment() {
    private val viewModel: NewTripViewModel by viewModels()
    private lateinit var binding: NewTripFragmentBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = NewTripFragmentBinding.inflate(inflater, container, false)
        setUI()
        return binding.root
    }

    private fun setUI() {
        binding.addTripButton.setOnClickListener {
            if (!_validForm()) {
                return@setOnClickListener
            }

            val trip = Trip(
                date = Date(),
                time = Date(),
                "Here",
                "There",
                1000,
                binding.newTripSeatView.getSeats()
            )
            viewModel.addTrip(trip)
        }

        binding.newTripDateContainer.editText?.setOnClickListener {
            showSnackbar("Click")
            val currentDate = DateTime.newBuilder()
            val dialog = DatePickerDialog(
                requireContext(),
                { _, year, month, dayOfMonth ->
                    binding.newTripDateContainer.editText?.setText("$dayOfMonth/$month/$year")
                },
                currentDate.year,
                currentDate.month,
                currentDate.day
            )
            dialog.setTitle("Pick date")
            dialog.show()
        }

        binding.newTripTimeContainer.editText?.setOnClickListener {
            showSnackbar("Click")
            val currentDate = DateTime.newBuilder()

            val dialog = TimePickerDialog(
                requireContext(),
                { _, hourOfDay, minute ->
                    binding.newTripTimeContainer.editText?.setText("$hourOfDay:$minute")
                },
                currentDate.hours,
                currentDate.minutes,
                false
            )
            dialog.setTitle("Pick date")
            dialog.show()
        }

        lifecycleScope.launchWhenStarted {
            viewModel.state.collect {
                binding.newTripProgressBar.isVisible = it.loading

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