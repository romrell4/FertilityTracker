package com.romrell4.fertility_tracker.domain

import android.os.Parcelable
import kotlinx.android.parcel.IgnoredOnParcel
import kotlinx.android.parcel.Parcelize

private const val NUM_NON_PEAK_MUCUS_DAYS_FOR_PEAK_DAY = 3
private const val NUM_DAYS_IN_PEAK_RANGE = 3

@Parcelize
data class Cycle(
    val days: List<Day>,
    val cycleNumber: Int = 0
) : Parcelable {
    @Parcelize
    data class Day(
        val dayOfCycle: Int,
        val symptomEntry: SymptomEntry
    ) : Parcelable, Comparable<Day> {
        override fun compareTo(other: Day): Int = compareValuesBy(this, other, { dayOfCycle })
    }

    @IgnoredOnParcel
    val peakDayIndexes: Set<Int>
        get() {
            val sorted = days.sorted()
            return sorted.mapIndexedNotNull { index, day ->
                val todayHasPeakMucus = day.symptomEntry.hasPeakMucus

                //If tomorrow doesn't exist yet, it cannot count as peak mucus
                val tomorrowHasPeakMucus = days.getOrNull(index + 1)?.symptomEntry?.hasPeakMucus ?: false

                val lastThreeDaysAreNonPeakMucus = try {
                    days.slice((index - NUM_NON_PEAK_MUCUS_DAYS_FOR_PEAK_DAY)..index)
                        .all { it.symptomEntry.hasNonPeakMucus }
                } catch (e: IndexOutOfBoundsException) {
                    //If we aren't even 3 days into the cycle, then we do not have three days of non-peak mucus
                    false
                }
                val nextDayHasMucus = days.getOrNull(index + 1)?.symptomEntry?.let {
                    it.hasNonPeakMucus || it.hasPeakMucus
                } ?: false

                //This is the last day of peak mucus (tomorrow is not peak mucus)
                //Or this is the last day of non-peak mucus, and last 3 days have all been non-peak mucus
                if ((todayHasPeakMucus && !tomorrowHasPeakMucus) || (lastThreeDaysAreNonPeakMucus && !nextDayHasMucus)) {
                    index
                } else null
            }.toSet()
        }

    @IgnoredOnParcel
    val peakDayRangeIndexes: Set<Int>
        get() = peakDayIndexes.map {
            (it..(it + NUM_DAYS_IN_PEAK_RANGE)).filter { it < days.size }
        }.flatten().toSet()
}
