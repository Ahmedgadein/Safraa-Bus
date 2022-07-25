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
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.dinder.rihlabus.common.Constants
import com.dinder.rihlabus.common.RihlaFragment
import com.dinder.rihlabus.databinding.VerificationFragmentBinding
import com.google.firebase.FirebaseException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthOptions
import com.google.firebase.auth.PhoneAuthProvider
import com.mixpanel.android.mpmetrics.MixpanelAPI
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import org.json.JSONObject
import java.util.concurrent.TimeUnit
import javax.inject.Inject

@AndroidEntryPoint
class VerificationFragment : RihlaFragment() {
    private val viewModel: VerificationViewModel by viewModels()
    private lateinit var binding: VerificationFragmentBinding
    private var verificationID: String = "placeholder" // prevents creating credentials crash
    private val args: VerificationFragmentArgs by navArgs()

    @Inject
    lateinit var mixpanelAPI: MixpanelAPI

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        sendSms()
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
                    val credentials =
                        PhoneAuthProvider.getCredential(verificationID, s.toString())
                    viewModel.onVerificationAttempt(credentials, args.phoneNumber)
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
                args.phoneNumber
            )
        findNavController().navigate(action)
    }

    private fun sendSms() {
        val options = PhoneAuthOptions.newBuilder(FirebaseAuth.getInstance())
            .setPhoneNumber(args.phoneNumber) // Phone number to verify
            .setTimeout(60L, TimeUnit.SECONDS)
            .setActivity(requireActivity())
            .setCallbacks(object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                override fun onVerificationCompleted(credentials: PhoneAuthCredential) {
                    viewModel.onVerificationAttempt(credentials, args.phoneNumber)
                }

                override fun onVerificationFailed(exception: FirebaseException) {
                    trackVerificationFailed()
                    Log.i("Ahmed", "onVerificationFailed: $exception")
                }

                override fun onCodeSent(
                    verificationId: String,
                    token: PhoneAuthProvider.ForceResendingToken
                ) {
                    super.onCodeSent(verificationId, token)
                    trackVerificationSend()
                    verificationID = verificationId
                }
            }).build()
        PhoneAuthProvider.verifyPhoneNumber(options)
    }

    private fun trackVerificationSend() {
        val props = JSONObject().apply {
            put("Code", "Send")
        }
        mixpanelAPI.track("Verification", props)
    }

    private fun trackVerificationFailed() {
        val props = JSONObject().apply {
            put("Code", "Not Send")
        }
        mixpanelAPI.track("Verification", props)
    }
}
