package com.blossy.flowerstore.presentation.cart.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.blossy.flowerstore.databinding.ItemCartBinding
import com.blossy.flowerstore.domain.model.CartItem
import com.blossy.flowerstore.utils.CurrencyFormatter
import com.bumptech.glide.Glide

class CartAdapter (
    private val onDeleteClicked: (CartItem) -> Unit,
    private val onQuantityChanged: (CartItem, Int) -> Unit
) : RecyclerView.Adapter<CartAdapter.ViewHolder>() {

    private var cartItems: MutableList<CartItem> = emptyList<CartItem>().toMutableList()

    inner class ViewHolder (private val binding: ItemCartBinding) : RecyclerView.ViewHolder(binding.root){
        fun bind(cartItem: CartItem) {
            binding.apply {
                Glide.with(binding.imageView.context)
                    .load(cartItem.product.images[0])
                    .into(binding.imageView)
                nameText.text = cartItem.product.name
                quantityText.text = "${cartItem.quantity}"
                priceText.text = CurrencyFormatter.formatVND(cartItem.product.price)
                addQuantityButton.setOnClickListener {
                    val newQuantity = cartItem.quantity + 1
                    onQuantityChanged(cartItem, newQuantity)
                }

                minusQuantityButton.setOnClickListener {
                    val newQuantity = cartItem.quantity - 1
                    if (newQuantity >= 1) {
                        onQuantityChanged(cartItem, newQuantity)
                    }
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
        holder.bind(cartItems[position])
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int, payloads: MutableList<Any>) {
        if (payloads.isNotEmpty() && payloads[0] == "quantity") {
            holder.bind(cartItems[position])
        } else {
            super.onBindViewHolder(holder, position, payloads)
        }
    }

    override fun getItemCount(): Int  = cartItems.size

    fun currentList(): List<CartItem> = cartItems.toList()

    fun submitList(newCartItems: List<CartItem>) {
        val diffResult = DiffUtil.calculateDiff(CartDiffCallBack(newCartItems, this.cartItems))
        this.cartItems = newCartItems.toMutableList()
        diffResult.dispatchUpdatesTo(this)
        notifyDataSetChanged()
    }

    fun updateItemQuantity(productId: String, newQuantity: Int) {
        val index = cartItems.indexOfFirst { it.product.id == productId }
        if (index != -1) {
            val updatedItem = cartItems[index].copy(quantity = newQuantity)
            cartItems[index] = updatedItem
            notifyItemChanged(index, "quantity")
        }
    }

    fun removeItemByProductId(productId: String) {
        val index = cartItems.indexOfFirst { it.product.id == productId }
        if (index != -1) {
            cartItems.removeAt(index)
            notifyItemRemoved(index)
        }
    }

    class CartDiffCallBack(
        private val newListItem: List<CartItem>,
        private val oldListItem: List<CartItem>
    ) : DiffUtil.Callback() {
        override fun getOldListSize(): Int = oldListItem.size
        override fun getNewListSize(): Int = newListItem.size

        override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
           return newListItem[newItemPosition].id == oldListItem[oldItemPosition].id
        }

        override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            return newListItem[newItemPosition] == oldListItem[oldItemPosition]
        }

        override fun getChangePayload(oldItemPosition: Int, newItemPosition: Int): Any? {
            return if (newListItem[newItemPosition].quantity != oldListItem[oldItemPosition].quantity) {
                "quantity"
            } else {
                super.getChangePayload(oldItemPosition, newItemPosition)
            }
        }
    }

}