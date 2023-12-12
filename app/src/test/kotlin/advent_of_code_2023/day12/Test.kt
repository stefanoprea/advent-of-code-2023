package advent_of_code_2023.day12

import kotlin.test.Test
import kotlin.test.assertEquals
import advent_of_code_2023.Days
import java.math.BigInteger


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
                        a to nn
                    }
                    .map { (a, nn) -> a.solve(nn) }
                    .reduce(BigInteger::plus)
                    .toString()
            },
        )
    }

    @Test
    fun B() {
        assertEquals(
            "10861030975833",
            Days.getInput(day).let {
                it
                    .trim('\n')
                    .lines()
                    .map {
                        val (a, b) = it.split(" ", limit=2)
                        val nn = b.split(",").map(String::toInt)
                        val A = List<String>(5) { a }.joinToString("?")
                        val NN = List<List<Int>>(5) { nn }.flatten()
                        A to NN
                    }
                    .map { (a, nn) -> a.solve(nn) }
                    .reduce(BigInteger::plus)
                    .toString()
            },
        )
    }
}

fun String.solve(nn: List<Int>) : BigInteger = (this + ".")
    .toCharArray()
    .fold(DP(listOf(), listOf(Dsp(listOf(), 1.toBigInteger())))) { old, c ->
        val d = DP(listOf(), old.ongoing + old.finished).clean()
        val h = DP(
            old.ongoing.map {
                Dsp(
                    it.groups.dropLast(1) + listOf(it.groups.last() + 1),
                    it.count
                )
            } +
                    old.finished.map {
                        Dsp(
                            it.groups + listOf(1),
                            it.count
                        )
                    },
            listOf(),
        ).clean()
        val q = DP(d.ongoing + h.ongoing, d.finished + h.finished).clean()
        val r = when (c) {
            '.' -> d
            '#' -> h
            '?' -> q
            else -> error("oops")
        }
        r.purge(nn)
    }
    .finished.firstOrNull { it.groups == nn }?.count?: 0.toBigInteger()

data class DP(val ongoing: List<Dsp>, val finished: List<Dsp>) {
    companion object {
        fun (List<Dsp>).clean() = this.groupBy({ it.groups }) { it.count }.entries.map { (k, v) -> Dsp(k, v.reduce(BigInteger::plus)) }

        fun (List<Int>).matchesStart(other: List<Int>) = when {
            this.size > other.size -> false
            other.take(this.size) == this -> true
            else -> false
        }
    }

    fun clean() = DP(ongoing.clean(), finished.clean())

    fun purge(x: List<Int>) = DP(
        ongoing.filter { it.groups.isEmpty() || it.groups.dropLast(1).matchesStart(x) },
        finished.filter { it.groups.matchesStart(x) },
    )
}

data class Dsp(val groups: List<Int>, val count: BigInteger)
