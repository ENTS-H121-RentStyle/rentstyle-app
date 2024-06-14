package com.example.rentstyle.ui.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.rentstyle.R
import com.example.rentstyle.databinding.FragmentSettingBinding
import com.example.rentstyle.ui.MainActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class SettingFragment : Fragment() {
    private lateinit var _binding: FragmentSettingBinding
    private val binding get() = _binding

    private lateinit var auth: FirebaseAuth

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSettingBinding.inflate(inflater, container, false)

        auth = Firebase.auth

        binding.btnLogout.setOnClickListener {
            auth.signOut()
            (activity as MainActivity).navigateToVerificationActivity()
        }

        return binding.root
    }
}