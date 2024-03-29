package com.dinder.rihlabus.ui.update

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.dinder.rihlabus.R
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class UpdateAppFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.update_app_fragment, container, false)
    }
}
