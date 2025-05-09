package com.blossy.flowerstore.presentation.productDetail.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.blossy.flowerstore.R
import com.blossy.flowerstore.databinding.ItemImageBinding
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy

class ProductImagePagerAdapter(private val images: List<String>) :
    RecyclerView.Adapter<ProductImagePagerAdapter.ImageViewHolder>() {

    inner class ImageViewHolder(private val binding: ItemImageBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(imageUrl: String, position: Int, totalImages: Int) {
            // Load image with Glide, including placeholder and error handling
            Glide.with(binding.imageView.context)
                .load(imageUrl)
                .thumbnail(0.25f) // Load a smaller thumbnail first for faster display
                .diskCacheStrategy(DiskCacheStrategy.ALL) // Cache both original and thumbnail
//                .placeholder(R.drawable.placeholder_image) // Placeholder while loading
//                .error(R.drawable.error_image) // Error image if loading fails
                .into(binding.imageView)

            // Set accessibility description
            binding.imageView.contentDescription = "Product image ${position + 1} of $totalImages"

            // Add click listener for potential zoom or full-screen view
            binding.imageView.setOnClickListener {
                // Placeholder: Add logic for full-screen image view or zoom
                // For now, we can log the click or show a toast
                android.widget.Toast.makeText(
                    binding.imageView.context,
                    "Clicked on image ${position + 1}",
                    android.widget.Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImageViewHolder {
        val binding = ItemImageBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ImageViewHolder(binding)
    }

    override fun getItemCount(): Int = images.size

    override fun onBindViewHolder(holder: ImageViewHolder, position: Int) {
        holder.bind(images[position], position, images.size)
    }
}