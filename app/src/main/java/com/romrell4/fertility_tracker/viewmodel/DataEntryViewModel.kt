package com.romrell4.fertility_tracker.viewmodel

import android.app.Application
import android.os.Parcelable
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import com.romrell4.fertility_tracker.domain.SymptomEntry
import com.romrell4.fertility_tracker.repo.FertilityTrackingRepositoryImpl
import com.romrell4.fertility_tracker.usecase.SaveSymptomEntryUseCase
import com.romrell4.fertility_tracker.usecase.SaveSymptomEntryUseCaseImpl
import kotlinx.android.parcel.Parcelize
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import java.time.LocalDate
import java.util.*

private const val STATE_KEY = "STATE_KEY"

@ExperimentalCoroutinesApi
class DataEntryViewModel(
    application: Application,
    val savedStateHandle: SavedStateHandle
) : AndroidViewModel(application) {
    private lateinit var saveEntryUseCase: SaveSymptomEntryUseCase

    val viewStateFlow: Flow<DataEntryViewState> by lazy {
        stateFlow
            .onEach { savedStateHandle.set(STATE_KEY, it) }
            .map { it.toViewState() }
            .distinctUntilChanged()
    }

    private val stateFlow: MutableStateFlow<DataEntryState> = run {
        val previous = savedStateHandle.get<DataEntryState>(STATE_KEY)
        MutableStateFlow(
            previous ?: DataEntryState(
                symptomEntry = SymptomEntry(
                    date = LocalDate.now()
                )
            )
        )
    }

    fun selectPreviousDate() {
        stateFlow.value = DataEntryState(SymptomEntry(date = stateFlow.value.symptomEntry.date.minusDays(1)))
    }

    fun selectNextDate() {
        stateFlow.value = DataEntryState(SymptomEntry(date = stateFlow.value.symptomEntry.date.plusDays(1)))
    }
}

@Parcelize
data class DataEntryState(
    val symptomEntry: SymptomEntry
) : Parcelable {
    fun toViewState() = DataEntryViewState(
        currentDate = symptomEntry.date
    )
}

data class DataEntryViewState(
    val currentDate: LocalDate
) {
    val previousDate: LocalDate
        get() = currentDate.minusDays(1)

    val nextDate: LocalDate
        get() = currentDate.plusDays(1)
}