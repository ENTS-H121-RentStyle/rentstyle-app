package com.example.rentstyle.ui

import android.os.Bundle
import android.view.View
import android.view.animation.AnimationUtils
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.example.rentstyle.R
import com.example.rentstyle.databinding.ActivityMainBinding
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    private lateinit var navView: BottomNavigationView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar!!.hide()

        setBottomNavigation()

//        val intent = Intent(this, VerificationActivity::class.java)
//        startActivity(intent)
    }

    private fun setBottomNavigation() {
        navView = binding.mainBottomNavigationView
        val navHostFragment = supportFragmentManager.findFragmentById(binding.navHostFragmentActivityMain.id) as NavHostFragment
        val navController = navHostFragment.navController

        navController.addOnDestinationChangedListener{ _, destination, _ ->
            navView.isVisible = destination.id in arrayListOf(R.id.navigation_home,
                R.id.navigation_explore, R.id.navigation_notification,
                R.id.navigation_profile)
        }

        val appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.navigation_home, R.id.navigation_explore, R.id.navigation_notification, R.id.navigation_profile
            )
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)

        var prevMenuItemClicked: View = navView.findViewById(R.id.navigation_home)

        navView.setOnItemSelectedListener { item ->
            val slideUp = AnimationUtils.loadAnimation(this, R.anim.slide_up)
            if (prevMenuItemClicked != navView.findViewById(item.itemId)) {
                navController.navigate(item.itemId)
                prevMenuItemClicked.clearAnimation()
            }

            when (item.itemId) {
                R.id.navigation_home -> {
                    navView.findViewById<View>(R.id.navigation_home).run {
                        startAnimation(slideUp)
                    }

                    prevMenuItemClicked = navView.findViewById(R.id.navigation_home)
                }
                R.id.navigation_explore -> {
                    navView.findViewById<View>(R.id.navigation_explore).run {
                        startAnimation(slideUp)
                    }

                    prevMenuItemClicked = navView.findViewById(R.id.navigation_explore)
                }
                R.id.navigation_notification -> {
                    navView.findViewById<View>(R.id.navigation_notification).run {
                        startAnimation(slideUp)
                    }

                    prevMenuItemClicked = navView.findViewById(R.id.navigation_notification)
                }
                R.id.navigation_profile -> {
                    navView.findViewById<View>(R.id.navigation_profile).run {
                        startAnimation(slideUp)
                    }

                    prevMenuItemClicked = navView.findViewById(R.id.navigation_profile)
                }
            }
            true
        }
    }
}