package com.romrell4.fertility_tracker.view

import android.app.Dialog
import android.app.TimePickerDialog
import android.os.Bundle
import android.view.View
import android.widget.EditText
import androidx.appcompat.app.AlertDialog
import androidx.core.os.bundleOf
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.DialogFragment
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.romrell4.fertility_tracker.R
import com.romrell4.fertility_tracker.databinding.TemperatureDialogBinding
import com.romrell4.fertility_tracker.domain.SymptomEntry
import com.romrell4.fertility_tracker.support.hideKeyboard
import com.romrell4.fertility_tracker.support.showKeyboard
import java.time.LocalTime
import java.time.format.DateTimeFormatter

private val TIME_FORMATTER = DateTimeFormatter.ofPattern("h:mm a")

interface TemperatureDialogCallback {
    fun temperatureSaved(temperature: SymptomEntry.Temperature?)
}

class TemperatureDialog : DialogFragment() {
    private lateinit var binding: TemperatureDialogBinding

    private val passedInTemp by lazy {
        requireArguments().getParcelable<SymptomEntry.Temperature>(TEMP_KEY)
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        binding = TemperatureDialogBinding.inflate(layoutInflater)

        setupTimeUI()
        setupTemperatureUI()
        setupAbnormalUI()

        return MaterialAlertDialogBuilder(requireContext())
            .setView(binding.root)
            //Don't worry. We override it again later so that it doesn't automatically dismiss
            .setPositiveButton(R.string.save, null)
            .setNegativeButton(R.string.clear) { _, _ ->
                binding.root.hideKeyboard()
                (targetFragment as? TemperatureDialogCallback)?.temperatureSaved(null)
            }
            .setNeutralButton(android.R.string.cancel) { _, _ ->
                binding.root.hideKeyboard()
            }
            .create()
            .also { dialog ->
                dialog.setOnShowListener {
                    dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener {
                        val temperatureValue =
                            "${binding.tempText1.text}${binding.tempText2.text}.${binding.tempText3.text}${binding.tempText4.text}".toDoubleOrNull()
                        if (temperatureValue != null) {
                            (targetFragment as? TemperatureDialogCallback)?.temperatureSaved(
                                SymptomEntry.Temperature(
                                    time = LocalTime.now(),
                                    value = temperatureValue,
                                    abnormal = binding.abnormalCheckbox.isChecked,
                                    abnormalNotes = binding.abnormalText.text?.toString()
                                )
                            )
                            binding.root.hideKeyboard()
                            dismiss()
                        }
                    }
                }
            }
    }

    private fun setupTimeUI() {
        fun setTime(time: LocalTime) {
            binding.timeText.text = time.format(TIME_FORMATTER)
        }

        val currentTime = LocalTime.now()
        setTime(currentTime)
        binding.timeText.setOnClickListener {
            TimePickerDialog(
                context,
                { _, hourOfDay, minute -> setTime(LocalTime.of(hourOfDay, minute)) },
                currentTime.hour, currentTime.minute, false
            ).show()
        }
    }

    private fun setupTemperatureUI() {
        //Populate the fields with the correct starting values
        passedInTemp?.let { "%02.2f".format(it.value) }?.takeIf { it.length == 5 }?.also {
            binding.tempText1.setText(it[0].toString())
            binding.tempText2.setText(it[1].toString())
            binding.tempText3.setText(it[3].toString())
            binding.tempText4.setText(it[4].toString())
        }

        //Start the first temperature edit text with focus
        binding.tempText1.requestEndFocus()
        binding.tempText1.showKeyboard()

        listOf(
            binding.tempText1 to binding.tempText2,
            binding.tempText2 to binding.tempText3,
            binding.tempText3 to binding.tempText4,
            binding.tempText4 to null
        ).forEach { (editText1, editText2) ->
            //TODO: Figure out edit text validation?

            editText1.addTextChangedListener {
                //Only change focus or change the keyboard if they added a value to the edit text
                if (it?.length == 1) {
                    if (editText2 != null) {
                        editText2.requestEndFocus()
                    } else {
                        editText1.hideKeyboard()
                    }
                }
            }
        }
    }

    private fun setupAbnormalUI() {
        //Populate starting values
        passedInTemp?.let {
            binding.abnormalCheckbox.isChecked = it.abnormal
            binding.abnormalText.visibility = if (it.abnormal) View.VISIBLE else View.GONE
            binding.abnormalText.setText(it.abnormalNotes)
        }

        binding.abnormalCheckbox.setOnCheckedChangeListener { _, isChecked ->
            binding.abnormalText.visibility = if (isChecked) View.VISIBLE else View.GONE
            binding.abnormalText.requestEndFocus()
            binding.abnormalText.showKeyboard()
        }
    }

    private fun EditText.requestEndFocus() {
        requestFocus()
        setSelection(text.length)
    }

    companion object {
        private const val TEMP_KEY = "temperature"

        fun newInstance(temperature: SymptomEntry.Temperature?): TemperatureDialog {
            return TemperatureDialog().apply {
                arguments = bundleOf(TEMP_KEY to temperature)
            }
        }
    }
}