package com.blossy.flowerstore.utils


import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

import androidx.lifecycle.repeatOnLifecycle
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.distinctUntilChanged

fun <T> LifecycleOwner.collectState(
    state: StateFlow<T>,
    action: (T) -> Unit
) {
    lifecycleScope.launch {
        lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
            state.collectLatest { action(it) }
        }
    }
}

inline fun <T> MutableStateFlow<T>.safeUpdate(update: (T) -> T) {
    val old = value
    val new = update(old)
    if (old != new) value = new
}