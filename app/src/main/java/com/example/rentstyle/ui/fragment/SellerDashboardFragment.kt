package com.example.rentstyle.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.rentstyle.R
import com.example.rentstyle.databinding.FragmentSellerDashboardBinding
import com.example.rentstyle.helpers.DataResult
import com.example.rentstyle.helpers.FirebaseToken.updateTokenId
import com.example.rentstyle.model.local.datastore.LoginSession
import com.example.rentstyle.model.local.datastore.dataStore
import com.example.rentstyle.viewmodel.SellerViewModel
import com.example.rentstyle.viewmodel.SellerViewModelFactory
import com.github.ybq.android.spinkit.style.WanderingCubes
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class SellerDashboardFragment : Fragment() {
    private lateinit var _binding: FragmentSellerDashboardBinding
    private val binding get() = _binding

    private lateinit var viewModel: SellerViewModel

    private lateinit var pref: LoginSession

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSellerDashboardBinding.inflate(inflater, container, false)

        updateTokenId(requireContext(), viewLifecycleOwner)

        val factory = SellerViewModelFactory.getInstance(this.requireActivity().application)
        viewModel = ViewModelProvider(this, factory)[SellerViewModel::class.java]

        pref = LoginSession.getInstance(requireActivity().application.dataStore)

        getSellerData()

        binding.btnSellerSelling.setOnClickListener {
            Toast.makeText(requireContext(), getString(R.string.txt_feature_is_not_available), Toast.LENGTH_SHORT).show()
        }

        binding.btnSellerWallet.setOnClickListener {
            Toast.makeText(requireContext(), getString(R.string.txt_feature_is_not_available), Toast.LENGTH_SHORT).show()
        }

        return binding.root
    }

    private fun getSellerData() {
        binding.ivLoadingSpinner.apply {
            isVisible = true
            setIndeterminateDrawable(WanderingCubes())
        }
        
        viewLifecycleOwner.lifecycleScope.launch {
            val userId = pref.getUserId().first().toString()

            viewModel.getSellerData(userId).observe(viewLifecycleOwner) { result ->
                if (result != null) {
                    when (result) {
                        is DataResult.Loading -> {}

                        is DataResult.Success -> {
                            binding.ivLoadingSpinner.isVisible = false

                            if (result.data.sellerName?.isNotEmpty() == true) {
                                updateSellerData(result.data.sellerName)
                            }

                            binding.btnEditProfile.setOnClickListener {
                                findNavController().navigate(SellerDashboardFragmentDirections.actionNavigationSellerDashboardToNavigationEditSellerProfile())
                            }

                            binding.btnAddProduct.setOnClickListener {
                                findNavController().navigate(SellerDashboardFragmentDirections.actionNavigationSellerDashboardToNavigationAddProduct(uri = null, id = result.data.id))
                            }
                        }

                        is DataResult.Error -> {
                            binding.ivLoadingSpinner.isVisible = false
                            Toast.makeText(requireContext(), getString(R.string.error_toast, result.error), Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            }
        }
    }

    private fun updateSellerData(sellerName: String) {
        binding.apply {
            tvHeading1.text = getString(R.string.welcome_seller, sellerName)
            tvUsername.text = sellerName
            tvRentedOrder.text = "0"
            tvUnconfirmedOrder.text = "0"
            tvReturnedOrder.text = "0"
            tvUnshippingOrder.text = "0"
        }
    }
}