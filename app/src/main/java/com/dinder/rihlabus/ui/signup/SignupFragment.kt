package com.dinder.rihlabus.ui.signup

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import android.text.SpannableString
import android.text.Spanned
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import com.dinder.rihlabus.R
import com.dinder.rihlabus.common.RihlaFragment
import com.dinder.rihlabus.databinding.SignupFragmentBinding

class SignupFragment : RihlaFragment() {

    val companies = arrayListOf<String>("Ahmed", "Ali")

    companion object {
        fun newInstance() = SignupFragment()
    }

    private lateinit var viewModel: SignupViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding = SignupFragmentBinding.inflate(inflater, container, false)
        val companies = resources.getStringArray(R.array.companies)
        val locations = resources.getStringArray(R.array.locations)

        val companiesAdapter = ArrayAdapter(requireContext(), R.layout.dropdown_item, companies)
        val locationsAdapter = ArrayAdapter(requireContext(), R.layout.dropdown_item, locations)

        binding.companiesDropDown.setAdapter(locationsAdapter)
        binding.locationDropDown.setAdapter(locationsAdapter)

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
        return binding.root
    }

}