package com.romrell4.fertility_tracker.view

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.core.view.GestureDetectorCompat
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.romrell4.fertility_tracker.databinding.FragmentDataEntryBinding
import com.romrell4.fertility_tracker.support.OnSwipeTouchListener
import com.romrell4.fertility_tracker.viewmodel.DataEntryViewModel
import com.romrell4.fertility_tracker.viewmodel.DataEntryViewState
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import java.time.format.DateTimeFormatter

private val DATE_FORMATTER = DateTimeFormatter.ofPattern("EEEE, MMM d")

@ExperimentalCoroutinesApi
class DataEntryFragment : Fragment() {
    private val viewModel: DataEntryViewModel by viewModels {
        defaultViewModelProviderFactory
    }

    private lateinit var binding: FragmentDataEntryBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentDataEntryBinding.inflate(inflater, container, false)
        return binding.root
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        lifecycleScope.launch {
            viewModel.viewStateFlow.collect {
                render(it)
            }
        }

        binding.previousDateView.setOnClickListener {
            viewModel.previousDate()
        }

        binding.nextDateView.setOnClickListener {
            viewModel.nextDate()
        }
    }

    private fun render(viewState: DataEntryViewState) {
        binding.currentDateView.text = DATE_FORMATTER.format(viewState.currentDate)
        binding.previousDateView.text = DATE_FORMATTER.format(viewState.previousDate)
        binding.nextDateView.text = DATE_FORMATTER.format(viewState.nextDate)
    }
}