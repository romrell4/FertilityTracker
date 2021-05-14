package com.romrell4.fertility_tracker.domain

import android.os.Parcelable
import com.fasterxml.jackson.annotation.JsonIgnore
import com.romrell4.fertility_tracker.support.toEmoji
import kotlinx.android.parcel.IgnoredOnParcel
import kotlinx.android.parcel.Parcelize
import java.time.LocalDate
import java.time.LocalTime

@Parcelize
data class SymptomEntry(
    val date: LocalDate,
    val sensation: Sensation? = null,
    val observation: Observation? = null,
    val mucus: Mucus? = null,
    val bleeding: Bleeding? = null,
    val sex: Sex? = null,
    val inDoubt: Boolean? = null,
    val temperature: Temperature? = null,
    val mood: Mood? = null,
    val energy: Energy? = null,
    val notes: String? = null
) : Parcelable, Comparable<SymptomEntry> {
    enum class Sensation(override val displayText: String) : Symptom {
        DRY("Dry"),
        SMOOTH("Smooth"),
        LUBRICATIVE("Lubricative")
    }

    enum class Observation(override val displayText: String) : Symptom {
        DRY("Dry"),
        DAMP("Damp"),
        WET("Wet"),
        SHINY("Shiny")
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

    enum class Mood(text: String, emojiUnicode: Int) : Symptom {
        HAPPY("Happy", 0x1F603),
        IRRITABLE("Irritable", 0x1F612),
        ANGRY("Angry", 0x1F621),
        SAD("Sad/Sensitive", 0x1F625),
        CHILL("Chill", 0x1F60C);

        val emoji: String = emojiUnicode.toEmoji()

        override val displayText: String = "$emoji $text"
    }

    enum class Energy(override val displayText: String) : Symptom {
        LOW("Low"),
        MEDIUM("Medium"),
        HIGH("High"),
    }

    interface Symptom {
        val displayText: String
    }

    @IgnoredOnParcel
    @get:JsonIgnore
    val hasPeakMucus: Boolean
        get() = mucus?.consistency == Mucus.Consistency.STRETCHY ||
                mucus?.color == Mucus.Color.CLEAR ||
                sensation == Sensation.LUBRICATIVE

    @IgnoredOnParcel
    @get:JsonIgnore
    val hasNonPeakMucus: Boolean
        get() = mucus != null && !hasPeakMucus

    @IgnoredOnParcel
    @get:JsonIgnore
    val hasRealBleeding: Boolean
        get() = bleeding != null && bleeding != Bleeding.SPOTTING

    override fun compareTo(other: SymptomEntry): Int = compareValuesBy(this, other, { it.date })
}
