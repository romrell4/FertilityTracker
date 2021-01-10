package com.romrell4.fertility_tracker.viewmodel

import android.os.Parcelable
import androidx.annotation.DrawableRes
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import com.romrell4.fertility_tracker.R
import com.romrell4.fertility_tracker.domain.Cycle
import com.romrell4.fertility_tracker.domain.SymptomEntry
import com.romrell4.fertility_tracker.usecase.GetAllCyclesUseCase
import com.romrell4.fertility_tracker.view.DI
import kotlinx.android.parcel.Parcelize
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
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

private val CYCLE_DATE_FORMATTER = DateTimeFormatter.ofPattern("MMM d, yyyy")
private val DIALOG_TITLE_FORMATTER = DateTimeFormatter.ofPattern("EEEE, MMM d")

@Parcelize
data class ChartState(
    val cycles: List<Cycle>
) : Parcelable {
    fun toViewState() = ChartViewState(
        cycles = cycles.map { cycle ->
            ChartViewState.CycleView(
                cycleNumber = cycle.cycleNumber,
                startDate = cycle.startDate.format(CYCLE_DATE_FORMATTER),
                endDate = cycle.endDate.format(CYCLE_DATE_FORMATTER),
                length = cycle.days.size,
                days = cycle.days.mapIndexed { index, day ->
                    ChartViewState.CycleView.DayView(
                        dayOfCycle = day.dayOfCycle.toString(),
                        date = day.symptomEntry.date.format(DateTimeFormatter.ofPattern("M/d")),
                        stampRes = when {
                            //TODO: Combined stamps?
                            day.symptomEntry.hasPeakMucus -> R.drawable.ic_circle_peak_mucus
                            day.symptomEntry.hasNonPeakMucus -> R.drawable.ic_circle_non_peak_mucus
                            day.symptomEntry.bleeding != null -> R.drawable.ic_circle_bleeding
                            else -> R.drawable.ic_circle_no_mucus
                        },
                        sensations = when (day.symptomEntry.sensation) {
                            SymptomEntry.Sensation.DRY -> null
                            SymptomEntry.Sensation.SMOOTH -> "S"
                            SymptomEntry.Sensation.LUBRICATIVE -> "L"
                            else -> null
                        },
                        observationRes = when (day.symptomEntry.observation) {
                            SymptomEntry.Observation.DAMP -> R.drawable.ic_observation_damp
                            SymptomEntry.Observation.WET -> R.drawable.ic_observation_wet
                            SymptomEntry.Observation.SHINY -> R.drawable.ic_observation_shiny
                            else -> null
                        },
                        bleedingRes = when (day.symptomEntry.bleeding) {
                            SymptomEntry.Bleeding.LIGHT -> R.drawable.ic_bleeding_light
                            SymptomEntry.Bleeding.MODERATE -> R.drawable.ic_bleeding_moderate
                            SymptomEntry.Bleeding.HEAVY -> R.drawable.ic_bleeding_heavy
                            else -> null
                        },
                        sexRes = when (day.symptomEntry.sex) {
                            SymptomEntry.Sex.PROTECTED -> R.drawable.ic_sex_protected
                            SymptomEntry.Sex.UNPROTECTED -> R.drawable.ic_sex_unprotected
                            else -> null
                        },
                        dialogTitle = "${day.symptomEntry.date.format(DIALOG_TITLE_FORMATTER)} (Day ${day.dayOfCycle})",
                        dialogMessage = listOfNotNull(
                            day.symptomEntry.bleeding?.displayText?.let { "Flow: $it" },
                            day.symptomEntry.mucus?.consistency?.displayText?.let { "Consistency: $it" },
                            day.symptomEntry.mucus?.color?.displayText?.let { "Color: $it" },
                            day.symptomEntry.mucus?.dailyOccurrences?.let { "Number of Occurrences: $it" },
                            day.symptomEntry.notes?.takeIf { it.isNotBlank() }?.let { "Notes: \n$it" }
                        ).joinToString("\n\n"),
                        stampHighlighted = cycle.ruleOfThreeIndexes.contains(index),
                        temperature = day.symptomEntry.temperature?.let {
                            ChartViewState.CycleView.TemperatureView(it.value, it.abnormal, it.abnormalNotes)
                        },
                        notes = day.symptomEntry.notes?.takeIf { it.isNotBlank() }
                    )
                },
                coverlineValue = cycle.coverlineValue
            )
        }
    )
}

data class ChartViewState(
    val cycles: List<CycleView>
) {
    data class CycleView(
        val cycleNumber: Int,
        val startDate: String,
        val endDate: String,
        val length: Int,
        val days: List<DayView>,
        val coverlineValue: Double?
    ) {
        data class DayView(
            val dayOfCycle: String,
            val date: String,
            @DrawableRes
            val stampRes: Int,
            val sensations: String?,
            @DrawableRes
            val observationRes: Int?,
            @DrawableRes
            val bleedingRes: Int?,
            @DrawableRes
            val sexRes: Int?,
            val dialogTitle: String,
            val dialogMessage: String?,
            val stampHighlighted: Boolean,
            val temperature: TemperatureView?,
            val notes: String?,
            var selected: Boolean = false
        )

        data class TemperatureView(
            val value: Double,
            val abnormal: Boolean,
            val abnormalNotes: String?
        )
    }
}

