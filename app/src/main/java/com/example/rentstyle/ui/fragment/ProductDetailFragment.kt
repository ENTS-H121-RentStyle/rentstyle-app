package com.example.rentstyle.ui.fragment

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.bumptech.glide.request.RequestOptions
import com.example.rentstyle.R
import com.example.rentstyle.databinding.FragmentProductDetailBinding
import com.example.rentstyle.helpers.GridSpacingItemDecoration
import com.example.rentstyle.helpers.adapter.ProductAdapter
import com.example.rentstyle.helpers.adapter.ReviewDummyAdapter
import com.example.rentstyle.model.Product
import com.example.rentstyle.model.remote.response.ProductDetailResponse
import com.example.rentstyle.model.remote.retrofit.ApiConfig
import kotlinx.coroutines.launch

class ProductDetailFragment : Fragment() {
    private var _binding: FragmentProductDetailBinding? = null
    private val binding get() = _binding!!

    private lateinit var reviewAdapter: ReviewDummyAdapter
    private lateinit var recommendationAdapter: ProductAdapter
    private lateinit var token: String

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProductDetailBinding.inflate(inflater, container, false)
        token = "TOKEN_DUMMY" // Gantikan dengan cara yang tepat untuk mendapatkan token

        reviewAdapter = ReviewDummyAdapter()
        recommendationAdapter = ProductAdapter(emptyList())

        binding.apply {
            rvProductRating.adapter = reviewAdapter
            rvProductRating.layoutManager = LinearLayoutManager(context)
            rvRecommendation.adapter = recommendationAdapter
            rvRecommendation.layoutManager = GridLayoutManager(context, 2)
            rvRecommendation.addItemDecoration(GridSpacingItemDecoration(2, 25, true))
        }

        recommendationAdapter.setOnClickListener(object : ProductAdapter.OnClickListener {
            override fun onClick(position: Int, image: ImageView) {
                val product = recommendationAdapter.getItem(position)
                navigateToProductDetail(product.productId ?: product.id)
            }
        })

        fetchProductDetail()
        fetchRecommendationProducts()

        return binding.root
    }

    private fun navigateToProductDetail(productId: String) {
        val action = ProductDetailFragmentDirections.actionNavigationProductDetailSelf(productId)
        findNavController().navigate(action)
    }

    private fun fetchProductDetail() {
        val productId = arguments?.getString("productId")
        if (productId == null) {
            Log.e("ProductDetailFragment", "Product ID is null")
            return
        }

        val apiService = ApiConfig.getApiService(token) // Tambahkan token di sini
        viewLifecycleOwner.lifecycleScope.launch {
            try {
                val response = apiService.getProductDetail(productId)
                if (response.isSuccessful) {
                    val product = response.body()
                    if (product != null) {
                        bindProductData(product)
                    } else {
                        Log.e("ProductDetailFragment", "Empty product detail response")
                    }
                } else {
                    Log.e("ProductDetailFragment", "Failed to load product detail: ${response.code()}")
                }
            } catch (e: Exception) {
                Log.e("ProductDetailFragment", "Failed to load product detail: ${e.message}", e)
            }
        }
    }

    private fun fetchRecommendationProducts() {
        val apiService = ApiConfig.getApiService(token) // Tambahkan token di sini
        viewLifecycleOwner.lifecycleScope.launch {
            try {
                val response = apiService.getProducts(1, 10)
                if (response.isSuccessful) {
                    response.body()?.let { updateRecommendationAdapter(it) }
                } else {
                    Log.e("ProductDetailFragment", "Failed to load recommendation products: ${response.code()}")
                }
            } catch (e: Exception) {
                Log.e("ProductDetailFragment", "Failed to load recommendation products: ${e.message}", e)
            }
        }
    }

    private fun updateRecommendationAdapter(products: List<Product>) {
        recommendationAdapter.updateData(products)
    }

    @SuppressLint("SetTextI18n")
    private fun bindProductData(product: ProductDetailResponse) {
        binding.apply {
            Glide.with(requireContext())
                .load(product.image)
                .apply(
                    RequestOptions()
                        .placeholder(R.drawable.img_placeholder)
                        .error(R.drawable.img_placeholder)
                )
                .transition(DrawableTransitionOptions.withCrossFade())
                .into(ivProductImage)
            tvProductName.text = product.productName
            tvProductPrice.text = "Rp ${product.rentPrice} / hari"
            tvProductRating.text =
                "Rating: %.1f".format(product.reviews.map { it.rating }.average())
            tvProductShopName.text = product.seller.sellerName
            tvShopLocation.text = product.seller.city
            tvProductCategory.text = product.category
            tvProductColor.text = product.color
            tvProductSize.text = product.size
            tvProductDescription.text = product.desc

            reviewAdapter.updateReviews(product.reviews)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
