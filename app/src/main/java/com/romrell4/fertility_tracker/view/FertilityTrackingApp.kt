package com.romrell4.fertility_tracker.view

import android.app.Application
import android.content.Context
import com.romrell4.fertility_tracker.repo.FertilityTrackingRepository
import com.romrell4.fertility_tracker.repo.FertilityTrackingRepositoryImpl
import com.romrell4.fertility_tracker.usecase.*

class FertilityTrackingApp : Application() {
    override fun onCreate() {
        super.onCreate()

        DI.instance = DI(applicationContext)
    }
}

class DI(
    private val context: Context
) {
    companion object {
        lateinit var instance: DI
            internal set
    }

    //Repos
    private val fertilityTrackingRepo: FertilityTrackingRepository by lazy { FertilityTrackingRepositoryImpl(context) }

    //Use cases
    val saveSymptomEntryUseCase: SaveSymptomEntryUseCase by lazy { SaveSymptomEntryUseCaseImpl(fertilityTrackingRepo) }
    val findSymptomEntryUseCase: FindSymptomEntryUseCase by lazy { FindSymptomEntryUseCaseImpl(fertilityTrackingRepo) }
    val getAllCyclesUseCase: GetAllCyclesUseCase by lazy { GetAllCyclesUseCaseImpl(fertilityTrackingRepo) }
    val exportDataUseCase: ExportDataUseCase by lazy { ExportDataUseCaseImpl(fertilityTrackingRepo) }
    val importDataUseCase: ImportDataUseCase by lazy { ImportDataUseCaseImpl(fertilityTrackingRepo) }
}
