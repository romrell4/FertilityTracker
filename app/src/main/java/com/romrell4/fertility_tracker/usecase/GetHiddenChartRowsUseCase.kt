package com.romrell4.fertility_tracker.usecase

import com.romrell4.fertility_tracker.domain.ChartRow
import com.romrell4.fertility_tracker.repo.FertilityTrackingRepository

interface GetHiddenChartRowsUseCase {
    fun execute(): List<ChartRow>
}

class GetHiddenChartRowsUseCaseImpl(
    private val fertilityTrackingRepo: FertilityTrackingRepository
) : GetHiddenChartRowsUseCase {
    override fun execute(): List<ChartRow> = fertilityTrackingRepo.getHiddenChartRows()
}
