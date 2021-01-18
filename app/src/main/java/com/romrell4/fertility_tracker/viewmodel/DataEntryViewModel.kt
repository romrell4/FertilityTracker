package com.romrell4.fertility_tracker.viewmodel

import android.os.Parcelable
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import kotlinx.android.parcel.Parcelize
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import java.time.LocalDate

private const val STATE_KEY = "DATA_ENTRY_STATE_KEY"

@ExperimentalCoroutinesApi
class DataEntryViewModel @JvmOverloads constructor(
    private val savedStateHandle: SavedStateHandle,
) : ViewModel() {

    val viewStateFlow: Flow<DataEntryViewState> by lazy {
        stateFlow.map { it.toViewState() }.distinctUntilChanged()
    }

    private val stateFlow: MutableStateFlow<DataEntryState> = MutableStateFlow(
        //If there is something saved in their state, use that first. Otherwise, start with today
        savedStateHandle.get(STATE_KEY) ?: DataEntryState(
            date = LocalDate.now()
        )
    ).also {
        //Whenever this changes, updated the saved state
        it.onEach { state ->
            savedStateHandle.set(STATE_KEY, state)
        }
    }

    fun reload() {
        selectDate(stateFlow.value.date)
    }

    fun selectPreviousDate() {
        val previousDate = stateFlow.value.date.minusDays(1)
        selectDate(previousDate)
    }

    fun selectNextDate() {
        val nextDate = stateFlow.value.date.plusDays(1)
        selectDate(nextDate)
    }

    fun selectDate(date: LocalDate) {
        stateFlow.value = DataEntryState(
            date = date
        )
    }
}

@Parcelize
data class DataEntryState(
    val date: LocalDate
) : Parcelable {
    fun toViewState() = DataEntryViewState(
        previousDate = date.minusDays(1),
        currentDate = date,
        nextDate = date.plusDays(1).takeIf { it < LocalDate.now() },
    )
}

data class DataEntryViewState(
    val previousDate: LocalDate,
    val currentDate: LocalDate,
    val nextDate: LocalDate?,
) {
    val dates: List<LocalDate> = listOfNotNull(previousDate, currentDate, nextDate)
}
