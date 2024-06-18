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
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.paging.PagingData
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
import com.example.rentstyle.model.local.datastore.LoginSession
import com.example.rentstyle.model.local.datastore.dataStore
import com.example.rentstyle.model.repository.RecommendationRepository
import com.example.rentstyle.ui.viewmodel.RecommendationViewModel
import com.example.rentstyle.ui.viewmodel.RecommendationViewModelFactory
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import retrofit2.Response
import kotlin.math.abs

class ForYouFragment : Fragment() {
    private var _binding: FragmentForYouBinding? = null
    private val binding get() = _binding!!

    private lateinit var carousel: ViewPager2
    private lateinit var carouselAdapter: ImageSliderAdapter

    private lateinit var highestRatingRecyclerView: RecyclerView
    private lateinit var newProductRecyclerView: RecyclerView
    private lateinit var recommendationProductRecyclerView: RecyclerView
    private lateinit var highestRatingAdapter: ProductAdapter
    private lateinit var newProductAdapter: ProductAdapter
    private lateinit var recommendationProductAdapter: ProductAdapter

    private lateinit var loginSession: LoginSession
    private lateinit var userId: String

    private val handler = Handler(Looper.getMainLooper())
    private val slideRunnable = Runnable {
        if (carousel.currentItem == 2) {
            carousel.currentItem = 0
        } else {
            carousel.currentItem += 1
        }
    }

    private lateinit var recommendationViewModel: RecommendationViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentForYouBinding.inflate(inflater, container, false)
        loginSession = LoginSession.getInstance(requireContext().dataStore)
        viewLifecycleOwner.lifecycleScope.launch {
            val token = loginSession.getSessionToken().first() ?: ""
            if (token.isNotEmpty()) {
                val repository = RecommendationRepository(ApiConfig.getApiService(token))
                val viewModelFactory = RecommendationViewModelFactory(repository)
                recommendationViewModel = ViewModelProvider(this@ForYouFragment, viewModelFactory)[RecommendationViewModel::class.java]

                createCarouselInstance()
                createProductRecyclerViewInstance()
                fetchFilteredProducts(token)
                observeRecommendationProducts()
            } else {
                Log.e("ForYouFragment", "Token is empty")
            }
        }

        return binding.root
    }

    private fun createProductRecyclerViewInstance() {
        binding.apply {
            highestRatingRecyclerView = rvHighestRating
            newProductRecyclerView = rvNewProduct
            recommendationProductRecyclerView = rvRecommendation
        }

        highestRatingAdapter = ProductAdapter()
        newProductAdapter = ProductAdapter()
        recommendationProductAdapter = ProductAdapter()

        val screenWidth = getDisplayWidthInDp(requireContext())
        val spacing = abs((screenWidth - 340) / 3)

        recommendationProductRecyclerView.addItemDecoration(GridSpacingItemDecoration(2, spacing, true))

        highestRatingRecyclerView.adapter = highestRatingAdapter
        newProductRecyclerView.adapter = newProductAdapter
        recommendationProductRecyclerView.adapter = recommendationProductAdapter

        highestRatingAdapter.setOnClickListener(object : ProductAdapter.OnClickListener {
            override fun onClick(position: Int, image: ImageView) {
                val product = highestRatingAdapter.snapshot().get(position)
                product?.let { navigateToProductDetail(it.id) }
            }
        })

        newProductAdapter.setOnClickListener(object : ProductAdapter.OnClickListener {
            override fun onClick(position: Int, image: ImageView) {
                val product = newProductAdapter.snapshot().get(position)
                product?.let { navigateToProductDetail(it.id) }
            }
        })

        recommendationProductAdapter.setOnClickListener(object : ProductAdapter.OnClickListener {
            override fun onClick(position: Int, image: ImageView) {
                val product = recommendationProductAdapter.snapshot().get(position)
                product?.let { navigateToProductDetail(it.id) }
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
        viewLifecycleOwner.lifecycleScope.launch {
            try {
                val highestRatingResponse = ApiConfig.getApiService(token).getFilteredProducts("Tertinggi", 1, 10)
                if (highestRatingResponse.isSuccessful) {
                    highestRatingResponse.body()?.products?.let { updateHighestRatingAdapter(it) }
                } else {
                    logError(highestRatingResponse)
                }

                val newProductResponse = ApiConfig.getApiService(token).getFilteredProducts("Terbaru", 1, 10)
                if (newProductResponse.isSuccessful) {
                    newProductResponse.body()?.products?.let { updateNewProductAdapter(it) }
                } else {
                    logError(newProductResponse)
                }
            } catch (e: Exception) {
                Log.e("ForYouFragment", "Terjadi kesalahan: ${e.message}", e)
            }
        }
    }

    private fun observeRecommendationProducts() {
        viewLifecycleOwner.lifecycleScope.launch {
            try {
                val token = loginSession.getSessionToken().first() ?: ""
                userId = loginSession.getUserId().first() ?: ""
                recommendationViewModel.getRecommendationProducts(userId)
                    .collectLatest { pagingData ->
                        recommendationProductAdapter.submitData(pagingData)
                    }
            } catch (e: Exception) {
                Log.e(
                    "ForYouFragment",
                    "Failed to observe recommendation products: ${e.message}",
                    e
                )
            }
        }
    }

    private fun updateHighestRatingAdapter(products: List<Product>) {
        highestRatingAdapter.submitData(lifecycle, PagingData.from(products))
    }

    private fun updateNewProductAdapter(products: List<Product>) {
        newProductAdapter.submitData(lifecycle, PagingData.from(products))
    }

    private fun logError(response: Response<*>) {
        val errorBody = response.errorBody()?.string()
        Log.e("ForYouFragment", "Failed to load data. Status Code: ${response.code()}, Error: $errorBody")
    }

    private fun getDisplayWidthInDp(context: Context): Int {
        val displayMetrics: DisplayMetrics = context.resources.displayMetrics
        val dpWidth = displayMetrics.widthPixels / displayMetrics.density
        return dpWidth.toInt()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        handler.removeCallbacks(slideRunnable)
        _binding = null
    }
}
