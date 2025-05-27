package com.blossy.flowerstore.utils

import android.content.Context
import android.os.SystemClock
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Toast
import com.blossy.flowerstore.R
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions

fun View.setOnSingleClickListener(interval: Long = 600L, onClick: (View) -> Unit) {
    var lastClickTime = 0L
    setOnClickListener {
        if (SystemClock.elapsedRealtime() - lastClickTime >= interval) {
            lastClickTime = SystemClock.elapsedRealtime()
            onClick(it)
        }
    }
}

fun ViewGroup.setChildrenEnabled(enabled: Boolean) {
    for (i in 0 until childCount) {
        val child = getChildAt(i)
        if (child is ViewGroup) {
            child.setChildrenEnabled(enabled)
        } else {
            child.isEnabled = enabled
        }
    }
}

fun Context.toast(message: String) {
    Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
}

fun ImageView.loadImage(url: String?) {
    Glide.with(this.context)
        .load(url)
        .apply(
            RequestOptions()
                .placeholder(R.drawable.placeholder)
                .error(R.drawable.error_image)
                .centerCrop()
        )
        .into(this)
}