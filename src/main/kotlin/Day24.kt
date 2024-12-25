import java.io.File

fun main() {
    val input = fileReader("day24/sample.txt").readLines()

    val wires = input
        .filter { it.contains(":") }
        .map { it.split(": ") }
        .associate { (name, value) -> name to value.toInt() }
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

    // Solution for part 2 was creating a mermaid diagram and then manually fixing the gates
    // (requires the real input, not the sample)
    printMermaid(File("day24.input.mmd"), outputs)

    printMermaid(
        File("day24.fixed.mmd"),
        gates.swap("z08", "ffj").swap("dwp", "kfm").swap("gjh", "z22").swap("jdr", "z31").associate { it.output to it }
    )
    listOf("z08", "ffj", "dwp", "kfm", "gjh", "z22", "jdr", "z31").sorted().joinToString(",").debug("result2")


    // Incomplete (not working) solution for part 2 ...

//    val zGates = gates.filter { it.output.startsWith("z") }
//        .sortedBy { it.output }
//
//    val correctGates = mutableSetOf<String>()
//    (1..zGates.size - 1).forEach {
//        val newMap = mutableMapOf<String, Int>()
//        if (singleBitAdd(it, outputs, newMap)) {
//            singleBitAdd(it, 0, 0, outputs, newMap, highBit = false) // reset newMap
//            correctGates.addAll(newMap.keys)
//            println("Correct for $it")
//        } else {
//            println("Failed for $it")
//            val suspects1 = newMap.keys.filter {
//                it !in correctGates && it.first() !in "xy"
//            }.debug("suspects1")
//            val suspects2 = suspects1.mapNotNull { outputs[it] }.flatMap { g ->
//                gates.filter { g.hasSameInputsAs(it) && it.output !in correctGates }.map { it.output }
//            }.debug("suspects2")
//            val suspects = (suspects1 + suspects2).distinct()
//
//            val pairs = getPairsOfTwoFrom(suspects).filter { (s1, s2) ->
//                !dependants(s1, outputs).contains(s2) && !dependants(s2, outputs).contains(s1)
//            }
//            pairs.filter { (s1, s2) ->
//                println("Trying to swap $s1 and $s2")
//                val newGates = gates.swap(s1, s2)
//                val newOutputs = newGates.associate { it.output to it }
//                singleBitAdd(it, newOutputs, newMap)
//            }.debugList("correct pairs")
//        }
//    }
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
                dependants(gate.output, outputs)
                    .mapNotNull { outputs[it] }
                    .sortedByDescending { maxOf(it.input1, it.input2) }
                    .forEach { printConnection(it) }
                printConnection(gate)
                out.println("end")
            }
    }
}

private fun List<Gate>.swap(g1: String, g2: String) = map { g ->
    when (g.output) {
        g1 -> g.copy(output = g2)
        g2 -> g.copy(output = g1)
        else -> g
    }
}

private fun dependants(g: String, outputs: Map<String, Gate>): Set<String> {
    if (g.first() in "xy") return setOf(g)
    return outputs[g]!!.let { gate ->
        dependants(gate.input1, outputs) + dependants(gate.input2, outputs) + g
    }
}

private data class Gate(
    val input1: String,
    val input2: String,
    val operation: Operation,
    val output: String,
) {
    companion object {
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

    fun compute(wires: MutableMap<String, Int>, outputs: Map<String, Gate>): Int {
        val value1 = wires[input1] ?: outputs[input1]!!.compute(wires, outputs)
        val value2 = wires[input2] ?: outputs[input2]!!.compute(wires, outputs)
        return when (operation) {
            Operation.AND -> value1 and value2
            Operation.OR -> value1 or value2
            Operation.XOR -> value1 xor value2
        }.also {
            wires[output] = it
        }
    }
}

private enum class Operation {
    AND, OR, XOR
}

//private fun Gate.hasSameInputsAs(other: Gate) =
//    this != other && (input1 == other.input1 && input2 == other.input2 || input1 == other.input2 && input2 == other.input1)
//
//private fun singleBitAdd(i: Int, newOutputs: Map<String, Gate>, newMap: MutableMap<String, Int>) =
//    singleBitAdd(i, 0, 0, newOutputs, newMap) &&
//            singleBitAdd(i, 1, 0, newOutputs, newMap) &&
//            singleBitAdd(i, 0, 1, newOutputs, newMap) &&
//            singleBitAdd(i, 1, 1, newOutputs, newMap)
//
//private fun Int.zeroPadded() = toString().padStart(2, '0')
//
//private fun singleBitAdd(
//    n: Int,
//    x: Int,
//    y: Int,
//    outputs: Map<String, Gate>,
//    newMap: MutableMap<String, Int>,
//    highBit: Boolean = true
//): Boolean {
//    newMap.clear()
//    (0..n).forEach {
//        newMap.put("x${it.zeroPadded()}", 0)
//        newMap.put("y${it.zeroPadded()}", 0)
//    }
//    newMap.put("x${(n - 1).zeroPadded()}", x)
//    newMap.put("y${(n - 1).zeroPadded()}", y)
//    val zl = outputs["z${(n - 1).zeroPadded()}"]!!.compute(newMap, outputs)
//    val zh = if (highBit) outputs["z${n.zeroPadded()}"]!!.compute(newMap, outputs) else -1
//    return zl == (x xor y) && zh == (x and y)
//}
//
//private fun <T> getPairsOfTwoFrom(set: List<T>): List<List<T>> {
//    val list = set.toList()
//    return list.flatMapIndexed { index, s ->
//        list.subList(index + 1, list.size).map {
//            listOf(s, it)
//        }
//    }
//}
