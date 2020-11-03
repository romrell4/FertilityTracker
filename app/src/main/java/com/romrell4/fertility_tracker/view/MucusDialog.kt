package com.romrell4.fertility_tracker.view

import android.app.Dialog
import android.os.Bundle
import android.widget.RadioButton
import android.widget.RadioGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.DialogFragment
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.romrell4.fertility_tracker.R
import com.romrell4.fertility_tracker.databinding.MucusDialogBinding
import com.romrell4.fertility_tracker.domain.SymptomEntry

private const val MUCUS_KEY = "Mucus"

interface MucusDialogCallback {
    fun mucusSaved(mucus: SymptomEntry.Mucus)
}

class MucusDialog private constructor() : DialogFragment() {
    private lateinit var binding: MucusDialogBinding

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        binding = MucusDialogBinding.inflate(layoutInflater)

        fun <T : SymptomEntry.Symptom> setupRadioList(values: Array<T>, currentValue: T?, radioGroup: RadioGroup) {
            values.map {
                RadioButton(context).apply {
                    text = it.displayText
                    tag = it
                }
            }.also { buttons ->
                buttons.forEach {
                    radioGroup.addView(it)
                }
                //Select the one matching the current selection
                buttons.firstOrNull { it.tag == currentValue }?.let {
                    radioGroup.check(it.id)
                }
            }
        }

        val currentMucus = arguments?.getParcelable<SymptomEntry.Mucus>(MUCUS_KEY)
        setupRadioList(SymptomEntry.Mucus.Consistency.values(), currentMucus?.consistency, binding.consistencyRadioGroup)
        setupRadioList(SymptomEntry.Mucus.Color.values(), currentMucus?.color, binding.colorRadioGroup)

        //Occurrences
        binding.occurrencesPicker.apply {
            min = 1
            value = currentMucus?.dailyOccurrences ?: min
        }

        return MaterialAlertDialogBuilder(requireContext())
            .setView(binding.root)
            .setPositiveButton(R.string.mucus_positive_text) { _, _ ->
                @Suppress("UNCHECKED_CAST")
                fun <T> getSymptom(radioGroup: RadioGroup): T? {
                    return radioGroup.findViewById<RadioButton>(
                        radioGroup.checkedRadioButtonId
                    )?.tag as? T
                }

                (targetFragment as? MucusDialogCallback)?.mucusSaved(
                    SymptomEntry.Mucus(
                        consistency = getSymptom<SymptomEntry.Mucus.Consistency>(binding.consistencyRadioGroup),
                        color = getSymptom<SymptomEntry.Mucus.Color>(binding.colorRadioGroup),
                        dailyOccurrences = binding.occurrencesPicker.value
                    )
                )
            }
            .setNegativeButton(android.R.string.cancel, null)
            .create()
    }

    companion object {
        fun newInstance(mucus: SymptomEntry.Mucus?): MucusDialog {
            return MucusDialog().apply {
                arguments = bundleOf(MUCUS_KEY to mucus)
            }
        }
    }
}