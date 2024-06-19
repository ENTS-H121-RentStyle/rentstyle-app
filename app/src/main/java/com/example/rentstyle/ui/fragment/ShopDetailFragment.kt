package com.example.rentstyle.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.rentstyle.R
import com.example.rentstyle.databinding.FragmentShopDetailBinding
import com.example.rentstyle.helpers.GridSpacingItemDecoration
import com.example.rentstyle.helpers.adapter.RecyclerDummyAdapter
import com.example.rentstyle.model.Product
import com.example.rentstyle.model.remote.response.SellerResponseData
import com.example.rentstyle.model.remote.retrofit.ApiConfig
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ShopDetailFragment : Fragment() {
    private lateinit var _binding: FragmentShopDetailBinding
    private val binding get() = _binding

    private lateinit var rvRecommendation: RecyclerView
    private lateinit var rvAllProducts: RecyclerView
    private lateinit var recommendationAdapter: RecyclerDummyAdapter
    private lateinit var allProductsAdapter: RecyclerDummyAdapter
    private var sellerId: String? = null

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

        rvAllProducts.addItemDecoration(GridSpacingItemDecoration(2, 25, true))

        recommendationAdapter.setOnClickListener(object : RecyclerDummyAdapter.OnClickListener {
            override fun onClick(product: Product, image: ImageView) {
                val action = ShopDetailFragmentDirections.actionNavigationShopDetailToNavigationProductDetail(product.id)
                findNavController().navigate(action)
            }
        })

        allProductsAdapter.setOnClickListener(object : RecyclerDummyAdapter.OnClickListener {
            override fun onClick(product: Product, image: ImageView) {
                val action = ShopDetailFragmentDirections.actionNavigationShopDetailToNavigationProductDetail(product.id)
                findNavController().navigate(action)
            }
        })

        binding.btnShoppingCart.setOnClickListener {
            findNavController().navigate(ShopDetailFragmentDirections.actionNavigationShopDetailToNavigationShoppingCart())
        }

        sellerId = arguments?.getString("sellerId")
        if (sellerId != null) {
            fetchProductsBySeller(sellerId!!)
        }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        sellerId = arguments?.getString("sellerId")
        if (sellerId != null) {
            fetchSellerData(sellerId!!)
        }
    }

    private fun fetchProductsBySeller(sellerId: String) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val apiService = ApiConfig.getApiService(sellerId)
                val response = apiService.getFilteredProducts(sellerId, 1, 10)
                if (response.isSuccessful) {
                    val products = response.body()?.products ?: emptyList()
                    withContext(Dispatchers.Main) {
                        allProductsAdapter.updateData(products)
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    private fun fetchSellerData(sellerId: String) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val apiService = ApiConfig.getApiService(sellerId)
                val response = apiService.getSellerData(sellerId)
                if (response.isSuccessful) {
                    val sellerData = response.body()
                    withContext(Dispatchers.Main) {
                        if (sellerData != null) {
                            displaySellerData(sellerData)
                        }
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    private fun displaySellerData(sellerData: SellerResponseData) {
        binding.apply {
            tvShopName.text = sellerData.sellerName
            tvShopLocation.text = sellerData.city

            Glide.with(requireContext())
                .load(sellerData.image)
                .placeholder(R.drawable.img_placeholder)
                .into(ivShopImage)

            groupButton.visibility = View.VISIBLE
        }
    }
}
