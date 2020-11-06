package com.romrell4.fertility_tracker.viewmodel

import android.os.Parcelable
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.romrell4.fertility_tracker.domain.Cycle
import com.romrell4.fertility_tracker.domain.SymptomEntry
import com.romrell4.fertility_tracker.domain.toCycles
import com.romrell4.fertility_tracker.usecase.GetAllEntriesUseCase
import com.romrell4.fertility_tracker.view.DI
import kotlinx.android.parcel.Parcelize
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import java.time.LocalDate

private const val STATE_KEY = "CHART_STATE_KEY"

@ExperimentalCoroutinesApi
class ChartViewModel @JvmOverloads constructor(
    private val savedStateHandle: SavedStateHandle,
    private val loadAllEntriesUseCase: GetAllEntriesUseCase = DI.instance.loadAllEntriesUseCase
) : ViewModel() {
    val viewStateFlow: Flow<ChartViewState> by lazy {
        stateFlow.map { it.toViewState() }
    }

    private val stateFlow: MutableStateFlow<ChartState> = MutableStateFlow(
        savedStateHandle.get(STATE_KEY) ?: ChartState(entries = emptyList())
    ).also {
        it.onEach { state ->
            savedStateHandle.set(STATE_KEY, state)
        }
    }

    fun loadAllEntries() {
        stateFlow.value = ChartState(entries = (0..10).map {
            SymptomEntry(LocalDate.now())
        })
        viewModelScope.launch(Dispatchers.IO) {
//            stateFlow.value = ChartState(loadAllEntriesUseCase.execute())
        }
    }
}

@Parcelize
data class ChartState(
    val entries: List<SymptomEntry>
) : Parcelable {
    fun toViewState() = ChartViewState(
        cycles = entries.toCycles()
    )
}

data class ChartViewState(
    val cycles: List<Cycle>
)
