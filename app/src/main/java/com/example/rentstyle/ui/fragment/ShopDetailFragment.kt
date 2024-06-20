package com.example.rentstyle.ui.fragment

import android.content.Context
import android.os.Bundle
import android.util.DisplayMetrics
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.rentstyle.R
import com.example.rentstyle.databinding.FragmentShopDetailBinding
import com.example.rentstyle.helpers.GridSpacingItemDecoration
import com.example.rentstyle.helpers.adapter.ProductAdapter2
import com.example.rentstyle.model.Product
import com.example.rentstyle.model.remote.response.SellerResponseData
import com.example.rentstyle.model.remote.retrofit.ApiConfig
import com.github.ybq.android.spinkit.style.WanderingCubes
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlin.math.abs

class ShopDetailFragment : Fragment() {
    private lateinit var _binding: FragmentShopDetailBinding
    private val binding get() = _binding

    private lateinit var rvAllProducts: RecyclerView
    private lateinit var allProductsAdapter: ProductAdapter2
    private var sellerId: String? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentShopDetailBinding.inflate(inflater, container, false)

        rvAllProducts = binding.rvShopAllProduct

        val screenWidth = getDisplayWidthInDp(requireContext())
        val spacing = abs((screenWidth - 340) / 3)
        rvAllProducts.addItemDecoration(GridSpacingItemDecoration(2, spacing, true))

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
        binding.ivLoadingSpinner.apply {
            isVisible = true
            setIndeterminateDrawable(WanderingCubes())
        }
        viewLifecycleOwner.lifecycleScope.launch {
            try {
                val apiService = ApiConfig.getApiService(sellerId)
                val response = apiService.getFilteredProducts(sellerId, 1, 10)
                if (response.isSuccessful) {
                    val products = response.body()?.products ?: emptyList()
                    allProductsAdapter = ProductAdapter2(products)
                    rvAllProducts.adapter = allProductsAdapter
                    binding.ivLoadingSpinner.isVisible = false

                    allProductsAdapter.setOnClickListener(object : ProductAdapter2.OnClickListener {
                        override fun onClick(product: Product, image: ImageView) {
                            val action = ShopDetailFragmentDirections.actionNavigationShopDetailToNavigationProductDetail(product.id)
                            findNavController().navigate(action)
                        }
                    })
                }
                binding.ivLoadingSpinner.isVisible = false
            } catch (e: Exception) {
                e.printStackTrace()
                binding.ivLoadingSpinner.isVisible = false
            }
        }
    }

    private fun fetchSellerData(sellerId: String) {
        viewLifecycleOwner.lifecycleScope.launch {
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
        }
    }

    private fun getDisplayWidthInDp(context: Context): Int {
        val displayMetrics: DisplayMetrics = context.resources.displayMetrics
        val dpWidth = displayMetrics.widthPixels / displayMetrics.density
        return dpWidth.toInt()
    }
}
