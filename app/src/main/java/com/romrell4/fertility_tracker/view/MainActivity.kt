package com.romrell4.fertility_tracker.view

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.fragment.app.Fragment
import com.romrell4.fertility_tracker.R
import com.romrell4.fertility_tracker.databinding.ActivityMainBinding
import kotlinx.coroutines.ExperimentalCoroutinesApi

@ExperimentalCoroutinesApi
class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private var currentFragment: Fragment? = null
        set(new) {
            //Only replace the fragment if it's actually changing
            if (field != new && new != null) {
                supportFragmentManager.beginTransaction().replace(R.id.frame, new).commit()
            }
            field = new
        }
    private val dataEntryFragment by lazy { DataEntryFragment() }
    private val liveChartFragment by lazy { LiveChartFragment() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //Set the default fragment
        currentFragment = dataEntryFragment

        binding.bottomNav.setOnNavigationItemSelectedListener {
            currentFragment = when (it.itemId) {
                R.id.bottom_nav_entry -> dataEntryFragment
                R.id.bottom_nav_chart -> liveChartFragment
                else -> throw NotImplementedError()
            }
            true
        }
    }
}