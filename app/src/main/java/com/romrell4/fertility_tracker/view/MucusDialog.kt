package com.romrell4.fertility_tracker.view

import android.content.Context
import androidx.appcompat.app.AlertDialog
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.romrell4.fertility_tracker.R

class MucusDialog(
    context: Context
) : AlertDialog(context) {
    private val binding by lazy { }

    init {
        MaterialAlertDialogBuilder(context)
            .setView(R.layout.mucus_dialog)
            .show()
    }
}