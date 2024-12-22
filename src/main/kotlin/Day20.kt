// https://adventofcode.com/2024/day/20

private const val MIN_SAVING = 50
//private const val MIN_SAVING = 100

fun main() {
    val map = fileReader("day20/sample.txt").readLines().toMap()

    val start = map.findPositionOf('S')

    val solution = move(map, listOf(listOf(start)))

    solution.mapIndexed { i, pos ->
        cheatingMove(map, listOf(listOf(pos)), i, 2, solution)
    }.sum().debug("result1")

    solution.mapIndexed { i, pos ->
        cheatingMove(map, listOf(listOf(pos)), i, 20, solution)
    }.sum().debug("result2")
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
        paths.last().let { map[it] == 'E' }
    }

    return when {
        solution != null -> solution
        newPaths.isEmpty() -> emptyList()
        else -> move(map, newPaths, visited + newPaths.map { it.last() })
    }
}

private fun cheatingMove(
    map: Array<CharArray>,
    paths: List<List<Position>>,
    steps: Int,
    remainingMoves: Int,
    solution: List<Position>,
    visited: Set<Position> = emptySet()
): Int {
    val newPaths = if (remainingMoves > 0) {
        paths.flatMap { path ->
            val current = path.last()
            Direction.entries.map { current + it }
                .filter { it.isInside(map) && it !in visited }
                .map { path + it }
        }.groupBy { it.last() }.values.map { it.first() }
    } else {
        emptyList()
    }

    val shortcuts = newPaths.map { it.last() }.filter { last ->
        map[last] != '#' && solution.indexOf(last) - steps > MIN_SAVING
    }.size

    return when {
        newPaths.isEmpty() -> shortcuts
        else -> shortcuts + cheatingMove(
            map,
            newPaths,
            steps + 1,
            remainingMoves - 1,
            solution,
            visited + newPaths.map { it.last() })
    }
}
