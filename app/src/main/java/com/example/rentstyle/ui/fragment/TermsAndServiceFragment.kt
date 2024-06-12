package com.example.rentstyle.ui.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.rentstyle.R
import com.example.rentstyle.databinding.FragmentTermsAndServiceBinding
import com.example.rentstyle.ui.VerificationActivity

class TermsAndServiceFragment : Fragment() {
    private lateinit var _binding: FragmentTermsAndServiceBinding
    private val binding get() = _binding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentTermsAndServiceBinding.inflate(inflater, container, false)

        binding.buttonAgree.setOnClickListener {
            (activity as VerificationActivity).navigateToMainActivity()
        }

        return binding.root
    }
}