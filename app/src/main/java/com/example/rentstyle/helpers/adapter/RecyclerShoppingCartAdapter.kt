package com.example.rentstyle.helpers.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.rentstyle.databinding.ShoppingCartItemBinding

class RecyclerShoppingCartAdapter (private val dataSet: ArrayList<Int>): RecyclerView.Adapter<RecyclerShoppingCartAdapter.ViewHolder>() {
    private var onClickListener: OnClickListener? = null

    inner class ViewHolder (binding: ShoppingCartItemBinding): RecyclerView.ViewHolder(binding.root) {
        val productImage = binding.ivProductPhoto
        val deleteButton = binding.btnDeleteCart
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ShoppingCartItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)

        return ViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return dataSet.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.deleteButton.setOnClickListener {
            removeItem(position)
        }

        holder.productImage.setOnClickListener {
            if (onClickListener != null) {
                onClickListener!!.onClick(position)
            }
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun removeItem(position: Int) {
        dataSet.removeAt(position)
        notifyDataSetChanged()
    }

    interface OnClickListener{
        fun onClick(position: Int)
    }

    fun setOnClickListener(onClickListener: OnClickListener){
        this.onClickListener = onClickListener
    }
}