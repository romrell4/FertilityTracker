package com.romrell4.fertility_tracker.usecase

import com.romrell4.fertility_tracker.domain.SymptomEntry
import com.romrell4.fertility_tracker.repo.FertilityTrackingRepository

interface SaveSymptomEntryUseCase {
    suspend fun execute(request: SymptomEntry)
}

class SaveSymptomEntryUseCaseImpl(
    private val repo: FertilityTrackingRepository
) : SaveSymptomEntryUseCase {
    override suspend fun execute(request: SymptomEntry) {
        repo.saveSymptomEntry(request)
    }
}