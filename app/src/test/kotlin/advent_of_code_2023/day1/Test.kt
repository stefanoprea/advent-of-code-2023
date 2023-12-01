package advent_of_code_2023.day0

import kotlin.test.Test
import kotlin.test.assertEquals
import advent_of_code_2023.Days


class MyTest {
    val day = "1"

    @Test
    fun A() {
        assertEquals(
            "",
            Days.getInput(day).let {
                it
                    .trim('\n')
                    .lines()
                    .first()
            },
        )
    }

    @Test
    fun B() {
        assertEquals(
            "",
            Days.getInput(day).let {
                it
                    .trim('\n')
                    .lines()
                    .first()
            },
        )
    }
}
