package advent_of_code_2023.day11

import kotlin.test.Test
import kotlin.test.assertEquals
import advent_of_code_2023.Days
import kotlin.math.*
import java.math.BigInteger


class MyTest {
    val day = "11"

    @Test
    fun A() {
        assertEquals(
            "9599070",
            Days.getInput(day).let {
                it.solve(2)
            },
        )
    }

    @Test
    fun B() {
        assertEquals(
            "842645913794",
            Days.getInput(day).let {
                it.solve(1000000)
            },
        )
    }
}

fun String.solve(galaxyExpansion: Int) = this
    .trim('\n')
    .lines()
    .withIndex()
    .map { line ->
        line.value.toCharArray().withIndex().map { c ->
            IndexedChar(c.value, c.index, line.index)
        }
    }
    .also {
        it.windowed(2).forEach { (a, b) ->
            val inc = when {
                a.all { it.c == '.' } -> galaxyExpansion
                else -> 1
            }
            b.forEach { it.y = a.first().y + inc }
        }
    }
    .transpose()
    .also {
        it.windowed(2).forEach { (a, b) ->
            val inc = when {
                a.all { it.c == '.' } -> galaxyExpansion
                else -> 1
            }
            b.forEach { it.x = a.first().x + inc }
        }
    }
    .flatten()
    .filter { it.c != '.' }
    .let { galaxies ->
        val r: List<Int> = galaxies.flatMap { a ->
            galaxies.map { b ->
                a.distance(b)
            }
        }
        r
    }
    .map { it.toBigInteger() }
    .reduce(BigInteger::plus)
    .let { it / 2.toBigInteger() }
    .toString()

fun <T> (List<List<T>>).transpose() : List<List<T>> = this
    .let { lists ->
        (0..<lists.first().size).map { index ->
            lists.map { it[index] }
        }
    }

fun String.duplicateIfEmptyLine() : List<String> = when {
    this.toCharArray().all { it == '.' } -> listOf(this, this)
    else -> listOf(this)
}

class IndexedChar(val c: Char, var x: Int, var y: Int)

fun (IndexedChar).distance(other: IndexedChar) = abs(this.x - other.x) + abs(this.y - other.y)