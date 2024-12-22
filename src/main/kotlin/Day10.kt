// https://adventofcode.com/2024/day/10

fun main() {
    val input = fileReader("day10/sample.txt").readLines()
        .map { line -> line.toCharArray().map { if (it == '.') -1 else it.toString().toInt() } }

    val startPositions = input.indices
        .flatMap { y -> input[y].indices.map { x -> Position(x, y) } }
        .filter { input.at(it) == 0 }

    startPositions
        .sumOf { trails(input, it, calcRating = false) }
        .debug("result1")
    startPositions
        .sumOf { trails(input, it, calcRating = true) }
        .debug("result2")
}

private fun trails(map: List<List<Int>>, position: Position, calcRating: Boolean = false, level: Int = 0, tops: MutableSet<Position> = mutableSetOf()): Int {
    if (map.at(position) == 9 && !tops.contains(position)) {
        if (!calcRating) {
            tops.add(position)
        }
        return 1
    }
    return Direction.entries.map { position + it }
        .filter { map.at(it) == level + 1 }
        .sumOf { trails(map, it, calcRating, level + 1, tops) }
}

private fun Position.isInside(map: List<List<Int>>) = y in map.indices && x in map[y].indices
private fun List<List<Int>>.at(position: Position) = if (position.isInside(this)) this[position.y][position.x] else -1
