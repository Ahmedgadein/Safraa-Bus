package com.dinder.rihlabus.ui.home.currentTrips

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.dinder.rihlabus.R
import com.dinder.rihlabus.common.RihlaFragment
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class CurrentTripsFragment : RihlaFragment() {

    companion object {
        fun newInstance() = CurrentTripsFragment()
    }

    private lateinit var viewModel: CurrentTripsViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.current_trips_fragment, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(CurrentTripsViewModel::class.java)
        // TODO: Use the ViewModel
    }

}