package com.romrell4.fertility_tracker.usecase

import com.romrell4.fertility_tracker.repo.FertilityTrackingRepository

interface ImportDataUseCase {
    fun execute(serializedData: String)
}

class ImportDataUseCaseImpl(private val repo: FertilityTrackingRepository) : ImportDataUseCase {
    override fun execute(serializedData: String) {
        repo.importData(serializedData)
    }
}
