package com.blossy.flowerstore.presentation.order.ui

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.Toast
import com.blossy.flowerstore.databinding.DialogCancelOrderBinding

class CancelOrderDialog(context: Context, private val onConfirm: (String) -> Unit) : Dialog(context) {

    private lateinit var binding: DialogCancelOrderBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DialogCancelOrderBinding.inflate(LayoutInflater.from(context))
        setContentView(binding.root)

        window?.setBackgroundDrawableResource(android.R.color.transparent)
        setCancelable(false)

        binding.cancelButton.setOnClickListener {
            dismiss()
        }

        binding.confirmButton.setOnClickListener {
            val reason = binding.reasonEditText.text.toString().trim()
            if (reason.isEmpty()) {
                Toast.makeText(context, "Please enter a reason", Toast.LENGTH_SHORT).show()
            } else {
                onConfirm(reason)
                dismiss()
            }
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