package com.blossy.flowerstore.presentation.order.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.blossy.flowerstore.databinding.ItemOrderBinding
import com.blossy.flowerstore.domain.model.OrderItem
import com.bumptech.glide.Glide

class OrderItemsAdapter(
    private val orderItems: List<OrderItem>
) : RecyclerView.Adapter<OrderItemsAdapter.ViewHolder>() {

    class ViewHolder(private val binding: ItemOrderBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(orderItem: OrderItem) {
            binding.apply {
                Glide.with(productImage.context)
                    .load(orderItem.image)
                    .into(productImage)
                productName.text = orderItem.name
                quantity.text = orderItem.quantity.toString()
                price.text = orderItem.price.toString()
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(ItemOrderBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        ))
    }

    override fun getItemCount(): Int {
        return orderItems.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(orderItems[position])
    }
}