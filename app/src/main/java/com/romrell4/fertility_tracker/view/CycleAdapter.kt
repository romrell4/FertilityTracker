package com.romrell4.fertility_tracker.view

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.romrell4.fertility_tracker.R
import com.romrell4.fertility_tracker.databinding.ViewHolderChartCycleBinding
import com.romrell4.fertility_tracker.domain.Cycle

class CycleAdapter : ListAdapter<Cycle, CycleViewHolder>(object : DiffUtil.ItemCallback<Cycle>() {
    override fun areItemsTheSame(oldItem: Cycle, newItem: Cycle): Boolean = oldItem.cycleNumber == newItem.cycleNumber
    override fun areContentsTheSame(oldItem: Cycle, newItem: Cycle): Boolean = oldItem == newItem
}) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CycleViewHolder {
        val binding = ViewHolderChartCycleBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return CycleViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CycleViewHolder, position: Int) {
        holder.bind(getItem(position))
    }
}

class CycleViewHolder(private val binding: ViewHolderChartCycleBinding) : RecyclerView.ViewHolder(binding.root) {
    private val adapter = CycleDayAdapter()

    fun bind(cycle: Cycle) {
        binding.cycleNumberText.text = itemView.context.getString(R.string.cycleNumber, cycle.cycleNumber)
        binding.daysRecyclerView.also {
            it.adapter = adapter
            it.layoutManager = LinearLayoutManager(itemView.context, LinearLayoutManager.HORIZONTAL, false)
        }
        adapter.submitList(cycle.days)
    }
}