package com.example.rentstyle.ui.fragment

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.bumptech.glide.request.RequestOptions
import com.example.rentstyle.R
import com.example.rentstyle.databinding.FragmentProductDetailBinding
import com.example.rentstyle.model.database.Favorite
import com.example.rentstyle.model.database.room.AppDatabase
import com.example.rentstyle.model.local.datastore.LoginSession
import com.example.rentstyle.model.local.datastore.dataStore
import com.example.rentstyle.model.remote.request.CartRequest
import com.example.rentstyle.model.remote.request.FavoriteRequest
import com.example.rentstyle.model.remote.response.ProductDetailResponse
import com.example.rentstyle.model.remote.retrofit.ApiConfig
import com.github.ybq.android.spinkit.style.WanderingCubes
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class ProductDetailFragment : Fragment() {
    private var _binding: FragmentProductDetailBinding? = null
    private val binding get() = _binding!!

    private lateinit var loginSession: LoginSession
    private var isFavorite: Boolean = false
    private var favoriteId: String? = null
    private lateinit var userId: String
    private lateinit var database: AppDatabase
    private lateinit var productDescription: TextView
    private lateinit var btnViewMore: TextView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProductDetailBinding.inflate(inflater, container, false)
        database = AppDatabase.getDatabase(requireContext())

        binding.mainToolbar.tvToolbarTitle.text = getString(R.string.title_product_detail)
        binding.mainToolbar.ivBackButton.setOnClickListener {
            findNavController().navigateUp()
        }

        binding.apply {
            productDescription = tvProductDescription
            btnViewMore = btnViewMoreDescription
            ivLoadingSpinner.setIndeterminateDrawable(WanderingCubes())
        }

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

        binding.btnViewMoreDescription.setOnClickListener {
            if (productDescription.maxLines == 2) {
                productDescription.maxLines = 999
                btnViewMore.text = getString(R.string.txt_view_less)
            } else {
                productDescription.maxLines = 2
                btnViewMore.text = getString(R.string.txt_view_more)
            }
        }

        fetchProductDetail()

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
                            Toast.makeText(requireContext(), "Failed to remove from favorite", Toast.LENGTH_SHORT).show()
                        }
                    } ?: run {
                        Toast.makeText(requireContext(), "Failed to remove from favorite", Toast.LENGTH_SHORT).show()
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
                        Toast.makeText(requireContext(), "Failed to remove from favorite", Toast.LENGTH_SHORT).show()
                    }
                }
            } catch (e: Exception) {
                Toast.makeText(requireContext(), "Failed to add to favorite", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun fetchProductDetail() {
        val productId = arguments?.getString("productId") ?: return

        viewLifecycleOwner.lifecycleScope.launch {
            try {
                val token = loginSession.getSessionToken().first() ?: ""
                userId = loginSession.getUserId().first() ?: ""
                val apiService = ApiConfig.getApiService(token)
                val response = apiService.getProductDetail(productId)
                if (response.isSuccessful) {
                    binding.loadingDetail.isVisible = false
                    val product = response.body()
                    if (product != null) {
                        bindProductData(product)
                        checkFavorite(productId)
                    } else {
                        Toast.makeText(requireContext(), "Failed to get product detail", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    binding.loadingDetail.isVisible = false
                    Toast.makeText(requireContext(), "Failed to load product detail", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                Toast.makeText(requireContext(), e.toString(), Toast.LENGTH_SHORT).show()
            }
            binding.loadingDetail.isVisible = false
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
            tvProductPrice.text = getString(R.string.txt_per_day, product.rentPrice.toString())
            tvProductRating.text = "Rating: %.1f".format(product.reviews.map { it.rating }.average())
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
                        duration = 1,
                        product_id = productId,
                        user_id = userId
                    )
                )
                if (response.isSuccessful) {
                    val cartResponse = response.body()
                    if (cartResponse != null) {
                        Toast.makeText(requireContext(), "Added to cart successfully", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Toast.makeText(requireContext(), "Failed to add to cart", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                Toast.makeText(requireContext(), "Failed to add to cart", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
