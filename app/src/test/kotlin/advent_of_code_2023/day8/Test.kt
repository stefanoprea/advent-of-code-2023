package advent_of_code_2023.day8

import advent_of_code_2023.Days
import kotlin.test.Test
import kotlin.test.assertEquals
import java.math.BigInteger


class MyTest {
    val day = "8"

    @Test
    fun A() {
        assertEquals(
            "22357",
            Days.getInput(day).let {
                it
                    .trim('\n')
                    .split("\n\n")
                    .let { (instructions, b) ->
                        val tree = b.lines().associateBy({ it.substring(0..2) }) {
                            it.substring(7..9) to it.substring(12..14)
                        }
                        var el = "AAA"
                        var counter = 0
                        while (el != "ZZZ") {
                            val node = tree[el]!!
                            val modCounter = counter % instructions.length
                            if (instructions.substring(modCounter..modCounter) == "L") {
                                el = node.first
                            } else {
                                el = node.second
                            }
                            counter += 1
                        }
                        counter
                    }
                    .toString()
            },
        )
    }

    @Test
    fun B() {
        assertEquals(
            "10371555451871",
            Days.getInput(day).let {
                it
                    .trim('\n')
                    .split("\n\n")
                    .let { (instructions, b) ->
                        val tree = b.lines().associateBy({ it.substring(0..2) }) {
                            it.substring(7..9) to it.substring(12..14)
                        }
                        val els = tree.keys.filter { it.endsWith("A") }
                        println(els)
                        val x = els.map { it.calculate(tree, instructions) }
                        println(x)
                        assert(x.all { (a, b) -> b.size == 1 && a == b.first() })
                        x.map { it.first.toBigInteger() }.reduce(::LCM)
                    }
                    .toString()
            },
        )
    }
}

fun String.calculate(tree: Map<String, Pair<String, String>>, instructions: String) : Pair<Int, List<Int>> {
    var el = this
    val Zs = mutableListOf<Pair<String, Int>>()
    var counter = 0
    while (Zs.size == Zs.map { it.first }.toSet().size) {
        val modCounter = counter % instructions.length
        val instruction = instructions.substring(modCounter..modCounter)
        val node = tree[el]!!
        if (instruction == "L") {
            el = node.first
        } else {
            el = node.second
        }
        counter += 1
        if (el.endsWith("Z")) {
            Zs.add(el to counter)
        }
    }
    println(Zs)
    return Zs.first().second to Zs.windowed(2).map { (a, b) -> b.second - a.second }
}

fun LCM(a: BigInteger, b: BigInteger) = (a / GCD(a, b)) * b

tailrec fun GCD(a: BigInteger, b: BigInteger) : BigInteger =
    if (b == 0.toBigInteger()) a
    else GCD(b, a % b)
