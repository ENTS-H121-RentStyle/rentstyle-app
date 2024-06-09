package com.example.rentstyle.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.appcompat.widget.SearchView.OnQueryTextListener
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.example.rentstyle.databinding.FragmentShopDetailBinding
import com.example.rentstyle.helpers.GridSpacingItemDecoration
import com.example.rentstyle.helpers.adapter.RecyclerDummyAdapter

class ShopDetailFragment : Fragment() {
    private lateinit var _binding: FragmentShopDetailBinding
    private val binding get() = _binding

    private lateinit var rvRecommendation : RecyclerView
    private lateinit var rvAllProducts : RecyclerView
    private lateinit var recommendationAdapter : RecyclerDummyAdapter
    private lateinit var allProductsAdapter : RecyclerDummyAdapter
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentShopDetailBinding.inflate(inflater, container, false)

        binding.apply {
            rvRecommendation = rvShopRecommendation
            rvAllProducts = rvShopAllProduct
        }

        recommendationAdapter = RecyclerDummyAdapter()
        allProductsAdapter = RecyclerDummyAdapter()

        rvRecommendation.adapter = recommendationAdapter
        rvAllProducts.adapter = allProductsAdapter

        rvAllProducts.addItemDecoration(GridSpacingItemDecoration(2,25,true))

        recommendationAdapter.setOnClickListener(object : RecyclerDummyAdapter.OnClickListener {
            override fun onClick(position: Int, image: ImageView) {
                findNavController().navigate(ShopDetailFragmentDirections.actionNavigationShopDetailToNavigationProductDetail())
            }
        })

        allProductsAdapter.setOnClickListener(object : RecyclerDummyAdapter.OnClickListener {
            override fun onClick(position: Int, image: ImageView) {
                findNavController().navigate(ShopDetailFragmentDirections.actionNavigationShopDetailToNavigationProductDetail())
            }
        })

        binding.btnShoppingCart.setOnClickListener {
            findNavController().navigate(ShopDetailFragmentDirections.actionNavigationShopDetailToNavigationShoppingCart())
        }

        return binding.root
    }
}