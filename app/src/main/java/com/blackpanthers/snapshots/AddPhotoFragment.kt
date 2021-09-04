package com.blackpanthers.snapshots

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.blackpanthers.snapshots.databinding.FragmentAddPhotoBinding
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.UploadTask.TaskSnapshot

class AddPhotoFragment : Fragment() {

  private val PATH_SNAPSHOTS = "snapshots"
  private lateinit var binding: FragmentAddPhotoBinding
  private lateinit var myDatabaseReference: DatabaseReference
  private lateinit var myStorageReference: StorageReference
  private var imagePicker = ImagePicker(this)
  private var selectedPhotoURI: Uri? = null

  override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
    binding = FragmentAddPhotoBinding.inflate(inflater, container, false)
    myDatabaseReference = FirebaseDatabase.getInstance().reference.child(PATH_SNAPSHOTS)
    myStorageReference = FirebaseStorage.getInstance().reference
    return binding.root
  }

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)
    with(binding) {
      btnSelect.setOnClickListener { imagePicker.openGallery() }
      btnPost.setOnClickListener { postSnapshot() }
    }
  }

  fun setSelectMode() {
    with(binding) {
      textMessage.text = getString(R.string.message_select_image)
      containerInputTitle.visibility = View.GONE
      btnPost.visibility = View.GONE
      imgPhoto.setImageURI(null)
    }
  }

  fun setPostMode() {
    with(binding) {
      textMessage.text = getString(R.string.message_add_title)
      containerInputTitle.visibility = View.VISIBLE
      btnPost.visibility = View.VISIBLE
    }
  }

  fun setPhoto(data: Intent?) {
    selectedPhotoURI = data?.data
    binding.imgPhoto.setImageURI(selectedPhotoURI)
  }

  fun saveSnapshot(snapshot: Snapshot) {
    Utilities.hideKeyboard(this)
    myDatabaseReference.child(snapshot.id).setValue(snapshot)
    Snackbar.make(binding.root, R.string.message_successful_upload, Snackbar.LENGTH_SHORT).show()
  }

  fun updateProgressBar(uploadTask: TaskSnapshot) {
    val progress = (100 * uploadTask.bytesTransferred / uploadTask.totalByteCount).toDouble()
    binding.progressBar.progress = progress.toInt()
    binding.textMessage.text = "$progress%"
  }

  fun toggleProgressBar() {
    binding.progressBar.apply {
      visibility = if (visibility == View.GONE) View.VISIBLE else View.GONE
    }
  }

  fun postSnapshot() {
    val key = myDatabaseReference.push().key!!
    val storageReference = myStorageReference.child(PATH_SNAPSHOTS).child(key)
    if (selectedPhotoURI != null) {
      toggleProgressBar()
      storageReference.putFile(selectedPhotoURI!!)
        .addOnProgressListener { updateProgressBar(it) }
        .addOnFailureListener {
          Snackbar.make(binding.root, R.string.message_failed_upload, Snackbar.LENGTH_SHORT)
            .show()
        }
        .addOnSuccessListener {
          it.storage.downloadUrl.addOnSuccessListener { uri ->
            val title = binding.inputTitle.text.toString()
            val url = uri.toString()
            saveSnapshot(Snapshot(key, title, url))
          }
        }
        .addOnCompleteListener { toggleProgressBar(); setSelectMode() }
    }
  }
}