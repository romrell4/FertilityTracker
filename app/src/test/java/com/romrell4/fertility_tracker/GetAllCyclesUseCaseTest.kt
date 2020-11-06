package com.romrell4.fertility_tracker

import com.romrell4.fertility_tracker.domain.SymptomEntry
import com.romrell4.fertility_tracker.repo.FertilityTrackingRepository
import com.romrell4.fertility_tracker.usecase.GetAllCyclesUseCaseImpl
import io.mockk.every
import io.mockk.mockk
import org.junit.Test
import java.time.LocalDate
import kotlin.test.assertEquals

class GetAllCyclesUseCaseTest {
    private val repo = mockk<FertilityTrackingRepository>()
    private val useCase = GetAllCyclesUseCaseImpl(repo)

    @Test
    fun `test filling in blanks`() {
        with(useCase) {
            //Test no entries
            emptyList<SymptomEntry>().apply {
                val newList = fillInBlanks()
                assertEquals(this, newList)
            }

            //Test single entry
            listOf(SymptomEntry(date = LocalDate.of(2020, 1, 1), notes = "Test")).apply {
                val newList = fillInBlanks()
                assertEquals(this, newList)
            }

            //Test many entries with no blanks to fill
            (1..20).map {
                SymptomEntry(date = LocalDate.of(2020, 1, it))
            }.apply {
                val newList = fillInBlanks()
                assertEquals(this, newList)
            }

            //Test many entries with blanks to fill
            listOf(
                SymptomEntry(date = LocalDate.of(2019, 1, 1)),
                SymptomEntry(date = LocalDate.of(2020, 1, 1))
            ).fillInBlanks().apply {
                assertEquals(366, size)
            }
        }
    }

    @Test
    fun `test splitting up symptom entries into cycles`() {
        fun entry(bleeding: Boolean): SymptomEntry = mockk {
            every { hasRealBleeding } returns bleeding
            every { compareTo(any()) } returns 1
        }

        with(useCase) {
            //Test empty list - should get back no cycles
            emptyList<SymptomEntry>().splitIntoCycles().apply {
                assertEquals(0, size)
            }

            //Test never bleeding - should get a single cycle
            (1..20).map { entry(bleeding = false) }.splitIntoCycles().apply {
                assertEquals(1, size)
            }

            //Test bleeding on the first day - should get a single cycle
            listOf(true, false, false, false).map { entry(bleeding = it) }.splitIntoCycles().apply {
                assertEquals(1, size)
            }

            //Test bleeding every day, still only one cycle
            listOf(true, true, true, true).map { entry(bleeding = it) }.splitIntoCycles().apply {
                assertEquals(1, size)
            }

            //Test bleeding on the second day, but not the first - should get two cycles
            listOf(false, true, false, false).map { entry(bleeding = it) }.splitIntoCycles().apply {
                assertEquals(2, size)
                assertEquals(1, get(0).size)
                assertEquals(3, get(1).size)
            }

            //Test real use case
            listOf(
                true, true, true, false, false, false, false,
                true, true, false, false,
                true, false,
                true
            ).map { entry(bleeding = it) }.splitIntoCycles().apply {
                assertEquals(4, size)
                assertEquals(7, get(0).size)
                assertEquals(4, get(1).size)
                assertEquals(2, get(2).size)
                assertEquals(1, get(3).size)
            }
        }
    }
}