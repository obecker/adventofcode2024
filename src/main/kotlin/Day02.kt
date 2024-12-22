// https://adventofcode.com/2024/day/2

import kotlin.math.abs

typealias Levels = List<Int>

fun main() {
    val space = Regex("\\s+")
    val input = fileReader("day02/sample.txt")
    val reports = input.lines()
        .map { line -> line.split(space).map { it.toInt() } }
        .toList()

    reports.count { levels -> isSafe(levels) }.debug("result1")

    reports
        .map { levels -> damped(levels) /* + listOf(levels) */ } // original levels not required
        .count { dampedList -> dampedList.any { levels -> isSafe(levels) } }
        .debug("result2")
}

private fun isSafe(levels: Levels): Boolean = (isIncreasing(levels) || isDecreasing(levels)) && hasAllowedDiff(levels)

private fun isIncreasing(levels: Levels): Boolean = levels.zipWithNext().all { (a, b) -> a <= b }
private fun isDecreasing(levels: Levels): Boolean = levels.zipWithNext().all { (a, b) -> a >= b }
private fun hasAllowedDiff(levels: Levels): Boolean = levels.zipWithNext().all { (a, b) -> abs(a - b) in 1..3 }

private fun damped(levels: Levels): List<Levels> =
    levels.mapIndexed { idx, _ -> levels.filterIndexed { i, _ -> i != idx } }
