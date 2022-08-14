package com.dinder.rihlabus.adapters

import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat.startActivity
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.dinder.rihlabus.data.model.Seat
import com.dinder.rihlabus.databinding.SeatDetailItemListBinding

class SeatAdapter : ListAdapter<Seat, SeatAdapter.SeatHolder>(SeatDiffUtils()) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SeatHolder {
        return SeatHolder(
            SeatDetailItemListBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: SeatHolder, position: Int) {
        val seat = getItem(position)
        holder.bind(seat)
    }

    class SeatHolder(private val binding: SeatDetailItemListBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(seat: Seat) {
            binding.seat = seat
            binding.callPassengerButton.setOnClickListener {
                // Call Passenger Number
                val intent = Intent(Intent.ACTION_DIAL)
                intent.data = Uri.parse("tel:${seat.passengerPhoneNumber}")
                startActivity(itemView.context, intent, null)
            }
        }
    }
}

class SeatDiffUtils : DiffUtil.ItemCallback<Seat>() {
    override fun areItemsTheSame(oldItem: Seat, newItem: Seat): Boolean {
        return oldItem == newItem
    }

    override fun areContentsTheSame(oldItem: Seat, newItem: Seat): Boolean {
        return oldItem.number == newItem.number
    }
}
