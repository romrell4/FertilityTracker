package com.romrell4.fertility_tracker.usecase

import com.romrell4.fertility_tracker.domain.SymptomEntry
import com.romrell4.fertility_tracker.repo.FertilityTrackingRepository

interface GetAllEntriesUseCase {
    suspend fun execute(): List<SymptomEntry>
}

class GetAllEntriesUseCaseImpl(
    private val fertilityTrackingRepo: FertilityTrackingRepository
) : GetAllEntriesUseCase {
    override suspend fun execute(): List<SymptomEntry> {
        return fertilityTrackingRepo.getAllSymptomEntries().values.toList()
    }
}