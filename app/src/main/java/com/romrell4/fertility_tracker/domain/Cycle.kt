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
