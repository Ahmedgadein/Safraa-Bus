package com.dinder.rihlabus.ui.home.newTrip

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.dinder.rihlabus.common.RihlaFragment
import com.dinder.rihlabus.data.model.Trip
import com.dinder.rihlabus.databinding.NewTripFragmentBinding
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

        lifecycleScope.launchWhenStarted {
            viewModel.state.collect {
                binding.newTripProgressBar.isVisible = it.loading

                it.messages.firstOrNull()?.let { message ->
                    showSnackbar(message.content)
                    viewModel.userMessageShown(message.id)
                    return@collect
                }

                if(it.isAdded){
                    showSnackbar("Added successfully")
                    findNavController().navigateUp()
                    return@collect
                }
            }
        }
    }
}