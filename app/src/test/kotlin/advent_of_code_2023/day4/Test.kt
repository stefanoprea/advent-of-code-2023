package advent_of_code_2023.day4

import kotlin.test.Test
import kotlin.test.assertEquals
import advent_of_code_2023.Days


class MyTest {
    val day = "4"

    @Test
    fun A() {
        assertEquals(
            "25004",
            Days.getInput(day).let {
                it
                    .trim('\n')
                    .lines()
                    .countWinners()
                    .map {
                        when(it) {
                            0 -> 0
                            else -> 2.pow(it - 1)
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
            "14427616",
            Days.getInput(day).let {
                it
                    .trim('\n')
                    .lines()
                    .countWinners()
                    .let {
                        it.fold(0 to List(it.size) { 1 }) { (temp, quantifiers), x ->
                            temp + quantifiers.first() to
                                    quantifiers.drop(1).mapIndexed { i, q -> if(i < x) q + quantifiers.first() else q }
                        }
                    }
                    .let { it.first }
                    .toString()
            },
        )
    }
}

fun List<String>.countWinners() = this
    .map(String::numbers)
    .map {(winning, ticket) ->
        ticket.count { winning.contains(it) }
    }

fun String.numbers() = this.split(":")[1].split("|").map {
    it.trim().split(" ").filter { it != "" }.map(String::toInt)
}

fun Int.pow(exp: Int) = List(exp) { this }.fold(1, Int::times)
