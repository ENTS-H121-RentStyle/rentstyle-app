package com.example.rentstyle.helpers.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.bumptech.glide.request.RequestOptions
import com.example.rentstyle.R
import com.example.rentstyle.model.Product

class ProductAdapter(private var products: List<Product>) :
    RecyclerView.Adapter<ProductAdapter.ProductViewHolder>() {

    private lateinit var context: Context
    private var onClickListener: OnClickListener? = null

    fun setOnClickListener(onClickListener: OnClickListener) {
        this.onClickListener = onClickListener
    }

    interface OnClickListener {
        fun onClick(position: Int, image: ImageView)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductViewHolder {
        context = parent.context
        val view = LayoutInflater.from(context).inflate(R.layout.product_image_item, parent, false)
        return ProductViewHolder(view)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ProductViewHolder, position: Int) {
        val product = products[position]

        holder.apply {
            // Load image using Glide
            Glide.with(context)
                .load(product.image)
                .apply(RequestOptions()
                    .placeholder(R.drawable.img_placeholder)
                    .error(R.drawable.img_placeholder))
                .transition(DrawableTransitionOptions.withCrossFade())
                .into(ivProductImage)

            tvProductName.text = product.productName
            tvProductPrice.text = "Rp ${product.rentPrice}"
            tvProductRating.text = product.avgRating.toString()
            tvProductLocation.text = product.city

            // Handle item click
            cvProductItem.setOnClickListener {
                onClickListener?.onClick(position, ivProductImage)
            }
        }
    }

    override fun getItemCount(): Int {
        return products.size
    }

    @SuppressLint("NotifyDataSetChanged")
    fun updateData(products: List<Product>) {
        this.products = products
        notifyDataSetChanged()
    }

    fun getItem(position: Int): Product {
        return products[position]
    }

    inner class ProductViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val cvProductItem: CardView = itemView.findViewById(R.id.cv_product_item)
        val ivProductImage: ImageView = itemView.findViewById(R.id.iv_product_image)
        val tvProductName: TextView = itemView.findViewById(R.id.tv_product_name)
        val tvProductPrice: TextView = itemView.findViewById(R.id.tv_product_price)
        val tvProductRating: TextView = itemView.findViewById(R.id.tv_product_rating)
        val tvProductLocation: TextView = itemView.findViewById(R.id.tv_product_location)
    }
}