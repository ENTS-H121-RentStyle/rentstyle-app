package com.example.rentstyle.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.net.toUri
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.rentstyle.R
import com.example.rentstyle.databinding.FragmentProfileBinding
import com.example.rentstyle.helpers.DataResult
import com.example.rentstyle.helpers.FirebaseToken.updateTokenId
import com.example.rentstyle.helpers.adapter.OrderAdapter
import com.example.rentstyle.helpers.adapter.OrderSkeletonAdapter
import com.example.rentstyle.model.local.datastore.LoginSession
import com.example.rentstyle.model.local.datastore.dataStore
import com.example.rentstyle.viewmodel.ProfileViewModel
import com.example.rentstyle.viewmodel.SellerViewModel
import com.example.rentstyle.viewmodel.SellerViewModelFactory
import com.example.rentstyle.viewmodel.TransactionViewModel
import com.example.rentstyle.viewmodel.UserViewModel
import com.example.rentstyle.viewmodel.UserViewModelFactory
import com.github.ybq.android.spinkit.style.WanderingCubes
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class ProfileFragment : Fragment() {
    private lateinit var _binding: FragmentProfileBinding
    private val binding get() = _binding

    private lateinit var viewModel: ProfileViewModel

    private val userViewModel: UserViewModel by activityViewModels {
        UserViewModelFactory.getInstance(this.requireActivity().application)
    }

    private val orderViewModel: TransactionViewModel by activityViewModels {
        UserViewModelFactory.getInstance(this.requireActivity().application)
    }

    private val sellerViewModel: SellerViewModel by activityViewModels{
        SellerViewModelFactory.getInstance(this.requireActivity().application)
    }

    private lateinit var orderListAdapter : OrderAdapter

    private lateinit var historySkeleton: RecyclerView
    private lateinit var historySkeletonAdapter: OrderSkeletonAdapter

    private lateinit var pref: LoginSession
    private var run = true

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProfileBinding.inflate(inflater, container, false)

        updateTokenId(requireContext(), viewLifecycleOwner)

        val factory = SellerViewModelFactory.getInstance(this.requireActivity().application)
        viewModel = ViewModelProvider(this, factory)[ProfileViewModel::class.java]

        pref = LoginSession.getInstance(requireActivity().application.dataStore)

        viewLifecycleOwner.lifecycleScope.launch {
            if (pref.getSellerId().first() != null && pref.getSellerId().first() != "null") {
                binding.tvBtnUserShop.text = getString(R.string.txt_shop_dashboard)
            } else {
                binding.tvBtnUserShop.text = getString(R.string.txt_join_us)
            }
        }

        historySkeleton = binding.rvHistorySkeleton
        historySkeletonAdapter = OrderSkeletonAdapter(5)
        historySkeleton.adapter = historySkeletonAdapter

        setUserProfile()

        getOrderHistory()

        binding.btnShoppingCart.setOnClickListener {
            findNavController().navigate(ProfileFragmentDirections.actionNavigationProfileToNavigationShoppingCart())
        }

        binding.btnUserSetting.setOnClickListener {
            findNavController().navigate(ProfileFragmentDirections.actionNavigationProfileToNavigationSetting())
        }

        binding.btnUserShop.setOnClickListener {
            viewLifecycleOwner.lifecycleScope.launch {
                if (pref.getSellerId().first() != null
                    && (pref.getSellerId().first() != "null" || sellerViewModel.registeredSellerId.value != null)) {
                    findNavController().navigate(ProfileFragmentDirections.actionNavigationProfileToNavigationSellerDashboard())
                } else if (pref.getSellerId().first() == "null"){
                    findNavController().navigate(ProfileFragmentDirections.actionNavigationProfileToNavigationOnboardingSeller())
                } else {
                    checkUser()
                }
            }
        }

        binding.btnChat.setOnClickListener {
            Toast.makeText(requireContext(), getString(R.string.txt_feature_is_not_available), Toast.LENGTH_SHORT).show()
        }

        return binding.root
    }

    private fun getOrderHistory() {
        viewLifecycleOwner.lifecycleScope.launch {
            orderViewModel.getAllOrder().observe(viewLifecycleOwner) { result ->
                if (result != null) {
                    when (result) {
                        is DataResult.Loading -> { }

                        is DataResult.Success -> {
                            val data = result.data
                            orderListAdapter = OrderAdapter(data)
                            binding.rvRentalHistory.adapter = orderListAdapter

                            if (data.isEmpty()) {
                                binding.tvNoHistory.isVisible = true
                                historySkeleton.isVisible = false
                            }

                            orderListAdapter.setOnClickListener(object : OrderAdapter.OnClickListener {
                                override fun onClick(position: Int, orderId: String) {
                                    findNavController().navigate(ProfileFragmentDirections.actionNavigationProfileToNavigationOrderDetail(orderId))
                                }

                            })

                            binding.rvRentalHistory.isVisible = true
                            historySkeleton.isVisible = false
                        }

                        is DataResult.Error -> {
                            Toast.makeText(requireContext(), getString(R.string.error_toast, result.error), Toast.LENGTH_SHORT).show()
                            historySkeleton.isVisible = false
                            binding.tvNoHistory.isVisible = true
                        }
                    }
                } else {
                    historySkeleton.isVisible = false
                    Toast.makeText(requireContext(), getString(R.string.txt_fail_get_transaction), Toast.LENGTH_SHORT).show()
                    binding.tvNoHistory.isVisible = true
                }
            }
        }
    }

    private fun setUserProfile() {
        binding.ivLoadingSpinner.apply {
            isVisible = true
            setIndeterminateDrawable(WanderingCubes())
        }

        if (userViewModel.userData.value == null) {
            viewLifecycleOwner.lifecycleScope.launch {
                userViewModel.getUserProfile().observe(viewLifecycleOwner) { result ->
                    if (result != null) {
                        when (result) {
                            is DataResult.Loading -> { }

                            is DataResult.Success -> {
                                val data = result.data
                                userViewModel.userData.value = arrayListOf(data.name, data.image)

                                updateUserProfile(data.name, data.image)
                                binding.ivLoadingSpinner.isVisible = false
                                binding.btnEditProfile.setOnClickListener {
                                    findNavController().navigate(ProfileFragmentDirections.actionNavigationProfileToNavigationEditUserProfile())
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
        } else {
            val data = userViewModel.userData.value
            updateUserProfile(data?.get(0), data?.get(1))
            binding.ivLoadingSpinner.isVisible = false
            binding.btnEditProfile.setOnClickListener {
                findNavController().navigate(ProfileFragmentDirections.actionNavigationProfileToNavigationEditUserProfile())
            }
        }
    }

    private fun updateUserProfile (name: String?, image: String?) {
        if (image != null) {
            Glide.with(requireContext())
                .load(image.toUri())
                .apply(
                    RequestOptions()
                        .placeholder(R.drawable.img_placeholder)
                        .error(R.drawable.img_placeholder)
                )
                .into(binding.ivUserImage)
        }
        binding.tvUsername.text = name
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