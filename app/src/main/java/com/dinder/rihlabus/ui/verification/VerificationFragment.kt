package com.dinder.rihlabus.ui.verification

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
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
        inflater: LayoutInflater, container: ViewGroup?,
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
                Log.i("Ahmed", "Text: ${s.toString()}")
                Log.i("Ahmed", "Code Needed: ${credentials.code}")
                if (s?.length == Constants.VERIFICATION_CODE_LENGTH) {
                    val phoneAuthCredential =
                        PhoneAuthProvider.getCredential(credentials.code, s.toString())
                    viewModel.onNumberVerified(phoneAuthCredential, credentials.phoneNumber)
                }
            }

        })
        lifecycleScope.launch {
            viewModel.verificationUiState.collect {
                Log.i("Loading", "Verification Fragment Loading: ${it.loading}")
                binding.verificationProgressBar.isVisible = it.loading
                Log.i(
                    "Verification",
                    "Verification Fragment: logged=${it.isLoggedIn} registered=${it.isRegistered}"
                )

                it.messages.firstOrNull()?.let { message ->
                    showToast(message.content)
                    viewModel.userMessageShown(message.id)
                    return@collect
                }

                if (it.isRegistered && it.isLoggedIn) {
                    navigateToHome()
                    return@collect
                }

                if (!it.isRegistered && it.isLoggedIn) {
                    navigateToSignup(credentials.phoneNumber)
                    return@collect
                }

            }
        }
    }

    private fun navigateToHome() {
        val action = VerificationFragmentDirections.actionVerificationFragmentToHomeFragment()
        findNavController().navigate(action)
    }

    private fun navigateToSignup(phoneNumber: String) {
        val action =
            VerificationFragmentDirections.actionVerificationFragmentToSignupFragment(credentials.phoneNumber)
        findNavController().navigate(action)
    }
}