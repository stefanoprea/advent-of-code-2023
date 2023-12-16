package advent_of_code_2023.day16

import kotlin.test.Test
import kotlin.test.assertEquals
import advent_of_code_2023.Days


class MyTest {
    val day = "16"

    @Test
    fun A() {
        assertEquals(
            "7498",
            Days.getInput(day).let {
                Cells.fromString(it)
                    .clone()
                    .performQueue(Dir.Right to Coord(0, 0))
                    .toString()
            },
        )
    }

    @Test
    fun B() {
        assertEquals(
            "7846",
            Days.getInput(day).let {
                val c = Cells.fromString(it)
                c
                    .start()
                    .map {
                        c.clone().performQueue(it)
                    }
                    .max()
                    .toString()
            },
        )
    }
}

class Cells(val cells: Map<Coord, Cell>) {
    companion object {
        fun fromString(s: String) = s
            .trim('\n')
            .lines()
            .withIndex()
            .flatMap { line ->
                line.value.toCharArray().withIndex().map { c ->
                    Cell(c.value, Coord(c.index, line.index))
                }
            }.associateBy { it.coord }
            .let(::Cells)
    }

    fun clone() = Cells(cells.entries
        .associate { (coord, cell) -> coord to cell.clone() }
    )

    fun start() = cells.values.flatMap { cell ->
        listOf(
            Dir.Up to Dir.Down,
            Dir.Down to Dir.Up,
            Dir.Left to Dir.Right,
            Dir.Right to Dir.Left,
        ).mapNotNull { (a, b) ->
            val n = cell.coord.neighborC(a)
            when (cells[n]) {
                null -> b to cell.coord
                else -> null
            }
        }
    }

    fun performQueue(vararg q: Pair<Dir, Coord>) : Int {
        val queue = q.toMutableList()
        while (queue.isNotEmpty()) {
            val p = queue.removeAt(0)
            // println("processing $p")
            addBeam(p).forEach {
                // println("enqueueing $it")
                queue.add(it)
            }
        }
        return cells.values
            .count { it.beams.isNotEmpty() }
    }

    fun addBeam(p : Pair<Dir, Coord>) : List<Pair<Dir, Coord>> {
        val (dir, coord) = p
        val cell = cells[coord] ?: return listOf()
        if (cell.beams.contains(dir)) {
            return listOf()
        }
        cell.beams.add(dir)
        return when (cell.tile) {
            '.' -> listOf(coord.neighbor(dir))
            '-' -> when (dir) {
                Dir.Left, Dir.Right -> listOf(coord.neighbor(dir))
                Dir.Up, Dir.Down -> listOf(
                    coord.neighbor(Dir.Left),
                    coord.neighbor(Dir.Right)
                )
            }
            '|' -> when (dir) {
                Dir.Up, Dir.Down -> listOf(coord.neighbor(dir))
                Dir.Left, Dir.Right -> listOf(
                    coord.neighbor(Dir.Up),
                    coord.neighbor(Dir.Down)
                )
            }
            '/' -> listOf(
                coord.neighbor(
                    when(dir) {
                        Dir.Left -> Dir.Down
                        Dir.Right -> Dir.Up
                        Dir.Up -> Dir.Right
                        Dir.Down -> Dir.Left
                    }
                )
            )
            '\\' -> listOf(
                coord.neighbor(
                    when(dir) {
                        Dir.Left -> Dir.Up
                        Dir.Right -> Dir.Down
                        Dir.Down -> Dir.Right
                        Dir.Up -> Dir.Left
                    }
                )
            )
            else -> error("oops")
        }
    }
}

class Cell(val tile: Char, val coord: Coord) {
    val beams = mutableListOf<Dir>()

    fun clone() = Cell(tile, coord)
}

data class Coord(val x: Int, val y: Int) {
    fun neighbor(dir: Dir) = dir to neighborC(dir)

    fun neighborC(dir: Dir) = when (dir) {
        Dir.Up -> Coord(x, y - 1)
        Dir.Down -> Coord(x, y + 1)
        Dir.Left -> Coord(x - 1, y)
        Dir.Right -> Coord(x + 1, y)
    }
}

enum class Dir {
    Up, Down, Left, Right
}