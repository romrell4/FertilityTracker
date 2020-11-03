package com.romrell4.fertility_tracker.usecase

import com.romrell4.fertility_tracker.domain.SymptomEntry
import com.romrell4.fertility_tracker.repo.FertilityTrackingRepository
import java.time.LocalDate

interface FindSymptomEntryUseCase {
    fun execute(date: LocalDate): SymptomEntry?
}

class FindSymptomEntryUseCaseImpl(
    private val repo: FertilityTrackingRepository
) : FindSymptomEntryUseCase {
    override fun execute(date: LocalDate): SymptomEntry? = repo.getAllSymptomEntries()[date]
}