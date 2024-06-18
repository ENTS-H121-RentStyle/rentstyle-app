package com.example.rentstyle.helpers.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.rentstyle.databinding.ProductImageItemBinding

class ProductSkeletonAdapter(val size: Int) : RecyclerView.Adapter<ProductSkeletonAdapter.ViewHolder>() {
    inner class ViewHolder (binding: ProductImageItemBinding): RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ProductImageItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {}
}