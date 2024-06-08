package com.example.rentstyle.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.example.rentstyle.R
import com.example.rentstyle.databinding.FragmentProductDetailBinding
import com.example.rentstyle.helpers.GridSpacingItemDecoration
import com.example.rentstyle.helpers.adapter.ImageSliderAdapter
import com.example.rentstyle.helpers.adapter.RecyclerDummyAdapter
import com.example.rentstyle.helpers.adapter.ReviewDummyAdapter

class ProductDetailFragment : Fragment() {
    private lateinit var _binding: FragmentProductDetailBinding
    private val binding get() = _binding

    private lateinit var carousel: ViewPager2
    private lateinit var carouselAdapter : ImageSliderAdapter
    private lateinit var productName: TextView
    private lateinit var productPrice: TextView
    private lateinit var productRating: TextView
    private lateinit var groupShop: ConstraintLayout
    private lateinit var productShopName: TextView
    private lateinit var productShopLocation: TextView
    private lateinit var productCategory: TextView
    private lateinit var productColor: TextView
    private lateinit var productSize: TextView
    private lateinit var productDescription: TextView
    private lateinit var btnViewMore: TextView
    private lateinit var productReviewScore: TextView

    private lateinit var rvProductReview: RecyclerView
    private lateinit var reviewAdapter: ReviewDummyAdapter
    private lateinit var rvProductRecommendation: RecyclerView
    private lateinit var productAdapter: RecyclerDummyAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProductDetailBinding.inflate(inflater, container, false)

        binding.apply {
            carousel = vpProductImageCarousel

            productName = tvProductName
            productPrice = tvProductPrice
            productRating = tvProductRating
            groupShop = groupProductShop
            productShopName = tvProductShopName
            productShopLocation = tvShopLocation
            productCategory = tvProductCategory
            productColor = tvProductColor
            productSize = tvProductSize
            productDescription = tvProductDescription
            btnViewMore = btnViewMoreDescription
            productReviewScore = tvProductRatingScore
            rvProductReview = rvProductRating
            rvProductRecommendation = rvRecommendation
        }

        binding.mainToolbar.tvToolbarTitle.text = "Product Detail"
        binding.mainToolbar.ivBackButton.setOnClickListener {
            findNavController().navigateUp()
        }

        setUpImageCarousel()

        productName.text = "Kostum Honkai: Star Rail Black Swan Cosplay Black Swan Costume Black Swan Kostum Full Set"
        productPrice.text = "590.500 / 2 hari"
        productRating.text = "Rating 4.5"
        productShopName.text = "Toko Maju Jaya"
        productShopLocation.text = "Jakarta"
        productCategory.text = "Baju cosplay"
        productColor.text = "Biru"
        productSize.text = "XL"
        productDescription.text = "Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat. Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur. Excepteur sint occaecat cupidatat non proident, sunt in culpa qui officia deserunt mollit anim id est laborum."
        productReviewScore.text = "Rating 4.5"

        btnViewMore.setOnClickListener {
            if (productDescription.maxLines == 2) {
                productDescription.maxLines = 999
                btnViewMore.text = "View less"
            } else {
                productDescription.maxLines = 2
                btnViewMore.text = "View more"
            }
        }

        reviewAdapter = ReviewDummyAdapter()
        productAdapter = RecyclerDummyAdapter()

        rvProductReview.adapter = reviewAdapter

        rvProductRecommendation.addItemDecoration(GridSpacingItemDecoration(2,25,true))
        rvProductRecommendation.adapter = productAdapter

        productAdapter.setOnClickListener(object : RecyclerDummyAdapter.OnClickListener {
            override fun onClick(position: Int, image: ImageView) {
                findNavController().navigate(ProductDetailFragmentDirections.actionNavigationProductDetailSelf())
            }
        })

        groupShop.setOnClickListener {
            findNavController().navigate(ProductDetailFragmentDirections.actionNavigationProductDetailToNavigationShopDetail())
        }

        return binding.root
    }

    private fun setUpImageCarousel() {
        val imageList = arrayListOf(R.drawable.img_placeholder, R.drawable.img_placeholder, R.drawable.img_placeholder)
        carouselAdapter = ImageSliderAdapter(requireContext(), imageList, "Product")

        carousel.apply {
            adapter = carouselAdapter
            clipToPadding = false
            clipChildren = false
            offscreenPageLimit = 3
            getChildAt(0).overScrollMode = RecyclerView.OVER_SCROLL_NEVER
        }
    }
}