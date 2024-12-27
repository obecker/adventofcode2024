// https://adventofcode.com/2024/day/24

import java.io.File
import kotlin.experimental.and
import kotlin.experimental.or
import kotlin.experimental.xor

fun main() {
    val input = fileReader("day24/sample.txt").readLines()

    val wires = input
        .filter { it.contains(":") }
        .map { it.split(": ") }
        .associate { (name, value) -> name to value.toByte() }
        .toMutableMap()

    val gates = input
        .filter { it.contains("->") }
        .map { Gate.parse(it) }

    val outputs = gates.associate { it.output to it }
    gates.filter { it.output.startsWith("z") }
        .map { it.output to it.compute(wires, outputs) }
        .sortedByDescending { it.first }
        .fold(0L) { acc, (_, value) -> (acc shl 1) + value }
        .debug("result1")

    // For the solution to part 2, creating a mermaid diagram proved to be very helpful
    // (requires the real input, not the sample)
    printMermaid(File("day24.input.mmd"), outputs)

    findSwappedGates(gates, outputs)
        .sorted()
        .joinToString(",")
        .debug("result2")
}

private data class Gate(
    val input1: String,
    val input2: String,
    val operation: Operation,
    val output: String,
) {
    companion object {
        enum class Operation {
            AND, OR, XOR
        }

        val regex = Regex("^(\\w+) (AND|OR|XOR) (\\w+) -> (\\w+)$")

        fun parse(spec: String): Gate {
            val match = regex.matchEntire(spec) ?: error("Invalid gate spec: $spec")
            return Gate(
                input1 = match.groupValues[1],
                operation = Operation.valueOf(match.groupValues[2]),
                input2 = match.groupValues[3],
                output = match.groupValues[4],
            )
        }
    }

    fun compute(wires: MutableMap<String, Byte>, outputs: Map<String, Gate>): Byte {
        val value1 = wires[input1] ?: outputs[input1]?.compute(wires, outputs) ?: return -1
        val value2 = wires[input2] ?: outputs[input2]?.compute(wires, outputs) ?: return -1
        return when (operation) {
            Operation.AND -> value1 and value2
            Operation.OR -> value1 or value2
            Operation.XOR -> value1 xor value2
        }.also {
            wires[output] = it
        }
    }
}

private fun findSwappedGates(
    gates: List<Gate>,
    outputs: Map<String, Gate>,
    zGates: List<Gate> = gates.filter { it.output.startsWith("z") }.sortedBy { it.output },
    n: Int = 0,
    correctGates: Set<String> = emptySet(),
    swappedGates: Set<String> = emptySet(),
): Set<String> {
    val wires = mutableMapOf<String, Byte>()
    return when {
        n == zGates.size - 1 -> swappedGates
        checkSingleBitAdd(n, outputs, wires) ->
            findSwappedGates(gates, outputs, zGates, n + 1, wires.keys, swappedGates)

        else -> {
            val swapCandidates = wires.keys.filter { it !in correctGates }
            val successors = allOutputs(swapCandidates, gates)

            (getPairsFrom(swapCandidates) + getPairsFrom(swapCandidates, successors))
                .asSequence()
                .filter { (g1, g2) -> g1 !in allInputs(g2, outputs) && g2 !in allInputs(g1, outputs) }
                .map { (g1, g2) ->
                    val newGates = gates.swap(g1, g2)
                    val newOutputs = newGates.associate { it.output to it }
                    if (checkSingleBitAdd(n, newOutputs, wires)) {
                        findSwappedGates(newGates, newOutputs, zGates, n + 1, wires.keys, swappedGates + g1 + g2)
                    } else {
                        emptySet()
                    }
                }.filter { it.isNotEmpty() }
                .firstOrNull()
                ?: emptySet()
        }
    }
}

