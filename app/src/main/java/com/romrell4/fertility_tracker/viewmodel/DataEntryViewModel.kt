package com.romrell4.fertility_tracker.viewmodel

import android.app.Application
import android.os.Parcelable
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.romrell4.fertility_tracker.domain.SymptomEntry
import com.romrell4.fertility_tracker.usecase.FindSymptomEntryUseCase
import com.romrell4.fertility_tracker.usecase.SaveSymptomEntryUseCase
import com.romrell4.fertility_tracker.view.DI
import kotlinx.android.parcel.Parcelize
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.time.LocalDate

private const val STATE_KEY = "STATE_KEY"

@ExperimentalCoroutinesApi
class DataEntryViewModel @JvmOverloads constructor(
    application: Application,
    private val savedStateHandle: SavedStateHandle,
    private val saveEntryUseCase: SaveSymptomEntryUseCase = DI.instance.saveSymptomEntryUseCase,
    private val findSymptomEntryUseCase: FindSymptomEntryUseCase = DI.instance.findSymptomEntryUseCase
) : AndroidViewModel(application) {

    val viewStateFlow: Flow<DataEntryViewState> by lazy {
        stateFlow.map { it.toViewState() }.distinctUntilChanged()
    }

    private val stateFlow: MutableStateFlow<DataEntryState> = MutableStateFlow(
        //If there is something saved in their state, use that first. Otherwise, look up from the repo. Finally create a blank
        savedStateHandle.get(STATE_KEY) ?: DataEntryState(
            symptomEntry = findSymptomEntryUseCase.execute(LocalDate.now())
                ?: SymptomEntry(date = LocalDate.now())
        )
    ).also {
        //Whenever this changes, updated the saved state
        it.onEach { state ->
            savedStateHandle.set(STATE_KEY, state)
        }
    }

    fun selectPreviousDate() {
        val previousDate = stateFlow.value.symptomEntry.date.minusDays(1)
        selectDate(previousDate)
    }

    fun selectNextDate() {
        val nextDate = stateFlow.value.symptomEntry.date.plusDays(1)
        selectDate(nextDate)
    }

    private fun selectDate(date: LocalDate) {
        stateFlow.value = DataEntryState(
            //If we can't find one, create a new one for that date
            symptomEntry = findSymptomEntryUseCase.execute(date) ?: SymptomEntry(date = date)
        )
    }

    fun selectSensation(sensation: SymptomEntry.Sensation) {
        updateSymptomEntry { it.copy(sensation = sensation) }
    }

    fun saveMucus(mucus: SymptomEntry.Mucus) {
        updateSymptomEntry { it.copy(mucus = mucus) }
    }

    fun selectBleeding(bleeding: SymptomEntry.Bleeding) {
        updateSymptomEntry { it.copy(bleeding = bleeding) }
    }

    fun selectSex(sex: SymptomEntry.Sex) {
        updateSymptomEntry { it.copy(sex = sex) }
    }

    fun saveNotes(notes: String?) {
        updateSymptomEntry { it.copy(notes = notes) }
    }

    private fun updateSymptomEntry(symptomEntryBlock: (SymptomEntry) -> SymptomEntry) {
        val symptomEntry = symptomEntryBlock(stateFlow.value.symptomEntry)
        stateFlow.value = stateFlow.value.copy(symptomEntry = symptomEntry)
        viewModelScope.launch(Dispatchers.IO) {
            saveEntryUseCase.execute(symptomEntry)
        }
    }
}

@Parcelize
data class DataEntryState(
    val symptomEntry: SymptomEntry
) : Parcelable {
    fun toViewState() = DataEntryViewState(
        currentDate = symptomEntry.date,
        sensation = symptomEntry.sensation,
        mucus = symptomEntry.mucus,
        bleeding = symptomEntry.bleeding,
        sex = symptomEntry.sex,
        notes = symptomEntry.notes
    )
}

data class DataEntryViewState(
    val currentDate: LocalDate,
    val sensation: SymptomEntry.Sensation?,
    val mucus: SymptomEntry.Mucus?,
    val bleeding: SymptomEntry.Bleeding?,
    val sex: SymptomEntry.Sex?,
    val notes: String?
) {
    val previousDate: LocalDate
        get() = currentDate.minusDays(1)

    val nextDate: LocalDate
        get() = currentDate.plusDays(1)
}