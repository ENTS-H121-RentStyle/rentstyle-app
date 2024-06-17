package com.example.rentstyle.ui.fragment

import android.content.Context
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.DisplayMetrics
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.CompositePageTransformer
import androidx.viewpager2.widget.MarginPageTransformer
import androidx.viewpager2.widget.ViewPager2
import com.example.rentstyle.R
import com.example.rentstyle.databinding.FragmentForYouBinding
import com.example.rentstyle.helpers.GridSpacingItemDecoration
import com.example.rentstyle.helpers.adapter.ImageSliderAdapter
import com.example.rentstyle.helpers.adapter.ProductAdapter
import com.example.rentstyle.model.Product
import com.example.rentstyle.model.remote.retrofit.ApiConfig
import kotlinx.coroutines.launch
import retrofit2.Response
import kotlin.math.abs

class ForYouFragment : Fragment() {
    private lateinit var _binding: FragmentForYouBinding
    private val binding get() = _binding

    private lateinit var carousel: ViewPager2
    private lateinit var carouselAdapter: ImageSliderAdapter

    private lateinit var highestRatingRecyclerView: RecyclerView
    private lateinit var newProductRecyclerView: RecyclerView
    private lateinit var recommendationProductRecyclerView: RecyclerView
    private lateinit var highestRatingAdapter: ProductAdapter
    private lateinit var newProductAdapter: ProductAdapter
    private lateinit var recommendationProductAdapter: ProductAdapter

    private val handler = Handler(Looper.getMainLooper())
    private val slideRunnable = Runnable {
        if (carousel.currentItem == 2) {
            carousel.currentItem = 0
        } else {
            carousel.currentItem += 1
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentForYouBinding.inflate(inflater, container, false)

        val token = getTokenFromPreferences()
        fetchFilteredProducts(token)
        createCarouselInstance()
        createProductRecyclerViewInstance()
        return binding.root
    }

    private fun createProductRecyclerViewInstance() {
        binding.apply {
            highestRatingRecyclerView = rvHighestRating
            newProductRecyclerView = rvNewProduct
            recommendationProductRecyclerView = rvRecommendation
        }

        highestRatingAdapter = ProductAdapter(emptyList())
        newProductAdapter = ProductAdapter(emptyList())
        recommendationProductAdapter = ProductAdapter(emptyList())

        val screenWidth = getDisplayWidthInDp(requireContext())
        val spacing = abs((screenWidth - 340)/3)

        recommendationProductRecyclerView.addItemDecoration(GridSpacingItemDecoration(2, spacing, true))

        highestRatingRecyclerView.adapter = highestRatingAdapter
        newProductRecyclerView.adapter = newProductAdapter
        recommendationProductRecyclerView.adapter = recommendationProductAdapter

        highestRatingAdapter.setOnClickListener(object : ProductAdapter.OnClickListener {
            override fun onClick(position: Int, image: ImageView) {
                val product = highestRatingAdapter.getItem(position)
                navigateToProductDetail(product.id)
            }
        })

        newProductAdapter.setOnClickListener(object : ProductAdapter.OnClickListener {
            override fun onClick(position: Int, image: ImageView) {
                val product = newProductAdapter.getItem(position)
                navigateToProductDetail(product.id)
            }
        })

        recommendationProductAdapter.setOnClickListener(object : ProductAdapter.OnClickListener {
            override fun onClick(position: Int, image: ImageView) {
                val product = recommendationProductAdapter.getItem(position)
                navigateToProductDetail(product.productId ?: product.id)
            }
        })
    }

    private fun navigateToProductDetail(productId: String) {
        val action = HomeFragmentDirections.actionNavigationHomeToNavigationProductDetail(productId)
        findNavController().navigate(action)
    }

    private fun createCarouselInstance() {
        carousel = binding.vpCarousel

        val imageList = arrayListOf(
            R.drawable.img_placeholder,
            R.drawable.img_placeholder,
            R.drawable.img_placeholder
        )
        carouselAdapter = ImageSliderAdapter(requireContext(), imageList, "Banner")

        carousel.apply {
            adapter = carouselAdapter
            clipToPadding = false
            clipChildren = false
            offscreenPageLimit = 3
            getChildAt(0).overScrollMode = RecyclerView.OVER_SCROLL_NEVER
        }

        val compositePageTransformer = CompositePageTransformer()
        compositePageTransformer.addTransformer(MarginPageTransformer(10))

        carousel.setPageTransformer(compositePageTransformer)

        carousel.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                handler.removeCallbacks(slideRunnable)
                handler.postDelayed(slideRunnable, 3000)
            }
        })
    }

    private fun fetchFilteredProducts(token: String) {
        lifecycleScope.launch {
            try {
                // Fetch products with highest ratings
                val highestRatingResponse = ApiConfig.getApiService(token).getFilteredProducts("Tertinggi", 1, 10)
                if (highestRatingResponse.isSuccessful) {
                    highestRatingResponse.body()?.let { updateHighestRatingAdapter(it) }
                } else {
                    logError(highestRatingResponse)
                }

                // Fetch new products
                val newProductResponse = ApiConfig.getApiService(token).getFilteredProducts("Terbaru", 1, 10)
                if (newProductResponse.isSuccessful) {
                    newProductResponse.body()?.let { updateNewProductAdapter(it) }
                } else {
                    logError(newProductResponse)
                }

                // Fetch recommendation products
                val recommendationResponse = ApiConfig.getApiService(token).getProducts(1, 10)
                if (recommendationResponse.isSuccessful) {
                    recommendationResponse.body()?.let { updateRecommendationAdapter(it) }
                } else {
                    logError(recommendationResponse)
                }
            } catch (e: Exception) {
                e.printStackTrace()
                showErrorLog("An error occurred: ${e.message}")
            }
        }
    }

    private fun updateHighestRatingAdapter(products: List<Product>) {
        highestRatingAdapter.updateData(products)
    }

    private fun updateNewProductAdapter(products: List<Product>) {
        newProductAdapter.updateData(products)
    }

    private fun updateRecommendationAdapter(products: List<Product>) {
        recommendationProductAdapter.updateData(products)
    }

    private fun logError(response: Response<*>) {
        val errorBody = response.errorBody()?.string()
        showErrorLog("Failed to load data. Status Code: ${response.code()}, Error: $errorBody")
    }
    private fun getTokenFromPreferences(): String {
        val sharedPreferences = requireContext().getSharedPreferences("MyPreferences", Context.MODE_PRIVATE)
        return sharedPreferences.getString("auth_token", "") ?: ""
    }

    private fun getDisplayWidthInDp(context: Context): Int {
        val displayMetrics: DisplayMetrics = context.resources.displayMetrics
        val dpWidth = displayMetrics.widthPixels / displayMetrics.density
        return dpWidth.toInt()
    }

    private fun showErrorLog(message: String) {
        Log.e("ForYouFragment", message)
    }
}
