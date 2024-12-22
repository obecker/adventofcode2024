// https://adventofcode.com/2024/day/13

fun main() {
    val input = fileReader("day13/sample.txt").readLines()
        .filter { it.isNotBlank() }

    val machines = input.chunked(3).map { parseSpec(it) }
    machines.mapNotNull { it.solve() }
        .sumOf { it.costs() }
        .debug("result1")

    val positionDiff = 10000000000000L
    machines.map { it.copy(x = it.x + positionDiff, y = it.y + positionDiff) }
        .mapNotNull { it.solve() }
        .sumOf { it.costs() }
        .debug("result2")
}

private data class ClawMachine(
    val ax: Long,
    val ay: Long,
    val bx: Long,
    val by: Long,
    val x: Long,
    val y: Long
) {
    fun solve(): Solution? {
        // system of simple linear equations
        val b = (ax * y - ay * x) / (ax * by - ay * bx)
        val a = (x - bx * b) / ax

        return Solution(a, b).takeIf { it.a * ax + it.b * bx == x && it.a * ay + it.b * by == y }
    }
}

private data class Solution(
    val a: Long,
    val b: Long
) {
    fun costs(): Long = 3 * a + b
}

private fun parseSpec(lines: List<String>): ClawMachine {
    fun String.parseLine(label: String, op: String) =
        substringAfter(label).split(",").map { it.substringAfter(op).toLong() }

    val (ax, ay) = lines[0].parseLine("Button A:", "+")
    val (bx, by) = lines[1].parseLine("Button B:", "+")
    val (x, y) = lines[2].parseLine("Prize:", "=")
    return ClawMachine(ax, ay, bx, by, x, y)
}
