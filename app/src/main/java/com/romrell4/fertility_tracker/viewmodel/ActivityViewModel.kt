package com.romrell4.fertility_tracker.viewmodel

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import com.romrell4.fertility_tracker.usecase.ExportDataUseCase
import com.romrell4.fertility_tracker.usecase.ImportDataUseCase
import com.romrell4.fertility_tracker.view.DI

class ActivityViewModel @JvmOverloads constructor(
    private val savedStateHandle: SavedStateHandle,
    private val exportDataUseCase: ExportDataUseCase = DI.instance.exportDataUseCase,
    private val importDataUseCase: ImportDataUseCase = DI.instance.importDataUseCase
) : ViewModel() {
    fun exportData(): String? {
        return exportDataUseCase.execute()
    }

    fun importData(serializedString: String) {
        importDataUseCase.execute(serializedString)
    }
}
