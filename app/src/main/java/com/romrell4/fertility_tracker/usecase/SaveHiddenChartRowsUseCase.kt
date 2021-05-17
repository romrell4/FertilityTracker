package com.romrell4.fertility_tracker.usecase

import com.romrell4.fertility_tracker.domain.ChartRow
import com.romrell4.fertility_tracker.repo.FertilityTrackingRepository

interface SaveHiddenChartRowsUseCase {
    fun execute(rows: List<ChartRow>)
}

class SaveHiddenChartRowsUseCaseImpl(
    private val fertilityTrackingRepo: FertilityTrackingRepository
) : SaveHiddenChartRowsUseCase {
    override fun execute(rows: List<ChartRow>) = fertilityTrackingRepo.saveHiddenChartRows(rows)
}
