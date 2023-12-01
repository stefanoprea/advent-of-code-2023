package advent_of_code_2023.day0

import kotlin.test.Test
import kotlin.test.assertEquals
import advent_of_code_2023.Days


class MyTest {
    val day = "1"

    @Test
    fun A() {
        assertEquals(
            "54597",
            Days.getInput(day).solve(digits),
        )
    }

    @Test
    fun B() {
        assertEquals(
            "54504",
            Days.getInput(day).solve(digits + words),
        )
    }
}

fun (String).solve(L: List<Pair<String, Int>>) = this
    .trim('\n')
    .lines()
    .map { it
        .substrings()
        .mapNotNull { substring ->
            L.firstNotNullOfOrNull { (k, v) -> v.takeIf { substring.startsWith(k) } }
        }
        .run { first() * 10 + last() }
    }
    .sum()
    .toString()

fun (String).substrings() = (0 ..< length).map { substring(it) }

val digits: List<Pair<String, Int>> = (0..9).map { it.toString() to it }

val words: List<Pair<String, Int>> = listOf(
    "one" to 1,
    "two" to 2,
    "three" to 3,
    "four" to 4,
    "five" to 5,
    "six" to 6,
    "seven" to 7,
    "eight" to 8,
    "nine" to 9,
)
