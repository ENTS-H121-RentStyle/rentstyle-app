package com.example.rentstyle.helpers.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.rentstyle.R
import com.example.rentstyle.databinding.FavoriteProductImageItemBinding
import com.example.rentstyle.model.Product

class FavoriteProductAdapter : RecyclerView.Adapter<FavoriteProductAdapter.FavoriteProductViewHolder>() {

    private var favoriteProducts = listOf<Product>()

    @SuppressLint("NotifyDataSetChanged")
    fun submitList(products: List<Product>) {
        favoriteProducts = products
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FavoriteProductViewHolder {
        val binding = FavoriteProductImageItemBinding.inflate(
            LayoutInflater.from(parent.context), parent, false)
        return FavoriteProductViewHolder(binding)
    }

    override fun onBindViewHolder(holder: FavoriteProductViewHolder, position: Int) {
        val product = favoriteProducts[position]
        holder.bind(product)
    }

    override fun getItemCount(): Int = favoriteProducts.size

    inner class FavoriteProductViewHolder(private val binding: FavoriteProductImageItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(product: Product) {
            binding.apply {
                Glide.with(itemView.context)
                    .load(product.image)
                    .placeholder(R.drawable.img_placeholder)
                    .into(listItemImage)
            }
        }
    }
}
