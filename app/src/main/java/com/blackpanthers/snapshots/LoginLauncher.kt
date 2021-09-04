package com.blackpanthers.snapshots

import android.app.Activity
import android.content.Intent
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts.StartActivityForResult
import androidx.appcompat.app.AppCompatActivity
import com.firebase.ui.auth.AuthUI
import com.firebase.ui.auth.IdpResponse
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth

class LoginLauncher(var activity: MainActivity) {
  private var auth: FirebaseAuth? = null
  private lateinit var authListener: FirebaseAuth.AuthStateListener
  private lateinit var loginLauncher: ActivityResultLauncher<Intent>

  fun setupAuth() {

    auth = FirebaseAuth.getInstance()

    authListener = FirebaseAuth.AuthStateListener {
      val user = it.currentUser
      if (user == null) {
        signIn()
      }
    }

    loginLauncher = activity.registerForActivityResult(StartActivityForResult()) {
      if (it.resultCode == Activity.RESULT_OK) {
        Snackbar.make(activity.binding.root, "Bienvenido...", Snackbar.LENGTH_SHORT).show()
      } else {
        if (IdpResponse.fromResultIntent(it.data) == null) {
          activity.finish()
        }
      }
    }

  }

  fun addAuthStateListener() {
    auth?.addAuthStateListener(authListener)
  }

  fun removeAuthStateListener() {
    auth?.removeAuthStateListener(authListener)
  }

  fun signIn() {
    val providers = listOf(AuthUI.IdpConfig.EmailBuilder().build(), AuthUI.IdpConfig.GoogleBuilder().build())
    val signInIntent = AuthUI.getInstance().createSignInIntentBuilder().setAvailableProviders(providers).build()
    loginLauncher.launch(signInIntent)
  }

}