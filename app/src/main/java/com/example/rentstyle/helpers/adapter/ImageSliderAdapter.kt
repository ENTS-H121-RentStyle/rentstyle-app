package com.example.rentstyle.helpers.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.rentstyle.databinding.CarouselImageItemBinding
import com.example.rentstyle.databinding.CarouselProductImageContainerBinding

class ImageSliderAdapter(private val context: Context, private val images: List<Int>, private val type: String) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private val bannerCarousel = 0
    private val productImageCarousel = 1
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val vh = if (viewType == bannerCarousel) {
            BannerViewHolder(CarouselImageItemBinding.inflate(LayoutInflater.from(parent.context), parent, false))
        } else {
            ProductViewHolder(CarouselProductImageContainerBinding.inflate(LayoutInflater.from(parent.context), parent, false))
        }

        return vh
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val imageUrl = images[position]

        if (holder is BannerViewHolder) {
            Glide.with(context)
                .load(imageUrl)
                .into(holder.binding.listItemImage)
        } else if (holder is ProductViewHolder) {
            Glide.with(context)
                .load(imageUrl)
                .into(holder.binding.ivProductImage)
        }
    }

    override fun getItemCount(): Int = 3

    inner class BannerViewHolder(val binding: CarouselImageItemBinding) : RecyclerView.ViewHolder(binding.root)

    inner class ProductViewHolder(val binding: CarouselProductImageContainerBinding) : RecyclerView.ViewHolder(binding.root)

    override fun getItemViewType(position: Int): Int {
        return if (type == "Banner") {
            bannerCarousel
        } else {
            productImageCarousel
        }
    }
}