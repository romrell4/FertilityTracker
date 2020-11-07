package com.romrell4.fertility_tracker.usecase

import androidx.annotation.VisibleForTesting
import com.romrell4.fertility_tracker.domain.Cycle
import com.romrell4.fertility_tracker.domain.SymptomEntry
import com.romrell4.fertility_tracker.repo.FertilityTrackingRepository

interface GetAllCyclesUseCase {
    fun execute(): List<Cycle>
}

class GetAllCyclesUseCaseImpl(
    private val fertilityTrackingRepo: FertilityTrackingRepository
) : GetAllCyclesUseCase {
    override fun execute(): List<Cycle> {
        val allEntries = fertilityTrackingRepo.getAllSymptomEntries().values.toList()
        return allEntries.fillInBlanks().splitIntoCycles()
            .mapIndexed { cycleIndex, entries ->
                Cycle(
                    cycleNumber = cycleIndex + 1,
                    days = entries.mapIndexed { dayIndex, entry ->
                        Cycle.Day(
                            dayOfCycle = dayIndex + 1,
                            symptomEntry = entry
                        )
                    }
                )
            }.sortedByDescending { it.cycleNumber }
    }

    @VisibleForTesting(otherwise = VisibleForTesting.PRIVATE)
    fun List<SymptomEntry>.fillInBlanks(): List<SymptomEntry> {
        val withBlanks = mutableListOf<SymptomEntry>()
        this.sorted().forEach { entry ->
            while (withBlanks.lastOrNull() != null && withBlanks.last().date != entry.date.minusDays(1)) {
                withBlanks.add(SymptomEntry(date = withBlanks.last().date.plusDays(1)))
            }
            withBlanks.add(entry)
        }
        return withBlanks
    }

    @VisibleForTesting(otherwise = VisibleForTesting.PRIVATE)
    fun List<SymptomEntry>.splitIntoCycles(): List<List<SymptomEntry>> {
        //Start with 0, since the first entry will always be the start of a cycle
        val cycleStartIndexes = mutableListOf(0)
        var wasBleeding = false
        this.sorted().forEachIndexed { index, entry ->
            //Start of a cycle is the first day of non-spotting bleeding.
            //If you have multiple days of bleeding in a row, it is not a new cycle
            if (entry.hasRealBleeding && !wasBleeding) {
                cycleStartIndexes.add(index)
            }
            wasBleeding = entry.hasRealBleeding
        }
        //Add one last element so that we can properly slice
        cycleStartIndexes.add(size)
        return cycleStartIndexes.distinct().zipWithNext { i1, i2 ->
            this.slice(i1 until i2)
        }
    }
}