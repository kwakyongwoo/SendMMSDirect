package com.dyddyd.sendmms.main.history.viewholder

import androidx.recyclerview.widget.RecyclerView
import com.dyddyd.sendmms.databinding.ItemHistoryBinding
import com.dyddyd.sendmms.repository.data.History

class HistoryViewHolder(private val binding: ItemHistoryBinding): RecyclerView.ViewHolder(binding.root) {
    fun onBind(item: History) {
        binding.item = item
    }
}