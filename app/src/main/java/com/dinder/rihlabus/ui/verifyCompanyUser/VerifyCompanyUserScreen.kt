package com.dinder.rihlabus.ui.verifyCompanyUser

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.dinder.rihlabus.R
import com.dinder.rihlabus.common.RihlaFragment

class VerifyCompanyUserScreen : RihlaFragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_verify_company_user_screen, container, false)
    }
}
