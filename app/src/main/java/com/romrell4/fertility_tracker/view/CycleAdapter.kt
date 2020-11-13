package com.romrell4.fertility_tracker.view

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.components.YAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.highlight.Highlight
import com.github.mikephil.charting.listener.OnChartValueSelectedListener
import com.github.mikephil.charting.renderer.XAxisRenderer
import com.github.mikephil.charting.utils.Utils
import com.romrell4.fertility_tracker.R
import com.romrell4.fertility_tracker.databinding.ViewHolderChartCycleBinding
import com.romrell4.fertility_tracker.viewmodel.ChartViewState
import kotlin.math.*

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
        //General UI
        description = null
        legend.isEnabled = false
        layoutParams = layoutParams.also {
            it.width = itemView.context.resources.getDimensionPixelSize(R.dimen.chart_cell_width) * cycle.days.size + CHART_SIZE_OFFSET_PX
        }

        //Add data
        data = cycle.getLineData()

        //Turn off pinching/scrolling (since we're using our own scrollview)
        isDragEnabled = false
        isScaleXEnabled = false
        isScaleYEnabled = false
        isDoubleTapToZoomEnabled = false

        //Configure X-axis
        xAxis.position = XAxis.XAxisPosition.BOTTOM
        xAxis.axisMinimum = 1f
        xAxis.axisMaximum = cycle.days.size.toFloat()
        xAxis.setLabelCount(cycle.days.size, true)
        setXAxisRenderer( //Hack to make the x axis actually layout all the days
            CustomXAxisRenderer(viewPortHandler, xAxis, getTransformer(YAxis.AxisDependency.LEFT), cycle.days.size)
        )

        //Configure Y-axis
        axisLeft.setPosition(YAxis.YAxisLabelPosition.INSIDE_CHART)
        //TODO: Figure out if Jess really wants this
//        axisLeft.axisMinimum = 94.5f
//        axisLeft.axisMaximum = 100f
//        axisLeft.labelCount = ((axisLeft.axisMaximum - axisLeft.axisMinimum) / .1).roundToInt() + 1
        axisRight.isEnabled = false

        //TODO: V3 Implement coverline in CycleView
//        axisRight.addLimitLine(LimitLine(98f, "Coverline"))

        //Set up interactions
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
        }, "Temperature").apply {
            lineWidth = 2f
            color = itemView.context.getColor(R.color.pink_light)
            circleRadius = 4f
            setDrawCircleHole(false)
            setCircleColor(itemView.context.getColor(R.color.pink))
        }))
    }

    companion object {
        private const val CHART_SIZE_OFFSET_PX = 10
    }
}