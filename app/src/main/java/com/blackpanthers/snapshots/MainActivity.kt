package com.blackpanthers.snapshots

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import com.blackpanthers.snapshots.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
  lateinit var binding: ActivityMainBinding
  private var fragmentManager = supportFragmentManager
  private var activeFragment: Fragment? = null
  private lateinit var loginLauncher: LoginLauncher

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    binding = ActivityMainBinding.inflate(layoutInflater)
    loginLauncher = LoginLauncher(this)
    setContentView(binding.root)
    setupBottomNavBar()
  }

  override fun onResume() {
    super.onResume()
    loginLauncher.addAuthStateListener()
  }

  override fun onPause() {
    super.onPause()
    loginLauncher.removeAuthStateListener()
  }

  fun addFragments(vararg fragments: Fragment) {
    for (fragment in fragments)
      fragmentManager.beginTransaction()
        .add(R.id.host_fragment, fragment, fragment::javaClass.name)
        .hide(fragment)
        .commit()
  }

  fun showFragment(fragmentToShow: Fragment) {
    fragmentManager.beginTransaction().show(fragmentToShow).commit()
    activeFragment = fragmentToShow
  }

  fun hideFragment(fragmentToHide: Fragment) {
    fragmentManager.beginTransaction().hide(fragmentToHide).commit()
    activeFragment = null
  }

  fun changeToFragment(fragmentToShow: Fragment): Boolean {
    if (fragmentToShow.isHidden) {
      fragmentManager.beginTransaction().hide(activeFragment!!).show(fragmentToShow).commit()
      activeFragment = fragmentToShow
      return true
    } else {
      return false
    }
  }

  fun setupBottomNavBar() {
    val homeFragment = HomeFragment()
    val addPhotoFragment = AddPhotoFragment()
    val profileFragment = ProfileFragment()
    addFragments(profileFragment, addPhotoFragment, homeFragment)
    showFragment(homeFragment)
    binding.bottomNavBar.setOnItemSelectedListener {
      when (it.itemId) {
        R.id.action_home -> changeToFragment(homeFragment)
        R.id.action_add_photo -> changeToFragment(addPhotoFragment)
        R.id.action_profile -> changeToFragment(profileFragment)
        else -> false
      }
    }
  }

}