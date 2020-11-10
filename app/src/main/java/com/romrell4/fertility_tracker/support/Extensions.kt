package com.romrell4.fertility_tracker.support

import android.app.Activity
import android.view.View
import android.view.inputmethod.InputMethodManager

fun View.showKeyboard() {
    (context.getSystemService(Activity.INPUT_METHOD_SERVICE) as? InputMethodManager)
        ?.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0)
}

fun View.hideKeyboard() {
    (context.getSystemService(Activity.INPUT_METHOD_SERVICE) as? InputMethodManager)
        ?.hideSoftInputFromWindow(windowToken, 0)
}