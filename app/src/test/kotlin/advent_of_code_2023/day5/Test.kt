package advent_of_code_2023.day5

import kotlin.test.Test
import kotlin.test.assertEquals
import advent_of_code_2023.Days


class MyTest {
    val day = "5"

    @Test
    fun A() {
        assertEquals(
            "650599855",
            Days.getInput(day).let {
                it.solve { it.map { it..it } }
            },
        )
    }

    @Test
    fun B() {
        assertEquals(
            "1240035",
            Days.getInput(day).let {
                it.solve { it.chunked(2).map { (a, b) -> a..a+b-1 } }
            },
        )
    }
}

fun String.solve(seedConvert: (List<Long>) -> List<LongRange>) = this
    .readInput(seedConvert)
    .let { (seeds, chunks) -> seeds to Items(chunks)}
    .let {(seeds, items) ->
        items.convert(seeds)
    }
    .minOf(LongRange::start)
    .toString()

fun String.readInput(seedConvert: (List<Long>) -> List<LongRange>) : Pair<List<LongRange>, List<List<List<Long>>>> {
    val chunks = this
        .trim('\n')
        .split("\n\n")

    val seedsChunk = chunks.first()
    val mapChunks = chunks.drop(1)

    val seeds = seedsChunk.split(":").last().trim().split(" ").map(String::toLong).let(seedConvert)

    val maps = mapChunks.map {
        it.lines().drop(1).map { it.split(" ").map(String::toLong) }
    }

//    println(seeds)
//    println(maps)

    return seeds to maps
}

class Item(chunks: List<List<Long>>)  {
    val maps = chunks.map { it[1]..(it[1]+it[2]-1) to it[0] - it[1] }

    fun convert(x: List<LongRange>) = maps
        .fold(x to listOf<LongRange>()) { ( nonConvertedRanges, convertedRanges), m ->
            nonConvertedRanges.map {
                val overlap = it.intersect(m.first)
                if (overlap == null) { listOf<LongRange>(it) to listOf<LongRange>() }
                else {
                    val converted = overlap.start + m.second..overlap.endInclusive + m.second
                    val rests = it.split(overlap)
                    rests to listOf<LongRange>(converted)
                }
            }
            .fold(listOf<LongRange>() to listOf<LongRange>()) { (a, b), (A, B) -> a + A to b + B}
            .let { (a, b) -> a to convertedRanges + b }
        }
        .let { (a, b) -> a + b }
//        .also { println(it) }
}

class Items(chunks: List<List<List<Long>>>) {
    val items = chunks.map { Item(it) }

    fun convert(x: List<LongRange>) = items.fold(x) { acc, item -> item.convert(acc) }
}

fun LongRange.intersect(other: LongRange) : LongRange? {
    val L = maxOf(this.start, other.start)
    val R = minOf(this.endInclusive, other.endInclusive)
    return if (L <= R) L..R else null
}

fun LongRange.split(other: LongRange) : List<LongRange> {
    val ret = mutableListOf<LongRange>()
    if (this.start < other.start) {
        ret.add(this.start..minOf(this.endInclusive, other.start - 1))
    }
    if (this.endInclusive > other.endInclusive) {
        ret.add(maxOf(this.start, other.endInclusive + 1)..this.endInclusive)
    }
    return ret
}
