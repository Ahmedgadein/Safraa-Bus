package com.dinder.rihlabus.ui.verification

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.dinder.rihlabus.common.Constants
import com.dinder.rihlabus.common.RihlaFragment
import com.dinder.rihlabus.data.model.Credential
import com.dinder.rihlabus.databinding.VerificationFragmentBinding
import com.google.firebase.auth.PhoneAuthProvider
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

@AndroidEntryPoint
class VerificationFragment : RihlaFragment() {
    private val viewModel: VerificationViewModel by viewModels()
    private lateinit var binding: VerificationFragmentBinding
    lateinit var credentials: Credential

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        credentials = VerificationFragmentArgs.fromBundle(arguments!!).credentials
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = VerificationFragmentBinding.inflate(inflater, container, false)
        setUI()

        return binding.root
    }

    private fun setUI() {
        binding.verificationCode.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }

            override fun afterTextChanged(s: Editable?) {
                if (s?.length == Constants.VERIFICATION_CODE_LENGTH) {
                    binding.verificationCode.isEnabled = false
                    val phoneAuthCredential =
                        PhoneAuthProvider.getCredential(credentials.code, s.toString())
                    viewModel.onVerificationAttempt(phoneAuthCredential, credentials.phoneNumber)
                }
            }
        })
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.state.collect {
                    with(it.loading) {
                        binding.verificationProgressBar.isVisible = this
                        binding.verificationCode.isEnabled = this.not()
                    }

                    it.messages.firstOrNull()?.let { message ->
                        showSnackbar(message.content)
                        viewModel.userMessageShown(message.id)
                        return@collect
                    }

                    if (it.navigateToHome) {
                        navigateToHome()
                        return@collect
                    }

                    if (it.navigateToSignup) {
                        navigateToSignup()
                        return@collect
                    }
                }
            }
        }
    }

    private fun navigateToHome() {
        val action = VerificationFragmentDirections.actionVerificationFragmentToHomeFragment()
        findNavController().navigate(action)
    }

    private fun navigateToSignup() {
        val action =
            VerificationFragmentDirections.actionVerificationFragmentToSignupFragment(
                credentials.phoneNumber
            )
        findNavController().navigate(action)
    }
}
