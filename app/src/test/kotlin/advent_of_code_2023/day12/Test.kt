package advent_of_code_2023.day12

import kotlin.test.Test
import kotlin.test.assertEquals
import advent_of_code_2023.Days


class MyTest {
    val day = "12"

    @Test
    fun A() {
        assertEquals(
            "7771",
            Days.getInput(day).let {
                it
                    .trim('\n')
                    .lines()
                    .map {
                        val (a, b) = it.split(" ", limit=2)
                        val nn = b.split(",").map(String::toInt)
                        a
                            .replaceS()
                            .count { it.toGroups() == nn }
                    }
                    .sum()
                    .toString()
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

fun String.toGroups() : List<Int> {
    val groups = mutableListOf<Int>()
    var c = 0
    (this + ".").toCharArray().forEach {
        when (it) {
            '.' -> if (c != 0) {
                groups.add(c)
                c = 0
            }
            '#' -> c = c + 1
            else -> error("bad spring $it")
        }
    }
    return groups.toList()
}

fun String.replaceS() : List<String> {
    val arr = this.toCharArray()
    val t = arr.count { it == '?' }
    return (0..<2.pow(t)).map {
        var mask = it
        arr.map {
            when (it) {
                '?' -> run {
                    val bit = mask and 1
                    mask = mask shr 1
                    when (bit) {
                        1 -> '.'
                        else -> '#'
                    }
                }
                else -> it
            }
        }.joinToString("")
    }
}

tailrec fun Int.pow(exp: Int) : Int = when {
    exp == 0 -> 1
    exp > 0 -> this * this.pow(exp - 1)
    else -> error("called pow with negative exponent")
}