package com.blossy.flowerstore.presentation.cart.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.blossy.flowerstore.databinding.ItemPriceDetailBinding
import com.blossy.flowerstore.domain.model.PriceDetailItem

class PriceDetailAdapter(
    private var items: List<PriceDetailItem>
) : RecyclerView.Adapter<PriceDetailAdapter.ViewHolder>() {

    class ViewHolder(val binding: ItemPriceDetailBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: PriceDetailItem) {
            binding.productName.text = item.name
            binding.quantity.text = "x ${item.quantity}"
            binding.price.text = String.format("%.2f $", item.totalPrice)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemPriceDetailBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return ViewHolder(binding)
    }

    override fun getItemCount() = items.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(items[position])
    }

    fun updateList(newItems: List<PriceDetailItem>) {
        items = newItems
        notifyDataSetChanged()
    }
}
