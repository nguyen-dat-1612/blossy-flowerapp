package com.blossy.flowerstore.presentation.category.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.blossy.flowerstore.databinding.ItemCategoryListBinding
import com.blossy.flowerstore.domain.model.CategoryModel
import com.blossy.flowerstore.utils.loadImage

class CategoryListAdapter (
    private val onItemClick: (CategoryModel) -> Unit
) : RecyclerView.Adapter<CategoryListAdapter.ViewHolder>() {

    private var categories: List<CategoryModel> = emptyList()


    inner class ViewHolder(private val binding: ItemCategoryListBinding) : RecyclerView.ViewHolder(binding.root){
        fun bind(category: CategoryModel) {
            binding.run {
                categoryImage.loadImage(category.image)
                categoryName.text = category.name
                categoryQuantity.text = category.productCount.toString() + " items"
                root.setOnClickListener {
                    onItemClick(category)
                }
            }

        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(ItemCategoryListBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun getItemCount(): Int {
        return categories.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(categories[position])
    }

    fun submitList(newCategories: List<CategoryModel>) {
        val diffResult = DiffUtil.calculateDiff(CategoryListCallback(categories, newCategories))
        categories = newCategories
        diffResult.dispatchUpdatesTo(this)
    }

    class CategoryListCallback(
        private val oldList: List<CategoryModel>,
        private val newList: List<CategoryModel>
    ) : DiffUtil.Callback() {

        override fun getOldListSize(): Int  = oldList.size
        override fun getNewListSize(): Int = newList.size

        override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            return oldList[oldItemPosition].id == newList[newItemPosition].id
        }

        override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            return oldList[oldItemPosition] == newList[newItemPosition]
        }
    }
}