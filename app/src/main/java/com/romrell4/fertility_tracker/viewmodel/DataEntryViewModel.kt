package com.romrell4.fertility_tracker.viewmodel

import android.app.Application
import android.os.Parcelable
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.SavedStateHandle
import com.romrell4.fertility_tracker.domain.SymptomEntry
import com.romrell4.fertility_tracker.usecase.SaveSymptomEntryUseCase
import com.romrell4.fertility_tracker.view.DI
import kotlinx.android.parcel.Parcelize
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import java.time.LocalDate

private const val STATE_KEY = "STATE_KEY"

@ExperimentalCoroutinesApi
class DataEntryViewModel @JvmOverloads constructor(
    application: Application,
    private val savedStateHandle: SavedStateHandle,
    private val saveEntryUseCase: SaveSymptomEntryUseCase = DI.instance.saveSymptomEntryUseCase
) : AndroidViewModel(application) {

    val viewStateFlow: Flow<DataEntryViewState> by lazy {
        stateFlow.map { it.toViewState() }.distinctUntilChanged()
    }

    private val stateFlow: MutableStateFlow<DataEntryState> = MutableStateFlow(
        savedStateHandle.get(STATE_KEY) ?: DataEntryState(
            symptomEntry = SymptomEntry(
                date = LocalDate.now()
            )
        )
    ).also {
        //Whenever this changes, updated the saved state
        it.onEach { state ->
            savedStateHandle.set(STATE_KEY, state)
        }
    }

    fun selectPreviousDate() {
        //TODO: Look up previous data
        stateFlow.value = DataEntryState(SymptomEntry(date = stateFlow.value.symptomEntry.date.minusDays(1)))
    }

    fun selectNextDate() {
        //TODO: Look up next data
        stateFlow.value = DataEntryState(SymptomEntry(date = stateFlow.value.symptomEntry.date.plusDays(1)))
    }

    fun selectSensation(sensation: SymptomEntry.Sensation) {
        updateSymptomEntry(stateFlow.value.symptomEntry.copy(sensation = sensation))
    }

    fun saveMucus(mucus: SymptomEntry.Mucus) {
        updateSymptomEntry(stateFlow.value.symptomEntry.copy(mucus = mucus))
    }

    fun selectBleeding(bleeding: SymptomEntry.Bleeding) {
        updateSymptomEntry(stateFlow.value.symptomEntry.copy(bleeding = bleeding))
    }

    fun selectSex(sex: SymptomEntry.Sex) {
        updateSymptomEntry(stateFlow.value.symptomEntry.copy(sex = sex))
    }

    private fun updateSymptomEntry(symptomEntry: SymptomEntry) {
        stateFlow.value = stateFlow.value.copy(symptomEntry = symptomEntry)
        saveEntryUseCase.execute(symptomEntry)
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
        sex = symptomEntry.sex
    )
}

data class DataEntryViewState(
    val currentDate: LocalDate,
    val sensation: SymptomEntry.Sensation?,
    val mucus: SymptomEntry.Mucus?,
    val bleeding: SymptomEntry.Bleeding?,
    val sex: SymptomEntry.Sex?
) {
    val previousDate: LocalDate
        get() = currentDate.minusDays(1)

    val nextDate: LocalDate
        get() = currentDate.plusDays(1)
}