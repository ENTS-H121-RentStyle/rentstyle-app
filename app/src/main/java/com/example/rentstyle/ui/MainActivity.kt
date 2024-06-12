package com.example.rentstyle.ui

import android.content.Intent
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
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    private lateinit var navView: BottomNavigationView

    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar!!.hide()

        auth = Firebase.auth
        val firebaseUser = auth.currentUser

        if (firebaseUser == null) {
            val intent = Intent(this, VerificationActivity::class.java)
            startActivity(intent)
        }

        setBottomNavigation()
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