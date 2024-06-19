package com.example.rentstyle.helpers.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.bumptech.glide.request.RequestOptions
import com.example.rentstyle.R
import com.example.rentstyle.databinding.ProductImageItemBinding
import com.example.rentstyle.model.Product

class ProductPagingAdapter: PagingDataAdapter<Product, ProductPagingAdapter.ViewHolder>(diffCallback) {

    private var onClickListener: OnClickListener?=null

    companion object{
        val diffCallback = object: DiffUtil.ItemCallback<Product>(){
            override fun areItemsTheSame(oldItem: Product, newItem: Product): Boolean {
                return oldItem == newItem
            }

            override fun areContentsTheSame(oldItem: Product, newItem: Product): Boolean {
                return oldItem.id == newItem.id
            }
        }
    }
    inner class ViewHolder(val binding: ProductImageItemBinding): RecyclerView.ViewHolder(binding.root)

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val currentProduct = getItem(position)

        holder.binding.apply {
            if (currentProduct != null) {
                Glide.with(holder.itemView.context)
                    .load(currentProduct.image ?: R.drawable.img_placeholder)
                    .apply(RequestOptions().placeholder(R.drawable.img_placeholder).error(R.drawable.img_placeholder))
                    .transition(DrawableTransitionOptions.withCrossFade())
                    .into(ivProductImage)
            }

            tvProductName.text = currentProduct?.productName
            tvProductPrice.text = currentProduct?.rentPrice.toString()
            tvProductRating.text = currentProduct?.avgRating.toString()
            tvProductLocation.text = currentProduct?.city.toString()
        }

        holder.itemView.setOnClickListener {
            if (onClickListener!=null){
                if (currentProduct != null) {
                    onClickListener!!.onClick(position, currentProduct)
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(ProductImageItemBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    interface OnClickListener{
        fun onClick(position: Int, model: Product)
    }

    fun setOnClickListener(onClickListener: OnClickListener){
        this.onClickListener = onClickListener
    }
}