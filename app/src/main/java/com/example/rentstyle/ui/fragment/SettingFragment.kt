package com.example.rentstyle.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.SwitchCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.rentstyle.R
import com.example.rentstyle.databinding.FragmentSettingBinding
import com.example.rentstyle.model.local.datastore.LoginSession
import com.example.rentstyle.model.local.datastore.SettingPref
import com.example.rentstyle.model.local.datastore.dataStore
import com.example.rentstyle.model.local.datastore.settingDataStore
import com.example.rentstyle.ui.MainActivity
import com.example.rentstyle.viewmodel.SettingViewModel
import com.example.rentstyle.viewmodel.SettingViewModelFactory
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.launch

class SettingFragment : Fragment() {
    private lateinit var _binding: FragmentSettingBinding
    private val binding get() = _binding

    private lateinit var viewModel: SettingViewModel

    private lateinit var auth: FirebaseAuth
    private lateinit var pref: LoginSession
    private lateinit var settingPref: SettingPref

    private lateinit var switchSetting: SwitchCompat

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSettingBinding.inflate(inflater, container, false)

        binding.mainToolbar.tvToolbarTitle.text = getString(R.string.txt_settings)
        binding.mainToolbar.ivBackButton.setOnClickListener {
            findNavController().navigateUp()
        }

        switchSetting = binding.switchAppTheme

        auth = Firebase.auth
        pref = LoginSession.getInstance(requireActivity().application.dataStore)
        settingPref = SettingPref.getInstance(requireActivity().application.settingDataStore)

        viewModel = ViewModelProvider(requireActivity(), SettingViewModelFactory.getInstance(settingPref))[SettingViewModel::class.java]

        setThemeSwitch()

        binding.btnTermsOfService.setOnClickListener {
            findNavController().navigate(SettingFragmentDirections.actionNavigationSettingToNavigationAgreement())
        }

        binding.btnChangeProfile.setOnClickListener {
            findNavController().navigate(SettingFragmentDirections.actionNavigationSettingToNavigationEditUserProfile())
        }

        binding.btnCredit.setOnClickListener {
            findNavController().navigate(SettingFragmentDirections.actionNavigationSettingToNavigationContributor())
        }

        binding.btnLogout.setOnClickListener {
            viewLifecycleOwner.lifecycleScope.launch {
                pref.clearSession()
            }

            auth.signOut()
            (activity as MainActivity).navigateToVerificationActivity()
        }

        return binding.root
    }

    private fun setThemeSwitch() {
        viewModel.getThemeSettings().observe(requireActivity()) {
            switchSetting.isChecked = it
        }

        switchSetting.setOnCheckedChangeListener { _, isChecked ->
            viewModel.saveThemeSetting(isChecked)
        }
    }
}