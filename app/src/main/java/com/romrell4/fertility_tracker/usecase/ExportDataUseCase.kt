package com.romrell4.fertility_tracker.usecase

import com.romrell4.fertility_tracker.repo.FertilityTrackingRepository

interface ExportDataUseCase {
    fun execute(): String?
}

class ExportDataUseCaseImpl(private val repo: FertilityTrackingRepository) : ExportDataUseCase {
    override fun execute(): String? {
        return repo.exportData()
    }
}
