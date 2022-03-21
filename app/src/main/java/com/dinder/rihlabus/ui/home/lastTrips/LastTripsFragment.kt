package com.dinder.rihlabus.ui.home.lastTrips

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
class LastTripsFragment : RihlaFragment() {

    companion object {
        fun newInstance() = LastTripsFragment()
    }

    private lateinit var viewModel: LastTripsViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.last_trips_fragment, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(LastTripsViewModel::class.java)
        // TODO: Use the ViewModel
    }

}