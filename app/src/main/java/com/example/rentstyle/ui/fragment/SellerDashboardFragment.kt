package com.example.rentstyle.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.rentstyle.databinding.FragmentSellerDashboardBinding

class SellerDashboardFragment : Fragment() {
    private lateinit var _binding: FragmentSellerDashboardBinding
    private val binding get() = _binding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSellerDashboardBinding.inflate(inflater, container, false)

        binding.tvHeading1.text = "Welcome user!"

        binding.btnSellerSelling.setOnClickListener {
            Toast.makeText(requireContext(), "Feature is not available yet", Toast.LENGTH_SHORT).show()
        }

        binding.btnSellerWallet.setOnClickListener {
            Toast.makeText(requireContext(), "Feature is not available yet", Toast.LENGTH_SHORT).show()
        }

        binding.btnAddProduct.setOnClickListener {
            findNavController().navigate(SellerDashboardFragmentDirections.actionNavigationSellerDashboardToNavigationAddProduct(null))
        }

        return binding.root
    }
}