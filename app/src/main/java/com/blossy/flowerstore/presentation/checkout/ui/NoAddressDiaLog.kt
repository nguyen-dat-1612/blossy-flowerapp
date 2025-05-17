package com.blossy.flowerstore.presentation.checkout.ui

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import com.blossy.flowerstore.databinding.DialogNoAddressBinding

class NoAddressDiaLog (context: Context, private val onCancel: () -> Unit, private val onConfirm: () -> Unit) : Dialog(context) {

    private lateinit var binding: DialogNoAddressBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DialogNoAddressBinding.inflate(LayoutInflater.from(context))
        setContentView(binding.root)

        window?.setBackgroundDrawableResource(android.R.color.transparent)
        setCancelable(false)

        binding.btnCancel.setOnClickListener {
            onCancel()
            dismiss()
        }

        binding.btnAddAddress.setOnClickListener {
            onConfirm()
            dismiss()
        }

    }

    override fun onStart() {
        super.onStart()
        window?.setLayout(
            (context.resources.displayMetrics.widthPixels * 0.90).toInt(),
            android.view.ViewGroup.LayoutParams.WRAP_CONTENT
        )
    }


}