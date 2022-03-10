package com.dinder.rihlabus.ui.verification

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import com.dinder.rihlabus.R
import com.dinder.rihlabus.databinding.VerificationFragmentBinding

class VerificationFragment : Fragment() {
    private val viewModel:VerificationViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding = VerificationFragmentBinding.inflate(inflater, container, false)
        setUI(binding)

        return binding.root
    }

    private fun setUI(binding: VerificationFragmentBinding){
//        binding
    }
}