package com.example.rentstyle.helpers.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.rentstyle.databinding.ProductRatingItemBinding

class ReviewDummyAdapter : RecyclerView.Adapter<ReviewDummyAdapter.ViewHolder>() {
    inner class ViewHolder (binding: ProductRatingItemBinding): RecyclerView.ViewHolder(binding.root) {
        val ratingStarImage = binding.ivReviewRating
        val username = binding.tvUsername
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ProductRatingItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.apply {
            username.text = "username"
            ratingStarImage.ratingScore = 3
        }
    }

    override fun getItemCount(): Int {
        return 2
    }
}