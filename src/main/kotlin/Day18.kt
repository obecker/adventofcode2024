// https://adventofcode.com/2024/day/18

private const val mapSize = 7
private const val startWith = 12
//private const val mapSize = 71
//private const val startWith = 1024

fun main() {
    val input = fileReader("day18/sample.txt").readLines()
    val corruptedPositions = input.map { it.split(",").map { it.toInt() } }.map { (x, y) -> Position(x, y) }

    val map = buildMap(corruptedPositions, startWith)
    val solution = move(map, listOf(listOf(Position(0, 0))))
    (solution.size - 1).debug("result1")

    // binary search
    var lowerBound = startWith - 1
    var upperBound = corruptedPositions.size - 1
    while (lowerBound < upperBound - 1) {
        val middle = (lowerBound + upperBound) / 2
        val map = buildMap(corruptedPositions, middle)
        if (move(map, listOf(listOf(Position(0, 0)))).isEmpty()) {
            upperBound = middle - 1
        } else {
            lowerBound = middle
        }
    }
    corruptedPositions[lowerBound].let { (x, y) -> "$x,$y" }.debug("result2")
}

private fun buildMap(corruptedPositions: List<Position>, limit: Int): Array<CharArray> =
    Array(mapSize) { CharArray(mapSize) { '.' } }.apply {
        corruptedPositions.take(limit).forEach { this[it] = '#' }
    }

private tailrec fun move(
    map: Array<CharArray>,
    paths: List<List<Position>>,
    visited: Set<Position> = emptySet()
): List<Position> {
    val newPaths = paths.flatMap { path ->
        val current = path.last()
        Direction.entries.map { current + it }
            .filter { it.isInside(map) && map[it] != '#' && it !in visited }
            .map { path + it }
    }.groupBy { it.last() }.values.map { it.first() }

    val solution = newPaths.firstOrNull { paths ->
        paths.last().let { (x, y) -> x == map.lastIndex && y == map.lastIndex }
    }

    return when {
        solution != null -> solution
        newPaths.isEmpty() -> emptyList()
        else -> move(map, newPaths, visited + newPaths.map { it.last() })
    }
}
