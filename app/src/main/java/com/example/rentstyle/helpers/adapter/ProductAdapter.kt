package com.example.rentstyle.helpers.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.bumptech.glide.request.RequestOptions
import com.example.rentstyle.R
import com.example.rentstyle.databinding.ProductImageItemBinding
import com.example.rentstyle.model.Product

class ProductAdapter : PagingDataAdapter<Product, ProductAdapter.ProductViewHolder>(ProductComparator) {
    private var onClickListener: OnClickListener? = null

    fun setOnClickListener(onClickListener: OnClickListener) {
        this.onClickListener = onClickListener
    }

    interface OnClickListener {
        fun onClick(position: Int, image: ImageView)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductViewHolder {
        return ProductViewHolder(ProductImageItemBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun onBindViewHolder(holder: ProductViewHolder, position: Int) {
        val product = getItem(position)

        Glide.with(holder.itemView.context)
            .load(product!!.image ?: R.drawable.img_placeholder)
            .apply(RequestOptions().placeholder(R.drawable.img_placeholder).error(R.drawable.img_placeholder))
            .transition(DrawableTransitionOptions.withCrossFade())
            .into(holder.binding.ivProductImage)

        product.let {
            holder.bind(it)
            holder.itemView.setOnClickListener {
                onClickListener?.onClick(position, holder.ivProductImage)
            }
        }
    }

    inner class ProductViewHolder(val binding: ProductImageItemBinding) : RecyclerView.ViewHolder(binding.root) {
        val ivProductImage: ImageView = binding.ivProductImage
        private val tvProductName: TextView = binding.tvProductName
        private val tvProductPrice: TextView = binding.tvProductPrice
        private val tvProductRating: TextView = binding.tvProductRating
        private val tvProductLocation: TextView = binding.tvProductLocation

        @SuppressLint("SetTextI18n")
        fun bind(product: Product) {
            tvProductName.text = product.productName
            tvProductPrice.text = "Rp ${product.rentPrice}"
            tvProductRating.text = product.avgRating.toString()
            tvProductLocation.text = product.city
        }
    }

    object ProductComparator : DiffUtil.ItemCallback<Product>() {
        override fun areItemsTheSame(oldItem: Product, newItem: Product): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Product, newItem: Product): Boolean {
            return oldItem == newItem
        }
    }
}