package com.blossy.flowerstore.presentation.shippingAddress.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.blossy.flowerstore.databinding.ItemAddressBinding
import com.blossy.flowerstore.domain.model.Address

class AddressAdapter(
    private val fromCheckout: Boolean,
    private val onAddressSelected: (Int) -> Unit,
    private val onEditClick: (Address) -> Unit
) : RecyclerView.Adapter<AddressAdapter.AddressViewHolder>() {

    private var addresses: List<Address> = emptyList()
    private var selectedPosition = -1

    fun submitList(newAddresses: List<Address>) {
        val diffResult = DiffUtil.calculateDiff(AddressDiffCallback(addresses, newAddresses))
        addresses = newAddresses
        diffResult.dispatchUpdatesTo(this)
    }


    inner class AddressViewHolder(val binding: ItemAddressBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(address: Address, position: Int) {
            binding.nameTextView.text = address.name
            binding.phoneTextView.text = address.phone
            binding.addressTextView.text = address.address
            binding.defaultIndicator.visibility = if (address.isDefault) View.VISIBLE else View.GONE

            if (fromCheckout) {
                binding.addressRadioButton.visibility = View.VISIBLE
                binding.addressRadioButton.isChecked = position == selectedPosition
                binding.root.setOnClickListener {
                    selectedPosition = position
                    onAddressSelected(position)
                    notifyDataSetChanged()
                }
            } else {
                binding.addressRadioButton.visibility = View.GONE
                binding.root.setOnClickListener(null)
            }

            binding.editBtn.setOnClickListener {
                onEditClick(address)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AddressViewHolder {
        val binding = ItemAddressBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return AddressViewHolder(binding)
    }

    override fun onBindViewHolder(holder: AddressViewHolder, position: Int) {
        holder.bind(addresses[position], position)
    }

    override fun getItemCount(): Int = addresses.size

    fun getSelectedAddress(): Address? {
        return if (selectedPosition != -1) addresses[selectedPosition] else null
    }

    class AddressDiffCallback(
        private val oldList: List<Address>,
        private val newList: List<Address>
    ) : DiffUtil.Callback() {

        override fun getOldListSize(): Int = oldList.size
        override fun getNewListSize(): Int = newList.size

        override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            return oldList[oldItemPosition].id == newList[newItemPosition].id
        }

        override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            return oldList[oldItemPosition] == newList[newItemPosition]
        }

        override fun getChangePayload(oldItemPosition: Int, newItemPosition: Int): Any? {
            return if (oldList[oldItemPosition].isDefault != newList[newItemPosition].isDefault) {
                "isDefault"
            } else {
                super.getChangePayload(oldItemPosition, newItemPosition)
            }
        }
    }
}