package advent_of_code_2023.day9

import kotlin.test.Test
import kotlin.test.assertEquals
import advent_of_code_2023.Days


class MyTest {
    val day = "9"

    @Test
    fun A() {
        assertEquals(
            "1930746032",
            Days.getInput(day).let {
                it
                    .read()
                    .map(List<Int>::solve)
                    .map { it.second }
                    .sum()
                    .toString()
            },
        )
    }

    @Test
    fun B() {
        assertEquals(
            "1154",
            Days.getInput(day).let {
                it
                    .read()
                    .map(List<Int>::solve)
                    .map { it.first }
                    .sum()
                    .toString()
            },
        )
    }
}

fun String.read() = this
    .trim('\n')
    .lines()
    .map { it.split(" ").map(String::toInt) }

fun (List<Int>).solve() : Pair<Int, Int> {
    val firsts = mutableListOf<Int>()
    val lasts = mutableListOf<Int>()
    var numbers = this
    while (!numbers.all { it == 0 }) {
        firsts.add(numbers.first())
        lasts.add(numbers.last())
        numbers = numbers.windowed(2).map { (a, b) -> b - a }
    }
    return (firsts.reversed().reduce { a, b -> b - a }) to lasts.sum()
}
