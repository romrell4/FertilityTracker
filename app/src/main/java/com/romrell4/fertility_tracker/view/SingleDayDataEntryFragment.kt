package com.romrell4.fertility_tracker.view

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.core.os.bundleOf
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.google.android.material.button.MaterialButton
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.romrell4.fertility_tracker.R
import com.romrell4.fertility_tracker.databinding.FragmentSingleDayDataEntryBinding
import com.romrell4.fertility_tracker.domain.SymptomEntry
import com.romrell4.fertility_tracker.viewmodel.SingleDayDataEntryViewModel
import com.romrell4.fertility_tracker.viewmodel.SingleDayDataEntryViewState
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import java.time.LocalDate

@ExperimentalCoroutinesApi
class SingleDayDataEntryFragment : Fragment(), MucusDialogCallback, TemperatureDialogCallback {
    private val viewModel: SingleDayDataEntryViewModel by viewModels {
        defaultViewModelProviderFactory
    }
    private lateinit var binding: FragmentSingleDayDataEntryBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        return FragmentSingleDayDataEntryBinding.inflate(inflater, container, false).also {
            binding = it
        }.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.date = requireNotNull(arguments?.getSerializable(ARG_DATE) as? LocalDate)

        lifecycleScope.launch {
            viewModel.viewStateFlow.collect {
                render(it)
            }
        }
    }

    override fun mucusSaved(mucus: SymptomEntry.Mucus?) {
        viewModel.saveMucus(mucus)
    }

    override fun temperatureSaved(temperature: SymptomEntry.Temperature?) {
        viewModel.saveTemperature(temperature)
    }

    private fun render(viewState: SingleDayDataEntryViewState) {
        //Buttons
        fun MaterialButton.setupSymptomButton(symptom: Any?) {
            setBackgroundColor(resources.getColor(if (symptom == null) R.color.gray else R.color.purple, null))
        }

        binding.sensationsButton.setupSymptomButton(viewState.sensation)
        binding.observationsButton.setupSymptomButton(viewState.observation)
        binding.mucusButton.setupSymptomButton(viewState.mucus)
        binding.bleedingButton.setupSymptomButton(viewState.bleeding)
        binding.sexButton.setupSymptomButton(viewState.sex)
        binding.temperatureButton.setupSymptomButton(viewState.temperature)

        binding.sensationsButton.setUpRadioDialogListener(
            values = SymptomEntry.Sensation.values(),
            currentValue = viewState.sensation,
            viewModelFunction = viewModel::selectSensation
        )
        binding.observationsButton.setUpRadioDialogListener(
            values = SymptomEntry.Observation.values(),
            currentValue = viewState.observation,
            viewModelFunction = viewModel::selectObservation
        )
        binding.mucusButton.setOnClickListener {
            MucusDialog.newInstance(viewState.mucus).also {
                it.setTargetFragment(this, 0)
            }.show(parentFragmentManager, null)
        }
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
        binding.temperatureButton.setOnClickListener {
            TemperatureDialog.newInstance(viewState.temperature).also {
                it.setTargetFragment(this, 0)
            }.show(parentFragmentManager, null)
        }

        //Notes
        if (binding.notesText.text.toString() != viewState.notes) {
            //If the notes are out of date, update them, and set the cursor to the end of the text
            binding.notesText.setText(viewState.notes)
            binding.notesText.setSelection(viewState.notes.orEmpty().length)
        }
        binding.notesText.setTextChangedListener {
            if (it?.toString().orEmpty() != viewState.notes.orEmpty()) {
                viewModel.saveNotes(it?.toString())
            }
        }
        binding.inDoubtCheckbox.setOnCheckedChangeListener { _, isChecked ->
            // Set it to "null" if it's not checked
            viewModel.setInDoubt(isChecked.takeIf { it })
        }
    }

    private var editTextListeners: MutableMap<EditText, TextWatcher> = mutableMapOf()
    private fun EditText.setTextChangedListener(listener: (Editable?) -> Unit) {
        editTextListeners[this]?.let { removeTextChangedListener(it) }
        editTextListeners[this] = addTextChangedListener { listener(it) }
    }

    private fun <T : SymptomEntry.Symptom> MaterialButton.setUpRadioDialogListener(
        values: Array<T>,
        currentValue: T?,
        viewModelFunction: (T?) -> Unit
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
                .setNeutralButton(android.R.string.cancel, null)
                .setNegativeButton(context.getString(R.string.clear)) { _, _ ->
                    viewModelFunction(null)
                }
                .show()
        }
    }

    companion object {
        private const val ARG_DATE = "DATE"

        fun newInstance(date: LocalDate): SingleDayDataEntryFragment {
            return SingleDayDataEntryFragment().apply {
                arguments = bundleOf(ARG_DATE to date)
            }
        }
    }
}
