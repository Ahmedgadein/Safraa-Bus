package com.dinder.rihlabus.ui.home.settings

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.dinder.rihlabus.common.RihlaFragment
import com.dinder.rihlabus.databinding.SettingsFragmentBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SettingsFragment : RihlaFragment() {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding = SettingsFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }
}
