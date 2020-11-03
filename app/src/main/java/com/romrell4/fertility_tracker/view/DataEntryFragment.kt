package com.romrell4.fertility_tracker.view

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.google.android.material.button.MaterialButton
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.romrell4.fertility_tracker.R
import com.romrell4.fertility_tracker.databinding.FragmentDataEntryBinding
import com.romrell4.fertility_tracker.domain.SymptomEntry
import com.romrell4.fertility_tracker.viewmodel.DataEntryViewModel
import com.romrell4.fertility_tracker.viewmodel.DataEntryViewState
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import java.time.format.DateTimeFormatter

private val DATE_FORMATTER = DateTimeFormatter.ofPattern("EEEE, MMM d")

@ExperimentalCoroutinesApi
class DataEntryFragment : Fragment(), MucusDialogCallback {
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
            viewModel.selectPreviousDate()
        }

        binding.nextDateView.setOnClickListener {
            viewModel.selectNextDate()
        }
    }

    override fun mucusSaved(mucus: SymptomEntry.Mucus) {
        viewModel.saveMucus(mucus)
    }

    private fun render(viewState: DataEntryViewState) {
        //Date
        binding.currentDateView.text = DATE_FORMATTER.format(viewState.currentDate)
        binding.previousDateView.text = DATE_FORMATTER.format(viewState.previousDate)
        binding.nextDateView.text = DATE_FORMATTER.format(viewState.nextDate)

        fun setupSymptomButton(button: MaterialButton, symptom: Any?) {
            button.setBackgroundColor(resources.getColor(if (symptom == null) R.color.gray else R.color.purple, null))
        }

        //Buttons
        setupSymptomButton(binding.sensationsButton, viewState.sensation)
        setupSymptomButton(binding.mucusButton, viewState.mucus)
        setupSymptomButton(binding.bleedingButton, viewState.bleeding)
        setupSymptomButton(binding.sexButton, viewState.sex)

        fun <T : SymptomEntry.Symptom> MaterialButton.setUpRadioDialogListener(
            values: Array<T>,
            currentValue: T?,
            viewModelFunction: (T) -> Unit
        ) {
            setOnClickListener {
                var selectedValue = currentValue ?: values.first()
                MaterialAlertDialogBuilder(requireContext())
                    .setSingleChoiceItems(
                        values.map { it.displayText }.toTypedArray(),
                        values.indexOf(selectedValue)
                    ) { _, i ->
                        selectedValue = values[i]
                    }
                    .setPositiveButton(getString(R.string.alert_positive_text)) { _, _ ->
                        viewModelFunction(selectedValue)
                    }
                    .setNegativeButton(android.R.string.cancel, null)
                    .show()
            }
        }

        binding.sensationsButton.setUpRadioDialogListener(
            values = SymptomEntry.Sensation.values(),
            currentValue = viewState.sensation,
            viewModelFunction = viewModel::selectSensation
        )
        binding.bleedingButton.setUpRadioDialogListener(
            values = SymptomEntry.Bleeding.values(),
            currentValue = viewState.bleeding,
            viewModelFunction = viewModel::selectBleeding
        )
        binding.sexButton.setUpRadioDialogListener(
            values = SymptomEntry.Sex.values(),
            currentValue = viewState.sex,
            viewModelFunction = viewModel::selectSex
        )

        binding.mucusButton.setOnClickListener {
            MucusDialog.newInstance(viewState.mucus).also {
                it.setTargetFragment(this, 0)
            }.show(parentFragmentManager, null)
        }
    }
}