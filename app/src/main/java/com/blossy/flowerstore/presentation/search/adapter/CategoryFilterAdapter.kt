package com.blossy.flowerstore.presentation.search.adapter

import android.content.res.ColorStateList
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.blossy.flowerstore.R
import com.blossy.flowerstore.databinding.ItemFilterCategoryBinding
import com.blossy.flowerstore.domain.model.CategoryModel

class CategoryFilterAdapter(
    private val categories: List<CategoryModel>,
    private val selectedCategories: Set<String>,
    private val onCategorySelected: (String) -> Unit
) : RecyclerView.Adapter<CategoryFilterAdapter.ViewHolder>() {

    inner class ViewHolder(private val binding: ItemFilterCategoryBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(category: CategoryModel) {
            val isSelected = selectedCategories.contains(category.id)
            binding.categoryButton.text = category.name

            val context = binding.root.context
            val selectedColor = context.getColor(R.color.pink_primary)
            val defaultColor = context.getColor(R.color.gray_e9edef)


            binding.categoryButton.setOnClickListener {
                binding.categoryButton.backgroundTintList = ColorStateList.valueOf(selectedColor)
                binding.categoryButton.setTextColor(context.getColor(R.color.white))
//                    android.content.res.ColorStateList.valueOf(if (isSelected) selectedColor else defaultColor)
                onCategorySelected(category.id)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemFilterCategoryBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(categories[position])
    }

    override fun getItemCount() = categories.size
}
