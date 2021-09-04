package com.blackpanthers.snapshots

import android.app.Activity
import android.content.Intent
import android.provider.MediaStore
import androidx.activity.result.contract.ActivityResultContracts.StartActivityForResult

class ImagePicker(addPhotoFragment: AddPhotoFragment) {

  private val galleryLauncher = addPhotoFragment.registerForActivityResult(StartActivityForResult()) {
    if (it.resultCode == Activity.RESULT_OK)
      with(addPhotoFragment) {
        setPhoto(it.data)
        setPostMode()
      }
  }

  fun openGallery() {
    val pickImageIntent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
    galleryLauncher.launch(pickImageIntent)
  }

}