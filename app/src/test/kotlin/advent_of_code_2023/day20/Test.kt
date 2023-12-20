package advent_of_code_2023.day20

import kotlin.test.Test
import kotlin.test.assertEquals
import advent_of_code_2023.Days
import java.math.BigInteger


class MyTest {
    val day = "20"

    @Test
    fun A() {
        assertEquals(
            "788848550",
            Days.getInput(day).let {
                val machine = it
                    .trim('\n')
                    .lines()
                    .map(Module::fromString)
                    .let(::Machine)
                (1..1000)
                    .map { machine.pressButton() }
                    .flatten()
                    .let {
                        it.count { it.pulse == Pulse.Low } to
                                it.count { it.pulse == Pulse.High }
                    }
                    .let { (a, b) -> a * b }
                    .toString()
            }
        )
    }

    @Test
    fun B() {
        assertEquals(
            "228300182686739",
            Days.getInput(day).let {
                val machine = it
                    .trim('\n')
                    .lines()
                    .map(Module::fromString)
                    .let(::Machine)
                listOf("rd", "bt", "fv", "pr")
                    .map(machine::firstLowPulse)
                    .map { it.toBigInteger() }
                    .reduce(::LCM)
                    .toString()
            },
        )
    }
}

fun LCM(a: BigInteger, b: BigInteger) = (a / GCD(a, b)) * b

tailrec fun GCD(a: BigInteger, b: BigInteger) : BigInteger =
    if (b == 0.toBigInteger()) a
    else GCD(b, a % b)

interface Module {
    companion object {
        fun fromString(s: String) : Module {
            val (name, xs) = s.split(" -> ")
            val destinationModules = xs.split(", ")
            return when (name.toCharArray().first()) {
                '%' -> FlipFlop(name.substring(1), destinationModules)
                '&' -> Conjunction(name.substring(1), destinationModules)
                else -> Simple(name, destinationModules)
            }
        }
    }

    val name: String
    val destinationModules : List<String>
    var hasSentInit: Boolean

    fun process(m: Message) : List<Message> = when (val rp = convertPulse(m)) {
        null -> listOf()
        else -> destinationModules.map { Message(name, rp, it) }
    }

    fun convertPulse(m: Message) : Pulse?

    fun reset()
}

class Machine(val modules: Map<String, Module>) {
    constructor(modules: List<Module>) : this(
        modules.associateBy { it.name }
    )

    init {
        perform(Pulse.Init)
    }

    fun reset() {
        modules.values.forEach { it.reset() }
    }

    fun pressButton() = perform(Pulse.Low)

    fun perform(p: Pulse) : List<Message> {
        val queue = mutableListOf(
            Message("", p, "broadcaster")
        )
        var i = 0
        while (i < queue.size) {
            val message = queue[i]
            i += 1
            val mod = modules[message.destination]
            if (mod == null) {
                // println("skipping message $message")
                continue
            }
            queue.addAll(mod.process(message))
        }
        return queue
    }

    fun firstLowPulse(name: String) : Int {
        reset()
        var i = 0
        do {
            i += 1
            val messages = pressButton()
        } while (
            !messages.any { it.destination == name && it.pulse == Pulse.Low }
        )
        return i
    }
}

class Simple(
    override val name: String,
    override val destinationModules : List<String>
) : Module {
    override var hasSentInit = false

    override fun convertPulse(m: Message) = when (m.pulse) {
        Pulse.Init -> when (hasSentInit) {
            false -> run {
                hasSentInit = true
                Pulse.Init
            }
            true -> null
        }
        Pulse.High, Pulse.Low -> m.pulse
    }

    override fun reset() = Unit
}

class FlipFlop(
    override val name: String,
    override val destinationModules : List<String>
) : Module {
    var state = State.Off
    override var hasSentInit = false

    override fun convertPulse(m: Message) = when (m.pulse) {
        Pulse.Init -> when (hasSentInit) {
            false -> run {
                hasSentInit = true
                Pulse.Init
            }
            true -> null
        }
        Pulse.High -> null
        Pulse.Low -> run {
            state = state.toggle()
            when (state) {
                State.On -> Pulse.High
                State.Off -> Pulse.Low
            }
        }
    }

    override fun reset() {
        state = State.Off
    }
}

class Conjunction(
    override val name: String,
    override val destinationModules : List<String>
) : Module {
    val inputs = mutableMapOf<String, Pulse>()
    override var hasSentInit = false

    override fun convertPulse(m: Message) = when (m.pulse) {
        Pulse.Init -> run {
            inputs.put(m.source, Pulse.Low)
            when (hasSentInit) {
                false -> run {
                    hasSentInit = true
                    Pulse.Init
                }
                true -> null
            }
        }
        else -> {
            inputs.put(m.source, m.pulse)
            if (inputs.values.all { it == Pulse.High }) {
                Pulse.Low
            } else {
                Pulse.High
            }
        }
    }

    override fun reset() {
        inputs.keys.forEach {
            inputs.put(it, Pulse.Low)
        }
    }
}

data class Message(val source: String, val pulse: Pulse, val destination: String)

enum class State {
    Off, On;

    fun toggle() = when (this) {
        On -> Off
        Off -> On
    }
}

enum class Pulse {
    Low, High, Init
}
