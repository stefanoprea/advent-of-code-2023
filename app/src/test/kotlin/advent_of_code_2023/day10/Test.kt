package advent_of_code_2023.day10

import kotlin.test.Test
import kotlin.test.assertEquals
import advent_of_code_2023.Days


class MyTest {
    val day = "10"

    @Test
    fun A() {
        assertEquals(
            "7012",
            Days.getInput(day).let {
                Labyrinth(it).solve().toString()
            },
        )
    }

    @Test
    fun B() {
        assertEquals(
            "395",
            Days.getInput(day).let {
                Labyrinth(it).solve2().toString()
            },
        )
    }
}

class Labyrinth(val input: String) {
    val m: MutableMap<Pair<Int, Int>, Cell>

    init {
        m = input
            .trim('\n')
            .lines()
            .withIndex()
            .flatMap { line ->
                line.value.toCharArray().withIndex().map { c ->
                    Cell(
                        3 * c.index + 2 to 3 * line.index + 2,
                        c.value,
                    )
                }
            }
            .flatMap { listOf(it) + it.padding() }
            .associateBy({ it.coord })
            .toMutableMap()
        addBorder()
    }

    fun touchdown(coord: Pair<Int, Int>) : List<Pair<Int, Int>> {
        val cell = m.get(coord) ?: return listOf()
        val newSteps = cell.steps!! + 1
        val neighbors = cell.neighbors().mapNotNull { m.get(it) }.filter {
            when {
                !it.neighbors().contains(cell.coord) -> false
                it.steps != null && it.steps!! <= newSteps -> false
                else -> true
            }
        }
        neighbors.forEach { it.steps = newSteps }
        return neighbors.map { it.coord }
    }

    fun touchdown2(coord: Pair<Int, Int>) : List<Pair<Int, Int>> {
        val cell = m.get(coord) ?: return listOf()
        val neighbors = cell.neighbors2().mapNotNull { m.get(it) }.filter {it.steps == null }
        neighbors.forEach { it.steps = -1 }
        return neighbors.map { it.coord }
    }

    fun solve() : Int {
        val (start,) = m.values.filter { it.tile == 'S' }
        start.steps = 0
        var queue = listOf(start.coord)
        while (queue.isNotEmpty()) {
            val el = queue.first()
            queue = queue.drop(1) + touchdown(el)
        }
        return m.values.mapNotNull { it.steps }.max() / 3
    }

    fun solve2() : Int {
        solve()
        val start = m.get(0 to 0)!!
        start.steps = -1
        var queue = listOf(start.coord)
        while (queue.isNotEmpty()) {
            val el = queue.first()
            queue = queue.drop(1) + touchdown2(el)
        }
        return m.values.count { !listOf('i', '#').contains(it.tile) && it.steps == null }
    }

    fun addBorder() {
        val xx = m.keys.map { it.first }
        val yy = m.keys.map { it.second }
        val minx = xx.min() - 1
        val maxx = xx.max() + 1
        val miny = yy.min() - 1
        val maxy = yy.max() + 1
        val borderTiles = listOf(
                (minx..maxx).map { listOf(it to miny, it to maxy) },
                (miny..maxy).map { listOf(minx to it, maxx to it) },
            )
            .flatten()
            .flatten()
            .map { Cell(it, 'i') }
        m.putAll(borderTiles.associateBy { it.coord })
    }
}

class Cell(
    val coord: Pair<Int, Int>,
    val tile: Char,
    var steps: Int? = null,
) {
    fun neighbors() = when (tile) {
        '.', 'i' -> listOf<Pair<Int, Int>>()
        'F' -> listOf((1 to 0), (0 to 1))
        '7' -> listOf((-1 to 0), (0 to 1))
        'L' -> listOf((1 to 0), (0 to -1))
        'J' -> listOf((-1 to 0), (0 to -1))
        '|' -> listOf((0 to 1), (0 to -1))
        '-' -> listOf((1 to 0), (-1 to 0))
        'S', '#' -> listOf((1 to 0), (-1 to 0), (0 to 1), (0 to -1))
        else -> listOf<Pair<Int, Int>>()
    }.map { it + coord }

    fun neighbors2() = listOf((1 to 0), (-1 to 0), (0 to 1), (0 to -1))
        .map { it + coord }

    fun padding() = listOf(
            (1 to 0),
            (-1 to 0),
            (0 to 1),
            (0 to -1),
            (1 to 1), (1 to -1), (-1 to 1), (-1 to -1)
        )
        .map { it + coord }
        .map {
            Cell(
                it,
                if (this.neighbors().contains(it)) '#' else 'i'
            )
        }
}

operator fun (Pair<Int, Int>).plus(other: Pair<Int, Int>) = this.first + other.first to this.second + other.second
