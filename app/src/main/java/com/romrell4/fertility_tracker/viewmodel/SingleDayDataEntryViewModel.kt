package com.romrell4.fertility_tracker.viewmodel

import android.os.Parcelable
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import com.romrell4.fertility_tracker.domain.SymptomEntry
import com.romrell4.fertility_tracker.usecase.FindSymptomEntryUseCase
import com.romrell4.fertility_tracker.usecase.SaveSymptomEntryUseCase
import com.romrell4.fertility_tracker.view.DI
import kotlinx.android.parcel.Parcelize
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import java.time.LocalDate

private const val STATE_KEY = "DATA_ENTRY_STATE_KEY"

@ExperimentalCoroutinesApi
class SingleDayDataEntryViewModel @JvmOverloads constructor(
    private val savedStateHandle: SavedStateHandle,
    private val saveEntryUseCase: SaveSymptomEntryUseCase = DI.instance.saveSymptomEntryUseCase,
    private val findSymptomEntryUseCase: FindSymptomEntryUseCase = DI.instance.findSymptomEntryUseCase
) : ViewModel() {
    lateinit var date: LocalDate

    val viewStateFlow: Flow<SingleDayDataEntryViewState> by lazy {
        stateFlow.map { it.toViewState() }.distinctUntilChanged()
    }

    private val stateFlow: MutableStateFlow<SingleDayDataEntryState> by lazy {
        MutableStateFlow(
            //If there is something saved in their state, use that first. Otherwise, look up from the repo. Finally create a blank
            savedStateHandle.get(STATE_KEY) ?: SingleDayDataEntryState(
                symptomEntry = findSymptomEntryUseCase.execute(date) ?: SymptomEntry(date = date)
            )
        ).also {
            //Whenever this changes, updated the saved state
            it.onEach { state ->
                savedStateHandle.set(STATE_KEY, state)
            }
        }
    }

    fun selectSensation(sensation: SymptomEntry.Sensation?) {
        updateSymptomEntry { it.copy(sensation = sensation) }
    }

    fun selectObservation(observation: SymptomEntry.Observation?) {
        updateSymptomEntry { it.copy(observation = observation) }
    }

    fun saveMucus(mucus: SymptomEntry.Mucus?) {
        updateSymptomEntry { it.copy(mucus = mucus) }
    }

    fun selectBleeding(bleeding: SymptomEntry.Bleeding?) {
        updateSymptomEntry { it.copy(bleeding = bleeding) }
    }

    fun selectSex(sex: SymptomEntry.Sex?) {
        updateSymptomEntry { it.copy(sex = sex) }
    }

    fun saveTemperature(temperature: SymptomEntry.Temperature?) {
        updateSymptomEntry { it.copy(temperature = temperature) }
    }

    fun setInDoubt(inDoubt: Boolean?) {
        updateSymptomEntry { it.copy(inDoubt = inDoubt) }
    }

    fun saveNotes(notes: String?) {
        updateSymptomEntry { it.copy(notes = notes) }
    }

    private fun updateSymptomEntry(symptomEntryBlock: (SymptomEntry) -> SymptomEntry) {
        val symptomEntry = symptomEntryBlock(stateFlow.value.symptomEntry)
        stateFlow.value = stateFlow.value.copy(symptomEntry = symptomEntry)
        saveEntryUseCase.execute(symptomEntry)
    }
}

@Parcelize
data class SingleDayDataEntryState(
    val symptomEntry: SymptomEntry
) : Parcelable {
    fun toViewState() = SingleDayDataEntryViewState(
        sensation = symptomEntry.sensation,
        observation = symptomEntry.observation,
        mucus = symptomEntry.mucus,
        bleeding = symptomEntry.bleeding,
        sex = symptomEntry.sex,
        inDoubt = symptomEntry.inDoubt,
        notes = symptomEntry.notes,
        temperature = symptomEntry.temperature,
    )
}

data class SingleDayDataEntryViewState(
    val sensation: SymptomEntry.Sensation?,
    val observation: SymptomEntry.Observation?,
    val mucus: SymptomEntry.Mucus?,
    val bleeding: SymptomEntry.Bleeding?,
    val sex: SymptomEntry.Sex?,
    val inDoubt: Boolean?,
    val notes: String?,
    val temperature: SymptomEntry.Temperature?,
)
