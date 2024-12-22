// https://adventofcode.com/2024/day/3

fun main() {
    val input = fileReader("day03/sample.txt").readText()

    val mulRegex = Regex("""mul\((\d+),(\d+)\)""")
    val result1 = mulRegex.findAll(input).map { matchResult ->
        val (a, b) = matchResult.destructured
        a.toInt() * b.toInt()
    }.sum()
    println(result1)

    val mulDoDontRegex = Regex("""mul\((\d+),(\d+)\)|do\(\)|don't\(\)""")
    val (_, result2) = mulDoDontRegex.findAll(input)
        .fold(Pair(true, 0)) { (enabled, acc), matchResult ->
            when (matchResult.value) {
                "do()" -> Pair(true, acc)
                "don't()" -> Pair(false, acc)
                else -> {
                    val mul = if (enabled) {
                        val (a, b) = matchResult.destructured
                        a.toInt() * b.toInt()
                    } else {
                        0
                    }
                    Pair(enabled, acc + mul)
                }
            }
        }
    println(result2)
}
