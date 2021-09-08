package com.blackpanthers.snapshots

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.blackpanthers.snapshots.databinding.FragmentAddPhotoBinding
import com.google.android.gms.tasks.Task
import com.google.firebase.database.DatabaseReference
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.UploadTask.TaskSnapshot
import java.lang.Exception

class AddPhotoFragment : Fragment() {

  private lateinit var binding: FragmentAddPhotoBinding
  private lateinit var myDatabaseReference: DatabaseReference
  private lateinit var myStorageReference: StorageReference
  private var imagePicker = ImagePicker()
  private var selectedPhotoURI: Uri? = null

  override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
    binding = FragmentAddPhotoBinding.inflate(inflater, container, false)
    return binding.root
  }

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)
    setupFirebase()
    setupImagePicker()
    setupButtons()
  }

  fun setupFirebase(){
    myDatabaseReference = FirebaseUtilities.getDatabaseReference()
    myStorageReference = FirebaseStorage.getInstance().reference
  }

  fun setupImagePicker() {
    imagePicker.setupLauncher(this) {
      if (it != null) setPostMode()
      setPhoto(it)
    }
  }

  fun setupButtons() {
    with(binding) {
      btnSelect.setOnClickListener { imagePicker.openGallery() }
      btnPost.setOnClickListener {
        KeyboardHider.hideKeyboard(this@AddPhotoFragment)
        postSnapshot()
      }
    }
  }

  fun setSelectMode() {
    with(binding) {
      textMessage.text = getString(R.string.message_select_image)
      containerInputTitle.visibility = View.GONE
      btnPost.visibility = View.GONE
      progressBar.visibility = View.GONE
      imgPhoto.setImageURI(null)
    }
  }

  fun setPostMode() {
    with(binding) {
      textMessage.text = getString(R.string.message_add_title)
      containerInputTitle.visibility = View.VISIBLE
      btnPost.visibility = View.VISIBLE
      progressBar.visibility = View.GONE
    }
  }

  fun setPhoto(intent: Intent?) {
    selectedPhotoURI = intent?.data
    binding.imgPhoto.setImageURI(selectedPhotoURI)
  }

  fun saveSnapshot(snapshot: Snapshot) {
    myDatabaseReference.child(snapshot.id).setValue(snapshot)
    Toast.makeText(context, R.string.message_successful_upload, Toast.LENGTH_SHORT).show()
  }

  fun onProgressUpload(uploadTask: TaskSnapshot) {
    val progress = (100 * uploadTask.bytesTransferred / uploadTask.totalByteCount).toDouble()
    binding.progressBar.visibility = View.VISIBLE
    binding.progressBar.progress = progress.toInt()
    binding.textMessage.text = progress.toString()
  }

  fun onFailedUpload(exception: Exception){
    Toast.makeText(context, exception.message, Toast.LENGTH_SHORT).show()
    setPostMode()
  }

  fun onSuccessUpload(task: Task<Uri?>, snapshot: Snapshot) {
    task.addOnSuccessListener {
      saveSnapshot(snapshot.apply { photoURL = it.toString() })
      setSelectMode()
    }
  }

  fun postSnapshot() {
    if (selectedPhotoURI != null) {
      val key = myDatabaseReference.push().key!!
      myStorageReference.child(FirebaseUtilities.ROOT_PATH)
        .child(FirebaseUtilities.getCurrentUserUID())
        .child(key)
        .putFile(selectedPhotoURI!!)
        .addOnProgressListener { onProgressUpload(it) }
        .addOnFailureListener { onFailedUpload(it) }
        .addOnSuccessListener {
          onSuccessUpload(it.storage.downloadUrl, Snapshot(key, binding.inputTitle.text.toString()))
        }
    }
  }
}