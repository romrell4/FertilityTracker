package com.romrell4.fertility_tracker.repo

import android.content.Context
import androidx.core.content.edit
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.kotlin.*
import com.romrell4.fertility_tracker.domain.SymptomEntry
import java.time.LocalDate

interface FertilityTrackingRepository {
    fun saveSymptomEntry(symptomEntry: SymptomEntry)
}

private const val SP_NAME = "FertilityTracking"
private const val SP_SYMPTOM_ENTRIES_KEY = "SymptomEntries"

class FertilityTrackingRepositoryImpl(context: Context) : FertilityTrackingRepository {
    private val sharedPrefs = context.getSharedPreferences(SP_NAME, Context.MODE_PRIVATE)
    private val objectMapper = jacksonObjectMapper().apply {
        registerModule(JavaTimeModule())
    }

    override fun saveSymptomEntry(symptomEntry: SymptomEntry) {
        val entries = getAllSymptomEntries().toMutableMap().also {
            it[symptomEntry.date] = symptomEntry
        }
        sharedPrefs.edit {
            putString(
                SP_SYMPTOM_ENTRIES_KEY,
                objectMapper.writeValueAsString(entries)
            )
        }
    }

    private fun getAllSymptomEntries(): Map<LocalDate, SymptomEntry> {
        return sharedPrefs.getString(SP_SYMPTOM_ENTRIES_KEY, null)?.let {
            objectMapper.readValue(it)
        } ?: emptyMap()
    }
}