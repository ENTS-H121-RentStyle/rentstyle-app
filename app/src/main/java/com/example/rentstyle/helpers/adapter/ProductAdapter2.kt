package com.example.rentstyle.helpers.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.bumptech.glide.request.RequestOptions
import com.example.rentstyle.R
import com.example.rentstyle.databinding.ProductImageItemBinding
import com.example.rentstyle.model.Product

class ProductAdapter2 (private val productList: List<Product>) : RecyclerView.Adapter<ProductAdapter2.ProductViewHolder>() {
    private var onClickListener: OnClickListener? = null

    fun setOnClickListener(onClickListener: OnClickListener) {
        this.onClickListener = onClickListener
    }

    interface OnClickListener {
        fun onClick(product: Product, image: ImageView)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductViewHolder {
        return ProductViewHolder(ProductImageItemBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun onBindViewHolder(holder: ProductViewHolder, position: Int) {
        val product = productList[position]

        Glide.with(holder.itemView.context)
            .load(product.image ?: R.drawable.img_placeholder)
            .apply(RequestOptions().placeholder(R.drawable.img_placeholder).error(R.drawable.img_placeholder))
            .transition(DrawableTransitionOptions.withCrossFade())
            .into(holder.binding.ivProductImage)

        holder.bind(product)
        holder.itemView.setOnClickListener {
            onClickListener?.onClick(product, holder.ivProductImage)
        }
    }

    override fun getItemCount(): Int = productList.size

    inner class ProductViewHolder(val binding: ProductImageItemBinding) : RecyclerView.ViewHolder(binding.root) {
        val ivProductImage: ImageView = binding.ivProductImage

        fun bind(product: Product) {
            binding.tvProductName.text = product.productName
            binding.tvProductPrice.text = "Rp ${product.rentPrice}"
            binding.tvProductRating.text = product.avgRating.toString()
            binding.tvProductLocation.text = product.city
        }
    }
}
