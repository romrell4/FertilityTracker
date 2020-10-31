package com.romrell4.fertility_tracker.repo

import android.content.Context
import androidx.core.content.edit
import com.fasterxml.jackson.module.kotlin.*
import com.romrell4.fertility_tracker.domain.SymptomEntry

interface FertilityTrackingRepository {
    fun saveSymptomEntry(symptomEntry: SymptomEntry)
}

private const val SP_NAME = "FertilityTracking"
private const val SP_SYMPTOM_ENTRIES_KEY = "SymptomEntries"

class FertilityTrackingRepositoryImpl(context: Context) : FertilityTrackingRepository {
    private val sharedPrefs = context.getSharedPreferences(SP_NAME, Context.MODE_PRIVATE)
    private val objectMapper = jacksonObjectMapper()

    override fun saveSymptomEntry(symptomEntry: SymptomEntry) {
        sharedPrefs.edit {
            putString(
                SP_SYMPTOM_ENTRIES_KEY,
                objectMapper.writeValueAsString(getAllSymptomEntries() + symptomEntry)
            )
        }
    }

    private fun getAllSymptomEntries(): List<SymptomEntry> {
        return sharedPrefs.getString(SP_SYMPTOM_ENTRIES_KEY, null)?.let {
            objectMapper.readValue(it)
        } ?: emptyList()
    }
}