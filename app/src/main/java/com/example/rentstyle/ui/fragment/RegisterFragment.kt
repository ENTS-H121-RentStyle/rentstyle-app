package com.example.rentstyle.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.widget.AppCompatButton
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.rentstyle.databinding.FragmentRegisterBinding
import com.example.rentstyle.ui.VerificationActivity
import com.example.rentstyle.ui.customview.CustomEditText

class RegisterFragment : Fragment() {
    private lateinit var _binding: FragmentRegisterBinding
    private val binding get() = _binding

    private lateinit var userRegisterEmail: CustomEditText
    private lateinit var userRegisterPass: CustomEditText
    private lateinit var userRegisterPassConfirm: CustomEditText
    private lateinit var buttonLogin: TextView
    private lateinit var buttonRegister: AppCompatButton
    private lateinit var buttonGoogleSignIn: AppCompatButton

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentRegisterBinding.inflate(inflater, container, false)

        binding.apply {
            userRegisterEmail = edRegisterEmail
            userRegisterPass = edRegisterPassword
            userRegisterPassConfirm = edRegisterPasswordConfirmation
            buttonRegister = btnRegister
            buttonLogin = btnLogin
            buttonGoogleSignIn = btnRegisterGoogle
        }

        navigateToLoginFragment()
        registerUsingGoogle()
        registerUsingEmail()

        return binding.root
    }

    private fun registerUsingEmail() {
        val email = userRegisterEmail.text.toString()
        val pass = userRegisterPass.text.toString()
        val passConfirm = userRegisterPassConfirm.text.toString()

        buttonRegister.setOnClickListener {
            if (email.isNotEmpty() && pass.isNotEmpty() && passConfirm.isNotEmpty()) {
                if (pass == passConfirm) {
                    (activity as VerificationActivity).signUpWithEmail(email, pass)
                }
            }
        }
    }

    private fun registerUsingGoogle() {
        buttonGoogleSignIn.setOnClickListener {
            (activity as VerificationActivity).signInToGoogle()
        }
    }

    private fun navigateToLoginFragment() {
        buttonLogin.setOnClickListener {
            findNavController().navigate(RegisterFragmentDirections.actionNavigationRegisterToNavigationLogin())
        }
    }
}