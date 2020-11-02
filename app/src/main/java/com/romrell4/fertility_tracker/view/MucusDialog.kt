package com.romrell4.fertility_tracker.view

import android.app.Dialog
import android.os.Bundle
import android.widget.RadioButton
import androidx.fragment.app.DialogFragment
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.romrell4.fertility_tracker.R
import com.romrell4.fertility_tracker.databinding.MucusDialogBinding
import com.romrell4.fertility_tracker.domain.SymptomEntry

interface MucusDialogCallback {
    fun mucusSaved(mucus: SymptomEntry.Mucus)
}

class MucusDialog private constructor() : DialogFragment() {
    private lateinit var binding: MucusDialogBinding

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        binding = MucusDialogBinding.inflate(layoutInflater)

        //Consistency
        SymptomEntry.Mucus.Consistency.values().map {
            RadioButton(context).apply {
                text = it.displayText
                tag = it
            }
        }.forEach {
            binding.consistencyRadioGroup.addView(it)
        }

        //Color
        SymptomEntry.Mucus.Color.values().map {
            RadioButton(context).apply {
                text = it.displayText
                tag = it
            }
        }.forEach {
            binding.colorRadioGroup.addView(it)
        }

        //Occurrences
        binding.occurrencesPicker.apply {
            min = 1
        }

        return MaterialAlertDialogBuilder(requireContext())
            .setView(binding.root)
            .setPositiveButton(R.string.mucus_positive_text) { _, _ ->
                val consistency = binding.consistencyRadioGroup.findViewById<RadioButton>(
                    binding.consistencyRadioGroup.checkedRadioButtonId
                )?.tag as? SymptomEntry.Mucus.Consistency

                val color = binding.colorRadioGroup.findViewById<RadioButton>(
                    binding.consistencyRadioGroup.checkedRadioButtonId
                )?.tag as? SymptomEntry.Mucus.Color

                (targetFragment as? MucusDialogCallback)?.mucusSaved(
                    SymptomEntry.Mucus(
                        consistency = consistency,
                        color = color,
                        dailyOccurrences = binding.occurrencesPicker.value
                    )
                )
            }
            .setNegativeButton(android.R.string.cancel, null)
            .create()
    }

    companion object {
        fun newInstance(): MucusDialog {
            val args = Bundle()

            val fragment = MucusDialog()
            fragment.arguments = args
            return fragment
        }
    }
}