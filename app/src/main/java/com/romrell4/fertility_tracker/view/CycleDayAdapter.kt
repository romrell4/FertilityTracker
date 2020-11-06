package com.romrell4.fertility_tracker.view

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.romrell4.fertility_tracker.databinding.ViewHolderChartDayBinding
import com.romrell4.fertility_tracker.domain.Cycle
import com.romrell4.fertility_tracker.viewmodel.ChartViewState

class CycleDayAdapter : ListAdapter<ChartViewState.CycleView.DayView, CycleDayViewHolder>(object :
    DiffUtil.ItemCallback<ChartViewState.CycleView.DayView>() {
    override fun areItemsTheSame(
        oldItem: ChartViewState.CycleView.DayView,
        newItem: ChartViewState.CycleView.DayView
    ): Boolean = oldItem.dayOfCycle == newItem.dayOfCycle

    override fun areContentsTheSame(
        oldItem: ChartViewState.CycleView.DayView,
        newItem: ChartViewState.CycleView.DayView
    ): Boolean = oldItem == newItem
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
    fun bind(day: ChartViewState.CycleView.DayView) {
        binding.dayOfCycleText.text = day.dayOfCycle
        binding.dateText.text = day.date
        binding.stampImage.setBackgroundResource(day.stampRes)
        binding.sensationText.text = day.sensations
    }
}
