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
import com.google.android.material.textfield.TextInputLayout

class LoginFragment : Fragment() {
    private lateinit var _binding: FragmentLoginBinding
    private val binding get() = _binding

    private lateinit var userLoginEmail: CustomEditText
    private lateinit var userLoginPass: CustomEditText
    private lateinit var userLoginEmailLayout: TextInputLayout
    private lateinit var userLoginPassLayout: TextInputLayout
    private lateinit var buttonRegister: TextView
    private lateinit var buttonLogin: AppCompatButton
    private lateinit var buttonGoogleSignIn: AppCompatButton

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLoginBinding.inflate(inflater, container, false)

        binding.apply {
            userLoginEmail = edLoginEmail
            userLoginPass = edLoginPassword
            userLoginEmailLayout = inputEmail
            userLoginPassLayout = inputPassword
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
        buttonLogin.setOnClickListener {
            val email = userLoginEmail.text.toString()
            val pass = userLoginPass.text.toString()

            if (userLoginEmailLayout.error == null
                && userLoginPassLayout.error == null) {
                if (email.isNotEmpty() && pass.isNotEmpty()) {
                    (activity as VerificationActivity).signInWithEmail(email, pass)
                }
            }
        }
    }

    private fun navigateToRegisterFragment() {
        buttonRegister.setOnClickListener {
            findNavController().navigate(LoginFragmentDirections.actionNavigationLoginToNavigationRegister())
        }
    }
}