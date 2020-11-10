package com.romrell4.fertility_tracker.domain

import android.os.Parcelable
import kotlinx.android.parcel.IgnoredOnParcel
import kotlinx.android.parcel.Parcelize
import java.time.LocalDate
import java.time.LocalTime

@Parcelize
data class SymptomEntry(
    val date: LocalDate,
    val sensation: Sensation? = null,
    val mucus: Mucus? = null,
    val bleeding: Bleeding? = null,
    val sex: Sex? = null,
    val temperature: Temperature? = null,
    val notes: String? = null
) : Parcelable, Comparable<SymptomEntry> {
    enum class Sensation(override val displayText: String) : Symptom {
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
        LIGHT("Light"),
        MODERATE("Moderate"),
        HEAVY("Heavy")
    }

    enum class Sex(override val displayText: String) : Symptom {
        PROTECTED("Protected"),
        UNPROTECTED("Unprotected")
    }

    @Parcelize
    data class Temperature(
        val time: LocalTime,
        val value: Double,
        val abnormal: Boolean = false,
        val abnormalNotes: String? = null
    ) : Parcelable

    interface Symptom {
        val displayText: String
    }

    @IgnoredOnParcel
    val hasPeakMucus: Boolean
        get() = mucus?.consistency == Mucus.Consistency.STRETCHY ||
                mucus?.color == Mucus.Color.CLEAR ||
                sensation == Sensation.LUBRICATIVE

    @IgnoredOnParcel
    val hasNonPeakMucus: Boolean
        get() = mucus != null && !hasPeakMucus

    @IgnoredOnParcel
    val hasRealBleeding: Boolean
        get() = bleeding != null && bleeding != Bleeding.SPOTTING

    override fun compareTo(other: SymptomEntry): Int = compareValuesBy(this, other, { it.date })
}
