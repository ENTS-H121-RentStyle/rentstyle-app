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
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.paging.LoadState
import androidx.paging.PagingData
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.CompositePageTransformer
import androidx.viewpager2.widget.MarginPageTransformer
import androidx.viewpager2.widget.ViewPager2
import com.example.rentstyle.R
import com.example.rentstyle.databinding.FragmentForYouBinding
import com.example.rentstyle.di.Injection
import com.example.rentstyle.helpers.FirebaseToken.updateTokenId
import com.example.rentstyle.helpers.GridSpacingItemDecoration
import com.example.rentstyle.helpers.adapter.ImageSliderAdapter
import com.example.rentstyle.helpers.adapter.ProductAdapter
import com.example.rentstyle.helpers.adapter.ProductPagingAdapter
import com.example.rentstyle.helpers.adapter.ProductSkeletonAdapter
import com.example.rentstyle.model.Product
import com.example.rentstyle.model.local.datastore.LoginSession
import com.example.rentstyle.model.local.datastore.dataStore
import com.example.rentstyle.model.remote.retrofit.ApiConfig
import com.example.rentstyle.ui.viewmodel.RecommendationViewModel
import com.example.rentstyle.ui.viewmodel.RecommendationViewModelFactory
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
    private lateinit var recommendationProductAdapter: ProductPagingAdapter

    private lateinit var highestRatingSkeleton: RecyclerView
    private lateinit var highestRatingSkeletonAdapter: ProductSkeletonAdapter
    private lateinit var newProductSkeleton: RecyclerView
    private lateinit var newProductSkeletonAdapter: ProductSkeletonAdapter
    private lateinit var recommendationSkeleton: RecyclerView
    private lateinit var recommendationSkeletonAdapter: ProductSkeletonAdapter

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

    private val recommendationViewModel: RecommendationViewModel by activityViewModels {
        RecommendationViewModelFactory(Injection.provideRecommendationRepository(requireContext()))
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentForYouBinding.inflate(inflater, container, false)

        updateTokenId(requireContext(), viewLifecycleOwner)

        loginSession = LoginSession.getInstance(requireActivity().application.dataStore)
        viewLifecycleOwner.lifecycleScope.launch {
            val token = loginSession.getSessionToken().first().toString()
            if (token.isNotEmpty()) {
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

            highestRatingSkeleton = rvHighestRatingSkeleton
            newProductSkeleton = rvNewProductSkeleton
            recommendationSkeleton = rvRecommendationSkeleton
        }

        highestRatingAdapter = ProductAdapter()
        newProductAdapter = ProductAdapter()
        recommendationProductAdapter = ProductPagingAdapter()

        highestRatingSkeletonAdapter = ProductSkeletonAdapter(4)
        newProductSkeletonAdapter = ProductSkeletonAdapter(4)
        recommendationSkeletonAdapter = ProductSkeletonAdapter(10)

        val screenWidth = getDisplayWidthInDp(requireContext())
        val spacing = abs((screenWidth - 340) / 3)

        recommendationProductRecyclerView.addItemDecoration(GridSpacingItemDecoration(2, spacing, true))
        recommendationSkeleton.addItemDecoration(GridSpacingItemDecoration(2, spacing, true))

        highestRatingRecyclerView.adapter = highestRatingAdapter
        newProductRecyclerView.adapter = newProductAdapter
        recommendationProductRecyclerView.adapter = recommendationProductAdapter

        highestRatingSkeleton.adapter = highestRatingSkeletonAdapter
        newProductSkeleton.adapter = newProductSkeletonAdapter
        recommendationSkeleton.adapter = recommendationSkeletonAdapter

        highestRatingAdapter.setOnClickListener(object : ProductAdapter.OnClickListener {
            override fun onClick(position: Int, image: ImageView) {
                val product = highestRatingAdapter.snapshot()[position]
                product?.let { navigateToProductDetail(it.id) }
            }
        })

        newProductAdapter.setOnClickListener(object : ProductAdapter.OnClickListener {
            override fun onClick(position: Int, image: ImageView) {
                val product = newProductAdapter.snapshot()[position]
                product?.let { navigateToProductDetail(it.id) }
            }
        })

        recommendationProductAdapter.setOnClickListener(object : ProductPagingAdapter.OnClickListener {
            override fun onClick(position: Int, model: Product) {
                val product = recommendationProductAdapter.snapshot()[position]
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
                    binding.shimmerViewHighestRating.isVisible = false
                    highestRatingRecyclerView.isVisible = true
                    highestRatingResponse.body()?.products?.let { updateHighestRatingAdapter(it) }
                } else {
                    binding.shimmerViewHighestRating.isVisible = false
                    binding.rvHighestRating.isVisible = true
                    logError(highestRatingResponse)
                }

                val newProductResponse = ApiConfig.getApiService(token).getFilteredProducts("Terbaru", 1, 10)
                if (newProductResponse.isSuccessful) {
                    binding.shimmerViewNewProduct.isVisible = false
                    newProductRecyclerView.isVisible = true
                    newProductResponse.body()?.products?.let { updateNewProductAdapter(it) }
                } else {
                    binding.shimmerViewHighestRating.isVisible = false
                    binding.rvHighestRating.isVisible = true
                    logError(newProductResponse)
                }
            } catch (e: Exception) {
                Log.e("ForYouFragment", "Terjadi kesalahan: ${e.message}", e)
            }
        }
    }

    private fun observeRecommendationProducts() {
        recommendationProductAdapter.addLoadStateListener {
            binding.shimmerViewRecommendation.isVisible = it.source.refresh is LoadState.Loading
            recommendationProductRecyclerView.isVisible = it.source.refresh is LoadState.NotLoading && recommendationProductAdapter.itemCount > 0
        }

        viewLifecycleOwner.lifecycleScope.launch {
            try {
                userId = loginSession.getUserId().first().toString()

                recommendationViewModel.recommendationFlow(userId).observe(viewLifecycleOwner) {
                    recommendationProductAdapter.submitData(viewLifecycleOwner.lifecycle, it)
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
