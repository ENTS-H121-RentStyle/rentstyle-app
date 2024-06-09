package com.example.rentstyle.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.rentstyle.databinding.FragmentOrderDetailBinding
import com.example.rentstyle.helpers.adapter.RecyclerCheckOutProductAdapter

class OrderDetailFragment : Fragment() {
    private lateinit var _binding: FragmentOrderDetailBinding
    private val binding get() = _binding

    private lateinit var productListAdapter: RecyclerCheckOutProductAdapter
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentOrderDetailBinding.inflate(inflater, container, false)

        binding.mainToolbar.tvToolbarTitle.text = "Order Detail"
        binding.mainToolbar.ivBackButton.setOnClickListener {
            findNavController().navigateUp()
        }

        productListAdapter = RecyclerCheckOutProductAdapter()
        binding.rvProductCoDetail.adapter = productListAdapter

        productListAdapter.setOnClickListener(object : RecyclerCheckOutProductAdapter.OnClickListener {
            override fun onClick(position: Int) {
                findNavController().navigate(OrderDetailFragmentDirections.actionNavigationOrderDetailToNavigationProductDetail())
            }
        })

        binding.tvOrderStatus.text = "Selesai"
        binding.tvOrderId.text = "12GHJ234"
        binding.tvOrderDate.text = "20/04/2020, 04:20"
        binding.tvOrderUsername.text = "Wawan"
        binding.tvOrderUserPhone.text = "08218888888"
        binding.tvOrderUserAddress.text = "Jl. Ikan Tembakang no 5, Teluk Betung"
        binding.tvOrderTotalCost.text = "Rp. 500.000"
        binding.tvOrderDepositCost.text = "Rp. 50.000"
        binding.tvOrderPaymentCost.text = "Rp. 550.000"
        binding.tvOrderPaymentMethod.text = "Dana Indonesia"
        binding.btnAccess1.text = "Nilai produk"
        binding.btnAccess2.text = "Sewa lagi"

        return binding.root
    }
}