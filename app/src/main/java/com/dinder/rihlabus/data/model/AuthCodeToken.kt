package com.dinder.rihlabus.data.model

import android.os.Parcelable
import com.google.firebase.auth.PhoneAuthProvider
import kotlinx.parcelize.Parcelize

@Parcelize
data class AuthCodeToken(val code: String, val token: PhoneAuthProvider.ForceResendingToken?) :
    Parcelable
