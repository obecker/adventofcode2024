// https://adventofcode.com/2024/day/1

import kotlin.math.abs

fun main() {
    val space = Regex("\\s+")
    val input = fileReader("day01/sample.txt")
    val (left, right) = input.lines()
        .map { line -> line.split(space) }
        .map { (first, second) -> first.toInt() to second.toInt() }
        .toList()
        .fold(listOf<Int>() to listOf<Int>()) { (first, second), (f, s) -> first + f to second + s }
        .let { (l, r) -> l.sorted() to r.sorted() }

    left.zip(right).sumOf { (l, r) -> abs(l - r) }.debug("result1")

    val rightFrequency = right.groupingBy { it }.eachCount()
    left.sumOf { (rightFrequency[it] ?: 0) * it }.debug("result2")
}
