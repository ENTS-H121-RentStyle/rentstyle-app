package com.example.rentstyle.helpers.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.rentstyle.R
import com.example.rentstyle.databinding.FavoriteProductImageItemBinding
import com.example.rentstyle.model.remote.response.FavoriteResponse

class FavoriteProductAdapter (private val favoriteProducts: List<FavoriteResponse>) : RecyclerView.Adapter<FavoriteProductAdapter.FavoriteProductViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FavoriteProductViewHolder {
        val binding = FavoriteProductImageItemBinding.inflate(
            LayoutInflater.from(parent.context), parent, false)
        return FavoriteProductViewHolder(binding)
    }

    override fun onBindViewHolder(holder: FavoriteProductViewHolder, position: Int) {
        val product = favoriteProducts[position]
        holder.bind(product.product?.image!!)
    }

    override fun getItemCount(): Int = favoriteProducts.size

    inner class FavoriteProductViewHolder(private val binding: FavoriteProductImageItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(product: String) {
            binding.apply {
                Glide.with(itemView.context)
                    .load(product)
                    .placeholder(R.drawable.img_placeholder)
                    .into(listItemImage)
            }
        }
    }
}