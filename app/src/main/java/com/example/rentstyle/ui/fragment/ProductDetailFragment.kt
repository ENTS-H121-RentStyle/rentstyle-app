package com.example.rentstyle.ui.fragment

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.bumptech.glide.request.RequestOptions
import com.example.rentstyle.R
import com.example.rentstyle.databinding.FragmentProductDetailBinding
import com.example.rentstyle.helpers.DataResult
import com.example.rentstyle.model.database.room.AppDatabase
import com.example.rentstyle.model.local.datastore.LoginSession
import com.example.rentstyle.model.local.datastore.dataStore
import com.example.rentstyle.model.remote.request.CartRequest
import com.example.rentstyle.model.remote.request.FavoriteRequest
import com.example.rentstyle.model.remote.response.ProductDetailResponse
import com.example.rentstyle.model.remote.retrofit.ApiConfig
import com.example.rentstyle.viewmodel.UserViewModel
import com.example.rentstyle.viewmodel.UserViewModelFactory
import com.github.ybq.android.spinkit.style.WanderingCubes
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class ProductDetailFragment : Fragment() {
    private var _binding: FragmentProductDetailBinding? = null
    private val binding get() = _binding!!

    private val userViewModel: UserViewModel by activityViewModels {
        UserViewModelFactory.getInstance(this.requireActivity().application)
    }

    private lateinit var loginSession: LoginSession
    private var isFavorite: Boolean = false
    private var favoriteId: String? = null
    private lateinit var userId: String
    private lateinit var database: AppDatabase
    private lateinit var productDescription: TextView
    private lateinit var btnViewMore: TextView

    private var productId: String = ""
    private var productName: String = ""
    private var rentPrice: Int = 0
    private var rentDuration: Int = 0
    private var productImage: String = ""
    private var sellerId: String = ""

    private var run = true

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
        binding.mainToolbar.ivIcon.apply {
            setImageResource(R.drawable.ic_cart)
            setOnClickListener {
                findNavController().navigate(ProductDetailFragmentDirections.actionNavigationProductDetailToNavigationShoppingCart())
            }
        }

        binding.apply {
            productDescription = tvProductDescription
            btnViewMore = btnViewMoreDescription
            ivLoadingSpinner.setIndeterminateDrawable(WanderingCubes())
        }

        loginSession = LoginSession.getInstance(requireActivity().application.dataStore)

        binding.btnCart.setOnClickListener {
            binding.ivLoadingSpinner2.apply {
                isVisible = true
                setIndeterminateDrawable(WanderingCubes())
            }
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
        binding.tvViewShop.setOnClickListener {
            if (sellerId.isNotEmpty()) {
                findNavController().navigate(
                    ProductDetailFragmentDirections.actionNavigationProductDetailToNavigationShopDetail(sellerId)
                )
            } else {
                Toast.makeText(requireContext(), "Seller ID not found", Toast.LENGTH_SHORT).show()
            }
        }

        fetchProductDetail()

        return binding.root
    }

    private fun checkOutListener() {
        binding.btnRent.setOnClickListener {
            binding.ivLoadingSpinner2.apply {
                isVisible = true
                setIndeterminateDrawable(WanderingCubes())
            }

            viewLifecycleOwner.lifecycleScope.launch {
                userViewModel.getUserProfile().observe(viewLifecycleOwner) { result ->
                    if (result != null) {
                        when (result) {
                            is DataResult.Loading -> { }

                            is DataResult.Success -> {
                                val data = result.data

                                if (run) {
                                    run = false
                                    if (data.address != null && data.phone != null) {
                                        findNavController().navigate(ProductDetailFragmentDirections.actionNavigationProductDetailToNavigationCheckOut(productId, rentDuration, rentPrice, productName, productImage))
                                    } else {
                                        Toast.makeText(requireContext(), "Please complete your profile", Toast.LENGTH_SHORT).show()
                                        findNavController().navigate(ProductDetailFragmentDirections.actionNavigationProductDetailToNavigationEditUserProfile())
                                    }
                                    binding.ivLoadingSpinner2.isVisible = false
                                }
                            }

                            is DataResult.Error -> {
                                binding.ivLoadingSpinner2.isVisible = false
                                Toast.makeText(requireContext(), getString(R.string.error_toast, result.error), Toast.LENGTH_SHORT).show()
                            }
                        }
                    }
                }
            }
        }
    }

    private fun toggleFavorite(productId: String) {
        binding.ivLoadingSpinner2.apply {
            isVisible = true
            setIndeterminateDrawable(WanderingCubes())
        }
        viewLifecycleOwner.lifecycleScope.launch {
            try {
                val token = loginSession.getSessionToken().first().toString()
                userId = loginSession.getUserId().first().toString()
                val apiService = ApiConfig.getApiService(token)

                if (isFavorite) {
                    favoriteId?.let { favId ->
                        val response = apiService.deleteFavorite(favId)
                        if (response.isSuccessful) {
                            isFavorite = false
                            favoriteId = null
                            binding.ivFavButton.setImageResource(R.drawable.ic_fav)
                            Toast.makeText(requireContext(), "Success removed from favorite", Toast.LENGTH_SHORT).show()
                        } else {
                            Toast.makeText(requireContext(), "Failed to remove from favorite", Toast.LENGTH_SHORT).show()
                        }
                        binding.ivLoadingSpinner2.isVisible = false
                    } ?: run {
                        Toast.makeText(requireContext(), "Failed to remove from favorite", Toast.LENGTH_SHORT).show()
                    }
                    binding.ivLoadingSpinner2.isVisible = false
                } else {
                    val favoriteRequest = FavoriteRequest(product_id = productId, user_id = userId)
                    val response = apiService.addFavorite(favoriteRequest)
                    if (response.code() == 201) {
                        response.body()?.id?.let { favId ->
                            isFavorite = true
                            favoriteId = favId
                            binding.ivFavButton.setImageResource(R.drawable.ic_fav_2)
                            Toast.makeText(requireContext(), "Success add to favorite", Toast.LENGTH_SHORT).show()
                        }
                    } else {
                        Toast.makeText(requireContext(), "Failed to add to favorite", Toast.LENGTH_SHORT).show()
                    }
                    binding.ivLoadingSpinner2.isVisible = false
                }
            } catch (e: Exception) {
                Toast.makeText(requireContext(), "Failed to add to favorite", Toast.LENGTH_SHORT).show()
            }
            binding.ivLoadingSpinner2.isVisible = false
        }
    }

    private fun fetchProductDetail() {
        val productId = arguments?.getString("productId") ?: return

        viewLifecycleOwner.lifecycleScope.launch {
            try {
                val token = loginSession.getSessionToken().first().toString()
                userId = loginSession.getUserId().first().toString()

                val apiService = ApiConfig.getApiService(token)
                val response = apiService.getProductDetail(productId)
                if (response.isSuccessful) {
                    val product = response.body()
                    if (product != null) {
                        bindProductData(product)
                        checkFavorite(productId, userId)
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

            productId = product.id
            productName = product.productName
            rentPrice = product.rentPrice
            rentDuration = 1
            productImage = product.image.toString()
            sellerId = product.sellerId
            checkOutListener()
        }
    }

    private fun checkFavorite(productId: String, userId: String) {
        viewLifecycleOwner.lifecycleScope.launch {
            try {
                val token = loginSession.getSessionToken().first().toString()

                val apiService = ApiConfig.getApiService(token)
                val response = apiService.getFavorites(userId)
                if (response.code() == 200) {
                    if (response.body()?.isNotEmpty() == true) {
                        response.body()?.map {
                            if (it.product?.productId == productId) {
                                isFavorite = true
                                favoriteId = it.id
                                binding.ivFavButton.setImageResource(R.drawable.ic_fav_2)
                            }
                        }
                    } else {
                        isFavorite = false
                        binding.ivFavButton.setImageResource(R.drawable.ic_fav)
                    }
                } else {
                    isFavorite = false
                    binding.ivFavButton.setImageResource(R.drawable.ic_fav)
                }

                binding.loadingDetail.isVisible = false

                binding.ivFavButton.setOnClickListener {
                    val productsId = arguments?.getString("productId")
                    productsId?.let {
                        toggleFavorite(productId)
                    }
                }
            } catch (_: Exception) { }
        }
    }

    private fun addToCart(productId: String) {
        viewLifecycleOwner.lifecycleScope.launch {
            try {
                val token = loginSession.getSessionToken().first().toString()
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
                binding.ivLoadingSpinner2.isVisible = false
            } catch (e: Exception) {
                Toast.makeText(requireContext(), "Failed to add to cart", Toast.LENGTH_SHORT).show()
            }
            binding.ivLoadingSpinner2.isVisible = false
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
