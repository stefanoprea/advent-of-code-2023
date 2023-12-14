package advent_of_code_2023.day14

import kotlin.test.Test
import kotlin.test.assertEquals
import advent_of_code_2023.Days


class MyTest {
    val day = "14"

    @Test
    fun A() {
        assertEquals(
            "110407",
            Days.getInput(day).let {
                it
                    .trim('\n')
                    .lines()
                    .transpose()
                    .roll()
                    .calculateLoad()
                    .toString()
            },
        )
    }

    @Test
    fun B() {
        assertEquals(
            "",
            Days.getInput(day).let {
                val lines = mutableListOf(
                    it
                        .trim('\n')
                        .lines()
                        .transpose()
                )
                while (true) {
                    val newLines = lines.last().rollN().rollW().rollS().rollE()
                    // newLines.transpose().forEach { println(it) }
                    // println("")
                    lines.add(newLines)
                    if (lines.count { it == newLines } > 1) {
                        break
                    }
                }
                // println(lines.map { it.calculateLoad().toString() })
                val x = lines.size - 1
                val y = lines.indexOfFirst { it == lines.last() }
                // println(x)
                // println(y)
                val idx = (1000000000 - y) % (x - y) + y
                lines[idx].calculateLoad().toString()
            },
        )
    }
}

fun (List<String>).rollN() = this.roll()

fun (List<String>).rollW() = this.transpose().roll().transpose()

fun (List<String>).rollS() = this.reversed().roll().reversed()

fun (List<String>).rollE() = this.transpose().reversed().roll().reversed().transpose()

fun (List<String>).roll() : List<String> = this.map {
    val line = it.toCharArray()
    val m = line
        .withIndex()
        .toList()
        .split { it.value != '#' }
        .flatMap {
            val noOfRoundStones = it.count { it.value == 'O' }
            it
                .zip(List(noOfRoundStones) { 'O' } + List(it.size - noOfRoundStones) { '.' })
                .map { (a, b) ->
                    a.index to b
                }
        }
        .associateBy({ it.first }) { it.second }
    line.indices
        .map {
            m[it] ?: '#'
        }
        .joinToString("")
}

fun (List<String>).calculateLoad() : Int = this
    .sumOf { line ->
        line.toCharArray().withIndex().sumOf {
            when (it.value) {
                'O' -> line.length - it.index
                else -> 0
            }
        }
    }

fun (List<String>).reversed() = this.map(String::reversed)

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

fun <T> (List<T>).split(c: (T) -> Boolean) : List<List<T>> {
    val list = this
    if (list.size == 0) { return listOf() }
    val x = list.takeWhile(c)
    return when (x.size) {
        0 -> list.drop(1).split(c)
        list.size -> listOf(list)
        else -> listOf(x) + list.drop(x.size + 1).split(c)
    }
}
