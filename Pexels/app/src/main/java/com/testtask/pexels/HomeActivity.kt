package com.testtask.pexels

import com.testtask.pexels.HomeFragment
import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import com.google.android.material.bottomnavigation.BottomNavigationView

class HomeActivity : AppCompatActivity() {
    private lateinit var contentView: LinearLayout
    private lateinit var drawerLayout: DrawerLayout
    private lateinit var searchEt: EditText
    private lateinit var searchIv: ImageView
    private lateinit var progressBar: ProgressBar

    private lateinit var activeFragment: Fragment
    private lateinit var homeFragment: HomeFragment
    private lateinit var bookmarksFragment: BookmarksFragment

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        try {
            homeFragment = HomeFragment()
            bookmarksFragment = BookmarksFragment()

            if (savedInstanceState == null) {
                supportFragmentManager.beginTransaction()
                    .add(R.id.fragment_container, homeFragment, "HOME_FRAGMENT")
                    .commit()
                activeFragment = homeFragment
            }

            val bottomNavigationView: BottomNavigationView = findViewById(R.id.bottom_navigation)
            bottomNavigationView.setOnItemSelectedListener { item ->
                when (item.itemId) {
                    R.id.home -> {
                        if (activeFragment != homeFragment) {
                            supportFragmentManager.beginTransaction()
                                .hide(activeFragment)
                                .show(homeFragment)
                                .commit()
                            activeFragment = homeFragment
                        }
                        true
                    }
                    R.id.favorites -> {
                        if (activeFragment != bookmarksFragment) {
                            if (!bookmarksFragment.isAdded) {
                                supportFragmentManager.beginTransaction()
                                    .hide(activeFragment)
                                    .add(R.id.fragment_container, bookmarksFragment, "BOOKMARKS_FRAGMENT")
                                    .commit()
                            } else {
                                supportFragmentManager.beginTransaction()
                                    .hide(activeFragment)
                                    .show(bookmarksFragment)
                                    .commit()
                            }
                            activeFragment = bookmarksFragment
                        }
                        true
                    }
                    else -> false
                }
            }

            contentView = findViewById(R.id.content_view)
            drawerLayout = findViewById(R.id.drawer_layout)
            searchEt = findViewById(R.id.searchEv)
            searchIv = findViewById(R.id.search_image)

            searchIv.setOnClickListener {
                Toast.makeText(this@HomeActivity, "Search Button Clicked", Toast.LENGTH_SHORT).show()
            }
        } catch (e: Exception) {
            Log.e("HomeActivity", "Error in onCreate", e)
            Toast.makeText(this, "An error occurred: ${e.message}", Toast.LENGTH_LONG).show()
        }
    }

    override fun onBackPressed() {
        if (drawerLayout.isDrawerVisible(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }
}
