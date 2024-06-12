package com.example.rentstyle.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.widget.AppCompatButton
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.rentstyle.databinding.FragmentLoginBinding
import com.example.rentstyle.ui.VerificationActivity
import com.example.rentstyle.ui.customview.CustomEditText

class LoginFragment : Fragment() {
    private lateinit var _binding: FragmentLoginBinding
    private val binding get() = _binding

    private lateinit var userRegisterEmail: CustomEditText
    private lateinit var userRegisterPass: CustomEditText
    private lateinit var buttonRegister: TextView
    private lateinit var buttonLogin: AppCompatButton
    private lateinit var buttonGoogleSignIn: AppCompatButton

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLoginBinding.inflate(inflater, container, false)

        binding.apply {
            userRegisterEmail = edLoginEmail
            userRegisterPass = edLoginPassword
            buttonRegister = btnRegister
            buttonLogin = btnLogin
            buttonGoogleSignIn = btnLoginGoogle
        }

        navigateToRegisterFragment()
        loginUsingEmail()
        loginUsingGoogle()

        return binding.root
    }

    private fun loginUsingGoogle() {
        buttonGoogleSignIn.setOnClickListener {
            (activity as VerificationActivity).signInToGoogle()
        }
    }

    private fun loginUsingEmail() {
        val email = userRegisterEmail.text.toString()
        val pass = userRegisterPass.text.toString()

        if (email.isNotEmpty() && pass.isNotEmpty()) {
            (activity as VerificationActivity).signInWithEmail(email, pass)
        }
    }

    private fun navigateToRegisterFragment() {
        buttonRegister.setOnClickListener {
            findNavController().navigate(LoginFragmentDirections.actionNavigationLoginToNavigationRegister())
        }
    }
}