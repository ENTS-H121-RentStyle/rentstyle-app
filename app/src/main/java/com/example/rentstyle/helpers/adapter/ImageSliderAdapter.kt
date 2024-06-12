package com.example.rentstyle.helpers.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.rentstyle.databinding.CarouselImageItemBinding

class ImageSliderAdapter(private val context: Context, private val images: List<Int>, private val type: String) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private val bannerCarousel = 0
    private val productImageCarousel = 1
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return BannerViewHolder(
            CarouselImageItemBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val imageUrl = images[position]

        if (holder is BannerViewHolder) {
            Glide.with(context)
                .load(imageUrl)
                .into(holder.binding.listItemImage)
        }
    }

    override fun getItemCount(): Int = 3

    inner class BannerViewHolder(val binding: CarouselImageItemBinding) : RecyclerView.ViewHolder(binding.root)

    override fun getItemViewType(position: Int): Int {
        return if (type == "Banner") {
            bannerCarousel
        } else {
            productImageCarousel
        }
    }
}