package com.romrell4.fertility_tracker.view

import android.content.Context
import com.github.mikephil.charting.components.MarkerView
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.highlight.Highlight
import com.github.mikephil.charting.utils.MPPointF
import com.romrell4.fertility_tracker.R
import com.romrell4.fertility_tracker.viewmodel.ChartViewState
import kotlinx.android.synthetic.main.temperature_marker_view.view.*

private const val Y_OFFSET_PX = 20

class TemperatureMarkerView(context: Context) : MarkerView(context, R.layout.temperature_marker_view) {
    private val temperatureText = rootView.temperatureText

    override fun refreshContent(e: Entry?, highlight: Highlight?) {
        temperatureText.text = (e?.data as? ChartViewState.CycleView.TemperatureView)?.value?.toString()

        super.refreshContent(e, highlight)
    }

    override fun getOffset(): MPPointF {
        return MPPointF.getInstance(-(width / 2).toFloat(), -(height + Y_OFFSET_PX).toFloat())
    }
}