package com.romrell4.fertility_tracker.view

import android.app.Dialog
import android.app.TimePickerDialog
import android.os.Bundle
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.core.content.ContextCompat.getSystemService
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

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        binding = TemperatureDialogBinding.inflate(layoutInflater)

        fun setTime(time: LocalTime) {
            binding.timeText.text = time.format(TIME_FORMATTER)
        }

        val currentTime = LocalTime.now()
        setTime(currentTime)
        binding.timeText.setOnClickListener {
            TimePickerDialog(
                context,
                { _, hourOfDay, minute -> setTime(LocalTime.of(hourOfDay, minute)) },
                currentTime.hour,
                currentTime.minute,
                false
            ).show()
        }

        binding.tempText1.requestFocus()
        binding.tempText1.showKeyboard()

        listOf(
            binding.tempText1 to binding.tempText2,
            binding.tempText2 to binding.tempText3,
            binding.tempText3 to binding.tempText4,
            binding.tempText4 to null
        ).forEach { (editText1, editText2) ->
            if (editText2 != null) {
                editText1.addTextChangedListener {
                    editText2.requestFocus()
                }
            } else {
                editText1.addTextChangedListener {
                    editText1.hideKeyboard()
                }
            }
        }

        binding.tempText1.addTextChangedListener {
            binding.tempText2.requestFocus()
        }

        binding.abnormalCheckbox.setOnCheckedChangeListener { _, isChecked ->
            binding.abnormalText.visibility = if (isChecked) View.VISIBLE else View.GONE
            binding.abnormalText.requestFocus()
            binding.abnormalText.showKeyboard()
        }

        return MaterialAlertDialogBuilder(requireContext())
            .setView(binding.root)
            .setPositiveButton(R.string.save) { _, _ ->
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
                } else {
                    //TODO: UI validation
                }
            }
            .setNegativeButton(R.string.clear) { _, _ ->
                (targetFragment as? TemperatureDialogCallback)?.temperatureSaved(null)
            }
            .setNeutralButton(android.R.string.cancel, null)
            .create()
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