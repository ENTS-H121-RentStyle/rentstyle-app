package com.example.rentstyle.helpers.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.rentstyle.R
import com.example.rentstyle.model.remote.response.CartResponse

class RecyclerShoppingCartAdapter(
    private var dataset: List<CartResponse>
) : RecyclerView.Adapter<RecyclerShoppingCartAdapter.ShoppingCartViewHolder>() {

    class ShoppingCartViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val productName: TextView = view.findViewById(R.id.tv_product_name)
        val productPrice: TextView = view.findViewById(R.id.tv_product_price)
        val productImage: ImageView = view.findViewById(R.id.iv_product_photo)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ShoppingCartViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.shopping_cart_item, parent, false)
        return ShoppingCartViewHolder(view)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ShoppingCartViewHolder, position: Int) {
        val item = dataset[position]

        holder.productName.text = item.product.productName
        holder.productPrice.text = "Rp. ${item.product.rentPrice} (x${item.duration} hari)"

        Glide.with(holder.itemView.context)
            .load(item.product.image)
            .placeholder(R.drawable.img_placeholder)
            .into(holder.productImage)
    }

    override fun getItemCount() = dataset.size

    @SuppressLint("NotifyDataSetChanged")
    fun updateData(newDataset: List<CartResponse>) {
        dataset = newDataset
        notifyDataSetChanged()
    }
}