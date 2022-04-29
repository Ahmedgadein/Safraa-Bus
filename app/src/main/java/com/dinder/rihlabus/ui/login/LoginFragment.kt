package com.dinder.rihlabus.ui.login

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.addTextChangedListener
import androidx.navigation.fragment.findNavController
import com.dinder.rihlabus.common.RihlaFragment
import com.dinder.rihlabus.databinding.LoginFragmentBinding
import com.dinder.rihlabus.utils.PhoneNumberFormatter
import com.dinder.rihlabus.utils.PhoneNumberValidator
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class LoginFragment : RihlaFragment() {

    private lateinit var binding: LoginFragmentBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = LoginFragmentBinding.inflate(inflater, container, false)
        setUI()

        return binding.root
    }

    private fun setUI() {
        binding.loginPhoneNumberContainer.editText?.addTextChangedListener {
            it?.let {
                val message = PhoneNumberValidator.validate(it.toString())
                binding.loginPhoneNumberContainer.helperText = message
            }
        }

        binding.loginButton.setOnClickListener {
            if (!_validForm()) {
                return@setOnClickListener
            }
            binding.loginPhoneNumberContainer.editText?.isEnabled = false
            val phoneNumber =
                PhoneNumberFormatter.getFullNumber(
                    binding.loginPhoneNumberContainer.editText?.text.toString()
                )
            navigateToVerification(phoneNumber)
        }
    }

    private fun _validateNumber() {
        with(binding.loginPhoneNumberContainer) {
            this.helperText = PhoneNumberValidator.validate(this.editText?.text.toString())
        }
    }

    private fun _validForm(): Boolean {
        _validateNumber()
        return binding.loginPhoneNumberContainer.helperText == null
    }

    private fun navigateToVerification(phoneNumber: String) {
        val action = LoginFragmentDirections.actionLoginFragmentToVerificationFragment(phoneNumber)
        findNavController().navigate(action)
    }
}
