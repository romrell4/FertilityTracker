package com.romrell4.fertility_tracker.domain

import io.mockk.every
import io.mockk.mockk
import io.mockk.spyk
import org.junit.Test
import kotlin.test.assertEquals

class CycleTest {
    @Test
    fun `test peak day indexes`() {
        fun day(peakMucus: Boolean = false, mucus: Boolean = false): Cycle.Day = mockk {
            every { compareTo(any()) } returns 1
            every { symptomEntry.hasPeakMucus } returns peakMucus
            every { symptomEntry.hasNonPeakMucus } returns mucus
        }

        //Test no days of mucus at all - no peak day
        Cycle(days = listOf(day())).peakDayIndexes.run {
            assertEquals(emptySet(), this)
        }

        //Test three days of mucus in a row - still no peak day
        Cycle(days = listOf(true, true, true).map { day(mucus = it) }).peakDayIndexes.run {
            assertEquals(emptySet(), this)
        }

        //Test four days of mucus in a row - PEAK DAY!
        Cycle(
            days = listOf(true, true, true, true, false, false, false, false, false).map { day(mucus = it) }
        ).peakDayIndexes.run {
            assertEquals(setOf(3), this)
        }

        //Test more than four days of mucus - only the last should be peak day
        Cycle(
            days = listOf(true, true, true, true, true, true).map { day(mucus = it) }
        ).peakDayIndexes.run {
            assertEquals(setOf(5), this)
        }

        //Test more than four days of mucus, followed by non-mucus - the last mucus should be peak day
        Cycle(
            days = listOf(true, true, true, true, true, true, false, false).map { day(mucus = it) }
        ).peakDayIndexes.run {
            assertEquals(setOf(5), this)
        }

        //Test single peak mucus - should be peak day
        Cycle(days = listOf(day(peakMucus = true))).peakDayIndexes.run {
            assertEquals(setOf(0), this)
        }

        //Test multiple days of peak mucus, only the last should be peak day
        Cycle(days = listOf(true, true).map { day(peakMucus = it) }).peakDayIndexes.run {
            assertEquals(setOf(1), this)
        }

        //Test non-peak mucus separated by peak mucus. Should only take peak mucus range into account
        Cycle(
            days = listOf(
                day(),
                day(mucus = true),
                day(mucus = true),
                day(peakMucus = true),
                day(mucus = true),
                day(mucus = true),
                day(mucus = true),
                day()
            )
        ).peakDayIndexes.run {
            assertEquals(setOf(3), this)
        }

        //Test multiple peak days of peak mucus
        Cycle(
            days = listOf(
                day(),
                day(peakMucus = true),
                day(),
                day(peakMucus = true)
            )
        ).peakDayIndexes.run {
            assertEquals(setOf(1, 3), this)
        }

        //Test multiple peak days of non-peak mucus
        Cycle(
            days = listOf(
                day(),
                day(mucus = true),
                day(mucus = true),
                day(mucus = true),
                day(mucus = true),
                day(),
                day(mucus = true),
                day(mucus = true),
                day(mucus = true),
                day(mucus = true),
            )
        ).peakDayIndexes.run {
            assertEquals(setOf(4, 9), this)
        }

        //Test multiple peak days of both types
        Cycle(
            days = listOf(
                day(),
                day(mucus = true),
                day(peakMucus = true),
                day(mucus = true),
                day(mucus = true),
                day(mucus = true),
                day(mucus = true),
                day()
            )
        ).peakDayIndexes.run {
            assertEquals(setOf(2, 6), this)
        }

        //Test many non-peak mucus days running into peak mucus day - shouldn't count non-peak mucus
        Cycle(
            days = listOf(
                day(mucus = true),
                day(mucus = true),
                day(mucus = true),
                day(mucus = true),
                day(mucus = true),
                day(mucus = true),
                day(peakMucus = true)
            )
        ).peakDayIndexes.run {
            assertEquals(setOf(6), this)
        }
    }

    @Test
    fun `test peak day ranges`() {
        fun cycle(numDays: Int, peakDays: Set<Int> = emptySet()): Cycle =
            spyk(Cycle(days = (0 until numDays).map { mockk() })) {
                every { peakDayIndexes } returns peakDays
            }

        //Test no peak days - should have no peak range
        cycle(numDays = 0).peakDayRangeIndexes.run {
            assertEquals(emptySet(), this)
        }

        //Test single peak day with a full range - should have a single full range
        cycle(numDays = 10, peakDays = setOf(0)).peakDayRangeIndexes.run {
            assertEquals(setOf(0, 1, 2, 3), this)
        }

        //Test single peak day with a truncated range - should only have the indexes in range
        cycle(numDays = 2, peakDays = setOf(0)).peakDayRangeIndexes.run {
            assertEquals(setOf(0, 1), this)
        }

        //Test multiple peak days that don't overlap - should be the union of the two ranges
        cycle(numDays = 10, peakDays = setOf(0, 5)).peakDayRangeIndexes.run {
            assertEquals(setOf(0, 1, 2, 3, 5, 6, 7, 8), this)
        }

        //Test multiple peak days that overlap - should be the union with duplicates removed
        cycle(numDays = 10, peakDays = setOf(0, 2)).peakDayRangeIndexes.run {
            assertEquals(setOf(0, 1, 2, 3, 4, 5), this)
        }
    }
}