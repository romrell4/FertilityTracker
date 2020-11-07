package com.romrell4.fertility_tracker.view

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.PersistableBundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.commit
import com.romrell4.fertility_tracker.R
import com.romrell4.fertility_tracker.databinding.ActivityMainBinding
import kotlinx.coroutines.ExperimentalCoroutinesApi

private const val FRAGMENT_STATE_KEY = "fragmentState"

@ExperimentalCoroutinesApi
class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private var fragmentState: FragmentState? = null
        set(new) {
            //Only replace the fragment if it's actually changing
            if (field != new && new != null) {
                supportFragmentManager.commit {
                    replace(R.id.frame, new.getFragment())
                }
            }
            field = new
        }
    private val dataEntryFragment by lazy { DataEntryFragment.newInstance() }
    private val chartFragment by lazy { ChartFragment.newInstance() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //Set the default fragment
        fragmentState = FragmentState.DATA_ENTRY

        binding.bottomNav.setOnNavigationItemSelectedListener {
            fragmentState = when (it.itemId) {
                R.id.bottom_nav_entry -> FragmentState.DATA_ENTRY
                R.id.bottom_nav_chart -> FragmentState.CHART
                else -> throw NotImplementedError()
            }
            true
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)

        outState.putSerializable(FRAGMENT_STATE_KEY, fragmentState)
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)

        fragmentState = savedInstanceState.getSerializable(FRAGMENT_STATE_KEY) as? FragmentState
    }

    enum class FragmentState {
        DATA_ENTRY,
        CHART
    }

    fun FragmentState.getFragment(): Fragment = when(this) {
        FragmentState.DATA_ENTRY -> dataEntryFragment
        FragmentState.CHART -> chartFragment
    }
}