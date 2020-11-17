package com.romrell4.fertility_tracker.view

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isInvisible
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.romrell4.fertility_tracker.R
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
        binding.dateText.text = day.date
        binding.stampImage.setBackgroundResource(day.stampRes)
        binding.stampImageWrapper.setBackgroundColor(
            itemView.context.getColor(if (day.peakMucusRange) R.color.chart_peak_range_background else android.R.color.transparent)
        )
        binding.layout.setBackgroundColor(
            itemView.context.getColor(if (day.selected) R.color.chart_selected else android.R.color.transparent)
        )
        binding.stampImage.setOnClickListener {
            day.dialogMessage?.let { dialogMessage ->
                MaterialAlertDialogBuilder(itemView.context)
                    .setTitle(day.dialogTitle)
                    .setMessage(dialogMessage)
                    .setPositiveButton(android.R.string.ok, null)
                    .show()
            }
        }
        binding.sensationText.text = day.sensations

        binding.notesImage.isInvisible = day.notes == null
        binding.notesImage.setOnClickListener {
            day.notes?.let {
                MaterialAlertDialogBuilder(itemView.context)
                    .setTitle(itemView.context.getString(R.string.notes_dialog_title, day.date))
                    .setMessage(it)
                    .setPositiveButton(android.R.string.ok, null)
                    .show()
            }
        }
    }
}
