package com.romrell4.fertility_tracker.viewmodel

import android.os.Parcelable
import androidx.annotation.DrawableRes
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.romrell4.fertility_tracker.R
import com.romrell4.fertility_tracker.domain.Cycle
import com.romrell4.fertility_tracker.domain.SymptomEntry
import com.romrell4.fertility_tracker.usecase.GetAllCyclesUseCase
import com.romrell4.fertility_tracker.view.DI
import kotlinx.android.parcel.Parcelize
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import java.time.format.DateTimeFormatter

private const val STATE_KEY = "CHART_STATE_KEY"

@ExperimentalCoroutinesApi
class ChartViewModel @JvmOverloads constructor(
    private val savedStateHandle: SavedStateHandle,
    private val getAllCycles: GetAllCyclesUseCase = DI.instance.getAllCyclesUseCase
) : ViewModel() {
    val viewStateFlow: Flow<ChartViewState> by lazy {
        stateFlow.map { it.toViewState() }
    }

    private val stateFlow: MutableStateFlow<ChartState> = MutableStateFlow(
        savedStateHandle.get(STATE_KEY) ?: ChartState(cycles = emptyList())
    ).also {
        it.onEach { state ->
            savedStateHandle.set(STATE_KEY, state)
        }
    }

    fun loadAllEntries() {
        stateFlow.value = ChartState(getAllCycles.execute())
    }
}

@Parcelize
data class ChartState(
    val cycles: List<Cycle>
) : Parcelable {
    fun toViewState() = ChartViewState(
        cycles = cycles.map { cycle ->
            ChartViewState.CycleView(
                cycleNumber = cycle.cycleNumber,
                days = cycle.days.map { day ->
                    ChartViewState.CycleView.DayView(
                        dayOfCycle = day.dayOfCycle.toString(),
                        date = day.symptomEntry.date.format(DateTimeFormatter.ofPattern("M/d")),
                        stampRes = when {
                            //TODO: Combined stamps?
                            day.symptomEntry.hasPeakMucus -> R.drawable.ic_circle_peak_mucus
                            day.symptomEntry.mucus != null -> R.drawable.ic_circle_non_peak_mucus
                            day.symptomEntry.bleeding != null -> R.drawable.ic_circle_bleeding
                            else -> R.drawable.ic_circle_no_mucus
                        },
                        sensations = when (day.symptomEntry.sensation) {
                            SymptomEntry.Sensation.DRY -> "D"
                            SymptomEntry.Sensation.SMOOTH -> "S"
                            SymptomEntry.Sensation.LUBRICATIVE -> "L"
                            else -> null
                        },
                        dialogTitle = "${day.symptomEntry.date.format(DateTimeFormatter.ofPattern("EEEE, MMM d"))} (Day ${day.dayOfCycle})",
                        dialogMessage = listOfNotNull(
                            day.symptomEntry.bleeding?.displayText?.let { "Flow: $it" },
                            day.symptomEntry.mucus?.consistency?.let { "Consistency: $it" },
                            day.symptomEntry.mucus?.color?.let { "Color: $it" },
                            day.symptomEntry.mucus?.dailyOccurrences?.let { "Number of Occurrences: $it" },
                            day.symptomEntry.notes?.takeIf { it.isNotBlank() }?.let { "Notes: \n$it" }
                        ).joinToString("\n\n")
                    )
                }
            )
        }
    )
}

data class ChartViewState(
    val cycles: List<CycleView>
) {
    data class CycleView(
        val cycleNumber: Int,
        val days: List<DayView>
    ) {
        data class DayView(
            val dayOfCycle: String,
            val date: String,
            @DrawableRes
            val stampRes: Int,
            val sensations: String?,
            val dialogTitle: String,
            val dialogMessage: String?
        )
    }
}
