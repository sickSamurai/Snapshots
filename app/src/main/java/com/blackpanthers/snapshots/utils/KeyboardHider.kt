package com.blackpanthers.snapshots.utils

import android.app.Activity
import android.content.Context
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.fragment.app.Fragment

class KeyboardHider {
  companion object{
    fun Context.hideKeyboard(view: View) {
      val inputMethodManager = getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
      inputMethodManager.hideSoftInputFromWindow(view.windowToken, 0)
    }

    fun hideKeyboard(fragment: Fragment) {
      fragment.view?.let { fragment.activity?.hideKeyboard(it) }
    }

    fun hideKeyboard(activity: Activity) {
      activity.hideKeyboard(activity.currentFocus ?: View(activity))
    }
  }

}