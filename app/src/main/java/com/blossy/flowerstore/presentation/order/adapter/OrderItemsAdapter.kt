package com.blossy.flowerstore.presentation.order.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.blossy.flowerstore.databinding.ItemOrderBinding
import com.blossy.flowerstore.domain.model.OrderItemModel
import com.blossy.flowerstore.utils.CurrencyFormatter
import com.bumptech.glide.Glide

class OrderItemsAdapter(
    private val orderItems: List<OrderItemModel>
) : RecyclerView.Adapter<OrderItemsAdapter.ViewHolder>() {

    class ViewHolder(private val binding: ItemOrderBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(orderItem: OrderItemModel) {
            binding.apply {
                Glide.with(imageView.context)
                    .load(orderItem.image)
                    .into(imageView)
                nameText.text = orderItem.name
                quantityText.text = orderItem.quantity.toString()
                priceText.text = CurrencyFormatter.formatVND(orderItem.price);
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