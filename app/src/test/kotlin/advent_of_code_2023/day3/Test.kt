package advent_of_code_2023.day3

import kotlin.test.Test
import kotlin.test.assertEquals
import advent_of_code_2023.Days


class MyTest {
    val day = "3"

    @Test
    fun A() {
        assertEquals(
            "528799",
            Days.getInput(day).let {
                it
                    .trim('\n')
                    .lines()
                    .let {
                        Group.getGroups(it)
                    }
                    .filter(Group::isAdjacentToSymbol)
                    .map(Group::toInt)
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
                    .let {
                        Group.getGroups(it) to Gear.getGears(it)
                    }
                    .let { (groups, gears) ->
                        gears.map {
                            groups.filter(it::isAdjacentTo)
                        }
                    }
                    .filter { it.size == 2 }
                    .map {
                        it.map(Group::toInt).reduce(Int::times)
                    }
                    .sum()
                    .toString()
            },
        )
    }
}

class Group(val lines: List<String>, val lineNumber: Int, val range: IntRange, val s: String) {
    companion object {
        val reg = "[^.0-9]".toRegex()

        val numberReg = "[0-9]+".toRegex()

        fun getGroups(lines: List<String>) = lines.withIndex().flatMap { line ->
            numberReg.findAll(line.value).map { group ->
                Group(lines, line.index, group.range, group.value)
            }
        }
    }

    fun isAdjacentToSymbol() = lines.slice(adjacentLines)
        .map { it.substring(adjacentRange) }
        .any(reg::containsMatchIn)

    val adjacentLines = (maxOf(0, lineNumber - 1)..minOf(lineNumber+1, lines.size-1))

    val adjacentRange = (maxOf(0, range.start-1)..minOf(range.endInclusive+1, lines.first().length-1))

    fun toInt() = s.toInt()
}

class Gear(val lineNumber: Int, val index: Int) {
    companion object {
        val reg = "[*]".toRegex()

        fun getGears(lines: List<String>) = lines.withIndex().flatMap { line ->
            reg.findAll(line.value).map { group ->
                Gear(line.index, group.range.start)
            }
        }
    }

    fun isAdjacentTo(g: Group) = g.adjacentLines.contains(lineNumber) && g.adjacentRange.contains(index)
}