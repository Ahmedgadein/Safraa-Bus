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
import com.dinder.rihlabus.adapters.RihlaArrayAdapter
import com.dinder.rihlabus.common.RihlaFragment
import com.dinder.rihlabus.data.model.Company
import com.dinder.rihlabus.data.model.Destination
import com.dinder.rihlabus.data.model.Trip
import com.dinder.rihlabus.databinding.NewTripFragmentBinding
import com.dinder.rihlabus.utils.*
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import java.util.*
import javax.inject.Inject

@AndroidEntryPoint
class NewTripFragment : RihlaFragment() {
    private val viewModel: NewTripViewModel by viewModels()
    private lateinit var binding: NewTripFragmentBinding

    @Inject
    lateinit var errorMessages: ErrorMessages

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
            binding.addTripButton.isEnabled = it != 0
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
            if (!NetworkUtils.isNetworkConnected(requireContext())) {
                showSnackbar(errorMessages.noNetworkConnection)
                return@setOnClickListener
            }

            if (!_validForm()) {
                return@setOnClickListener
            }

            val trip = Trip(
                departure = DateTimeUtils.tripDeparture(
                    binding.newTripDateContainer.editText?.text.toString(),
                    binding.newTripTimeContainer.editText?.text.toString()
                ),
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
            dialog.setTitle(requireContext().resources.getString(R.string.pick_date))
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
                false
            )
            dialog.setTitle(requireContext().resources.getString(R.string.pick_time))
            dialog.show()
        }

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.state.collect {
                    with(binding) {
                        this.newTripProgressBar.isVisible = it.loading
                        this.addTripButton.isEnabled =
                            !it.loading && !SeatUtils.getSelectedSeatsAsUnbooked(
                            binding.newTripSeatView.getSeats()
                        ).isNullOrEmpty()
                    }

                    it.messages.firstOrNull()?.let { message ->
                        showSnackbar(message.content)
                        viewModel.userMessageShown(message.id)
                        return@collect
                    }

                    setLocationsDropdown(it.locations)
                    setCompaniesDropdown(it.companies)

                    if (it.isAdded) {
                        showSnackbar(requireContext().resources.getString(R.string.trip_added))
                        findNavController().navigateUp()
                        return@collect
                    }
                }
            }
        }
    }

    private fun setLocationsDropdown(destinations: List<Destination>) {
        val fromLocationsAdapter =
            RihlaArrayAdapter<Destination>(
                requireContext(),
                R.layout.dropdown_item,
                destinations.toMutableList()
            )

        val toLocationsAdapter =
            RihlaArrayAdapter<Destination>(
                requireContext(),
                R.layout.dropdown_item,
                destinations.toMutableList()
            )

        binding.fromDropdown.setAdapter(fromLocationsAdapter)
        binding.fromDropdown.setOnItemClickListener { _, _, position, _ ->
            val location = fromLocationsAdapter.getItem(
                position
            )
            viewModel.onFromLocationSelected(location)
        }

        binding.toDropdown.setAdapter(toLocationsAdapter)
        binding.toDropdown.setOnItemClickListener { _, _, position, _ ->
            val location = toLocationsAdapter.getItem(
                position
            )
            viewModel.onToLocationSelected(location)
        }
    }

    private fun setCompaniesDropdown(companies: List<Company>) {
        val companyAdapter =
            RihlaArrayAdapter<Company>(
                requireContext(),
                R.layout.dropdown_item,
                companies.toMutableList()
            )

        binding.companyDropdown.setAdapter(companyAdapter)
        binding.companyDropdown.setOnItemClickListener { _, _, position, _ ->
            val company = companyAdapter.getItem(
                position
            )
            viewModel.onCompanySelected(company)
        }
    }

    private fun _validForm(): Boolean {
        _validateDestinations()
        _validateCompany()
        _validateDate()
        _validateTime()
        _validatePrice()
        return with(binding) {
            return@with this.fromContainer.helperText.isNullOrEmpty() &&
                this.toContainer.helperText.isNullOrEmpty() &&
                this.companyContainer.helperText.isNullOrEmpty() &&
                this.newTripDateContainer.helperText.isNullOrEmpty() &&
                this.newTripTimeContainer.helperText.isNullOrEmpty() &&
                this.newTripPriceContainer.helperText.isNullOrEmpty()
        }
    }

    private fun _validateDestinations(): String? {
        return with(binding) {
            val fromMessage =
                if (viewModel.state.value.from == null) {
                    requireContext().resources.getString(
                        R.string.required
                    )
                } else null

            val toMessage = if (viewModel.state.value.to == null) {
                requireContext().resources.getString(
                    R.string.required
                )
            } else null

            this.fromContainer.helperText = fromMessage
            this.toContainer.helperText = toMessage

            return@with null
        }
    }

    private fun _validateCompany(): String? {
        return with(binding.companyContainer) {
            val message =
                if (this.editText?.text.isNullOrEmpty()) requireContext().resources.getString(
                    R.string.required
                ) else null
            this.helperText = message
            return@with message
        }
    }

    private fun _validateDate(): String? {
        return with(binding.newTripDateContainer) {
            val message =
                if (this.editText?.text.isNullOrEmpty()) requireContext().resources.getString(
                    R.string.required
                ) else null
            this.helperText = message
            return@with message
        }
    }

    private fun _validateTime(): String? {
        return with(binding.newTripTimeContainer) {
            val message =
                if (this.editText?.text.isNullOrEmpty()) requireContext().resources.getString(
                    R.string.required
                ) else null
            this.helperText = message
            return@with message
        }
    }

    private fun _validatePrice(): String? {
        return with(binding.newTripPriceContainer) {
            val message =
                if (this.editText?.text.isNullOrEmpty()) requireContext().resources.getString(
                    R.string.required
                ) else null
            this.helperText = message
            return@with message
        }
    }
}
