// ReviewDummyAdapter.kt
package com.example.rentstyle.helpers.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.rentstyle.databinding.ProductRatingItemBinding
import com.example.rentstyle.model.remote.response.Review

class ReviewDummyAdapter(private val reviewList: List<Review>) : RecyclerView.Adapter<ReviewDummyAdapter.ViewHolder>() {

    inner class ViewHolder(private val binding: ProductRatingItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(review: Review) {
            binding.tvUsername.text = review.userId
            binding.ivReviewRating.ratingScore = review.rating
            binding.tvReviewDescription.text = review.review
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding =
            ProductRatingItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val currentReview = reviewList[position]
    }

    override fun getItemCount(): Int = reviewList.size
}