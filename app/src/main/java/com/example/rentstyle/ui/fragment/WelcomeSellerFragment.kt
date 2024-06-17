package com.example.rentstyle.ui.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.viewpager2.widget.ViewPager2
import com.example.rentstyle.R
import com.example.rentstyle.databinding.FragmentWelcomeSellerBinding

class WelcomeSellerFragment : Fragment() {
    private lateinit var _binding: FragmentWelcomeSellerBinding
    private val binding get() = _binding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentWelcomeSellerBinding.inflate(inflater, container, false)

        val viewPager = activity?.findViewById<ViewPager2>(R.id.vp_welcome_screen_seller)

        binding.buttonAgree.setOnClickListener {
            viewPager?.currentItem = 1
        }

        return binding.root
    }
}