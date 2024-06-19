package com.example.rentstyle.helpers.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.rentstyle.R
import com.example.rentstyle.databinding.ProductRatingItemBinding
import com.example.rentstyle.model.remote.response.Review

class ProductRatingAdapter(
    private var reviews: List<Review>,
    private val productImageUrl: String
) : RecyclerView.Adapter<ProductRatingAdapter.ReviewViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ReviewViewHolder {
        val binding = ProductRatingItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ReviewViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ReviewViewHolder, position: Int) {
        holder.bind(reviews[position], productImageUrl)
    }

    override fun getItemCount(): Int = reviews.size

    @SuppressLint("NotifyDataSetChanged")
    fun updateReviews(newReviews: List<Review>) {
        reviews = newReviews.take(3)
        notifyDataSetChanged()
    }

    class ReviewViewHolder(private val binding: ProductRatingItemBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(review: Review, productImageUrl: String) {
            binding.apply {
                tvUsername.text = review.user.name
                tvReviewDescription.text = review.review
                ivReviewRating.ratingScore = review.rating
                Glide.with(ivReviewPhoto.context)
                    .load(productImageUrl)
                    .placeholder(R.drawable.img_placeholder)
                    .into(ivReviewPhoto)
            }
        }
    }
}