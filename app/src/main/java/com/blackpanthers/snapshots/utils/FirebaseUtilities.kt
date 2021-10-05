package com.blackpanthers.snapshots.utils

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class FirebaseUtilities {
  companion object {

    const val ROOT_PATH: String = "snapshots"

    fun getDatabaseReference(): DatabaseReference = FirebaseDatabase.getInstance().reference.child(ROOT_PATH)

    fun getCurrentUser(): FirebaseUser? = FirebaseAuth.getInstance().currentUser

    fun getCurrentUserUID(): String = FirebaseAuth.getInstance().currentUser?.uid!!

  }
}