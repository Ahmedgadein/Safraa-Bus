package com.dinder.rihlabus.ui.login

import android.app.Activity
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.dinder.rihlabus.common.RihlaFragment
import com.dinder.rihlabus.data.model.AuthCodeToken
import com.dinder.rihlabus.databinding.LoginFragmentBinding
import com.dinder.rihlabus.utils.PhoneNumberValidator
import com.google.firebase.FirebaseException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthOptions
import com.google.firebase.auth.PhoneAuthProvider
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import java.util.concurrent.TimeUnit

@AndroidEntryPoint
class LoginFragment : RihlaFragment() {

    private val viewModel: LoginViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding = LoginFragmentBinding.inflate(inflater, container, false)
        setUI(binding)

        return binding.root
    }

    private fun setUI(binding: LoginFragmentBinding) {
        binding.phoneNumber.addTextChangedListener {
            it?.let {
                val message = PhoneNumberValidator.validate(it.toString())
                binding.phoneNumberContainer.helperText = message
            }
        }
        binding.signupButton.setOnClickListener {
            navigateToSignup()
        }

        binding.loginButton.setOnClickListener {
            with(binding.phoneNumberContainer.helperText) {
                if (this != null) {
                    showToast("Phone $this")
                    return@setOnClickListener
                }
            }
            val phone = binding.phoneNumber.text.toString()
            sendSms(phone)
        }

        lifecycleScope.launchWhenCreated {
            viewModel.loginUiState.collect {
                binding.loginProgressBar.visibility =
                    if (it.loading) View.VISIBLE else View.INVISIBLE

                it.messages.firstOrNull()?.let { message ->
                    showSnackbar(message.content)
                    viewModel.userMessageShown(message.id)
                }
            }
        }
    }

    private fun sendSms(mobile: String) {
        if (!viewModel.loginUiState.value.isRegistered) {
            showToast("Unregistered, please sign up")
            return
        }

        val options = PhoneAuthOptions.newBuilder(FirebaseAuth.getInstance())
            .setPhoneNumber("+249$mobile")       // Phone number to verify
            .setTimeout(60L, TimeUnit.SECONDS)
            .setActivity(this.activity!!)
            .setCallbacks(object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                override fun onVerificationCompleted(credentials: PhoneAuthCredential) {
                    viewModel.login(credentials)
                }

                override fun onVerificationFailed(exception: FirebaseException) {
                    Log.i("Ahmed", "onVerificationFailed: $exception")
                    showSnackbar("Verification Failed")
                }

                override fun onCodeSent(
                    code: String,
                    token: PhoneAuthProvider.ForceResendingToken
                ) {
                    super.onCodeSent(code, token)
                    Log.i("Ahmed", "onCodeSent: $code")
                    val codeToken = AuthCodeToken(code = code, token = token)
                    navigateToVerification(codeToken)
                }

            }).build()
        PhoneAuthProvider.verifyPhoneNumber(options)
    }

    private fun navigateToSignup() {
        val action = LoginFragmentDirections.actionLoginFragmentToSignupFragment()
        findNavController().navigate(action)
    }

    private fun navigateToHome() {

    }

    private fun navigateToVerification(codeToken: AuthCodeToken) {
        val action = LoginFragmentDirections.actionLoginFragmentToVerificationFragment(codeToken)
        findNavController().navigate(action)
    }
}