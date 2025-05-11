package com.blossy.flowerstore.presentation.order.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.blossy.flowerstore.databinding.ItemOrderHistoryBinding
import com.blossy.flowerstore.domain.model.Order
import com.bumptech.glide.Glide
import com.blossy.flowerstore.R
import com.blossy.flowerstore.domain.model.OrderItem

class OrderHistoryAdapter (
    private val onItemClicked: (Order) -> Unit,
    private val onCancelClicked: (Order) -> Unit,
    private val onConfirmClicked: (Order) -> Unit
): RecyclerView.Adapter<OrderHistoryAdapter.ViewHolder>() {

    private var orderHistory: List<Order> = emptyList()
    private var isExpanded = false

    inner class ViewHolder(private val binding: ItemOrderHistoryBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(order: Order) {
            binding.apply {

                orderId.text = order.id
                orderStatus.text = "${order.status}"
                val statusColor = when (order.status.lowercase()) {
                    "pending" -> R.color.orange_light
                    "processing" -> R.color.beige_light
                    "shipped" -> R.color.pink_primary
                    "delivered" -> R.color.grey_dark
                    "cancelled" -> R.color.red_dark
                    else -> R.color.grey_dark
                }
                orderStatus.setTextColor(ContextCompat.getColor(itemView.context, statusColor))

                // Set first product details
                val firstItem = order.orderItems[0]
                Glide.with(itemView.context)
                    .load(firstItem.image)
                    .into(productImage)
                productName.text = firstItem.name
                quantity.text = "x${firstItem.quantity}"
                price.text = "$${firstItem.price}"

                totalPrice.text = "Total (${order.orderItems.size} items): $${order.totalPrice}"

                cancelButton.visibility =
                    if (order.status in listOf("pending", "processing")) View.VISIBLE else View.GONE
                trackButton.visibility = if (order.status == "shipped") View.VISIBLE else View.GONE

                if (order.isDelivered && order.status == "delivered") {
                    confirmButton.visibility = View.GONE
                } else if (order.status == "delivered"){
                    confirmButton.visibility = View.VISIBLE
                }

                if (order.orderItems.size > 1) {
                    viewMoreButton.visibility = View.VISIBLE
                    otherItemsContainer.visibility = if (isExpanded) View.VISIBLE else View.GONE

                    viewMoreButton.setOnClickListener {
                        isExpanded = !isExpanded
                        otherItemsContainer.visibility = if (isExpanded) View.VISIBLE else View.GONE
                        viewMoreButton.text = if (isExpanded) "View Less" else "View More"
                        if (isExpanded) {
                            populateOtherItems(order.orderItems.drop(1))
                        }
                    }
                } else {
                    viewMoreButton.visibility = View.GONE
                }

                binding.root.setOnClickListener {
                    onItemClicked(order)
                }
                binding.cancelButton.setOnClickListener {
                    onCancelClicked(order)
                }
                binding.confirmButton.setOnClickListener {
                    onConfirmClicked(order)
                }

            }
        }

        private fun populateOtherItems(otherItems: List<OrderItem>) {
            binding.otherItemsContainer.removeAllViews()
            otherItems.forEach { item ->
                val itemView = LayoutInflater.from(itemView.context)
                    .inflate(R.layout.item_order, binding.otherItemsContainer, false)
                itemView.findViewById<ImageView>(R.id.productImage).also {
                    Glide.with(itemView.context).load(item.image).into(it)
                }
                itemView.findViewById<TextView>(R.id.productName).text = item.name
                itemView.findViewById<TextView>(R.id.quantity).text = "x${item.quantity}"
                itemView.findViewById<TextView>(R.id.price).text = "$${item.price}"
                binding.otherItemsContainer.addView(itemView)
            }
        }
    }

    fun submitList(newList: List<Order>) {
        val diffResult = DiffUtil.calculateDiff(OrderHistoryCallback(orderHistory, newList))
        orderHistory = newList
        diffResult.dispatchUpdatesTo(this)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(ItemOrderHistoryBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false))
    }

    override fun getItemCount(): Int {
        return orderHistory.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(orderHistory[position])
    }

    class OrderHistoryCallback(
        private val oldList: List<Order>,
        private val newList: List<Order>
    ) : DiffUtil.Callback() {
        override fun getOldListSize(): Int =  oldList.size

        override fun getNewListSize(): Int = newList.size

        override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            return oldList[oldItemPosition].id == newList[newItemPosition].id
        }

        override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            return oldList[oldItemPosition] == newList[newItemPosition]
        }

        override fun getChangePayload(oldItemPosition: Int, newItemPosition: Int): Any? {
            return if (oldList[oldItemPosition].isDelivered != newList[newItemPosition].isDelivered) {
                "isDelivered"
            } else {
                super.getChangePayload(oldItemPosition, newItemPosition)
            }
        }

    }
}