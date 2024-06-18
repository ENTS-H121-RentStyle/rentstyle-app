package com.example.rentstyle.helpers

import android.content.Context
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.lifecycleScope
import com.example.rentstyle.model.local.datastore.LoginSession
import com.example.rentstyle.model.local.datastore.dataStore
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.launch

object FirebaseToken {
    fun updateTokenId(context: Context, viewLifecycleOwner: LifecycleOwner) {
        val auth: FirebaseAuth = Firebase.auth

        val pref = LoginSession.getInstance(context.dataStore)

        auth.currentUser?.getIdToken(true)?.addOnCompleteListener { result ->
            if (result.isSuccessful) {
                if (result.result.token != null) {
                    viewLifecycleOwner.lifecycleScope.launch {
                        pref.setUserIdAndToken(auth.uid!!, result.result.token!!)
                    }
                }
            }
        }
    }
}