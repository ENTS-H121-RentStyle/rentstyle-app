package com.example.rentstyle.helpers.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.rentstyle.R
import com.example.rentstyle.databinding.OrderItemBinding
import com.example.rentstyle.model.remote.response.ResponseOrderItem

class OrderAdapter (private val orderList: List<ResponseOrderItem>) : RecyclerView.Adapter<OrderAdapter.ViewHolder>() {
    private var onClickListener: OnClickListener? = null
    inner class ViewHolder (binding: OrderItemBinding): RecyclerView.ViewHolder(binding.root) {
        val date = binding.tvOrderDate
        val productName = binding.tvProductName
        val productAmount = binding.tvProductAmount
        val rentDuration = binding.tvProductRentDuration
        val orderCost = binding.tvOrderTotalCost
        val productPhoto = binding.ivProductPhoto
        val orderButton = binding.btnOrderAction
        val orderStatus = binding.tvOrderStatus
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = OrderItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)

        return ViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return orderList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val currentOrder = orderList[position]

        holder.apply {
            date.text = currentOrder.orderDate
            productName.text = currentOrder.product!!.productName
            Glide.with(itemView.context)
                .load(currentOrder.product.productImage)
                .apply(
                    RequestOptions()
                        .placeholder(R.drawable.img_placeholder)
                        .error(R.drawable.img_placeholder)
                ).into(productPhoto)
            productAmount.text = itemView.context.getString(R.string.txt_order_quantity)
            rentDuration.text = itemView.context.getString(R.string.txt_order_duration, currentOrder.rentDuration.toString())
            orderCost.text = itemView.context.getString(R.string.txt_total_order_payment, currentOrder.totalPayment.toString())
            orderStatus.text = currentOrder.orderStatus
            orderButton.text = itemView.context.getString(R.string.txt_check_order)
        }

        holder.itemView.setOnClickListener {
            if (onClickListener != null) {
                onClickListener!!.onClick(position, currentOrder.id.toString())
            }
        }
    }

    interface OnClickListener{
        fun onClick(position: Int, orderId: String)
    }

    fun setOnClickListener(onClickListener: OnClickListener){
        this.onClickListener = onClickListener
    }
}