package com.example.rentstyle.helpers.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.rentstyle.databinding.FavoriteProductImageItemBinding
import com.example.rentstyle.databinding.ProductCheckOutListItemBinding

class RecyclerCheckOutProductAdapter : RecyclerView.Adapter<RecyclerCheckOutProductAdapter.ViewHolder>() {
    private var onClickListener: OnClickListener? = null
    inner class ViewHolder (binding: ProductCheckOutListItemBinding): RecyclerView.ViewHolder(binding.root) {
        val productName = binding.tvProductName
        val productPrice = binding.tvProductPrice
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ProductCheckOutListItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)

        return ViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return 5
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.productName.text = "Kostum Honkai: Star Rail Black Swan Cosplay Black Swan Costume Black Swan Kostum Full Set"
        holder.productPrice.text = "Rp. 58.000 (2 hari)"

        holder.itemView.setOnClickListener {
            if (onClickListener != null) {
                onClickListener!!.onClick(position)
            }
        }
    }

    interface OnClickListener{
        fun onClick(position: Int)
    }

    fun setOnClickListener(onClickListener: OnClickListener){
        this.onClickListener = onClickListener
    }
}