private fun checkSingleBitAdd(n: Int, outputs: Map<String, Gate>, wires: MutableMap<String, Byte>): Boolean {
    val b0: Byte = 0
    val b1: Byte = 1

    fun String.no(n: Int) = this + n.toString().padStart(2, '0')

    fun checkSingleBitAdd(x: Byte, y: Byte, xLow: Byte = b0, yLow: Byte = b0): Boolean {
        wires.clear()
        (0..n).forEach {
            wires.put("x".no(it), b0)
            wires.put("y".no(it), b0)
        }
        wires.put("x".no(n), x)
        wires.put("y".no(n), y)
        if (n > 0) {
            wires.put("x".no(n - 1), xLow)
            wires.put("y".no(n - 1), yLow)
        }
        val z = outputs["z".no(n)]!!.compute(wires, outputs)
        return z == ((xLow and yLow) xor (x xor y))
    }

    val bitPairs = listOf(b0 to b0, b1 to b0, b0 to b1, b1 to b1)
    val checked = bitPairs.all { (x, y) -> checkSingleBitAdd(x, y) }

    val checkedWithLowerBit = (n == 0) ||
            listOf(b0 to b1, b1 to b0, b1 to b1).all { (xl, yl) ->
                bitPairs.all { (x, y) -> checkSingleBitAdd(x, y, xl, yl) }
            }

    return checked && checkedWithLowerBit
}

private fun allInputs(name: String, outputs: Map<String, Gate>): Set<String> =
    outputs[name]?.let { gate ->
        allInputs(gate.input1, outputs) + allInputs(gate.input2, outputs)
    }.orEmpty() + name

private fun allOutputs(names: List<String>, gates: List<Gate>) =
    allOutputs(
        names,
        gates.flatMap { gate -> listOf(gate.input1 to gate, gate.input2 to gate) }
            .groupBy { (input, _) -> input }
            .mapValues { (_, value) -> value.map { (_, gate) -> gate } }
    )

private fun allOutputs(names: List<String>, inputs: Map<String, List<Gate>>): List<String> {
    val next = names.flatMap { inputs[it] ?: emptyList() }.map { it.output }.distinct()
    return next + if (next.isEmpty()) emptyList() else allOutputs(next - names, inputs)
}

private fun List<Gate>.swap(out1: String, out2: String) = map { gate ->
    when (gate.output) {
        out1 -> gate.copy(output = out2)
        out2 -> gate.copy(output = out1)
        else -> gate
    }
}

private fun <T> getPairsFrom(list: List<T>) =
    list.flatMapIndexed { index, e1 ->
        list.subList(index + 1, list.size).map { e2 -> e1 to e2 }
    }

private fun <T> getPairsFrom(list1: List<T>, list2: List<T>) =
    list2.flatMap { e2 ->
        list1.map { e1 -> e1 to e2 }
    }

private fun printMermaid(file: File, outputs: Map<String, Gate>) {
    file.printWriter().use { out ->
        val printed = mutableSetOf<Gate>()

        fun classdef(s: String) = when (s.first()) {
            'x', 'y' -> ":::in"
            'z' -> ":::out"
            else -> ""
        }

        fun printConnection(gate: Gate) {
            if (gate !in printed) {
                listOf(gate.input1, gate.input2).sorted().forEach {
                    out.println("$it${classdef(it)} -->|${gate.operation}| ${gate.output}${classdef(gate.output)}")
                }
                printed.add(gate)
            }
        }

        out.println("graph TD")
        out.println("classDef in fill: #fbbf24, stroke: #92400e")
        out.println("classDef out fill: #22d3ee, stroke: #155e75")

        outputs.filterKeys { it.startsWith("z") }.values
            .sortedBy { it.output }
            .forEach { gate ->
                out.println("subgraph bit${gate.output.drop(1)}")
                allInputs(gate.output, outputs)
                    .mapNotNull { outputs[it] }
                    .sortedByDescending { maxOf(it.input1, it.input2) }
                    .forEach { printConnection(it) }
                printConnection(gate)
                out.println("end")
            }
    }
}
