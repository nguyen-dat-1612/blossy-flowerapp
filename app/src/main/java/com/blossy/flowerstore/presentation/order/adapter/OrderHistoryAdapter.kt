package com.blossy.flowerstore.presentation.order.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.blossy.flowerstore.databinding.ItemOrderHistoryBinding
import com.blossy.flowerstore.domain.model.OrderModel
import com.bumptech.glide.Glide
import com.blossy.flowerstore.R
import com.blossy.flowerstore.domain.model.OrderItemModel
import com.blossy.flowerstore.utils.CurrencyFormatter

class OrderHistoryAdapter (
    private val onItemClicked: (OrderModel) -> Unit,
    private val onCancelClicked: (OrderModel) -> Unit,
    private val onConfirmClicked: (OrderModel) -> Unit
): RecyclerView.Adapter<OrderHistoryAdapter.ViewHolder>() {

    private var orderHistory: List<OrderModel> = emptyList()
    private var isExpanded = false

    inner class ViewHolder(private val binding: ItemOrderHistoryBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(order: OrderModel) {
            binding.apply {

                orderId.text = order.id
                orderStatus.text = "${order.status.uppercase()}"
                val firstItem = order.orderItems[0]
                Glide.with(itemView.context)
                    .load(firstItem.image)
                    .into(imageView)
                nameText.text = firstItem.name
                quantityText.text = "${firstItem.quantity}"

                priceText.text = CurrencyFormatter.formatVND(firstItem.price);

                totalPrice.text = "Total (${order.orderItems.size} items): ${CurrencyFormatter.formatVND(order.totalPrice)}"

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

        private fun populateOtherItems(otherItems: List<OrderItemModel>) {
            binding.otherItemsContainer.removeAllViews()
            otherItems.forEach { item ->
                val itemView = LayoutInflater.from(itemView.context)
                    .inflate(R.layout.item_order, binding.otherItemsContainer, false)
                itemView.findViewById<ImageView>(R.id.imageView).also {
                    Glide.with(itemView.context).load(item.image).into(it)
                }
                itemView.findViewById<TextView>(R.id.nameText).text = item.name
                itemView.findViewById<TextView>(R.id.quantityText).text = "${item.quantity}"
                itemView.findViewById<TextView>(R.id.priceText).text = "đ${item.price}"
                binding.otherItemsContainer.addView(itemView)
            }
        }
    }

    fun submitList(newList: List<OrderModel>) {
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
        private val oldList: List<OrderModel>,
        private val newList: List<OrderModel>
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