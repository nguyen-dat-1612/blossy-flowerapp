package com.blossy.flowerstore.presentation.cart.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.blossy.flowerstore.databinding.ItemCartBinding
import com.blossy.flowerstore.domain.model.CartItemModel
import com.blossy.flowerstore.utils.CurrencyFormatter
import com.blossy.flowerstore.utils.loadImage

class CartAdapter(
    private val onDeleteClicked: (CartItemModel) -> Unit,
    private val onQuantityChanged: (CartItemModel, Int) -> Unit
) : ListAdapter<CartItemModel, CartAdapter.ViewHolder>(CartDiffCallback()) {

    inner class ViewHolder(private val binding: ItemCartBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(cartItem: CartItemModel) {
            with(binding) {
                imageView.loadImage(cartItem.product.images.firstOrNull())

                nameText.text = cartItem.product.name
                quantityText.text = cartItem.quantity.toString()
                priceText.text = CurrencyFormatter.formatVND(cartItem.product.price)

                addQuantityButton.setOnClickListener {
                    onQuantityChanged(cartItem, cartItem.quantity + 1)
                }

                minusQuantityButton.setOnClickListener {
                    val newQuantity = cartItem.quantity - 1
                    if (newQuantity >= 1) onQuantityChanged(cartItem, newQuantity)
                }

                deleteButton.setOnClickListener {
                    onDeleteClicked(cartItem)
                }
            }
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemCartBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }


    class CartDiffCallback : DiffUtil.ItemCallback<CartItemModel>() {
        override fun areItemsTheSame(oldItem: CartItemModel, newItem: CartItemModel): Boolean {
            return oldItem.product.id == newItem.product.id
        }

        override fun areContentsTheSame(oldItem: CartItemModel, newItem: CartItemModel): Boolean {
            return oldItem.quantity == newItem.quantity &&
                    oldItem.product.price == newItem.product.price &&
                    oldItem.product.name == newItem.product.name
        }

        override fun getChangePayload(oldItem: CartItemModel, newItem: CartItemModel): Any? {
            return if (oldItem.quantity != newItem.quantity) {
                "quantity"
            } else {
                null
            }
        }
    }
}