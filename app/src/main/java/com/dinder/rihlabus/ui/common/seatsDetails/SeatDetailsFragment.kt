package com.dinder.rihlabus.ui.common.seatsDetails

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import com.dinder.rihlabus.common.RihlaFragment
import com.dinder.rihlabus.databinding.SeatDetailsFragmentBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SeatDetailsFragment : RihlaFragment() {
    private val viewModel: SeatDetailsViewModel by viewModels()
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
    }
}
