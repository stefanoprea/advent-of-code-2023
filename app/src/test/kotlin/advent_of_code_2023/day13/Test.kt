package advent_of_code_2023.day13

import kotlin.test.Test
import kotlin.test.assertEquals
import advent_of_code_2023.Days


class MyTest {
    val day = "13"

    @Test
    fun A() {
        assertEquals(
            "35691",
            Days.getInput(day).let {
                it
                    .trim('\n')
                    .split("\n\n")
                    .map {
                        val strings = it.lines()
                        100 * strings.mirrors().sum() + strings.transpose().mirrors().sum()
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
                    .split("\n\n")
                    .map {
                        val strings = it.lines()
                        100 * strings.mirrors(1).sum() + strings.transpose().mirrors(1).sum()
                    }
                    .sum()
                    .toString()
            },
        )
    }
}

fun (List<String>).mirrors(diffCount: Int = 0) = let {strings ->
    strings.withIndex().windowed(2).mapNotNull { (_, f) ->
        val idx = f.index
        val a = strings.take(idx).reversed()
        val b = strings.drop(idx)
        when {
            a.zip(b).map { (x, y) -> x.diff(y) }.sum() == diffCount -> idx
            else -> null
        }
    }
}

fun String.diff(other: String) = this.toCharArray().zip(other.toCharArray()).count { (x, y) -> x != y }

fun (List<String>).transpose() : List<String> = this
    .map(String::toCharArray)
    .let { lists ->
        val lengths = lists.map { it.size }.distinct()
        when {
            lengths.size != 1 -> error("oops")
            else -> (0..<lengths.first()).map { index ->
                lists.map { list -> list[index] }
            }
        }
    }
    .map { it.joinToString("") }
