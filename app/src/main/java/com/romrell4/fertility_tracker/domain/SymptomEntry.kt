package com.romrell4.fertility_tracker.domain

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize
import java.time.LocalDate
import java.util.*

@Parcelize
data class SymptomEntry(
    val id: UUID = UUID.randomUUID(),
    val date: LocalDate,
    val sensation: Sensation? = null,
    //TODO: Necessary?
    val subSensation: SubSensation? = null,
    val mucus: Mucus? = null,
    val bleeding: Bleeding? = null,
    val sex: Sex? = null,
    val notes: String? = null
) : Parcelable {
    enum class Sensation(val displayText: String) {
        DRY("Dry"),
        SMOOTH("Smooth"),
        LUBRICATIVE("Lubricative")
    }

    enum class SubSensation {
        DRY,
        DAMP,
        WET,
        SHINY;

        companion object {
            fun getSubSensations(sensation: Sensation) = when (sensation) {
                Sensation.DRY, Sensation.SMOOTH -> listOf(DRY, DAMP, WET, SHINY)
                Sensation.LUBRICATIVE -> listOf(DAMP, WET, SHINY)
            }
        }
    }

    @Parcelize
    data class Mucus(
        val consistency: Consistency? = null,
        val color: Color? = null,
        val dailyOccurrences: Int = 1
    ) : Parcelable

    enum class Consistency {
        STICKY,
        TACKY,
        STRETCHY,
        PASTY,
        GUMMY_GLUEY
    }

    enum class Color {
        CLOUDY,
        CLEAR,
        RED_PINK,
        BROWN,
        YELLOW
    }

    enum class Bleeding {
        SPOTTING,
        VERY_LIGHT,
        LIGHT,
        MODERATE,
        HEAVY
    }

    enum class Sex {
        PROTECTED,
        UNPROTECTED
    }
}
