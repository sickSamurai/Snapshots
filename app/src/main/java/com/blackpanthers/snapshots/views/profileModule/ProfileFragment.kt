package com.blackpanthers.snapshots.views.profileModule

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.blackpanthers.snapshots.utils.FirebaseUtilities
import com.blackpanthers.snapshots.R
import com.blackpanthers.snapshots.databinding.FragmentProfileBinding
import com.firebase.ui.auth.AuthUI

class ProfileFragment : Fragment() {
  private lateinit var binding: FragmentProfileBinding

  override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
    binding = FragmentProfileBinding.inflate(inflater, container, false)
    return binding.root
  }

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)
    binding.btnCloseSession.setOnClickListener { signOut() }
  }

  override fun onResume() {
    super.onResume()
    FirebaseUtilities.getCurrentUser()?.let {
      binding.textName.text = it.displayName
      binding.textEmail.text = it.email
    }
  }

  private fun signOut() {
    context?.let {
      AuthUI.getInstance().signOut(it).addOnSuccessListener {
        Toast.makeText(context, R.string.message_sign_out, Toast.LENGTH_SHORT).show()
      }
    }
  }

}