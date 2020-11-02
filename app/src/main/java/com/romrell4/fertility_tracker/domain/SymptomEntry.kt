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

    @Parcelize
    data class Mucus(
        val consistency: Consistency? = null,
        val color: Color? = null,
        val dailyOccurrences: Int = 1
    ) : Parcelable {
        enum class Consistency(val displayText: String) {
            STICKY("Sticky"),
            TACKY("Tacky"),
            STRETCHY("Stretchy"),
            PASTY("Pasty"),
            GUMMY_GLUEY("Gummy/Gluey")
        }

        enum class Color(val displayText: String) {
            CLOUDY("Cloudy"),
            CLEAR("Clear"),
            RED_PINK("Red/Pink"),
            BROWN("Brown"),
            YELLOW("Yellow")
        }
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
