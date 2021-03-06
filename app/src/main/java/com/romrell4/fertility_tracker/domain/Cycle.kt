package com.romrell4.fertility_tracker.domain

import android.os.Parcelable
import kotlinx.parcelize.IgnoredOnParcel
import kotlinx.parcelize.Parcelize
import java.time.LocalDate

private const val NUM_NON_PEAK_MUCUS_DAYS_FOR_PEAK_DAY = 3
private const val NUM_RULE_OF_THREE_DAYS = 3
private const val COVERLINE_PREVIOUS_TEMPS = 6
private const val COVERLINE_FOLLOWING_TEMPS = 3
private const val COVERLINE_CONST = 0.1

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
    val startDate: LocalDate
        get() = days.map { it.symptomEntry.date }.minOrNull() ?: throw IllegalStateException("No days in cycle")

    @IgnoredOnParcel
    val endDate: LocalDate
        get() = days.map { it.symptomEntry.date }.maxOrNull() ?: throw IllegalStateException("No days in cycle")

    @IgnoredOnParcel
    val coverlineValue: Double?
        get() {
            val normalTemps = days.mapNotNull { it.symptomEntry.temperature }.filter { !it.abnormal }.map { it.value }
            for (i in COVERLINE_PREVIOUS_TEMPS..(normalTemps.size - COVERLINE_FOLLOWING_TEMPS)) {
                val maxOfPrevious = normalTemps.slice(i - COVERLINE_PREVIOUS_TEMPS until i).maxOrNull() ?: 0.0
                val minOfFollowing = normalTemps.slice(i until i + COVERLINE_FOLLOWING_TEMPS).minOrNull() ?: 0.0
                if (minOfFollowing > maxOfPrevious) {
                    return maxOfPrevious + COVERLINE_CONST
                }
            }
            return null
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
    val spottingIndexes: Set<Int>
        get() = days.sorted().mapIndexedNotNull { index, day ->
            if (day.symptomEntry.bleeding == SymptomEntry.Bleeding.SPOTTING) index else null
        }.toSet()

    @IgnoredOnParcel
    val whenInDoubtIndexes: Set<Int>
        get() = days.sorted().mapIndexedNotNull { index, day ->
            if (day.symptomEntry.inDoubt == true) index else null
        }.toSet()

    @IgnoredOnParcel
    val ruleOfThreeIndexes: Set<Int>
        get() = (peakDayIndexes + spottingIndexes + whenInDoubtIndexes).map { index ->
            (index..(index + NUM_RULE_OF_THREE_DAYS)).filter { it < days.size }
        }.flatten().toSet()
}
