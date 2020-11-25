package com.romrell4.fertility_tracker.domain

import io.mockk.every
import io.mockk.mockk
import io.mockk.spyk
import org.junit.Test
import java.time.LocalDate
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertNull

class CycleTest {
    @Test
    fun `test start and end dates`() {
        fun day(date: LocalDate): Cycle.Day = mockk {
            every { symptomEntry.date } returns date
        }

        //Test no dates - should throw exception
        Cycle(days = emptyList()).apply {
            assertFailsWith<IllegalStateException> { startDate }
            assertFailsWith<IllegalStateException> { endDate }
        }

        //Test a bunch of random dates - should calculate correctly
        Cycle(days = (1..28).shuffled().map { day(LocalDate.of(2020, 1, it)) }).apply {
            assertEquals(LocalDate.of(2020, 1, 1), startDate)
            assertEquals(LocalDate.of(2020, 1, 28), endDate)
        }
    }

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
    fun `test rule of three indexes`() {
        fun cycle(numDays: Int, peakDays: Set<Int> = emptySet(), spottingDays: Set<Int> = emptySet(), inDoubtDays: Set<Int> = emptySet()): Cycle =
            spyk(Cycle(days = (0 until numDays).map { mockk() })) {
                every { peakDayIndexes } returns peakDays
                every { spottingIndexes } returns spottingDays
                every { whenInDoubtIndexes } returns inDoubtDays
            }

        //Test no peak or spotting days - should have no ROT range
        cycle(numDays = 0).ruleOfThreeIndexes.run {
            assertEquals(emptySet(), this)
        }

        //Test single peak day with a full range - should have a single full range
        cycle(numDays = 10, peakDays = setOf(0)).ruleOfThreeIndexes.run {
            assertEquals(setOf(0, 1, 2, 3), this)
        }

        //Test single spotting day with a truncated range - should only have the indexes in range
        cycle(numDays = 2, spottingDays = setOf(0)).ruleOfThreeIndexes.run {
            assertEquals(setOf(0, 1), this)
        }

        //Test multiple peak days that don't overlap - should be the union of the two ranges
        cycle(numDays = 10, peakDays = setOf(0, 5)).ruleOfThreeIndexes.run {
            assertEquals(setOf(0, 1, 2, 3, 5, 6, 7, 8), this)
        }

        //Test multiple spotting days that overlap - should be the union with duplicates removed
        cycle(numDays = 10, spottingDays = setOf(0, 2)).ruleOfThreeIndexes.run {
            assertEquals(setOf(0, 1, 2, 3, 4, 5), this)
        }

        //Test both peak and spotting days - should be the union of everything with dupes removed
        cycle(numDays = 10, peakDays = setOf(1, 8), spottingDays = setOf(0, 2)).ruleOfThreeIndexes.run {
            assertEquals(setOf(0, 1, 2, 3, 4, 5, 8, 9), this)
        }

        //Test a when in doubt day as well
        cycle(numDays = 10, inDoubtDays = setOf(1)).ruleOfThreeIndexes.run {
            assertEquals(setOf(1, 2, 3, 4), this)
        }
    }

    @Test
    fun `test coverline`() {
        fun day(temp: Double? = null, isAbnormal: Boolean = false): Cycle.Day = mockk {
            every { symptomEntry.temperature } returns temp?.let {
                mockk {
                    every { value } returns it
                    every { abnormal } returns isAbnormal
                }
            }
        }
        //No days - no coverline
        assertNull(Cycle(days = emptyList()).coverlineValue)

        //Tons of days, no temps - no coverline
        assertNull(Cycle(days = (0..30).map { day() }).coverlineValue)

        //Tons of abnormal days - no coverline
        assertNull(Cycle(days = (0..30).map { day(isAbnormal = true) }).coverlineValue)

        //Days all increasing, but barely not enough days - no coverline
        assertNull(Cycle(days = (1..8).map { day(temp = it.toDouble()) }).coverlineValue)

        //Days all increasing, and exact number of days - coverline
        assertEquals(6.1, Cycle(days = (1..9).map { day(temp = it.toDouble()) }).coverlineValue)

        //Enough days, but always decreasing - no coverline
        assertNull(Cycle(days = (30 downTo 1).map { day(temp = it.toDouble()) }).coverlineValue)

        //All equal - no coverline
        assertNull(Cycle(days = (0..30).map { day(temp = 0.0) }).coverlineValue)

        //One value that screws it up - no coverline
        assertNull(Cycle(days = listOf(0, 10, 0, 0, 0, 0, 0, 1, 1, 1).map { day(temp = it.toDouble()) }).coverlineValue)

        //Real live cycle - coverline
        assertEquals(
            97.6, Cycle(
                days = listOf(
                    day(96.55),
                    day(96.8),
                    day(97.11),
                    day(96.87),
                    day(95.68, isAbnormal = true),
                    day(97.04),
                    day(96.88),
                    day(96.68),
                    day(96.80),
                    day(97.5),
                    day(96.72),
                    day(97.07),
                    day(97.01),
                    day(97.84),
                    day(97.77),
                    day(98.05),
                    day(97.45)
                )
            ).coverlineValue
        )
    }
}
