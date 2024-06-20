package com.example.rentstyle.helpers.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.rentstyle.databinding.ShopCardItemBinding

class RecyclerDummyShopAdapter : RecyclerView.Adapter<RecyclerDummyShopAdapter.ViewHolder>() {
    private var onClickListener: OnClickListener? = null

    inner class ViewHolder (binding: ShopCardItemBinding): RecyclerView.ViewHolder(binding.root) {
        val shopName = binding.tvShopName
        val shopLocation = binding.tvShopRating
        val shopImage1 = binding.ivProductImage1
        val shopImage2 = binding.ivProductImage2
        val shopImage3 = binding.ivProductImage3
        val shopImage4 = binding.ivProductImage4
        val shopImage5 = binding.ivProductImage5
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ShopCardItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)

        return ViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return 1
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.shopName.text = "Yoga"
        holder.shopLocation.text = "Bogor"
        Glide.with(holder.itemView.context)
            .load("https://storage.googleapis.com/rentstyle-drive/product/1718464970950-product_image.jpg")
            .into(holder.shopImage1)
        Glide.with(holder.itemView.context)
            .load("https://storage.googleapis.com/rentstyle-drive/product/1718464481066-product_image.jpg")
            .into(holder.shopImage2)
        Glide.with(holder.itemView.context)
            .load("https://storage.googleapis.com/rentstyle-drive/product/1718464624353-product_image.jpg")
            .into(holder.shopImage3)
        Glide.with(holder.itemView.context)
            .load("https://storage.googleapis.com/rentstyle-drive/product/1718464164312-product_image.jpg")
            .into(holder.shopImage4)
        Glide.with(holder.itemView.context)
            .load("https://storage.googleapis.com/rentstyle-drive/product/1718465084678-product_image.jpg")
            .into(holder.shopImage5)

        holder.itemView.setOnClickListener {
            if (onClickListener != null) {
                onClickListener!!.onClick(position, "9725268d-cc08-4113-bcae-6f7a45d308a0")
            }
        }
    }

    interface OnClickListener{
        fun onClick(position: Int, shopId: String)
    }

    fun setOnClickListener(onClickListener: OnClickListener){
        this.onClickListener = onClickListener
    }
}