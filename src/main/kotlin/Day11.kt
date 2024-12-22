// https://adventofcode.com/2024/day/11

fun main() {
    val input = fileReader("day11/sample.txt").readLine().trim().split(" ").map { it.toLong() }

    compute1(input).debug("result1")
    compute2(input).debug("result2")
}

private fun compute1(input: List<Long>): Int {
    var numbers = input
    (0 until 25).forEach {
        numbers = numbers.flatMap { transform(it) }
    }
    return numbers.size
}

private fun compute2(input: List<Long>): Long {
    var numbers =
        input.fold(mutableMapOf<Long, Long>()) { map, n -> map.put(n, map.getOrDefault(n, 0) + 1); map }

    fun MutableMap<Long, Long>.add(n: Long, c: Long) {
        this[n] = this.getOrDefault(n, 0) + c
    }

    (0 until 75).forEach {
        numbers = numbers.entries.fold(mutableMapOf<Long, Long>()) { map, (n, count) ->
            transform(n).forEach { map.add(it, count) }
            map
        }.also { (it.size to it.values.sum()) }
    }
    return numbers.values.sum()
}

private fun transform(number: Long): List<Long> = when {
    number == 0L -> listOf(1L)
    number.toString().length % 2 == 0 -> number.toString().let {
        val half = it.length / 2
        listOf(
            it.substring(0..<half).toLong(),
            it.substring(half..<it.length).toLong()
        )
    }

    else -> listOf(number * 2024)
}
