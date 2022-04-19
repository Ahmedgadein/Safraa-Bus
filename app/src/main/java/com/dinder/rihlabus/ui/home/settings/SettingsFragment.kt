package com.dinder.rihlabus.ui.home.settings

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.dinder.rihlabus.common.RihlaFragment
import com.dinder.rihlabus.databinding.SettingsFragmentBinding
import com.dinder.rihlabus.ui.home.HomeFragmentDirections
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SettingsFragment : RihlaFragment() {
    private val viewModel: SettingsViewModel by viewModels()
    private lateinit var binding: SettingsFragmentBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = SettingsFragmentBinding.inflate(inflater, container, false)
        setUI()
        return binding.root
    }

    private fun setUI() {
        binding.logoutButton.setOnClickListener {
            viewModel.logout()
            findNavController().navigate(
                HomeFragmentDirections.actionHomeFragmentToLoginFragment()
            )
        }
    }
}
