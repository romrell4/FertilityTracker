package com.romrell4.fertility_tracker.view

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isInvisible
import androidx.core.view.isVisible
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.romrell4.fertility_tracker.R
import com.romrell4.fertility_tracker.databinding.ViewHolderChartDayBinding
import com.romrell4.fertility_tracker.domain.ChartRow
import com.romrell4.fertility_tracker.viewmodel.ChartViewState

class CycleDayAdapter : ListAdapter<ChartViewState.CycleView.DayView, CycleDayViewHolder>(
    object : DiffUtil.ItemCallback<ChartViewState.CycleView.DayView>() {
        override fun areItemsTheSame(
            oldItem: ChartViewState.CycleView.DayView,
            newItem: ChartViewState.CycleView.DayView
        ): Boolean = oldItem.dayOfCycle == newItem.dayOfCycle

        override fun areContentsTheSame(
            oldItem: ChartViewState.CycleView.DayView,
            newItem: ChartViewState.CycleView.DayView
        ): Boolean = oldItem == newItem
    }
) {
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
        binding.layout.setBackgroundColor(
            itemView.context.getColor(if (day.selected) R.color.chart_selected else android.R.color.transparent)
        )

        day.hiddenRows.forEach {
            it.toView().isVisible = false
        }

        binding.dateText.text = day.date
        binding.stampImageWrapper.setBackgroundColor(
            itemView.context.getColor(if (day.stampHighlighted) R.color.chart_peak_range_background else android.R.color.transparent)
        )
        binding.stampImage.apply {
            setBackgroundResource(day.stampRes)
            setOnClickListener {
                day.dialogMessage?.let { dialogMessage ->
                    MaterialAlertDialogBuilder(itemView.context)
                        .setTitle(day.dialogTitle)
                        .setMessage(dialogMessage)
                        .setPositiveButton(android.R.string.ok, null)
                        .show()
                }
            }
        }
        binding.sensationText.text = day.sensations
        binding.observationImage.setBackgroundResource(day.observationRes ?: 0)
        binding.bleedingImage.setBackgroundResource(day.bleedingRes ?: 0)
        binding.sexImage.setBackgroundResource(day.sexRes ?: 0)
        binding.moodText.text = day.moodEmoji
        binding.energyImage.setBackgroundResource(day.energyRes ?: 0)

        binding.notesImage.apply {
            isInvisible = day.notes == null
            setOnClickListener {
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

    fun ChartRow.toView(): View = when (this) {
        ChartRow.DATE -> binding.dateText
        ChartRow.STAMP -> binding.stampImageWrapper
        ChartRow.SENSATION -> binding.sensationText
        ChartRow.OBSERVATION -> binding.observationImageWrapper
        ChartRow.BLEEDING -> binding.bleedingImageWrapper
        ChartRow.SEX -> binding.sexImageWrapper
        ChartRow.MOOD -> binding.moodText
        ChartRow.ENERGY -> binding.energyImageWrapper
        ChartRow.NOTES -> binding.notesImage
    }
}
