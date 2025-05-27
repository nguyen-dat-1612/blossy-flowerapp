package com.blossy.flowerstore.presentation.checkout.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.blossy.flowerstore.databinding.ItemProductPaymentBinding
import com.blossy.flowerstore.domain.model.CartItemModel
import com.blossy.flowerstore.utils.CurrencyFormatter
import com.bumptech.glide.Glide

class OrderItemAdapter (
    private var cartItems: MutableList<CartItemModel>,
) : RecyclerView.Adapter<OrderItemAdapter.ViewHolder>() {

    inner class ViewHolder (private val binding: ItemProductPaymentBinding) : RecyclerView.ViewHolder(binding.root){
        fun bind(cartItem: CartItemModel) {
            binding.apply {
                Glide.with(binding.imageView.context)
                    .load(cartItem.product.images[0])
                    .into(binding.imageView)
                nameText.text = cartItem.product.name
                quantityText.text = "${cartItem.quantity}"
                priceText.text = CurrencyFormatter.formatVND(cartItem.product.price)

            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemProductPaymentBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun getItemCount(): Int {
       return cartItems.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(cartItems[position])
    }

    fun submitList(data: List<CartItemModel>) {
        this.cartItems = data.toMutableList()
        notifyDataSetChanged()
    }

}