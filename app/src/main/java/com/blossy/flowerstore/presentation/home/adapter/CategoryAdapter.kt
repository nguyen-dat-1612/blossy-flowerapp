package com.blossy.flowerstore.presentation.home.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.blossy.flowerstore.databinding.ItemCategoryBinding
import com.blossy.flowerstore.domain.model.Category
import com.bumptech.glide.Glide

class CategoryAdapter(
    private val onItemClick: (Category) -> Unit
) : RecyclerView.Adapter<CategoryAdapter.CategoryViewHolder>(){

    private var categoryList = mutableListOf<Category>()

    inner class CategoryViewHolder(private val binding: ItemCategoryBinding) : RecyclerView.ViewHolder(binding.root){
        fun bind(category: Category){
            binding.categoryName.text = category.name
            Glide.with(binding.categoryImage.context)
                .load(category.image)
                .into(binding.categoryImage)
            binding.root.setOnClickListener {
                onItemClick(category)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoryViewHolder {
        val binding = ItemCategoryBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return CategoryViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return categoryList.size
    }

    override fun onBindViewHolder(holder: CategoryViewHolder, position: Int) {
        holder.bind(categoryList[position])
    }

    fun submitList(categories: List<Category>) {
        categoryList = categories.toMutableList()
        notifyDataSetChanged()
    }


}