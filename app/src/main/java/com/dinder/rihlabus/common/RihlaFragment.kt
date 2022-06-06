package com.dinder.rihlabus.common

import android.widget.Toast
import androidx.fragment.app.Fragment
import com.google.android.material.snackbar.Snackbar

open class RihlaFragment : Fragment() {
    protected fun showSnackbar(message: String) {
        Snackbar.make(view!!, message, Snackbar.LENGTH_LONG)
            .show()
    }

    protected fun showToast(message: String) {
        Toast.makeText(context, message, Toast.LENGTH_LONG)
            .show()
    }
}
