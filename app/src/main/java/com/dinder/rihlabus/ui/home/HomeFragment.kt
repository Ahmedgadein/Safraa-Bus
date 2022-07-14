package com.dinder.rihlabus.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.dinder.rihlabus.R
import com.dinder.rihlabus.common.RihlaFragment
import com.dinder.rihlabus.databinding.HomeFragmentBinding
import com.dinder.rihlabus.ui.home.currentTrips.CurrentTripsFragment
import com.dinder.rihlabus.ui.home.lastTrips.LastTripsFragment
import com.dinder.rihlabus.ui.home.settings.SettingsFragment
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

@AndroidEntryPoint
class HomeFragment : RihlaFragment() {

    private lateinit var binding: HomeFragmentBinding
    private val viewModel: HomeViewModel by viewModels()
    val fragments =
        listOf(CurrentTripsFragment(), LastTripsFragment(), SettingsFragment())

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.state.collect {
                    if (it.navigateToUserVerificationScreen) {
                        navigateToUserVerificationScreen()
                    }
                }
            }
        }
    }

    private fun navigateToUserVerificationScreen() {
        findNavController().navigate(
            HomeFragmentDirections.actionHomeFragmentToVerifyCompanyUserScreen()
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = HomeFragmentBinding.inflate(inflater, container, false)
        setUI()
        return binding.root
    }

    private fun setUI() {
        binding.homeViewPagger.adapter = object : FragmentStateAdapter(this) {
            override fun getItemCount() = fragments.size

            override fun createFragment(position: Int) = fragments[position]
        }

        binding.homeViewPagger.isUserInputEnabled = false

        binding.bottomNavigationView.setOnItemSelectedListener {
            when (it.itemId) {
                R.id.currentTrips -> setFragment(0)
                R.id.lastTrips -> setFragment(1)
                R.id.settings -> setFragment(2)
            }
            true
        }
    }

    private fun setFragment(position: Int) {
        binding.homeViewPagger.currentItem = position
    }
}
