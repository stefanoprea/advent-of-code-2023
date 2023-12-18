package advent_of_code_2023.day18

import kotlin.test.Test
import kotlin.test.assertEquals
import advent_of_code_2023.Days
import java.math.BigInteger


class MyTest {
    val day = "18"

    @Test
    fun AWithFill() {
        assertEquals(
            "66993",
            Days.getInput(day).let {
                val digger = FillDigger()
                it
                    .trim('\n')
                    .lines()
                    .forEach { digger.go(it) }
                digger.fill()
                // println(digger)
                digger.m.size.toString()
            },
        )
    }

    @Test
    fun A() {
        assertEquals(
            "66993",
            Days.getInput(day).let {
                val digger = Digger()
                it
                    .trim('\n')
                    .lines()
                    .forEach { digger.go(it) }
                // println(digger.toString('*'))
                digger.calculate().toString()
            },
        )
    }

    @Test
    fun Ae() {
        assertEquals(
            true,
            Days.getInput(day).let {
                val digger = Digger()
                val fillDigger = FillDigger()
                it
                    .trim('\n')
                    .lines()
                    .forEach {
                        digger.go(it)
                        fillDigger.go(it)
                    }
                digger.toString() == fillDigger.toString()
            },
        )
    }

    @Test
    fun B() {
        assertEquals(
            "177243763226648",
            Days.getInput(day).let {
                Days.getInput(day).let {
                    val digger = Digger()
                    it
                        .trim('\n')
                        .lines()
                        .forEach { digger.goHexa(it) }
                    digger.calculate().toString()
                }
            },
        )
    }

    @Test
    fun DisjointRanges() {
        assertEquals(
            listOf<IntRange>(0..0, 1..4, 5..5, 6..6, 7..9, 12..57),
            listOf<IntRange>(1..5, 0..4, 7..9, 12..57, 5..6)
                .disjointRanges()
                .sortedBy { it.start },
        )
    }
}

enum class Direction {
    R, D, L, U
}

data class Coord(val x: Int, val y: Int) {
    fun neighbor(d: Direction) = when (d) {
        Direction.D -> Coord(x, y + 1)
        Direction.U -> Coord(x, y - 1)
        Direction.L -> Coord(x - 1, y)
        Direction.R -> Coord(x + 1, y)
    }

    fun neighbors() = Direction.entries.map { neighbor(it) }
}

class FillDigger : Reader {
    val m = mutableSetOf<Coord>()

    var currentPos = Coord(0, 0)
        get() = field
        set(value) {
            m.add(value)
            field = value
        }

    init {
        currentPos = Coord(0, 0)
    }

    override fun go(d: Direction, a: Int) {
        (1..a).forEach {
            currentPos = currentPos.neighbor(d)
        }
    }

    fun fill(max: Int? = null) {
        val minY = m.map { it.y }.min()
        val minX0 = m.filter { it.y == minY }.map { it.x }.min()
        val queue = mutableListOf<Coord>(Coord(minX0 + 1, minY + 1))
        var count = 0
        while (queue.isNotEmpty() && (max == null || count < max)) {
            val el = queue.removeAt(0)
            val newEls = fillItem(el)
            if (newEls.isNotEmpty()) {
                count += 1
            }
            queue.addAll(newEls)
        }
    }

    fun fillItem(el: Coord) : List<Coord> {
        if (m.contains(el)) {
            return listOf()
        }
        m.add(el)
        return el.neighbors()
    }

    override fun toString() : String {
        val xs = m.map { it.x }
        val xMin = xs.min()
        val xMax = xs.max()
        return m.groupBy { it.y }.toMap().entries.sortedBy { it.key }.map { it.value }.map {
            val xs = it.map { it.x }
            (xMin..xMax).map { x ->
                if (xs.contains(x)) {
                    '#'
                } else {
                    '.'
                }
            }.joinToString("")
        }
            .joinToString("\n")
    }
}

class Digger : Reader {
    val m = mutableListOf<Move>(Init(Coord(0, 0)))

    override fun go(d: Direction, a: Int) {
        when (d) {
            Direction.U, Direction.D -> m.add(
                Vertical(
                    d,
                    m.last().last,
                    a
                )
            )
            Direction.L, Direction.R -> m.add(
                Horizontal(
                    d,
                    m.last().last,
                    a
                )
            )
        }
    }

    fun dirMoves() : List<DirMove> = m.filterIsInstance<DirMove>()

    fun disjointYRanges() = dirMoves().map { it.yRange }.disjointRanges().sortedBy { it.start }

    fun pruned() = dirMoves()
        .let { listOf(it.last()) + it + listOf(it.first()) }
        .windowed(3)
        .mapNotNull { (a, b, c) ->
            when (b) {
                is Vertical -> b
                is Horizontal -> when {
                    a is Horizontal || c is Horizontal -> error("oops")
                    a.dir != c.dir -> b.toPeak()
                    else -> b
                }
                is Peak -> error("oops")
            }
        }
        .sortedBy { it.leftmost }

    fun byY() = disjointYRanges()
        .map { yRange ->
            val filteredMoves = pruned()
                .filter { it.yRange.contains(yRange) }
            yRange to filteredMoves
        }

