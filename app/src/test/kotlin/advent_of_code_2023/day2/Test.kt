package advent_of_code_2023.day2

import kotlin.test.Test
import kotlin.test.assertEquals
import advent_of_code_2023.Days


class MyTest {
    val day = "2"

    @Test
    fun A() {
        assertEquals(
            "2541",
            Days.getInput(day).let {
                it
                    .trim('\n')
                    .lines().withIndex().mapNotNull { line ->
                        (line.index + 1).takeIf {
                            listOf("red" to 12, "green" to 13, "blue" to 14).
                                all {
                                    line.value.getColor(it.first).max() <= it.second
                                }
                        }
                    }
                    .sum()
                    .toString()
            },
        )
    }

    @Test
    fun B() {
        assertEquals(
            "66016 ",
            Days.getInput(day).let { it
                .trim('\n')
                .lines()
                .map {
                    listOf("red", "green", "blue")
                        .map(it::getColor)
                        .map(Sequence<Int>::max)
                        .reduce(Int::times)
                }
                .sum()
                .toString()
            }
        )
    }
}

fun String.getColor(color: String) = reg.get("([0-9]+) $color")
    .findAll(this)
    .map { it.groupValues[1].toInt() }

object reg {
    val reg = mutableMapOf<String, Regex>()

    fun get(s: String) = reg.getOrPut(s, s::toRegex)
}
