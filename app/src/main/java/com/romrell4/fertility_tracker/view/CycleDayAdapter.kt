package com.romrell4.fertility_tracker.view

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.romrell4.fertility_tracker.R
import com.romrell4.fertility_tracker.databinding.ViewHolderChartDayBinding
import com.romrell4.fertility_tracker.domain.Cycle
import java.time.format.DateTimeFormatter

class CycleDayAdapter : ListAdapter<Cycle.Day, CycleDayViewHolder>(object : DiffUtil.ItemCallback<Cycle.Day>() {
    override fun areItemsTheSame(oldItem: Cycle.Day, newItem: Cycle.Day): Boolean = oldItem.dayOfCycle == newItem.dayOfCycle
    override fun areContentsTheSame(oldItem: Cycle.Day, newItem: Cycle.Day): Boolean = oldItem == newItem
}) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CycleDayViewHolder {
        val binding = ViewHolderChartDayBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return CycleDayViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CycleDayViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

}

class CycleDayViewHolder(private val binding: ViewHolderChartDayBinding) : RecyclerView.ViewHolder(binding.root) {
    fun bind(day: Cycle.Day) {
        binding.dayOfCycleText.text = day.dayOfCycle.toString()
        binding.dateText.text = day.symptomEntry.date.format(DateTimeFormatter.ofPattern("M/d"))
        binding.stampImage.setBackgroundResource(R.drawable.ic_circle_green_24) //TODO
        binding.sensationText.text = "S" //TODO
    }
}
