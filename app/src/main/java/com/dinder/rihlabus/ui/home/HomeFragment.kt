package com.dinder.rihlabus.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import com.dinder.rihlabus.R
import com.dinder.rihlabus.common.RihlaFragment
import com.dinder.rihlabus.databinding.HomeFragmentBinding
import com.dinder.rihlabus.ui.home.currentTrips.CurrentTripsFragment
import com.dinder.rihlabus.ui.home.lastTrips.LastTripsFragment
import com.dinder.rihlabus.ui.home.settings.SettingsFragment

class HomeFragment : RihlaFragment() {

    private lateinit var binding: HomeFragmentBinding
    val fragments =
        listOf(CurrentTripsFragment(), LastTripsFragment(), SettingsFragment())

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
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

        binding.homeViewPagger.registerOnPageChangeCallback(object :
            ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                binding.bottomNavigationView.selectedItemId =
                    listOf(R.id.currentTrips, R.id.lastTrips, R.id.settings)[position]

            }
        })

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