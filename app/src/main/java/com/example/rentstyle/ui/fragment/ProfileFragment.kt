package com.example.rentstyle.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.rentstyle.databinding.FragmentProfileBinding
import com.example.rentstyle.helpers.FilterModel
import com.example.rentstyle.helpers.adapter.RecyclerDummyOrderAdapter
import com.example.rentstyle.helpers.adapter.RecyclerFilterAdapter

class ProfileFragment : Fragment() {
    private lateinit var _binding: FragmentProfileBinding
    private val binding get() = _binding

    private lateinit var filterAdapter : RecyclerFilterAdapter
    private lateinit var orderListAdapter : RecyclerDummyOrderAdapter
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProfileBinding.inflate(inflater, container, false)

        binding.tvUsername.text = "username"

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

        return binding.root
    }
}