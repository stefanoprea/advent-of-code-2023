package advent_of_code_2023.day6

import kotlin.test.Test
import kotlin.test.assertEquals
import advent_of_code_2023.Days


class MyTest {
    val day = "6"

    @Test
    fun A() {
        assertEquals(
            "771628",
            Days.getInput(day).let {
                it
                    .trim('\n')
                    .lines()
                    .map { it.split(":")[1].split(" ").filter { it != "" }.map(String::toInt) }
                    .let { (a, b) -> a.zip(b) }
                    .map { (time, record) ->
                        (1..time-1).map { it * (time - it) }.count { it > record }
                    }
                    .reduce(Int::times)
                    .toString()
            },
        )
    }

    @Test
    fun B() {
        assertEquals(
            "27363861",
            Days.getInput(day).let {
                it
                    .trim('\n')
                    .lines()
                    .map { it.split(":")[1].replace(" ", "").let(String::toBigInteger) }
                    .let { (time, record) ->
                        var a = 1.toBigInteger()
                        while (a < time && a * (time - a) <= record) {
                            a += 1.toBigInteger()
                        }

                        var b = time - 1.toBigInteger()
                        while (b > 0.toBigInteger() && b * (time - b) <= record) {
                            b -= 1.toBigInteger()
                        }

                        b - a + 1.toBigInteger()
                    }
                    .toString()
            },
        )
    }
}
