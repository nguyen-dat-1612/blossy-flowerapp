package com.blossy.flowerstore.presentation.favorite.adapter

import android.view.LayoutInflater
import android.view.View.VISIBLE
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.blossy.flowerstore.R
import com.blossy.flowerstore.databinding.ItemSearchProductBinding
import com.blossy.flowerstore.domain.model.ProductModel
import com.blossy.flowerstore.utils.CurrencyFormatter
import com.blossy.flowerstore.utils.loadImage

class FavoritesAdapter(
    private val onFavoriteClicked: (ProductModel) -> Unit
) : RecyclerView.Adapter<FavoritesAdapter.FavoriteViewHolder>() {

    private var products: List<ProductModel> = emptyList()

    fun submitList(newProducts: List<ProductModel>) {
        val diffResult = DiffUtil.calculateDiff(ProductDiffCallback(products, newProducts))
        products = newProducts
        diffResult.dispatchUpdatesTo(this)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FavoriteViewHolder {
        val binding = ItemSearchProductBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return FavoriteViewHolder(binding)
    }

    override fun onBindViewHolder(holder: FavoriteViewHolder, position: Int) {
        val product = products[position]
        holder.bind(product)
    }

    override fun getItemCount(): Int = products.size

    inner class FavoriteViewHolder(private val binding: ItemSearchProductBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(product: ProductModel) = with(binding) {
            imageProduct.loadImage(product.images.firstOrNull())
            nameProduct.text = product.name
            priceProduct.text = CurrencyFormatter.formatVND(product.price)
            ratingProduct.text = product.rating.toString()
            categoryProduct.text = product.category.name
            stockStatus.text = if (product.stock > 0) "In Stock" else "Out of Stock"
            ratingBar.run {
                rating = product.rating.toFloat()
                setIsIndicator(true)
            }
            ivFavorite.apply {
                visibility = VISIBLE;
                setImageResource(
                    if (product.isFavorite) R.drawable.ic_favorite else R.drawable.ic_favorite_border
                )
                setOnClickListener {
                    onFavoriteClicked(product)
                }
            }
        }
    }

    class ProductDiffCallback(
        private val oldList: List<ProductModel>,
        private val newList: List<ProductModel>
    ) : DiffUtil.Callback() {

        override fun getOldListSize(): Int = oldList.size
        override fun getNewListSize(): Int = newList.size

        override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            return oldList[oldItemPosition].id == newList[newItemPosition].id
        }

        override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            return oldList[oldItemPosition].isFavorite == newList[newItemPosition].isFavorite
        }

        override fun getChangePayload(oldItemPosition: Int, newItemPosition: Int): Any? {
            return if (oldList[oldItemPosition].isFavorite != newList[newItemPosition].isFavorite) {
                "isFavorite"
            } else {
                super.getChangePayload(oldItemPosition, newItemPosition)
            }
        }
    }
}
