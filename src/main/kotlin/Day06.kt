// https://adventofcode.com/2024/day/6

fun main() {
    val map = fileReader("day06/sample.txt").readLines()

    val startPosition = map.mapIndexed { y, line -> y to line }
        .filter { (_, line) -> line.indexOf('^') != -1 }
        .map { (y, line) -> y to line.indexOf('^') }
        .map { (y, x) -> Position(x, y) }
        .single()

    val (_, pathMap) = walk(startPosition, map)

    pathMap.sumOf { line -> line.count { it == 'X' } }.debug("result1")

    var result2 = 0
    for (y in pathMap.indices) {
        for (x in pathMap[y].indices) {
            val currentPosition = Position(x, y)
            if (currentPosition != startPosition && pathMap.visited(currentPosition)) {
                val (state, _) = walk(startPosition, map.mark(currentPosition, '#'))
                if (state == FinalState.LOOP) {
                    result2++
                }
            }
        }
    }
    result2.debug("result2")
}

private fun walk(
    startPosition: Position,
    startMap: List<String>,
): Pair<FinalState, List<String>> {
    var position = startPosition
    var map = startMap
    var direction = Direction.UP
    val turned = mutableSetOf<Pair<Direction, Position>>()
    while (position.isInside(map)) {
        map = map.mark(position)
        position += direction
        while (position.isInside(map) && map.obstacleAt(position)) {
            if (turned.contains(direction to position)) {
                return FinalState.LOOP to map
            }
            turned += direction to position
            position -= direction
            direction = direction.turnRight()
            position += direction
        }
    }
    return FinalState.OUTSIDE to map
}

enum class FinalState {
    LOOP, OUTSIDE
}

private fun List<String>.mark(position: Position, c: Char = 'X') = mapIndexed { y, line ->
    if (y == position.y) line.substring(0, position.x) + c + line.substring(position.x + 1)
    else line
}

private fun List<String>.obstacleAt(position: Position) = this[position.y][position.x] == '#'

private fun List<String>.visited(position: Position) = this[position.y][position.x] == 'X'
