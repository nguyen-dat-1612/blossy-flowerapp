package com.blossy.flowerstore.presentation.cart.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.blossy.flowerstore.databinding.ItemCartBinding
import com.blossy.flowerstore.domain.model.CartItem
import com.bumptech.glide.Glide

class CartAdapter (
    private var cartItems: MutableList<CartItem>,
    private val onDeleteClicked: (CartItem) -> Unit,
    private val onQuantityChanged: (CartItem, Int) -> Unit
) : RecyclerView.Adapter<CartAdapter.ViewHolder>() {

    inner class ViewHolder (private val binding: ItemCartBinding) : RecyclerView.ViewHolder(binding.root){
        fun bind(cartItem: CartItem) {
            binding.apply {
                Glide.with(binding.imageView.context)
                    .load(cartItem.product.images[0])
                    .into(binding.imageView)
                nameText.text = cartItem.product.name
                quantityText.text = "${cartItem.quantity}"
                priceText.text = "đ${cartItem.product.price}"
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

    override fun getItemCount(): Int {
       return cartItems.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(cartItems[position])
    }

    // Cập nhật lại danh sách cartItems
    fun submitList(cartItems: List<CartItem>) {
        this.cartItems = cartItems.toMutableList() // Chuyển List thành MutableList để có thể thay đổi
        notifyDataSetChanged() // Cập nhật dữ liệu cho Adapter
    }

    // Thêm item vào giỏ hàng
    fun addItem(cartItem: CartItem) {
        cartItems.add(cartItem)
        notifyItemInserted(cartItems.size - 1)
    }

    // Xóa item khỏi giỏ hàng
    fun removeItem(position: Int) {
        cartItems.removeAt(position)
        notifyItemRemoved(position)
    }

}