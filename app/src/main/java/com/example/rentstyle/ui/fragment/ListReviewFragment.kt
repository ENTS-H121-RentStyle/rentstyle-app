package com.example.rentstyle.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.rentstyle.databinding.FragmentListReviewBinding

class ListReviewFragment : Fragment() {
    private lateinit var _binding: FragmentListReviewBinding
    private val binding get() = _binding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentListReviewBinding.inflate(inflater, container, false)

        return binding.root
    }
}