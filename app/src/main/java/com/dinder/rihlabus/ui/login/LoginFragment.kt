package com.dinder.rihlabus.ui.login

import android.app.Activity
import android.os.Bundle
import android.text.BoringLayout.make
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.dinder.rihlabus.R
import com.dinder.rihlabus.databinding.LoginFragmentBinding
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.FirebaseException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthOptions
import com.google.firebase.auth.PhoneAuthProvider
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import java.util.concurrent.TimeUnit

@AndroidEntryPoint
class LoginFragment : Fragment() {

    private val viewModel: LoginViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding = LoginFragmentBinding.inflate(inflater, container, false)
        setUI(binding)

        return binding.root
    }

    private fun setUI(binding: LoginFragmentBinding) {
        binding.signupButton.setOnClickListener {
            navigateToSignup()
        }

        binding.loginButton.setOnClickListener {
            val mobile = "+249" + binding.phoneNumber.text.toString()
            sendSms(mobile)
        }

        lifecycleScope.launch {
            viewModel.loginUiState.collect {
                if (it.isLoggedIn) {
                    navigateToHome()
                }

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
            .setPhoneNumber(mobile)       // Phone number to verify
            .setTimeout(60L, TimeUnit.SECONDS)
            .setActivity(activity as Activity)
            .setCallbacks(object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                override fun onVerificationCompleted(credentials: PhoneAuthCredential) {
                    viewModel.login(credentials)
                }

                override fun onVerificationFailed(exception: FirebaseException) {
                    showSnackbar("Verification Failed")
                }

                override fun onCodeSent(code: String, token: PhoneAuthProvider.ForceResendingToken) {
                    super.onCodeSent(code, token)
                    navigateToVerification()
                }

            })// Timeout and unit
            .build()
        PhoneAuthProvider.verifyPhoneNumber(options)
    }

    private fun navigateToSignup() {
        val action = LoginFragmentDirections.actionLoginFragmentToSignupFragment()
        findNavController().navigate(action)
    }

    private fun showSnackbar(message: String) {
        Snackbar.make(view!!, message, Snackbar.LENGTH_SHORT)
            .show()
    }

    private fun showToast(message: String) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT)
            .show()
    }

    private fun navigateToHome() {

    }

    private fun navigateToVerification() {
        val action = LoginFragmentDirections.actionLoginFragmentToVerification()
        findNavController().navigate(action)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
    }
}