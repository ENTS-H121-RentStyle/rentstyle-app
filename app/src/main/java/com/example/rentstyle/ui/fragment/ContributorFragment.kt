package com.example.rentstyle.ui.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.example.rentstyle.R
import com.example.rentstyle.databinding.FragmentContributorBinding

class ContributorFragment : Fragment() {
    private lateinit var _binding: FragmentContributorBinding
    private val binding get() = _binding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentContributorBinding.inflate(inflater, container, false)

        binding.mainToolbar.tvToolbarTitle.text = getString(R.string.txt_rentstyle_dev)
        binding.mainToolbar.ivBackButton.setOnClickListener {
            findNavController().navigateUp()
        }

        return binding.root
    }
}