// https://adventofcode.com/2024/day/19

fun main() {
    val input = fileReader("day19/sample.txt").readLines()

    val patterns = input.first().split(",").map { it.trim() }
    val designs = input.drop(1).filter { it.isNotBlank() }

    val possibleDesigns = designs.filter { isPossible(it, patterns) }
    possibleDesigns.size.debug("result1")

    possibleDesigns.sumOf { design ->
        val possiblePatterns = patterns.filter { design.contains(it) }
        allDesigns(design, possiblePatterns)
    }.debug("result2")
}

private fun isPossible(design: String, patterns: List<String>): Boolean {
    if (design.isEmpty()) return true

    for (pattern in patterns) {
        if (design.startsWith(pattern) && isPossible(design.substring(pattern.length), patterns)) {
            return true
        }
    }
    return false
}

private fun allDesigns(design: String, patterns: List<String>, known: MutableMap<String, Long> = mutableMapOf()): Long {
    return if (design.isEmpty()) {
        1
    } else known[design] ?: patterns.sumOf { pattern ->
        if (design.startsWith(pattern)) {
            allDesigns(design.substring(pattern.length), patterns, known)
        } else {
            0
        }
    }.also {
        known[design] = it
    }
}
