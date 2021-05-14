package com.romrell4.fertility_tracker.view

import android.content.Context
import android.widget.TextView
import androidx.core.view.isVisible
import com.github.mikephil.charting.components.MarkerView
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.highlight.Highlight
import com.github.mikephil.charting.utils.MPPointF
import com.romrell4.fertility_tracker.R
import com.romrell4.fertility_tracker.viewmodel.ChartViewState

private const val Y_OFFSET_PX = 40

class TemperatureMarkerView(context: Context) : MarkerView(context, R.layout.temperature_marker_view) {
    private val temperatureText = rootView.findViewById<TextView>(R.id.temperatureText)
    private val abnormalText = rootView.findViewById<TextView>(R.id.abnormalText)

    override fun refreshContent(e: Entry?, highlight: Highlight?) {
        val dayView = e?.data as? ChartViewState.CycleView.DayView
        temperatureText.text = dayView?.temperature?.value?.toString()

        val abnormalNotes = dayView?.temperature?.abnormalNotes?.takeIf { it.isNotBlank() }
        abnormalText.isVisible = abnormalNotes != null
        abnormalText.text = abnormalNotes

        super.refreshContent(e, highlight)
    }

    override fun getOffset(): MPPointF {
        return MPPointF.getInstance(-(width / 2).toFloat(), -(height + Y_OFFSET_PX).toFloat())
    }
}
