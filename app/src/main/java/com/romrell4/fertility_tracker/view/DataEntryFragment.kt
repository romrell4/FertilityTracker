package com.romrell4.fertility_tracker.view

import android.app.DatePickerDialog
import android.content.DialogInterface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import com.romrell4.fertility_tracker.databinding.FragmentDataEntryBinding
import com.romrell4.fertility_tracker.viewmodel.DataEntryViewModel
import com.romrell4.fertility_tracker.viewmodel.DataEntryViewState
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter

private val DATE_FORMATTER = DateTimeFormatter.ofPattern("EEEE, MMM d")

@ExperimentalCoroutinesApi
class DataEntryFragment : MainFragment() {
    private val viewModel: DataEntryViewModel by viewModels {
        defaultViewModelProviderFactory
    }

    private lateinit var binding: FragmentDataEntryBinding
    private val viewPagerAdapter by lazy { DayViewPagerAdapter(this) }
    private var onPageChangeCallback: ViewPager2.OnPageChangeCallback? = null
        set(value) {
            field?.let {
                binding.dayViewPager.unregisterOnPageChangeCallback(it)
            }
            binding.dayViewPager.currentItem = 1
            value?.let {
                binding.dayViewPager.registerOnPageChangeCallback(it)
            }
            field = value
        }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentDataEntryBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        lifecycleScope.launch {
            viewModel.viewStateFlow.collect {
                render(it)
            }
        }

        binding.dayViewPager.adapter = viewPagerAdapter
    }

    override fun reload() {
        viewModel.reload()
    }

    private fun render(viewState: DataEntryViewState) {
        //Date
        binding.currentDateView.text = DATE_FORMATTER.format(viewState.currentDate)
        binding.currentDateView.setOnClickListener {
            DatePickerDialog(
                requireContext(),
                { _, year, month, dayOfMonth ->
                    viewModel.selectDate(LocalDate.of(year, month + 1, dayOfMonth))
                },
                viewState.currentDate.year, viewState.currentDate.monthValue, viewState.currentDate.dayOfMonth
            ).apply {
                datePicker.maxDate = LocalDate.now().atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli()
                setButton(DialogInterface.BUTTON_NEUTRAL, "Today") { _, _ ->
                    viewModel.selectDate(LocalDate.now())
                }
            }.show()
        }
        binding.previousDateButton.setOnClickListener { viewModel.selectDate(viewState.previousDate) }
        binding.nextDateButton.visibility = if (viewState.nextDate != null) View.VISIBLE else View.INVISIBLE
        binding.nextDateButton.setOnClickListener { viewState.nextDate?.let { viewModel.selectDate(it) } }

        viewPagerAdapter.dates = viewState.dates

//        binding.dayViewPager.unregisterOnPageChangeCallback(onPageChangeCallback)
//        binding.dayViewPager.currentItem = 1 // Always set it to the middle one

        onPageChangeCallback = object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)

                post {
                    viewModel.selectDate(viewPagerAdapter.dates[position])
                }
            }
        }
    }

    class DayViewPagerAdapter(fragment: Fragment) : FragmentStateAdapter(fragment) {
        var dates: List<LocalDate> = emptyList()
            set(value) {
                if (field != value) {
                    notifyDataSetChanged()
                }
                field = value
            }

        override fun getItemCount() = dates.size

        override fun createFragment(position: Int) = SingleDayDataEntryFragment.newInstance(dates[position])
    }

    companion object {
        fun newInstance(): DataEntryFragment {
            return DataEntryFragment()
        }
    }
}
