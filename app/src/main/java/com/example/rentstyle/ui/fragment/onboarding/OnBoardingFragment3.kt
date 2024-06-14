package com.example.rentstyle.ui.fragment.onboarding

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import androidx.core.view.isVisible
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.rentstyle.R
import com.example.rentstyle.databinding.FragmentOnBoarding3Binding
import com.example.rentstyle.model.local.datastore.LoginSession
import com.example.rentstyle.model.local.datastore.dataStore
import com.example.rentstyle.ui.fragment.OnBoardingFragmentDirections
import kotlinx.coroutines.launch

class OnBoardingFragment3 : Fragment() {
    private lateinit var _binding: FragmentOnBoarding3Binding
    private val binding get() = _binding

    private lateinit var pref: LoginSession

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentOnBoarding3Binding.inflate(inflater, container, false)

        pref = LoginSession.getInstance(requireActivity().application.dataStore)

        binding.next.setOnClickListener {
            viewLifecycleOwner.lifecycleScope.launch {
                pref.setFirstLoginSession()
                findNavController().navigate(OnBoardingFragmentDirections.actionNavigationOnBoardingToNavigationLogin())
            }
        }

        val animation = AnimationUtils.loadAnimation(requireContext(), R.anim.pop_up)
        binding.apply {
            ivOnBoarding.isVisible = true
            ivOnBoarding.startAnimation(animation)
        }

        return binding.root
    }
}