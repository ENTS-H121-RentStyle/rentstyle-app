package com.example.rentstyle.ui.fragment

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.bumptech.glide.request.RequestOptions
import com.example.rentstyle.R
import com.example.rentstyle.databinding.FragmentProductDetailBinding
import com.example.rentstyle.helpers.GridSpacingItemDecoration
import com.example.rentstyle.helpers.adapter.ProductAdapter
import com.example.rentstyle.model.database.Favorite
import com.example.rentstyle.model.database.room.AppDatabase
import com.example.rentstyle.model.remote.retrofit.ApiConfig
import com.example.rentstyle.model.local.datastore.LoginSession
import com.example.rentstyle.model.local.datastore.dataStore
import com.example.rentstyle.model.remote.request.CartRequest
import com.example.rentstyle.model.remote.request.FavoriteRequest
import com.example.rentstyle.model.remote.response.ProductDetailResponse
import com.example.rentstyle.model.repository.RecommendationRepository
import com.example.rentstyle.ui.viewmodel.RecommendationViewModel
import com.example.rentstyle.ui.viewmodel.RecommendationViewModelFactory
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class ProductDetailFragment : Fragment() {
    private var _binding: FragmentProductDetailBinding? = null
    private val binding get() = _binding!!

    private lateinit var recommendationAdapter: ProductAdapter
    private lateinit var recommendationViewModel: RecommendationViewModel
    private lateinit var loginSession: LoginSession
    private var isFavorite: Boolean = false
    private var favoriteId: String? = null
    private lateinit var userId: String
    private lateinit var database: AppDatabase

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProductDetailBinding.inflate(inflater, container, false)
        recommendationAdapter = ProductAdapter()
        database = AppDatabase.getDatabase(requireContext())

        binding.mainToolbar.tvToolbarTitle.text = "Product Detail"
        binding.mainToolbar.ivBackButton.setOnClickListener {
            findNavController().navigateUp()
        }

        binding.apply {
            rvRecommendation.adapter = recommendationAdapter
            rvRecommendation.layoutManager = GridLayoutManager(context, 2)
            rvRecommendation.addItemDecoration(GridSpacingItemDecoration(2, 25, true))
        }

        recommendationAdapter.setOnClickListener(object : ProductAdapter.OnClickListener {
            override fun onClick(position: Int, image: ImageView) {
                val product = recommendationAdapter.snapshot().get(position)
                product?.let { navigateToProductDetail(it.id) }
            }
        })

        loginSession = LoginSession.getInstance(requireContext().dataStore)

        binding.ivFavButton.setOnClickListener {
            val productId = arguments?.getString("productId")
            productId?.let {
                toggleFavorite(it)
            }
        }
        binding.btnCart.setOnClickListener {
            val productId = arguments?.getString("productId")
            productId?.let {
                addToCart(it)
            }
        }

        fetchProductDetail()
        observeRecommendationProducts()

        return binding.root
    }

    private fun toggleFavorite(productId: String) {
        viewLifecycleOwner.lifecycleScope.launch {
            try {
                val token = loginSession.getSessionToken().first() ?: ""
                userId = loginSession.getUserId().first() ?: ""
                val apiService = ApiConfig.getApiService(token)

                if (isFavorite) {
                    favoriteId?.let { favId ->
                        val response = apiService.deleteFavorite(favId)
                        if (response.isSuccessful) {
                            database.favoriteDao().deleteFavorite(Favorite(productId, userId))
                            isFavorite = false
                            favoriteId = null
                            binding.ivFavButton.setImageResource(R.drawable.ic_fav)
                        } else {
                            Log.e("ProductDetailFragment", "Failed to remove favorite: ${response.code()} - ${response.errorBody()?.string()}")
                        }
                    } ?: run {
                        Log.e("ProductDetailFragment", "favoriteId is null, cannot remove favorite")
                    }
                } else {
                    val favoriteRequest = FavoriteRequest(product_id = productId, user_id = userId)
                    val response = apiService.addFavorite(favoriteRequest)
                    if (response.isSuccessful) {
                        response.body()?.id?.let { favId ->
                            database.favoriteDao().insertFavorite(Favorite(productId, userId))
                            isFavorite = true
                            favoriteId = favId
                            binding.ivFavButton.setImageResource(R.drawable.ic_fav_2)
                        }
                    } else {
                        Log.e("ProductDetailFragment", "Failed to add favorite: ${response.code()} - ${response.errorBody()?.string()}")
                    }
                }
            } catch (e: Exception) {
                Log.e("ProductDetailFragment", "Failed to toggle favorite: ${e.message}", e)
            }
        }
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

        viewLifecycleOwner.lifecycleScope.launch {
            try {
                val token = loginSession.getSessionToken().first() ?: ""
                userId = loginSession.getUserId().first() ?: ""
                val apiService = ApiConfig.getApiService(token)
                val response = apiService.getProductDetail(productId)
                if (response.isSuccessful) {
                    val product = response.body()
                    if (product != null) {
                        bindProductData(product)
                        checkFavorite(productId)
                    } else {
                        Log.e("ProductDetailFragment", "Empty product detail response")
                    }
                } else {
                    Log.e(
                        "ProductDetailFragment",
                        "Failed to load product detail: ${response.code()}"
                    )
                }
            } catch (e: Exception) {
                Log.e("ProductDetailFragment", "Failed to load product detail: ${e.message}", e)
            }
        }
    }

    private fun observeRecommendationProducts() {
        viewLifecycleOwner.lifecycleScope.launch {
            try {
                val token = loginSession.getSessionToken().first() ?: ""
                userId = loginSession.getUserId().first() ?: ""
                val repository = RecommendationRepository(ApiConfig.getApiService(token))
                val viewModelFactory = RecommendationViewModelFactory(repository)
                recommendationViewModel =
                    ViewModelProvider(this@ProductDetailFragment, viewModelFactory)
                        .get(RecommendationViewModel::class.java)

                recommendationViewModel.getRecommendationProducts(userId)
                    .collectLatest { pagingData ->
                        recommendationAdapter.submitData(pagingData)
                    }
            } catch (e: Exception) {
                Log.e(
                    "ProductDetailFragment",
                    "Failed to observe recommendation products: ${e.message}"
                )
            }
        }
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
        }
    }

    private fun checkFavorite(productId: String) {
        viewLifecycleOwner.lifecycleScope.launch {
            try {
                val favorite = database.favoriteDao().getFavorite(productId)
                if (favorite != null) {
                    isFavorite = true
                    favoriteId = favorite.productId
                    binding.ivFavButton.setImageResource(R.drawable.ic_fav_2)
                } else {
                    val token = loginSession.getSessionToken().first() ?: ""
                    val apiService = ApiConfig.getApiService(token)
                    val response = apiService.getFavorites(userId, token)
                    if (response.isSuccessful) {
                        val favorites = response.body() ?: emptyList()
                        val serverFavorite = favorites.find { it.product_id == productId }
                        if (serverFavorite != null) {
                            isFavorite = true
                            favoriteId = serverFavorite.product_id
                            binding.ivFavButton.setImageResource(R.drawable.ic_fav_2)
                        } else {
                            isFavorite = false
                            favoriteId = null
                            binding.ivFavButton.setImageResource(R.drawable.ic_fav)
                        }
                    } else {
                        Log.e(
                            "ProductDetailFragment",
                            "Failed to check favorites: ${response.code()}"
                        )
                    }
                }
            } catch (e: Exception) {
                Log.e("ProductDetailFragment", "Failed to check favorites: ${e.message}", e)
            }
        }
    }

    private fun addToCart(productId: String) {
        viewLifecycleOwner.lifecycleScope.launch {
            try {
                val token = loginSession.getSessionToken().first() ?: ""
                val apiService = ApiConfig.getApiService(token)
                val response = apiService.addToCart(
                    CartRequest(
                        duration = 3,
                        product_id = productId,
                        user_id = userId
                    )
                )
                if (response.isSuccessful) {
                    val cartResponse = response.body()
                    if (cartResponse != null) {
                        Log.d("ProductDetailFragment", "Added to cart successfully")
                    } else {
                        Log.e("ProductDetailFragment", "Empty response body")
                    }
                } else {
                    Log.e("ProductDetailFragment", "Failed to add to cart: ${response.code()}")
                }
            } catch (e: Exception) {
                Log.e("ProductDetailFragment", "Failed to add to cart: ${e.message}", e)
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
