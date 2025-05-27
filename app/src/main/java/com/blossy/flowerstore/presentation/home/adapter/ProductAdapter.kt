package com.blossy.flowerstore.presentation.home.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.blossy.flowerstore.databinding.ItemProductBinding
import com.blossy.flowerstore.domain.model.ProductModel
import com.blossy.flowerstore.utils.CurrencyFormatter
import com.bumptech.glide.Glide

class ProductAdapter (
    private val onClickListener: (ProductModel) -> Unit
) : RecyclerView.Adapter<ProductAdapter.ViewHolder>() {

    private var productList = mutableListOf<ProductModel>()

    inner class ViewHolder (private val binding: ItemProductBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(product: ProductModel) {
            Glide.with(binding.imageProduct.context)
                .load(product.images[0])
                .into(binding.imageProduct)
            binding.apply {
                nameProduct.text = product.name
                categoryProduct.text = product.category.name
                ratingBar.rating = product.rating.toFloat()
                numReviews.text = "(${product.numReviews})"
                stockStatus.text = if (product.stock > 0) "In Stock" else "Out of Stock"
                priceProduct.text = CurrencyFormatter.formatVND(product.price)
            }
            binding.root.setOnClickListener {
                onClickListener(product)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemProductBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return productList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(productList[position])
    }

    fun submitList(products: List<ProductModel>) {
        productList = products.toMutableList()
        notifyDataSetChanged()
    }

}