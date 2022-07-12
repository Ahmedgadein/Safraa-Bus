package com.dinder.rihlabus.utils

import android.content.res.Resources
import com.dinder.rihlabus.R
import javax.inject.Inject

class ErrorMessages @Inject constructor(private val resources: Resources) {

    private fun getResource(id: Int): String = resources.getString(id)

    val failedToCheckRegistration
        get() = getResource(R.string.failed_to_check_registration)

    val loginFailed
        get() = getResource(R.string.failed_to_check_registration)

    val registrationFailed
        get() = getResource(R.string.registration_failed)

    val failedToAddCompany
        get() = getResource(R.string.add_company_failed)

    val failedToLoadCompanies
        get() = getResource(R.string.loading_companies_failed)

    val failedToLoadDestinations
        get() = getResource(R.string.loading_destinations_failed)

    val failedToAddTrip
        get() = getResource(R.string.add_trip_failed)

    val failedToLoadCurrentTrips
        get() = getResource(R.string.loading_current_trips_failed)

    val failedToLoadLastTrips
        get() = getResource(R.string.loading_last_trips_failed)

    val failedToLoadTrip
        get() = getResource(R.string.loading_trip_failed)

    val failedToUpdateSeatInfo
        get() = getResource(R.string.failed_to_update_seat)

    val couldntFindUser
        get() = getResource(R.string.couldnt_find_user)

    val failedToResolveAppVersion
        get() = getResource(R.string.failed_to_resolve_app_version)
}
