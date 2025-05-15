package com.blossy.flowerstore.presentation.notification.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.blossy.flowerstore.presentation.notification.ui.NotificationUiItem
import com.blossy.flowerstore.R
import com.blossy.flowerstore.domain.model.Notification

class NotificationAdapter(
    private val onMarkAllAsReadClick: (String) -> Unit
) : ListAdapter<NotificationUiItem, RecyclerView.ViewHolder>(DiffCallback) {

    companion object {
        private const val VIEW_TYPE_HEADER = 0
        private const val VIEW_TYPE_ITEM = 1

        val DiffCallback = object : DiffUtil.ItemCallback<NotificationUiItem>() {
            override fun areItemsTheSame(old: NotificationUiItem, new: NotificationUiItem): Boolean {
                return old == new
            }

            override fun areContentsTheSame(old: NotificationUiItem, new: NotificationUiItem): Boolean {
                return old == new
            }
        }
    }

    override fun getItemViewType(position: Int): Int {
        return when (getItem(position)) {
            is NotificationUiItem.Header -> VIEW_TYPE_HEADER
            is NotificationUiItem.Item -> VIEW_TYPE_ITEM
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when(viewType) {
            VIEW_TYPE_HEADER -> {
                val view = LayoutInflater.from(parent.context)
                    .inflate(R.layout.item_notification_header, parent, false)
                HeaderViewHolder(view)
            }
            else -> {
                val view = LayoutInflater.from(parent.context)
                    .inflate(R.layout.item_notification, parent, false)
                ItemViewHolder(view)
            }
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (val item = getItem(position)) {
            is NotificationUiItem.Header -> (holder as HeaderViewHolder).bind(item)
            is NotificationUiItem.Item -> (holder as ItemViewHolder).bind(item.notification)
        }
    }

    inner class HeaderViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val tvTitle = itemView.findViewById<TextView>(R.id.tvHeaderTitle)
        private val tvMarkAllAsRead = itemView.findViewById<TextView>(R.id.tvMarkAllAsRead)

        fun bind(header: NotificationUiItem.Header) {
            tvTitle.text = header.label

            if (header.label == "Today") {
                tvMarkAllAsRead.visibility = View.VISIBLE
                tvMarkAllAsRead.setOnClickListener {
                    onMarkAllAsReadClick(header.label)
                }
            } else {
                tvMarkAllAsRead.visibility = View.GONE
            }
        }
    }

    inner class ItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val tvTitle = itemView.findViewById<TextView>(R.id.notificationTitle)
        private val tvMessage = itemView.findViewById<TextView>(R.id.notificationMessage)
        private val container = itemView.findViewById<LinearLayout>(R.id.notificationItem)

        fun bind(notification: Notification) {
            tvTitle.text = notification.title
            tvMessage.text = notification.message

            val bgColor = if (notification.read) R.color.grey_light else R.color.white
            container.setBackgroundResource(bgColor)
        }
    }
}