    fun calculate() = byY()
        .map { (yRange, moves) ->
            val nonPeaks = moves.withIndex().filter { it.value !is Peak }
            val nonPeakChunkRanges = nonPeaks.map { it.index }.chunked(2).map { (a, b) -> a..b }
            val peakSum = moves.withIndex().map { p ->
                when {
                    p.value !is Peak -> 0
                    nonPeakChunkRanges.any { it.contains(p.index) } -> 0
                    else -> p.value - p.value
                }
            }.sum()
            val nonPeakSum = nonPeaks.map { it.value }.chunked(2).map { (a, b) -> b - a }.sum()
            yRange.length().toBigInteger() * (peakSum + nonPeakSum).toBigInteger()
        }
        .reduce(BigInteger::plus)

    override fun toString() = toString('#')

    fun toString(peakChar: Char) : String {
        val xMin = dirMoves().map { it.leftmost }.min()
        val xMax = dirMoves().map { it.rightmost }.max()
        return byY().flatMap { (yRange, moves) ->
            val s = (xMin..xMax).map { x ->
                when {
                    moves.filterIsInstance<Peak>().any { (it.leftmost..it.rightmost).contains(x) } -> peakChar
                    moves.any { (it.leftmost..it.rightmost).contains(x) }-> '#'
                    else -> '.'
                }
            }
            .joinToString("")
            yRange.map { s }
        }
            .joinToString("\n")
    }
}

fun (IntRange).contains(other: IntRange) = this.start <= other.start && other.endInclusive <= this.endInclusive

fun (List<IntRange>).disjointRanges() : List<IntRange> {
    var ranges = this.distinct()
    while (true) {
       val p = ranges.firstNotNullOfOrNull { a ->
            ranges.firstNotNullOfOrNull { b ->
                when {
                    a == b -> null
                    a.isDisjoint(b) -> null
                    else -> a to b
                }
            }
        }
        if (p == null) {
            break
        }
        val (a, b) = p
        ranges = ranges.filter { it != a && it != b } + a.splitRange(b)
        ranges = ranges.distinct()
    }
    return ranges
}

fun (IntRange).length() = this.endInclusive - this.start + 1

fun (IntRange).isDisjoint(other: IntRange) = this.endInclusive < other.start ||
        other.endInclusive < this.start

fun (IntRange).splitRange(other: IntRange) : List<IntRange> {
    val (minStart, maxStart) = listOf(this.start, other.start).sorted()
    val (minEndInclusive, maxEndInclusive) = listOf(this.endInclusive, other.endInclusive).sorted()
    return when {
        this == other -> listOf(this)
        minEndInclusive < maxStart -> listOf(this, other)
        this.start == other.start -> listOf(
            this.start..minEndInclusive,
            minEndInclusive+1..maxEndInclusive
        )
        this.endInclusive == other.endInclusive -> listOf(
            minStart..maxStart-1,
            maxStart..this.endInclusive
        )
        else -> listOf(
            minStart..maxStart-1,
            maxStart..minEndInclusive,
            minEndInclusive+1..maxEndInclusive
        )
    }
}

sealed interface Move {
    val leftmost: Int
    val rightmost: Int
    val last: Coord
    val yRange: IntRange
    operator fun minus(other: Move) = rightmost - other.leftmost + 1
}

sealed interface DirMove : Move {
    val dir: Direction
}

data class Init(val coord: Coord) : Move {
    override val leftmost get() = coord.x
    override val rightmost get() = coord.x
    override val yRange get() = error("oops")
    override val last get() = coord
}

data class Vertical(override val dir: Direction, val old: Coord, val new: Coord) : DirMove {
    constructor(dir: Direction, old: Coord, amount: Int) : this(
        dir,
        old,
        Coord(
            old.x,
            old.y + when (dir) {
                Direction.U -> -amount
                Direction.D -> amount
                else -> error("oops")
            },
        )
    )

    override val leftmost get() = new.x
    override val rightmost get() = new.x
    override val last get() = new
    override val yRange get() = when (dir) {
        Direction.U -> new.y..old.y
        Direction.D -> old.y..new.y
        else -> error("oops")
    }.let { it.start+1..it.endInclusive-1 }
}

sealed interface HMove : DirMove {
    val old: Coord
    val new: Coord
    override val leftmost get() = when (dir) {
        Direction.L -> new.x
        Direction.R -> old.x
        else -> error("oops")
    }
    override val rightmost get() = when (dir) {
        Direction.R -> new.x
        Direction.L -> old.x
        else -> error("oops")
    }
    override val last get() = new
    override val yRange get() = new.y..new.y
}

data class Horizontal(
    override val dir: Direction,
    override val old: Coord,
    override val new: Coord
) : HMove {
    constructor(dir: Direction, old: Coord, amount: Int) : this(
        dir,
        old,
        Coord(
            old.x + when (dir) {
                Direction.L -> -amount
                Direction.R -> amount
                else -> error("oops")
            },
            old.y
        )
    )

    fun toPeak() = Peak(dir, old, new)
}

data class Peak(
    override val dir: Direction,
    override val old: Coord,
    override val new: Coord
) : HMove

interface Reader {
    fun go(d: Direction, a: Int)

    fun go(line: String) {
        val items = line.split(" ")
        val (dirS, amountS) = items.take(2)
        val dir = Direction.valueOf(dirS)
        val amount = amountS.toInt()
        go(dir, amount)
    }

    fun goHexa(line: String) {
        val h = line.split(" ").drop(2).first()
        val amount = h.substring(2..6).toInt(16)
        val dirOrd = h.substring(7..7).toInt()
        val dir = Direction.values()[dirOrd]!!
        go(dir, amount)
    }
}
