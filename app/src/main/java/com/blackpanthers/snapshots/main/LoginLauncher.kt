package com.blackpanthers.snapshots.main

import android.app.Activity
import android.content.Intent
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts.StartActivityForResult
import androidx.appcompat.app.AppCompatActivity
import com.blackpanthers.snapshots.utils.FirebaseUtilities
import com.blackpanthers.snapshots.R
import com.firebase.ui.auth.AuthUI
import com.firebase.ui.auth.IdpResponse
import com.google.firebase.auth.FirebaseAuth

class LoginLauncher() {
  private var auth: FirebaseAuth? = null
  private  var authListener: FirebaseAuth.AuthStateListener
  private lateinit var loginLauncher: ActivityResultLauncher<Intent>

  init {
    auth = FirebaseAuth.getInstance()
    authListener = FirebaseAuth.AuthStateListener { if (it.currentUser == null) signIn() }
  }

  fun setupLauncher(activity: AppCompatActivity) {
    loginLauncher = activity.registerForActivityResult(StartActivityForResult()) {
      if (it.resultCode == Activity.RESULT_OK) {
        val userName = FirebaseUtilities.getCurrentUser()?.displayName!!
        val signInMessage = activity.getString(R.string.message_sign_in) + " " + userName
        Toast.makeText(activity.applicationContext, signInMessage, Toast.LENGTH_SHORT).show()
      } else {
        if (IdpResponse.fromResultIntent(it.data) == null) {
          activity.finish()
        }
      }
    }
  }

  fun signIn() {
    val providers = listOf(AuthUI.IdpConfig.EmailBuilder().build(), AuthUI.IdpConfig.GoogleBuilder().build())
    val signInIntent = AuthUI.getInstance()
      .createSignInIntentBuilder()
      .setIsSmartLockEnabled(false)
      .setAvailableProviders(providers)
      .build()
    loginLauncher.launch(signInIntent)
  }

  fun addAuthStateListener() {
    auth?.addAuthStateListener(authListener)
  }

  fun removeAuthStateListener() {
    auth?.removeAuthStateListener(authListener)
  }
}