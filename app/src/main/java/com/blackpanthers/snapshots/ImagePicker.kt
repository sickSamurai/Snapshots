package com.blackpanthers.snapshots

import android.app.Activity
import android.content.Intent
import android.provider.MediaStore
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts.StartActivityForResult
import androidx.fragment.app.Fragment

class ImagePicker {

  private lateinit var galleryLauncher: ActivityResultLauncher<Intent>

  fun setupLauncher(fragment: Fragment, action: (intent: Intent?) -> Unit) {
    galleryLauncher = fragment.registerForActivityResult(StartActivityForResult()) {
      if (it.resultCode == Activity.RESULT_OK)
        action(it.data)
    }
  }

  fun openGallery() {
    val pickImageIntent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
    galleryLauncher.launch(pickImageIntent)
  }

}