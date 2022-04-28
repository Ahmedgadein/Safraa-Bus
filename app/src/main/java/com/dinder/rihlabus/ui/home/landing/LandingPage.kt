package com.dinder.rihlabus.ui.home.landing

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.dinder.rihlabus.common.RihlaFragment
import com.dinder.rihlabus.databinding.LandingPageFragmentBinding
import com.dinder.rihlabus.ui.login.LandingPageViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

@AndroidEntryPoint
class LandingPage : RihlaFragment() {
    private val viewModel: LandingPageViewModel by viewModels()

    private lateinit var binding: LandingPageFragmentBinding
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = LandingPageFragmentBinding.inflate(inflater, container, false)
        setUI()
        return binding.root
    }

    private fun setUI() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.state.collect {
                    if (it.navigateToHome) {
                        navigateToHome()
                        return@collect
                    }

                    if (it.navigateToLogin) {
                        navigateToLogin()
                    }
                }
            }
        }
    }

    private fun navigateToHome() {
        findNavController().navigate(LandingPageDirections.actionLandingPageToHomeFragment())
    }

    private fun navigateToLogin() {
        findNavController().navigate(LandingPageDirections.actionLandingPageToLoginFragment())
    }
}
