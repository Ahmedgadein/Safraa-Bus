package com.dinder.rihlabus.ui.signup

import android.os.Bundle
import android.text.SpannableString
import android.text.Spanned
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.dinder.rihlabus.R
import com.dinder.rihlabus.common.RihlaFragment
import com.dinder.rihlabus.data.model.AuthCodeToken
import com.dinder.rihlabus.data.model.Company
import com.dinder.rihlabus.databinding.SignupFragmentBinding
import com.dinder.rihlabus.utils.NameValidator
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
class SignupFragment : RihlaFragment() {
    private val viewModel: SignupViewModel by viewModels()
    private lateinit var binding: SignupFragmentBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = SignupFragmentBinding.inflate(inflater, container, false)
        _setUI()

        return binding.root
    }

    private fun _setUI() {
        _setCompaniesDropdown()
        _setLocationsDropdown()
        _setTermsAndConditionsView()

        binding.signupButton.setOnClickListener {
            if (!_validForm())
                return@setOnClickListener

            sendSms()
        }

        lifecycleScope.launchWhenCreated {
            viewModel.signupUiState.collect {
                it.messages.firstOrNull()?.let { message ->
                    showSnackbar(message.content)
                    viewModel.userMessageShown(message.id)
                }

                if (it.isRegistered && it.isLoggedIn)
                    navigateToHome()
            }
        }
    }

    private fun _setCompaniesDropdown() {
        val companies = resources.getStringArray(R.array.companies)
        val companiesAdapter = ArrayAdapter(requireContext(), R.layout.dropdown_item, companies)
        binding.companiesDropDown.setAdapter(companiesAdapter)
    }

    private fun _setLocationsDropdown() {
        val locations = resources.getStringArray(R.array.locations)
        val locationsAdapter = ArrayAdapter(requireContext(), R.layout.dropdown_item, locations)
        binding.locationDropDown.setAdapter(locationsAdapter)
    }

    private fun _setTermsAndConditionsView() {
        val termsAndConditions =
            SpannableString("By signing up you agree to the terms and conditions of Rihla")

        val onShowTermsAndConditions = object : ClickableSpan() {
            override fun onClick(widget: View) {
                showToast("Terms And Conditions")
            }
        }
        termsAndConditions.setSpan(
            onShowTermsAndConditions,
            termsAndConditions.indexOf("terms"),
            termsAndConditions.indexOf("conditions") + "conditions".length,
            Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
        )

        binding.termsAndConditions.apply {
            text = termsAndConditions
            movementMethod = LinkMovementMethod.getInstance()
        }
    }

    private fun _validateName() {
        with(binding.signupNameContainer) {
            this.helperText = NameValidator.validate(this.editText?.text.toString())
        }
    }

    private fun _validateNumber() {
        with(binding.signupPhoneNumberContainer) {
            this.helperText = PhoneNumberValidator.validate(this.editText?.text.toString())
        }
    }

    private fun _validateCompany() {
        with(binding.signupCompanyContainer) {
            this.helperText = if (this.editText?.text.isNullOrEmpty()) "Required" else null
        }
    }

    private fun _validateLocation() {
        with(binding.signupLocationContainer) {
            this.helperText = if (this.editText?.text.isNullOrEmpty()) "Required" else null
        }
    }

    private fun _validForm(): Boolean {
        _validateNumber()
        _validateName()
        _validateCompany()
        _validateLocation()
        return binding.let {
            it.signupPhoneNumberContainer.helperText == null && it.signupNameContainer.helperText == null
        }
    }

    private fun navigateToVerification(codeToken: AuthCodeToken) {
        val action = SignupFragmentDirections.actionSignupFragmentToVerificationFragment(codeToken)
        findNavController().navigate(action)
    }

    private fun sendSms() {
        val mobile = binding.signupPhoneNumberContainer.editText?.text.toString()
        val options = PhoneAuthOptions.newBuilder(FirebaseAuth.getInstance())
            .setPhoneNumber("+249$mobile")       // Phone number to verify
            .setTimeout(60L, TimeUnit.SECONDS)
            .setActivity(this.activity!!)
            .setCallbacks(object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                override fun onVerificationCompleted(credentials: PhoneAuthCredential) {
                    val name = binding.signupNameContainer.editText?.text.toString()
                    val phoneNumber = binding.signupPhoneNumberContainer.editText?.text.toString()
                    val company = binding.signupCompanyContainer.editText?.text.toString()
                    val location = binding.signupLocationContainer.editText?.text.toString()

                    viewModel.signup(
                        credentials,
                        name,
                        phoneNumber,
                        Company(name = company, location = location)
                    )
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

    fun navigateToHome() {

    }
}