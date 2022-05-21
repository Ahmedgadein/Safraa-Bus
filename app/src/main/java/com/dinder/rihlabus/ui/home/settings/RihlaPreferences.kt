package com.dinder.rihlabus.ui.home.settings

import android.content.SharedPreferences
import android.os.Bundle
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import androidx.preference.PreferenceManager
import com.dinder.rihlabus.R
import com.dinder.rihlabus.data.model.Company
import com.dinder.rihlabus.data.model.Destination
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import java.util.*

@AndroidEntryPoint
class RihlaPreferences : PreferenceFragmentCompat() {
    private val viewModel: SettingsViewModel by viewModels()
    private lateinit var preferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        preferences =
            PreferenceManager.getDefaultSharedPreferences(activity!!.baseContext)
        displayPreferences()
    }

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.preferences, rootKey)
        setUI()
    }

    private fun setUI() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.getUser()
                viewModel.state.collect { state ->
                    state.messages.firstOrNull()?.let {
                        showSnackbar(it.content)
                        viewModel.userMessageShown(it.id)
                    }
                    state.user?.let {
                        with(preferences.edit()) {
                            putString("name", it.name)
                            putString("phone", it.phoneNumber)
                            putString("companyName", getCompanyName(it.company))
                            putString("location", getCompanyLocation(it.location))
                            apply()
                        }
                        displayPreferences()
                    }
                }
            }
        }
    }

    private fun displayPreferences() {
        findPreference<Preference>("name")?.summary =
            preferences.getString("name", "NA")

        findPreference<Preference>("phone")?.summary =
            preferences.getString("phone", "NA")

        findPreference<Preference>("companyName")?.summary =
            preferences.getString("companyName", "NA")

        findPreference<Preference>("location")?.summary =
            preferences.getString("location", "NA")
    }

    private fun getCompanyName(company: Company?): String {
        company?.let {
            val isArabic = Locale.getDefault().language.equals(Locale("ar").language)
            return if (isArabic) company.arabicName else company.name
        }
        return "NA"
    }

    private fun getCompanyLocation(destination: Destination?): String {
        destination?.let {
            val isArabic = Locale.getDefault().language.equals(Locale("ar").language)
            return if (isArabic) destination.arabicName else destination.name
        }

        return "NA"
    }

    private fun showSnackbar(message: String) {
        Snackbar.make(view!!, message, Snackbar.LENGTH_SHORT)
            .show()
    }
}
