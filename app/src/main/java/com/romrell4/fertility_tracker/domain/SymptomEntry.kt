package com.romrell4.fertility_tracker.domain

import android.os.Parcelable
import kotlinx.android.parcel.IgnoredOnParcel
import kotlinx.android.parcel.Parcelize
import java.time.LocalDate

@Parcelize
data class SymptomEntry(
    val date: LocalDate,
    val sensation: Sensation? = null,
    val mucus: Mucus? = null,
    val bleeding: Bleeding? = null,
    val sex: Sex? = null,
    val notes: String? = null
) : Parcelable {
    enum class Sensation(override val displayText: String) : Symptom {
        DRY("Dry"),
        SMOOTH("Smooth"),
        LUBRICATIVE("Lubricative")
    }

    @Parcelize
    data class Mucus(
        val consistency: Consistency? = null,
        val color: Color? = null,
        val dailyOccurrences: Int
    ) : Parcelable {
        enum class Consistency(override val displayText: String) : Symptom {
            STICKY("Sticky"),
            TACKY("Tacky"),
            STRETCHY("Stretchy"),
            PASTY("Pasty"),
            GUMMY_GLUEY("Gummy/Gluey")
        }

        enum class Color(override val displayText: String) : Symptom {
            CLOUDY("Cloudy"),
            CLEAR("Clear"),
            RED_PINK("Red/Pink"),
            BROWN("Brown"),
            YELLOW("Yellow")
        }
    }

    enum class Bleeding(override val displayText: String) : Symptom {
        SPOTTING("Spotting"),
        VERY_LIGHT("Very Light"),
        LIGHT("Light"),
        MODERATE("Moderate"),
        HEAVY("Heavy")
    }

    enum class Sex(override val displayText: String) : Symptom {
        PROTECTED("Protected"),
        UNPROTECTED("Unprotected")
    }

    interface Symptom {
        val displayText: String
    }

    @IgnoredOnParcel
    val hasPeakMucus: Boolean
        get() = mucus?.consistency == Mucus.Consistency.STRETCHY ||
                mucus?.color == Mucus.Color.CLEAR ||
                sensation == Sensation.LUBRICATIVE
}
