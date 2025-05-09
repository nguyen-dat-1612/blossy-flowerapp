package com.blossy.flowerstore.presentation.common


import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

fun <T> LifecycleOwner.collectState(
    state: StateFlow<T>,
    action: (T) -> Unit
) {
    lifecycleScope.launch {
        state.collectLatest { action(it) }
    }
}