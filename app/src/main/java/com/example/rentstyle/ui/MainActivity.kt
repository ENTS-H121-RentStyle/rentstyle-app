package com.example.rentstyle.ui

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.animation.AnimationUtils
import androidx.appcompat.app.AppCompatActivity
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.core.view.isVisible
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.example.rentstyle.R
import com.example.rentstyle.databinding.ActivityMainBinding
import com.example.rentstyle.model.local.datastore.LoginSession
import com.example.rentstyle.model.local.datastore.dataStore
import com.github.ybq.android.spinkit.style.WanderingCubes
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    private lateinit var isLoading: MutableLiveData<Boolean>

    private lateinit var navView: BottomNavigationView

    private lateinit var pref: LoginSession
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        pref = LoginSession.getInstance(application.dataStore)
        auth = Firebase.auth

        supportActionBar!!.hide()

        isLoading = MutableLiveData(true)
        installSplashScreen().apply {
            setKeepOnScreenCondition{
                isLoading.value!!
            }
        }

        checkLoginSession()
    }

    fun startLoadingSpinner () {
        binding.ivLoadingSpinner.apply {
            isVisible = true
            setIndeterminateDrawable(WanderingCubes())
        }
    }

    fun stopLoadingSpinner () {
        binding.ivLoadingSpinner.isVisible = false
    }

    fun navigateToVerificationActivity () {
        val intent = Intent(this, VerificationActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
        startActivity(intent)
    }

    private fun checkLoginSession() {
        val firebaseUser = auth.currentUser

        lifecycleScope.launch {
            if (firebaseUser == null || !pref.getPrefCheck().first()) {
                navigateToVerificationActivity()
            }
            isLoading.value = false
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