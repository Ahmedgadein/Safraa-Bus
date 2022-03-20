package com.dinder.rihlabus.ui.login

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.dinder.rihlabus.common.RihlaFragment
import com.dinder.rihlabus.data.model.Credential
import com.dinder.rihlabus.databinding.LoginFragmentBinding
import com.dinder.rihlabus.utils.PhoneNumberValidator
import com.google.firebase.FirebaseException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthOptions
import com.google.firebase.auth.PhoneAuthProvider
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import java.util.concurrent.TimeUnit

@AndroidEntryPoint
class LoginFragment : RihlaFragment() {

    private val viewModel: LoginViewModel by viewModels()
    private lateinit var binding: LoginFragmentBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = LoginFragmentBinding.inflate(inflater, container, false)
        setUI()

        return binding.root
    }

    private fun setUI() {
        binding.phoneNumber.addTextChangedListener {
            it?.let {
                val message = PhoneNumberValidator.validate(it.toString())
                binding.loginPhoneNumberContainer.helperText = message
            }
        }
        binding.signupButton.setOnClickListener {
            navigateToSignup()
        }

        binding.loginButton.setOnClickListener {
            if (!_validForm())
                return@setOnClickListener
            sendSms()
        }

        lifecycleScope.launch {
            viewModel.loginUiState.collect {
                it.messages.firstOrNull()?.let { message ->
                    showSnackbar(message.content)
                    viewModel.userMessageShown(message.id)
                    return@collect
                }

                if(it.isLoggedIn){
                    navigateToHome()
                    return@collect
                }

                if(!it.isRegistered && it.isLoggedIn){
                    navigateToSignup()
                    return@collect
                }
            }
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

    private fun sendSms() {
        val mobile = binding.loginPhoneNumberContainer.editText?.text.toString()
        val options = PhoneAuthOptions.newBuilder(FirebaseAuth.getInstance())
            .setPhoneNumber("+249$mobile")       // Phone number to verify
            .setTimeout(60L, TimeUnit.SECONDS)
            .setActivity(requireActivity())
            .setCallbacks(object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                override fun onVerificationCompleted(credentials: PhoneAuthCredential) {
                    viewModel.login(credentials, mobile)
                }

                override fun onVerificationFailed(exception: FirebaseException) {
                    Log.i("Ahmed", "onVerificationFailed: $exception")
                    showSnackbar("Login Failed Failed")
                }

                override fun onCodeSent(
                    code: String,
                    token: PhoneAuthProvider.ForceResendingToken
                ) {
                    super.onCodeSent(code, token)
                    Log.i("Ahmed", "onCodeSent: $code")
                    val codeToken = Credential(code = code, token = token, phoneNumber = mobile)
                    navigateToVerification(codeToken)
                }

            }).build()
        PhoneAuthProvider.verifyPhoneNumber(options)
    }

    private fun navigateToSignup() {
//        val action = LoginFragmentDirections.actionLoginFragmentToSignupFragment()
//        findNavController().navigate(action)
        showToast("Navigating to SIGNUP")
    }

    private fun navigateToHome() {
        val action = LoginFragmentDirections.actionLoginFragmentToHomeFragment()
        findNavController().navigate(action)
    }

    private fun navigateToVerification(credential: Credential) {
        val action = LoginFragmentDirections.actionLoginFragmentToVerificationFragment(credential)
        findNavController().navigate(action)
    }
}