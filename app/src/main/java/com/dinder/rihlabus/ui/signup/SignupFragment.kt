package com.dinder.rihlabus.ui.signup

import android.graphics.Color
import android.os.Bundle
import android.text.SpannableString
import android.text.Spanned
import android.text.TextPaint
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
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
import com.dinder.rihlabus.R
import com.dinder.rihlabus.adapters.RihlaArrayAdapter
import com.dinder.rihlabus.common.RihlaFragment
import com.dinder.rihlabus.data.model.Company
import com.dinder.rihlabus.data.model.Destination
import com.dinder.rihlabus.data.model.User
import com.dinder.rihlabus.databinding.SignupFragmentBinding
import com.dinder.rihlabus.utils.NameValidator
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import java.util.*

@AndroidEntryPoint
class SignupFragment : RihlaFragment() {
    private val viewModel: SignupViewModel by viewModels()
    private lateinit var binding: SignupFragmentBinding
    private val args: SignupFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = SignupFragmentBinding.inflate(inflater, container, false)
        setUI()
        return binding.root
    }

    private fun setUI() {
        _setTermsAndConditionsView()

        binding.signupButton.setOnClickListener {
            if (!_validForm()) {
                return@setOnClickListener
            }
            with(binding) {
                signupCompanyContainer.editText?.isEnabled = false
                signupNameContainer.editText?.isEnabled = false
                signupLocationContainer.editText?.isEnabled = false
            }
            viewModel.signup(
                user = User(
                    id = "",
                    name = binding.signupNameContainer.editText?.text.toString(),
                    phoneNumber = args.phoneNumber
                )
            )
        }

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.state.collect {
                    with(it.loading) {
                        binding.signupProgressBar.isVisible = this
                        binding.signupCompanyContainer.editText?.isEnabled = this.not()
                        binding.signupNameContainer.editText?.isEnabled = this.not()
                        binding.signupLocationContainer.editText?.isEnabled = this.not()
                    }
                    it.messages.firstOrNull()?.let { message ->
                        showSnackbar(message.content)
                        viewModel.userMessageShown(message.id)
                    }

                    setLocationsDropdown(it.locations)
                    setCompaniesDropdown(it.companies)

                    if (it.navigateToHome) {
                        navigateToHome()
                        return@collect
                    }
                }
            }
        }
    }

    private fun setCompaniesDropdown(companies: List<Company>) {
        val companiesAdapter =
            RihlaArrayAdapter<Company>(
                requireContext(),
                R.layout.dropdown_item,
                companies.toMutableList()
            )
        binding.companiesDropDown.setAdapter(companiesAdapter)
        binding.companiesDropDown.setOnItemClickListener { _, _, position, _ ->
            val company = companiesAdapter.getItem(position)
            viewModel.onCompanySelected(company)
        }
    }

    private fun setLocationsDropdown(destinations: List<Destination>) {
        val locationsAdapter =
            RihlaArrayAdapter<Destination>(
                requireContext(),
                R.layout.dropdown_item,
                destinations.toMutableList()
            )
        binding.locationDropDown.setAdapter(locationsAdapter)
        binding.locationDropDown.setOnItemClickListener { _, _, position, _ ->
            val location = locationsAdapter.getItem(
                position
            )
            viewModel.onLocationSelected(location)
        }
    }

    private fun _setTermsAndConditionsView() {
        val termsAndConditions =
            SpannableString(getString(R.string.terms_and_conditions_label))

        val onShowTermsAndConditions = object : ClickableSpan() {

            override fun updateDrawState(ds: TextPaint) {
                ds.color = resources.getColor(R.color.teal_700, context?.theme)
                ds.isUnderlineText = false
            }

            override fun onClick(widget: View) {
                showToast("Terms And Conditions")
                view?.invalidateOutline()
            }
        }

        if (Locale.getDefault().language.equals(Locale("ar").language)) {
            termsAndConditions.setSpan(
                onShowTermsAndConditions,
                19,
                35,
                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
            )
        } else {
            termsAndConditions.setSpan(
                onShowTermsAndConditions,
                termsAndConditions.indexOf("terms"),
                termsAndConditions.indexOf("conditions") + "conditions".length,
                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
            )
        }
        binding.termsAndConditions.apply {
            text = termsAndConditions
            movementMethod = LinkMovementMethod.getInstance()
            highlightColor = Color.TRANSPARENT
        }
    }

    private fun _validateName() {
        with(binding.signupNameContainer) {
            this.helperText = NameValidator.validate(this.editText?.text.toString())
        }
    }

    private fun _validateCompany() {
        with(binding.signupCompanyContainer) {
            this.helperText =
                if (viewModel.state.value.selectedCompany == null) "Required" else null
        }
    }

    private fun _validateLocation() {
        with(binding.signupLocationContainer) {
            this.helperText =
                if (viewModel.state.value.selectedLocation == null) "Required" else null
        }
    }

    private fun _validForm(): Boolean {
        _validateName()
        _validateCompany()
        _validateLocation()
        return binding.let {
            it.signupNameContainer.helperText == null
        }
    }

    private fun navigateToHome() {
        val action = SignupFragmentDirections.actionSignupFragmentToHomeFragment()
        findNavController().navigate(action)
    }
}
