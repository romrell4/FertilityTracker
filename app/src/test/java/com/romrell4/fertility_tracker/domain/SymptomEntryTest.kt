package com.romrell4.fertility_tracker.domain

import io.mockk.every
import io.mockk.mockk
import io.mockk.spyk
import org.junit.Test
import java.time.LocalDate
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class SymptomEntryTest {
    @Test
    fun `test has peak mucus`() {
        //Test no mucus or sensation - false
        assertFalse(entry().hasPeakMucus)

        //Test mucus, but it's not stretchy or clear - false
        assertFalse(entry(mucus = SymptomEntry.Mucus()).hasPeakMucus)

        //Test stretchy mucus - true
        assertTrue(entry(mucus = SymptomEntry.Mucus(consistency = SymptomEntry.Mucus.Consistency.STRETCHY)).hasPeakMucus)

        //Test clear mucus - true
        assertTrue(entry(mucus = SymptomEntry.Mucus(color = SymptomEntry.Mucus.Color.CLEAR)).hasPeakMucus)

        //Test lubricative sensations - true
        assertTrue(entry(sensation = SymptomEntry.Sensation.LUBRICATIVE).hasPeakMucus)
    }

    @Test
    fun `test has non-peak mucus`() {
        //Test doesn't have mucus - false
        assertFalse(entry().hasNonPeakMucus)

        //Test has mucus, but it's peak mucus - false
        assertFalse(entry(mucus = mockk(), peakMucus = true).hasNonPeakMucus)

        //Test has mucus that's NOT peak mucus - true
        assertTrue(entry(mucus = mockk(), peakMucus = false).hasNonPeakMucus)
    }

    @Test
    fun `test has real bleeding`() {
        //Test doesn't have bleeding - false
        assertFalse(entry().hasRealBleeding)

        //Test has spotting - false
        assertFalse(entry(bleeding = SymptomEntry.Bleeding.SPOTTING).hasRealBleeding)

        //Test has other bleeding - true
        assertTrue(entry(bleeding = SymptomEntry.Bleeding.LIGHT).hasRealBleeding)
    }

    private fun entry(
        mucus: SymptomEntry.Mucus? = null,
        sensation: SymptomEntry.Sensation? = null,
        bleeding: SymptomEntry.Bleeding? = null,
        peakMucus: Boolean? = null
    ): SymptomEntry = spyk(
        SymptomEntry(
            date = LocalDate.now(),
            mucus = mucus,
            sensation = sensation,
            bleeding = bleeding
        )
    ) {
        peakMucus?.let { every { hasPeakMucus } returns it }
    }
}