package com.romrell4.fertility_tracker.view

import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ScrollView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.LimitLine
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.components.YAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.highlight.Highlight
import com.github.mikephil.charting.listener.OnChartValueSelectedListener
import com.romrell4.fertility_tracker.R
import com.romrell4.fertility_tracker.databinding.ViewHolderChartCycleBinding
import com.romrell4.fertility_tracker.viewmodel.ChartViewState

class CycleAdapter : ListAdapter<ChartViewState.CycleView, CycleViewHolder>(
    object : DiffUtil.ItemCallback<ChartViewState.CycleView>() {
        override fun areItemsTheSame(oldItem: ChartViewState.CycleView, newItem: ChartViewState.CycleView): Boolean =
            oldItem.cycleNumber == newItem.cycleNumber

        override fun areContentsTheSame(oldItem: ChartViewState.CycleView, newItem: ChartViewState.CycleView): Boolean =
            oldItem == newItem
    }
) {
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

    fun bind(cycle: ChartViewState.CycleView) {
        binding.cycleNumberText.text = itemView.context.getString(R.string.cycleNumber, cycle.cycleNumber)

        //Scroll to the end
        binding.chartScrollView.post {
            binding.chartScrollView.scrollTo(binding.chartScrollView.right, 0)
        }

        binding.daysRecyclerView.also {
            it.adapter = adapter
            it.layoutManager = LinearLayoutManager(itemView.context, LinearLayoutManager.HORIZONTAL, false)
        }
        adapter.submitList(cycle.days)

        binding.chart.setup(cycle)
    }

    private fun LineChart.setup(cycle: ChartViewState.CycleView) {
        data = cycle.getLineData()
        //Turn off pinching/scrolling (since we're using our own scrollview)
        description = null
        isDragEnabled = false
        isScaleXEnabled = false
        isScaleYEnabled = false
        isDoubleTapToZoomEnabled = false
        axisLeft.setDrawLabels(false)
        axisRight.setDrawLabels(false)
        axisRight.setPosition(YAxis.YAxisLabelPosition.INSIDE_CHART)
        axisLeft.granularity = 0.1f
        xAxis.position = XAxis.XAxisPosition.BOTTOM
        xAxis.axisMinimum = 1f
        xAxis.axisMaximum = cycle.days.size.toFloat()
        xAxis.setLabelCount(cycle.days.size, true)
        legend.isEnabled = false
        layoutParams = layoutParams.also {
            it.width = TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP,
                24.5f, //This value matches the @dimens/chart_cell_width + .5 for the margins between cells
                resources.displayMetrics
            ).toInt() * cycle.days.size + 1
        }
        //TODO: V3 Implement coverline in CycleView
//        axisRight.addLimitLine(LimitLine(98f, "Coverline"))

        setOnChartValueSelectedListener(object : OnChartValueSelectedListener {
            override fun onValueSelected(e: Entry?, h: Highlight?) {
                //TODO: Dismiss any existing markers, and pop up a new marker
            }

            override fun onNothingSelected() {
                //TODO: Dismiss marker
            }
        })
    }

    private fun ChartViewState.CycleView.getLineData(): LineData? {
        return LineData(listOf(LineDataSet(days.mapIndexedNotNull { index, dayView ->
            dayView.temperature?.toFloat()?.let {
                Entry(index.toFloat() + 1, it)
            }
        }, "Temperature")))
    }
}