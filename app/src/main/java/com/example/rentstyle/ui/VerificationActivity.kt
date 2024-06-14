package com.example.rentstyle.ui

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.credentials.CredentialManager
import androidx.credentials.CustomCredential
import androidx.credentials.GetCredentialRequest
import androidx.credentials.GetCredentialResponse
import androidx.credentials.exceptions.GetCredentialException
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.NavHostFragment
import com.example.rentstyle.BuildConfig
import com.example.rentstyle.R
import com.example.rentstyle.databinding.ActivityVerificationBinding
import com.example.rentstyle.model.local.datastore.LoginSession
import com.example.rentstyle.model.local.datastore.dataStore
import com.example.rentstyle.ui.fragment.LoginFragmentDirections
import com.example.rentstyle.ui.fragment.RegisterFragmentDirections
import com.github.ybq.android.spinkit.style.WanderingCubes
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class VerificationActivity : AppCompatActivity() {
    private lateinit var binding: ActivityVerificationBinding

    private lateinit var pref: LoginSession
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityVerificationBinding.inflate(layoutInflater)
        setContentView(binding.root)

        pref = LoginSession.getInstance(application.dataStore)
        auth = Firebase.auth

        checkLoginSession()

        supportActionBar!!.hide()
    }

    private fun checkLoginSession() {
        lifecycleScope.launch {
            val navHostFragment = supportFragmentManager.findFragmentById(binding.navHostFragmentActivityVerification.id) as NavHostFragment
            val navController = navHostFragment.navController

            if (!pref.getFirstLoginSession().first()) {
                navController.navigate(LoginFragmentDirections.actionNavigationLoginToNavigationOnBoarding())
            } else if (!pref.getPrefCheck().first() && auth.currentUser != null) {
                navController.navigate(LoginFragmentDirections.actionNavigationLoginToNavigationInterestedCategory())
            }
        }
    }

    fun navigateToMainActivity() {
        val intent = Intent(this, MainActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
        startActivity(intent)
    }

    fun signInWithEmail(email: String, pass: String) {
        binding.ivLoadingSpinner.apply {
            isVisible = true
            setIndeterminateDrawable(WanderingCubes())
        }

        auth.signInWithEmailAndPassword(email, pass)
            .addOnCompleteListener {
                binding.ivLoadingSpinner.isVisible = false
                if (it.isSuccessful) {
                    updateUI(auth.currentUser, false)
                } else {
                    Toast.makeText(this, "Email or password mismatched", Toast.LENGTH_SHORT).show()
                }
            }
    }

    fun signUpWithEmail(email: String, pass: String) {
        binding.ivLoadingSpinner.apply {
            isVisible = true
            setIndeterminateDrawable(WanderingCubes())
        }

        auth.createUserWithEmailAndPassword(email, pass)
            .addOnCompleteListener {
                binding.ivLoadingSpinner.isVisible = false
                if (it.isSuccessful) {
                    updateUI(auth.currentUser, true)
                } else {
                    Log.d("Error", "Error registering account")
                }
            }
    }

    fun signInToGoogle() {
        val credentialManager = CredentialManager.create(this)

        val googleIdOption = GetGoogleIdOption.Builder()
            .setFilterByAuthorizedAccounts(false)
            .setServerClientId(BuildConfig.WEB_CLIENT_ID)
            .build()

        val request = GetCredentialRequest.Builder()
            .addCredentialOption(googleIdOption)
            .build()

        lifecycleScope.launch {
            try {
                val result: GetCredentialResponse = credentialManager.getCredential(
                    request = request,
                    context = this@VerificationActivity,
                )
                handleSignInByGoogle(result)
            } catch (e: GetCredentialException) {
                Log.d("Error", e.message.toString())
            }
        }
    }

    private fun handleSignInByGoogle(result: GetCredentialResponse) {
        when (val credential = result.credential) {
            is CustomCredential -> {
                if (credential.type == GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL) {
                    val googleIdTokenCredential = GoogleIdTokenCredential.createFrom(credential.data)
                    firebaseAuthWithGoogle(googleIdTokenCredential.idToken)
                } else {
                    Log.e(TAG, "Unexpected type of credential")
                }
            }

            else -> {
                Log.e(TAG, "Unexpected type of credential")
            }
        }
    }

    private fun firebaseAuthWithGoogle(idToken: String) {
        val credential: AuthCredential = GoogleAuthProvider.getCredential(idToken, null)
        auth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    val isNewUser = task.result?.additionalUserInfo?.isNewUser ?: true

                    val user: FirebaseUser? = auth.currentUser
                    updateUI(user, isNewUser)
                } else {
                    Log.w(TAG, "signInWithCredential:failure", task.exception)
                    updateUI(null, false)
                }
            }
    }

    private fun updateUI(currentUser: FirebaseUser?, isNewUser: Boolean) {
        if (currentUser != null && isNewUser) {
            val navHostFragment = supportFragmentManager.findFragmentById(binding.navHostFragmentActivityVerification.id) as NavHostFragment
            val navController = navHostFragment.navController

            navController.navigate(RegisterFragmentDirections.actionNavigationRegisterToNavigationAgreement())
        } else {
            navigateToMainActivity()
        }
    }

    companion object {
        private const val TAG = "LoginActivity"
    }
}