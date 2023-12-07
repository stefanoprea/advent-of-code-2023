package advent_of_code_2023.day7

import kotlin.test.Test
import kotlin.test.assertEquals
import advent_of_code_2023.Days


class MyTest {
    val day = "7"

    @Test
    fun A() {
        assertEquals(
            "251121738",
            Days.getInput(day).let {
                it.solve { (cards, bid) -> Hand(cards, bid) }
            },
        )
    }

    @Test
    fun B() {
        assertEquals(
            "251421071",
            Days.getInput(day).let {
                it.solve { (cards, bid) -> HandWithJokers(cards, bid) }
            },
        )
    }
}

fun String.solve(toClass: (Pair<String, Int>) -> HandItf) = this
    .trim('\n')
    .lines()
    .map {
        it.split(" ").let { (cards, valS) -> cards to valS.toInt() }
    }
    .map(toClass)
    .sorted()
    .withIndex()
    .sumOf { it.value.bid * (it.index + 1) }
    .toString()


class HandWithJokers(override val cards: String, override val bid: Int) : HandItf {
    override val strength = "AKQT98765432J"

    override fun score() = strength
        .chunked(1)
        .map {
            cardsScore() + handScore(cards.replace("J", it))
        }
        .max()
}


open class Hand(override val cards: String, override val bid: Int) : HandItf {
    override val strength = "AKQJT98765432"

    override fun score() = cardsScore() + handScore(cards)
}


interface HandItf : Comparable<HandItf> {
    companion object {
        fun Int.pow(exp: Int) = when {
            exp == 0 -> 1
            exp < 0 -> error("negative exponent")
            else -> List<Int>(exp) { this }.reduce(Int::times)
        }
    }

    val cards: String
    val bid: Int
    val strength: String
    val zeros get() = 10000000

    override fun compareTo(other: HandItf) = this.score().compareTo(other.score())

    fun score() : Int

    fun handScore(cards: String) = when {
        this.groups(cards).contains(5) -> 8 * zeros
        this.groups(cards).contains(4) -> 7 * zeros
        this.groups(cards).let { it.contains(3) && it.contains(2) } -> 6 * zeros
        this.groups(cards).contains(3) -> 5 * zeros
        this.groups(cards).let { it.count { it == 2 } == 2 } -> 4 * zeros
        this.groups(cards).contains(2) -> 3 * zeros
        else -> 0
    }

    fun cardsScore() = strength
        .reversed()
        .toCharArray()
        .withIndex()
        .flatMap { score ->
            cards
                .reversed()
                .toCharArray()
                .withIndex()
                .mapNotNull { card ->
                    (16.pow(card.index) * score.index).takeIf { card.value == score.value }
                }
        }
        .sum()

    fun groups(cards: String) : List<Int> = strength
        .toCharArray()
        .map { cards.count(it::equals) }
}
