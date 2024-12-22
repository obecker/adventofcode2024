// https://adventofcode.com/2024/day/15

fun main() {
    val input = fileReader("day15/sample.txt").readLines()

    val movements = input.filter { it.isNotBlank() && !it.startsWith("#") }.joinToString("").map {
        when (it) {
            '<' -> Direction.LEFT
            '>' -> Direction.RIGHT
            '^' -> Direction.UP
            'v' -> Direction.DOWN
            else -> throw IllegalArgumentException("Invalid movement")
        }
    }

    val map = input.filter { it.startsWith("#") }.toMap()

    var position = map.findPositionOf('@')
    for (movement in movements) {
        if (move(map, position, movement)) {
            map[position] = '.'
            position += movement
        }
    }

    computeGpsSum(map).debug("result1")

    val scaledMap = input.filter { it.startsWith("#") }
        .map {
            it.toCharArray().flatMap { c ->
                when (c) {
                    'O' -> listOf('[', ']')
                    '@' -> listOf('@', '.')
                    else -> listOf(c, c)
                }
            }.toCharArray()
        }.toTypedArray()
    position = scaledMap.findPositionOf('@')

    for (movement in movements) {
        when (movement) {
            Direction.LEFT, Direction.RIGHT ->
                if (move(scaledMap, position, movement)) {
                    scaledMap[position] = '.'
                    position += movement
                }

            Direction.UP, Direction.DOWN -> {
                val compound = findCompound(scaledMap, position + movement, movement)
                if (compound != null) {
                    moveCompound(scaledMap, compound, movement)
                    scaledMap[position] = '.'
                    position += movement
                    scaledMap[position] = '@'
                }
            }
        }
    }

    computeGpsSum(scaledMap).debug("result2")
}

private fun computeGpsSum(map: Array<CharArray>): Int {
    var sum = 0
    for (y in map.indices) {
        for (x in map[y].indices) {
            if (map[y][x] in listOf('O', '[')) {
                sum += x + y * 100
            }
        }
    }
    return sum
}

private fun move(map: Array<CharArray>, position: Position, direction: Direction): Boolean {
    val newPosition = position + direction
    return when (map[newPosition]) {
        'O', '[', ']' -> move(map, newPosition, direction)
        '.' -> true
        else -> false
    }.also {
        if (it) {
            map[newPosition] = map[position]
        }
    }
}

private fun findCompound(map: Array<CharArray>, position: Position, direction: Direction): Set<Position>? {

    fun findCompound(p1: Position, p2: Position) =
        findCompound(map, p1 + direction, direction)?.let { compound1 ->
            findCompound(map, p2 + direction, direction)?.let { compound2 ->
                compound1 + compound2 + p1 + p2
            }
        }

    return when (map[position]) {
        '[' -> findCompound(position, position + Direction.RIGHT)
        ']' -> findCompound(position, position + Direction.LEFT)
        '.' -> emptySet()
        else -> null
    }
}

private fun moveCompound(map: Array<CharArray>, positions: Set<Position>, direction: Direction) {
    positions.sortedWith { p1, p2 -> (p2.y - p1.y) * direction.vector.dy }.forEach { position ->
        map[position + direction] = map[position]
        map[position] = '.'
    }
}
