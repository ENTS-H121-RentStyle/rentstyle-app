package com.example.rentstyle.helpers.adapter

import android.util.TypedValue
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.rentstyle.R
import com.example.rentstyle.databinding.FilterItemBinding

class RecyclerFilterAdapter (private val filterData: ArrayList<String>) : RecyclerView.Adapter<RecyclerFilterAdapter.ViewHolder>() {
    private var onClickListener: OnClickListener? = null
    private val viewHolderList = mutableListOf<ViewHolder>()
    private lateinit var firstViewHolder: ViewHolder
    private var setFirstFilterOn = true
    inner class ViewHolder (binding: FilterItemBinding): RecyclerView.ViewHolder(binding.root) {
        val containerFilterName = binding.container
        val filterName = binding.tvFilterName
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = FilterItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)

        return ViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return filterData.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.filterName.text = filterData[position]

        if (!viewHolderList.contains(holder)) {
            viewHolderList.add(holder)
        }

        if (position == 0 && setFirstFilterOn) {
            firstViewHolder = holder
            resetAllFilterAppearance()

            holder.filterName.setTextColor(ContextCompat.getColor(holder.itemView.context, R.color.orange_1))
            holder.containerFilterName.setBackgroundResource(R.drawable.rounded_rect_active)

            setFirstFilterOn = false
        }

        holder.itemView.setOnClickListener {
            if (onClickListener != null) {
                onClickListener!!.onClick(position)
            }

            resetAllFilterAppearance()

            holder.filterName.setTextColor(ContextCompat.getColor(holder.itemView.context, R.color.orange_1))
            holder.containerFilterName.setBackgroundResource(R.drawable.rounded_rect_active)
        }
    }

    interface OnClickListener{
        fun onClick(position: Int)
    }

    fun setOnClickListener(onClickListener: OnClickListener){
        this.onClickListener = onClickListener
    }

    private fun resetAllFilterAppearance() {
        for (viewHolder in viewHolderList) {
            val typedValue = TypedValue()
            viewHolder.itemView.context.theme.resolveAttribute(android.R.attr.textColorHint, typedValue, true)
            val defaultColor = ContextCompat.getColor(viewHolder.itemView.context, typedValue.resourceId)

            viewHolder.filterName.setTextColor(defaultColor)
            viewHolder.containerFilterName.setBackgroundResource(R.drawable.rounded_rect)
        }
    }

    fun resetFilterAppearance() {
        resetAllFilterAppearance()

        firstViewHolder.filterName.setTextColor(ContextCompat.getColor(firstViewHolder.itemView.context, R.color.orange_1))
        firstViewHolder.containerFilterName.setBackgroundResource(R.drawable.rounded_rect_active)
    }
}