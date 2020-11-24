package com.romrell4.fertility_tracker.view

import android.app.DatePickerDialog
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.core.widget.addTextChangedListener
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
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter

private val DATE_FORMATTER = DateTimeFormatter.ofPattern("EEEE, MMM d")

@ExperimentalCoroutinesApi
class DataEntryFragment : Fragment(), MucusDialogCallback, TemperatureDialogCallback {
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

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        lifecycleScope.launch {
            viewModel.viewStateFlow.collect {
                render(it)
            }
        }

        //Dates
        binding.previousDateButton.setOnClickListener { viewModel.selectPreviousDate() }
        binding.nextDateButton.setOnClickListener { viewModel.selectNextDate() }
    }

    override fun mucusSaved(mucus: SymptomEntry.Mucus?) {
        viewModel.saveMucus(mucus)
    }

    override fun temperatureSaved(temperature: SymptomEntry.Temperature?) {
        viewModel.saveTemperature(temperature)
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
            }.show()
        }
        binding.nextDateButton.visibility = if (viewState.canSelectNextDate) View.VISIBLE else View.INVISIBLE

        //Buttons
        fun MaterialButton.setupSymptomButton(symptom: Any?) {
            setBackgroundColor(resources.getColor(if (symptom == null) R.color.gray else R.color.purple, null))
        }

        binding.sensationsButton.setupSymptomButton(viewState.sensation)
        binding.mucusButton.setupSymptomButton(viewState.mucus)
        binding.bleedingButton.setupSymptomButton(viewState.bleeding)
        binding.sexButton.setupSymptomButton(viewState.sex)
        binding.temperatureButton.setupSymptomButton(viewState.temperature)

        binding.sensationsButton.setUpRadioDialogListener(
            values = SymptomEntry.Sensation.values(),
            currentValue = viewState.sensation,
            viewModelFunction = viewModel::selectSensation
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
            binding.notesText.setSelection(viewState.notes?.length ?: 0)
        }

        //Notes
        binding.notesText.setTextChangedListener {
            if (it?.toString().orEmpty() != viewState.notes.orEmpty()) {
                viewModel.saveNotes(it?.toString())
            }
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
        fun newInstance(): DataEntryFragment {
            return DataEntryFragment()
        }
    }
}