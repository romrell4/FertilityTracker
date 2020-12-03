package com.romrell4.fertility_tracker.repo

import android.content.Context
import androidx.core.content.edit
import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.kotlin.*
import com.romrell4.fertility_tracker.domain.SymptomEntry
import java.time.LocalDate

interface FertilityTrackingRepository {
    fun saveSymptomEntry(symptomEntry: SymptomEntry)
    fun getAllSymptomEntries(): Map<LocalDate, SymptomEntry>
    fun exportData(): String?
    fun importData(serializedData: String)
}

private const val SP_NAME = "FertilityTracking"
private const val SP_SYMPTOM_ENTRIES_KEY = "SymptomEntries"

class FertilityTrackingRepositoryImpl(context: Context) : FertilityTrackingRepository {
    private val sharedPrefs = context.getSharedPreferences(SP_NAME, Context.MODE_PRIVATE)
    private val objectMapper = jacksonObjectMapper().apply {
        registerModule(JavaTimeModule())
        disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
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

    override fun getAllSymptomEntries(): Map<LocalDate, SymptomEntry> {
        return sharedPrefs.getString(SP_SYMPTOM_ENTRIES_KEY, null)?.let {
            objectMapper.readValue(it)
        } ?: emptyMap()
    }

    override fun exportData(): String? {
        return sharedPrefs.getString(SP_SYMPTOM_ENTRIES_KEY, null)
    }

    override fun importData(serializedData: String) {
        //Make sure that we can serialize the value
        objectMapper.readValue<Map<LocalDate, SymptomEntry>>(serializedData)

        sharedPrefs.edit {
            putString(SP_SYMPTOM_ENTRIES_KEY, serializedData)
        }
    }
}
