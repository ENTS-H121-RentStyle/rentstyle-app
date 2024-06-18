package com.example.rentstyle.helpers.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.rentstyle.R
import com.example.rentstyle.model.remote.response.CartResponse

class RecyclerShoppingCartAdapter(
    private var dataset: MutableList<CartResponse>
) : RecyclerView.Adapter<RecyclerShoppingCartAdapter.ShoppingCartViewHolder>() {

    private var onClickListener: OnClickListener?=null

    private val viewHolderList = mutableListOf<ShoppingCartViewHolder>()

    class ShoppingCartViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val productName: TextView = view.findViewById(R.id.tv_product_name)
        val productPrice: TextView = view.findViewById(R.id.tv_product_price)
        val productImage: ImageView = view.findViewById(R.id.iv_product_photo)
        val deleteItem: ImageView = view.findViewById(R.id.btn_delete_cart)
        val addCart: ImageView = view.findViewById(R.id.btn_add_product_cart)
        var productAmount: TextView = view.findViewById(R.id.tv_product_amount_cart)
        val minCart: ImageView = view.findViewById(R.id.btn_min_product_cart)
        val selectBox: CheckBox = view.findViewById(R.id.cb_product_shopping_cart)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ShoppingCartViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.shopping_cart_item, parent, false)
        return ShoppingCartViewHolder(view)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ShoppingCartViewHolder, position: Int) {
        val item = dataset[position]

        if (!viewHolderList.contains(holder)) {
            viewHolderList.add(holder)
        }

        holder.productName.text = item.product.productName
        holder.productPrice.text = "${item.product.rentPrice}"
        holder.productAmount.text = item.duration.toString()

        Glide.with(holder.itemView.context)
            .load(item.product.image)
            .placeholder(R.drawable.img_placeholder)
            .into(holder.productImage)

        holder.selectBox.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                resetAllAppearance()
                holder.selectBox.isChecked = true
                if (onClickListener!=null){
                    onClickListener!!.onItemSelected(position, item.id, item)
                }
            } else {
                if (onClickListener!=null){
                    onClickListener!!.onItemUnSelected()
                }
            }
        }

        holder.minCart.setOnClickListener {
            if (onClickListener!=null){
                onClickListener!!.onReduceClick(position, item.id, item.duration, item)
            }
        }

        holder.addCart.setOnClickListener {
            if (onClickListener!=null){
                onClickListener!!.onAddClick(position, item.id, item.duration, item)
            }
        }

        holder.deleteItem.setOnClickListener {
            if (onClickListener!=null){
                onClickListener!!.onClick(position, item.id)
            }
        }
    }

    override fun getItemCount() = dataset.size

    private fun resetAllAppearance() {
        for (viewHolder in viewHolderList) {
            viewHolder.selectBox.isChecked = false
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    fun deleteItem(position: Int, id: String) {
        dataset.map {
            if (it.id == id) {
                dataset.remove(it)
            }
        }

        viewHolderList.removeAt(position)
        notifyDataSetChanged()
    }

    @SuppressLint("NotifyDataSetChanged")
    fun updateItemDuration(position: Int, id: String, duration: Int) {
        dataset.map {
            if (it.id == id) {
                dataset[position].duration = duration
                notifyDataSetChanged()
            }
        }
    }

    interface OnClickListener{
        fun onClick(position: Int, id: String)

        fun onAddClick(position: Int, id: String, duration: Int, model: CartResponse)

        fun onReduceClick(position: Int, id: String, duration: Int, model: CartResponse)

        fun onItemSelected(position: Int, id:String, model: CartResponse)

        fun onItemUnSelected()
    }

    fun setOnClickListener(onClickListener: OnClickListener){
        this.onClickListener = onClickListener
    }
}