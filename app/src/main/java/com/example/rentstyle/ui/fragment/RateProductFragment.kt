package com.example.rentstyle.ui.fragment

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.RatingBar
import android.widget.Toast
import androidx.core.net.toUri
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.Glide
import com.example.rentstyle.R
import com.example.rentstyle.databinding.FragmentRateProductBinding
import com.example.rentstyle.helpers.CacheImageManager
import com.example.rentstyle.helpers.ImageFileHelper
import com.example.rentstyle.helpers.ImageFileHelper.reduceFileImage
import com.example.rentstyle.helpers.StatusResult
import com.example.rentstyle.model.local.datastore.LoginSession
import com.example.rentstyle.model.local.datastore.dataStore
import com.example.rentstyle.model.remote.retrofit.ApiConfig
import com.example.rentstyle.viewmodel.OrderViewModel
import com.example.rentstyle.viewmodel.ProductViewModelFactory
import com.github.ybq.android.spinkit.style.WanderingCubes
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody

class RateProductFragment : Fragment() {
    private lateinit var _binding: FragmentRateProductBinding
    private val binding get() = _binding

    private val orderViewModel: OrderViewModel by activityViewModels {
        ProductViewModelFactory.getInstance(this.requireActivity().application)
    }

    private val args: RateProductFragmentArgs by navArgs()

    private lateinit var pref: LoginSession

    private var rating = "0"
    private var sellerId = ""
    private var orderId = ""
    private var productId = ""
    private var userId = ""

    private var run = true

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentRateProductBinding.inflate(inflater, container, false)

        binding.mainToolbar.tvToolbarTitle.text = getString(R.string.title_rate_product)
        binding.mainToolbar.ivBackButton.setOnClickListener {
            findNavController().navigateUp()
        }

        if (args.uri != null) {
            Glide.with(requireContext())
                .load(args.uri)
                .into(binding.ivReviewImage)
        }

        pref = LoginSession.getInstance(requireActivity().application.dataStore)

        checkProductId()

        binding.apply {
            ivReviewImage.setOnClickListener {
                findNavController().navigate(RateProductFragmentDirections.actionNavigationRatingToNavigationCamera("rating"))
            }

            ratingBarReview.onRatingBarChangeListener = RatingBar.OnRatingBarChangeListener { _, star, _ ->
                rating = star.toInt().toString()
            }
        }

        return binding.root
    }

    private fun checkProductId() {
        if (args.productId != null && args.orderId != null) {
            productId = args.productId!!
            orderId = args.orderId!!

            orderViewModel.productId.value = args.productId!!
            orderViewModel.orderId.value = args.orderId!!
        } else {
            productId = orderViewModel.productId.value!!
            orderId = orderViewModel.orderId.value!!
        }

        if (productId != "") {
            binding.ivLoadingSpinner.apply {
                isVisible = true
                setIndeterminateDrawable(WanderingCubes())
            }

            viewLifecycleOwner.lifecycleScope.launch {
                try {
                    val token = pref.getSessionToken().first().toString()
                    userId = pref.getUserId().first().toString()

                    val apiService = ApiConfig.getApiService(token)
                    val response = apiService.getProductDetail(productId)
                    if (response.isSuccessful) {
                        val product = response.body()
                        if (product != null) {
                            sellerId = product.sellerId
                            postReview()
                        } else {
                            Toast.makeText(requireContext(), getString(R.string.txt_try_again), Toast.LENGTH_SHORT).show()
                        }
                    } else {
                        binding.ivLoadingSpinner.isVisible = false
                        Toast.makeText(requireContext(), getString(R.string.txt_try_again), Toast.LENGTH_SHORT).show()
                    }
                } catch (e: Exception) {
                    Toast.makeText(requireContext(), e.toString(), Toast.LENGTH_SHORT).show()
                }
                binding.ivLoadingSpinner.isVisible = false
            }
        }
    }

    private fun postReview() {
        binding.btnSubmit.setOnClickListener {
            binding.ivLoadingSpinner.apply {
                isVisible = true
                setIndeterminateDrawable(WanderingCubes())
            }

            val reviewMessage = binding.edReviewDetail.text.toString()

            if (args.uri != null && rating != "0" && reviewMessage.isNotEmpty()
                && sellerId != "" && orderId != "" && productId != "" && userId != "") {

                val bodyOrderId = orderId.toRequestBody("text/plain".toMediaType())
                val bodyProductId = productId.toRequestBody("text/plain".toMediaType())
                val bodyUserId = userId.toRequestBody("text/plain".toMediaType())
                val bodySellerId = sellerId.toRequestBody("text/plain".toMediaType())
                val bodyRating = rating.toRequestBody("text/plain".toMediaType())
                val bodyReview = reviewMessage.toRequestBody("text/plain".toMediaType())

                val imageFile = ImageFileHelper.uriToFile(args.uri!!.toUri(), requireContext()).reduceFileImage()
                val requestImageFile = imageFile.asRequestBody("image/jpg".toMediaType())
                val multipartBody = MultipartBody.Part.createFormData(
                    "image",
                    imageFile.name,
                    requestImageFile
                )

                viewLifecycleOwner.lifecycleScope.launch {
                    orderViewModel.makeReview(bodyOrderId, bodyProductId, bodyUserId,
                        bodySellerId, bodyRating, bodyReview, multipartBody).observe(viewLifecycleOwner) { result ->
                        if (result != null) {
                            when (result) {
                                is StatusResult.Loading -> {}

                                is StatusResult.Success -> {
                                    binding.ivLoadingSpinner.isVisible = false
                                    Toast.makeText(requireContext(), result.success, Toast.LENGTH_SHORT).show()

                                    if (run) {
                                        CacheImageManager.clearTempImages(requireContext())
                                        Glide.with(requireContext())
                                            .clear(binding.ivReviewImage)
                                        findNavController().navigate(RateProductFragmentDirections.actionNavigationRatingToNavigationProfile())
                                    }
                                }

                                is StatusResult.Error -> {
                                    binding.ivLoadingSpinner.isVisible = false
                                    Toast.makeText(requireContext(), result.error, Toast.LENGTH_SHORT).show()
                                    Log.d("error", result.error)
                                }
                            }
                        } else {
                            binding.ivLoadingSpinner.isVisible = false
                            Toast.makeText(requireContext(), getString(R.string.txt_error_give_review), Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            } else {
                binding.ivLoadingSpinner.isVisible = false
                Toast.makeText(requireContext(), getString(R.string.empty_field), Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onResume() {
        super.onResume()

        requireActivity().window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN)
    }

    override fun onPause() {
        super.onPause()

        requireActivity().window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_NOTHING)
    }
}