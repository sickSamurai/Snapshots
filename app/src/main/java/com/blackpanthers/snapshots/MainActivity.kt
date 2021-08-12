package com.blackpanthers.snapshots

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import com.blackpanthers.snapshots.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
  private lateinit var binding: ActivityMainBinding
  private var fragmentManager: FragmentManager = supportFragmentManager
  private lateinit var activeFragment: Fragment

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    binding = ActivityMainBinding.inflate(layoutInflater)
    setContentView(binding.root)
    setupBottomNavBar()
  }

  fun addFragments(vararg fragments: Fragment) {
    for (fragment in fragments)
      fragmentManager.beginTransaction()
        .add(R.id.host_fragment, fragment, fragment::javaClass.name)
        .hide(fragment)
        .commit()
  }

  fun showFragment(fragmentToShow: Fragment){
    fragmentManager.beginTransaction().show(fragmentToShow).commit()
    activeFragment = fragmentToShow
  }

  fun hideFragment(fragmentToHide: Fragment){
    fragmentManager.beginTransaction().hide(fragmentToHide).commit()
  }

  fun changeToFragment(fragmentToShow: Fragment): Boolean {
    return if (fragmentToShow.isHidden) {
      fragmentManager.beginTransaction().hide(activeFragment).show(fragmentToShow).commit()
      activeFragment = fragmentToShow
      true
    } else false
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