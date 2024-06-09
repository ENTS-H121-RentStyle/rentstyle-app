package com.example.rentstyle.helpers.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.rentstyle.databinding.OrderItemBinding

class RecyclerDummyOrderAdapter : RecyclerView.Adapter<RecyclerDummyOrderAdapter.ViewHolder>() {
    private var onClickListener: OnClickListener? = null
    inner class ViewHolder (binding: OrderItemBinding): RecyclerView.ViewHolder(binding.root) {
        val date = binding.tvOrderDate
        val productName = binding.tvProductName
        val productAmount = binding.tvProductAmount
        val rentDuration = binding.tvProductRentDuration
        val orderCost = binding.tvOrderTotalCost
        val orderButton = binding.btnOrderAction
        val orderAmount = binding.tvOrderAmount
        val orderStatus = binding.tvOrderStatus
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = OrderItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)

        return ViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return 15
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.apply {
            date.text = "25 Mei 2024"
            productName.text = "Kostum Honkai: Star Rail Black Swan Cosplay Black Swan Costume Black Swan Kostum Full Set"
            productAmount.text = "Quantity : 1"
            rentDuration.text = "Durasi sewa : 2 hari"
            orderCost.text = "Total Belanja : Rp xxxx"
            orderAmount.text = "+ 3 Produk Lainnya"
            orderStatus.text = "Belum Dibayar"
            orderButton.text = "Bayar"
        }

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