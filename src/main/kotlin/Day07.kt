// https://adventofcode.com/2024/day/7

fun main() {
    val input = fileReader("day07/sample.txt").readLines()
        .map { it.split(':') }
        .map { (a, b) ->
            a.toLong() to b.trim().split(' ').map { it.toLong() }
        }

    input.filter { (result, operands) -> isEquation(result, operands, false) }
        .sumOf { it.first }
        .debug("result1")

    input.filter { (result, operands) -> isEquation(result, operands, true) }
        .sumOf { it.first }
        .debug("result2")
}

private fun isEquation(result: Long, operands: List<Long>, concat: Boolean): Boolean = when {
    operands.isEmpty() -> false
    operands.size == 1 -> operands.first() == result
    else -> {
        val (first, second) = operands
        val rest = operands.drop(2)
        isEquation(result, listOf(first + second) + rest, concat) ||
                isEquation(result, listOf(first * second) + rest, concat) ||
                (concat && isEquation(result, listOf("$first$second".toLong()) + rest, concat))
    }
}
