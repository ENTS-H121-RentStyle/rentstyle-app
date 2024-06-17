package com.example.rentstyle.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.rentstyle.R
import com.example.rentstyle.databinding.FragmentProfileBinding
import com.example.rentstyle.helpers.DataResult
import com.example.rentstyle.helpers.FilterModel
import com.example.rentstyle.helpers.adapter.RecyclerDummyOrderAdapter
import com.example.rentstyle.helpers.adapter.RecyclerFilterAdapter
import com.example.rentstyle.model.local.datastore.LoginSession
import com.example.rentstyle.model.local.datastore.dataStore
import com.example.rentstyle.viewmodel.ProfileViewModel
import com.example.rentstyle.viewmodel.SellerViewModelFactory
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class ProfileFragment : Fragment() {
    private lateinit var _binding: FragmentProfileBinding
    private val binding get() = _binding

    private lateinit var viewModel: ProfileViewModel

    private lateinit var filterAdapter : RecyclerFilterAdapter
    private lateinit var orderListAdapter : RecyclerDummyOrderAdapter

    private lateinit var pref: LoginSession
    private var run = true

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProfileBinding.inflate(inflater, container, false)

        binding.tvUsername.text = "username"

        val factory = SellerViewModelFactory.getInstance(this.requireActivity().application)
        viewModel = ViewModelProvider(this, factory)[ProfileViewModel::class.java]

        pref = LoginSession.getInstance(requireActivity().application.dataStore)

        val filterData = FilterModel.getOrderFilter()
        filterAdapter = RecyclerFilterAdapter(filterData)
        binding.rvFilterRentalHistory.adapter = filterAdapter

        orderListAdapter = RecyclerDummyOrderAdapter()
        binding.rvRentalHistory.adapter = orderListAdapter

        orderListAdapter.setOnClickListener(object : RecyclerDummyOrderAdapter.OnClickListener {
            override fun onClick(position: Int) {
                findNavController().navigate(ProfileFragmentDirections.actionNavigationProfileToNavigationOrderDetail())
            }

        })

        binding.btnShoppingCart.setOnClickListener {
            findNavController().navigate(ProfileFragmentDirections.actionNavigationProfileToNavigationShoppingCart())
        }

        binding.btnUserSetting.setOnClickListener {
            findNavController().navigate(ProfileFragmentDirections.actionNavigationProfileToNavigationSetting())
        }

        binding.btnUserShop.setOnClickListener {
            viewLifecycleOwner.lifecycleScope.launch {
                if (pref.getSellerId().first() != null && pref.getSellerId().first() != "null") {
                    findNavController().navigate(ProfileFragmentDirections.actionNavigationProfileToNavigationSellerDashboard())
                } else if (pref.getSellerId().first() == "null"){
                    findNavController().navigate(ProfileFragmentDirections.actionNavigationProfileToNavigationOnboardingSeller())
                } else {
                    checkUser()
                }
            }
        }

        return binding.root
    }

    private fun checkUser() {
        viewLifecycleOwner.lifecycleScope.launch {
            val userId = pref.getUserId().first()

            viewModel.getSellerData(userId!!).observe(viewLifecycleOwner) {result ->
                if (result != null) {
                    when (result) {
                        is DataResult.Loading -> {
                        }

                        is DataResult.Success -> {
                            lifecycleScope.launch {
                                pref.setSellerId(result.data.id!!)
                            }

                            if (run) {
                                run = false
                                findNavController().navigate(ProfileFragmentDirections.actionNavigationProfileToNavigationSellerDashboard())
                            }
                        }

                        is DataResult.Error -> {
                            if (result.error == "null") {
                                lifecycleScope.launch {
                                    pref.setSellerId("null")
                                }

                                if (run) {
                                    run = false
                                    findNavController().navigate(ProfileFragmentDirections.actionNavigationProfileToNavigationOnboardingSeller())
                                }
                            } else {
                                Toast.makeText(requireContext(), getString(R.string.error_toast, result.error), Toast.LENGTH_SHORT).show()
                            }
                        }
                    }
                }
            }
        }
    }
}