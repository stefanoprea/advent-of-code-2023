package advent_of_code_2023.day17

import kotlin.test.Test
import kotlin.test.assertEquals
import advent_of_code_2023.Days


class MyTest {
    val day = "17"

    @Test
    fun A() {
        assertEquals(
            "956",
            Days.getInput(day).let {
                val d = Dijkstra<ItemImpl, CellImpl>(
                    CellImpl('0', Coord(0, 0)),
                    it
                )
                val cells = d
                    .solve(
                        ItemImpl(
                            Coord(1, 0),
                            Direction.Right,
                            1,
                            0
                        ),
                        ItemImpl(
                            Coord(0, 1),
                            Direction.Down,
                            1,
                            0
                        )
                    )
                val maxX = cells.map { it.coord.x }.max()
                val maxY = cells.map { it.coord.y }.max()
                cells
                    .find { it.coord == Coord(maxX, maxY) }!!
                    .items.values.min()
                    .toString()
            },
        )
    }

    @Test
    fun B() {
        assertEquals(
            "1106",
            Days.getInput(day).let {
                val d = Dijkstra<ItemImpl, UltraCell>(
                    UltraCell('0', Coord(0, 0)),
                    it
                )
                val cells = d
                    .solve(
                        ItemImpl(
                            Coord(1, 0),
                            Direction.Right,
                            1,
                            0
                        ),
                        ItemImpl(
                            Coord(0, 1),
                            Direction.Down,
                            1,
                            0
                        )
                    )
                val maxX = cells.map { it.coord.x }.max()
                val maxY = cells.map { it.coord.y }.max()
                cells
                    .find { it.coord == Coord(maxX, maxY) }!!
                    .items
                    .entries
                    .filter { it.key.second >= 4 }
                    .map { it.value }
                    .min()
                    .toString()
            },
        )
    }
}

data class UltraCell(val tile: Int, override val coord: Coord, val items: Map<Pair<Direction, Int>, Int> = mapOf()) : CellInterface<UltraCell, ItemImpl> {
    constructor(tile: Char, coord: Coord) : this(tile.toString().toInt(), coord)

    override fun fromChar(tile: Char, coord: Coord) = UltraCell(tile, coord)

    override fun update(item: ItemImpl) : Pair<UltraCell, List<ItemImpl>> {
        val p = item.dir to item.stepNo
        val olds = when (item.stepNo) {
            in 1..3 -> listOf(items[p]).filterNotNull()
            else -> (4..item.stepNo).map { item.dir to it }.mapNotNull { items[it] }
        }
        val new = item.totalHeatLoss + tile
        if (olds.any { it <= new }) {
            return this to listOf()
        }
        val newCell = UltraCell(
            tile,
            coord,
            items + (p to new)
        )
        val nextItems = listOf(
            item.dir.perpendicular().map {
                when {
                    item.stepNo >= 4 -> ItemImpl(
                        it.go(coord),
                        it,
                        1,
                        new
                    )
                    else -> null
                }
            },
            listOf(
                when (item.stepNo) {
                    10 -> null
                    in 1..9 -> ItemImpl(
                        item.dir.go(coord),
                        item.dir,
                        item.stepNo + 1,
                        new
                    )
                    else -> error("oops")
                }
            )
        )
            .flatten()
            .filterNotNull()

        return newCell to nextItems
    }

    override fun prune(items: List<ItemImpl>) = items.sortedBy { it.totalHeatLoss }
}

data class CellImpl(val tile: Int, override val coord: Coord, val items: Map<Pair<Direction, Int>, Int> = mapOf()) : CellInterface<CellImpl, ItemImpl> {
    constructor(tile: Char, coord: Coord) : this(tile.toString().toInt(), coord)

    override fun fromChar(tile: Char, coord: Coord) = CellImpl(tile, coord)

    override fun update(item: ItemImpl) : Pair<CellImpl, List<ItemImpl>> {
        val p = item.dir to item.stepNo
        val olds = (1..item.stepNo).map { item.dir to it }.mapNotNull { items[it] }
        val new = item.totalHeatLoss + tile
        if (olds.any { it <= new }) {
            return this to listOf()
        }
        val newCell = CellImpl(
            tile,
            coord,
            items + (p to new)
        )
        val nextItems = listOf(
            item.dir.perpendicular().map {
                ItemImpl(
                    it.go(coord),
                    it,
                    1,
                    new
                )
            },
            listOf(
                when (item.stepNo) {
                    3 -> null
                    in 1..2 -> ItemImpl(
                        item.dir.go(coord),
                        item.dir,
                        item.stepNo + 1,
                        new
                    )
                    else -> error("oops")
                }
            )
        )
            .flatten()
            .filterNotNull()

        return newCell to nextItems
    }

    override fun prune(items: List<ItemImpl>) = items.sortedBy { it.totalHeatLoss }
}

data class ItemImpl(
    override val coord: Coord,
    val dir: Direction,
    val stepNo: Int,
    val totalHeatLoss: Int
) : ItemInterface

enum class Direction {
    Up, Down, Left, Right;

    fun opposite() = when (this) {
        Up -> Down
        Down -> Up
        Left -> Right
        Right -> Left
    }

    fun perpendicular() = when (this) {
        Up, Down -> listOf(Left, Right)
        Left, Right -> listOf(Up, Down)
    }

    fun go(c: Coord) = when (this) {
        Up -> Coord(c.x, c.y - 1)
        Down -> Coord(c.x, c.y + 1)
        Left -> Coord(c.x - 1, c.y)
        Right -> Coord(c.x + 1, c.y)
    }
}

class Dijkstra<
        Item : ItemInterface,
        Cell : CellInterface<Cell, Item>>(
    val cells: Map<Coord, Cell>
) {
    constructor(cells: List<Cell>) : this(
        cells.associateBy { it.coord }
    )

    constructor(cellFactory: Cell, text: String) : this(
        text
            .trim('\n')
            .lines()
            .withIndex()
            .flatMap { line ->
                line.value.toCharArray().withIndex().map { c ->
                    cellFactory.fromChar(c.value, Coord(c.index, line.index))
                }
            }
    )

    fun solve(vararg q: Item) : List<Cell> {
        val s = cells.toMutableMap()
        var queue = q.toList()

        if (queue.isEmpty()) {
            return error("empty queue")
        }

        while (queue.isNotEmpty()) {
            val x = queue.first()
            queue = queue.drop(1)
            val coord = x.coord
            val cell = s[coord] ?: continue
            val (newState, nextItems) = cell.update(x)
            s.put(coord, newState)
            queue = cells.values.first().prune(queue + nextItems)
        }

        return s.values.toList()
    }
}

interface CellInterface<Cell, Item> {
    fun fromChar(tile: Char, coord: Coord) : Cell

    fun update(item: Item) : Pair<Cell, List<Item>>

    fun prune(items: List<Item>) = items

    val coord: Coord
}

interface ItemInterface {
    val coord: Coord
}

data class Coord(val x: Int, val y: Int)
