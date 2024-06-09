package com.example.rentstyle.helpers.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.example.rentstyle.databinding.ProductImageItemBinding

class RecyclerDummyAdapter : RecyclerView.Adapter<RecyclerDummyAdapter.ViewHolder>() {
    inner class ViewHolder (binding: ProductImageItemBinding): RecyclerView.ViewHolder(binding.root) {
        val image = binding.ivProductImage
    }

    private var onClickListener: OnClickListener?=null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ProductImageItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)

        return ViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return 10
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.itemView.setOnClickListener {
            if (onClickListener != null) {
                onClickListener!!.onClick(position, holder.image)
            }
        }
    }

    interface OnClickListener{
        fun onClick(position: Int, image: ImageView)
    }

    fun setOnClickListener(onClickListener: OnClickListener){
        this.onClickListener = onClickListener
    }
}