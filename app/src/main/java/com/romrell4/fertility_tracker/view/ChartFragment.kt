package com.romrell4.fertility_tracker.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.romrell4.fertility_tracker.databinding.FragmentLiveChartBinding
import com.romrell4.fertility_tracker.viewmodel.ChartViewModel
import com.romrell4.fertility_tracker.viewmodel.ChartViewState
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

@ExperimentalCoroutinesApi
class ChartFragment : Fragment() {
    private val viewModel: ChartViewModel by viewModels {
        defaultViewModelProviderFactory
    }
    private val adapter = CycleAdapter()

    private lateinit var binding: FragmentLiveChartBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentLiveChartBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        lifecycleScope.launch {
            viewModel.viewStateFlow.collect {
                render(it)
            }
        }

        binding.cyclesRecyclerView.also {
            it.adapter = adapter
            it.layoutManager = LinearLayoutManager(context)
        }

        viewModel.loadAllEntries()
    }

    private fun render(viewState: ChartViewState) {
        adapter.submitList(viewState.cycles)
    }

    companion object {
        fun newInstance(): ChartFragment {
            return ChartFragment()
        }
    }
}