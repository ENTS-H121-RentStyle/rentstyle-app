// ReviewDummyAdapter.kt
package com.example.rentstyle.helpers.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.rentstyle.databinding.ProductRatingItemBinding
import com.example.rentstyle.model.remote.response.Review

class ReviewDummyAdapter : RecyclerView.Adapter<ReviewDummyAdapter.ViewHolder>() {

    private var reviews: List<Review> = emptyList()

    inner class ViewHolder(private val binding: ProductRatingItemBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(review: Review) {
            binding.tvUsername.text = review.userId
            binding.ivReviewRating.ratingScore = review.rating.toInt()
            binding.tvReviewDescription.text = review.review
            // Set image dan data lain yang sesuai
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ProductRatingItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(reviews[position])
    }

    override fun getItemCount(): Int = reviews.size

    @SuppressLint("NotifyDataSetChanged")
    fun updateReviews(newReviews: List<Review>) {
        reviews = newReviews
        notifyDataSetChanged()
    }
}
