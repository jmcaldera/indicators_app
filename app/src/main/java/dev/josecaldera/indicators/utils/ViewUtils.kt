package dev.josecaldera.indicators.utils

import android.view.inputmethod.InputMethodManager
import androidx.fragment.app.Fragment

fun Fragment.hideSoftKeyboard() {
    val imm: InputMethodManager? = requireActivity()
        .getSystemService(
            android.content.Context.INPUT_METHOD_SERVICE
        ) as? InputMethodManager
    imm?.hideSoftInputFromWindow(requireView().windowToken, 0)
}
