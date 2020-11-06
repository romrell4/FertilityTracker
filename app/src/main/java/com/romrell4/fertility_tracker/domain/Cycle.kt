package com.romrell4.fertility_tracker.domain

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Cycle(
    val days: List<Day>,
    val cycleNumber: Int = 0
) : Parcelable {
    @Parcelize
    data class Day(
        val dayOfCycle: Int,
        val symptomEntry: SymptomEntry
    ) : Parcelable
}

fun List<SymptomEntry>.toCycles(): List<Cycle> {
    return listOf(
        Cycle(
            cycleNumber = 1,
            days = this.sortedBy { it.date }.mapIndexed { index, symptomEntry ->
                Cycle.Day(index + 1, symptomEntry)
            }
        )
    )
//    val cycles = mutableListOf<Cycle>()
    //TODO: Implement
//    return cycles
}