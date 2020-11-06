package com.romrell4.fertility_tracker.view

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.romrell4.fertility_tracker.R

class LiveChartFragment private constructor(): Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_live_chart, container, false)
    }

    companion object {
        fun newInstance(): LiveChartFragment {
            return LiveChartFragment()
        }
    }
}