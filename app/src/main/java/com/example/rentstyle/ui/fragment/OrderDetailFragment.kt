package com.example.rentstyle.ui.fragment

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.bumptech.glide.request.RequestOptions
import com.example.rentstyle.R
import com.example.rentstyle.databinding.FragmentOrderDetailBinding
import com.example.rentstyle.helpers.DataResult
import com.example.rentstyle.helpers.adapter.OrderAdapter
import com.example.rentstyle.model.remote.response.ResponseOrderItem
import com.example.rentstyle.viewmodel.TransactionViewModel
import com.example.rentstyle.viewmodel.UserViewModel
import com.example.rentstyle.viewmodel.UserViewModelFactory
import kotlinx.coroutines.launch

class OrderDetailFragment : Fragment() {
    private lateinit var _binding: FragmentOrderDetailBinding
    private val binding get() = _binding

    private val args: OrderDetailFragmentArgs by navArgs()

    private val orderViewModel: TransactionViewModel by activityViewModels {
        UserViewModelFactory.getInstance(this.requireActivity().application)
    }

    private val userViewModel: UserViewModel by activityViewModels {
        UserViewModelFactory.getInstance(this.requireActivity().application)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentOrderDetailBinding.inflate(inflater, container, false)

        binding.mainToolbar.tvToolbarTitle.text = getString(R.string.title_order_detail)
        binding.mainToolbar.ivBackButton.setOnClickListener {
            findNavController().navigateUp()
        }

        Log.d("order id", args.orderId)

        viewLifecycleOwner.lifecycleScope.launch {
            orderViewModel.getOrderById(args.orderId).observe(viewLifecycleOwner) { result ->
                if (result != null) {
                    when (result) {
                        is DataResult.Loading -> { }

                        is DataResult.Success -> {
                            val data = result.data
                            bindingOrderData(data)
                        }

                        is DataResult.Error -> {
                            binding.ivLoadingSpinner.isVisible = false
                            Toast.makeText(requireContext(), getString(R.string.error_toast, result.error), Toast.LENGTH_SHORT).show()
                            binding.ivLoadingSpinner.isVisible = false
                        }
                    }
                } else {
                    binding.ivLoadingSpinner.isVisible = false
                    Toast.makeText(requireContext(), "Fail to get order detail", Toast.LENGTH_SHORT).show()
                }
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            userViewModel.getUserProfile().observe(viewLifecycleOwner) { result ->
                if (result != null) {
                    when (result) {
                        is DataResult.Loading -> { }

                        is DataResult.Success -> {
                            val data = result.data
                            updateUserData(data.name!!, data.phone.toString(), data.address!!)
                        }

                        is DataResult.Error -> {
                            Toast.makeText(requireContext(), getString(R.string.error_toast, result.error), Toast.LENGTH_SHORT).show()
                        }
                    }
                    binding.ivLoadingSpinner.isVisible = false
                } else {
                    binding.ivLoadingSpinner.isVisible = false
                    Toast.makeText(requireContext(), "Fail to get order detail", Toast.LENGTH_SHORT).show()
                }
            }
        }

        binding.btnAccess1.text = "Nilai produk"
        binding.btnAccess2.text = "Sewa lagi"

        return binding.root
    }

    private fun updateUserData(name: String, phone: String, address: String) {
        binding.apply {
            tvOrderUsername.text = name
            tvOrderUserPhone.text = phone
            tvOrderUserAddress.text = address
        }

        binding.ivLoadingSpinner.isVisible = false
    }

    private fun bindingOrderData(order: ResponseOrderItem) {
        binding.apply {
            tvOrderStatus.text = order.orderStatus
            tvOrderId.text = order.id!!.slice(0..6)
            Glide.with(requireContext())
                .load(order.product!!.productImage)
                .apply(
                    RequestOptions()
                        .placeholder(R.drawable.img_placeholder)
                        .error(R.drawable.img_placeholder)
                ).into(ivProductPhoto)
            tvProductName.text = order.product.productName
            tvOrderDate.text = order.orderDate
            tvOrderReturnDate.text = order.returnDate
            tvProductPrice.text = getString(R.string.product_co_price, order.rentPrice.toString(), order.rentDuration.toString())
            tvOrderTotalCost.text = getString(R.string.txt_order_money, (order.rentDuration?.let {
                order.rentPrice?.times(
                    it
                )
            }).toString())
            tvOrderDepositCost.text = getString(R.string.txt_order_money, order.deposit.toString())
            tvOrderPaymentCost.text = getString(R.string.txt_order_money, order.totalPayment.toString())
        }

        binding.ivLoadingSpinner.isVisible = false
    }
}