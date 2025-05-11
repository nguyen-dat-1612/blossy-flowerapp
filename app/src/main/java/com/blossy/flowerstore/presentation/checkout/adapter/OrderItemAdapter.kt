package com.blossy.flowerstore.presentation.checkout.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.blossy.flowerstore.databinding.ItemProductPaymentBinding
import com.blossy.flowerstore.domain.model.CartItem
import com.bumptech.glide.Glide

class OrderItemAdapter (
    private var cartItems: MutableList<CartItem>,
) : RecyclerView.Adapter<OrderItemAdapter.ViewHolder>() {

    inner class ViewHolder (private val binding: ItemProductPaymentBinding) : RecyclerView.ViewHolder(binding.root){
        fun bind(cartItem: CartItem) {
            binding.apply {
                Glide.with(binding.imageProduct.context)
                    .load(cartItem.product.images[0])
                    .into(binding.imageProduct)
                nameProduct.text = cartItem.product.name
                descriptionProduct.text = cartItem.product.description
                quantityProduct.text = "x" +"${cartItem.quantity}"
                priceProduct.text = cartItem.product.price.toString()

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

    fun submitList(data: List<CartItem>) {
        this.cartItems = data.toMutableList()
        notifyDataSetChanged()
    }

}