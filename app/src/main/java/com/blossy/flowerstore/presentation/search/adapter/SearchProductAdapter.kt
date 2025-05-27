package com.blossy.flowerstore.presentation.search.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.blossy.flowerstore.databinding.ItemSearchProductBinding
import com.blossy.flowerstore.databinding.ItemLoadingBinding
import com.blossy.flowerstore.domain.model.ProductModel
import com.blossy.flowerstore.utils.CurrencyFormatter
import com.blossy.flowerstore.utils.loadImage

class SearchProductAdapter(
    private val onItemClick: (ProductModel) -> Unit
) : ListAdapter<ProductModel, RecyclerView.ViewHolder>(DiffCallback()) {

    companion object {
        private const val ITEM_VIEW_TYPE = 0
        private const val LOADING_VIEW_TYPE = 1
    }

    private var isLoading = false

    fun setLoading(loading: Boolean) {
        if (isLoading != loading) {
            isLoading = loading
            if (loading) {
                notifyItemInserted(itemCount)
            } else {
                notifyItemRemoved(itemCount)
            }
        }
    }

    override fun getItemViewType(position: Int): Int {
        return if (isLoading && position == itemCount - 1) LOADING_VIEW_TYPE else ITEM_VIEW_TYPE
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            ITEM_VIEW_TYPE -> {
                val binding = ItemSearchProductBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
                ProductViewHolder(binding)
            }
            else -> {
                val binding = ItemLoadingBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
                LoadingViewHolder(binding)
            }
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is ProductViewHolder -> {
                val product = getItem(position)
                holder.bind(product)
            }
            is LoadingViewHolder -> {
            }
        }
    }

    override fun getItemCount(): Int {
        return super.getItemCount() + if (isLoading) 1 else 0
    }

    inner class ProductViewHolder(private val binding: ItemSearchProductBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(product: ProductModel) = with(binding) {
            imageProduct.loadImage(product.images.firstOrNull())
            nameProduct.text = product.name
            priceProduct.text = CurrencyFormatter.formatVND(product.price)
            ratingProduct.text = product.rating.toString()
            categoryProduct.text = product.category.name
            stockStatus.text = if (product.stock > 0) "In Stock" else "Out of Stock"
            ratingBar.rating = product.rating.toFloat()
            ratingBar.setIsIndicator(true)
            root.setOnClickListener {
                onItemClick(product)
            }
        }
    }

    class LoadingViewHolder(binding: ItemLoadingBinding) :
        RecyclerView.ViewHolder(binding.root)

    class DiffCallback : DiffUtil.ItemCallback<ProductModel>() {
        override fun areItemsTheSame(oldItem: ProductModel, newItem: ProductModel): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: ProductModel, newItem: ProductModel): Boolean {
            return oldItem == newItem
        }
    }
}