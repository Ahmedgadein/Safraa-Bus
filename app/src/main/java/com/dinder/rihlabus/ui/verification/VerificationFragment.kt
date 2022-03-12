package com.dinder.rihlabus.ui.verification

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.dinder.rihlabus.common.Constants
import com.dinder.rihlabus.common.RihlaFragment
import com.dinder.rihlabus.databinding.VerificationFragmentBinding
import com.google.firebase.auth.PhoneAuthProvider
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect

@AndroidEntryPoint
class VerificationFragment : RihlaFragment() {
    private val viewModel: VerificationViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding = VerificationFragmentBinding.inflate(inflater, container, false)
        setUI(binding)

        return binding.root
    }

    private fun setUI(binding: VerificationFragmentBinding) {
        val authCodeToken = VerificationFragmentArgs.fromBundle(arguments!!).codeToken

        binding.verificationCode.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }

            override fun afterTextChanged(s: Editable?) {
                Log.i("Ahmed", "Text: ${s.toString()}")
                Log.i("Ahmed", "Code Needed: ${authCodeToken.code}")
                if (s?.length == Constants.VERIFICATION_CODE_LENGTH) {
                    showToast("Got it!")
                    val credentials =
                        PhoneAuthProvider.getCredential(authCodeToken.code, s.toString())
                    viewModel.onNumberVerified(credentials)
                }
            }

        })
        lifecycleScope.launchWhenCreated {
            viewModel.verificationUiState.collect {
                binding.verificationProgressBar.visibility =
                    if (it.loading) View.VISIBLE else View.INVISIBLE

                if (it.isLoggedIn)
                    navigateToHome()

                it.messages.firstOrNull()?.let { message ->
                    showToast(message.content)
                    viewModel.userMessageShown(message.id)
                }
            }
        }
    }

    private fun navigateToHome() {
        showToast("MADE IT MAAAAAAAAAAAAAAAAAN")
    }
}