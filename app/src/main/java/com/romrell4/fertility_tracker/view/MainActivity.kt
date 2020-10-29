package com.romrell4.fertility_tracker.view

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.romrell4.fertility_tracker.R
import com.romrell4.fertility_tracker.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private val dataEntryFragment by lazy { DataEntryFragment() }
    private val liveChartFragment by lazy { LiveChartFragment() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.bottomNav.setOnNavigationItemSelectedListener {
            supportFragmentManager.beginTransaction().replace(R.id.frame, when (it.itemId) {
                R.id.bottom_nav_entry -> dataEntryFragment
                R.id.bottom_nav_chart -> liveChartFragment
                else -> throw NotImplementedError()
            }).commit()
            true
        }
    }
}