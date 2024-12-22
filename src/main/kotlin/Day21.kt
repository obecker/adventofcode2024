// https://adventofcode.com/2024/day/21

fun main() {
    val input = fileReader("day21/sample.txt").readLines()

    val numericKeypad = Keypad(
        """
        789
        456
        123
         0A
        """
    )
    val directionalKeypad = Keypad(
        """
         ^A
        <v>
        """
    )

    input.sumOf { code ->
        val number = code.dropLast(1).toInt()
        val length = numericKeypad.type(code)
            .let { directionalKeypad.type(it) }
            .let { directionalKeypad.type(it) }
            .length
        length * number
    }.debug("result1")

    input.sumOf { code ->
        val number = code.dropLast(1).toInt()
        var typingFrequencies = mapOf(numericKeypad.type(code) to 1L)
        (1..25).forEach {
            typingFrequencies = directionalKeypad.memoType(typingFrequencies)
        }
        val length = typingFrequencies.map { (keys, count) -> keys.length * count }.sum()
        length * number
    }.debug("result2")
}

private class Keypad(spec: String) {
    private val pad = spec.trimIndent().split("\n").toMap()
    private val aPos = pad.findPositionOf('A')

    fun type(input: String): String =
        input.map { c -> pad.findPositionOf(c) }
            .fold(aPos to "") { (latest, steps), next ->
                next to (steps + nextSteps(next, latest))
            }.second

    private fun nextSteps(next: Position, latest: Position): String {
        val (xDiff, yDiff) = next - latest
        val hSteps = when {
            xDiff > 0 -> ">".repeat(xDiff)
            xDiff < 0 -> "<".repeat(-xDiff)
            else -> ""
        }
        val vSteps = when {
            yDiff > 0 -> "v".repeat(yDiff)
            yDiff < 0 -> "^".repeat(-yDiff)
            else -> ""
        }
        val next = when {
            // we must not touch a gap, so choose the first direction that does not touch a gap
            pad[latest + Vector(xDiff, 0)] == ' ' -> vSteps + hSteps
            pad[latest + Vector(0, yDiff)] == ' ' -> hSteps + vSteps
            // < and ^ -> vertical last, because ^ is closer to A than <
            xDiff < 0 && yDiff < 0 -> hSteps + vSteps
            // < and v -> vertical last, because < is closer to A than <
            xDiff < 0 && yDiff > 0 -> hSteps + vSteps
            // > and v -> horizontal last, because > is closer to A than v
            xDiff > 0 && yDiff > 0 -> vSteps + hSteps
            // else one of them is empty (order doesn't matter) or
            // > and ^, for which first vertical and then horizontal is (for some magical reason) the better choice
            else -> vSteps + hSteps
        }
        return next + "A"
    }

    fun memoType(frequencies: Map<String, Long>): Map<String, Long> =
        frequencies.map { (keys, count) ->
            keys.map { key -> pad.findPositionOf(key) }
                .fold(aPos to emptyList<String>()) { (latest, steps), next ->
                    next to (steps + nextSteps(next, latest))
                }.second.groupingBy { it }.eachCount().mapValues { (_, keysCount) -> keysCount * count }
        }.fold(mutableMapOf()) { merged, map ->
            merged.apply {
                map.forEach { (key, value) -> merge(key, value) { count1, count2 -> count1 + count2 } }
            }
        }
}
