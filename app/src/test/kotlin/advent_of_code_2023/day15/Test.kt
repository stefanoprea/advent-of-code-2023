package advent_of_code_2023.day15

import kotlin.test.Test
import kotlin.test.assertEquals
import advent_of_code_2023.Days


class MyTest {
    val day = "15"

    @Test
    fun A() {
        assertEquals(
            "511257",
            Days.getInput(day).let {
                it
                    .trim('\n')
                    .split(",")
                    .map {
                        it.toCharArray().fold(0) { acc, c -> (acc + c.code) * 17 % 256 }
                    }
                    .sum()
                    .toString()
            },
        )
    }

    @Test
    fun B() {
        assertEquals(
            "239484",
            Days.getInput(day).let {
                val input = it
                    .trim('\n')
                    .split(",")
                val machine = Machine()
                input.forEach { machine.process(it) }
                machine.score().toString()
            },
        )
    }
}

class Machine() {
    val boxes = MutableList<List<Lens>>(256) { listOf<Lens>() }

    fun process(item: String) {
        when {
            item.endsWith("-") -> deleteLens(item)
            else -> upsertLens(item)
        }
    }

    fun score() = boxes.withIndex().sumOf { box ->
        box.value.withIndex().sumOf { lens ->
            (box.index + 1) * (lens.index + 1) * lens.value.focal
        }
    }

    fun deleteLens(item: String) {
        val label = item.trimEnd('-')
        val boxNo = calculateBoxNo(label)
        boxes[boxNo] = boxes[boxNo].filter { it.label != label }
    }

    fun upsertLens(item: String) {
        val (label, focalStr) = item.split("=")
        val focal = focalStr.toInt()
        val boxNo = calculateBoxNo(label)
        boxes[boxNo] = when (boxes[boxNo].any { it.label == label }) {
            true -> boxes[boxNo].map {
                when (it.label) {
                    label -> Lens(label, focal)
                    else -> it
                }
            }
            else -> boxes[boxNo] + listOf(Lens(label, focal))
        }
    }

    fun calculateBoxNo(label: String) = label.toCharArray().fold(0) { acc, c -> (acc + c.code) * 17 % 256 }
}

data class Lens(val label: String, val focal: Int)