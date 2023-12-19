package advent_of_code_2023.day19

import kotlin.test.Test
import kotlin.test.assertEquals
import advent_of_code_2023.Days


class MyTest {
    val day = "19"

    @Test
    fun A() {
        assertEquals(
            "397643",
            Days.getInput(day).let {
                val (a, b) = it
                    .trim('\n')
                    .split("\n\n")
                    .map { it.lines() }
                val workflows = a
                   .map { Workflow(it) }
                   .let { Workflows(it) }
                b
                    .map { Item(it) }
                    .filter { workflows.perform(it) == "A" }
                    .map { it.sum() }
                    .sum()
                    .toString()
            },
        )
    }

    @Test
    fun B() {
        assertEquals(
            "132392981697081",
            Days.getInput(day).let {
                it
                    .trim('\n')
                    .split("\n\n")
                    .first()
                    .lines()
                    .map(::Workflow)
                    .let(::Workflows)
                    .let {
                        it.perform(
                            ItemRange(
                                "x" to 1..4000,
                                "m" to 1..4000,
                                "a" to 1..4000,
                                "s" to 1..4000
                            )
                        )
                    }
                    .first
                    .map { it.size }
                    .reduce { a, b -> a + b }
                    .toString()
            },
        )
    }
}

class Workflows(val workflows: Map<String, Workflow>) {
    constructor(wf: List<Workflow>) : this(
        wf.associateBy { it.key }
    )

    fun perform(item: Item) : String {
        var k = "in"
        while (k != "A" && k != "R") {
            val wf = workflows[k]!!
            k = wf.perform(item)
        }
        return k
    }

    fun perform(item: ItemRange) : Pair<List<ItemRange>, List<ItemRange>> {
        val queue = mutableListOf(item to "in")
        val a = mutableListOf<ItemRange>()
        val r = mutableListOf<ItemRange>()
        while (queue.isNotEmpty()) {
            val (i, k) = queue.removeAt(0)
            val res = workflows[k]!!.perform(i)
            queue.addAll(res.filter { (_, k) -> k != "A" && k != "R" })
            a.addAll(res.mapNotNull { (i, k) -> i.takeIf { k == "A" } })
            r.addAll(res.mapNotNull { (i, k) -> i.takeIf { k == "R" } })
        }
        return a to r
    }
}

class Workflow {
    val key: String
    val steps: List<Step>
    val default: String

    constructor(s: String) {
        val (keyv, xs) = s.split("{")
        val xss = xs.trim('}').split(",")
        default = xss.last()
        key = keyv
        steps = xss.dropLast(1).map { Step(key, it) }
    }

    fun perform(item: Item) = steps.firstNotNullOfOrNull { it.perform(item) } ?: default

    fun perform(item: ItemRange) = steps.firstNotNullOfOrNull { it.perform(item) } ?: listOf(item to default)
}

class Step {
    companion object {
        val reg = "^(.*)([=<>])(.*):(.*)$".toRegex()
    }

    val name: String
    val v: String
    val op: String
    val am: Int
    val res: String

    constructor(namev: String, s: String) {
        val (vv, opv, ams, resv) = reg.find(s)!!.destructured
        val amv = ams.toInt()
        v = vv
        op = opv
        am = amv
        res = resv
        name = namev
    }

    fun perform(item: Item) : String? {
        val itemAmount = when (v) {
            "x" -> item.x
            "m" -> item.m
            "a" -> item.a
            "s" -> item.s
            else -> error("oops $v")
        }

        val t = when (op) {
            "<" -> itemAmount < am
            "=" -> itemAmount == am
            ">" -> itemAmount > am
            else -> error("oops $op")
        }

        return res.takeIf { t }
    }

    fun perform(item: ItemRange) = when (op) {
        "<" -> performLt(item)
        "=" -> performEq(item)
        ">" -> performGt(item)
        else -> error("oops $op")
    }

    fun performLt(item: ItemRange) : List<Pair<ItemRange, String>>? {
        val lt = item.intervalLt(v, am) ?: return null
        return listOf(lt to res) + listOf(
            item.intervalGte(v, am)
        )
            .filterNotNull()
            .map { it to name }
    }

    fun performGt(item: ItemRange) : List<Pair<ItemRange, String>>? {
        val lt = item.intervalGt(v, am) ?: return null
        return listOf(lt to res) + listOf(
            item.intervalLte(v, am)
        )
            .filterNotNull()
            .map { it to name }
    }

    fun performEq(item: ItemRange) : List<Pair<ItemRange, String>>? {
        val lt = item.intervalEq(v, am) ?: return null
        return listOf(lt to res) + listOf(
            item.intervalLt(v, am),
            item.intervalGt(v, am),
        )
            .filterNotNull()
            .map { it to name }
    }
}

class Item {
    val x: Int
    val m: Int
    val a: Int
    val s: Int

    constructor(str: String) {
        val (xv, mv, av, sv) = str
            .split(",")
            .map {
                it.trim('{', '}', 'x', 'm', 'a', 's', '=')
            }
            .map { it.toInt() }
        x = xv
        m = mv
        a = av
        s = sv
    }

    fun sum() = x + m + a + s
}

class ItemRange(val xmas: Map<String, IntRange>) {
    constructor(vararg xmas: Pair<String, IntRange>) : this(
        xmas.associate { it }
    )

    fun intervalLte(k: String, v: Int) : ItemRange? {
        val old = xmas[k]!!
        return when {
            old.contains(v) -> ItemRange(xmas + (k to old.start..v))
            else -> null
        }
    }

    fun intervalEq(k: String, v: Int) : ItemRange? {
        val old = xmas[k]!!
        return when {
            old.contains(v) -> ItemRange(xmas + (k to v..v))
            else -> null
        }
    }

    fun intervalGte(k: String, v: Int) : ItemRange? {
        val old = xmas[k]!!
        return when {
            old.contains(v) -> ItemRange(xmas + (k to v..old.endInclusive))
            else -> null
        }
    }

    fun intervalLt(k: String, v: Int) = intervalLte(k, v-1)
    fun intervalGt(k: String, v: Int) = intervalGte(k, v+1)

    val size get() = xmas.values
        .map { it.endInclusive - it.start + 1 }
        .map { it.toBigInteger() }
        .reduce { a, b -> a * b }
}
