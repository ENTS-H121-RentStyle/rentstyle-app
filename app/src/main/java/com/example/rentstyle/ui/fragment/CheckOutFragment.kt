package com.example.rentstyle.ui.fragment

import android.annotation.SuppressLint
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.Glide
import com.example.rentstyle.R
import com.example.rentstyle.databinding.FragmentCheckOutBinding
import com.example.rentstyle.helpers.DataResult
import com.example.rentstyle.helpers.DateHelpers.getReturnDate
import com.example.rentstyle.helpers.FirebaseToken
import com.example.rentstyle.helpers.FirebaseToken.updateTokenId
import com.example.rentstyle.helpers.StatusResult
import com.example.rentstyle.model.local.datastore.LoginSession
import com.example.rentstyle.model.local.datastore.dataStore
import com.example.rentstyle.viewmodel.OrderViewModel
import com.example.rentstyle.viewmodel.ProductViewModelFactory
import com.example.rentstyle.viewmodel.UserViewModel
import com.example.rentstyle.viewmodel.UserViewModelFactory
import com.github.ybq.android.spinkit.style.WanderingCubes
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class CheckOutFragment : Fragment() {
    private lateinit var _binding: FragmentCheckOutBinding
    private val binding get() = _binding

    private val userViewModel: UserViewModel by activityViewModels {
        UserViewModelFactory.getInstance(this.requireActivity().application)
    }

    private val orderViewModel: OrderViewModel by activityViewModels {
        ProductViewModelFactory.getInstance(this.requireActivity().application)
    }

    private val args: CheckOutFragmentArgs by navArgs()

    private lateinit var pref: LoginSession

    private var run = true

    private var productId = ""
    private var userId = ""
    private var orderDate = ""
    private var returnDate = ""
    private var rentDuration = ""
    private var serviceFee = "1000"
    private var deposit = ""
    private var rentPrice = ""
    private var totalPayment = ""

    @RequiresApi(Build.VERSION_CODES.O)
    @SuppressLint("SetTextI18n")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCheckOutBinding.inflate(inflater, container, false)

        updateTokenId(requireContext(), viewLifecycleOwner)

        binding.mainToolbar.apply {
            tvToolbarTitle.text = getString(R.string.title_checkout)
            ivBackButton.setOnClickListener {
                findNavController().navigateUp()
            }
        }

        pref = LoginSession.getInstance(requireActivity().application.dataStore)

        binding.apply {
            tvProductName.text = args.productName
            tvProductPrice.text = getString(R.string.product_co_price, args.rentPrice.toString(), args.rentDuration.toString())
            tvOrderTotalCost.text = "Rp. ${args.rentPrice * args.rentDuration}"
            tvOrderDepositCost.text = "Rp. ${args.rentPrice * args.rentDuration * 10 / 100}"
            tvOrderPaymentCost.text = "Rp. ${(args.rentPrice * args.rentDuration) + (args.rentPrice * args.rentDuration * 10 / 100)}"
        }

        Glide.with(requireContext())
            .load(args.productImage)
            .into(binding.ivProductPhoto)

        getUserData()
        getOrderData()
        checkOut()

        binding.btnChangePayment.setOnClickListener {
            Toast.makeText(requireContext(), "Feature is not available yet", Toast.LENGTH_SHORT).show()
        }

        return binding.root
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun getOrderData() {
        productId = args.productId

        viewLifecycleOwner.lifecycleScope.launch {
            userId = pref.getUserId().first().toString()
        }

        val timeStamp: String = SimpleDateFormat("yyyy-MM-dd-HH-mm-ss-SSS", Locale.getDefault()).format(Date())

        orderDate = timeStamp.slice(0..9)
        returnDate = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) getReturnDate(orderDate, args.rentDuration.toLong()) else orderDate
        rentDuration = args.rentDuration.toString()
        deposit = "${args.rentPrice * args.rentDuration * 10 / 100}"
        rentPrice = args.rentPrice.toString()
        totalPayment = "${(args.rentPrice * args.rentDuration) + (args.rentPrice * args.rentDuration * 10 / 100)}"
    }

    private fun checkOut() {
        binding.btnCheckOut.setOnClickListener {

            if (productId != "" && userId != "" && orderDate != ""
                && returnDate != "" && rentDuration != ""
                && deposit != "" && rentPrice != "" && totalPayment != "") {
                binding.ivLoadingSpinner.apply {
                    isVisible = true
                    setIndeterminateDrawable(WanderingCubes())
                }

                val bodyProductId = productId.toRequestBody("text/plain".toMediaType())
                val bodyUserId = userId.toRequestBody("text/plain".toMediaType())
                val bodyOrderDate = orderDate.toRequestBody("text/plain".toMediaType())
                val bodyReturnDate = returnDate.toRequestBody("text/plain".toMediaType())
                val bodyRentDuration = rentDuration.toRequestBody("text/plain".toMediaType())
                val bodyServiceFee = serviceFee.toRequestBody("text/plain".toMediaType())
                val bodyDeposit = deposit.toRequestBody("text/plain".toMediaType())
                val bodyRentPrice = rentPrice.toRequestBody("text/plain".toMediaType())
                val bodyTotalPayment = totalPayment.toRequestBody("text/plain".toMediaType())

                viewLifecycleOwner.lifecycleScope.launch {
                    orderViewModel.makeOrder(bodyProductId, bodyUserId, bodyOrderDate,
                        bodyReturnDate, bodyRentDuration, bodyServiceFee, bodyDeposit,
                        bodyRentPrice, bodyTotalPayment).observe(viewLifecycleOwner) { result ->
                            if (result != null) {
                                when (result) {
                                    is StatusResult.Loading -> {}

                                    is StatusResult.Success -> {
                                        binding.ivLoadingSpinner.isVisible = false
                                        if (run) {
                                            Toast.makeText(requireContext(), result.success, Toast.LENGTH_SHORT).show()
                                            run = false
                                            findNavController().navigate(CheckOutFragmentDirections.actionNavigationCheckOutToNavigationProfile())
                                        }
                                    }

                                    is StatusResult.Error -> {
                                        binding.ivLoadingSpinner.isVisible = false
                                        Toast.makeText(requireContext(), result.error, Toast.LENGTH_SHORT).show()
                                        if (run) {
                                            run = false
                                            findNavController().navigateUp()
                                        }
                                    }
                                }
                            } else {
                                binding.ivLoadingSpinner.apply {
                                    isVisible = true
                                    setIndeterminateDrawable(WanderingCubes())
                                }
                                Toast.makeText(requireContext(), "Fail to make order", Toast.LENGTH_SHORT).show()
                                findNavController().navigateUp()
                            }
                    }
                }

            } else {
                Toast.makeText(requireContext(), "Failed to make order", Toast.LENGTH_SHORT).show()
                findNavController().navigateUp()
            }
        }
    }

    private fun getUserData() {
        binding.ivLoadingSpinner.apply {
            isVisible = true
            setIndeterminateDrawable(WanderingCubes())
        }

        viewLifecycleOwner.lifecycleScope.launch {
            userViewModel.getUserProfile().observe(viewLifecycleOwner) { result ->
                if (result != null) {
                    when (result) {
                        is DataResult.Loading -> { }

                        is DataResult.Success -> {
                            val data = result.data

                            binding.apply {
                                tvOrderUsername.text = data.name
                                tvOrderUserPhone.text = data.phone
                                tvOrderUserAddress.text = data.address
                            }

                            binding.ivLoadingSpinner.isVisible = false
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
}