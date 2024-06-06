package com.example.rentstyle.helpers.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.rentstyle.databinding.CarouselImageItemBinding

class ImageSliderAdapter(private val context: Context, private val images: List<Int>) : RecyclerView.Adapter<ImageSliderAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = CarouselImageItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val imageUrl = images[position]
        Glide.with(context)
            .load(imageUrl)
            .into(holder.binding.listItemImage)
    }

    override fun getItemCount(): Int = 3

    inner class ViewHolder(val binding: CarouselImageItemBinding) : RecyclerView.ViewHolder(binding.root)
}