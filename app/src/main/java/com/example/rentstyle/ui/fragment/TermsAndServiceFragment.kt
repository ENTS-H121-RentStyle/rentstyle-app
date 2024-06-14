package com.example.rentstyle.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.rentstyle.databinding.FragmentTermsAndServiceBinding

class TermsAndServiceFragment : Fragment() {
    private lateinit var _binding: FragmentTermsAndServiceBinding
    private val binding get() = _binding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentTermsAndServiceBinding.inflate(inflater, container, false)

        binding.buttonAgree.setOnClickListener {
            findNavController().navigate(TermsAndServiceFragmentDirections.actionNavigationAgreementToNavigationInterestedCategory())
        }

        return binding.root
    }
}