package com.blossy.flowerstore.presentation.order.ui

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.Toast
import com.blossy.flowerstore.databinding.DialogConfirmOrderReceivedBinding

class ConfirmOrderReceivedDialog(context: Context, private val onConfirm: () -> Unit) : Dialog(context) {

    private lateinit var binding: DialogConfirmOrderReceivedBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DialogConfirmOrderReceivedBinding.inflate(LayoutInflater.from(context))
        setContentView(binding.root)

        // Set dialog properties
        window?.setBackgroundDrawableResource(android.R.color.transparent)
        setCancelable(false)

        // Cancel button click
        binding.cancelButton.setOnClickListener {
            dismiss()
        }

        // Confirm button click
        binding.confirmButton.setOnClickListener {
            onConfirm()
            Toast.makeText(context, "Order confirmed as received", Toast.LENGTH_SHORT).show()
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