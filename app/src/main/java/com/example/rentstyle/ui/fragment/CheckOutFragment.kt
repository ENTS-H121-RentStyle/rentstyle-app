package com.example.rentstyle.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.rentstyle.databinding.FragmentCheckOutBinding

class CheckOutFragment : Fragment() {
    private lateinit var _binding: FragmentCheckOutBinding
    private val binding get() = _binding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCheckOutBinding.inflate(inflater, container, false)

        binding.mainToolbar.apply {
            tvToolbarTitle.text = "Checkout"
            ivBackButton.setOnClickListener {
                findNavController().navigateUp()
            }
        }

        return binding.root
    }
}