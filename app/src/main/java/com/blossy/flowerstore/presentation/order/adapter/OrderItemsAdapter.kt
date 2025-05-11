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
                Glide.with(imageView.context)
                    .load(orderItem.image)
                    .into(imageView)
                nameText.text = orderItem.name
                quantityText.text = orderItem.quantity.toString()
                priceText.text = orderItem.price.toString()
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