package com.romrell4.fertility_tracker.usecase

import com.romrell4.fertility_tracker.domain.Cycle
import com.romrell4.fertility_tracker.domain.SymptomEntry
import com.romrell4.fertility_tracker.repo.FertilityTrackingRepository
import java.time.LocalDate

interface GetAllCyclesUseCase {
    suspend fun execute(): List<Cycle>
}

class GetAllCyclesUseCaseImpl(
    private val fertilityTrackingRepo: FertilityTrackingRepository
) : GetAllCyclesUseCase {
    override suspend fun execute(): List<Cycle> {
        //TODO: Compute cycles instead of entries
        val entries = fertilityTrackingRepo.getAllSymptomEntries().values.toList()

        return (0..3).map { cycleIndex ->
            Cycle(
                cycleNumber = cycleIndex + 1,
                days = (0..27).map { dayIndex ->
                    Cycle.Day(
                        dayOfCycle = dayIndex + 1,
                        SymptomEntry(
                            date = LocalDate.of(2019, cycleIndex + 1, dayIndex + 1),
                            sensation = SymptomEntry.Sensation.values().random(),
                            mucus = listOf(SymptomEntry.Mucus(
                                consistency = SymptomEntry.Mucus.Consistency.values().random(),
                                color = SymptomEntry.Mucus.Color.values().random(),
                                dailyOccurrences = listOf(1, 2, 3).random()
                            ), null).random(),
                            bleeding = when (dayIndex) {
                                0,1,2,3 -> SymptomEntry.Bleeding.MODERATE
                                else -> null
                            }
                        )
                    )
                }
            )
        }
    }

    fun List<SymptomEntry>.getCycleStartIndexes(): List<Int> {
        val cycleStartIndexes = mutableListOf<Int>()
        this.forEachIndexed { index, entry ->

        }
        return cycleStartIndexes
    }
}