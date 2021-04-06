package com.romrell4.fertility_tracker.view

import android.view.LayoutInflater
import android.view.ViewGroup
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
import kotlin.math.roundToInt

class CycleAdapter(private val delegate: ExportDelegate) : ListAdapter<ChartViewState.CycleView, CycleViewHolder>(
    object : DiffUtil.ItemCallback<ChartViewState.CycleView>() {
        override fun areItemsTheSame(oldItem: ChartViewState.CycleView, newItem: ChartViewState.CycleView): Boolean =
            oldItem.cycleNumber == newItem.cycleNumber

        override fun areContentsTheSame(oldItem: ChartViewState.CycleView, newItem: ChartViewState.CycleView): Boolean =
            oldItem == newItem
    }
) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CycleViewHolder {
        val binding = ViewHolderChartCycleBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return CycleViewHolder(delegate, binding)
    }

    override fun onBindViewHolder(holder: CycleViewHolder, position: Int) {
        holder.bind(getItem(position))
    }
}

class CycleViewHolder(private val delegate: ExportDelegate, private val binding: ViewHolderChartCycleBinding) : RecyclerView.ViewHolder(binding.root) {
    private val adapter = CycleDayAdapter()

    fun bind(cycle: ChartViewState.CycleView) {
        binding.cycleNumberText.text = itemView.context.getString(R.string.cycleNumber, cycle.cycleNumber)
        binding.cycleDatesText.text = itemView.context.getString(R.string.cycle_dates, cycle.startDate, cycle.endDate)
        binding.cycleLengthText.text = itemView.context.getString(R.string.cycle_length, cycle.length)

        binding.exportButton.setOnClickListener {
            delegate.exportCycleView(binding, "Cycle #${cycle.cycleNumber} (${System.currentTimeMillis()})")
        }

        //Scroll to the end
        binding.chartScrollView.post {
            binding.chartScrollView.scrollTo(binding.chartScrollView.right, 0)
        }

        binding.daysRecyclerView.also {
            it.adapter = adapter
            it.layoutManager = LinearLayoutManager(itemView.context, LinearLayoutManager.HORIZONTAL, false)
        }
        adapter.submitList(cycle.days)

        //Set up chart highlighting when graph is selected
        binding.chart.setOnChartValueSelectedListener(object : OnChartValueSelectedListener {
            override fun onValueSelected(e: Entry?, h: Highlight?) {
                adapter.submitList(deselectAllDays().map {
                    if (it.date == (e?.data as? ChartViewState.CycleView.DayView)?.date) {
                        it.copy(selected = true)
                    } else it
                })
            }

            override fun onNothingSelected() {
                adapter.submitList(deselectAllDays())
            }

            private fun deselectAllDays(): List<ChartViewState.CycleView.DayView> {
                return cycle.days.map {
                    if (it.selected) {
                        it.copy(selected = false)
                    } else it
                }
            }
        })

        binding.chart.setup(cycle)
    }

    private fun LineChart.setup(cycle: ChartViewState.CycleView) {
        //General UI
        description = null
        legend.isEnabled = false
        layoutParams = layoutParams.also {
            it.width =
                itemView.context.resources.getDimensionPixelSize(R.dimen.chart_cell_width) * cycle.days.size + CHART_SIZE_OFFSET_PX
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
        axisLeft.setDrawLabels(false)
        axisLeft.axisMinimum = Y_AXIS_MIN.toFloat()
        axisLeft.axisMaximum = Y_AXIS_MAX.toFloat()
        axisLeft.labelCount = ((axisLeft.axisMaximum - axisLeft.axisMinimum) / Y_AXIS_STEP).roundToInt() + 1
        axisRight.isEnabled = false

        marker = TemperatureMarkerView(itemView.context)

        cycle.coverlineValue?.let {
            axisLeft.removeAllLimitLines()
            axisLeft.addLimitLine(LimitLine(it.toFloat(), null).also { line ->
                line.lineWidth = 2f
                line.lineColor = itemView.context.getColor(R.color.graph_coverline)
            })
        }
    }

    private fun ChartViewState.CycleView.getLineData(): LineData {
        return LineData(listOf(LineDataSet(days.mapIndexedNotNull { index, dayView ->
            dayView.temperature?.let {
                Entry(index.toFloat() + 1, it.value.toFloat(), dayView)
            }
        }, null).apply {
            lineWidth = 3f
            color = itemView.context.getColor(R.color.graph_line)
            circleRadius = 6f
            circleHoleRadius = 3f
            setDrawCircleHole(true)
            circleColors = days.mapNotNull { day ->
                day.temperature?.let { itemView.context.getColor(if (it.abnormal) R.color.graph_abnormal_day else R.color.graph_normal_day) }
            }
        }))
    }

    companion object {
        private const val CHART_SIZE_OFFSET_PX = 10
        private const val Y_AXIS_MIN = 95
        private const val Y_AXIS_MAX = 99
        private const val Y_AXIS_STEP = .2
    }
}

interface ExportDelegate {
    fun exportCycleView(binding: ViewHolderChartCycleBinding, filename: String)
}
